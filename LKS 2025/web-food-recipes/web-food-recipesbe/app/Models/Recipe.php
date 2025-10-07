<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Support\Str;

class Recipe extends Model
{
    use SoftDeletes;

    protected $fillable = [
        'name',
        'slug',
        'description',
        'thumbnail',
        'video_url',
        'file_url',
        'cooking_time',
        'servings',
        'difficulty',
        'is_featured',
        'category_id',
        'recipe_author_id',
    ];

    protected $casts = [
        'is_featured' => 'boolean',
        'created_at' => 'datetime',
        'updated_at' => 'datetime',
        'deleted_at' => 'datetime',
    ];

    // Boot method untuk auto-generate slug
    protected static function boot()
    {
        parent::boot();

        static::creating(function ($model) {
            if (empty($model->slug)) {
                $model->slug = Str::slug($model->name);
            }
        });

        static::updating(function ($model) {
            if ($model->isDirty('name') && empty($model->slug)) {
                $model->slug = Str::slug($model->name);
            }
        });
    }

    // Relasi BelongsTo
    public function category(): BelongsTo
    {
        return $this->belongsTo(Category::class);
    }

    public function recipeAuthor(): BelongsTo
    {
        return $this->belongsTo(RecipeAuthor::class);
    }

    // Relasi HasMany
    public function recipeIngredients(): HasMany
    {
        return $this->hasMany(RecipeIngredient::class);
    }

    public function recipePhotos(): HasMany
    {
        return $this->hasMany(RecipePhoto::class)->orderBy('order');
    }

    public function recipeTutorials(): HasMany
    {
        return $this->hasMany(RecipeTutorial::class)->orderBy('step_number');
    }

    // Route key name untuk slug-based routing
    public function getRouteKeyName(): string
    {
        return 'slug';
    }

    // Scopes
    public function scopeFeatured($query)
    {
        return $query->where('is_featured', true);
    }

    public function scopeByCategory($query, $categoryId)
    {
        return $query->where('category_id', $categoryId);
    }

    public function scopeSearch($query, $search)
    {
        return $query->where('name', 'like', '%' . $search . '%')
                    ->orWhere('description', 'like', '%' . $search . '%');
    }
}
