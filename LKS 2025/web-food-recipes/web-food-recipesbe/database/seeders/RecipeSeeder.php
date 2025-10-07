<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class RecipeSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Create Categories
        $categories = [
            ['name' => 'Main Course', 'slug' => 'main-course', 'description' => 'Hearty main dishes for every occasion'],
            ['name' => 'Desserts', 'slug' => 'desserts', 'description' => 'Sweet treats and desserts'],
            ['name' => 'Appetizers', 'slug' => 'appetizers', 'description' => 'Light starters and appetizers'],
            ['name' => 'Beverages', 'slug' => 'beverages', 'description' => 'Refreshing drinks and beverages'],
            ['name' => 'Salads', 'slug' => 'salads', 'description' => 'Fresh and healthy salads'],
        ];

        foreach ($categories as $categoryData) {
            \App\Models\Category::create($categoryData);
        }

        // Create Recipe Authors
        $authors = [
            ['name' => 'Chef Gordon', 'bio' => 'Professional chef with 15 years experience', 'email' => 'gordon@example.com'],
            ['name' => 'Maria Rodriguez', 'bio' => 'Home cooking specialist', 'email' => 'maria@example.com'],
            ['name' => 'John Smith', 'bio' => 'Pastry chef and dessert expert', 'email' => 'john@example.com'],
        ];

        foreach ($authors as $authorData) {
            \App\Models\RecipeAuthor::create($authorData);
        }

        // Create Ingredients
        $ingredients = [
            ['name' => 'Chicken Breast', 'description' => 'Boneless chicken breast'],
            ['name' => 'Rice', 'description' => 'Jasmine white rice'],
            ['name' => 'Garlic', 'description' => 'Fresh garlic cloves'],
            ['name' => 'Onion', 'description' => 'Yellow onion'],
            ['name' => 'Tomato', 'description' => 'Fresh tomatoes'],
            ['name' => 'Flour', 'description' => 'All-purpose flour'],
            ['name' => 'Sugar', 'description' => 'Granulated white sugar'],
            ['name' => 'Butter', 'description' => 'Unsalted butter'],
            ['name' => 'Eggs', 'description' => 'Large eggs'],
            ['name' => 'Milk', 'description' => 'Whole milk'],
        ];

        foreach ($ingredients as $ingredientData) {
            \App\Models\Ingredient::create($ingredientData);
        }

        // Create Sample Recipes
        $recipes = [
            [
                'name' => 'Grilled Chicken with Rice',
                'slug' => 'grilled-chicken-with-rice',
                'description' => 'Delicious grilled chicken served with fragrant jasmine rice',
                'cooking_time' => 45,
                'servings' => 4,
                'difficulty' => 'easy',
                'is_featured' => true,
                'category_id' => 1,
                'recipe_author_id' => 1,
            ],
            [
                'name' => 'Chocolate Cake',
                'slug' => 'chocolate-cake',
                'description' => 'Rich and moist chocolate cake perfect for celebrations',
                'cooking_time' => 60,
                'servings' => 8,
                'difficulty' => 'medium',
                'is_featured' => true,
                'category_id' => 2,
                'recipe_author_id' => 3,
            ],
            [
                'name' => 'Caesar Salad',
                'slug' => 'caesar-salad',
                'description' => 'Classic Caesar salad with fresh romaine and homemade dressing',
                'cooking_time' => 15,
                'servings' => 2,
                'difficulty' => 'easy',
                'is_featured' => false,
                'category_id' => 5,
                'recipe_author_id' => 2,
            ],
        ];

        foreach ($recipes as $recipeData) {
            $recipe = \App\Models\Recipe::create($recipeData);

            // Add sample ingredients for first recipe
            if ($recipe->id === 1) {
                \App\Models\RecipeIngredient::create([
                    'recipe_id' => $recipe->id,
                    'ingredient_id' => 1,
                    'quantity' => '500g',
                    'notes' => 'Cut into strips'
                ]);
                \App\Models\RecipeIngredient::create([
                    'recipe_id' => $recipe->id,
                    'ingredient_id' => 2,
                    'quantity' => '2 cups',
                    'notes' => 'Wash before cooking'
                ]);

                // Add sample tutorial steps
                \App\Models\RecipeTutorial::create([
                    'recipe_id' => $recipe->id,
                    'step_number' => 1,
                    'title' => 'Prepare the chicken',
                    'instruction' => 'Season the chicken breast with salt and pepper, then cut into strips.'
                ]);
                \App\Models\RecipeTutorial::create([
                    'recipe_id' => $recipe->id,
                    'step_number' => 2,
                    'title' => 'Cook the rice',
                    'instruction' => 'Rinse rice until water runs clear, then cook in rice cooker with 1:1.5 ratio of rice to water.'
                ]);
                \App\Models\RecipeTutorial::create([
                    'recipe_id' => $recipe->id,
                    'step_number' => 3,
                    'title' => 'Grill the chicken',
                    'instruction' => 'Heat grill pan over medium-high heat. Cook chicken strips for 3-4 minutes per side until golden brown.'
                ]);
            }
        }
    }
}
