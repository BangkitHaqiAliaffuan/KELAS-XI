<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\UserController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');


Route::post('/users/auth', [AuthController::class, 'login']);
Route::post('/users', [UserController::class, 'createUser']);
Route::get('/users', [UserController::class, 'getAllUser']);
Route::get('/users/{id}', [UserController::class, 'getUser']);
Route::put('/users/{id}/update', [UserController::class, 'updateUser']);
Route::delete('/users/{id}/delete', [UserController::class, 'deleteUser']);

