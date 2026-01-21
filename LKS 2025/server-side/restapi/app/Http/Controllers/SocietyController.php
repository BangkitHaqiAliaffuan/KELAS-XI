<?php

namespace App\Http\Controllers;

use App\Models\Society;
use Illuminate\Http\Request;
use Illuminate\Support\Str;
use Illuminate\Support\Facades\DB;

class SocietyController extends Controller
{
    // Membuat data society baru (Register)
    public function store(Request $request)
    {
        $society = Society::create([
            'id_card_number' => $request->id_card_number,
            'password' => $request->password,
            'name' => $request->name,
            'born_date' => $request->born_date,
            'gender' => $request->gender,
            'address' => $request->address,
            'regional_id' => $request->regional_id,
            'login_tokens' => $request->login_tokens,
        ]);

        return response()->json([
            'message' => 'Society berhasil dibuat',
            'data' => $society
        ], 201);
    }

    // Login
    public function login(Request $request)
    {
        $society = Society::where('id_card_number', $request->idnumber)->first();

        if (!$society || $request->password !== $society->password) {
            return response()->json([
                'error' => 'ID Card Number atau Password salah'
            ], 401);
        }

        // Generate token manual
        $token = $society->createToken('token')->plainTextToken;

        // Update login_tokens di database
        $society->update([
            'login_tokens' => $token
        ]);

        return response()->json([
            'message' => 'Login berhasil',
            'token' => $token,
            'society' => [
                'id' => $society->id,
                'name' => $society->name,
                'id_card_number' => $society->id_card_number
            ]
        ]);
    }
}
