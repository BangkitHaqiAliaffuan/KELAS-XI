<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class UserResource extends JsonResource
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
            'name' => $this->name,
            'email' => $this->email,
            'phone' => $this->phone,
            'address' => $this->address,
            'email_verified_at' => $this->email_verified_at,
            'is_active' => !is_null($this->email_verified_at),
            'status_badge' => $this->getStatusBadge(),
            'transactions_count' => $this->whenLoaded('transactions', function () {
                return $this->transactions->count();
            }),
            'active_transactions_count' => $this->whenLoaded('transactions', function () {
                return $this->transactions->whereIn('status', ['confirmed', 'pending'])->count();
            }),
            'total_spent' => $this->whenLoaded('transactions', function () {
                return $this->transactions->where('payment_status', 'paid')->sum('final_amount');
            }),
            'created_at' => $this->created_at,
            'updated_at' => $this->updated_at,
        ];
    }

    /**
     * Get status badge for the user
     */
    private function getStatusBadge(): array
    {
        if ($this->email_verified_at) {
            return [
                'text' => 'Active',
                'color' => 'green'
            ];
        } else {
            return [
                'text' => 'Inactive',
                'color' => 'red'
            ];
        }
    }
}
