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
Route::get('/player/{id}', [PlayerController::class, 'getPlayer']);

Route::middleware(['auth:sanctum'])->group(function () {
    Route::put('/users/{id}/update', [UserController::class, 'updateUser']);
    Route::delete('/users/{id}/delete', [UserController::class, 'deleteUser']);
    Route::get('/player', [PlayerController::class, 'getAllPlayers']);
    Route::post('/player/post', [PlayerController::class, 'createNewPlayer']);
    Route::post('/player/{id}/update', [PlayerController::class, 'updatePlayer']);
    Route::delete('/player/{id}', [PlayerController::class, 'deletePlayer']);
});

Route::get('/test-token', function (Request $request) {

    $user = $request->user();

    return response()->json([
        'user' => $user->id,
        'token_valid' => $request->user() ? true : false
    ]);
})->middleware('auth:sanctum');

