<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PickupRequest extends Model
{
    use HasFactory;

    /**
     * The attributes that are mass assignable.
     *
     * @var array<int, string>
     */
    protected $fillable = [
        'user_id',
        'collector_id',
        'pickup_address',
        'pickup_lat',
        'pickup_lng',
        'scheduled_date',
        'status',
        'total_weight',
        'total_price',
        'notes',
        'rating',
        'review',
    ];

    /**
     * The attributes that should be cast.
     *
     * @var array<string, string>
     */
    protected $casts = [
        'pickup_lat' => 'decimal:8',
        'pickup_lng' => 'decimal:8',
        'scheduled_date' => 'datetime',
        'total_weight' => 'decimal:2',
        'total_price' => 'decimal:2',
        'rating' => 'integer',
    ];

    /**
     * Get the user who requested the pickup
     */
    public function user()
    {
        return $this->belongsTo(User::class, 'user_id');
    }

    /**
     * Get the collector assigned to this pickup
     */
    public function collector()
    {
        return $this->belongsTo(User::class, 'collector_id');
    }

    /**
     * Get the pickup items for this request
     */
    public function items()
    {
        return $this->hasMany(PickupItem::class, 'pickup_request_id');
    }
    
    /**
     * Scope to get pending pickups
     */
    public function scopePending($query)
    {
        return $query->where('status', 'pending');
    }
    
    /**
     * Scope to get accepted pickups
     */
    public function scopeAccepted($query)
    {
        return $query->where('status', 'accepted');
    }
    
    /**
     * Scope to get completed pickups
     */
    public function scopeCompleted($query)
    {
        return $query->where('status', 'completed');
    }
    
    /**
     * Scope to get cancelled pickups
     */
    public function scopeCancelled($query)
    {
        return $query->where('status', 'cancelled');
    }
}
