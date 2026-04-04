<?php

namespace App\Filament\Resources\Transactions\Schemas;

use Filament\Forms\Components\DatePicker;
use Filament\Forms\Components\Select;
use Filament\Forms\Components\TextInput;
use Filament\Forms\Components\Textarea;
use Filament\Schemas\Schema;

class TransactionForm
{
    public static function configure(Schema $schema): Schema
    {
        return $schema
            ->components([
                Select::make('user_id')
                    ->relationship('user', 'name')
                    ->searchable()
                    ->preload()
                    ->required(),
                Select::make('type')
                    ->options(['income' => 'Income', 'expense' => 'Expense'])
                    ->required(),
                Select::make('category')
                    ->options([
                        'Salary' => 'Salary',
                        'Freelance' => 'Freelance',
                        'Investment' => 'Investment',
                        'Bills & Utilities' => 'Bills & Utilities',
                        'Food & Dining' => 'Food & Dining',
                        'Transportation' => 'Transportation',
                        'Shopping' => 'Shopping',
                        'Entertainment' => 'Entertainment',
                        'Health' => 'Health',
                        'Education' => 'Education',
                        'Other' => 'Other',
                    ])
                    ->searchable()
                    ->native(false)
                    ->required(),
                Textarea::make('note')
                    ->columnSpanFull(),
                TextInput::make('amount')
                    ->required()
                    ->numeric()
                    ->prefix('Rp')
                    ->minValue(1),
                DatePicker::make('transaction_date')
                    ->required(),
            ]);
    }
}
