import React, { useState, useEffect } from 'react';
import { useUser } from '@clerk/clerk-react';
import { CheckCircle, Clock, BookOpen, ArrowRight } from 'lucide-react';
import { enrollmentAPI } from '../services/api.js';

const EnrollmentStatus = ({ courseId, onEnroll, className = "" }) => {
  const { user, isSignedIn } = useUser();
  const [enrollment, setEnrollment] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (isSignedIn && courseId) {
      checkEnrollmentStatus();
    } else {
      setIsLoading(false);
    }
  }, [isSignedIn, courseId]);

  const checkEnrollmentStatus = async () => {
    try {
      setIsLoading(true);
      const response = await enrollmentAPI.getUserEnrollments(user.id);
      
      if (response.success) {
        const courseEnrollment = response.data.find(
          enrollment => enrollment.courseId === courseId
        );
        setEnrollment(courseEnrollment || null);
      }
    } catch (err) {
      console.error('Error checking enrollment:', err);
      setError('Unable to check enrollment status');
    } finally {
      setIsLoading(false);
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const calculateProgress = () => {
    if (!enrollment) return 0;
    const completed = enrollment.completedLessons?.length || 0;
    const total = enrollment.totalLessons || 1;
    return Math.round((completed / total) * 100);
  };

  if (isLoading) {
    return (
      <div className={`animate-pulse ${className}`}>
        <div className="h-20 bg-gray-200 rounded-lg"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className={`bg-red-50 border border-red-200 rounded-lg p-4 ${className}`}>
        <p className="text-red-700 text-sm">{error}</p>
      </div>
    );
  }

  if (!isSignedIn) {
    return (
      <div className={`bg-blue-50 border border-blue-200 rounded-lg p-4 ${className}`}>
        <p className="text-blue-700 text-sm text-center">
          Please sign in to check your enrollment status
        </p>
      </div>
    );
  }

  if (!enrollment) {
    return (
      <div className={`bg-gray-50 border border-gray-200 rounded-lg p-4 ${className}`}>
        <div className="text-center">
          <BookOpen className="h-8 w-8 text-gray-400 mx-auto mb-2" />
          <p className="text-gray-600 text-sm">
            You are not enrolled in this course yet
          </p>
          {onEnroll && (
            <button
              onClick={onEnroll}
              className="mt-2 text-blue-600 hover:text-blue-700 text-sm font-medium"
            >
              Enroll Now
            </button>
          )}
        </div>
      </div>
    );
  }

  const progress = calculateProgress();

  return (
    <div className={`bg-green-50 border border-green-200 rounded-lg p-4 ${className}`}>
      <div className="flex items-start space-x-3">
        <CheckCircle className="h-6 w-6 text-green-600 flex-shrink-0 mt-0.5" />
        <div className="flex-1">
          <div className="flex items-center justify-between mb-2">
            <h3 className="font-semibold text-green-900">
              Enrolled
            </h3>
            <span className="text-sm text-green-700">
              {progress}% Complete
            </span>
          </div>
          
          <div className="space-y-2">
            <p className="text-sm text-green-700">
              Enrolled on {formatDate(enrollment.enrolledDate)}
            </p>
            
            {enrollment.lastAccessedDate && (
              <p className="text-sm text-green-600">
                Last accessed: {formatDate(enrollment.lastAccessedDate)}
              </p>
            )}
            
            {/* Progress Bar */}
            <div className="w-full bg-green-200 rounded-full h-2">
              <div 
                className="bg-green-600 h-2 rounded-full transition-all duration-300"
                style={{ width: `${progress}%` }}
              ></div>
            </div>
            
            <div className="flex items-center justify-between text-sm text-green-700">
              <span>
                {enrollment.completedLessons?.length || 0} of {enrollment.totalLessons || 0} lessons
              </span>
              <span className="flex items-center space-x-1">
                <Clock className="h-4 w-4" />
                <span>
                  {enrollment.timeSpent || 0} hours studied
                </span>
              </span>
            </div>
          </div>
          
          {/* Continue Learning Button */}
          <div className="mt-3">
            <button
              onClick={() => {
                // Navigate to course content or next lesson
                window.location.href = `/course/${courseId}/learn`;
              }}
              className="w-full bg-green-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors flex items-center justify-center space-x-2"
            >
              <span>Continue Learning</span>
              <ArrowRight className="h-4 w-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EnrollmentStatus;
