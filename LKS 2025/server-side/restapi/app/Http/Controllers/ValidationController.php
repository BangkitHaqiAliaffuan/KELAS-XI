<?php

namespace App\Http\Controllers;

use App\Models\Validation;
use App\Models\Society;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class ValidationController extends Controller
{
    public function request(Request $request)
    {
        // Ambil token dari header Authorization
        $token = $request->bearerToken();

        if (!$token) {
            return response()->json([
                'error' => 'Token tidak ditemukan'
            ], 401);
        }

        // DEBUG: Cek semua societies dan login_tokens mereka


        // Cari society berdasarkan login_tokens menggunakan DB Query
        $societyData = Society::where('login_tokens', $token)->first();



        $validation = Validation::create([
            // 'status' => 'pending',
            'society_id' => $societyData->id,
            'job_position' => $request->job_position,
            'job_category_id' => $request->job_category_id,
            'reason_accepted' => $request->reason_accepted,
            'work_experience' => $request->work_experience,
        ]);

        return response()->json([
            'message' => 'Validation request berhasil dibuat',
            'data' => $validation,
        ], 201);
    }
    public function getValidation(Request $request){
        $token = $request->bearerToken();
        $society = Society::where('login_tokens', $token)->first();
        $validation = Validation::where('society_id', $society->id)
            ->orderBy('created_at', 'desc')
            ->take(2)
            ->get();
        return response()->json([
            'message' => $society->id,
            'validation' => $validation,
        ]);
    }
}
