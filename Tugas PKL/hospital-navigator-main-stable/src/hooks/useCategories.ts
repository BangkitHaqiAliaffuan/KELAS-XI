/**
 * useCategories Hook
 * Custom hook for fetching and managing categories data
 */

import { useQuery } from "@tanstack/react-query";
import { categoriesApi } from "@/services/api";
import type { Category } from "@/types/category";

const FIVE_MINUTES = 5 * 60 * 1000;

// Fallback static categories (in case API fails)
const STATIC_CATEGORIES: Category[] = [
  {
    name: "Administration",
    description: "Area administrasi dan manajemen rumah sakit",
  },
  {
    name: "Critical Care",
    description: "Unit perawatan intensif untuk pasien dengan kondisi kritis",
  },
  {
    name: "Diagnostic",
    description: "Layanan pemeriksaan penunjang dan diagnostik medis",
  },
  {
    name: "Emergency",
    description: "Layanan gawat darurat dan penanganan kondisi medis kritis yang memerlukan tindakan segera 24 jam",
  },
  {
    name: "Facility",
    description: "Fasilitas umum dan pendukung untuk kenyamanan pasien dan pengunjung",
  },
  {
    name: "Outpatient",
    description: "Layanan konsultasi dan pemeriksaan rawat jalan untuk berbagai spesialisasi medis",
  },
  {
    name: "Service",
    description: "Layanan pendukung medis dan non-medis",
  },
  {
    name: "Surgery",
    description: "Area tindakan operasi dan prosedur bedah",
  },
  {
    name: "Treatment",
    description: "Ruang terapi dan tindakan medis khusus",
  },
  {
    name: "Ward",
    description: "Ruang rawat inap untuk perawatan pasien",
  },
];

/**
 * Hook to fetch all categories with descriptions
 */
export const useCategories = () => {
  return useQuery<Category[]>({
    queryKey: ["categories"],
    queryFn: async () => {
      try {
        const response = await categoriesApi.getAll();
        return response.data.data;
      } catch (error) {
        console.warn("Categories API failed, using static data:", error);
        return STATIC_CATEGORIES;
      }
    },
    staleTime: FIVE_MINUTES,
    retry: 1,
  });
};

/**
 * Hook to fetch category names only
 */
export const useCategoryNames = () => {
  return useQuery<string[]>({
    queryKey: ["categories", "names"],
    queryFn: async () => {
      try {
        const response = await categoriesApi.getNames();
        return response.data.data;
      } catch (error) {
        console.warn("Category names API failed, using static data:", error);
        return STATIC_CATEGORIES.map((c) => c.name).sort();
      }
    },
    staleTime: FIVE_MINUTES,
    retry: 1,
  });
};

/**
 * Hook to fetch a specific category by name
 */
export const useCategoryByName = (name: string) => {
  return useQuery<Category>({
    queryKey: ["category", name],
    queryFn: async () => {
      const response = await categoriesApi.getByName(name);
      return response.data.data;
    },
    enabled: !!name,
    staleTime: FIVE_MINUTES,
  });
};

/**
 * Hook to fetch category statistics
 */
export const useCategoryStats = () => {
  return useQuery<{ total: number; categories: string[] }>({
    queryKey: ["categories", "stats"],
    queryFn: async () => {
      const response = await categoriesApi.getStats();
      return response.data.data;
    },
    staleTime: FIVE_MINUTES,
  });
};

/**
 * Helper hook to get category description by name
 */
export const useCategoryDescription = (categoryName: string) => {
  const { data: categories } = useCategories();
  
  if (!categories) return null;
  
  const category = categories.find((c) => c.name === categoryName);
  return category?.description || null;
};
