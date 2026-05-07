/**
 * Category Type Definitions
 * Represents hospital room categories with descriptions
 */

export interface Category {
  name: string;
  description: string;
}

export interface CategoryResponse {
  success: boolean;
  data: Category[];
  count: number;
}

export interface CategoryNamesResponse {
  success: boolean;
  data: string[];
  count: number;
}

export interface CategoryStatsResponse {
  success: boolean;
  data: {
    total: number;
    categories: string[];
  };
}

export interface CategoryValidationResponse {
  success: boolean;
  message: string;
  data?: Category;
}

// Available category names as const for type safety
export const CATEGORY_NAMES = [
  "Administration",
  "Critical Care",
  "Diagnostic",
  "Emergency",
  "Facility",
  "Outpatient",
  "Service",
  "Surgery",
  "Treatment",
  "Ward",
] as const;

export type CategoryName = (typeof CATEGORY_NAMES)[number];
