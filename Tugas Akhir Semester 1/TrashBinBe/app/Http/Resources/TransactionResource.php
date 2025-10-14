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
            'type' => $this->type,
            'reference_id' => $this->reference_id,
            'reference_type' => $this->reference_type,
            'amount' => $this->amount,
            'points_earned' => $this->points_earned,
            'description' => $this->description,
            'created_at' => $this->created_at?->toISOString(),
        ];
    }
}
