<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\PickupRequest;
use App\Models\PointsHistory;
use App\Models\Transaction;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;

class CollectorPickupController extends Controller
{
    /**
     * Get available pickup requests near the collector
     */
    public function availablePickups(Request $request)
    {
        $user = $request->user();
        
        if (!$user->isCollector()) {
            return response()->json([
                'message' => 'Access denied. Only collectors can access this resource.'
            ], 403);
        }
        
        // Check if collector has valid location
        if (!$user->lat || !$user->lng) {
            return response()->json([
                'message' => 'Collector location not set. Please update your location in profile.'
            ], 400);
        }

        $lat = $user->lat;
        $lng = $user->lng;
        $radius = 5; // 5km radius

        // Calculate bounding box for the radius
        $latRange = $radius / 111.045; // 1 degree of latitude is approximately 111.045 km
        $lngRange = $radius / (111.045 * cos(deg2rad($lat))); // Account for longitude convergence

        $nearbyPickups = PickupRequest::where('status', 'pending')
            ->whereBetween('pickup_lat', [$lat - $latRange, $lat + $latRange])
            ->whereBetween('pickup_lng', [$lng - $lngRange, $lng + $lngRange])
            ->with(['items.wasteCategory', 'user:id,name,phone,address'])
            ->select(['*', 
                DB::raw("(
                    6371 * acos(
                        cos(radians(?)) * 
                        cos(radians(pickup_lat)) * 
                        cos(radians(pickup_lng) - radians(?)) + 
                        sin(radians(?)) * 
                        sin(radians(pickup_lat))
                    )
                ) AS distance"), [$lat, $lng, $lat])
            ])
            ->having('distance', '<', $radius)
            ->orderBy('distance')
            ->get();

        return response()->json([
            'data' => $nearbyPickups
        ]);
    }

    /**
     * Accept a pickup request
     */
    public function acceptPickup($id, Request $request)
    {
        $user = $request->user();
        
        if (!$user->isCollector()) {
            return response()->json([
                'message' => 'Access denied. Only collectors can accept pickups.'
            ], 403);
        }

        $pickup = PickupRequest::where('id', $id)
            ->where('status', 'pending')
            ->with(['items.wasteCategory'])
            ->firstOrFail();

        // Check if collector is within range (simplified for now)
        $pickup->update([
            'collector_id' => $user->id,
            'status' => 'accepted'
        ]);

        // Refresh the model to get updated data
        $pickup->refresh();
        $pickup->load(['user:id,name,phone,address']);

        return response()->json([
            'message' => 'Pickup request accepted successfully',
            'data' => $pickup
        ]);
    }

    /**
     * Update pickup status
     */
    public function updateStatus($id, Request $request)
    {
        $validator = Validator::make($request->all(), [
            'status' => 'required|in:on_the_way,picked_up'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $pickup = PickupRequest::where('id', $id)
            ->where('collector_id', $request->user()->id)
            ->whereIn('status', ['accepted', 'on_the_way']) // Only allow updating if in these states
            ->firstOrFail();

        $pickup->update([
            'status' => $request->status
        ]);

        $pickup->refresh();
        $pickup->load(['user:id,name,phone,address', 'items.wasteCategory']);

        return response()->json([
            'message' => 'Pickup status updated successfully',
            'data' => $pickup
        ]);
    }

    /**
     * Confirm weights for pickup items
     */
    public function confirmWeight($id, Request $request)
    {
        $validator = Validator::make($request->all(), [
            'items' => 'required|array',
            'items.*.id' => 'required|exists:pickup_items,id,pickup_request_id,' . $id,
            'items.*.actual_weight' => 'required|numeric|min:0.01',
            'items.*.photo_url' => 'nullable|url',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $pickup = PickupRequest::where('id', $id)
            ->where('collector_id', $request->user()->id)
            ->where('status', 'picked_up') // Should only happen after pickup
            ->with(['items', 'user'])
            ->firstOrFail();

        DB::beginTransaction();

        try {
            $totalWeight = 0;
            $totalPrice = 0;

            foreach ($request->items as $itemData) {
                $item = $pickup->items()->find($itemData['id']);
                
                if (!$item) {
                    throw new \Exception('Pickup item not found');
                }

                $item->update([
                    'actual_weight' => $itemData['actual_weight'],
                    'photo_url' => $itemData['photo_url'] ?? $item->photo_url,
                ]);

                $totalWeight += $item->actual_weight;
                $totalPrice += $item->subtotal;
            }

            // Update pickup with final weights and prices
            $pickup->update([
                'total_weight' => $totalWeight,
                'total_price' => $totalPrice,
                'status' => 'completed'
            ]);

            // Award points to user based on weight
            $pointsEarned = (int)($totalWeight * 10); // 10 points per kg
            $pickup->user->increment('points', $pointsEarned);

            // Record points history
            PointsHistory::create([
                'user_id' => $pickup->user->id,
                'points' => $pointsEarned,
                'type' => 'earned',
                'description' => 'Points earned from completed pickup',
                'reference_id' => $pickup->id,
            ]);

            // Record transaction for user
            Transaction::create([
                'user_id' => $pickup->user->id,
                'type' => 'pickup_earning',
                'reference_id' => $pickup->id,
                'reference_type' => PickupRequest::class,
                'amount' => $totalPrice,
                'points_earned' => $pointsEarned,
                'description' => 'Earnings from completed pickup'
            ]);

            // Record transaction for collector
            Transaction::create([
                'user_id' => $request->user()->id,
                'type' => 'pickup_earning',
                'reference_id' => $pickup->id,
                'reference_type' => PickupRequest::class,
                'amount' => $totalPrice,
                'points_earned' => 0, // Collectors don't get points
                'description' => 'Earnings from completed pickup'
            ]);

            DB::commit();

            $pickup->refresh();
            $pickup->load(['user:id,name,phone,address', 'items.wasteCategory']);

            return response()->json([
                'message' => 'Weights confirmed and pickup completed successfully',
                'data' => $pickup,
                'points_earned' => $pointsEarned
            ]);
        } catch (\Exception $e) {
            DB::rollback();
            return response()->json([
                'message' => 'Failed to confirm weights',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Complete a pickup (for internal use - already handled in confirmWeight)
     */
    public function complete($id, Request $request)
    {
        $pickup = PickupRequest::where('id', $id)
            ->where('collector_id', $request->user()->id)
            ->where('status', 'picked_up')
            ->firstOrFail();

        // This would be called after weights are confirmed
        // For now, just update status to completed
        $pickup->update([
            'status' => 'completed'
        ]);

        $pickup->refresh();
        $pickup->load(['user:id,name,phone,address', 'items.wasteCategory']);

        return response()->json([
            'message' => 'Pickup completed successfully',
            'data' => $pickup
        ]);
    }
}
