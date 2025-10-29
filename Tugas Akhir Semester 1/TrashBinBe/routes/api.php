<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\WasteCategoryController;
use App\Http\Controllers\Api\PickupController;
use App\Http\Controllers\Api\CollectorPickupController;
use App\Http\Controllers\Api\MarketplaceListingController;
use App\Http\Controllers\Api\MarketplaceOrderController;
use App\Http\Controllers\Api\PointsController;
use App\Http\Controllers\Api\RewardsController;
use App\Http\Controllers\Api\WasteClassificationController;

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

// Public routes
Route::post('/auth/register', [AuthController::class, 'register']);
Route::post('/auth/login', [AuthController::class, 'login']);

// Protected routes (require authentication)
Route::middleware('auth:sanctum')->group(function () {
    // Authentication routes
    Route::post('/auth/logout', [AuthController::class, 'logout']);
    Route::get('/auth/me', [AuthController::class, 'me']);
    Route::put('/auth/profile', [AuthController::class, 'updateProfile']);
    
    // Waste categories
    Route::apiResource('waste-categories', WasteCategoryController::class);
    
    // Pickup requests
    Route::apiResource('pickups', PickupController::class);
    Route::put('/pickups/{id}/cancel', [PickupController::class, 'cancel']);
    
    // Collector routes
    Route::prefix('collector')->group(function () {
        Route::get('/available-pickups', [CollectorPickupController::class, 'availablePickups']);
        Route::post('/pickups/{id}/accept', [CollectorPickupController::class, 'acceptPickup']);
        Route::put('/pickups/{id}/update-status', [CollectorPickupController::class, 'updateStatus']);
        Route::post('/pickups/{id}/confirm-weight', [CollectorPickupController::class, 'confirmWeight']);
        Route::put('/pickups/{id}/complete', [CollectorPickupController::class, 'complete']);
    });
    
    // Marketplace
    Route::prefix('marketplace')->group(function () {
        Route::apiResource('listings', MarketplaceListingController::class);
        Route::apiResource('orders', MarketplaceOrderController::class);
        Route::put('/orders/{id}/confirm', [MarketplaceOrderController::class, 'confirmOrder']);
        Route::put('/orders/{id}/ship', [MarketplaceOrderController::class, 'shipOrder']);
        Route::put('/orders/{id}/complete', [MarketplaceOrderController::class, 'completeOrder']);
        Route::put('/orders/{id}/cancel', [MarketplaceOrderController::class, 'cancelOrder']);
        Route::post('/orders/{id}/review', [MarketplaceOrderController::class, 'review']);
    });
    
    // Points & Rewards
    Route::get('/points', [PointsController::class, 'balance']);
    Route::get('/points/history', [PointsController::class, 'history']);
    Route::get('/rewards', [RewardsController::class, 'index']);
    Route::post('/rewards/{id}/redeem', [RewardsController::class, 'redeem']);
    
    // Waste Classification (AI)
    Route::post('/waste/classify', [WasteClassificationController::class, 'classifyWaste']);
});