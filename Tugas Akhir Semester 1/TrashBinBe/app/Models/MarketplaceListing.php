<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class MarketplaceListing extends Model
{
    use HasFactory;

    /**
     * The attributes that are mass assignable.
     *
     * @var array<int, string>
     */
    protected $fillable = [
        'seller_id',
        'waste_category_id',
        'title',
        'description',
        'quantity',
        'unit',
        'price_per_unit',
        'total_price',
        'condition',
        'location',
        'lat',
        'lng',
        'status',
        'photos',
        'views_count',
        'expires_at',
    ];

    /**
     * The attributes that should be cast.
     *
     * @var array<string, string>
     */
    protected $casts = [
        'lat' => 'decimal:8',
        'lng' => 'decimal:8',
        'quantity' => 'decimal:2',
        'price_per_unit' => 'decimal:2',
        'total_price' => 'decimal:2',
        'photos' => 'array',
        'expires_at' => 'datetime',
        'views_count' => 'integer',
    ];

    /**
     * Get the seller of this listing
     */
    public function seller()
    {
        return $this->belongsTo(User::class, 'seller_id');
    }

    /**
     * Get the waste category for this listing
     */
    public function wasteCategory()
    {
        return $this->belongsTo(WasteCategory::class, 'waste_category_id');
    }

    /**
     * Get the orders for this listing
     */
    public function orders()
    {
        return $this->hasMany(Order::class);
    }
    
    /**
     * Scope to get available listings
     */
    public function scopeAvailable($query)
    {
        return $query->where('status', 'available');
    }
    
    /**
     * Scope to get expired listings
     */
    public function scopeExpired($query)
    {
        return $query->where('status', 'expired')->orWhere('expires_at', '<', now());
    }
    
    /**
     * Scope to get sold listings
     */
    public function scopeSold($query)
    {
        return $query->where('status', 'sold');
    }
    
    /**
     * Scope to get reserved listings
     */
    public function scopeReserved($query)
    {
        return $query->where('status', 'reserved');
    }
}
