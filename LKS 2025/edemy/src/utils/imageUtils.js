import { assets } from '../assets/assets.js';

// Base URL untuk server
const SERVER_BASE_URL = 'http://localhost:5000';

/**
 * Get the proper image URL for course thumbnails
 * @param {string} courseThumbnail - The thumbnail path from database
 * @param {string} fallbackImage - Fallback image from assets
 * @returns {string} - Complete image URL
 */
export const getCourseImageUrl = (courseThumbnail, fallbackImage = assets.course_1_thumbnail) => {
  // Jika tidak ada thumbnail, gunakan fallback
  if (!courseThumbnail) {
    return fallbackImage;
  }

  // Jika thumbnail adalah base64 string, gunakan langsung
  if (courseThumbnail.startsWith('data:image/')) {
    return courseThumbnail;
  }

  // Jika thumbnail adalah path file (starts with /uploads/), buat full URL
  if (courseThumbnail.startsWith('/uploads/')) {
    return `${SERVER_BASE_URL}${courseThumbnail}`;
  }

  // Jika thumbnail adalah full URL, gunakan langsung
  if (courseThumbnail.startsWith('http')) {
    return courseThumbnail;
  }

  // Jika thumbnail adalah relative path, tambahkan server URL
  return `${SERVER_BASE_URL}/uploads/course-thumbnails/${courseThumbnail}`;
};

/**
 * Get fallback image based on course category or index
 * @param {string} category - Course category
 * @param {number} index - Course index for rotating fallbacks
 * @returns {string} - Fallback image URL
 */
export const getFallbackCourseImage = (category = '', index = 0) => {
  const fallbackImages = [
    assets.course_1_thumbnail,
    assets.course_2_thumbnail,
    assets.course_3_thumbnail,
    assets.course_4_thumbnail
  ];

  // Rotate through fallback images based on index
  return fallbackImages[index % fallbackImages.length];
};

/**
 * Preload image to check if it exists
 * @param {string} imageUrl - Image URL to check
 * @returns {Promise<boolean>} - Returns true if image loads successfully
 */
export const preloadImage = (imageUrl) => {
  return new Promise((resolve) => {
    const img = new Image();
    img.onload = () => resolve(true);
    img.onerror = () => resolve(false);
    img.src = imageUrl;
  });
};

/**
 * Smart image component props generator
 * @param {string} courseThumbnail - Thumbnail from database
 * @param {string} courseTitle - Course title for alt text
 * @param {number} index - Course index for fallback rotation
 * @returns {object} - Props for img element
 */
export const getImageProps = (courseThumbnail, courseTitle = 'Course', index = 0) => {
  const primaryUrl = getCourseImageUrl(courseThumbnail);
  const fallbackUrl = getFallbackCourseImage('', index);

  return {
    src: primaryUrl,
    alt: `${courseTitle} thumbnail`,
    onError: (e) => {
      // Jika primary image gagal load, gunakan fallback
      if (e.target.src !== fallbackUrl) {
        e.target.src = fallbackUrl;
      }
    },
    loading: 'lazy' // Lazy loading untuk performance
  };
};
