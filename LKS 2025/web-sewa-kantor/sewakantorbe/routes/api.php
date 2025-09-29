<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\CityController;
use App\Http\Controllers\Api\OfficeController;
use App\Http\Controllers\Api\FacilityController;
use App\Http\Controllers\Api\TransactionController;

// Public routes (tidak memerlukan authentication)
Route::prefix('v1')->group(function () {
    // Cities
    Route::apiResource('cities', CityController::class)->only(['index', 'show']);

    // Offices
    Route::apiResource('offices', OfficeController::class)->only(['index', 'show']);

    

    // Facilities
    Route::apiResource('facilities', FacilityController::class)->only(['index', 'show']);

    // Office search with advanced filters
    Route::get('offices/search', [OfficeController::class, 'index']);

    // Get offices by city
    Route::get('cities/{city}/offices', function ($cityId) {
        return app(OfficeController::class)->index(request()->merge(['city_id' => $cityId]));
    });
});

// Protected routes (memerlukan authentication)
Route::prefix('v1')->middleware('auth:sanctum')->group(function () {
    // User profile
    Route::get('/user', function (Request $request) {
        return $request->user();
    });

    // User transactions
    Route::get('/user/transactions', [TransactionController::class, 'userTransactions']);

    // Create booking/transaction
    Route::post('/bookings', [TransactionController::class, 'store']);
    Route::get('/bookings/{booking}', [TransactionController::class, 'show']);
});

// Admin routes (memerlukan authentication dan admin role)
Route::prefix('v1/admin')->middleware(['auth:sanctum'])->group(function () {
    // Cities management
    Route::apiResource('cities', CityController::class)->except(['index', 'show']);

    // Offices management
    Route::apiResource('offices', OfficeController::class)->except(['index', 'show']);

    // Facilities management
    Route::apiResource('facilities', FacilityController::class);

    // Transactions management
    Route::apiResource('transactions', TransactionController::class);
    Route::patch('transactions/{transaction}/status', [TransactionController::class, 'updateStatus']);
    Route::patch('transactions/{transaction}/payment-status', [TransactionController::class, 'updatePaymentStatus']);
});
