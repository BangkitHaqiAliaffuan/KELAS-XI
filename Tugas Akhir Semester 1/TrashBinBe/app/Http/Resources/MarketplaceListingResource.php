<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class MarketplaceListingResource extends JsonResource
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
            'seller' => new UserResource($this->whenLoaded('seller')),
            'waste_category' => new WasteCategoryResource($this->whenLoaded('wasteCategory')),
            'title' => $this->title,
            'description' => $this->description,
            'quantity' => $this->quantity,
            'unit' => $this->unit,
            'price_per_unit' => $this->price_per_unit,
            'total_price' => $this->total_price,
            'condition' => $this->condition,
            'location' => $this->location,
            'lat' => $this->lat,
            'lng' => $this->lng,
            'status' => $this->status,
            'photos' => $this->photos,
            'views_count' => $this->views_count,
            'expires_at' => $this->expires_at ? $this->expires_at->toISOString() : null,
            'created_at' => $this->created_at?->toISOString(),
            'updated_at' => $this->updated_at?->toISOString(),
        ];
    }
}
