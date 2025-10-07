<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Resources\RecipeResource;
use App\Models\Recipe;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;

class SearchController extends Controller
{
    /**
     * Search recipes by query.
     */
    public function index(Request $request): JsonResponse
    {
        $query = $request->get('q', '');

        if (empty($query)) {
            return response()->json([
                'success' => false,
                'message' => 'Search query is required',
                'data' => []
            ], 400);
        }

        $recipes = Recipe::with(['category', 'recipeAuthor'])
            ->search($query)
            ->paginate($request->get('per_page', 12));

        return response()->json([
            'success' => true,
            'data' => RecipeResource::collection($recipes),
            'meta' => [
                'current_page' => $recipes->currentPage(),
                'last_page' => $recipes->lastPage(),
                'per_page' => $recipes->perPage(),
                'total' => $recipes->total(),
                'query' => $query
            ],
            'message' => 'Search results retrieved successfully'
        ]);
    }
}
