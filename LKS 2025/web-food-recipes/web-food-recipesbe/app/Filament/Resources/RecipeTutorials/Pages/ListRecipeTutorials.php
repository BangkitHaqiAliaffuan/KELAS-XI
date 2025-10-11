<?php

namespace App\Filament\Resources\RecipeTutorials\Pages;

use App\Filament\Resources\RecipeTutorials\RecipeTutorialResource;
use Filament\Actions\CreateAction;
use Filament\Resources\Pages\ListRecords;

class ListRecipeTutorials extends ListRecords
{
    protected static string $resource = RecipeTutorialResource::class;

    protected function getHeaderActions(): array
    {
        return [
            CreateAction::make(),
        ];
    }
}
