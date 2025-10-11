<?php

namespace App\Filament\Resources\Categories\Schemas;

use Filament\Forms\Components\TextInput;
use Filament\Forms\Components\Textarea;
use Filament\Schemas\Schema;
use Illuminate\Support\Str;

class CategoryForm
{
    public static function configure(Schema $schema): Schema
    {
        return $schema
            ->components([
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
                    ->helperText('Akan otomatis dibuat dari nama kategori'),
                Textarea::make('description')
                    ->rows(3)
                    ->columnSpanFull()
                    ->helperText('Deskripsi singkat tentang kategori ini'),
                TextInput::make('icon')
                    ->maxLength(255)
                    ->helperText('Icon untuk kategori (opsional)')
                    ->placeholder('Contoh: 🍔 atau fa-utensils'),
            ]);
    }
}
