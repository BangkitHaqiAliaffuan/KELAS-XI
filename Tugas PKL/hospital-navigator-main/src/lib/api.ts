/**
 * API Client for Hospital Navigator Backend
 * Provides integration with backend API with fallback to static data
 */

import axios, { AxiosInstance } from 'axios';

// API Configuration
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3001/api/v1';
const API_TIMEOUT = 5000; // 5 seconds timeout



// Create axios instance
export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.code === 'ECONNABORTED') {
      console.warn('⏱️ [API] Request timeout - falling back to static data');
    } else if (error.code === 'ERR_NETWORK') {
      console.warn('🔌 [API] Network error - backend may not be running');
    } else {
      console.error('❌ [API] Error:', error.response?.data || error.message);
    }
    return Promise.reject(error);
  }
);

// Health check to verify backend is running
export const checkBackendHealth = async (): Promise<boolean> => {
  try {
    const response = await apiClient.get('/health', { timeout: 2000 });
    return response.data.success === true;
  } catch {
    return false;
  }
};

// Flag to track if backend is available
let backendAvailable: boolean | null = null;

export const isBackendAvailable = async (): Promise<boolean> => {
  if (backendAvailable !== null) {
    return backendAvailable;
  }
  backendAvailable = await checkBackendHealth();
  return backendAvailable;
};

// Reset backend availability check (useful for retry)
export const resetBackendCheck = () => {
  backendAvailable = null;
};

export default apiClient;
