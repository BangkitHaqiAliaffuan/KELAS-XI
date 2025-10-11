<?php

namespace App\Filament\Resources\RecipePhotos\Pages;

use App\Filament\Resources\RecipePhotos\RecipePhotoResource;
use Filament\Actions\CreateAction;
use Filament\Resources\Pages\ListRecords;

class ListRecipePhotos extends ListRecords
{
    protected static string $resource = RecipePhotoResource::class;

    protected function getHeaderActions(): array
    {
        return [
            CreateAction::make(),
        ];
    }
}
