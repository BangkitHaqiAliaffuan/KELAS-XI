<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\PointsHistory;
use App\Models\Transaction;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;

class RewardsController extends Controller
{
    /**
     * Get available rewards
     */
    public function index()
    {
        // Define available rewards
        $rewards = [
            [
                'id' => 1,
                'name' => 'Pulsa Rp 10,000',
                'description' => 'Voucher pulsa senilai Rp 10,000',
                'required_points' => 1000,
                'icon' => 'topup'
            ],
            [
                'id' => 2,
                'name' => 'Voucher Belanja Rp 25,000',
                'description' => 'Voucher belanja senilai Rp 25,000',
                'required_points' => 2000,
                'icon' => 'shopping'
            ],
            [
                'id' => 3,
                'name' => 'Cashback Rp 50,000',
                'description' => 'Cashback ke wallet senilai Rp 50,000',
                'required_points' => 5000,
                'icon' => 'wallet'
            ]
        ];

        return response()->json([
            'data' => $rewards
        ]);
    }

    /**
     * Redeem a reward
     */
    public function redeem(Request $request, $id)
    {
        $validator = Validator::make([
            'reward_id' => $id
        ], [
            'reward_id' => 'required|integer|in:1,2,3'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'message' => 'Invalid reward ID',
                'errors' => $validator->errors()
            ], 422);
        }

        // Define available rewards with their required points
        $availableRewards = [
            1 => ['name' => 'Pulsa Rp 10,000', 'required_points' => 1000],
            2 => ['name' => 'Voucher Belanja Rp 25,000', 'required_points' => 2000],
            3 => ['name' => 'Cashback Rp 50,000', 'required_points' => 5000],
        ];

        if (!isset($availableRewards[$id])) {
            return response()->json([
                'message' => 'Reward not found'
            ], 404);
        }

        $reward = $availableRewards[$id];
        $user = $request->user();

        if ($user->points < $reward['required_points']) {
            return response()->json([
                'message' => 'Insufficient points',
                'required_points' => $reward['required_points'],
                'user_points' => $user->points
            ], 400);
        }

        DB::beginTransaction();

        try {
            // Deduct points from user
            $user->decrement('points', $reward['required_points']);

            // Record points redemption in history
            PointsHistory::create([
                'user_id' => $user->id,
                'points' => -$reward['required_points'], // Negative for redemption
                'type' => 'redeemed',
                'description' => 'Redeemed for ' . $reward['name'],
                'reference_id' => $id,
            ]);

            // Record transaction
            Transaction::create([
                'user_id' => $user->id,
                'type' => 'points_redeem',
                'reference_id' => $id,
                'reference_type' => 'Reward',
                'amount' => 0, // No monetary value for points redemption
                'points_earned' => -$reward['required_points'],
                'description' => 'Redeemed for ' . $reward['name']
            ]);

            DB::commit();

            return response()->json([
                'message' => 'Reward redeemed successfully',
                'reward' => $reward,
                'remaining_points' => $user->points
            ]);
        } catch (\Exception $e) {
            DB::rollback();
            return response()->json([
                'message' => 'Failed to redeem reward',
                'error' => $e->getMessage()
            ], 500);
        }
    }
}
