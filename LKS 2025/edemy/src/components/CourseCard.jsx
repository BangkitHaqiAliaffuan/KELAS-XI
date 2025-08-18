import React from 'react';
import { Link } from 'react-router-dom';
import { Clock, Users, PlayCircle, DollarSign } from 'lucide-react';
import StarRating from './StarRating.jsx';
import { useApp } from '../context/AppContext.jsx';
import { formatPrice, calculateTotalDuration, countTotalLectures } from '../utils/helpers.js';

const CourseCard = ({ course, className = "" }) => {
  const { getAverageRating, getDiscountedPrice } = useApp();
  
  const averageRating = parseFloat(getAverageRating(course.courseRatings));
  const discountedPrice = getDiscountedPrice(course.coursePrice, course.discount);
  const totalDuration = calculateTotalDuration(course.courseContent);
  const totalLectures = countTotalLectures(course.courseContent);
  const enrolledCount = course.enrolledStudents ? course.enrolledStudents.length : 0;

  return (
    <div className={`bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300 overflow-hidden ${className}`}>
      {/* Course Thumbnail */}
      <div className="relative">
        <Link to={`/course/${course._id}`}>
          <div className="aspect-video bg-gray-200 overflow-hidden">
            <img 
              src={course.courseThumbnail || '/api/placeholder/400/225'} 
              alt={course.courseTitle}
              className="w-full h-full object-cover hover:scale-105 transition-transform duration-300"
            />
          </div>
          <div className="absolute top-3 right-3 bg-black bg-opacity-50 rounded-full p-2">
            <PlayCircle className="h-6 w-6 text-white" />
          </div>
        </Link>
        
        {/* Discount Badge */}
        {course.discount > 0 && (
          <div className="absolute top-3 left-3 bg-red-500 text-white px-2 py-1 rounded text-xs font-semibold">
            {course.discount}% OFF
          </div>
        )}
      </div>

      {/* Course Content */}
      <div className="p-4">
        {/* Course Title */}
        <Link to={`/course/${course._id}`}>
          <h3 className="text-lg font-semibold text-gray-900 mb-2 line-clamp-2 hover:text-blue-600 transition-colors">
            {course.courseTitle}
          </h3>
        </Link>

        {/* Course Description */}
        <div 
          className="text-gray-600 text-sm mb-3 line-clamp-2"
          dangerouslySetInnerHTML={{ 
            __html: course.courseDescription.replace(/<[^>]*>/g, '').slice(0, 100) + '...' 
          }}
        />

        {/* Course Stats */}
        <div className="flex items-center space-x-4 text-xs text-gray-500 mb-3">
          <div className="flex items-center space-x-1">
            <Clock className="h-3 w-3" />
            <span>{totalDuration}</span>
          </div>
          <div className="flex items-center space-x-1">
            <PlayCircle className="h-3 w-3" />
            <span>{totalLectures} lectures</span>
          </div>
          <div className="flex items-center space-x-1">
            <Users className="h-3 w-3" />
            <span>{enrolledCount} students</span>
          </div>
        </div>

        {/* Rating */}
        <div className="flex items-center justify-between mb-3">
          <StarRating 
            rating={averageRating} 
            size={14} 
            showRating={true}
            className="text-xs"
          />
          <span className="text-xs text-gray-500">
            ({course.courseRatings ? course.courseRatings.length : 0} reviews)
          </span>
        </div>

        {/* Price */}
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            {course.discount > 0 ? (
              <>
                <span className="text-lg font-bold text-gray-900">
                  {formatPrice(discountedPrice)}
                </span>
                <span className="text-sm text-gray-500 line-through">
                  {formatPrice(course.coursePrice)}
                </span>
              </>
            ) : (
              <span className="text-lg font-bold text-gray-900">
                {formatPrice(course.coursePrice)}
              </span>
            )}
          </div>
          
          {/* Enroll Button */}
          <Link 
            to={`/course/${course._id}`}
            className="bg-blue-600 text-white px-3 py-1.5 rounded text-sm font-medium hover:bg-blue-700 transition-colors"
          >
            View Course
          </Link>
        </div>
      </div>
    </div>
  );
};

export default CourseCard;
