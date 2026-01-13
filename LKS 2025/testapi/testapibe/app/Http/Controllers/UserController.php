<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;

class UserController extends Controller
{
    public function createUser(Request $request){

        $request->validate([
            'name' => 'required',
            'password' => 'required',
            'email' => 'required|email',
        ]);

        $user = User::create([
            'email' => $request->email,
            'password' => $request->password,
            'name' => $request->name,
        ]);

        if(!$user){
            return response()->json([
                'message' => 'Register Gagal',
            ]);
        }

        return response()->json([
            'message' => 'Register Berhasil',
            'user' => $user
        ]);
    }


    public function getAllUser(){

        $user = User::get()->all();

        if(!$user){
            return response()->json([
                'message' => 'Get All User Gagal',
            ]);
        }

        return response()->json([
            'message' => 'Get All User Berhasil',
            'user' => $user
        ]);

    }

    public function getUser($id){
        $user = User::find($id);

        if(!$user){
            return response()->json([
                'message' => 'User Tidak Ditemukan',
            ]);
        }

        return response()->json([
            'message' => 'User Berhasil Ditemukan',
            'user' => $user
        ]);
    }
    public function updateUser(Request $request,$id){
        $user = User::find($id);

        if(!$user){
            return response()->json([
                'message' => 'User Tidak Ditemukan',
            ]);
        }

        $user->update([
            'name' => $request->name,
            'email' => $request->email,
        ]);

        return response()->json([
            'message' => 'User Berhasil Ditemukan',
            'user' => $user
        ]);
    }

    public function deleteUser($id){
        $user = User::find($id);

        $user->delete();

        return response()->json([
            'message' => 'Berhasil Dihapus'
        ]);
    }

    


}
