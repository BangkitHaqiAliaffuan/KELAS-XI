<?php

namespace App\Filament\Widgets;

use App\Models\Transaction;
use App\Models\User;
use Filament\Support\Icons\Heroicon;
use Filament\Widgets\StatsOverviewWidget;
use Filament\Widgets\StatsOverviewWidget\Stat;
use Illuminate\Support\Carbon;

class AdminOverview extends StatsOverviewWidget
{
    protected function getStats(): array
    {
        $today = Carbon::today();
        $monthStart = Carbon::now()->startOfMonth();
        $monthEnd = Carbon::now()->endOfMonth();

        $totalUsers = User::query()->count();
        $newUsersToday = User::query()->whereDate('created_at', $today)->count();

        $totalTransactions = Transaction::query()->count();
        $transactionsToday = Transaction::query()->whereDate('transaction_date', $today)->count();

        $incomeMonth = (float) Transaction::query()
            ->where('type', 'income')
            ->whereBetween('transaction_date', [$monthStart, $monthEnd])
            ->sum('amount');

        $expenseMonth = (float) Transaction::query()
            ->where('type', 'expense')
            ->whereBetween('transaction_date', [$monthStart, $monthEnd])
            ->sum('amount');

        $netCashflowMonth = $incomeMonth - $expenseMonth;

        return [
            Stat::make('Total Users', number_format($totalUsers))
                ->description("+{$newUsersToday} user hari ini")
                ->descriptionIcon(Heroicon::OutlinedUsers)
                ->color('primary'),

            Stat::make('Total Transactions', number_format($totalTransactions))
                ->description("{$transactionsToday} transaksi hari ini")
                ->descriptionIcon(Heroicon::OutlinedClipboardDocumentList)
                ->color('info'),

            Stat::make('Income Bulan Ini', 'Rp ' . number_format($incomeMonth, 0, ',', '.'))
                ->descriptionIcon(Heroicon::OutlinedArrowTrendingUp)
                ->color('success'),

            Stat::make('Expense Bulan Ini', 'Rp ' . number_format($expenseMonth, 0, ',', '.'))
                ->descriptionIcon(Heroicon::OutlinedArrowTrendingDown)
                ->color('danger'),

            Stat::make('Net Cashflow', 'Rp ' . number_format($netCashflowMonth, 0, ',', '.'))
                ->description('Income - Expense (bulan ini)')
                ->descriptionIcon(Heroicon::OutlinedScale)
                ->color($netCashflowMonth >= 0 ? 'success' : 'danger'),
        ];
    }
}
