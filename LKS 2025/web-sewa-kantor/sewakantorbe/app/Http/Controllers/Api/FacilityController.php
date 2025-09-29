<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Resources\FacilityResource;
use App\Models\Facility;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;

class FacilityController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(): JsonResponse
    {
        $facilities = Facility::all();

        return response()->json([
            'success' => true,
            'data' => FacilityResource::collection($facilities),
            'message' => 'Facilities retrieved successfully'
        ]);
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'icon' => 'nullable|string',
            'description' => 'nullable|string',
        ]);

        $facility = Facility::create($validated);

        return response()->json([
            'success' => true,
            'data' => new FacilityResource($facility),
            'message' => 'Facility created successfully'
        ], 201);
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id): JsonResponse
    {
        $facility = Facility::findOrFail($id);

        return response()->json([
            'success' => true,
            'data' => new FacilityResource($facility),
            'message' => 'Facility retrieved successfully'
        ]);
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id): JsonResponse
    {
        $facility = Facility::findOrFail($id);

        $validated = $request->validate([
            'name' => 'sometimes|required|string|max:255',
            'icon' => 'nullable|string',
            'description' => 'nullable|string',
        ]);

        $facility->update($validated);

        return response()->json([
            'success' => true,
            'data' => new FacilityResource($facility),
            'message' => 'Facility updated successfully'
        ]);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id): JsonResponse
    {
        $facility = Facility::findOrFail($id);
        $facility->delete();

        return response()->json([
            'success' => true,
            'message' => 'Facility deleted successfully'
        ]);
    }
}
