/**
 * Category Service
 * Handles all category-related API calls
 */

import { categoriesApi } from "@/services/api";
import type {
  Category,
  CategoryResponse,
  CategoryNamesResponse,
  CategoryStatsResponse,
  CategoryValidationResponse,
} from "@/types/category";

interface ApiResponse<T> {
  success: boolean;
  data: T;
  count?: number;
  message?: string;
}

const readData = <T>(response: { data: ApiResponse<T> }): T => response.data.data;

/**
 * Get all categories with descriptions
 */
export const getAllCategories = async (): Promise<Category[]> => {
  return readData<Category[]>(await categoriesApi.getAll());
};

/**
 * Get category names only (without descriptions)
 */
export const getCategoryNames = async (): Promise<string[]> => {
  return readData<string[]>(await categoriesApi.getNames());
};

/**
 * Get category statistics
 */
export const getCategoryStats = async (): Promise<{ total: number; categories: string[] }> => {
  return readData<{ total: number; categories: string[] }>(await categoriesApi.getStats());
};

/**
 * Get a specific category by name
 */
export const getCategoryByName = async (name: string): Promise<Category> => {
  return readData<Category>(await categoriesApi.getByName(name));
};

/**
 * Validate if a category name exists
 */
export const validateCategory = async (name: string): Promise<boolean> => {
  try {
    const response = await categoriesApi.validate(name);
    return response.data.success;
  } catch (error) {
    console.error("Category validation error:", error);
    return false;
  }
};

/**
 * Get category description by name
 */
export const getCategoryDescription = async (name: string): Promise<string | null> => {
  try {
    const category = await getCategoryByName(name);
    return category.description;
  } catch (error) {
    console.error("Failed to get category description:", error);
    return null;
  }
};

export const categoryService = {
  getAllCategories,
  getCategoryNames,
  getCategoryStats,
  getCategoryByName,
  validateCategory,
  getCategoryDescription,
};

export default categoryService;
