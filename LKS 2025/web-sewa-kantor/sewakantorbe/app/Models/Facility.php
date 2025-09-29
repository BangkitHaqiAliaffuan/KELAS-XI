<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;

class Facility extends Model
{
    protected $fillable = [
        'name',
        'icon',
        'description',
    ];

    public function offices(): BelongsToMany
    {
        return $this->belongsToMany(Office::class, 'office_facilities');
    }
}
