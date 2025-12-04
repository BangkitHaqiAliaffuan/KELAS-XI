<?php

namespace App\Filament\Resources;

use App\Filament\Resources\ScheduleResource\Pages;
use App\Models\Kelas;
use App\Models\Schedule;
use App\Models\Subject;
use App\Models\Teacher;
use Filament\Actions\BulkActionGroup;
use Filament\Actions\DeleteBulkAction;
use Filament\Actions\EditAction;
use Filament\Actions\ViewAction;
use Filament\Forms\Components\Select;
use Filament\Forms\Components\TextInput;
use Filament\Forms\Components\TimePicker;
use Filament\Resources\Resource;
use Filament\Schemas\Components\Grid;
use Filament\Schemas\Components\Section;
use Filament\Schemas\Components\Tabs;
use Filament\Schemas\Schema;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Filters\SelectFilter;
use Filament\Tables\Table;

class ScheduleResource extends Resource
{
    protected static ?string $model = Schedule::class;

    protected static string|\BackedEnum|null $navigationIcon = 'heroicon-o-calendar-days';

    protected static ?string $navigationLabel = 'Jadwal';

    protected static ?string $modelLabel = 'Jadwal';

    protected static ?string $pluralModelLabel = 'Data Jadwal';

    protected static string|\UnitEnum|null $navigationGroup = '📊 Akademik';

    protected static ?int $navigationSort = 1;

    public static function form(Schema $schema): Schema
    {
        return $schema
            ->components([
                Tabs::make('Tabs')
                    ->tabs([
                        Tabs\Tab::make('📅 Informasi Jadwal')
                            ->icon('heroicon-o-calendar-days')
                            ->schema([
                                Section::make('Waktu & Hari')
                                    ->description('Tentukan hari dan waktu pembelajaran')
                                    ->icon('heroicon-o-clock')
                                    ->collapsible()
                                    ->schema([
                                        Grid::make(3)
                                            ->schema([
                                                Select::make('hari')
                                                    ->label('Hari')
                                                    ->options([
                                                        'Senin' => '🟦 Senin',
                                                        'Selasa' => '🟩 Selasa',
                                                        'Rabu' => '🟨 Rabu',
                                                        'Kamis' => '🟥 Kamis',
                                                        'Jumat' => '🟪 Jumat',
                                                        'Sabtu' => '⬜ Sabtu',
                                                    ])
                                                    ->required()
                                                    ->native(false)
                                                    ->searchable()
                                                    ->prefixIcon('heroicon-o-calendar'),
                                                TimePicker::make('jam_mulai')
                                                    ->label('Jam Mulai')
                                                    ->required()
                                                    ->seconds(false)
                                                    ->prefixIcon('heroicon-o-play')
                                                    ->helperText('Format: HH:MM'),
                                                TimePicker::make('jam_selesai')
                                                    ->label('Jam Selesai')
                                                    ->required()
                                                    ->seconds(false)
                                                    ->after('jam_mulai')
                                                    ->prefixIcon('heroicon-o-stop')
                                                    ->helperText('Format: HH:MM'),
                                            ]),
                                    ]),
                                Section::make('Detail Pembelajaran')
                                    ->description('Tentukan kelas, mata pelajaran, dan guru')
                                    ->icon('heroicon-o-academic-cap')
                                    ->collapsible()
                                    ->schema([
                                        Grid::make(2)
                                            ->schema([
                                                Select::make('kelas')
                                                    ->label('Kelas')
                                                    ->options(function () {
                                                        return Kelas::pluck('nama_kelas', 'nama_kelas')->toArray();
                                                    })
                                                    ->required()
                                                    ->native(false)
                                                    ->searchable()
                                                    ->prefixIcon('heroicon-o-building-library')
                                                    ->helperText('Pilih kelas untuk jadwal ini'),
                                                Select::make('mata_pelajaran')
                                                    ->label('Mata Pelajaran')
                                                    ->options(function () {
                                                        return Subject::pluck('nama', 'nama')->toArray();
                                                    })
                                                    ->required()
                                                    ->native(false)
                                                    ->searchable()
                                                    ->prefixIcon('heroicon-o-book-open')
                                                    ->helperText('Pilih mata pelajaran'),
                                            ]),
                                        Grid::make(2)
                                            ->schema([
                                                Select::make('guru_id')
                                                    ->label('Guru Pengajar')
                                                    ->options(function () {
                                                        return Teacher::pluck('name', 'id')->toArray();
                                                    })
                                                    ->required()
                                                    ->native(false)
                                                    ->searchable()
                                                    ->prefixIcon('heroicon-o-user')
                                                    ->helperText('Pilih guru pengajar'),
                                                TextInput::make('ruang')
                                                    ->label('Ruang Kelas')
                                                    ->maxLength(255)
                                                    ->nullable()
                                                    ->prefixIcon('heroicon-o-map-pin')
                                                    ->placeholder('Contoh: Lab Komputer 1')
                                                    ->helperText('Opsional: Nama ruangan'),
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
                TextColumn::make('hari')
                    ->label('Hari')
                    ->sortable()
                    ->searchable()
                    ->badge()
                    ->color(fn (string $state): string => match ($state) {
                        'Senin' => 'info',
                        'Selasa' => 'success',
                        'Rabu' => 'warning',
                        'Kamis' => 'danger',
                        'Jumat' => 'primary',
                        'Sabtu' => 'gray',
                        default => 'gray',
                    })
                    ->icon('heroicon-o-calendar'),
                TextColumn::make('kelas')
                    ->label('Kelas')
                    ->searchable()
                    ->sortable()
                    ->icon('heroicon-o-building-library')
                    ->weight('bold')
                    ->color('primary'),
                TextColumn::make('mata_pelajaran')
                    ->label('Mata Pelajaran')
                    ->searchable()
                    ->sortable()
                    ->badge()
                    ->color('info')
                    ->wrap(),
                TextColumn::make('guru.name')
                    ->label('Guru Pengajar')
                    ->searchable()
                    ->sortable()
                    ->icon('heroicon-o-user')
                    ->color('success'),
                TextColumn::make('jam_mulai')
                    ->label('Jam Mulai')
                    ->time('H:i')
                    ->sortable()
                    ->icon('heroicon-o-play')
                    ->badge()
                    ->color('success'),
                TextColumn::make('jam_selesai')
                    ->label('Jam Selesai')
                    ->time('H:i')
                    ->sortable()
                    ->icon('heroicon-o-stop')
                    ->badge()
                    ->color('danger'),
                TextColumn::make('ruang')
                    ->label('Ruang')
                    ->searchable()
                    ->icon('heroicon-o-map-pin')
                    ->placeholder('—')
                    ->toggleable(isToggledHiddenByDefault: false),
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
                SelectFilter::make('hari')
                    ->label('Filter Hari')
                    ->options([
                        'Senin' => 'Senin',
                        'Selasa' => 'Selasa',
                        'Rabu' => 'Rabu',
                        'Kamis' => 'Kamis',
                        'Jumat' => 'Jumat',
                        'Sabtu' => 'Sabtu',
                    ])
                    ->indicator('Hari'),
                SelectFilter::make('kelas')
                    ->label('Filter Kelas')
                    ->options(function () {
                        return Kelas::pluck('nama_kelas', 'nama_kelas')->toArray();
                    })
                    ->indicator('Kelas'),
                SelectFilter::make('guru_id')
                    ->label('Filter Guru')
                    ->relationship('guru', 'name')
                    ->searchable()
                    ->preload()
                    ->indicator('Guru'),
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
            ->defaultSort('hari', 'asc')
            ->emptyStateHeading('Belum Ada Jadwal')
            ->emptyStateDescription('Tambahkan jadwal pelajaran baru.')
            ->emptyStateIcon('heroicon-o-calendar-days');
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
            'index' => Pages\ListSchedules::route('/'),
            'create' => Pages\CreateSchedule::route('/create'),
            'view' => Pages\ViewSchedule::route('/{record}'),
            'edit' => Pages\EditSchedule::route('/{record}/edit'),
        ];
    }
}
