<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\PickupRequest;
use App\Models\PickupItem;
use App\Models\WasteCategory;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;

class PickupController extends Controller
{
    /**
     * Display a listing of the user's pickup requests
     */
    public function index(Request $request)
    {
        $pickups = PickupRequest::where('user_id', $request->user()->id)
            ->with(['items.wasteCategory', 'collector:id,name,avatar,phone'])
            ->orderBy('created_at', 'desc')
            ->paginate(10);

        return response()->json([
            'data' => $pickups
        ]);
    }

    /**
     * Store a newly created pickup request
     */
    public function store(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'pickup_address' => 'required|string',
            'pickup_lat' => 'required|numeric|min:-90|max:90',
            'pickup_lng' => 'required|numeric|min:-180|max:180',
            'scheduled_date' => 'required|date|after:now',
            'items' => 'required|array|min:1',
            'items.*.category_id' => 'required|exists:waste_categories,id',
            'items.*.estimated_weight' => 'required|numeric|min:0.1',
            'items.*.photo_url' => 'nullable|url',
            'notes' => 'nullable|string|max:500',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        DB::beginTransaction();

        try {
            $pickup = PickupRequest::create([
                'user_id' => $request->user()->id,
                'pickup_address' => $request->pickup_address,
                'pickup_lat' => $request->pickup_lat,
                'pickup_lng' => $request->pickup_lng,
                'scheduled_date' => $request->scheduled_date,
                'notes' => $request->notes,
                'status' => 'pending',
            ]);

            $totalEstimatedPrice = 0;

            foreach ($request->items as $item) {
                $wasteCategory = WasteCategory::findOrFail($item['category_id']);
                
                $pickupItem = PickupItem::create([
                    'pickup_request_id' => $pickup->id,
                    'waste_category_id' => $item['category_id'],
                    'estimated_weight' => $item['estimated_weight'],
                    'photo_url' => $item['photo_url'] ?? null,
                    'price_per_unit' => $wasteCategory->base_price_per_unit,
                ]);

                $totalEstimatedPrice += $pickupItem->subtotal;
            }

            // Update the pickup with the estimated total price
            $pickup->update([
                'total_price' => $totalEstimatedPrice
            ]);

            DB::commit();

            $pickup->load(['items.wasteCategory', 'user', 'collector']);

            return response()->json([
                'message' => 'Pickup request created successfully',
                'data' => $pickup
            ], 201);
        } catch (\Exception $e) {
            DB::rollback();
            return response()->json([
                'message' => 'Failed to create pickup request',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Display the specified pickup request
     */
    public function show($id, Request $request)
    {
        $pickup = PickupRequest::where('id', $id)
            ->where('user_id', $request->user()->id)
            ->with(['items.wasteCategory', 'user:id,name,phone', 'collector:id,name,phone,avatar'])
            ->firstOrFail();

        return response()->json([
            'data' => $pickup
        ]);
    }

    /**
     * Cancel the specified pickup request
     */
    public function cancel($id, Request $request)
    {
        $pickup = PickupRequest::where('id', $id)
            ->where('user_id', $request->user()->id)
            ->firstOrFail();

        if ($pickup->status !== 'pending') {
            return response()->json([
                'message' => 'Cannot cancel pickup request that is not pending'
            ], 400);
        }

        $pickup->update([
            'status' => 'cancelled'
        ]);

        return response()->json([
            'message' => 'Pickup request cancelled successfully',
            'data' => $pickup
        ]);
    }
}
