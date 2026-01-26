<?php

use App\Http\Controllers\JobVacanciesController;
use App\Http\Controllers\SocietiesController;
use App\Http\Controllers\ValidationController;
use App\Models\JobVacancies;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');

Route::post('/v1/auth/login ', [SocietiesController::class, 'login'])->name('login');

Route::post('/v1/validations ', [ValidationController::class, 'create']);
Route::get('/v1/validations ', [ValidationController::class, 'show']);

Route::middleware(['auth:sanctum'])->group(function () {
    Route::get('/v1/job_vacancies/{id}', [JobVacanciesController::class, 'index']);
    Route::get('/v1/job_vacancies/{id}/detail', [JobVacanciesController::class, 'show']);
    Route::post('/v1/job_vacancies/apply', [JobVacanciesController::class, 'create']);
    Route::get('/v1/application/get', [JobVacanciesController::class, 'showOwnJob']);
});
