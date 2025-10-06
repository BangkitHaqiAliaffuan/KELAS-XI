import axios from 'axios';

// Base API configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8000/api';
const API_KEY = import.meta.env.VITE_API_KEY || 'your-api-key-here';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    'X-API-Key': API_KEY,
  },
});

// Request interceptor to add auth token if available
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token');
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
      // Clear auth token and redirect to login
      localStorage.removeItem('auth_token');
      window.location.href = '/login';
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

// City Service
export const cityService = {
  getAll: async () => {
    const response = await api.get('/cities');
    return response.data;
  },
  
  getById: async (id) => {
    const response = await api.get(`/cities/${id}`);
    return response.data;
  }
};

// Office Service
export const officeService = {
  getAll: async (params = {}) => {
    const response = await api.get('/offices', { params });
    return response.data;
  },
  
  getById: async (id) => {
    const response = await api.get(`v1/offices/${id}`);
    return response.data;
  },
  
  getFeatured: async () => {
    const response = await api.get('/offices?featured=1');
    return response.data;
  },
  
  create: async (officeData) => {
    const response = await api.post('/offices', officeData);
    return response.data;
  },
  
  update: async (id, officeData) => {
    const response = await api.put(`/offices/${id}`, officeData);
    return response.data;
  },
  
  delete: async (id) => {
    const response = await api.delete(`/offices/${id}`);
    return response.data;
  }
};

// Facility Service
export const facilityService = {
  getAll: async () => {
    const response = await api.get('/facilities');
    return response.data;
  },
  
  getById: async (id) => {
    const response = await api.get(`/facilities/${id}`);
    return response.data;
  },
  
  create: async (facilityData) => {
    const response = await api.post('/facilities', facilityData);
    return response.data;
  },
  
  update: async (id, facilityData) => {
    const response = await api.put(`/facilities/${id}`, facilityData);
    return response.data;
  },
  
  delete: async (id) => {
    const response = await api.delete(`/facilities/${id}`);
    return response.data;
  }
};

// Transaction Service
export const transactionService = {
  getAll: async (params = {}) => {
    const response = await api.get('/transactions', { params });
    return response.data;
  },
  
  getById: async (id) => {
    const response = await api.get(`/transactions/${id}`);
    return response.data;
  },
  
  create: async (transactionData) => {
    const response = await api.post('/transactions', transactionData);
    return response.data;
  },
  
  update: async (id, transactionData) => {
    const response = await api.put(`/transactions/${id}`, transactionData);
    return response.data;
  },
  
  delete: async (id) => {
    const response = await api.delete(`/transactions/${id}`);
    return response.data;
  },
  
  updateStatus: async (id, status) => {
    const response = await api.patch(`/transactions/${id}/status`, { status });
    return response.data;
  }
};

export default api;