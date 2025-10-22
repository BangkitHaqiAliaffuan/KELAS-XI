<?php

namespace App\Filament\Resources\SubjectResource\Pages;

use App\Filament\Resources\SubjectResource;
use App\Imports\SubjectImport;
use Filament\Actions;
use Filament\Forms\Components\FileUpload;
use Filament\Notifications\Notification;
use Filament\Resources\Pages\ListRecords;

class ListSubjects extends ListRecords
{
    protected static string $resource = SubjectResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\CreateAction::make(),
            Actions\Action::make('import')
                ->label('Import Subjects')
                ->icon('heroicon-o-arrow-down-tray')
                ->form([
                    FileUpload::make('upload')
                        ->label('Spreadsheet File')
                        ->acceptedFileTypes(['text/csv', 'text/plain', 'application/vnd.ms-excel', 'application/csv', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'])
                        ->required()
                        ->preserveFilenames()
                        ->helperText('CSV or XLSX file should have columns: nama, kode')
                ])
                ->action(function (array $data) {
                    try {
                        // Get the uploaded file from the data array
                        $uploadedFile = $data['upload'];
                        
                        // Check if it's a valid uploaded file
                        if (!$uploadedFile || !($uploadedFile instanceof \Illuminate\Http\UploadedFile)) {
                            throw new \Exception('Invalid file upload. Please make sure you\'ve selected a valid file.');
                        }
                        
                        // Import the data
                        $importer = new SubjectImport();
                        $result = $importer->import($uploadedFile);

                        if (count($result['errors']) > 0) {
                            Notification::make()
                                ->title('Import completed with errors')
                                ->body("Imported {$result['imported']} subjects. Skipped {$result['skipped']} due to errors.")
                                ->warning()
                                ->send();
                        } else {
                            Notification::make()
                                ->title('Subjects imported successfully')
                                ->body("Imported {$result['imported']} subjects.")
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