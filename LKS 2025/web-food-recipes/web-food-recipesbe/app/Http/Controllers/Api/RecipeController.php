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
        $query = Recipe::with(['category', 'recipeAuthor', 'recipePhotos']);

        // Search by name or description
        if ($request->has('search') && $request->search) {
            $query->where(function($q) use ($request) {
                $q->where('name', 'LIKE', '%' . $request->search . '%')
                  ->orWhere('description', 'LIKE', '%' . $request->search . '%');
            });
        }

        // Filter by category slug
        if ($request->has('category') && $request->category) {
            $query->whereHas('category', function($q) use ($request) {
                $q->where('slug', $request->category);
            });
        }

        // Filter by category_id (for backward compatibility)
        if ($request->has('category_id')) {
            $query->where('category_id', $request->category_id);
        }

        // Filter by difficulty
        if ($request->has('difficulty') && $request->difficulty) {
            $query->where('difficulty', $request->difficulty);
        }

        // Filter featured recipes
        if ($request->has('featured') && $request->featured == 'true') {
            $query->featured();
        }

        // Sorting
        $sort = $request->get('sort', 'latest');
        switch ($sort) {
            case 'latest':
                $query->orderBy('created_at', 'desc');
                break;
            case 'oldest':
                $query->orderBy('created_at', 'asc');
                break;
            case 'name_asc':
                $query->orderBy('name', 'asc');
                break;
            case 'name_desc':
                $query->orderBy('name', 'desc');
                break;
            case 'difficulty_asc':
                $query->orderByRaw("FIELD(difficulty, 'easy', 'medium', 'hard')");
                break;
            case 'difficulty_desc':
                $query->orderByRaw("FIELD(difficulty, 'hard', 'medium', 'easy')");
                break;
            default:
                $query->orderBy('created_at', 'desc');
        }

        // Pagination
        $perPage = $request->get('per_page', 12);
        $limit = $request->get('limit', $perPage);
        $recipes = $query->paginate($limit);

        return response()->json([
            'success' => true,
            'data' => RecipeResource::collection($recipes),
            'meta' => [
                'current_page' => $recipes->currentPage(),
                'last_page' => $recipes->lastPage(),
                'per_page' => $recipes->perPage(),
                'total' => $recipes->total(),
                'query' => $request->search ?? null,
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
