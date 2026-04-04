<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Carbon;

class ReportController extends Controller
{
    public function overview(Request $request): JsonResponse
    {
        $payload = $request->validate([
            'period' => ['nullable', 'in:monthly,quarterly,yearly'],
        ]);

        $period = $payload['period'] ?? 'monthly';
        [$start, $end, $prevStart, $prevEnd] = $this->resolvePeriodRange($period);

        $user = $request->user();

        $income = (float) $user->transactions()
            ->where('type', 'income')
            ->whereBetween('transaction_date', [$start, $end])
            ->sum('amount');

        $expense = (float) $user->transactions()
            ->where('type', 'expense')
            ->whereBetween('transaction_date', [$start, $end])
            ->sum('amount');

        $previousExpense = (float) $user->transactions()
            ->where('type', 'expense')
            ->whereBetween('transaction_date', [$prevStart, $prevEnd])
            ->sum('amount');

        $expenseChange = $previousExpense > 0
            ? round((($expense - $previousExpense) / $previousExpense) * 100, 1)
            : 0;

        $topCategories = $user->transactions()
            ->where('type', 'expense')
            ->whereBetween('transaction_date', [$start, $end])
            ->selectRaw('category, SUM(amount) as total')
            ->groupBy('category')
            ->orderByDesc('total')
            ->limit(8)
            ->get()
            ->map(function ($item) use ($expense) {
                $percent = $expense > 0 ? round(((float) $item->total / $expense) * 100, 1) : 0;

                return [
                    'category' => $item->category,
                    'amount' => (float) $item->total,
                    'percent' => $percent,
                ];
            })
            ->values();

        $timeline = $user->transactions()
            ->where('type', 'expense')
            ->whereBetween('transaction_date', [Carbon::now()->subMonths(5)->startOfMonth(), Carbon::now()->endOfMonth()])
            ->selectRaw("DATE_FORMAT(transaction_date, '%Y-%m') as month_key, SUM(amount) as total")
            ->groupBy('month_key')
            ->orderBy('month_key')
            ->get()
            ->map(fn ($item) => [
                'month_key' => $item->month_key,
                'amount' => (float) $item->total,
            ]);

        return response()->json([
            'report' => [
                'period' => $period,
                'start_date' => $start->toDateString(),
                'end_date' => $end->toDateString(),
                'monthly_expenses' => $expense,
                'expense_change_percent' => $expenseChange,
                'income' => $income,
                'expenses' => $expense,
                'savings' => $income - $expense,
                'net_cash_flow' => $income - $expense,
                'top_categories' => $topCategories,
                'timeline' => $timeline,
            ],
        ]);
    }

    private function resolvePeriodRange(string $period): array
    {
        $now = Carbon::now();

        return match ($period) {
            'yearly' => [
                $now->copy()->startOfYear(),
                $now->copy()->endOfYear(),
                $now->copy()->subYear()->startOfYear(),
                $now->copy()->subYear()->endOfYear(),
            ],
            'quarterly' => [
                $now->copy()->firstOfQuarter(),
                $now->copy()->lastOfQuarter(),
                $now->copy()->subQuarter()->firstOfQuarter(),
                $now->copy()->subQuarter()->lastOfQuarter(),
            ],
            default => [
                $now->copy()->startOfMonth(),
                $now->copy()->endOfMonth(),
                $now->copy()->subMonthNoOverflow()->startOfMonth(),
                $now->copy()->subMonthNoOverflow()->endOfMonth(),
            ],
        };
    }
}
