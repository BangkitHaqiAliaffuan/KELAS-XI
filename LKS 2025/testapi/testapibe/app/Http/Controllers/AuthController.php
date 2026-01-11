<?php

namespace App\Http\Controllers;

use Auth;
use Illuminate\Http\Request;

class AuthController extends Controller
{

    public function login (Request $request){
        $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        $credetentials = $request->only('email', 'password');

        if(!Auth::attempt($credetentials)){
            return response()->json([
                'message' => 'Email Atau Password Salah'
            ]);
        }

        $user = Auth::user();
        $createdToken = $user->createToken('token')->plainTextToken ;

        return response()->json([
            'message' => 'Login Berhasil',
            'user' => $user,
            'token' => $createdToken
        ]);
    }

}
