<?php

use App\Http\Controllers\InstallmentController;
use App\Http\Controllers\SocietyController;
use App\Http\Controllers\ValidationController;
use App\Models\Installment;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');

Route::post('/v1/auth/login', [SocietyController::class, 'login']);

Route::middleware(['auth:sanctum'])->group(function () {
    Route::post('/v1/auth/logout', [SocietyController::class, 'logout']);
    Route::post('/v1/validation', [ValidationController::class, 'create']);
    Route::get('/v1/validation', [ValidationController::class, 'show']);
    Route::get('/v1/installment', [InstallmentController::class, 'index']);
    Route::get('/v1/installment/{id}', [InstallmentController::class, 'show']);
});
