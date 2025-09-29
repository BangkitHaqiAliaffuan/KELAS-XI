<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Support\Str;

class City extends Model
{
    protected $fillable = [
        'name',
        'slug',
        'photo',
        'description',
    ];

    protected static function boot()
    {
        parent::boot();

        static::creating(function ($city) {
            if (empty($city->slug)) {
                $city->slug = Str::slug($city->name);
            }
        });
    }

    public function offices(): HasMany
    {
        return $this->hasMany(Office::class);
    }

    public function availableOffices(): HasMany
    {
        return $this->hasMany(Office::class)->where('status', 'available');
    }
}
