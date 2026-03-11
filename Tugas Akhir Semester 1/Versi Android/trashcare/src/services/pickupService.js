import api from './api';

export const getPickups          = ()       => api.get('/pickups');
export const createPickup        = (data)   => api.post('/pickups', data);
export const getPickup           = (id)     => api.get(`/pickups/${id}`);
export const cancelPickup        = (id)     => api.post(`/pickups/${id}/cancel`);

export const getCourierPickups   = (params) => api.get('/courier/available-pickups', { params });
export const getPickupDetail     = (id)     => api.get(`/pickups/${id}`);
export const acceptPickup        = (id)     => api.post(`/courier/pickups/${id}/accept`);
export const startPickup         = (id)     => api.post(`/courier/pickups/${id}/start`);
export const completePickup      = (id)     => api.post(`/courier/pickups/${id}/complete`);
export const getMyActivePickup   = ()       => api.get('/courier/active-pickup');
