import axios from 'axios';
import type { 
  ApiResponse, 
  PaginatedResponse, 
  Category, 
  Recipe, 
  SearchParams 
} from '../types';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8000/api';
const API_KEY = import.meta.env.VITE_API_KEY || 'food_recipes_secure_api_key_2025';

// Create axios instance
const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    'X-API-Key': API_KEY,
  },
});

// API functions
export const recipesApi = {
  // Categories
  async getCategories(): Promise<ApiResponse<Category[]>> {
    const response = await api.get('/categories');
    return response.data;
  },

  async getCategoryBySlug(slug: string): Promise<ApiResponse<Category>> {
    const response = await api.get(`/categories/${slug}`);
    return response.data;
  },

  // Recipes
  async getRecipes(params?: SearchParams): Promise<PaginatedResponse<Recipe>> {
    const response = await api.get('/recipes', { params });
    return response.data;
  },

  async getFeaturedRecipes(): Promise<ApiResponse<Recipe[]>> {
    const response = await api.get('/recipes/featured');
    return response.data;
  },

  async getRecipeBySlug(slug: string): Promise<ApiResponse<Recipe>> {
    const response = await api.get(`/recipes/${slug}`);
    return response.data;
  },

  async getRecipesByCategory(
    categorySlug: string, 
    params?: SearchParams
  ): Promise<PaginatedResponse<Recipe>> {
    const response = await api.get(`/categories/${categorySlug}/recipes`, { params });
    return response.data;
  },

  // Search
  async searchRecipes(params: SearchParams): Promise<PaginatedResponse<Recipe>> {
    const response = await api.get('/search', { params });
    return response.data;
  },

  // Health check
  async healthCheck() {
    const response = await api.get('/health');
    return response.data;
  },
};

// Error handler
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export default api;