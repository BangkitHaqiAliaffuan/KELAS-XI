<?php

namespace App\Filament\Widgets;

use App\Models\Kelas;
use App\Models\Schedule;
use App\Models\Subject;
use App\Models\Teacher;
use App\Models\User;
use Filament\Widgets\StatsOverviewWidget as BaseWidget;
use Filament\Widgets\StatsOverviewWidget\Stat;

class StatsOverviewWidget extends BaseWidget
{
    protected static ?int $sort = 1;

    protected function getStats(): array
    {
        return [
            Stat::make('Total Pengguna', User::count())
                ->description('Semua pengguna terdaftar')
                ->descriptionIcon('heroicon-o-users')
                ->chart([7, 3, 4, 5, 6, 3, 5, 8])
                ->color('primary')
                ->icon('heroicon-o-users'),
            Stat::make('Total Guru', Teacher::count())
                ->description('Guru aktif mengajar')
                ->descriptionIcon('heroicon-o-academic-cap')
                ->chart([3, 5, 7, 6, 8, 4, 6, 9])
                ->color('success')
                ->icon('heroicon-o-academic-cap'),
            Stat::make('Total Kelas', Kelas::count())
                ->description('Kelas tersedia')
                ->descriptionIcon('heroicon-o-building-library')
                ->chart([4, 6, 5, 7, 4, 8, 6, 7])
                ->color('info')
                ->icon('heroicon-o-building-library'),
            Stat::make('Total Jadwal', Schedule::count())
                ->description('Jadwal pelajaran')
                ->descriptionIcon('heroicon-o-calendar-days')
                ->chart([5, 4, 6, 8, 7, 5, 6, 9])
                ->color('warning')
                ->icon('heroicon-o-calendar-days'),
        ];
    }
}
