<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;

class AuthController extends Controller
{
    public function updateUser(Request $request)
    {

        $token = $request->bearerToken();

        $user = User::where('remember_token', $token)->first();

        if(!$user){
            return response()->json([
                'message' => 'unauthorized'
            ]);
        }

        $user->update([
            'name' => $request->name,
            // 'password' => $request->password,
            'email' => $request->email,
        ]);

        $user->save();

        return response()->json([
            'message' => 'Done',
            'data' => $user
        ]);
    }
    public function register(Request $request)
    {
        $validate = Validator::make($request->all(), [
            'name' => 'required|min:3',
            'email' => 'required',
            'password' => 'required',
        ]);

        if ($validate->fails()) {
            return response()->json([
                'status' => 'error',
                'error' => $validate->errors(),
            ]);
        }

        $user = User::create([
            'name' => $request->name,
            'password' => Hash::make($request->password),
            'email' => $request->email,
        ]);

        return response()->json([
            'status' => 'success',
            'error' => $user,
        ]);

    }

    public function login(Request $request)
    {
        $validate = Validator::make($request->all(), [
            // 'name' => 'required|min:3',
            'email' => 'required',
            'password' => 'required',
        ]);

        if ($validate->fails()) {
            return response()->json([
                'status' => 'error',
                'error' => $validate->errors(),
            ]);
        }

        $user = User::where('email', $request->email)->first();

        if (!$user) {
            return response()->json([
                'status' => 'error',
                'error' => 'user not found',
            ]);
        }

        if ($user && Hash::check($request->password, $user->password)) {

            $token = $user->createToken('token')->plainTextToken;
            $user->update([
                'remember_token' => $token
            ]);

            return response()->json([
                'status' => 'success',
                'error' => 'Login Success',
                'token' => $token
            ]);
        }

    }
}
