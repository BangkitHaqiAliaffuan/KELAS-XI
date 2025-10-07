<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\{
    AuthController,
    AdminAuthController,
    CityController,
    OfficeController,
    FacilityController,
    TransactionController
};
use App\Http\Controllers\Api\Admin\UserController as AdminUserController;

// User Authentication routes (public)
Route::prefix('v1/auth')->group(function () {
    Route::post('/register', [AuthController::class, 'register']);
    Route::post('/login', [AuthController::class, 'login']);
    Route::post('/logout', [AuthController::class, 'logout'])->middleware('auth:sanctum');
    Route::get('/me', [AuthController::class, 'me'])->middleware('auth:sanctum');
});

// Admin Authentication routes (public)
Route::prefix('v1/admin/auth')->group(function () {
    Route::post('/login', [AdminAuthController::class, 'login']);
    Route::post('/logout', [AdminAuthController::class, 'logout'])->middleware('auth:sanctum');
    Route::get('/me', [AdminAuthController::class, 'me'])->middleware('auth:sanctum');
    Route::put('/profile', [AdminAuthController::class, 'updateProfile'])->middleware('auth:sanctum');
    Route::post('/change-password', [AdminAuthController::class, 'changePassword'])->middleware('auth:sanctum');
});

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

    // User Dashboard routes
    Route::prefix('dashboard')->group(function () {
        Route::get('/statistics', [TransactionController::class, 'userStatistics']);
        Route::get('/bookings', [TransactionController::class, 'userBookings']);
        Route::patch('/bookings/{id}/cancel', [TransactionController::class, 'cancelBooking']);
    });
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

    // Users management
    Route::apiResource('users', AdminUserController::class)->except(['store']);
    Route::patch('users/{user}/toggle-status', [AdminUserController::class, 'toggleStatus']);
});
