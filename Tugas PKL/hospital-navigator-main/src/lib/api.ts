/**
 * API Client for Hospital Navigator Backend
 * Provides integration with backend API with fallback to static data
 */

import axios, { AxiosInstance } from 'axios';

// API Configuration
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3001/api/v1';
const API_TIMEOUT = 5000; // 5 seconds timeout

console.log('═══════════════════════════════════════════════════════');
console.log('🏥 Hospital Navigator - API Configuration');
console.log('═══════════════════════════════════════════════════════');
console.log('API Base URL:', API_BASE_URL);
console.log('Environment:', import.meta.env.MODE);
console.log('VITE_API_URL:', import.meta.env.VITE_API_URL || 'Not set (using default)');
console.log('═══════════════════════════════════════════════════════');

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
  console.log('🔍 [API] Checking backend health...');
  console.log('🔍 [API] Health check URL:', `${API_BASE_URL}/health`);
  
  try {
    const response = await apiClient.get('/health', { timeout: 2000 });
    const isHealthy = response.data.success === true;
    
    if (isHealthy) {
      console.log('✅ [API] Backend is HEALTHY and AVAILABLE');
      console.log('✅ [API] Response:', response.data);
    } else {
      console.warn('⚠️ [API] Backend responded but health check failed');
    }
    
    return isHealthy;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.code === 'ERR_NETWORK') {
        console.warn('❌ [API] Cannot connect to backend - Network error');
        console.warn('❌ [API] Make sure backend is running at:', API_BASE_URL);
      } else if (error.code === 'ECONNABORTED') {
        console.warn('❌ [API] Backend health check timeout');
      } else {
        console.warn('❌ [API] Backend health check failed:', error.message);
      }
    } else {
      console.warn('❌ [API] Unexpected error during health check:', error);
    }
    return false;
  }
};

// Flag to track if backend is available
let backendAvailable: boolean | null = null;

export const isBackendAvailable = async (): Promise<boolean> => {
  if (backendAvailable !== null) {
    console.log(`📦 [API] Using cached backend status: ${backendAvailable ? 'AVAILABLE' : 'NOT AVAILABLE'}`);
    return backendAvailable;
  }
  
  backendAvailable = await checkBackendHealth();
  
  console.log('═══════════════════════════════════════════════════════');
  if (backendAvailable) {
    console.log('✅ Backend is AVAILABLE - Using API data');
    console.log('📡 Data source: Backend API (Real-time)');
  } else {
    console.log('❌ Backend is NOT AVAILABLE - Using static data');
    console.log('📁 Data source: Static files (Fallback)');
    console.log('💡 To use backend: cd server && npm run dev');
  }
  console.log('═══════════════════════════════════════════════════════');
  
  return backendAvailable;
};

// Reset backend availability check (useful for retry)
export const resetBackendCheck = () => {
  console.log('🔄 [API] Resetting backend availability check');
  backendAvailable = null;
};

export default apiClient;
