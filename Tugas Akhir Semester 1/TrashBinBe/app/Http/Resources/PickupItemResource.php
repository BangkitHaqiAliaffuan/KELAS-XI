<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class PickupItemResource extends JsonResource
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
            'waste_category' => new WasteCategoryResource($this->whenLoaded('wasteCategory')),
            'estimated_weight' => $this->estimated_weight,
            'actual_weight' => $this->actual_weight,
            'photo_url' => $this->photo_url,
            'price_per_unit' => $this->price_per_unit,
            'subtotal' => $this->subtotal,
            'created_at' => $this->created_at?->toISOString(),
            'updated_at' => $this->updated_at?->toISOString(),
        ];
    }
}
