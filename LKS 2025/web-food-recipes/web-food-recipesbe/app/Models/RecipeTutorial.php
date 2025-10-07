<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class RecipeTutorial extends Model
{
    protected $fillable = [
        'recipe_id',
        'step_number',
        'title',
        'instruction',
        'image',
    ];

    protected $casts = [
        'step_number' => 'integer',
        'created_at' => 'datetime',
        'updated_at' => 'datetime',
    ];

    // Relasi
    public function recipe(): BelongsTo
    {
        return $this->belongsTo(Recipe::class);
    }
}
