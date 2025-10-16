<?php

namespace App\Http\Controllers;

use App\Models\TeacherAttendance;
use App\Models\Schedule;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Carbon\Carbon;

class TeacherAttendanceController extends Controller
{
    /**
     * Display a listing of teacher attendances with optional filters
     */
    public function index(Request $request)
    {
        $query = TeacherAttendance::with(['schedule', 'guru', 'createdBy']);

        // Filter by date
        if ($request->has('tanggal')) {
            $query->where('tanggal', $request->tanggal);
        }

        // Filter by date range
        if ($request->has('start_date') && $request->has('end_date')) {
            $query->whereBetween('tanggal', [$request->start_date, $request->end_date]);
        }

        // Filter by guru
        if ($request->has('guru_id')) {
            $query->where('guru_id', $request->guru_id);
        }

        // Filter by status
        if ($request->has('status')) {
            $query->where('status', $request->status);
        }

        // Filter by kelas
        if ($request->has('kelas')) {
            $query->whereHas('schedule', function($q) use ($request) {
                $q->where('kelas', $request->kelas);
            });
        }

        // Filter by mata pelajaran
        if ($request->has('mata_pelajaran')) {
            $query->whereHas('schedule', function($q) use ($request) {
                $q->where('mata_pelajaran', $request->mata_pelajaran);
            });
        }

        $attendances = $query->orderBy('tanggal', 'desc')
                            ->orderBy('created_at', 'desc')
                            ->paginate($request->get('per_page', 15));

        return response()->json($attendances);
    }

    /**
     * Get today's teacher attendances
     */
    public function today(Request $request)
    {
        $today = Carbon::today()->format('Y-m-d');

        $attendances = TeacherAttendance::with(['schedule', 'guru', 'createdBy'])
            ->where('tanggal', $today)
            ->orderBy('jam_masuk', 'asc')
            ->get();

        return response()->json([
            'tanggal' => $today,
            'total' => $attendances->count(),
            'hadir' => $attendances->where('status', 'hadir')->count(),
            'telat' => $attendances->where('status', 'telat')->count(),
            'tidak_hadir' => $attendances->where('status', 'tidak_hadir')->count(),
            'data' => $attendances
        ]);
    }

    /**
     * Get today's schedules with attendance status
     */
    public function todaySchedules(Request $request)
    {
        $today = Carbon::today()->format('Y-m-d');
        $dayName = Carbon::today()->locale('id')->dayName;

        // Also try English day name for fallback
        $dayNameEn = Carbon::today()->format('l');

        // Get schedules for today (try both Indonesian and English)
        $schedules = Schedule::with(['guru'])
            ->where(function($query) use ($dayName, $dayNameEn) {
                $query->where('hari', $dayName)
                      ->orWhere('hari', $dayNameEn)
                      ->orWhere('hari', ucfirst(strtolower($dayName)))
                      ->orWhere('hari', ucfirst(strtolower($dayNameEn)));
            })
            ->orderBy('jam_mulai', 'asc')
            ->get();

        // Get attendances for today with relationships
        $attendances = TeacherAttendance::with(['schedule', 'guru', 'createdBy', 'guruAsli'])
            ->where('tanggal', $today)
            ->get()
            ->keyBy('schedule_id');

        // Merge schedule with attendance data
        $result = $schedules->map(function($schedule) use ($attendances) {
            $attendance = $attendances->get($schedule->id);

            return [
                'schedule' => $schedule,
                'attendance' => $attendance,
                'has_attendance' => $attendance !== null,
                'status' => $attendance ? $attendance->status : 'belum_dicatat'
            ];
        });

        return response()->json([
            'tanggal' => $today,
            'hari' => $dayName,
            'hari_en' => $dayNameEn, // Debug info
            'total_schedules' => $result->count(),
            'sudah_dicatat' => $result->where('has_attendance', true)->count(),
            'belum_dicatat' => $result->where('has_attendance', false)->count(),
            'data' => $result
        ]);
    }

    /**
     * Get all schedules with attendance status (for all days)
     */
    public function allSchedules(Request $request)
    {
        $today = Carbon::today()->format('Y-m-d');

        // Get parameter for date filtering (optional)
        $tanggal = $request->get('tanggal', $today);

        // Get all schedules
        $schedules = Schedule::with(['guru'])
            ->orderBy('hari', 'asc')
            ->orderBy('jam_mulai', 'asc')
            ->get();

        // Get attendances for the specified date with relationships
        $attendances = TeacherAttendance::with(['schedule', 'guru', 'createdBy', 'guruAsli'])
            ->where('tanggal', $tanggal)
            ->get()
            ->keyBy('schedule_id');

        // Merge schedule with attendance data
        $result = $schedules->map(function($schedule) use ($attendances) {
            $attendance = $attendances->get($schedule->id);

            return [
                'schedule' => $schedule,
                'attendance' => $attendance,
                'has_attendance' => $attendance !== null,
                'status' => $attendance ? $attendance->status : 'belum_dicatat'
            ];
        });

        // Group by day
        $groupedByDay = $result->groupBy(function($item) {
            return $item['schedule']->hari;
        });

        return response()->json([
            'tanggal' => $tanggal,
            'total_schedules' => $result->count(),
            'sudah_dicatat' => $result->where('has_attendance', true)->count(),
            'belum_dicatat' => $result->where('has_attendance', false)->count(),
            'data' => $result,
            'grouped_by_day' => $groupedByDay
        ]);
    }

    /**
     * Store a newly created attendance
     */
    public function store(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'schedule_id' => 'required|exists:schedules,id',
            'guru_id' => 'required|exists:users,id',
            'tanggal' => 'required|date',
            'jam_masuk' => 'required|date_format:H:i',
            'status' => 'required|in:hadir,telat,tidak_hadir',
            'keterangan' => 'nullable|string'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validation error',
                'errors' => $validator->errors()
            ], 422);
        }

        // Check if attendance already exists
        $existing = TeacherAttendance::where('schedule_id', $request->schedule_id)
            ->where('guru_id', $request->guru_id)
            ->where('tanggal', $request->tanggal)
            ->first();

        if ($existing) {
            return response()->json([
                'message' => 'Kehadiran untuk jadwal ini sudah dicatat'
            ], 409);
        }

        $attendance = TeacherAttendance::create([
            'schedule_id' => $request->schedule_id,
            'guru_id' => $request->guru_id,
            'tanggal' => $request->tanggal,
            'jam_masuk' => $request->jam_masuk,
            'status' => $request->status,
            'keterangan' => $request->keterangan,
            'created_by' => $request->user()->id
        ]);

        $attendance->load(['schedule', 'guru', 'createdBy']);

        return response()->json([
            'message' => 'Kehadiran guru berhasil dicatat',
            'data' => $attendance
        ], 201);
    }

    /**
     * Display the specified attendance
     */
    public function show($id)
    {
        $attendance = TeacherAttendance::with(['schedule', 'guru', 'createdBy'])
            ->findOrFail($id);

        return response()->json($attendance);
    }

    /**
     * Update the specified attendance
     */
    public function update(Request $request, $id)
    {
        $attendance = TeacherAttendance::findOrFail($id);

        $validator = Validator::make($request->all(), [
            'jam_masuk' => 'sometimes|date_format:H:i',
            'status' => 'sometimes|in:hadir,telat,tidak_hadir',
            'keterangan' => 'nullable|string'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Validation error',
                'errors' => $validator->errors()
            ], 422);
        }

        $attendance->update($request->only(['jam_masuk', 'status', 'keterangan']));
        $attendance->load(['schedule', 'guru', 'createdBy']);

        return response()->json([
            'message' => 'Kehadiran guru berhasil diperbarui',
            'data' => $attendance
        ]);
    }

    /**
     * Remove the specified attendance
     */
    public function destroy($id)
    {
        $attendance = TeacherAttendance::findOrFail($id);
        $attendance->delete();

        return response()->json([
            'message' => 'Kehadiran guru berhasil dihapus'
        ]);
    }

    /**
     * Get attendance statistics
     */
    public function statistics(Request $request)
    {
        $query = TeacherAttendance::query();

        // Filter by date range
        if ($request->has('start_date') && $request->has('end_date')) {
            $query->whereBetween('tanggal', [$request->start_date, $request->end_date]);
        }

        // Filter by guru
        if ($request->has('guru_id')) {
            $query->where('guru_id', $request->guru_id);
        }

        $stats = [
            'total' => $query->count(),
            'hadir' => (clone $query)->where('status', 'hadir')->count(),
            'telat' => (clone $query)->where('status', 'telat')->count(),
            'tidak_hadir' => (clone $query)->where('status', 'tidak_hadir')->count(),
        ];

        $stats['percentage_hadir'] = $stats['total'] > 0
            ? round(($stats['hadir'] / $stats['total']) * 100, 2)
            : 0;
        $stats['percentage_telat'] = $stats['total'] > 0
            ? round(($stats['telat'] / $stats['total']) * 100, 2)
            : 0;

        return response()->json($stats);
    }
}
