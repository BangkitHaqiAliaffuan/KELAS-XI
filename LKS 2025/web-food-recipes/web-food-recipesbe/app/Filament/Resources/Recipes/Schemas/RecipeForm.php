<?php

namespace App\Filament\Resources\Recipes\Schemas;

use Filament\Forms\Components\Section;
use Filament\Forms\Components\Grid;
use Filament\Forms\Components\Select;
use Filament\Forms\Components\TextInput;
use Filament\Forms\Components\Textarea;
use Filament\Forms\Components\Toggle;
use Filament\Forms\Components\FileUpload;
use Filament\Schemas\Schema;
use Illuminate\Support\Str;

class RecipeForm
{
    public static function configure(Schema $schema): Schema
    {
        return $schema
            ->components([
                Section::make('Basic Information')
                    ->description('Enter the basic details of the recipe')
                    ->schema([
                        TextInput::make('name')
                            ->required()
                            ->maxLength(255)
                            ->live(onBlur: true)
                            ->afterStateUpdated(function (string $operation, $state, $set) {
                                if ($operation !== 'create') {
                                    return;
                                }
                                $set('slug', Str::slug($state));
                            }),
                        TextInput::make('slug')
                            ->required()
                            ->maxLength(255)
                            ->unique(ignoreRecord: true)
                            ->alpha_dash()
                            ->helperText('URL-friendly version of the name'),
                        Textarea::make('description')
                            ->rows(4)
                            ->columnSpanFull()
                            ->helperText('Brief description of the recipe'),
                    ])->columns(2),

                Section::make('Media & Files')
                    ->description('Upload images and add media links')
                    ->schema([
                        FileUpload::make('thumbnail')
                            ->image()
                            ->directory('recipes/thumbnails')
                            ->helperText('Main image for the recipe'),
                        TextInput::make('video_url')
                            ->url()
                            ->placeholder('https://youtube.com/watch?v=...')
                            ->helperText('Optional: YouTube or other video URL'),
                        TextInput::make('file_url')
                            ->url()
                            ->placeholder('https://example.com/recipe.pdf')
                            ->helperText('Optional: PDF or document URL'),
                    ])->columns(1),

                Section::make('Recipe Details')
                    ->description('Cooking information and difficulty')
                    ->schema([
                        Grid::make(3)->schema([
                            TextInput::make('cooking_time')
                                ->numeric()
                                ->suffix('minutes')
                                ->placeholder('30')
                                ->helperText('Cooking time in minutes'),
                            TextInput::make('servings')
                                ->numeric()
                                ->placeholder('4')
                                ->helperText('Number of servings'),
                            Select::make('difficulty')
                                ->options([
                                    'easy' => 'Easy',
                                    'medium' => 'Medium',
                                    'hard' => 'Hard'
                                ])
                                ->default('easy')
                                ->required()
                                ->native(false),
                        ]),
                        Toggle::make('is_featured')
                            ->label('Featured Recipe')
                            ->helperText('Show this recipe on the homepage'),
                    ]),

                Section::make('Relationships')
                    ->description('Connect recipe to category and author')
                    ->schema([
                        Select::make('category_id')
                            ->relationship('category', 'name')
                            ->required()
                            ->native(false)
                            ->preload()
                            ->searchable(),
                        Select::make('recipe_author_id')
                            ->relationship('recipeAuthor', 'name')
                            ->required()
                            ->native(false)
                            ->preload()
                            ->searchable()
                            ->createOptionForm([
                                TextInput::make('name')
                                    ->required()
                                    ->maxLength(255),
                                TextInput::make('email')
                                    ->email()
                                    ->unique('recipe_authors', 'email'),
                                Textarea::make('bio')
                                    ->rows(3),
                            ]),
                    ])->columns(2),
            ]);
    }
}
