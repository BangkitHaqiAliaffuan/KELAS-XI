<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Support\Str;

class Office extends Model
{
    protected $fillable = [
        'name',
        'slug',
        'description',
        'address',
        'latitude',
        'longitude',
        'capacity',
        'price_per_day',
        'price_per_week',
        'price_per_month',
        'photos',
        'status',
        'operating_hours',
        'city_id',
    ];

    protected $casts = [
        'photos' => 'array',
        'price_per_day' => 'decimal:2',
        'price_per_week' => 'decimal:2',
        'price_per_month' => 'decimal:2',
        'rating' => 'decimal:2',
    ];

    protected static function boot()
    {
        parent::boot();

        static::creating(function ($office) {
            if (empty($office->slug)) {
                $office->slug = Str::slug($office->name);
            }
        });
    }

    public function city(): BelongsTo
    {
        return $this->belongsTo(City::class);
    }

    public function facilities(): BelongsToMany
    {
        return $this->belongsToMany(Facility::class, 'office_facilities');
    }

    public function transactions(): HasMany
    {
        return $this->hasMany(Transaction::class);
    }

    // Scopes
    public function scopeAvailable(Builder $query): Builder
    {
        return $query->where('status', 'available');
    }

    public function scopeByCity(Builder $query, $cityId): Builder
    {
        return $query->where('city_id', $cityId);
    }

    public function scopeByCapacity(Builder $query, $minCapacity, $maxCapacity = null): Builder
    {
        $query->where('capacity', '>=', $minCapacity);

        if ($maxCapacity) {
            $query->where('capacity', '<=', $maxCapacity);
        }

        return $query;
    }

    public function scopeByPriceRange(Builder $query, $minPrice, $maxPrice = null, $priceType = 'price_per_day'): Builder
    {
        $query->where($priceType, '>=', $minPrice);

        if ($maxPrice) {
            $query->where($priceType, '<=', $maxPrice);
        }

        return $query;
    }

    public function scopeWithFacilities(Builder $query, array $facilityIds): Builder
    {
        return $query->whereHas('facilities', function ($q) use ($facilityIds) {
            $q->whereIn('facilities.id', $facilityIds);
        });
    }

    // Accessors
    public function getMainPhotoAttribute(): ?string
    {
        return $this->photos ? $this->photos[0] ?? null : null;
    }

    public function getPriceForDurationAttribute(): array
    {
        return [
            'daily' => $this->price_per_day,
            'weekly' => $this->price_per_week,
            'monthly' => $this->price_per_month,
        ];
    }
}
