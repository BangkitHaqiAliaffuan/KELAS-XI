<?php

use App\Http\Controllers\SocietyController;
use App\Http\Controllers\ValidationController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

// Route publik (tanpa login)
Route::post('/v1/auth/login', [SocietyController::class, 'login']);
Route::post('/v1/auth/register', [SocietyController::class, 'store']);

// Route yang membutuhkan login

Route::group(['auth:sanctum'], function (){
    Route::post('/v1/validation', [ValidationController::class, 'request']);
    Route::get('/v1/validation', [ValidationController::class, 'getValidation']);
});

