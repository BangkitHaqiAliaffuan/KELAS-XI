<?php

namespace App\Filament\Resources\KelasResource\Pages;

use App\Filament\Resources\KelasResource;
use App\Imports\KelasImport;
use Filament\Actions;
use Filament\Forms\Components\FileUpload;
use Filament\Notifications\Notification;
use Filament\Resources\Pages\ListRecords;
use Illuminate\Http\UploadedFile;

class ListKelas extends ListRecords
{
    protected static string $resource = KelasResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\CreateAction::make(),
            Actions\Action::make('import')
                ->label('Import Classes')
                ->icon('heroicon-o-arrow-down-tray')
                ->form([
                    FileUpload::make('upload')
                        ->label('Spreadsheet File')
                        ->acceptedFileTypes(['text/csv', 'text/plain', 'application/vnd.ms-excel', 'application/csv', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'])
                        ->required()
                        ->preserveFilenames()
                        ->helperText('CSV or XLSX file should have columns: nama_kelas, kode_kelas')
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
                        $importer = new KelasImport();
                        $result = $importer->import($uploadedFile);

                        if (count($result['errors']) > 0) {
                            Notification::make()
                                ->title('Import completed with errors')
                                ->body("Imported {$result['imported']} classes. Skipped {$result['skipped']} due to errors.")
                                ->warning()
                                ->send();
                        } else {
                            Notification::make()
                                ->title('Classes imported successfully')
                                ->body("Imported {$result['imported']} classes.")
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