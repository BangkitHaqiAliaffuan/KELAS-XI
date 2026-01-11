<?php

namespace App\Http\Controllers;

use App\Models\Player;
use Auth;
use Illuminate\Http\Request;

class PlayerController extends Controller
{
    public function getAllPlayers()
    {
        $players = Player::get()->all();

        return response()->json([
            'message' => 'Get PLayer Berhasil',
            'Players' => $players
        ]);
    }

    public function createNewPlayer(Request $request)
    {

        $request->validate([
            'posisi' => 'required',
            'name' => 'required',
            'nomor_punggung' => 'required',
        ]);

        $user = $request->user();
        $userid = $user->id;

        $player = Player::create([
            'posisi' => $request->posisi,
            'name' => $request->name,
            'nomor_punggung' => $request->nomor_punggung,
            'createdBy' => $userid,
            'modifiedBy' => $userid
        ]);

        return response()->json([
            'message' => 'Create New PLayer Berhasil',
            'Players' => $player
        ]);
    }


    public function getPlayer($id)
    {
        $player = Player::find($id);

        if (!$player) {
            return response()->json([
                'message' => 'Get Player Gagal',
            ]);
        }

        return response()->json([
            'message' => 'Get Player Berhasil',
            'player' => $player
        ]);
    }

    public function updatePlayer(Request $request, $id)
    {
        $user = $request->user();
        $player = Player::find($id);

        if (!$player) {
            return response()->json([
                'message' => 'Get Player Gagal',
            ]);
        }

        $player->update([
            'posisi' => $request->posisi,
            'name' => $request->name,
            'nomer_punggung' => $request->posisi,
            'modifiedBy' => $user->id,
        ]);

        return response()->json([
            'message' => 'Update Berhasil',
            'player' => $player,
        ]);


    }


    public function deletePlayer($id){
        $player = Player::find($id);

        $player->delete();

        return response()->json([
            'message' => 'Delete Berhasil'
        ]);
    }
}
