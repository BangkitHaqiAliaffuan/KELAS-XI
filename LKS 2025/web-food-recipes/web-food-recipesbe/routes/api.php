<?php

use App\Http\Controllers\Api\CategoryController;
use App\Http\Controllers\Api\RecipeController;
use App\Http\Controllers\Api\SearchController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

Route::middleware('api.key')->group(function () {
    // Categories
    Route::get('/categories', [CategoryController::class, 'index']);
    Route::get('/categories/{category:slug}', [CategoryController::class, 'show']);

    // Recipes
    Route::get('/recipes', [RecipeController::class, 'index']);
    Route::get('/recipes/featured', [RecipeController::class, 'featured']);
    Route::get('/recipes/{recipe:slug}', [RecipeController::class, 'show']);
    Route::get('/categories/{categorySlug}/recipes', [RecipeController::class, 'byCategory']);

    // Search
    Route::get('/search', [SearchController::class, 'index']);
});

// Health check route (no API key required)
Route::get('/health', function () {
    return response()->json([
        'status' => 'ok',
        'timestamp' => now()->toISOString(),
    ]);
});
