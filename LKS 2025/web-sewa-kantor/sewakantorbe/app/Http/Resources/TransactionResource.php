<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class TransactionResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     *
     * @return array<string, mixed>
     */
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->id,
            'booking_code' => $this->booking_code,
            'customer_name' => $this->customer_name,
            'customer_email' => $this->customer_email,
            'customer_phone' => $this->customer_phone,
            'start_date' => $this->start_date,
            'end_date' => $this->end_date,
            'duration_days' => $this->duration_days,
            'rental_type' => $this->rental_type,
            'price_per_unit' => $this->price_per_unit,
            'total_amount' => $this->total_amount,
            'tax_amount' => $this->tax_amount,
            'discount_amount' => $this->discount_amount,
            'final_amount' => $this->final_amount,
            'payment_status' => $this->payment_status,
            'payment_method' => $this->payment_method,
            'payment_reference' => $this->payment_reference,
            'payment_date' => $this->payment_date,
            'notes' => $this->notes,
            'status' => $this->status,
            'status_badge' => $this->status_badge,
            'payment_status_badge' => $this->payment_status_badge,
            'office' => new OfficeResource($this->whenLoaded('office')),
            'user' => $this->whenLoaded('user', function () {
                return [
                    'id' => $this->user->id,
                    'name' => $this->user->name,
                    'email' => $this->user->email,
                ];
            }),
            'created_at' => $this->created_at,
            'updated_at' => $this->updated_at,
        ];
    }
}
