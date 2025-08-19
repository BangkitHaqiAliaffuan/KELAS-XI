// API configuration
const API_BASE_URL = process.env.NODE_ENV === 'production' 
  ? 'https://your-production-api.com' 
  : 'http://localhost:5000';

// Generic API request function
const apiRequest = async (endpoint, options = {}) => {
  const url = `${API_BASE_URL}${endpoint}`;
  
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  };

  try {
    const response = await fetch(url, config);
    const data = await response.json();
    
    if (!response.ok) {
      throw new Error(data.message || `HTTP error! status: ${response.status}`);
    }
    
    return data;
  } catch (error) {
    console.error(`API request failed: ${endpoint}`, error);
    throw error;
  }
};

// Course API functions
export const courseAPI = {
  // Get all courses with filters and pagination
  getCourses: async (params = {}) => {
    const queryString = new URLSearchParams(params).toString();
    const endpoint = `/api/courses${queryString ? `?${queryString}` : ''}`;
    return apiRequest(endpoint);
  },

  // Get featured courses
  getFeaturedCourses: async () => {
    return apiRequest('/api/courses/featured');
  },

  // Get course categories
  getCategories: async () => {
    return apiRequest('/api/courses/categories');
  },

  // Get single course by ID
  getCourse: async (id) => {
    return apiRequest(`/api/courses/${id}`);
  },

  // Create new course (for educators)
  createCourse: async (courseData) => {
    return apiRequest('/api/courses', {
      method: 'POST',
      body: JSON.stringify(courseData),
    });
  },
};

// User API functions
export const userAPI = {
  // Get all users (admin)
  getUsers: async (params = {}) => {
    const queryString = new URLSearchParams(params).toString();
    const endpoint = `/api/users${queryString ? `?${queryString}` : ''}`;
    return apiRequest(endpoint);
  },

  // Get user by Clerk ID
  getUser: async (clerkId) => {
    return apiRequest(`/api/users/${clerkId}`);
  },

  // Create new user
  createUser: async (userData) => {
    return apiRequest('/api/users', {
      method: 'POST',
      body: JSON.stringify(userData),
    });
  },

  // Update user profile
  updateUser: async (clerkId, userData) => {
    return apiRequest(`/api/users/${clerkId}`, {
      method: 'PUT',
      body: JSON.stringify(userData),
    });
  },
};

// Testimonial API functions
export const testimonialAPI = {
  // Get all testimonials
  getTestimonials: async (params = {}) => {
    const queryString = new URLSearchParams(params).toString();
    const endpoint = `/api/testimonials${queryString ? `?${queryString}` : ''}`;
    return apiRequest(endpoint);
  },

  // Get featured testimonials
  getFeaturedTestimonials: async () => {
    return apiRequest('/api/testimonials/featured');
  },

  // Create new testimonial
  createTestimonial: async (testimonialData) => {
    return apiRequest('/api/testimonials', {
      method: 'POST',
      body: JSON.stringify(testimonialData),
    });
  },
};

// Enrollment API functions
export const enrollmentAPI = {
  // Get all enrollments
  getEnrollments: async (params = {}) => {
    const queryString = new URLSearchParams(params).toString();
    const endpoint = `/api/enrollments${queryString ? `?${queryString}` : ''}`;
    return apiRequest(endpoint);
  },

  // Get user's enrollments
  getUserEnrollments: async (userId) => {
    return apiRequest(`/api/enrollments/user/${userId}`);
  },

  // Create new enrollment
  createEnrollment: async (enrollmentData) => {
    return apiRequest('/api/enrollments', {
      method: 'POST',
      body: JSON.stringify(enrollmentData),
    });
  },

  // Update enrollment status
  updateEnrollmentStatus: async (id, status) => {
    return apiRequest(`/api/enrollments/${id}/status`, {
      method: 'PUT',
      body: JSON.stringify({ status }),
    });
  },

  // Update course progress
  updateProgress: async (id, progressData) => {
    return apiRequest(`/api/enrollments/${id}/progress`, {
      method: 'PUT',
      body: JSON.stringify(progressData),
    });
  },
};

// Payment API functions
export const paymentAPI = {
  // Create payment intent
  createPaymentIntent: async (paymentData) => {
    return apiRequest('/api/payments/create-payment-intent', {
      method: 'POST',
      body: JSON.stringify(paymentData),
    });
  },
};

// Health check
export const healthCheck = async () => {
  return apiRequest('/health');
};

export default {
  courseAPI,
  userAPI,
  testimonialAPI,
  enrollmentAPI,
  paymentAPI,
  healthCheck,
};
