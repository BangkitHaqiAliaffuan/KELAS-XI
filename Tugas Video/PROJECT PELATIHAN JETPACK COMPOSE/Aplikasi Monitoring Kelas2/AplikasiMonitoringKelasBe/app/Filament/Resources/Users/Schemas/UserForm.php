<?php

namespace App\Filament\Resources\Users\Schemas;

use App\Models\Kelas;
use Filament\Forms\Components\DateTimePicker;
use Filament\Forms\Components\Select;
use Filament\Forms\Components\TextInput;
use Filament\Forms\Components\Toggle;
use Filament\Schemas\Components\Grid;
use Filament\Schemas\Components\Section;
use Filament\Schemas\Components\Tabs;
use Filament\Schemas\Schema;

class UserForm
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
                                    ->description('Informasi dasar pengguna')
                                    ->icon('heroicon-o-identification')
                                    ->collapsible()
                                    ->schema([
                                        Grid::make(2)
                                            ->schema([
                                                TextInput::make('name')
                                                    ->label('Nama Lengkap')
                                                    ->required()
                                                    ->prefixIcon('heroicon-o-user')
                                                    ->placeholder('Masukkan nama lengkap'),
                                                TextInput::make('email')
                                                    ->label('Alamat Email')
                                                    ->email()
                                                    ->required()
                                                    ->prefixIcon('heroicon-o-envelope')
                                                    ->placeholder('email@example.com'),
                                            ]),
                                    ]),
                                Section::make('Peran & Kelas')
                                    ->description('Tentukan peran dan kelas pengguna')
                                    ->icon('heroicon-o-user-group')
                                    ->collapsible()
                                    ->schema([
                                        Grid::make(2)
                                            ->schema([
                                                Select::make('role')
                                                    ->label('Peran')
                                                    ->options([
                                                        'admin' => '🔑 Admin',
                                                        'guru' => '👨‍🏫 Guru',
                                                        'siswa' => '🎓 Siswa',
                                                        'kurikulum' => '📋 Kurikulum',
                                                        'kepala_sekolah' => '👔 Kepala Sekolah'
                                                    ])
                                                    ->default('siswa')
                                                    ->required()
                                                    ->native(false)
                                                    ->prefixIcon('heroicon-o-shield-check')
                                                    ->live(),
                                                Select::make('class_id')
                                                    ->label('Kelas')
                                                    ->relationship('kelas', 'nama_kelas')
                                                    ->placeholder('Pilih kelas (khusus siswa)')
                                                    ->visible(fn ($get) => $get('role') === 'siswa')
                                                    ->searchable()
                                                    ->preload()
                                                    ->prefixIcon('heroicon-o-building-library'),
                                            ]),
                                        TextInput::make('mata_pelajaran')
                                            ->label('Mata Pelajaran')
                                            ->visible(fn ($get) => $get('role') === 'guru')
                                            ->prefixIcon('heroicon-o-book-open')
                                            ->placeholder('Contoh: Matematika')
                                            ->default(null),
                                    ]),
                            ]),
                        Tabs\Tab::make('🔐 Keamanan')
                            ->icon('heroicon-o-lock-closed')
                            ->schema([
                                Section::make('Kredensial Login')
                                    ->description('Pengaturan password dan verifikasi')
                                    ->icon('heroicon-o-key')
                                    ->collapsible()
                                    ->schema([
                                        Grid::make(2)
                                            ->schema([
                                                TextInput::make('password')
                                                    ->label('Password')
                                                    ->password()
                                                    ->revealable()
                                                    ->required(fn (string $operation): bool => $operation === 'create')
                                                    ->dehydrated(fn ($state) => filled($state))
                                                    ->prefixIcon('heroicon-o-key')
                                                    ->placeholder('Minimal 8 karakter')
                                                    ->helperText('Kosongkan jika tidak ingin mengubah'),
                                                DateTimePicker::make('email_verified_at')
                                                    ->label('Email Terverifikasi Pada')
                                                    ->prefixIcon('heroicon-o-check-badge')
                                                    ->displayFormat('d M Y, H:i')
                                                    ->native(false),
                                            ]),
                                        Toggle::make('is_banned')
                                            ->label('Blokir Pengguna')
                                            ->default(false)
                                            ->inline(false)
                                            ->onColor('danger')
                                            ->offColor('success')
                                            ->helperText('Aktifkan untuk memblokir akses pengguna ini'),
                                    ]),
                            ]),
                    ])->columnSpanFull()
                    ->persistTabInQueryString(),
            ]);
    }
}
