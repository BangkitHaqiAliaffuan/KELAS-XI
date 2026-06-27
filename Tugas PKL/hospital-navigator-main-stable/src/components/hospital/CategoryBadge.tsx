/**
 * CategoryBadge Component
 * Displays category badge with description tooltip
 */

import { Badge } from "@/components/ui/badge";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { useCategoryDescription } from "@/hooks/useCategories";

interface CategoryBadgeProps {
  categoryName: string;
  variant?: "default" | "secondary" | "outline" | "destructive";
  className?: string;
  showTooltip?: boolean;
}

export const CategoryBadge = ({
  categoryName,
  variant = "outline",
  className = "",
  showTooltip = true,
}: CategoryBadgeProps) => {
  const description = useCategoryDescription(categoryName);

  if (!showTooltip || !description) {
    return (
      <Badge variant={variant} className={className}>
        {categoryName}
      </Badge>
    );
  }

  return (
    <TooltipProvider>
      <Tooltip>
        <TooltipTrigger asChild>
          <Badge variant={variant} className={`cursor-help ${className}`}>
            {categoryName}
          </Badge>
        </TooltipTrigger>
        <TooltipContent className="max-w-xs">
          <p className="text-sm">{description}</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
};

export default CategoryBadge;
