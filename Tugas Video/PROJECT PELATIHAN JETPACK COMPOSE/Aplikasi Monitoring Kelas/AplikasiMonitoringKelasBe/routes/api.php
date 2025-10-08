<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\ScheduleController;
use App\Http\Controllers\MonitoringController;

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
Route::post('/register', [AuthController::class, 'register']);
Route::post('/login', [AuthController::class, 'login']);

// Protected routes - Semua user yang sudah login
Route::middleware('auth:sanctum')->group(function () {
    // User Profile
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/user', [AuthController::class, 'user']);
    Route::put('/user', [AuthController::class, 'updateProfile']);

    // Jadwal Pelajaran - Bisa diakses semua role
    Route::get('/jadwal', [ScheduleController::class, 'index']);

    // Monitoring - Bisa diakses semua role (untuk melihat)
    Route::get('/monitoring', [MonitoringController::class, 'index']);
    Route::post('/monitoring/store', [MonitoringController::class, 'store']);
});

// Routes untuk Admin only
Route::middleware(['auth:sanctum', 'role:admin'])->group(function () {
    Route::get('/users', [AuthController::class, 'getAllUsers']);
    Route::put('/users/{id}/role', [AuthController::class, 'updateUserRole']);

    // Admin bisa mengelola jadwal
    Route::post('/jadwal', [ScheduleController::class, 'store']);
});

// Routes untuk Guru dan Admin
Route::middleware(['auth:sanctum', 'role:guru,admin'])->group(function () {
    Route::get('/guru/dashboard', function () {
        return response()->json([
            'success' => true,
            'message' => 'Guru Dashboard',
            'data' => ['role' => 'guru']
        ]);
    });
});

// Routes untuk semua role (Siswa, Guru, Admin)
Route::middleware(['auth:sanctum', 'role:siswa,guru,admin'])->group(function () {
    Route::get('/siswa/dashboard', function () {
        return response()->json([
            'success' => true,
            'message' => 'Siswa Dashboard',
            'data' => ['role' => 'siswa']
        ]);
    });
});
