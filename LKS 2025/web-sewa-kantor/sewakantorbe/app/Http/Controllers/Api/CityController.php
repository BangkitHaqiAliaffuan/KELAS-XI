<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Resources\CityResource;
use App\Models\City;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;

class CityController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(): JsonResponse
    {
        $cities = City::withCount(['offices', 'availableOffices'])->get();

        return response()->json([
            'success' => true,
            'data' => CityResource::collection($cities),
            'message' => 'Cities retrieved successfully'
        ]);
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'description' => 'nullable|string',
            'photo' => 'nullable|string',
        ]);

        $city = City::create($validated);

        return response()->json([
            'success' => true,
            'data' => new CityResource($city),
            'message' => 'City created successfully'
        ], 201);
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id): JsonResponse
    {
        $city = City::with(['offices.facilities'])
            ->withCount(['offices', 'availableOffices'])
            ->findOrFail($id);

        return response()->json([
            'success' => true,
            'data' => new CityResource($city),
            'message' => 'City retrieved successfully'
        ]);
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id): JsonResponse
    {
        $city = City::findOrFail($id);

        $validated = $request->validate([
            'name' => 'sometimes|required|string|max:255',
            'description' => 'nullable|string',
            'photo' => 'nullable|string',
        ]);

        $city->update($validated);

        return response()->json([
            'success' => true,
            'data' => new CityResource($city),
            'message' => 'City updated successfully'
        ]);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id): JsonResponse
    {
        $city = City::findOrFail($id);
        $city->delete();

        return response()->json([
            'success' => true,
            'message' => 'City deleted successfully'
        ]);
    }
}
