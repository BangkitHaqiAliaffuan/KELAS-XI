<?php

namespace App\Filament\Resources\Users\Tables;

use Filament\Actions\BulkActionGroup;
use Filament\Actions\DeleteBulkAction;
use Filament\Actions\EditAction;
use Filament\Actions\ViewAction;
use Filament\Tables\Columns\IconColumn;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Filters\SelectFilter;
use Filament\Tables\Filters\TernaryFilter;
use Filament\Tables\Table;

class UsersTable
{
    public static function configure(Table $table): Table
    {
        return $table
            ->columns([
                TextColumn::make('name')
                    ->label('Nama')
                    ->searchable()
                    ->sortable()
                    ->icon('heroicon-o-user')
                    ->weight('bold')
                    ->color('primary'),
                TextColumn::make('email')
                    ->label('Email')
                    ->searchable()
                    ->icon('heroicon-o-envelope')
                    ->copyable()
                    ->copyMessage('Email disalin!')
                    ->color('gray'),
                TextColumn::make('role')
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
                TextColumn::make('kelas.nama_kelas')
                    ->label('Kelas')
                    ->sortable()
                    ->searchable()
                    ->badge()
                    ->color('info')
                    ->icon('heroicon-o-building-library')
                    ->placeholder('—'),
                TextColumn::make('mata_pelajaran')
                    ->label('Mapel')
                    ->searchable()
                    ->badge()
                    ->color('success')
                    ->icon('heroicon-o-book-open')
                    ->placeholder('—'),
                IconColumn::make('is_banned')
                    ->label('Status')
                    ->boolean()
                    ->trueIcon('heroicon-o-x-circle')
                    ->falseIcon('heroicon-o-check-circle')
                    ->trueColor('danger')
                    ->falseColor('success'),
                TextColumn::make('email_verified_at')
                    ->label('Verifikasi')
                    ->dateTime('d M Y')
                    ->sortable()
                    ->icon(fn ($state) => $state ? 'heroicon-o-check-badge' : 'heroicon-o-clock')
                    ->color(fn ($state) => $state ? 'success' : 'warning')
                    ->placeholder('Belum'),
                TextColumn::make('created_at')
                    ->label('Dibuat')
                    ->dateTime('d M Y')
                    ->sortable()
                    ->toggleable(isToggledHiddenByDefault: true),
                TextColumn::make('updated_at')
                    ->label('Diperbarui')
                    ->dateTime('d M Y')
                    ->sortable()
                    ->toggleable(isToggledHiddenByDefault: true),
            ])
            ->filters([
                SelectFilter::make('role')
                    ->label('Peran')
                    ->options([
                        'admin' => '🔑 Admin',
                        'guru' => '👨‍🏫 Guru',
                        'siswa' => '🎓 Siswa',
                        'kurikulum' => '📋 Kurikulum',
                        'kepala_sekolah' => '👔 Kepala Sekolah'
                    ])
                    ->indicator('Peran'),
                SelectFilter::make('class_id')
                    ->label('Kelas')
                    ->relationship('kelas', 'nama_kelas')
                    ->searchable()
                    ->preload()
                    ->indicator('Kelas'),
                TernaryFilter::make('is_banned')
                    ->label('Status Akun')
                    ->placeholder('Semua')
                    ->trueLabel('Diblokir')
                    ->falseLabel('Aktif'),
                TernaryFilter::make('email_verified_at')
                    ->label('Verifikasi Email')
                    ->placeholder('Semua')
                    ->trueLabel('Terverifikasi')
                    ->falseLabel('Belum')
                    ->nullable(),
            ])
            ->actions([
                ViewAction::make()
                    ->color('info')
                    ->icon('heroicon-o-eye'),
                EditAction::make()
                    ->color('warning')
                    ->icon('heroicon-o-pencil-square'),
            ])
            ->bulkActions([
                BulkActionGroup::make([
                    DeleteBulkAction::make()
                        ->icon('heroicon-o-trash'),
                ]),
            ])
            ->striped()
            ->defaultSort('created_at', 'desc')
            ->emptyStateHeading('Belum Ada Pengguna')
            ->emptyStateDescription('Tambahkan pengguna baru untuk memulai.')
            ->emptyStateIcon('heroicon-o-users');
    }
}
