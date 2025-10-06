import api from './api';

// Cities API
export const citiesApi = {
  getAll: () => api.get('/v1/cities'),
  getById: (id) => api.get(`/v1/cities/${id}`),
  getOfficesByCity: (cityId, params = {}) => 
    api.get(`/v1/cities/${cityId}/offices`, { params }),
};

// Offices API
export const officesApi = {
  getAll: (params = {}) => api.get('/v1/offices', { params }),
  getById: (id) => api.get(`/v1/offices/${id}`),
  search: (params = {}) => api.get('/v1/offices/search', { params }),
  create: (data) => api.post('/v1/admin/offices', data),
  update: (id, data) => api.put(`/v1/admin/offices/${id}`, data),
  delete: (id) => api.delete(`/v1/admin/offices/${id}`),
};

// Facilities API
export const facilitiesApi = {
  getAll: () => api.get('/v1/facilities'),
  getById: (id) => api.get(`/v1/facilities/${id}`),
  create: (data) => api.post('/v1/admin/facilities', data),
  update: (id, data) => api.put(`/v1/admin/facilities/${id}`, data),
  delete: (id) => api.delete(`/v1/admin/facilities/${id}`),
};

// Transactions API
export const transactionsApi = {
  getAll: (params = {}) => api.get('/v1/admin/transactions', { params }),
  getById: (id) => api.get(`/v1/admin/transactions/${id}`),
  getUserTransactions: (params = {}) => api.get('/v1/user/transactions', { params }),
  create: (data) => api.post('/v1/bookings', data),
  update: (id, data) => api.put(`/v1/admin/transactions/${id}`, data),
  updateStatus: (id, status) => 
    api.patch(`/v1/admin/transactions/${id}/status`, { status }),
  updatePaymentStatus: (id, data) => 
    api.patch(`/v1/admin/transactions/${id}/payment-status`, data),
  delete: (id) => api.delete(`/v1/admin/transactions/${id}`),
};

// Auth API
export const authApi = {
  login: (credentials) => api.post('/v1/auth/login', credentials),
  register: (userData) => api.post('/v1/auth/register', userData),
  logout: () => api.post('/v1/auth/logout'),
  user: () => api.get('/v1/auth/me'),
  forgotPassword: (email) => api.post('/v1/auth/forgot-password', { email }),
  resetPassword: (data) => api.post('/v1/auth/reset-password', data),
};