import React, { createContext, useState, useContext, useEffect } from 'react';
import { useUser } from '@clerk/clerk-react';
import { dummyCourses, dummyTestimonial, dummyEducatorData } from '../assets/assets.js';

// Create context
const AppContext = createContext();

// Create provider component
export const AppProvider = ({ children }) => {
  const [courses, setCourses] = useState(dummyCourses);
  const [searchQuery, setSearchQuery] = useState('');
  const [filteredCourses, setFilteredCourses] = useState(dummyCourses);
  const [enrolledCourses, setEnrolledCourses] = useState([]);
  const [testimonials] = useState(dummyTestimonial);
  const [educator] = useState(dummyEducatorData);
  
  // Get user data from Clerk
  const { user, isSignedIn, isLoaded } = useUser();

  // Filter courses based on search query
  const searchCourses = (query) => {
    setSearchQuery(query);
    if (query.trim() === '') {
      setFilteredCourses(courses);
    } else {
      const filtered = courses.filter(course =>
        course.courseTitle.toLowerCase().includes(query.toLowerCase()) ||
        course.courseDescription.toLowerCase().includes(query.toLowerCase())
      );
      setFilteredCourses(filtered);
    }
  };

  // Get course by ID
  const getCourseById = (id) => {
    return courses.find(course => course._id === id);
  };

  // Calculate average rating for a course
  const getAverageRating = (courseRatings) => {
    if (!courseRatings || courseRatings.length === 0) return 0;
    const total = courseRatings.reduce((sum, rating) => sum + rating.rating, 0);
    return (total / courseRatings.length).toFixed(1);
  };

  // Calculate discounted price
  const getDiscountedPrice = (price, discount) => {
    return (price - (price * discount / 100)).toFixed(2);
  };

  // Enroll in a course
  const enrollInCourse = (courseId) => {
    if (!enrolledCourses.includes(courseId)) {
      setEnrolledCourses([...enrolledCourses, courseId]);
    }
  };

  // Check if user is enrolled in a course
  const isEnrolled = (courseId) => {
    return enrolledCourses.includes(courseId);
  };

  const value = {
    courses,
    setCourses,
    searchQuery,
    setSearchQuery,
    filteredCourses,
    setFilteredCourses,
    searchCourses,
    getCourseById,
    getAverageRating,
    getDiscountedPrice,
    currentUser: user, // Use Clerk user
    isSignedIn,
    isLoaded,
    enrolledCourses,
    setEnrolledCourses,
    enrollInCourse,
    isEnrolled,
    testimonials,
    educator
  };

  return (
    <AppContext.Provider value={value}>
      {children}
    </AppContext.Provider>
  );
};

// Custom hook to use context
export const useApp = () => {
  const context = useContext(AppContext);
  if (context === undefined) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
};

export default AppContext;
