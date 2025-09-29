<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class CityResource extends JsonResource
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
            'slug' => $this->slug,
            'photo' => $this->photo,
            'description' => $this->description,
            'offices_count' => $this->when(
                $this->relationLoaded('offices'),
                $this->offices_count ?? $this->offices->count()
            ),
            'available_offices_count' => $this->when(
                $this->relationLoaded('availableOffices'),
                $this->available_offices_count ?? $this->availableOffices->count()
            ),
            'created_at' => $this->created_at,
            'updated_at' => $this->updated_at,
        ];
    }
}
