<?php

use App\Http\Controllers\PostController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');

// Endpoint untuk mendapatkan semua data berita (posts)
Route::get('/posts', [PostController::class, 'index']); // Endpoint List
Route::get('/posts/{id}', [PostController::class, 'show']); // Endpoint Detail
