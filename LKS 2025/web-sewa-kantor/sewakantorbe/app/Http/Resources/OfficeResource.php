<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class OfficeResource extends JsonResource
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
            'description' => $this->description,
            'address' => $this->address,
            'latitude' => $this->latitude,
            'longitude' => $this->longitude,
            'capacity' => $this->capacity,
            'price_per_day' => $this->price_per_day,
            'price_per_week' => $this->price_per_week,
            'price_per_month' => $this->price_per_month,
            'photos' => $this->photos ? array_map(function($photo) {
                return asset('storage/' . $photo);
            }, $this->photos) : [],
            'main_photo' => $this->main_photo ? asset('storage/' . $this->main_photo) : null,
            'status' => $this->status,
            'operating_hours' => $this->operating_hours,
            'rating' => $this->rating,
            'total_reviews' => $this->total_reviews,
            'city' => new CityResource($this->whenLoaded('city')),
            'facilities' => FacilityResource::collection($this->whenLoaded('facilities')),
            'price_for_duration' => $this->price_for_duration,
            'created_at' => $this->created_at,
            'updated_at' => $this->updated_at,
        ];
    }
}
