import axios from 'axios';

// Base API configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8000/api';
const API_KEY = import.meta.env.VITE_API_KEY || 'your-api-key-here';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'X-API-Key': API_KEY,
  },
  withCredentials: true, // Important for CORS with credentials
});

// Request interceptor to add auth token if available
api.interceptors.request.use(
  (config) => {
    // Check if this is an admin route
    const isAdminRoute = config.url.includes('/v1/admin/');
    
    let token = null;
    if (isAdminRoute) {
      token = localStorage.getItem('admin_token');
    } else {
      token = localStorage.getItem('auth_token');
    }
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Check if this was an admin route
      const wasAdminRoute = error.config.url.includes('/v1/admin/');
      
      // Clear appropriate token
      if (wasAdminRoute) {
        localStorage.removeItem('admin_token');
        // Redirect to admin login
        window.location.href = '/admin/login';
      } else {
        localStorage.removeItem('auth_token');
        // Redirect to public login
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// Auth Service
export const authService = {
  login: async (email, password) => {
    const response = await api.post('/v1/auth/login', { email, password });
    return response.data;
  },
  
  register: async (userData) => {
    const response = await api.post('/v1/auth/register', userData);
    return response.data;
  },
  
  logout: async () => {
    const response = await api.post('/v1/auth/logout');
    return response.data;
  },
  
  me: async () => {
    const response = await api.get('/v1/auth/me');
    return response.data;
  }
};

// Admin Authentication Service
export const adminAuthApi = {
  login: async (credentials) => {
    const response = await api.post('/v1/admin/auth/login', credentials);
    return response.data;
  },
  
  logout: async () => {
    const response = await api.post('/v1/admin/auth/logout');
    return response.data;
  },
  
  me: async () => {
    const response = await api.get('/v1/admin/auth/me');
    return response.data;
  },
  
  updateProfile: async (profileData) => {
    const response = await api.put('/v1/admin/auth/profile', profileData);
    return response.data;
  },
  
  changePassword: async (passwordData) => {
    const response = await api.post('/v1/admin/auth/change-password', passwordData);
    return response.data;
  }
};

// City Service
export const cityService = {
  getAll: async () => {
    const response = await api.get('/v1/cities');
    return response.data;
  },
  
  getById: async (id) => {
    const response = await api.get(`/v1/cities/${id}`);
    return response.data;
  }
};

// Office Service
export const officeService = {
  getAll: async (params = {}) => {
    const response = await api.get('/v1/offices', { params });
    return response.data;
  },
  
  getById: async (id) => {
    const response = await api.get(`/v1/offices/${id}`);
    return response.data;
  },
  
  getFeatured: async () => {
    const response = await api.get('/v1/offices?featured=1');
    return response.data;
  },
  
  create: async (officeData) => {
    const response = await api.post('/v1/admin/offices', officeData);
    return response.data;
  },
  
  update: async (id, officeData) => {
    const response = await api.put(`/v1/admin/offices/${id}`, officeData);
    return response.data;
  },
  
  delete: async (id) => {
    const response = await api.delete(`/v1/admin/offices/${id}`);
    return response.data;
  }
};

// Facility Service
export const facilityService = {
  getAll: async () => {
    const response = await api.get('/v1/facilities');
    return response.data;
  },
  
  getById: async (id) => {
    const response = await api.get(`/v1/facilities/${id}`);
    return response.data;
  },
  
  create: async (facilityData) => {
    const response = await api.post('/v1/admin/facilities', facilityData);
    return response.data;
  },
  
  update: async (id, facilityData) => {
    const response = await api.put(`/v1/admin/facilities/${id}`, facilityData);
    return response.data;
  },
  
  delete: async (id) => {
    const response = await api.delete(`/v1/admin/facilities/${id}`);
    return response.data;
  }
};

// Transaction Service
export const transactionService = {
  getAll: async (params = {}) => {
    const response = await api.get('/v1/admin/transactions', { params });
    return response.data;
  },
  
  getById: async (id) => {
    const response = await api.get(`/v1/bookings/${id}`);
    return response.data;
  },
  
  create: async (transactionData) => {
    const response = await api.post('/v1/bookings', transactionData);
    return response.data;
  },
  
  update: async (id, transactionData) => {
    const response = await api.put(`/v1/admin/transactions/${id}`, transactionData);
    return response.data;
  },
  
  delete: async (id) => {
    const response = await api.delete(`/v1/admin/transactions/${id}`);
    return response.data;
  },
  
  updateStatus: async (id, status) => {
    const response = await api.patch(`/v1/admin/transactions/${id}/status`, { status });
    return response.data;
  }
};

// Dashboard Service
export const dashboardService = {
  getStatistics: async () => {
    const response = await api.get('/v1/dashboard/statistics');
    return response.data;
  },
  
  getUserBookings: async (params = {}) => {
    const response = await api.get('/v1/dashboard/bookings', { params });
    return response.data;
  },
  
  cancelBooking: async (id) => {
    const response = await api.patch(`/v1/dashboard/bookings/${id}/cancel`);
    return response.data;
  }
};

export default api;