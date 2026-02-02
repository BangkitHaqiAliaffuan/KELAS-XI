<?php

namespace App\Http\Controllers;

use App\Models\Society;
use Illuminate\Http\Request;

class SocietyController extends Controller
{
    /**
     * Display a listing of the resource.
     */

    public function login(Request $request)
    {
        $society = Society::where('id_card_number', $request->id_card_number)->first();

        if (!$society) {
            return response()->json([
                'message' => 'Password Atau Id salah'
            ]);
        }

        $token = $society->createToken('token')->plainTextToken;

        $society->update([
            'login_tokens' => $token
        ]);

        return response()->json([
            'message' => 'done',
            'data' => $society,
        ]);
    }

    public function logout(Request $request){
        $society = Society::where('login_tokens', $request->bearerToken())->first();

        if(!$society){
            return response()->json([
                'message' => 'user tidak ditemukan'
            ]);
        }

        $society->update(['login_tokens'=>null]);

        return response()->json([
            'message' => 'berhasil'
        ]);
    }

    public function index()
    {
        //
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        //
    }

    /**
     * Display the specified resource.
     */
    public function show(Society $society)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Society $society)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Society $society)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Society $society)
    {
        //
    }
}
