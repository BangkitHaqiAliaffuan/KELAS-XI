<?php

namespace App\Http\Controllers;

use App\Models\Society;
use Illuminate\Http\Request;

class SocietyController extends Controller
{
    public function login(Request $request)
    {
        $society = Society::where('id_card_number', $request->idnumber)->first();

        if (!$society || $society->password !== $request->password) {
            return response()->json([
                'error' => 'tra bisa'
            ]);
        }

        $token = $society->createToken('token')->plainTextToken;

        if ($society->password == $request->password) {
            $society->update([
                'login_tokens' => $token
            ]);
            return response()->json([
                'test' => $society
            ]);
        }

    }

}
