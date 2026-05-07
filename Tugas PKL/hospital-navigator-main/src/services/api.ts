import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:3001/api/v1";

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("API Error:", error.response?.data || error.message);
    return Promise.reject(error);
  },
);

export const roomsApi = {
  getAll: () => api.get("/rooms"),
  getById: (id: string) => api.get(`/rooms/${id}`),
  getByCategory: (category: string) => api.get(`/rooms/category/${encodeURIComponent(category)}`),
  getByFloor: (floor: number) => api.get(`/rooms/floor/${floor}`),
  search: (query: string) => api.get(`/rooms/search?q=${encodeURIComponent(query)}`),
};

export const qrAnchorsApi = {
  getAll: () => api.get("/qr-anchors"),
  getById: (qrId: string) => api.get(`/qr-anchors/${encodeURIComponent(qrId)}`),
  getByRoomId: (roomId: string) => api.get(`/qr-anchors/room/${encodeURIComponent(roomId)}`),
  getByFloor: (floor: number) => api.get(`/qr-anchors/floor/${floor}`),
  resolve: (qrCode: string) => api.get(`/qr-anchors/resolve?qr=${encodeURIComponent(qrCode)}`),
};

export default api;
