<?php

use App\Http\Controllers\AdministratorController;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\GameController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');



Route::post('/v1/auth/signup', [AuthController::class, 'signup']);
Route::post('/v1/auth/signin', [AuthController::class, 'signin']);
Route::post('/v1/auth/signout', [AuthController::class, 'signout']);
// Route::post('/v1/auth/signin', [AdministratorController::class, 'signin']);

Route::middleware(['auth:sanctum'])->group(function () {
    Route::get('/v1/admins', [AdministratorController::class, 'index'])->middleware('admin');
    Route::post('/v1/users', [AdministratorController::class, 'create'])->middleware('admin');
    Route::get('/v1/users', [AdministratorController::class, 'showUser'])->middleware('admin');
    Route::put('/v1/users/{id}', [AdministratorController::class, 'update'])->middleware('admin');
    Route::delete('/v1/users/{id}', [AdministratorController::class, 'destroy'])->middleware('admin');
    Route::get('/v1/games', [GameController::class, 'index']);
});
