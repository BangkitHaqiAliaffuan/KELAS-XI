<?php

namespace App\Filament\Resources\Users\Schemas;

use Filament\Infolists\Components\IconEntry;
use Filament\Infolists\Components\TextEntry;
use Filament\Schemas\Components\Grid;
use Filament\Schemas\Components\Section;
use Filament\Schemas\Components\Tabs;
use Filament\Schemas\Schema;

class UserInfolist
{
    public static function configure(Schema $schema): Schema
    {
        return $schema
            ->components([
                Tabs::make('Tabs')
                    ->tabs([
                        Tabs\Tab::make('👤 Informasi Pribadi')
                            ->icon('heroicon-o-user')
                            ->schema([
                                Section::make('Data Pengguna')
                                    ->icon('heroicon-o-identification')
                                    ->schema([
                                        Grid::make(2)
                                            ->schema([
                                                TextEntry::make('name')
                                                    ->label('Nama Lengkap')
                                                    ->icon('heroicon-o-user')
                                                    ->weight('bold')
                                                    ->color('primary'),
                                                TextEntry::make('email')
                                                    ->label('Alamat Email')
                                                    ->icon('heroicon-o-envelope')
                                                    ->copyable()
                                                    ->copyMessage('Email disalin!'),
                                            ]),
                                    ]),
                                Section::make('Peran & Kelas')
                                    ->icon('heroicon-o-user-group')
                                    ->schema([
                                        Grid::make(3)
                                            ->schema([
                                                TextEntry::make('role')
                                                    ->label('Peran')
                                                    ->badge()
                                                    ->color(fn (string $state): string => match ($state) {
                                                        'admin' => 'danger',
                                                        'guru' => 'info',
                                                        'siswa' => 'success',
                                                        'kurikulum' => 'warning',
                                                        'kepala_sekolah' => 'primary',
                                                        default => 'gray',
                                                    })
                                                    ->formatStateUsing(fn (string $state): string => match ($state) {
                                                        'admin' => '🔑 Admin',
                                                        'guru' => '👨‍🏫 Guru',
                                                        'siswa' => '🎓 Siswa',
                                                        'kurikulum' => '📋 Kurikulum',
                                                        'kepala_sekolah' => '👔 Kepala Sekolah',
                                                        default => $state,
                                                    }),
                                                TextEntry::make('kelas.nama_kelas')
                                                    ->label('Kelas')
                                                    ->icon('heroicon-o-building-library')
                                                    ->badge()
                                                    ->color('info')
                                                    ->placeholder('—'),
                                                TextEntry::make('mata_pelajaran')
                                                    ->label('Mata Pelajaran')
                                                    ->icon('heroicon-o-book-open')
                                                    ->badge()
                                                    ->color('success')
                                                    ->placeholder('—'),
                                            ]),
                                    ]),
                            ]),
                        Tabs\Tab::make('🔐 Keamanan')
                            ->icon('heroicon-o-lock-closed')
                            ->schema([
                                Section::make('Status Akun')
                                    ->icon('heroicon-o-shield-check')
                                    ->schema([
                                        Grid::make(2)
                                            ->schema([
                                                IconEntry::make('is_banned')
                                                    ->label('Status Blokir')
                                                    ->boolean()
                                                    ->trueIcon('heroicon-o-x-circle')
                                                    ->falseIcon('heroicon-o-check-circle')
                                                    ->trueColor('danger')
                                                    ->falseColor('success'),
                                                TextEntry::make('email_verified_at')
                                                    ->label('Email Terverifikasi')
                                                    ->dateTime('d M Y, H:i')
                                                    ->icon(fn ($state) => $state ? 'heroicon-o-check-badge' : 'heroicon-o-clock')
                                                    ->color(fn ($state) => $state ? 'success' : 'warning')
                                                    ->placeholder('Belum Terverifikasi'),
                                            ]),
                                    ]),
                                Section::make('Riwayat')
                                    ->icon('heroicon-o-clock')
                                    ->schema([
                                        Grid::make(2)
                                            ->schema([
                                                TextEntry::make('created_at')
                                                    ->label('Dibuat Pada')
                                                    ->dateTime('d M Y, H:i')
                                                    ->icon('heroicon-o-calendar'),
                                                TextEntry::make('updated_at')
                                                    ->label('Diperbarui Pada')
                                                    ->dateTime('d M Y, H:i')
                                                    ->icon('heroicon-o-arrow-path'),
                                            ]),
                                    ]),
                            ]),
                    ])->columnSpanFull(),
            ]);
    }
}
