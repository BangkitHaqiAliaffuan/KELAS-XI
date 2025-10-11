<?php

namespace App\Filament\Resources\RecipeAuthors;

use App\Filament\Resources\RecipeAuthors\Pages\CreateRecipeAuthor;
use App\Filament\Resources\RecipeAuthors\Pages\EditRecipeAuthor;
use App\Filament\Resources\RecipeAuthors\Pages\ListRecipeAuthors;
use App\Filament\Resources\RecipeAuthors\Schemas\RecipeAuthorForm;
use App\Filament\Resources\RecipeAuthors\Tables\RecipeAuthorsTable;
use App\Models\RecipeAuthor;
use App\Enums\NavigationGroup;
use Filament\Resources\Resource;
use Filament\Schemas\Schema;
use Filament\Tables\Table;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\SoftDeletingScope;
use BackedEnum;
use UnitEnum;

class RecipeAuthorResource extends Resource
{
    protected static ?string $model = RecipeAuthor::class;

    protected static string|BackedEnum|null $navigationIcon = 'heroicon-o-users';

    protected static string|UnitEnum|null $navigationGroup = 'Food Management';

    protected static ?int $navigationSort = 3;

    protected static ?string $recordTitleAttribute = 'name';

    public static function form(Schema $schema): Schema
    {
        return RecipeAuthorForm::configure($schema);
    }

    public static function table(Table $table): Table
    {
        return RecipeAuthorsTable::configure($table);
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
            'index' => ListRecipeAuthors::route('/'),
            'create' => CreateRecipeAuthor::route('/create'),
            'edit' => EditRecipeAuthor::route('/{record}/edit'),
        ];
    }

    public static function getRecordRouteBindingEloquentQuery(): Builder
    {
        return parent::getRecordRouteBindingEloquentQuery()
            ->withoutGlobalScopes([
                SoftDeletingScope::class,
            ]);
    }
}
