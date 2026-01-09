<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\PlayerController;
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
Route::put('/users/{id}/update', [UserController::class, 'updateUser'])->middleware('auth:sanctum');
Route::delete('/users/{id}/delete', [UserController::class, 'deleteUser'])->middleware('auth:sanctum');
Route::get('/player', [PlayerController::class, 'getAllPlayers']);
Route::post('/player/post', [PlayerController::class, 'createNewPlayer'])->middleware('auth:sanctum');
Route::get('/test-token', function (Request $request) {
    return response()->json([
        'user' => $request->user(),
        'token_valid' => $request->user() ? true : false
    ]);
})->middleware('auth:sanctum');

