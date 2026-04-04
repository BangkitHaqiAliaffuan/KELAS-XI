<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Carbon;

class DashboardController extends Controller
{
    public function summary(Request $request): JsonResponse
    {
        $user = $request->user();
        $monthStart = Carbon::now()->startOfMonth();
        $monthEnd = Carbon::now()->endOfMonth();

        $totalIncome = (float) $user->transactions()->where('type', 'income')->sum('amount');
        $totalExpense = (float) $user->transactions()->where('type', 'expense')->sum('amount');
        $currentBalance = $totalIncome - $totalExpense;

        $monthIncome = (float) $user->transactions()
            ->where('type', 'income')
            ->whereBetween('transaction_date', [$monthStart, $monthEnd])
            ->sum('amount');

        $monthExpense = (float) $user->transactions()
            ->where('type', 'expense')
            ->whereBetween('transaction_date', [$monthStart, $monthEnd])
            ->sum('amount');

        $topCategories = $user->transactions()
            ->where('type', 'expense')
            ->whereBetween('transaction_date', [$monthStart, $monthEnd])
            ->selectRaw('category, SUM(amount) as total')
            ->groupBy('category')
            ->orderByDesc('total')
            ->limit(4)
            ->get()
            ->map(function ($item) use ($monthExpense) {
                $percent = $monthExpense > 0 ? round(((float) $item->total / $monthExpense) * 100, 1) : 0;

                return [
                    'category' => $item->category,
                    'amount' => (float) $item->total,
                    'percent' => $percent,
                ];
            })
            ->values();

        return response()->json([
            'summary' => [
                'current_balance' => $currentBalance,
                'month_income' => $monthIncome,
                'month_expense' => $monthExpense,
                'month_savings' => $monthIncome - $monthExpense,
                'chart_total' => $monthExpense,
                'top_categories' => $topCategories,
            ],
        ]);
    }
}
