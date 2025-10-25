<?php

namespace App\Models;

// use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

class User extends Authenticatable
{
    use HasApiTokens, HasFactory, Notifiable;

    /**
     * The attributes that are mass assignable.
     *
     * @var array<int, string>
     */
    protected $fillable = [
        'name',
        'email',
        'phone',
        'password',
        'role',
        'avatar',
        'address',
        'lat',
        'lng',
        'points',
        'trashpay_amount',
        'is_verified',
        'fcm_token',
    ];

    /**
     * The attributes that should be hidden for serialization.
     *
     * @var array<int, string>
     */
    protected $hidden = [
        'password',
        'remember_token',
    ];

    /**
     * Get the attributes that should be cast.
     *
     * @return array<string, string>
     */
    protected function casts(): array
    {
        return [
            'email_verified_at' => 'datetime',
            'password' => 'hashed',
            'lat' => 'decimal:8',
            'lng' => 'decimal:8',
            'points' => 'integer',
            'trashpay_amount' => 'decimal:2',
            'is_verified' => 'boolean',
        ];
    }
    
    /**
     * Check if user is a regular user
     */
    public function isUser(): bool
    {
        return $this->role === 'user';
    }
    
    /**
     * Check if user is a collector
     */
    public function isCollector(): bool
    {
        return $this->role === 'collector';
    }
    
    /**
     * Check if user is an industry user
     */
    public function isIndustry(): bool
    {
        return $this->role === 'industry';
    }
    
    /**
     * Check if user is an admin
     */
    public function isAdmin(): bool
    {
        return $this->role === 'admin';
    }
    
    /**
     * Get the pickup requests created by this user
     */
    public function pickupRequests()
    {
        return $this->hasMany(\App\Models\PickupRequest::class, 'user_id');
    }
    
    /**
     * Get the pickup requests assigned to this collector
     */
    public function assignedPickups()
    {
        return $this->hasMany(\App\Models\PickupRequest::class, 'collector_id');
    }
    
    /**
     * Get the marketplace listings created by this user
     */
    public function listings()
    {
        return $this->hasMany(\App\Models\MarketplaceListing::class, 'seller_id');
    }
    
    /**
     * Get the orders where this user is the buyer
     */
    public function boughtOrders()
    {
        return $this->hasMany(\App\Models\Order::class, 'buyer_id');
    }
    
    /**
     * Get the orders where this user is the seller
     */
    public function soldOrders()
    {
        return $this->hasMany(\App\Models\Order::class, 'seller_id');
    }
    
    /**
     * Get the transactions for this user
     */
    public function transactions()
    {
        return $this->hasMany(\App\Models\Transaction::class, 'user_id');
    }
    
    /**
     * Get the points history for this user
     */
    public function pointsHistory()
    {
        return $this->hasMany(\App\Models\PointsHistory::class, 'user_id');
    }
}
