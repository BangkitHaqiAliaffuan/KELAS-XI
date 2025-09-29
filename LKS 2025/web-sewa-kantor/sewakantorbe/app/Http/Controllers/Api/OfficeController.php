<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Resources\OfficeResource;
use App\Models\Office;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;

class OfficeController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request): JsonResponse
    {
        $query = Office::with(['city', 'facilities']);

        // Filter by city
        if ($request->has('city_id')) {
            $query->byCity($request->city_id);
        }

        // Filter by capacity
        if ($request->has('min_capacity')) {
            $query->byCapacity(
                $request->min_capacity,
                $request->max_capacity
            );
        }

        // Filter by price range
        if ($request->has('min_price')) {
            $priceType = $request->input('price_type', 'price_per_day');
            $query->byPriceRange(
                $request->min_price,
                $request->max_price,
                $priceType
            );
        }

        // Filter by facilities
        if ($request->has('facilities')) {
            $facilityIds = explode(',', $request->facilities);
            $query->withFacilities($facilityIds);
        }

        // Filter by status (default to available)
        $status = $request->input('status', 'available');
        if ($status === 'available') {
            $query->available();
        } else {
            $query->where('status', $status);
        }

        // Search by name or address
        if ($request->has('search')) {
            $search = $request->search;
            $query->where(function ($q) use ($search) {
                $q->where('name', 'like', "%{$search}%")
                  ->orWhere('address', 'like', "%{$search}%")
                  ->orWhere('description', 'like', "%{$search}%");
            });
        }

        // Sorting
        $sortBy = $request->input('sort_by', 'created_at');
        $sortOrder = $request->input('sort_order', 'desc');
        $query->orderBy($sortBy, $sortOrder);

        // Pagination
        $perPage = $request->input('per_page', 15);
        $offices = $query->paginate($perPage);

        return response()->json([
            'success' => true,
            'data' => OfficeResource::collection($offices->items()),
            'pagination' => [
                'current_page' => $offices->currentPage(),
                'last_page' => $offices->lastPage(),
                'per_page' => $offices->perPage(),
                'total' => $offices->total(),
                'from' => $offices->firstItem(),
                'to' => $offices->lastItem(),
            ],
            'message' => 'Offices retrieved successfully'
        ]);
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'description' => 'required|string',
            'address' => 'required|string',
            'latitude' => 'nullable|string',
            'longitude' => 'nullable|string',
            'capacity' => 'required|integer|min:1',
            'price_per_day' => 'required|numeric|min:0',
            'price_per_week' => 'required|numeric|min:0',
            'price_per_month' => 'required|numeric|min:0',
            'photos' => 'nullable|array',
            'photos.*' => 'string',
            'operating_hours' => 'nullable|string',
            'city_id' => 'required|exists:cities,id',
            'facility_ids' => 'nullable|array',
            'facility_ids.*' => 'exists:facilities,id',
        ]);

        $facilityIds = $validated['facility_ids'] ?? [];
        unset($validated['facility_ids']);

        $office = Office::create($validated);

        if (!empty($facilityIds)) {
            $office->facilities()->attach($facilityIds);
        }

        $office->load(['city', 'facilities']);

        return response()->json([
            'success' => true,
            'data' => new OfficeResource($office),
            'message' => 'Office created successfully'
        ], 201);
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id): JsonResponse
    {
        $office = Office::with(['city', 'facilities'])
            ->findOrFail($id);

        return response()->json([
            'success' => true,
            'data' => new OfficeResource($office),
            'message' => 'Office retrieved successfully'
        ]);
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id): JsonResponse
    {
        $office = Office::findOrFail($id);

        $validated = $request->validate([
            'name' => 'sometimes|required|string|max:255',
            'description' => 'sometimes|required|string',
            'address' => 'sometimes|required|string',
            'latitude' => 'nullable|string',
            'longitude' => 'nullable|string',
            'capacity' => 'sometimes|required|integer|min:1',
            'price_per_day' => 'sometimes|required|numeric|min:0',
            'price_per_week' => 'sometimes|required|numeric|min:0',
            'price_per_month' => 'sometimes|required|numeric|min:0',
            'photos' => 'nullable|array',
            'photos.*' => 'string',
            'status' => 'sometimes|in:available,unavailable,maintenance',
            'operating_hours' => 'nullable|string',
            'city_id' => 'sometimes|required|exists:cities,id',
            'facility_ids' => 'nullable|array',
            'facility_ids.*' => 'exists:facilities,id',
        ]);

        $facilityIds = $validated['facility_ids'] ?? null;
        unset($validated['facility_ids']);

        $office->update($validated);

        if ($facilityIds !== null) {
            $office->facilities()->sync($facilityIds);
        }

        $office->load(['city', 'facilities']);

        return response()->json([
            'success' => true,
            'data' => new OfficeResource($office),
            'message' => 'Office updated successfully'
        ]);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id): JsonResponse
    {
        $office = Office::findOrFail($id);
        $office->delete();

        return response()->json([
            'success' => true,
            'message' => 'Office deleted successfully'
        ]);
    }
}
