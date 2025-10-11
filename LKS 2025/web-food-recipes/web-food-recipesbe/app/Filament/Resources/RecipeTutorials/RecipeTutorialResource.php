<?php

namespace App\Filament\Resources\RecipeTutorials;

use App\Filament\Resources\RecipeTutorials\Pages\CreateRecipeTutorial;
use App\Filament\Resources\RecipeTutorials\Pages\EditRecipeTutorial;
use App\Filament\Resources\RecipeTutorials\Pages\ListRecipeTutorials;
use App\Filament\Resources\RecipeTutorials\Schemas\RecipeTutorialForm;
use App\Filament\Resources\RecipeTutorials\Tables\RecipeTutorialsTable;
use App\Models\RecipeTutorial;
use Filament\Resources\Resource;
use Filament\Schemas\Schema;
use Filament\Tables\Table;
use BackedEnum;
use UnitEnum;

class RecipeTutorialResource extends Resource
{
    protected static ?string $model = RecipeTutorial::class;

    protected static string|BackedEnum|null $navigationIcon = 'heroicon-o-play-circle';

    protected static string|UnitEnum|null $navigationGroup = 'Media';

    protected static ?int $navigationSort = 2;

    protected static ?string $recordTitleAttribute = 'step_title';

    public static function form(Schema $schema): Schema
    {
        return RecipeTutorialForm::configure($schema);
    }

    public static function table(Table $table): Table
    {
        return RecipeTutorialsTable::configure($table);
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
            'index' => ListRecipeTutorials::route('/'),
            'create' => CreateRecipeTutorial::route('/create'),
            'edit' => EditRecipeTutorial::route('/{record}/edit'),
        ];
    }
}
