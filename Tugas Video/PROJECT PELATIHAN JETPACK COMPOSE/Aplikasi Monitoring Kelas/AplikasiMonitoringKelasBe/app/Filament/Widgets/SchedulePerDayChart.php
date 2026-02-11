<?php

namespace App\Filament\Widgets;

use App\Models\Schedule;
use Filament\Widgets\ChartWidget;

class SchedulePerDayChart extends ChartWidget
{
    protected static ?int $sort = 4;

    public function getHeading(): string
    {
        return '📅 Jadwal Per Hari';
    }

    protected function getData(): array
    {
        $days = ['Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu'];
        $counts = [];

        foreach ($days as $day) {
            $counts[] = Schedule::where('hari', $day)->count();
        }

        return [
            'datasets' => [
                [
                    'label' => 'Jumlah Jadwal',
                    'data' => $counts,
                    'backgroundColor' => [
                        'rgba(59, 130, 246, 0.8)',  // Blue - Senin
                        'rgba(34, 197, 94, 0.8)',   // Green - Selasa
                        'rgba(251, 191, 36, 0.8)', // Yellow - Rabu
                        'rgba(239, 68, 68, 0.8)',   // Red - Kamis
                        'rgba(139, 92, 246, 0.8)', // Purple - Jumat
                        'rgba(107, 114, 128, 0.8)', // Gray - Sabtu
                    ],
                    'borderColor' => [
                        'rgba(59, 130, 246, 1)',
                        'rgba(34, 197, 94, 1)',
                        'rgba(251, 191, 36, 1)',
                        'rgba(239, 68, 68, 1)',
                        'rgba(139, 92, 246, 1)',
                        'rgba(107, 114, 128, 1)',
                    ],
                    'borderWidth' => 2,
                    'borderRadius' => 8,
                ],
            ],
            'labels' => ['🟦 Senin', '🟩 Selasa', '🟨 Rabu', '🟥 Kamis', '🟪 Jumat', '⬜ Sabtu'],
        ];
    }

    protected function getType(): string
    {
        return 'bar';
    }

    protected function getOptions(): array
    {
        return [
            'plugins' => [
                'legend' => [
                    'display' => false,
                ],
            ],
            'scales' => [
                'y' => [
                    'beginAtZero' => true,
                    'ticks' => [
                        'stepSize' => 1,
                    ],
                ],
            ],
        ];
    }
}
