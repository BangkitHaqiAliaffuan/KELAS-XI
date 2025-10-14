<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Resources\WasteCategoryResource;
use App\Models\WasteCategory;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class WasteCategoryController extends Controller
{
    /**
     * Display a listing of active waste categories
     */
    public function index()
    {
        $categories = WasteCategory::where('is_active', true)->get();

        return response()->json([
            'data' => WasteCategoryResource::collection($categories)
        ]);
    }

    /**
     * Store a newly created waste category
     */
    public function store(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'name' => 'required|string|max:255',
            'description' => 'nullable|string',
            'unit' => 'required|in:kg,pcs,liter',
            'base_price_per_unit' => 'required|numeric|min:0',
            'icon_url' => 'nullable|url',
            'is_active' => 'boolean',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $category = WasteCategory::create($request->all());

        return response()->json([
            'message' => 'Waste category created successfully',
            'data' => new WasteCategoryResource($category)
        ], 201);
    }

    /**
     * Display the specified waste category
     */
    public function show($id)
    {
        $category = WasteCategory::findOrFail($id);

        if (!$category->is_active) {
            return response()->json([
                'message' => 'Waste category not found or not active'
            ], 404);
        }

        return response()->json([
            'data' => new WasteCategoryResource($category)
        ]);
    }

    /**
     * Update the specified waste category
     */
    public function update(Request $request, $id)
    {
        $category = WasteCategory::findOrFail($id);

        $validator = Validator::make($request->all(), [
            'name' => 'sometimes|required|string|max:255',
            'description' => 'nullable|string',
            'unit' => 'sometimes|required|in:kg,pcs,liter',
            'base_price_per_unit' => 'sometimes|required|numeric|min:0',
            'icon_url' => 'nullable|url',
            'is_active' => 'boolean',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $category->update($request->all());

        return response()->json([
            'message' => 'Waste category updated successfully',
            'data' => new WasteCategoryResource($category)
        ]);
    }

    /**
     * Remove the specified waste category
     */
    public function destroy($id)
    {
        $category = WasteCategory::findOrFail($id);
        $category->delete();

        return response()->json([
            'message' => 'Waste category deleted successfully'
        ]);
    }
}
