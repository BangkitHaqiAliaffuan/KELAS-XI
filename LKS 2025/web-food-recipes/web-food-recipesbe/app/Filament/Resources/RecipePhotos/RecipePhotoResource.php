<?php

namespace App\Filament\Resources\RecipePhotos;

use App\Filament\Resources\RecipePhotos\Pages\CreateRecipePhoto;
use App\Filament\Resources\RecipePhotos\Pages\EditRecipePhoto;
use App\Filament\Resources\RecipePhotos\Pages\ListRecipePhotos;
use App\Filament\Resources\RecipePhotos\Schemas\RecipePhotoForm;
use App\Filament\Resources\RecipePhotos\Tables\RecipePhotosTable;
use App\Models\RecipePhoto;
use Filament\Resources\Resource;
use Filament\Schemas\Schema;
use Filament\Tables\Table;
use BackedEnum;
use UnitEnum;

class RecipePhotoResource extends Resource
{
    protected static ?string $model = RecipePhoto::class;

    protected static string|BackedEnum|null $navigationIcon = 'heroicon-o-camera';

    protected static string|UnitEnum|null $navigationGroup = 'Media';

    protected static ?int $navigationSort = 1;

    protected static ?string $recordTitleAttribute = 'photo_url';

    public static function form(Schema $schema): Schema
    {
        return RecipePhotoForm::configure($schema);
    }

    public static function table(Table $table): Table
    {
        return RecipePhotosTable::configure($table);
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
            'index' => ListRecipePhotos::route('/'),
            'create' => CreateRecipePhoto::route('/create'),
            'edit' => EditRecipePhoto::route('/{record}/edit'),
        ];
    }
}
