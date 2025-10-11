<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class RecipeResource extends JsonResource
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
            'thumbnail' => $this->thumbnail,
            'video_url' => $this->video_url,
            'file_url' => $this->file_url,
            'cooking_time' => $this->cooking_time,
            'servings' => $this->servings,
            'difficulty' => $this->difficulty,
            'is_featured' => $this->is_featured,
            'category' => new CategoryResource($this->whenLoaded('category')),
            'recipe_author' => new RecipeAuthorResource($this->whenLoaded('recipeAuthor')),
            'ingredients' => RecipeIngredientResource::collection($this->whenLoaded('recipeIngredients')),
            'photos' => RecipePhotoResource::collection($this->whenLoaded('recipePhotos')),
            'recipe_photos' => $this->whenLoaded('recipePhotos', function() {
                return $this->recipePhotos->map(function($photo) {
                    return [
                        'photo_url' => $photo->photo_path,
                        'alt_text' => $photo->alt_text,
                        'order' => $photo->order,
                    ];
                });
            }),
            'tutorials' => RecipeTutorialResource::collection($this->whenLoaded('recipeTutorials')),
            'created_at' => $this->created_at?->toISOString(),
            'updated_at' => $this->updated_at?->toISOString(),
        ];
    }
}
