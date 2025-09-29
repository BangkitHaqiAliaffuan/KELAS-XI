<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Support\Str;
use Carbon\Carbon;

class Transaction extends Model
{
    protected $fillable = [
        'booking_code',
        'customer_name',
        'customer_email',
        'customer_phone',
        'office_id',
        'user_id',
        'start_date',
        'end_date',
        'duration_days',
        'rental_type',
        'price_per_unit',
        'total_amount',
        'tax_amount',
        'discount_amount',
        'final_amount',
        'payment_status',
        'payment_method',
        'payment_reference',
        'payment_date',
        'notes',
        'status',
    ];

    protected $casts = [
        'start_date' => 'date',
        'end_date' => 'date',
        'payment_date' => 'datetime',
        'price_per_unit' => 'decimal:2',
        'total_amount' => 'decimal:2',
        'tax_amount' => 'decimal:2',
        'discount_amount' => 'decimal:2',
        'final_amount' => 'decimal:2',
    ];

    protected static function boot()
    {
        parent::boot();

        static::creating(function ($transaction) {
            if (empty($transaction->booking_code)) {
                $transaction->booking_code = 'BK' . strtoupper(Str::random(8));
            }

            // Calculate duration days
            if ($transaction->start_date && $transaction->end_date) {
                $transaction->duration_days = Carbon::parse($transaction->start_date)
                    ->diffInDays(Carbon::parse($transaction->end_date)) + 1;
            }
        });
    }

    public function office(): BelongsTo
    {
        return $this->belongsTo(Office::class);
    }

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    // Accessors
    public function getFormattedBookingCodeAttribute(): string
    {
        return $this->booking_code;
    }

    public function getStatusBadgeAttribute(): string
    {
        return match($this->status) {
            'confirmed' => 'success',
            'cancelled' => 'danger',
            'completed' => 'info',
            default => 'warning'
        };
    }

    public function getPaymentStatusBadgeAttribute(): string
    {
        return match($this->payment_status) {
            'paid' => 'success',
            'failed' => 'danger',
            'cancelled' => 'secondary',
            'refunded' => 'info',
            default => 'warning'
        };
    }
}
