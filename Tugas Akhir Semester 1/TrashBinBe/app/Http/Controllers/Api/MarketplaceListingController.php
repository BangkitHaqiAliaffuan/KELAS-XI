<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\MarketplaceListing;
use App\Models\WasteCategory;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;

class MarketplaceListingController extends Controller
{
    /**
     * Display a listing of marketplace listings with filters
     */
    public function index(Request $request)
    {
        $query = MarketplaceListing::with(['wasteCategory', 'seller:id,name,avatar,points'])
            ->where('status', 'available')
            ->where('expires_at', '>', now())
            ->orderBy('created_at', 'desc');

        // Apply filters
        if ($request->has('category')) {
            $query->where('waste_category_id', $request->category);
        }

        if ($request->has('min_price') && is_numeric($request->min_price)) {
            $query->where('price_per_unit', '>=', $request->min_price);
        }

        if ($request->has('max_price') && is_numeric($request->max_price)) {
            $query->where('price_per_unit', '<=', $request->max_price);
        }

        if ($request->has('condition') && in_array($request->condition, ['clean', 'needs_cleaning', 'mixed'])) {
            $query->where('condition', $request->condition);
        }

        if ($request->has('lat') && $request->has('lng') && $request->has('radius')) {
            $lat = $request->lat;
            $lng = $request->lng;
            $radius = $request->radius; // in km

            $query->select([
                '*',
                DB::raw("(
                    6371 * acos(
                        cos(radians(?)) *
                        cos(radians(lat)) *
                        cos(radians(lng) - radians(?)) +
                        sin(radians(?)) *
                        sin(radians(lat))
                    )
                ) AS distance"),
                [$lat, $lng, $lat]
            ])
                ->having('distance', '<', $radius);
        }

        if ($request->has('search')) {
            $search = $request->search;
            $query->where(function ($q) use ($search) {
                $q->where('title', 'like', "%{$search}%")
                    ->orWhere('description', 'like', "%{$search}%");
            });
        }

        $listings = $query->paginate(10);

        // Increment view count for each listing retrieved (for the first page load)
        $listings->getCollection()->each(function ($listing) {
            $listing->increment('views_count');
        });

        return response()->json([
            'success' => true,
            'data' => $listings
        ]);
    }

    /**
     * Store a newly created marketplace listing
     */
    public function store(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'waste_category_id' => 'required|exists:waste_categories,id',
            'title' => 'required|string|max:100',
            'description' => 'required|string',
            'quantity' => 'required|numeric|min:0.01',
            'unit' => 'required|string|max:20',
            'price_per_unit' => 'required|numeric|min:0',
            'total_price' => 'required|numeric|min:0',
            'condition' => 'required|in:clean,needs_cleaning,mixed',
            'location' => 'required|string',
            'lat' => 'required|numeric|min:-90|max:90',
            'lng' => 'required|numeric|min:-180|max:180',
            'photos' => 'required|array|min:1|max:5',
            'photos.*' => 'url',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $listing = MarketplaceListing::create([
            'seller_id' => $request->user()->id,
            'waste_category_id' => $request->waste_category_id,
            'title' => $request->title,
            'description' => $request->description,
            'quantity' => $request->quantity,
            'unit' => $request->unit,
            'price_per_unit' => $request->price_per_unit,
            'total_price' => $request->total_price,
            'condition' => $request->condition,
            'location' => $request->location,
            'lat' => $request->lat,
            'lng' => $request->lng,
            'photos' => $request->photos,
            'status' => 'available',
            'expires_at' => now()->addDays(30), // Expire after 30 days
        ]);

        $listing->load(['wasteCategory', 'seller:id,name,avatar,points']);

        return response()->json([
            'message' => 'Marketplace listing created successfully',
            'data' => $listing
        ], 201);
    }

    /**
     * Display the specified marketplace listing
     */
    public function show($id, Request $request)
    {
        $listing = MarketplaceListing::where('id', $id)
            ->with(['wasteCategory', 'seller:id,name,avatar,phone,points'])
            ->firstOrFail();

        if ($listing->status !== 'available' || $listing->expires_at < now()) {
            return response()->json([
                'message' => 'Listing not available'
            ], 404);
        }

        // Increment view count
        $listing->increment('views_count');

        return response()->json([
            'data' => $listing
        ]);
    }

    /**
     * Update the specified marketplace listing
     */
    public function update(Request $request, $id)
    {
        $listing = MarketplaceListing::where('id', $id)
            ->where('seller_id', $request->user()->id)
            ->firstOrFail();

        if ($listing->status !== 'available') {
            return response()->json([
                'message' => 'Cannot update listing that is not available'
            ], 400);
        }

        $validator = Validator::make($request->all(), [
            'waste_category_id' => 'sometimes|required|exists:waste_categories,id',
            'title' => 'sometimes|required|string|max:100',
            'description' => 'sometimes|required|string',
            'quantity' => 'sometimes|required|numeric|min:0.01',
            'unit' => 'sometimes|required|string|max:20',
            'price_per_unit' => 'sometimes|required|numeric|min:0',
            'total_price' => 'sometimes|required|numeric|min:0',
            'condition' => 'sometimes|required|in:clean,needs_cleaning,mixed',
            'location' => 'sometimes|required|string',
            'lat' => 'sometimes|required|numeric|min:-90|max:90',
            'lng' => 'sometimes|required|numeric|min:-180|max:180',
            'photos' => 'sometimes|required|array|min:1|max:5',
            'photos.*' => 'url',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $listing->update($request->all());

        $listing->load(['wasteCategory', 'seller:id,name,avatar,points']);

        return response()->json([
            'message' => 'Marketplace listing updated successfully',
            'data' => $listing
        ]);
    }

    /**
     * Remove the specified marketplace listing
     */
    public function destroy($id, Request $request)
    {
        $listing = MarketplaceListing::where('id', $id)
            ->where('seller_id', $request->user()->id)
            ->firstOrFail();

        if ($listing->orders()->where('status', '!=', 'cancelled')->exists()) {
            return response()->json([
                'message' => 'Cannot delete listing with active orders'
            ], 400);
        }

        $listing->delete();

        return response()->json([
            'message' => 'Marketplace listing deleted successfully'
        ]);
    }
}
