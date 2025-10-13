<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Order extends Model
{
    use HasFactory;

    /**
     * The attributes that are mass assignable.
     *
     * @var array<int, string>
     */
    protected $fillable = [
        'listing_id',
        'buyer_id',
        'seller_id',
        'quantity',
        'total_price',
        'status',
        'payment_status',
        'shipping_address',
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
        'quantity' => 'decimal:2',
        'total_price' => 'decimal:2',
        'rating' => 'integer',
    ];

    /**
     * Get the listing for this order
     */
    public function listing()
    {
        return $this->belongsTo(MarketplaceListing::class, 'listing_id');
    }

    /**
     * Get the buyer of this order
     */
    public function buyer()
    {
        return $this->belongsTo(User::class, 'buyer_id');
    }

    /**
     * Get the seller of this order
     */
    public function seller()
    {
        return $this->belongsTo(User::class, 'seller_id');
    }
    
    /**
     * Scope to get pending orders
     */
    public function scopePending($query)
    {
        return $query->where('status', 'pending');
    }
    
    /**
     * Scope to get confirmed orders
     */
    public function scopeConfirmed($query)
    {
        return $query->where('status', 'confirmed');
    }
    
    /**
     * Scope to get completed orders
     */
    public function scopeCompleted($query)
    {
        return $query->where('status', 'completed');
    }
    
    /**
     * Scope to get cancelled orders
     */
    public function scopeCancelled($query)
    {
        return $query->where('status', 'cancelled');
    }
}
