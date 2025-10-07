<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Resources\OfficeResource;
use App\Models\Office;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Str;

class OfficeController extends Controller
{
    /**
     * Handle image upload and return the stored path
     */
    private function handleImageUpload($image, $existingPath = null): string
    {
        // Delete existing image if provided
        if ($existingPath && Storage::disk('public')->exists($existingPath)) {
            Storage::disk('public')->delete($existingPath);
        }

        // Generate unique filename
        $filename = Str::uuid() . '.' . $image->getClientOriginalExtension();

        // Store the image in the offices directory
        $path = $image->storeAs('offices', $filename, 'public');

        return $path;
    }

    /**
     * Process uploaded images and return array of paths
     */
    private function processImages(Request $request, $existingPhotos = []): array
    {
        $photos = [];

        if ($request->hasFile('images')) {
            // Delete existing photos if we're replacing them
            if (!empty($existingPhotos)) {
                foreach ($existingPhotos as $photo) {
                    if (Storage::disk('public')->exists($photo)) {
                        Storage::disk('public')->delete($photo);
                    }
                }
            }

            // Upload new images
            foreach ($request->file('images') as $image) {
                if ($image->isValid()) {
                    $photos[] = $this->handleImageUpload($image);
                }
            }
        } elseif (!empty($existingPhotos)) {
            // Keep existing photos if no new images uploaded
            $photos = $existingPhotos;
        }

        return $photos;
    }

    /**
     * Display a listing of the resource.
     */
    public function index(Request $request): JsonResponse
    {
        $query = Office::with(['city', 'facilities']);

        // Filter by city
        if ($request->filled('city_id')) {
            $query->byCity($request->city_id);
        }

        // Filter by capacity
        if ($request->filled('min_capacity')) {
            $maxCapacity = $request->filled('max_capacity') ? $request->max_capacity : null;
            $query->byCapacity($request->min_capacity, $maxCapacity);
        }

        // Filter by price range
        if ($request->filled('min_price')) {
            $priceType = $request->input('price_type', 'price');
            $maxPrice = $request->filled('max_price') ? $request->max_price : null;
            $query->byPriceRange($request->min_price, $maxPrice, $priceType);
        }

        // Filter by facilities
        if ($request->filled('facilities')) {
            $facilities = $request->facilities;
            if (is_string($facilities)) {
                $facilityIds = array_filter(explode(',', $facilities));
            } else {
                $facilityIds = is_array($facilities) ? $facilities : [];
            }

            if (!empty($facilityIds)) {
                $query->withFacilities($facilityIds);
            }
        }

        // Filter by status (default to available)
        $status = $request->input('status', 'available');
        if ($status === 'available') {
            $query->available();
        } elseif ($request->filled('status')) {
            $query->where('status', $status);
        }

        // Search by name or address
        if ($request->filled('search')) {
            $search = trim($request->search);
            if ($search !== '') {
                $query->where(function ($q) use ($search) {
                    $q->where('name', 'like', "%{$search}%")
                      ->orWhere('address', 'like', "%{$search}%")
                      ->orWhere('description', 'like', "%{$search}%");
                });
            }
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
            'operating_hours' => 'nullable|string',
            'city_id' => 'required|exists:cities,id',
            'facility_ids' => 'nullable|array',
            'facility_ids.*' => 'exists:facilities,id',
            'images' => 'nullable|array|max:5',
            'images.*' => 'image|mimes:jpeg,jpg,png,webp|max:2048',
        ]);

        $facilityIds = $validated['facility_ids'] ?? [];
        unset($validated['facility_ids']);

        // Process image uploads
        $photos = $this->processImages($request);
        $validated['photos'] = $photos;

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
            'status' => 'sometimes|in:available,unavailable,maintenance',
            'operating_hours' => 'nullable|string',
            'city_id' => 'sometimes|required|exists:cities,id',
            'facility_ids' => 'nullable|array',
            'facility_ids.*' => 'exists:facilities,id',
            'images' => 'nullable|array|max:5',
            'images.*' => 'image|mimes:jpeg,jpg,png,webp|max:2048',
            'remove_images' => 'nullable|boolean',
        ]);

        $facilityIds = $validated['facility_ids'] ?? null;
        unset($validated['facility_ids']);

        // Handle image uploads if provided
        if ($request->hasFile('images') || $request->boolean('remove_images')) {
            $existingPhotos = $office->photos ?? [];

            if ($request->boolean('remove_images')) {
                // Remove all existing images
                $photos = $this->processImages($request, $existingPhotos);
            } else {
                // Add new images to existing ones or replace if new images provided
                $photos = $this->processImages($request, $existingPhotos);
            }

            $validated['photos'] = $photos;
        }

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

        // Delete associated images
        if (!empty($office->photos)) {
            foreach ($office->photos as $photo) {
                if (Storage::disk('public')->exists($photo)) {
                    Storage::disk('public')->delete($photo);
                }
            }
        }

        $office->delete();

        return response()->json([
            'success' => true,
            'message' => 'Office deleted successfully'
        ]);
    }
}
