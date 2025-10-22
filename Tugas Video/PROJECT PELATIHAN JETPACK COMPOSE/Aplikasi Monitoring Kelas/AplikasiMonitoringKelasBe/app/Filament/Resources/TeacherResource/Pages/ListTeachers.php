<?php

namespace App\Filament\Resources\TeacherResource\Pages;

use App\Filament\Resources\TeacherResource;
use App\Imports\TeacherImport;
use Filament\Actions;
use Filament\Forms\Components\FileUpload;
use Filament\Notifications\Notification;
use Filament\Resources\Pages\ListRecords;

class ListTeachers extends ListRecords
{
    protected static string $resource = TeacherResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\CreateAction::make(),
            Actions\Action::make('import')
                ->label('Import Teachers')
                ->icon('heroicon-o-arrow-down-tray')
                ->form([
                    FileUpload::make('upload')
                        ->label('Spreadsheet File')
                        ->acceptedFileTypes(['text/csv', 'text/plain', 'application/vnd.ms-excel', 'application/csv', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'])
                        ->required()
                        ->preserveFilenames()
                        ->helperText('CSV or XLSX file should have columns: name, email, mata_pelajaran, is_banned, password (optional)')
                ])
                ->action(function (array $data) {
                    try {
                        $importer = new TeacherImport();
                        $result = $importer->import($data['upload']);

                        if (count($result['errors']) > 0) {
                            Notification::make()
                                ->title('Import completed with errors')
                                ->body("Imported {$result['imported']} teachers. Skipped {$result['skipped']} due to errors.")
                                ->warning()
                                ->send();
                        } else {
                            Notification::make()
                                ->title('Teachers imported successfully')
                                ->body("Imported {$result['imported']} teachers.")
                                ->success()
                                ->send();
                        }
                    } catch (\Exception $e) {
                        Notification::make()
                            ->title('Import failed')
                            ->body($e->getMessage())
                            ->danger()
                            ->send();
                    }
                }),
        ];
    }
}