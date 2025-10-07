<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class RecipePhoto extends Model
{
    protected $fillable = [
        'recipe_id',
        'photo_path',
        'alt_text',
        'order',
    ];

    protected $casts = [
        'order' => 'integer',
        'created_at' => 'datetime',
        'updated_at' => 'datetime',
    ];

    // Relasi
    public function recipe(): BelongsTo
    {
        return $this->belongsTo(Recipe::class);
    }
}
