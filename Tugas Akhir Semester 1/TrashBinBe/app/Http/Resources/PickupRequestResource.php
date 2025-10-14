<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class PickupRequestResource extends JsonResource
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
            'user' => new UserResource($this->whenLoaded('user')),
            'collector' => new UserResource($this->whenLoaded('collector')),
            'pickup_address' => $this->pickup_address,
            'pickup_lat' => $this->pickup_lat,
            'pickup_lng' => $this->pickup_lng,
            'scheduled_date' => $this->scheduled_date?->toISOString(),
            'status' => $this->status,
            'total_weight' => $this->total_weight,
            'total_price' => $this->total_price,
            'notes' => $this->notes,
            'rating' => $this->rating,
            'review' => $this->review,
            'items' => PickupItemResource::collection($this->whenLoaded('items')),
            'created_at' => $this->created_at?->toISOString(),
            'updated_at' => $this->updated_at?->toISOString(),
        ];
    }
}
