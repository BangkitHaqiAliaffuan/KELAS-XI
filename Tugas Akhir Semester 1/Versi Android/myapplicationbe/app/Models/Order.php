<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Order extends Model
{
    use HasFactory;

    protected $fillable = [
        'buyer_id',
        'listing_id',
        'courier_id',
        'status',
        'total_price',
        'quantity',
        'notes',
        'cart_checkout_id',
        'shipping_address',
        'latitude',
        'longitude',
        'mayar_payment_id',
        'mayar_payment_link',
        'payment_status',
        'paid_at',
        'confirmed_at',
        'searching_at',
        'shipped_at',
        'completed_at',
        'cancelled_at',
        'cancellation_reason',
        'courier_rating',
        'courier_review',
        'listing_rating',
        'listing_review',
        'rated_at',
    ];

    protected $casts = [
        'total_price'    => 'integer',
        'quantity'       => 'integer',
        'latitude'       => 'float',
        'longitude'      => 'float',
        'courier_rating' => 'integer',
        'listing_rating' => 'integer',
        'paid_at'        => 'datetime',
        'confirmed_at'   => 'datetime',
        'searching_at'   => 'datetime',
        'shipped_at'     => 'datetime',
        'completed_at'   => 'datetime',
        'cancelled_at'   => 'datetime',
        'rated_at'       => 'datetime',
    ];

    // ── Payment helpers ────────────────────────────────────────────
    public function isPaid(): bool          { return $this->payment_status === 'paid'; }
    public function isPaymentClosed(): bool { return $this->payment_status === 'closed'; }
    public function isRated(): bool         { return $this->rated_at !== null; }

    // ── Relationships ─────────────────────────────────────────────

    public function buyer(): BelongsTo
    {
        return $this->belongsTo(User::class, 'buyer_id');
    }

    public function listing(): BelongsTo
    {
        return $this->belongsTo(MarketplaceListing::class, 'listing_id');
    }

    public function courier(): BelongsTo
    {
        return $this->belongsTo(Courier::class, 'courier_id');
    }

    // ── Status helpers ────────────────────────────────────────────

    /** True when the order is still awaiting action (payment polling is active) */
    public function isPending(): bool    { return in_array($this->status, ['pending', 'searching']); }
    public function isConfirmed(): bool  { return $this->status === 'confirmed'; }
    public function isSearching(): bool  { return $this->status === 'searching'; }
    public function isShipped(): bool    { return $this->status === 'shipped'; }
    public function isCompleted(): bool  { return $this->status === 'completed'; }
    public function isCancelled(): bool  { return $this->status === 'cancelled'; }
}
