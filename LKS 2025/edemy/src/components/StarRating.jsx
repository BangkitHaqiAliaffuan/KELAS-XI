import React, { useState } from 'react';
import { Star } from 'lucide-react';

const StarRating = ({ 
  rating = 0, 
  maxRating = 5, 
  size = 16, 
  interactive = false, 
  onRatingChange = null,
  showRating = true,
  className = ""
}) => {
  const [hoverRating, setHoverRating] = useState(0);
  const [selectedRating, setSelectedRating] = useState(rating);

  const handleStarClick = (starValue) => {
    if (!interactive) return;
    
    setSelectedRating(starValue);
    if (onRatingChange) {
      onRatingChange(starValue);
    }
  };

  const handleStarHover = (starValue) => {
    if (!interactive) return;
    setHoverRating(starValue);
  };

  const handleMouseLeave = () => {
    if (!interactive) return;
    setHoverRating(0);
  };

  const currentRating = interactive ? (hoverRating || selectedRating) : rating;

  return (
    <div className={`flex items-center space-x-1 ${className}`}>
      <div className="flex items-center">
        {[...Array(maxRating)].map((_, index) => {
          const starValue = index + 1;
          const isFilled = starValue <= currentRating;
          const isHalfFilled = !isFilled && starValue - 0.5 <= currentRating;
          
          return (
            <button
              key={index}
              type="button"
              className={`relative ${interactive ? 'cursor-pointer' : 'cursor-default'} transition-colors duration-150`}
              onClick={() => handleStarClick(starValue)}
              onMouseEnter={() => handleStarHover(starValue)}
              onMouseLeave={handleMouseLeave}
              disabled={!interactive}
            >
              <Star 
                size={size} 
                className={`${
                  isFilled 
                    ? 'text-yellow-400 fill-current' 
                    : isHalfFilled
                    ? 'text-yellow-400 fill-current'
                    : interactive && hoverRating >= starValue
                    ? 'text-yellow-400 fill-current'
                    : 'text-gray-300'
                } transition-colors duration-150`}
              />
              {isHalfFilled && (
                <Star 
                  size={size} 
                  className="absolute top-0 left-0 text-yellow-400 fill-current"
                  style={{
                    clipPath: 'polygon(0% 0%, 50% 0%, 50% 100%, 0% 100%)'
                  }}
                />
              )}
            </button>
          );
        })}
      </div>
      
      {showRating && (
        <span className="text-sm text-gray-600 font-medium ml-2">
          {currentRating > 0 ? currentRating.toFixed(1) : '0.0'}
        </span>
      )}
    </div>
  );
};

export default StarRating;
