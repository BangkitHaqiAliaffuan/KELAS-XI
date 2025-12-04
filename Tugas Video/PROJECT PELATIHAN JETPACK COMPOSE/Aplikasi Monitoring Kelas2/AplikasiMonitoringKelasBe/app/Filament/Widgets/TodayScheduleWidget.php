<?php

namespace App\Filament\Widgets;

use App\Models\Schedule;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Table;
use Filament\Widgets\TableWidget as BaseWidget;

class TodayScheduleWidget extends BaseWidget
{
    protected static ?int $sort = 2;

    protected int|string|array $columnSpan = 'full';

    public function getHeading(): string
    {
        return '📅 Jadwal Hari Ini';
    }

    public function table(Table $table): Table
    {
        $today = now()->locale('id')->isoFormat('dddd');

        return $table
            ->query(
                Schedule::query()
                    ->where('hari', ucfirst($today))
                    ->orderBy('jam_mulai')
            )
            ->columns([
                TextColumn::make('kelas')
                    ->label('Kelas')
                    ->icon('heroicon-o-building-library')
                    ->weight('bold')
                    ->color('primary'),
                TextColumn::make('mata_pelajaran')
                    ->label('Mata Pelajaran')
                    ->badge()
                    ->color('info'),
                TextColumn::make('guru.name')
                    ->label('Guru')
                    ->icon('heroicon-o-user')
                    ->color('success'),
                TextColumn::make('jam_mulai')
                    ->label('Mulai')
                    ->time('H:i')
                    ->badge()
                    ->color('success'),
                TextColumn::make('jam_selesai')
                    ->label('Selesai')
                    ->time('H:i')
                    ->badge()
                    ->color('danger'),
                TextColumn::make('ruang')
                    ->label('Ruang')
                    ->icon('heroicon-o-map-pin')
                    ->placeholder('—'),
            ])
            ->striped()
            ->emptyStateHeading('Tidak Ada Jadwal')
            ->emptyStateDescription('Tidak ada jadwal untuk hari ini.')
            ->emptyStateIcon('heroicon-o-calendar');
    }
}
