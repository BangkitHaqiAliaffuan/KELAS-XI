<?php

namespace App\Http\Controllers;

use App\Models\Monitoring;
use App\Models\Schedule;
use App\Models\TeacherAttendance;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Carbon\Carbon;

class MonitoringController extends Controller
{
    /**
     * Menyimpan data monitoring kehadiran guru
     */
    public function store(Request $request)
    {
        try {
            $request->validate([
                'guru_id' => 'required|exists:users,id',
                'status_hadir' => 'required|in:Hadir,Terlambat,Tidak Hadir',
                'catatan' => 'nullable|string',
                'kelas' => 'required|string|max:10',
                'mata_pelajaran' => 'required|string|max:255',
                'tanggal' => 'nullable|date',
                'jam_laporan' => 'nullable|date_format:H:i'
            ]);

            $monitoring = Monitoring::create([
                'guru_id' => $request->guru_id,
                'pelapor_id' => $request->user()->id, // User yang sedang login
                'status_hadir' => $request->status_hadir,
                'catatan' => $request->catatan,
                'kelas' => $request->kelas,
                'mata_pelajaran' => $request->mata_pelajaran,
                'tanggal' => $request->tanggal ?? Carbon::today(),
                'jam_laporan' => $request->jam_laporan ?? Carbon::now()->format('H:i')
            ]);

            $monitoring->load(['guru:id,name,email', 'pelapor:id,name,email']);

            return response()->json([
                'success' => true,
                'message' => 'Monitoring berhasil dicatat',
                'data' => $monitoring
            ], Response::HTTP_CREATED);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mengambil data monitoring
     */
    public function index(Request $request)
    {
        try {
            $query = Monitoring::with(['guru:id,name,email', 'pelapor:id,name,email']);

            // Filter berdasarkan tanggal
            if ($request->has('tanggal') && $request->tanggal) {
                $query->whereDate('tanggal', $request->tanggal);
            }

            // Filter berdasarkan kelas
            if ($request->has('kelas') && $request->kelas) {
                $query->where('kelas', $request->kelas);
            }

            // Filter berdasarkan guru_id
            if ($request->has('guru_id') && $request->guru_id) {
                $query->where('guru_id', $request->guru_id);
            }

            $monitoring = $query->orderBy('tanggal', 'desc')
                               ->orderBy('jam_laporan', 'desc')
                               ->get();

            return response()->json([
                'success' => true,
                'message' => 'Data monitoring berhasil diambil',
                'data' => $monitoring
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mengambil laporan monitoring yang dibuat siswa (untuk siswa)
     */
    public function myReports(Request $request)
    {
        try {
            $query = Monitoring::with(['guru:id,name,email,mata_pelajaran'])
                              ->where('pelapor_id', $request->user()->id);

            // Filter berdasarkan tanggal
            if ($request->has('tanggal') && $request->tanggal) {
                $query->whereDate('tanggal', $request->tanggal);
            }

            $monitoring = $query->orderBy('created_at', 'desc')->get();

            return response()->json([
                'success' => true,
                'message' => 'Data laporan berhasil diambil',
                'data' => $monitoring
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mengecek kelas kosong berdasarkan jadwal hari ini dan teacher attendance
     */
    public function kelasKosong(Request $request)
    {
        try {
            // Ambil tanggal dari request (dari device) atau gunakan tanggal hari ini dari server
            // Jika client mengirim tanggal, gunakan tanggal tersebut
            $tanggal = $request->input('tanggal');

            // Jika tidak ada tanggal dari client, gunakan tanggal server dengan timezone Asia/Jakarta
            if (!$tanggal) {
                $tanggal = Carbon::now('Asia/Jakarta')->format('Y-m-d');
            } else {
                // Validasi format tanggal
                try {
                    $tanggal = Carbon::parse($tanggal)->format('Y-m-d');
                } catch (\Exception $e) {
                    return response()->json([
                        'success' => false,
                        'message' => 'Format tanggal tidak valid. Gunakan format YYYY-MM-DD',
                        'error' => $e->getMessage()
                    ], Response::HTTP_BAD_REQUEST);
                }
            }

            // Mapping hari dari Carbon ke format database
            $hariMapping = [
                'Monday' => 'Senin',
                'Tuesday' => 'Selasa',
                'Wednesday' => 'Rabu',
                'Thursday' => 'Kamis',
                'Friday' => 'Jumat',
                'Saturday' => 'Sabtu',
                'Sunday' => 'Minggu'
            ];

            $hari = $hariMapping[Carbon::parse($tanggal)->format('l')] ?? 'Senin';

            // Ambil semua jadwal untuk hari ini
            $jadwalHariIni = Schedule::with(['guru:id,name,email,mata_pelajaran'])
                ->where('hari', $hari)
                ->get();

            // Ambil teacher attendance untuk tanggal yang diminta dengan status tidak_hadir
            $teacherAttendanceTidakHadir = TeacherAttendance::with(['schedule', 'guru:id,name,email,mata_pelajaran'])
                ->whereDate('tanggal', $tanggal)
                ->where('status', 'tidak_hadir')
                ->get()
                ->keyBy('schedule_id');

            $kelasKosong = [];

            // Cek setiap jadwal apakah guru tidak hadir
            foreach ($jadwalHariIni as $jadwal) {
                // Jika ada teacher attendance dengan status tidak_hadir untuk jadwal ini
                if (isset($teacherAttendanceTidakHadir[$jadwal->id])) {
                    $attendance = $teacherAttendanceTidakHadir[$jadwal->id];

                    $kelasKosong[] = [
                        'jadwal_id' => $jadwal->id,
                        'attendance_id' => $attendance->id,
                        'kelas' => $jadwal->kelas,
                        'mata_pelajaran' => $jadwal->mata_pelajaran,
                        'guru' => $jadwal->guru,
                        'jam_mulai' => $jadwal->jam_mulai,
                        'jam_selesai' => $jadwal->jam_selesai,
                        'ruang' => $jadwal->ruang,
                        'tanggal' => $tanggal,
                        'hari' => $hari,
                        'status' => 'Tidak Hadir',
                        'keterangan' => $attendance->keterangan
                    ];
                }
            }

            return response()->json([
                'success' => true,
                'message' => 'Data kelas kosong berhasil diambil',
                'data' => $kelasKosong,
                'summary' => [
                    'total_jadwal' => $jadwalHariIni->count(),
                    'total_kelas_kosong' => count($kelasKosong),
                    'tanggal' => $tanggal,
                    'hari' => $hari
                ]
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mengambil semua laporan kehadiran guru (khusus untuk kepala sekolah dan admin)
     */
    public function getAllEmptyClassReports(Request $request)
    {
        try {
            // Get all teacher attendances (all statuses)
            $query = \App\Models\TeacherAttendance::with(['guru:id,name,email,mata_pelajaran', 'schedule:id,kelas,mata_pelajaran,ruang,hari,jam_mulai,jam_selesai']);

            // Filter berdasarkan tanggal
            if ($request->has('tanggal') && $request->tanggal) {
                $query->whereDate('tanggal', $request->tanggal);
            }

            // Filter berdasarkan kelas melalui schedule
            if ($request->has('kelas') && $request->kelas) {
                $query->whereHas('schedule', function($q) use ($request) {
                    $q->where('kelas', $request->kelas);
                });
            }

            // Filter berdasarkan guru_id
            if ($request->has('guru_id') && $request->guru_id) {
                $query->where('guru_id', $request->guru_id);
            }

            // Filter berdasarkan status (opsional)
            if ($request->has('status') && $request->status) {
                $query->where('status', $request->status);
            }

            $attendances = $query->orderBy('tanggal', 'desc')
                                ->orderBy('jam_masuk', 'desc')
                                ->get();

            // Format the data to match the expected monitoring response structure
            $formattedData = $attendances->map(function($attendance) {
                return [
                    'id' => $attendance->id,
                    'guru_id' => $attendance->guru_id,
                    'guru' => $attendance->guru,
                    'pelapor_id' => $attendance->created_by,
                    'pelapor' => $attendance->createdBy, // Using createdBy from the relationship
                    'status_hadir' => $attendance->status,
                    'catatan' => $attendance->keterangan,
                    'kelas' => $attendance->schedule->kelas ?? 'N/A',
                    'mata_pelajaran' => $attendance->schedule->mata_pelajaran ?? 'N/A',
                    'tanggal' => $attendance->tanggal,
                    'jam_laporan' => $attendance->jam_masuk,
                    'created_at' => $attendance->created_at,
                    'updated_at' => $attendance->updated_at
                ];
            });

            return response()->json([
                'success' => true,
                'message' => 'Data laporan kehadiran guru berhasil diambil',
                'data' => $formattedData
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Mengambil laporan kelas kosong (hanya status tidak hadir) dari siswa (khusus untuk kepala sekolah dan admin)
     */
    public function getEmptyClassOnly(Request $request)
    {
        try {
            // Get teacher attendances with status 'tidak_hadir' only (empty classes)
            $query = \App\Models\TeacherAttendance::with(['guru:id,name,email,mata_pelajaran', 'schedule:id,kelas,mata_pelajaran,ruang,hari,jam_mulai,jam_selesai'])
                              ->where('status', 'tidak_hadir');

            // Filter berdasarkan tanggal
            if ($request->has('tanggal') && $request->tanggal) {
                $query->whereDate('tanggal', $request->tanggal);
            }

            // Filter berdasarkan kelas melalui schedule
            if ($request->has('kelas') && $request->kelas) {
                $query->whereHas('schedule', function($q) use ($request) {
                    $q->where('kelas', $request->kelas);
                });
            }

            // Filter berdasarkan guru_id
            if ($request->has('guru_id') && $request->guru_id) {
                $query->where('guru_id', $request->guru_id);
            }

            $attendances = $query->orderBy('tanggal', 'desc')
                                ->orderBy('jam_masuk', 'desc')
                                ->get();

            // Format the data to match the expected monitoring response structure
            $formattedData = $attendances->map(function($attendance) {
                return [
                    'id' => $attendance->id,
                    'guru_id' => $attendance->guru_id,
                    'guru' => $attendance->guru,
                    'pelapor_id' => $attendance->created_by,
                    'pelapor' => $attendance->createdBy, // Using createdBy from the relationship
                    'status_hadir' => $attendance->status,
                    'catatan' => $attendance->keterangan,
                    'kelas' => $attendance->schedule->kelas ?? 'N/A',
                    'mata_pelajaran' => $attendance->schedule->mata_pelajaran ?? 'N/A',
                    'tanggal' => $attendance->tanggal,
                    'jam_laporan' => $attendance->jam_masuk,
                    'created_at' => $attendance->created_at,
                    'updated_at' => $attendance->updated_at
                ];
            });

            return response()->json([
                'success' => true,
                'message' => 'Data laporan kelas kosong berhasil diambil',
                'data' => $formattedData
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Mengambil data kelas kosong berdasarkan teacher attendance (endpoint khusus untuk kelas kosong dari kehadiran guru)
     */
    public function getKelasKosongFromAttendance(Request $request)
    {
        try {
            // Ambil tanggal dari request atau gunakan tanggal hari ini
            $tanggal = $request->input('tanggal');
            
            if (!$tanggal) {
                $tanggal = Carbon::now('Asia/Jakarta')->format('Y-m-d');
            } else {
                // Validasi format tanggal
                try {
                    $tanggal = Carbon::parse($tanggal)->format('Y-m-d');
                } catch (\Exception $e) {
                    return response()->json([
                        'success' => false,
                        'message' => 'Format tanggal tidak valid. Gunakan format YYYY-MM-DD',
                        'error' => $e->getMessage()
                    ], Response::HTTP_BAD_REQUEST);
                }
            }

            // Ambil semua teacher attendance dengan status 'tidak_hadir' pada tanggal tertentu
            $query = TeacherAttendance::with(['guru:id,name,email,mata_pelajaran', 'schedule:id,kelas,mata_pelajaran,ruang,hari,jam_mulai,jam_selesai'])
                      ->where('status', 'tidak_hadir')
                      ->whereDate('tanggal', $tanggal);

            // Filter berdasarkan kelas jika disediakan
            if ($request->has('kelas') && $request->kelas) {
                $query->whereHas('schedule', function($q) use ($request) {
                    $q->where('kelas', $request->kelas);
                });
            }

            // Filter berdasarkan guru_id jika disediakan
            if ($request->has('guru_id') && $request->guru_id) {
                $query->where('guru_id', $request->guru_id);
            }

            $emptyClasses = $query->orderBy('tanggal', 'desc')
                                  ->orderBy('jam_masuk', 'desc')
                                  ->get();

            // Format the data to match the kelas_kosong response structure
            $formattedData = $emptyClasses->map(function($attendance) {
                $schedule = $attendance->schedule;
                
                return [
                    'attendance_id' => $attendance->id,
                    'kelas' => $schedule->kelas ?? 'N/A',
                    'mata_pelajaran' => $schedule->mata_pelajaran ?? 'N/A',
                    'guru' => $attendance->guru,
                    'jam_mulai' => $schedule->jam_mulai,
                    'jam_selesai' => $schedule->jam_selesai,
                    'ruang' => $schedule->ruang,
                    'tanggal' => $attendance->tanggal,
                    'hari' => $schedule->hari,
                    'keterangan' => $attendance->keterangan,
                    'status' => $attendance->status
                ];
            });

            return response()->json([
                'success' => true,
                'message' => 'Data kelas kosong dari kehadiran guru berhasil diambil',
                'data' => $formattedData,
                'summary' => [
                    'total_kelas_kosong' => $formattedData->count(),
                    'tanggal' => $tanggal,
                ]
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }
}
