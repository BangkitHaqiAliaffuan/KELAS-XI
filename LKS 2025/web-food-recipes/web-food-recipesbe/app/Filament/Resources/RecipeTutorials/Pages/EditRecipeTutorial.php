<?php

namespace App\Filament\Resources\RecipeTutorials\Pages;

use App\Filament\Resources\RecipeTutorials\RecipeTutorialResource;
use Filament\Actions\DeleteAction;
use Filament\Resources\Pages\EditRecord;

class EditRecipeTutorial extends EditRecord
{
    protected static string $resource = RecipeTutorialResource::class;

    protected function getHeaderActions(): array
    {
        return [
            DeleteAction::make(),
        ];
    }
}
