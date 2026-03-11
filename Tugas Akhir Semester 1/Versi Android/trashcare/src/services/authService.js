import api from './api';

export const login        = (data)   => api.post('/auth/login', data);
export const register     = (data)   => api.post('/auth/register', data);
export const getProfile   = ()       => api.get('/auth/profile');
export const updateProfile= (data)   => api.put('/auth/profile', data);
export const logout       = ()       => api.post('/auth/logout');

// Address
export const getAddresses  = ()       => api.get('/addresses');
export const createAddress = (data)   => api.post('/addresses', data);
export const updateAddress = (id, d)  => api.put(`/addresses/${id}`, d);
export const deleteAddress = (id)     => api.delete(`/addresses/${id}`);
