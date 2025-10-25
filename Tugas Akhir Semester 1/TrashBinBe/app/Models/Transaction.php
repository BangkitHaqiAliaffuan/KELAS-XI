<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Transaction extends Model
{
    use HasFactory;

    /**
     * The attributes that are mass assignable.
     *
     * @var array<int, string>
     */
    protected $fillable = [
        'user_id',
        'type',
        'reference_id',
        'reference_type',
        'amount',
        'points_earned',
        'description',
        'payment_method',
    ];

    /**
     * The attributes that should be cast.
     *
     * @var array<string, string>
     */
    protected $casts = [
        'amount' => 'decimal:2',
        'points_earned' => 'integer',
        'payment_method' => 'string',
    ];

    /**
     * Get the user for this transaction
     */
    public function user()
    {
        return $this->belongsTo(User::class, 'user_id');
    }
    
    /**
     * Get the related model for this transaction (PickupRequest, Order, etc.)
     */
    public function reference()
    {
        return $this->morphTo();
    }
}
