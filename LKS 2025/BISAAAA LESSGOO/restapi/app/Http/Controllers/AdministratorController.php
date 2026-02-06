<?php

namespace App\Http\Controllers;

use App\Models\Administrator;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;

class AdministratorController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $user = Administrator::get()->all();
        return response()->json([
            'data' => $user
        ]);
    }

    public function showUser()
    {
        $user = User::get()->all();
        return response()->json([
            'data' => $user
        ]);
    }

    public function signin(Request $request)
    {
        $user = Administrator::where('username', $request->username)->first();

        if ($user && Hash::check($request->password, $user->password)) {

            $token = $user->createToken('token')->plainTextToken;

            $user->update([
                'api_token' => $token
            ]);

            return response()->json([
                'message' => 'Success',
                'data' => $user,
                'token' => $token
            ]);
        }
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create(Request $request)
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

        return response()->json([
            'data' => $user,
            'message' => 'done min'
        ]);
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
    public function show(Administrator $administrator)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Administrator $administrator)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Administrator $administrator, $id)
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

        $user = User::find($id);

        if (!$user) {
            return response()->json([
                'message' => 'user tidak ada'
            ]);
        }

        $user->update([
            'username' => $request->username,
            'password' => $request->password,
        ]);

        return response()->json([
            'message' => 'done min',
            'data' => $user
        ]);

    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Administrator $administrator, $id)
    {
        $user = User::find($id);
        if (!$user) {
            return response()->json([
                'message' => 'tra da usernya',
                // 'data' => $user
            ]);
        }
        $user->games()->delete();
        $user->delete();

        return response()->json([
            'message' => 'done min',
            // 'data' => $user
        ]);
    }
}
