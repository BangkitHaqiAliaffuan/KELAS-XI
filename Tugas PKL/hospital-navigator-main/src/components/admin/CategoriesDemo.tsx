/**
 * CategoriesDemo Component
 * Demo component to showcase categories integration
 */

import { useCategories, useCategoryStats } from "@/hooks/useCategories";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { CheckCircle2, XCircle, Info } from "lucide-react";
import { CategoryBadge } from "@/components/hospital/CategoryBadge";

export const CategoriesDemo = () => {
  const { data: categories, isLoading, error } = useCategories();
  const { data: stats } = useCategoryStats();

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Categories</CardTitle>
          <CardDescription>Loading categories...</CardDescription>
        </CardHeader>
        <CardContent className="space-y-3">
          {[1, 2, 3, 4, 5].map((i) => (
            <Skeleton key={i} className="h-20 w-full" />
          ))}
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Alert variant="destructive">
        <XCircle className="h-4 w-4" />
        <AlertTitle>Error</AlertTitle>
        <AlertDescription>
          Failed to load categories from API. Using fallback data.
        </AlertDescription>
      </Alert>
    );
  }

  return (
    <div className="space-y-4">
      {/* Success Alert */}
      <Alert>
        <CheckCircle2 className="h-4 w-4" />
        <AlertTitle>Categories Loaded Successfully</AlertTitle>
        <AlertDescription>
          Loaded {categories?.length || 0} categories from backend API
          {stats && ` (Total: ${stats.total})`}
        </AlertDescription>
      </Alert>

      {/* Categories Grid */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {categories?.map((category) => (
          <Card key={category.name} className="hover:shadow-md transition-shadow">
            <CardHeader>
              <div className="flex items-center justify-between">
                <CategoryBadge categoryName={category.name} variant="default" />
              </div>
            </CardHeader>
            <CardContent>
              <p className="text-sm text-muted-foreground">
                {category.description}
              </p>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* API Info */}
      <Alert>
        <Info className="h-4 w-4" />
        <AlertTitle>API Integration</AlertTitle>
        <AlertDescription className="space-y-2">
          <p>Categories are now loaded from the backend API:</p>
          <code className="block bg-muted p-2 rounded text-xs">
            GET {import.meta.env.VITE_API_URL}/categories
          </code>
          <p className="text-xs mt-2">
            Each category includes a name and description. Hover over category badges
            throughout the app to see descriptions.
          </p>
        </AlertDescription>
      </Alert>
    </div>
  );
};

export default CategoriesDemo;
