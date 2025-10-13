<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\PointsHistory;
use Illuminate\Http\Request;

class PointsController extends Controller
{
    /**
     * Get user's current points balance
     */
    public function balance(Request $request)
    {
        $user = $request->user();
        
        return response()->json([
            'points' => $user->points,
            'user' => $user
        ]);
    }

    /**
     * Get user's points transaction history
     */
    public function history(Request $request)
    {
        $pointsHistory = PointsHistory::where('user_id', $request->user()->id)
            ->orderBy('created_at', 'desc')
            ->paginate(15);

        return response()->json([
            'data' => $pointsHistory
        ]);
    }
}
