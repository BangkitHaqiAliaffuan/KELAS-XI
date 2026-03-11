import api from './api';

export const getListings      = (params)     => api.get('/marketplace/listings', { params });
export const getListing       = (id)         => api.get(`/marketplace/listings/${id}`);
export const getMyListings    = ()           => api.get('/marketplace/mine');
export const createListing    = (formData)   => api.post('/marketplace/listings', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
});
export const updateListing    = (id, formData) => api.post(`/marketplace/listings/${id}`, formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
});
export const deleteListing    = (id)         => api.delete(`/marketplace/${id}`);

export const getWishlist      = ()           => api.get('/wishlist');
export const toggleWishlist   = (listingId)  => api.post('/wishlist/toggle', { listing_id: listingId });

export const createOrder      = (data)       => api.post('/orders', data);
export const getOrders        = (params)     => api.get('/orders', { params });
export const getOrder         = (id)         => api.get(`/orders/${id}`);
export const payOrder         = (id)         => api.post(`/orders/${id}/pay`);
export const cancelOrder      = (id, data)   => api.post(`/orders/${id}/cancel`, data);
export const getPaymentStatus = (id)         => api.get(`/orders/${id}/payment-status`);
export const getSalesOrders   = ()           => api.get('/orders/sales-transactions');
export const confirmOrder     = (id)         => api.post(`/orders/${id}/confirm`);
export const shipOrder        = (id, data)   => api.post(`/orders/${id}/ship`, data);
export const completeOrder    = (id)         => api.post(`/orders/${id}/complete`);

export const checkoutCart     = (data)       => api.post('/orders/checkout-cart', data);
export const getCartCheckouts = ()           => api.get('/orders/cart-checkouts');
export const pollCartStatus   = (id)         => api.get(`/orders/cart-checkout/${id}/status`);
export const cancelCartCheckout = (id, data) => api.post(`/orders/cart-checkout/${id}/cancel`, data);
