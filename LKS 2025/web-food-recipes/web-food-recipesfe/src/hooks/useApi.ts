import { useState, useEffect } from 'react';
import { recipesApi } from '../lib/api';
import type { 
  Category, 
  Recipe, 
  SearchParams, 
  ApiResponse,
  PaginatedResponse 
} from '../types';

// Hook for fetching categories
export const useCategories = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        setLoading(true);
        const response = await recipesApi.getCategories();
        setCategories(response.data);
      } catch (err) {
        setError('Failed to fetch categories');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchCategories();
  }, []);

  return { categories, loading, error };
};

// Hook for fetching featured recipes
export const useFeaturedRecipes = () => {
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchFeaturedRecipes = async () => {
      try {
        setLoading(true);
        const response = await recipesApi.getFeaturedRecipes();
        setRecipes(response.data);
      } catch (err) {
        setError('Failed to fetch featured recipes');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchFeaturedRecipes();
  }, []);

  return { recipes, loading, error };
};

// Hook for fetching recipes with pagination
export const useRecipes = (params?: SearchParams) => {
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [meta, setMeta] = useState<PaginatedResponse<Recipe>['meta'] | null>(null);

  useEffect(() => {
    const fetchRecipes = async () => {
      try {
        setLoading(true);
        const response = await recipesApi.getRecipes(params);
        setRecipes(response.data);
        setMeta(response.meta);
      } catch (err) {
        setError('Failed to fetch recipes');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchRecipes();
  }, [params?.page, params?.category_id, params?.featured, params?.per_page]);

  return { recipes, loading, error, meta };
};

// Hook for fetching single recipe by slug
export const useRecipe = (slug: string | undefined) => {
  const [recipe, setRecipe] = useState<Recipe | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!slug) {
      setLoading(false);
      return;
    }

    const fetchRecipe = async () => {
      try {
        setLoading(true);
        const response = await recipesApi.getRecipeBySlug(slug);
        setRecipe(response.data);
      } catch (err) {
        setError('Failed to fetch recipe');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchRecipe();
  }, [slug]);

  return { recipe, loading, error };
};

// Hook for fetching single category by slug
export const useCategory = (slug: string | undefined) => {
  const [category, setCategory] = useState<Category | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!slug) {
      setLoading(false);
      return;
    }

    const fetchCategory = async () => {
      try {
        setLoading(true);
        const response = await recipesApi.getCategoryBySlug(slug);
        setCategory(response.data);
      } catch (err) {
        setError('Failed to fetch category');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchCategory();
  }, [slug]);

  return { category, loading, error };
};

// Hook for search functionality
export const useSearch = () => {
  const [results, setResults] = useState<Recipe[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [meta, setMeta] = useState<PaginatedResponse<Recipe>['meta'] | null>(null);

  const search = async (params: SearchParams) => {
    if (!params.q || params.q.trim() === '') {
      setResults([]);
      setMeta(null);
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const response = await recipesApi.searchRecipes(params);
      setResults(response.data);
      setMeta(response.meta);
    } catch (err) {
      setError('Failed to search recipes');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const clearSearch = () => {
    setResults([]);
    setMeta(null);
    setError(null);
  };

  return { results, loading, error, meta, search, clearSearch };
};