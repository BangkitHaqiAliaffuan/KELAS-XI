<?php

namespace App\Http\Controllers;

use App\Models\Barang;
use App\Models\Order;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class BarangController extends Controller
{
    public function createBarang(Request $request)
    {
        $validate = Validator::make($request->all(), [
            'nama' => 'required|min:3',
            'jumlah' => 'required',
            'harga' => 'required',
        ]);


        if ($validate->fails()) {
            return response()->json([
                'status' => 'error',
                'error' => $validate->errors(),
            ]);
        }

        $barang = Barang::create([
            'nama' => $request->nama,
            'jumlah' => $request->jumlah,
            'harga' => $request->harga,
        ]);

        return response()->json([
            'status' => 'success',
            'dataa' => $barang,
        ]);
    }

    public function createOrder(Request $request)
    {
        $token = $request->bearerToken();

        $user = User::where('remember_token', $token)->first();
        $barang = $request->barang_id;

        $order = Order::create([
            'user_id' => $user->id,
            'barang_id' => $barang,
        ]);

        return response()->json([
            'status' => 'success',
            'data' => $order,
        ]);
    }
}
