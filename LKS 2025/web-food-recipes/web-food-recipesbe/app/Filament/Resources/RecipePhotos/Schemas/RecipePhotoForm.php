<?php

namespace App\Filament\Resources\RecipePhotos\Schemas;

use Filament\Forms\Components\Select;
use Filament\Forms\Components\TextInput;
use Filament\Schemas\Schema;

class RecipePhotoForm
{
    public static function configure(Schema $schema): Schema
    {
        return $schema
            ->components([
                Select::make('recipe_id')
                    ->relationship('recipe', 'name')
                    ->required(),
                TextInput::make('photo_path')
                    ->required(),
                TextInput::make('alt_text')
                    ->default(null),
                TextInput::make('order')
                    ->required()
                    ->numeric()
                    ->default(0),
            ]);
    }
}
