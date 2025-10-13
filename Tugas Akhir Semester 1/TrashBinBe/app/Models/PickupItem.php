<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PickupItem extends Model
{
    use HasFactory;

    /**
     * The attributes that are mass assignable.
     *
     * @var array<int, string>
     */
    protected $fillable = [
        'pickup_request_id',
        'waste_category_id',
        'estimated_weight',
        'actual_weight',
        'photo_url',
        'price_per_unit',
        'subtotal',
    ];

    /**
     * The attributes that should be cast.
     *
     * @var array<string, string>
     */
    protected $casts = [
        'estimated_weight' => 'decimal:2',
        'actual_weight' => 'decimal:2',
        'price_per_unit' => 'decimal:2',
        'subtotal' => 'decimal:2',
    ];

    /**
     * Calculate subtotal when saving
     */
    protected static function boot()
    {
        parent::boot();

        static::saving(function ($model) {
            if ($model->actual_weight && $model->price_per_unit) {
                $model->subtotal = $model->actual_weight * $model->price_per_unit;
            } elseif ($model->estimated_weight && $model->price_per_unit) {
                $model->subtotal = $model->estimated_weight * $model->price_per_unit;
            }
        });
    }

    /**
     * Get the pickup request this item belongs to
     */
    public function pickupRequest()
    {
        return $this->belongsTo(PickupRequest::class, 'pickup_request_id');
    }

    /**
     * Get the waste category for this item
     */
    public function wasteCategory()
    {
        return $this->belongsTo(WasteCategory::class, 'waste_category_id');
    }
}
