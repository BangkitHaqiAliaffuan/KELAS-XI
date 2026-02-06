<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;

class AuthController extends Controller
{
    public function signup(Request $request){
        $validator = Validator::make($request->all(), [
            'username' => 'required|unique:users',
            'password' => 'required|min:6'
        ]);

        if($validator->fails()){
            return response()->json([
                'message' => $validator->errors()
            ]);
        }


        $user = User::create([
            'username'=> $request->username,
            'password' => Hash::make($request->password),
        ]);

        $token = $user->createToken('token')->plainTextToken;

        $user->update([
            'remember_token' => $token
        ]);

        return response()->json([
            'message' => 'done min',
            'data' => $user,
        ]);
    }

    public function signin(Request $request){
        $user = User::where('username', $request->username)->first();

        if($user && Hash::check($user->password, $request->password)){

            $token = $user->createToken('token')->plainTextToken;

            return response()->json([
                'message' => 'Success',
                'data' => $user,
                'token' => $token
            ]);
        }


    }
}
