<?php

namespace App\Http\Controllers;

use App\Models\Admin;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;

class AuthController extends Controller
{
    public function signup(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'username' => 'required|unique:users',
            'password' => 'required|min:6'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => $validator->errors()
            ]);
        }


        $user = User::create([
            'username' => $request->username,
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

    public function signin(Request $request)
    {

        $validator = Validator::make($request->all(), [
            'username' => 'required',
            'password' => 'required|min:6'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => $validator->errors()
            ]);
        }

        $admin = Admin::where('username', $request->username)->first();
        if ($admin && Hash::check($request->password, $admin->password)) {

            $token = $admin->createToken('token')->plainTextToken;

            $admin->update([
                'api_token' => $token
            ]);

            return response()->json([
                'message' => 'Success',
                'data' => $admin,
                'token' => $token,
                'role' => 'admin'
            ]);
        }


        $user = User::where('username', $request->username)->first();

        if ($user && Hash::check($request->password, $user->password)) {

            $token = $user->createToken('token')->plainTextToken;

            $user->update([
                'remember_token' => $token
            ]);

            return response()->json([
                'message' => 'Success',
                'data' => $user,
                'token' => $token,
                'role' => $user->role
            ]);
        } else {
            return response()->json([
                'message' => 'salah',
                // 'tes' => $request->username,
            ]);

        }
    }

    public function signout(Request $request)
    {
        $token = $request->bearerToken();
        $user = User::where('remember_token', $token);

        $user->select('api_token')->delete();


        return response()->json([
            'message' => 'done min'

        ]);
    }
}
