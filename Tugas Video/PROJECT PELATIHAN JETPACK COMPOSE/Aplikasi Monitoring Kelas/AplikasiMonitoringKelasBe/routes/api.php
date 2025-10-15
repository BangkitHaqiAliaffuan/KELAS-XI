<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\ScheduleController;
use App\Http\Controllers\MonitoringController;
use App\Http\Controllers\GuruPenggantiController;
use App\Http\Controllers\TeacherAttendanceController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
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

    // Teacher Attendance - Bisa diakses semua role yang sudah login
    Route::get('/teacher-attendance/today-schedules', [TeacherAttendanceController::class, 'todaySchedules']);
    Route::get('/teacher-attendance/today', [TeacherAttendanceController::class, 'today']);
    Route::get('/teacher-attendance/statistics', [TeacherAttendanceController::class, 'statistics']);
    Route::get('/teacher-attendance', [TeacherAttendanceController::class, 'index']);
    Route::post('/teacher-attendance', [TeacherAttendanceController::class, 'store']);
    Route::get('/teacher-attendance/{id}', [TeacherAttendanceController::class, 'show']);
    Route::put('/teacher-attendance/{id}', [TeacherAttendanceController::class, 'update']);
    Route::delete('/teacher-attendance/{id}', [TeacherAttendanceController::class, 'destroy']);
});

// Routes untuk SISWA - Mencatat monitoring
Route::middleware(['auth:sanctum', 'role:siswa'])->group(function () {
    Route::post('/monitoring/store', [MonitoringController::class, 'store']);
    Route::get('/monitoring/my-reports', [MonitoringController::class, 'myReports']); // Laporan yang dibuat siswa
});

// Routes untuk KURIKULUM - Cek kelas kosong & beri guru pengganti
Route::middleware(['auth:sanctum', 'role:kurikulum'])->group(function () {
    Route::get('/monitoring', [MonitoringController::class, 'index']);
    Route::get('/monitoring/kelas-kosong', [MonitoringController::class, 'kelasKosong']);

    // Guru Pengganti Management
    Route::get('/guru-pengganti', [GuruPenggantiController::class, 'index']);
    Route::post('/guru-pengganti', [GuruPenggantiController::class, 'store']);
    Route::put('/guru-pengganti/{id}', [GuruPenggantiController::class, 'update']);
    Route::delete('/guru-pengganti/{id}', [GuruPenggantiController::class, 'destroy']);
});

// Routes untuk KEPALA SEKOLAH - Hanya cek kelas kosong (readonly)
Route::middleware(['auth:sanctum', 'role:kepala_sekolah'])->group(function () {
    Route::get('/monitoring', [MonitoringController::class, 'index']);
    Route::get('/monitoring/kelas-kosong', [MonitoringController::class, 'kelasKosong']);
    Route::get('/guru-pengganti', [GuruPenggantiController::class, 'index']); // Hanya lihat
});

// Routes untuk ADMIN - User Management
Route::middleware(['auth:sanctum', 'role:admin'])->group(function () {
    // User Management
    Route::get('/users', [AuthController::class, 'getAllUsers']);
    Route::post('/users', [AuthController::class, 'createUser']);
    Route::put('/users/{id}/role', [AuthController::class, 'updateUserRole']);
    Route::put('/users/{id}/ban', [AuthController::class, 'banUser']);
    Route::put('/users/{id}/unban', [AuthController::class, 'unbanUser']);
    Route::delete('/users/{id}', [AuthController::class, 'deleteUser']);

    // Admin bisa mengelola jadwal
    Route::post('/jadwal', [ScheduleController::class, 'store']);
    Route::put('/jadwal/{id}', [ScheduleController::class, 'update']);
    Route::delete('/jadwal/{id}', [ScheduleController::class, 'destroy']);

    // Admin bisa lihat semua monitoring
    Route::get('/monitoring', [MonitoringController::class, 'index']);
});

