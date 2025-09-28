<?php

use App\Http\Controllers\PostController;
use App\Http\Controllers\AuthController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

// Authentication Routes (Public)
Route::post('/register', [AuthController::class, 'register']);
Route::post('/login', [AuthController::class, 'login'])->name("login");

// Public Posts Routes (No Authentication Required)
Route::get('/posts', [PostController::class, 'index']); // Endpoint List
Route::get('/posts/{id}', [PostController::class, 'show']); // Endpoint Detail

Route::get('/login',function(){
    return response()->json([
        'message' => 'Please Login First',
        'error' => 'authenticated',
    ]);
});

// Protected Routes (Require Authentication)
Route::middleware('auth:sanctum')->group(function () {
    // User Profile Routes
    Route::get('/profile', [AuthController::class, 'profile']); // atau /me
    Route::post('/logout', [AuthController::class, 'logout']);

    // Posts CRUD Routes (Authenticated)
    Route::post('/posts', [PostController::class, 'store']); // Create Post
    Route::put('/posts/{id}', [PostController::class, 'update'])->middleware('pemilik_postingan'); // Update Post
    Route::delete('/posts/{id}', [PostController::class, 'destroy'])->middleware('pemilik_postingan'); // Delete Post
});
