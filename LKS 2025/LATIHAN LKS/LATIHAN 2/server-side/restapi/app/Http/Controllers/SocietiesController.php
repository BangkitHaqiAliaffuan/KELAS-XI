<?php

namespace App\Http\Controllers;

use App\Models\Societies;
use Illuminate\Http\Request;

class SocietiesController extends Controller
{
    /**
     * Display a listing of the resource.
     */
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
    public function show(Societies $societies)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Societies $societies)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Societies $societies)
    {
        //
    }

    public function login(Request $request)
    {
        $society = Societies::where('id_card_number', $request->idcard)->first();

        if (!$society) {
            return response([
                'message' => 'id card atau password salah'
            ]);
        }

        $token = $society->createToken('token')->plainTextToken;

        $society->update([
            'login_tokens' => $token
        ]);

        return response([
            'user' => $society,
            'token' => $token,
            'message' => 'mantap',
        ]);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Societies $societies)
    {
        //
    }
}
