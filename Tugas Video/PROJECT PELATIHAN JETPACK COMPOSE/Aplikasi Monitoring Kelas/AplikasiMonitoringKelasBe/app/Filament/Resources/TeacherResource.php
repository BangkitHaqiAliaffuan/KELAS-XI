<?php

namespace App\Filament\Resources;

use App\Filament\Resources\TeacherResource\Pages;
use App\Models\Teacher;
use Filament\Actions\BulkActionGroup;
use Filament\Actions\DeleteBulkAction;
use Filament\Actions\EditAction;
use Filament\Actions\ForceDeleteBulkAction;
use Filament\Actions\RestoreBulkAction;
use Filament\Actions\ViewAction;
use Filament\Forms\Components\DateTimePicker;
use Filament\Forms\Components\TextInput;
use Filament\Forms\Components\Toggle;
use Filament\Resources\Resource;
use Filament\Schemas\Components\Grid;
use Filament\Schemas\Components\Section;
use Filament\Schemas\Components\Tabs;
use Filament\Schemas\Schema;
use Filament\Tables\Columns\IconColumn;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Filters\TernaryFilter;
use Filament\Tables\Table;

class TeacherResource extends Resource
{
    protected static ?string $model = Teacher::class;

    protected static string|\BackedEnum|null $navigationIcon = 'heroicon-o-academic-cap';

    protected static ?string $navigationLabel = 'Guru';

    protected static ?string $modelLabel = 'Guru';

    protected static ?string $pluralModelLabel = 'Data Guru';

    protected static string|\UnitEnum|null $navigationGroup = '📁 Data Master';

    protected static ?int $navigationSort = 1;

    public static function form(Schema $schema): Schema
    {
        return $schema
            ->components([
                Tabs::make('Tabs')
                    ->tabs([
                        Tabs\Tab::make('👤 Informasi Utama')
                            ->icon('heroicon-o-user')
                            ->schema([
                                Section::make('Data Pribadi')
                                    ->description('Informasi dasar guru')
                                    ->icon('heroicon-o-identification')
                                    ->collapsible()
                                    ->schema([
                                        Grid::make(2)
                                            ->schema([
                                                TextInput::make('name')
                                                    ->label('Nama Lengkap')
                                                    ->required()
                                                    ->maxLength(255)
                                                    ->prefixIcon('heroicon-o-user')
                                                    ->placeholder('Masukkan nama lengkap guru')
                                                    ->autocomplete('name'),
                                                TextInput::make('email')
                                                    ->label('Email')
                                                    ->email()
                                                    ->required()
                                                    ->maxLength(255)
                                                    ->prefixIcon('heroicon-o-envelope')
                                                    ->placeholder('email@example.com')
                                                    ->autocomplete('email'),
                                            ]),
                                    ]),
                                Section::make('Informasi Mengajar')
                                    ->description('Detail mata pelajaran dan status')
                                    ->icon('heroicon-o-book-open')
                                    ->collapsible()
                                    ->schema([
                                        Grid::make(2)
                                            ->schema([
                                                TextInput::make('mata_pelajaran')
                                                    ->label('Mata Pelajaran')
                                                    ->maxLength(255)
                                                    ->prefixIcon('heroicon-o-book-open')
                                                    ->placeholder('Contoh: Matematika'),
                                                Toggle::make('is_banned')
                                                    ->label('Status Banned')
                                                    ->default(false)
                                                    ->inline(false)
                                                    ->onColor('danger')
                                                    ->offColor('success')
                                                    ->helperText('Aktifkan untuk memblokir akses guru ini'),
                                            ]),
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
                                                    ->maxLength(255)
                                                    ->prefixIcon('heroicon-o-key')
                                                    ->placeholder('Minimal 8 karakter')
                                                    ->helperText('Kosongkan jika tidak ingin mengubah password'),
                                                DateTimePicker::make('email_verified_at')
                                                    ->label('Email Terverifikasi Pada')
                                                    ->prefixIcon('heroicon-o-check-badge')
                                                    ->displayFormat('d M Y, H:i')
                                                    ->native(false),
                                            ]),
                                    ]),
                            ]),
                    ])->columnSpanFull()
                    ->persistTabInQueryString(),
            ]);
    }

    public static function table(Table $table): Table
    {
        return $table
            ->columns([
                TextColumn::make('name')
                    ->label('Nama Guru')
                    ->searchable()
                    ->sortable()
                    ->icon('heroicon-o-user')
                    ->weight('bold')
                    ->color('primary')
                    ->description(fn (Teacher $record): string => $record->mata_pelajaran ?? 'Belum ditentukan'),
                TextColumn::make('email')
                    ->label('Email')
                    ->searchable()
                    ->icon('heroicon-o-envelope')
                    ->copyable()
                    ->copyMessage('Email disalin!')
                    ->color('gray'),
                TextColumn::make('mata_pelajaran')
                    ->label('Mata Pelajaran')
                    ->searchable()
                    ->badge()
                    ->color('info')
                    ->icon('heroicon-o-book-open')
                    ->placeholder('Belum ditentukan'),
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
                    ->placeholder('Belum Verifikasi')
                    ->toggleable(),
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
                TernaryFilter::make('is_banned')
                    ->label('Status Akun')
                    ->placeholder('Semua Status')
                    ->trueLabel('Diblokir')
                    ->falseLabel('Aktif'),
                TernaryFilter::make('email_verified_at')
                    ->label('Verifikasi Email')
                    ->placeholder('Semua')
                    ->trueLabel('Sudah Verifikasi')
                    ->falseLabel('Belum Verifikasi')
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
                    ForceDeleteBulkAction::make(),
                    RestoreBulkAction::make(),
                ]),
            ])
            ->striped()
            ->defaultSort('created_at', 'desc')
            ->emptyStateHeading('Belum Ada Data Guru')
            ->emptyStateDescription('Tambahkan guru baru untuk memulai.')
            ->emptyStateIcon('heroicon-o-academic-cap');
    }

    public static function getRelations(): array
    {
        return [
            //
        ];
    }

    public static function getPages(): array
    {
        return [
            'index' => Pages\ListTeachers::route('/'),
            'create' => Pages\CreateTeacher::route('/create'),
            'view' => Pages\ViewTeacher::route('/{record}'),
            'edit' => Pages\EditTeacher::route('/{record}/edit'),
        ];
    }
}
