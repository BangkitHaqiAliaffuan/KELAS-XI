<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\Api\PickupController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

// ─────────────────────────────────────────────────────────────────
// Public auth routes (no token required)
// ─────────────────────────────────────────────────────────────────
Route::prefix('auth')->group(function () {
    Route::post('/register', [AuthController::class, 'register']);
    Route::post('/login',    [AuthController::class, 'login']);
});

// ─────────────────────────────────────────────────────────────────
// Protected routes (Sanctum Bearer token required)
// ─────────────────────────────────────────────────────────────────
Route::middleware('auth:sanctum')->prefix('auth')->group(function () {
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/me',      [AuthController::class, 'me']);
});

// ─────────────────────────────────────────────────────────────────
// Pickup routes (Sanctum Bearer token required)
// ─────────────────────────────────────────────────────────────────
Route::middleware('auth:sanctum')->prefix('pickups')->group(function () {
    Route::get('/',         [PickupController::class, 'index']);   // GET  /api/pickups
    Route::post('/',        [PickupController::class, 'store']);   // POST /api/pickups
    Route::get('/{id}',     [PickupController::class, 'show']);    // GET  /api/pickups/{id}
    Route::post('/{id}/cancel', [PickupController::class, 'cancel']); // POST /api/pickups/{id}/cancel
});
