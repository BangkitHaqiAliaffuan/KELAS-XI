<?php

namespace App\Filament\Resources\RecipePhotos\Pages;

use App\Filament\Resources\RecipePhotos\RecipePhotoResource;
use Filament\Actions\DeleteAction;
use Filament\Resources\Pages\EditRecord;

class EditRecipePhoto extends EditRecord
{
    protected static string $resource = RecipePhotoResource::class;

    protected function getHeaderActions(): array
    {
        return [
            DeleteAction::make(),
        ];
    }
}
