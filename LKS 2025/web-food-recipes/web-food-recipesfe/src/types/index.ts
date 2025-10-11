// Base interfaces
export interface BaseEntity {
  id: number;
  created_at: string;
  updated_at: string;
}

// Category interface
export interface Category extends BaseEntity {
  name: string;
  slug: string;
  description?: string;
  icon?: string;
  recipes_count?: number;
  recipes?: Recipe[];
}

// Recipe Author interface
export interface RecipeAuthor extends BaseEntity {
  name: string;
  bio?: string;
  email?: string;
  photo?: string;
}

// Ingredient interface
export interface Ingredient extends BaseEntity {
  name: string;
  description?: string;
}

// Recipe Ingredient interface (with quantity and notes)
export interface RecipeIngredient extends BaseEntity {
  quantity: string;
  notes?: string;
  ingredient?: Ingredient;
}

// Recipe Photo interface
export interface RecipePhoto extends BaseEntity {
  photo_path: string;
  alt_text?: string;
  order: number;
}

// Recipe Tutorial interface
export interface RecipeTutorial extends BaseEntity {
  step_number: number;
  title: string;
  instruction: string;
  image?: string;
}

// Recipe interface
export interface Recipe extends BaseEntity {
  name: string;
  slug: string;
  description?: string;
  thumbnail?: string;
  video_url?: string;
  file_url?: string;
  cooking_time?: number;
  servings?: number;
  difficulty: 'easy' | 'medium' | 'hard';
  is_featured: boolean;
  category?: Category;
  recipe_author?: RecipeAuthor;
  ingredients?: RecipeIngredient[];
  photos?: RecipePhoto[];
  recipe_photos?: Array<{ photo_url: string; alt_text?: string; order?: number }>;
  tutorials?: RecipeTutorial[];
}

// API Response interfaces
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
  meta?: {
    current_page: number;
    last_page: number;
    per_page: number;
    total: number;
    query?: string;
  };
}

export interface PaginatedResponse<T> extends ApiResponse<T[]> {
  meta: {
    current_page: number;
    last_page: number;
    per_page: number;
    total: number;
  };
}

// Search and filter interfaces
export interface SearchParams {
  q?: string;
  category_id?: number;
  featured?: boolean;
  per_page?: number;
  page?: number;
}

// Error interface
export interface ApiError {
  success: false;
  message: string;
  errors?: Record<string, string[]>;
}