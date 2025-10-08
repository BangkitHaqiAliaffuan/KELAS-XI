<?php

namespace App\Http\Controllers;

use App\Models\Monitoring;
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
}
