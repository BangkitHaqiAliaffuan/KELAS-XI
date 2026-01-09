<?php

namespace App\Http\Controllers;

use App\Models\Player;
use Auth;
use Illuminate\Http\Request;

class PlayerController extends Controller
{
    public function getAllPlayers(){
        $players = Player::get()->all();

        return response()->json([
            'message' => 'Get PLayer Berhasil',
            'Players' => $players
        ]);
    }

    public function createNewPlayer(Request $request){
        
        $request->validate([
            'posisi' => 'required',
            'name' => 'required',
            'nomor_punggung' => 'required',
        ]);




        $player = Player::create([
            'posisi' => $request->posisi,
            'name' => $request->name,
            'nomor_punggung' => $request->nomor_punggung,
            'createdBy' => $user->id,
        ]);
        if(!$player | !$user){

            return response()->json([
                'message' => 'Create new player reay',

            ]);
        }

        return response()->json([
            'message' => 'Create New PLayer Berhasil',
            'Players' => $player
        ]);
    }
}
