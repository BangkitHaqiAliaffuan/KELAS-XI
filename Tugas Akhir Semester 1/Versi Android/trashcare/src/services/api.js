import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://127.0.0.1:8000/api',
  timeout: 15000,
  headers: { 'Accept': 'application/json' },
});

// Attach token on every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Auto-logout on 401
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

export default api;

/** Parse Laravel error response → string */
export function parseError(err) {
  if (!err.response) return 'Tidak dapat terhubung ke server.';
  const data = err.response.data;
  if (data?.message) return data.message;
  if (data?.errors) return Object.values(data.errors).flat().join(' ');
  return 'Terjadi kesalahan.';
}

/** Format IDR */
export function formatRupiah(num) {
  return 'Rp ' + Number(num).toLocaleString('id-ID');
}

/** Format date string */
export function formatDate(str) {
  if (!str) return '-';
  return new Date(str).toLocaleDateString('id-ID', {
    day: '2-digit', month: 'short', year: 'numeric'
  });
}

/** Image URL from backend storage */
export function storageUrl(path) {
  if (!path) return null;
  if (path.startsWith('http')) return path;
  const base = process.env.REACT_APP_API_URL || 'http://192.168.1.7:8000';
  return `${base.replace('/api/', '')}/storage/${path}`;
}
