<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Resources\RecipeResource;
use App\Models\Recipe;
use App\Models\Category;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;

class RecipeController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request): JsonResponse
    {
        $query = Recipe::with(['category', 'recipeAuthor']);

        // Filter by category if provided
        if ($request->has('category_id')) {
            $query->where('category_id', $request->category_id);
        }

        // Filter featured recipes
        if ($request->has('featured') && $request->featured == 'true') {
            $query->featured();
        }

        // Pagination
        $perPage = $request->get('per_page', 12);
        $recipes = $query->paginate($perPage);

        return response()->json([
            'success' => true,
            'data' => RecipeResource::collection($recipes),
            'meta' => [
                'current_page' => $recipes->currentPage(),
                'last_page' => $recipes->lastPage(),
                'per_page' => $recipes->perPage(),
                'total' => $recipes->total(),
            ],
            'message' => 'Recipes retrieved successfully'
        ]);
    }

    /**
     * Display the specified resource by slug.
     */
    public function show(Recipe $recipe): JsonResponse
    {
        $recipe->load([
            'category',
            'recipeAuthor',
            'recipeIngredients.ingredient',
            'recipePhotos',
            'recipeTutorials'
        ]);

        return response()->json([
            'success' => true,
            'data' => new RecipeResource($recipe),
            'message' => 'Recipe retrieved successfully'
        ]);
    }

    /**
     * Get featured recipes.
     */
    public function featured(): JsonResponse
    {
        $recipes = Recipe::with(['category', 'recipeAuthor'])
            ->featured()
            ->take(6)
            ->get();

        return response()->json([
            'success' => true,
            'data' => RecipeResource::collection($recipes),
            'message' => 'Featured recipes retrieved successfully'
        ]);
    }

    /**
     * Get recipes by category.
     */
    public function byCategory(Request $request, $categorySlug): JsonResponse
    {
        $category = Category::where('slug', $categorySlug)->firstOrFail();

        $query = Recipe::with(['category', 'recipeAuthor'])
            ->where('category_id', $category->id);

        $perPage = $request->get('per_page', 12);
        $recipes = $query->paginate($perPage);

        return response()->json([
            'success' => true,
            'data' => RecipeResource::collection($recipes),
            'meta' => [
                'current_page' => $recipes->currentPage(),
                'last_page' => $recipes->lastPage(),
                'per_page' => $recipes->perPage(),
                'total' => $recipes->total(),
            ],
            'category' => $category,
            'message' => 'Recipes by category retrieved successfully'
        ]);
    }
}
