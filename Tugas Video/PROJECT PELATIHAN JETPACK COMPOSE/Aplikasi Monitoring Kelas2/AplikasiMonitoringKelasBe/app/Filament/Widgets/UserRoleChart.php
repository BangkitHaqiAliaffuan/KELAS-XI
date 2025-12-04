<?php

namespace App\Filament\Widgets;

use App\Models\Teacher;
use App\Models\User;
use Filament\Widgets\ChartWidget;

class UserRoleChart extends ChartWidget
{
    protected static ?int $sort = 3;

    public function getHeading(): string
    {
        return '📊 Distribusi Pengguna';
    }

    protected function getData(): array
    {
        $roles = User::selectRaw('role, count(*) as count')
            ->groupBy('role')
            ->pluck('count', 'role')
            ->toArray();

        return [
            'datasets' => [
                [
                    'label' => 'Jumlah Pengguna',
                    'data' => array_values($roles),
                    'backgroundColor' => [
                        'rgba(99, 102, 241, 0.8)',   // Indigo - Admin
                        'rgba(20, 184, 166, 0.8)',  // Teal - Guru
                        'rgba(34, 197, 94, 0.8)',   // Green - Siswa
                        'rgba(251, 146, 60, 0.8)', // Orange - Kurikulum
                        'rgba(139, 92, 246, 0.8)',  // Purple - Kepala Sekolah
                    ],
                    'borderColor' => [
                        'rgba(99, 102, 241, 1)',
                        'rgba(20, 184, 166, 1)',
                        'rgba(34, 197, 94, 1)',
                        'rgba(251, 146, 60, 1)',
                        'rgba(139, 92, 246, 1)',
                    ],
                    'borderWidth' => 2,
                ],
            ],
            'labels' => array_map(function ($role) {
                return match ($role) {
                    'admin' => '🔑 Admin',
                    'guru' => '👨‍🏫 Guru',
                    'siswa' => '🎓 Siswa',
                    'kurikulum' => '📋 Kurikulum',
                    'kepala_sekolah' => '👔 Kepala Sekolah',
                    default => ucfirst($role),
                };
            }, array_keys($roles)),
        ];
    }

    protected function getType(): string
    {
        return 'doughnut';
    }

    protected function getOptions(): array
    {
        return [
            'plugins' => [
                'legend' => [
                    'position' => 'bottom',
                ],
            ],
        ];
    }
}
