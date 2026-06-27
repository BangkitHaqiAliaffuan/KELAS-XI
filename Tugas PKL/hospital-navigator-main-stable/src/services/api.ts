import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:3001/api/v1";

export const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.code === "ECONNABORTED") {
      console.warn("⏱️ [API] Request timeout");
    } else if (error.code === "ERR_NETWORK") {
      console.warn("🔌 [API] Network error — backend may not be running");
    } else {
      console.error("❌ [API] Error:", error.response?.data || error.message);
    }
    return Promise.reject(error);
  },
);

// ─── Rooms ────────────────────────────────────────────────────────────────────
export const roomsApi = {
  getAll: () => api.get("/rooms"),
  getById: (id: string) => api.get(`/rooms/${id}`),
  /** Filter uses query params: /rooms?category=Emergency */
  getByCategory: (category: string) => api.get("/rooms", { params: { category } }),
  /** Filter uses query params: /rooms?floor=1 */
  getByFloor: (floor: number) => api.get("/rooms", { params: { floor } }),
  /** Search uses query params: /rooms?search=igd */
  search: (query: string) => api.get("/rooms", { params: { search: query } }),
  getStats: () => api.get("/rooms/stats"),
};

// ─── QR Anchors ───────────────────────────────────────────────────────────────
export const qrAnchorsApi = {
  getAll: () => api.get("/qr-anchors"),
  getById: (qrId: string) => api.get(`/qr-anchors/${encodeURIComponent(qrId)}`),
  /** Filter uses query params: /qr-anchors?roomId=IGD */
  getByRoomId: (roomId: string) => api.get("/qr-anchors", { params: { roomId } }),
  /** Filter uses query params: /qr-anchors?floor=1 */
  getByFloor: (floor: number) => api.get("/qr-anchors", { params: { floor } }),
  /** Resolve uses POST with JSON body: { qrCode: "QR-F1-N01" } */
  resolve: (qrCode: string) => api.post("/qr-anchors/resolve", { qrCode }),
  getStats: () => api.get("/qr-anchors/stats"),
};

// ─── Categories ───────────────────────────────────────────────────────────────
export const categoriesApi = {
  getAll: () => api.get("/categories"),
  getNames: () => api.get("/categories/names"),
  getStats: () => api.get("/categories/stats"),
  getByName: (name: string) => api.get(`/categories/${encodeURIComponent(name)}`),
  validate: (name: string) => api.post("/categories/validate", { name }),
};

// ─── Health ───────────────────────────────────────────────────────────────────
export const checkHealth = () => api.get("/health", { timeout: 3000 });

export default api;
