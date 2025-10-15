<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\ScheduleController;
use App\Http\Controllers\MonitoringController;
use App\Http\Controllers\AssignmentController;
use App\Http\Controllers\GradeController;

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

    // Assignments - Semua role bisa lihat
    Route::get('/assignments', [AssignmentController::class, 'index']);
    Route::get('/assignments/{id}', [AssignmentController::class, 'show']);

    // Grades - Semua role bisa lihat (filtered by role in controller)
    Route::get('/grades', [GradeController::class, 'index']);
    Route::get('/grades/siswa/{id}', [GradeController::class, 'getSiswaGrades']);
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

    // Assignment Management (Guru only)
    Route::post('/assignments', [AssignmentController::class, 'store']);
    Route::put('/assignments/{id}', [AssignmentController::class, 'update']);
    Route::delete('/assignments/{id}', [AssignmentController::class, 'destroy']);
    Route::get('/assignments/{id}/submissions', [AssignmentController::class, 'getSubmissions']);

    // Grade Management (Guru only)
    Route::post('/grades', [GradeController::class, 'store']);
    Route::put('/grades/{id}', [GradeController::class, 'update']);
    Route::delete('/grades/{id}', [GradeController::class, 'destroy']);
    Route::get('/grades/kelas/{kelas}', [GradeController::class, 'getKelasGrades']);
});

// Routes untuk Siswa
Route::middleware(['auth:sanctum', 'role:siswa,guru,admin'])->group(function () {
    // Submit assignment
    Route::post('/assignments/{id}/submit', [AssignmentController::class, 'submit']);
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
