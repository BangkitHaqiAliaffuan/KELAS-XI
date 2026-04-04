<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\BarangController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');

Route::post('/barang', [BarangController::class, 'createBarang']);
Route::post('/order', [BarangController::class, 'createOrder']);
Route::post('/user', [AuthController::class, 'register']);
Route::post('/user/login', [AuthController::class, 'login']);
