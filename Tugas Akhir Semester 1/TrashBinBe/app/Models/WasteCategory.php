<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class WasteCategory extends Model
{
    use HasFactory;

    /**
     * Indicates if the model should be timestamped.
     *
     * @var bool
     */
    public $timestamps = true;

    /**
     * The attributes that are mass assignable.
     *
     * @var array<int, string>
     */
    protected $fillable = [
        'name',
        'slug',
        'description',
        'unit',
        'base_price_per_unit',
        'icon_url',
        'is_active',
    ];

    /**
     * The attributes that should be cast.
     *
     * @var array<string, string>
     */
    protected $casts = [
        'base_price_per_unit' => 'decimal:2',
        'is_active' => 'boolean',
    ];

    /**
     * Boot the model and set up slug generation
     */
    protected static function boot()
    {
        parent::boot();

        static::creating(function ($model) {
            if (empty($model->slug)) {
                $model->slug = \Str::slug($model->name);
            }
        });

        static::updating(function ($model) {
            if (empty($model->slug)) {
                $model->slug = \Str::slug($model->name);
            }
        });
    }

    /**
     * Get the pickup items that belong to this waste category
     */
    public function pickupItems()
    {
        return $this->hasMany(PickupItem::class);
    }

    /**
     * Get the marketplace listings that use this waste category
     */
    public function listings()
    {
        return $this->hasMany(MarketplaceListing::class);
    }
}
