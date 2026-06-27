/**
 * CategoryFilter Component
 * Filter component for selecting categories
 */

import { useCategories } from "@/hooks/useCategories";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";

interface CategoryFilterProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  showAllOption?: boolean;
  allOptionLabel?: string;
}

export const CategoryFilter = ({
  value,
  onChange,
  placeholder = "Select category",
  showAllOption = true,
  allOptionLabel = "All Categories",
}: CategoryFilterProps) => {
  const { data: categories, isLoading, error } = useCategories();

  if (isLoading) {
    return <Skeleton className="h-10 w-full" />;
  }

  if (error) {
    return (
      <div className="text-sm text-destructive">
        Failed to load categories
      </div>
    );
  }

  return (
    <Select value={value} onValueChange={onChange}>
      <SelectTrigger className="w-full">
        <SelectValue placeholder={placeholder} />
      </SelectTrigger>
      <SelectContent>
        {showAllOption && (
          <SelectItem value="all">{allOptionLabel}</SelectItem>
        )}
        {categories?.map((category) => (
          <SelectItem key={category.name} value={category.name}>
            <div className="flex flex-col items-start">
              <span className="font-medium">{category.name}</span>
              <span className="text-xs text-muted-foreground line-clamp-1">
                {category.description}
              </span>
            </div>
          </SelectItem>
        ))}
      </SelectContent>
    </Select>
  );
};

export default CategoryFilter;
