<?php

namespace Database\Seeders;

use App\Models\User;
use App\Models\Category;
use App\Models\RecipeAuthor;
use App\Models\Recipe;
use App\Models\Ingredient;
use App\Models\RecipeIngredient;
use App\Models\RecipePhoto;
use App\Models\RecipeTutorial;
use Illuminate\Database\Seeder;
use Illuminate\Support\Str;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        // Create test user
        User::factory()->create([
            'name' => 'Test User',
            'email' => 'test@example.com',
        ]);

        // Create Categories
        $categories = [
            [
                'name' => 'Appetizer',
                'description' => 'Start your meal with these delicious appetizers and small bites',
                'icon' => '🥗'
            ],
            [
                'name' => 'Main Course',
                'description' => 'Hearty main dishes that will satisfy your hunger',
                'icon' => '🍽️'
            ],
            [
                'name' => 'Dessert',
                'description' => 'Sweet treats to end your meal on a perfect note',
                'icon' => '🍰'
            ],
            [
                'name' => 'Breakfast',
                'description' => 'Start your day right with these breakfast recipes',
                'icon' => '🍳'
            ],
            [
                'name' => 'Soup',
                'description' => 'Warm and comforting soups for any occasion',
                'icon' => '🍲'
            ],
            [
                'name' => 'Salad',
                'description' => 'Fresh and healthy salad recipes',
                'icon' => '🥗'
            ],
            [
                'name' => 'Seafood',
                'description' => 'Fresh seafood dishes from the ocean',
                'icon' => '🐟'
            ],
            [
                'name' => 'Vegetarian',
                'description' => 'Delicious plant-based recipes',
                'icon' => '🥬'
            ]
        ];

        foreach ($categories as $categoryData) {
            Category::create([
                'name' => $categoryData['name'],
                'slug' => Str::slug($categoryData['name']),
                'description' => $categoryData['description'],
                'icon' => $categoryData['icon']
            ]);
        }

        // Create Recipe Authors
        $authors = [
            [
                'name' => 'Chef Marco Italian',
                'bio' => 'Expert in Italian cuisine with 15 years of experience',
                'email' => 'marco@example.com',
                'photo' => 'https://images.unsplash.com/photo-1583394293214-28ded15ee548?w=150&h=150&fit=crop&crop=face'
            ],
            [
                'name' => 'Sarah Kitchen Master',
                'bio' => 'Home cooking specialist and cookbook author',
                'email' => 'sarah@example.com',
                'photo' => 'https://images.unsplash.com/photo-1494790108755-2616b332c913?w=150&h=150&fit=crop&crop=face'
            ],
            [
                'name' => 'Chef David Asian',
                'bio' => 'Asian cuisine expert specializing in fusion cooking',
                'email' => 'david@example.com',
                'photo' => 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face'
            ],
            [
                'name' => 'Maria Healthy Cook',
                'bio' => 'Nutritionist and healthy cooking advocate',
                'email' => 'maria@example.com',
                'photo' => 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face'
            ]
        ];

        foreach ($authors as $authorData) {
            RecipeAuthor::create($authorData);
        }

        // Create Ingredients
        $ingredients = [
            'Salt', 'Black Pepper', 'Olive Oil', 'Garlic', 'Onion', 'Tomato', 'Basil',
            'Oregano', 'Thyme', 'Rosemary', 'Parsley', 'Lemon', 'Butter', 'Flour',
            'Sugar', 'Eggs', 'Milk', 'Cheese', 'Chicken Breast', 'Beef',
            'Salmon', 'Shrimp', 'Rice', 'Pasta', 'Bread', 'Lettuce', 'Cucumber',
            'Carrot', 'Bell Pepper', 'Mushroom', 'Spinach', 'Potato'
        ];

        foreach ($ingredients as $ingredient) {
            Ingredient::create([
                'name' => $ingredient,
                'description' => "Fresh {$ingredient} for cooking"
            ]);
        }

        // Create Sample Recipes
        $categories = Category::all();
        $authors = RecipeAuthor::all();
        $allIngredients = Ingredient::all();

        $recipes = [
            [
                'name' => 'Caesar Salad',
                'description' => 'Classic Caesar salad with crispy croutons and parmesan cheese',
                'difficulty' => 'easy',
                'cooking_time' => 15,
                'servings' => 4,
                'is_featured' => true,
                'category' => 'Appetizer',
                'thumbnail' => 'https://images.unsplash.com/photo-1551248429-40975aa4de74?w=400&h=300&fit=crop'
            ],
            [
                'name' => 'Spaghetti Carbonara',
                'description' => 'Traditional Italian pasta dish with eggs, cheese, and pancetta',
                'difficulty' => 'medium',
                'cooking_time' => 25,
                'servings' => 4,
                'is_featured' => true,
                'category' => 'Main Course',
                'thumbnail' => 'https://images.unsplash.com/photo-1621996346565-e3dbc353d946?w=400&h=300&fit=crop'
            ],
            [
                'name' => 'Chocolate Lava Cake',
                'description' => 'Decadent chocolate cake with molten chocolate center',
                'difficulty' => 'hard',
                'cooking_time' => 30,
                'servings' => 2,
                'is_featured' => true,
                'category' => 'Dessert',
                'thumbnail' => 'https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400&h=300&fit=crop'
            ],
            [
                'name' => 'Avocado Toast',
                'description' => 'Healthy breakfast with avocado, eggs, and seasonings',
                'difficulty' => 'easy',
                'cooking_time' => 10,
                'servings' => 2,
                'is_featured' => false,
                'category' => 'Breakfast',
                'thumbnail' => 'https://images.unsplash.com/photo-1541519227354-08fa5d50c44d?w=400&h=300&fit=crop'
            ],
            [
                'name' => 'Tomato Basil Soup',
                'description' => 'Creamy tomato soup with fresh basil',
                'difficulty' => 'easy',
                'cooking_time' => 35,
                'servings' => 4,
                'is_featured' => false,
                'category' => 'Soup',
                'thumbnail' => 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400&h=300&fit=crop'
            ],
            [
                'name' => 'Greek Salad',
                'description' => 'Fresh Mediterranean salad with feta cheese and olives',
                'difficulty' => 'easy',
                'cooking_time' => 10,
                'servings' => 4,
                'is_featured' => false,
                'category' => 'Salad',
                'thumbnail' => 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400&h=300&fit=crop'
            ],
            [
                'name' => 'Grilled Salmon',
                'description' => 'Perfectly grilled salmon with lemon and herbs',
                'difficulty' => 'medium',
                'cooking_time' => 20,
                'servings' => 4,
                'is_featured' => true,
                'category' => 'Seafood',
                'thumbnail' => 'https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=400&h=300&fit=crop'
            ],
            [
                'name' => 'Quinoa Buddha Bowl',
                'description' => 'Nutritious bowl with quinoa, vegetables, and tahini dressing',
                'difficulty' => 'easy',
                'cooking_time' => 25,
                'servings' => 2,
                'is_featured' => true,
                'category' => 'Vegetarian',
                'thumbnail' => 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&h=300&fit=crop'
            ]
        ];

        foreach ($recipes as $recipeData) {
            $category = $categories->where('name', $recipeData['category'])->first();
            $author = $authors->random();

            $recipe = Recipe::create([
                'name' => $recipeData['name'],
                'slug' => Str::slug($recipeData['name']),
                'description' => $recipeData['description'],
                'thumbnail' => $recipeData['thumbnail'],
                'cooking_time' => $recipeData['cooking_time'],
                'servings' => $recipeData['servings'],
                'difficulty' => $recipeData['difficulty'],
                'is_featured' => $recipeData['is_featured'],
                'category_id' => $category->id,
                'recipe_author_id' => $author->id,
            ]);

            // Add some random ingredients to each recipe
            $recipeIngredients = $allIngredients->random(rand(4, 8));
            foreach ($recipeIngredients as $index => $ingredient) {
                RecipeIngredient::create([
                    'recipe_id' => $recipe->id,
                    'ingredient_id' => $ingredient->id,
                    'quantity' => rand(1, 3) . ' ' . ['cup', 'tbsp', 'tsp', 'piece', 'slice'][rand(0, 4)],
                    'notes' => rand(0, 1) ? 'Fresh' : null,
                ]);
            }

            // Add recipe photos
            RecipePhoto::create([
                'recipe_id' => $recipe->id,
                'photo_path' => $recipeData['thumbnail'],
                'alt_text' => $recipe->name . ' photo',
                'order' => 1,
            ]);

            // Add recipe tutorial steps
            for ($step = 1; $step <= rand(3, 6); $step++) {
                RecipeTutorial::create([
                    'recipe_id' => $recipe->id,
                    'step_number' => $step,
                    'title' => "Step {$step}",
                    'instruction' => "Detailed instruction for step {$step} of {$recipe->name}. Follow this step carefully to achieve the best results.",
                ]);
            }
        }
    }
}
