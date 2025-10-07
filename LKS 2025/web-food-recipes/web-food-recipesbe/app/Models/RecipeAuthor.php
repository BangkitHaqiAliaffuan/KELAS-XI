<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Relations\HasMany;

class RecipeAuthor extends Model
{
    use SoftDeletes;

    protected $fillable = [
        'name',
        'bio',
        'email',
        'photo',
    ];

    protected $casts = [
        'created_at' => 'datetime',
        'updated_at' => 'datetime',
        'deleted_at' => 'datetime',
    ];

    // Relasi
    public function recipes(): HasMany
    {
        return $this->hasMany(Recipe::class);
    }
}
