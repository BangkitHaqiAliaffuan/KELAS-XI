import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Clock, Users, PlayCircle, BookOpen } from 'lucide-react';
import StarRating from './StarRating.jsx';
import { assets } from '../assets/assets.js';
import { userAPI } from '../services/api.js';

const CourseCard = ({ course, className = "" }) => {
  const [educatorName, setEducatorName] = useState('Loading...');

  // Fetch educator data
  useEffect(() => {
    const fetchEducatorData = async () => {
      if (course.educator) {
        try {
          const response = await userAPI.getUser(course.educator);
          if (response.success && response.data) {
            const userData = response.data;
            const displayName = userData.name || userData.firstName || userData.email?.split('@')[0] || 'Anonymous Instructor';
            setEducatorName(displayName);
          }
        } catch (error) {
          console.error('Error fetching educator data:', error);
          setEducatorName('Anonymous Instructor');
        }
      } else {
        setEducatorName('Anonymous Instructor');
      }
    };

    fetchEducatorData();
  }, [course.educator]);
  // Format price helper
  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  };

  // Calculate course statistics
  const calculateCourseStats = () => {
    let totalLectures = 0;
    let totalDurationMinutes = 0;
    
    if (course.courseContent && Array.isArray(course.courseContent)) {
      course.courseContent.forEach(chapter => {
        if (chapter.chapterContent && Array.isArray(chapter.chapterContent)) {
          totalLectures += chapter.chapterContent.length;
          chapter.chapterContent.forEach(lecture => {
            // Assuming each lecture has a duration in minutes
            if (lecture.duration) {
              totalDurationMinutes += lecture.duration;
            } else {
              // Default duration if not specified
              totalDurationMinutes += 10; // 10 minutes default
            }
          });
        }
      });
    }
    
    // Format duration
    const hours = Math.floor(totalDurationMinutes / 60);
    const minutes = totalDurationMinutes % 60;
    let formattedDuration = '';
    
    if (hours > 0) {
      formattedDuration = `${hours}h`;
      if (minutes > 0) {
        formattedDuration += ` ${minutes}m`;
      }
    } else {
      formattedDuration = `${minutes}m`;
    }
    
    return {
      totalLectures,
      totalDuration: formattedDuration,
      enrolledCount: course.enrolledStudents ? course.enrolledStudents.length : 0
    };
  };

  const { totalLectures, totalDuration, enrolledCount } = calculateCourseStats();

  // Calculate average rating
  const calculateAverageRating = () => {
    if (!course.courseRatings || course.courseRatings.length === 0) {
      return 0;
    }
    const sum = course.courseRatings.reduce((acc, rating) => acc + rating.rating, 0);
    return Math.round((sum / course.courseRatings.length) * 10) / 10; // Round to 1 decimal
  };

  const averageRating = calculateAverageRating();

  // Calculate discount percentage if originalPrice exists
  const discountPercentage = course.discount 
    ? course.discount
    : (course.originalPrice && course.originalPrice > course.coursePrice 
        ? Math.round(((course.originalPrice - course.coursePrice) / course.originalPrice) * 100)
        : 0);

  // Calculate discounted price
  const discountedPrice = course.discount > 0 
    ? course.coursePrice * (1 - course.discount / 100)
    : course.coursePrice;

  return (
    <div className={`bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300 overflow-hidden border border-gray-200 ${className}`}>
      {/* Course Thumbnail */}
      <div className="relative">
        <Link to={`/course/${course._id}`}>
          <div className="aspect-video bg-gray-200 overflow-hidden">
            <img 
              src={course.courseThumbnail || assets.course_1}
              alt={course.courseTitle}
              className="w-full h-full object-cover hover:scale-105 transition-transform duration-300"
              onError={(e) => {
                e.target.src = assets.course_1; // Fallback image
              }}
            />
          </div>
          <div className="absolute top-3 right-3 bg-black bg-opacity-50 rounded-full p-2">
            <PlayCircle className="h-6 w-6 text-white" />
          </div>
        </Link>
        
        {/* Discount Badge */}
        {discountPercentage > 0 && (
          <div className="absolute top-3 left-3 bg-red-500 text-white px-2 py-1 rounded text-xs font-semibold">
            {discountPercentage}% OFF
          </div>
        )}

        {/* Level Badge */}
        <div className="absolute bottom-3 left-3 bg-blue-500 text-white px-2 py-1 rounded text-xs font-semibold">
          {course.level || 'Beginner'}
        </div>
      </div>

      {/* Course Content */}
      <div className="p-4">
        {/* Course Title */}
        <Link to={`/course/${course._id}`}>
          <h3 className="text-lg font-semibold text-gray-900 mb-2 line-clamp-2 hover:text-blue-600 transition-colors min-h-[3.5rem]">
            {course.courseTitle}
          </h3>
        </Link>

        {/* Instructor */}
        <p className="text-sm text-gray-600 mb-2">
          by <span className="font-medium">{educatorName}</span>
        </p>

        {/* Course Description */}
        <div 
          className="text-gray-600 text-sm mb-3 line-clamp-2"
          dangerouslySetInnerHTML={{ 
            __html: (course.courseDescription || '').replace(/<[^>]*>/g, '').slice(0, 100) + '...' 
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
