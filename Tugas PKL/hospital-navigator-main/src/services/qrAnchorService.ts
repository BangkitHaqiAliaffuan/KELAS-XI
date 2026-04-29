/**
 * QR Anchor Service - Hybrid data source (API + Static fallback)
 * Automatically uses backend API if available, otherwise falls back to static data
 */

import apiClient, { isBackendAvailable } from '@/lib/api';
import { QR_ANCHOR_REGISTRY, resolveQrAnchor, type QrAnchor } from '@/data/hospitalRouteGraph';

interface ApiResponse<T> {
  success: boolean;
  data: T;
  count?: number;
  message?: string;
}

/**
 * Get all QR anchors
 */
export const getAllQrAnchors = async (): Promise<QrAnchor[]> => {
  const useApi = await isBackendAvailable();
  
  if (useApi) {
    try {
      const response = await apiClient.get<ApiResponse<QrAnchor[]>>('/qr-anchors');
      console.log('[QrAnchorService] Fetched QR anchors from API:', response.data.count);
      return response.data.data;
    } catch (error) {
      console.warn('[QrAnchorService] API failed, falling back to static data');
    }
  }
  
  // Fallback to static data
  return Object.values(QR_ANCHOR_REGISTRY);
};

/**
 * Get QR anchor by ID
 */
export const getQrAnchorById = async (qrId: string): Promise<QrAnchor | null> => {
  const useApi = await isBackendAvailable();
  
  if (useApi) {
    try {
      const response = await apiClient.get<ApiResponse<QrAnchor>>(`/qr-anchors/${qrId}`);
      return response.data.data;
    } catch (error) {
      console.warn('[QrAnchorService] API failed, falling back to static data');
    }
  }
  
  // Fallback to static data
  return QR_ANCHOR_REGISTRY[qrId] || null;
};

/**
 * Get QR anchors by room ID
 */
export const getQrAnchorsByRoomId = async (roomId: string): Promise<QrAnchor[]> => {
  const useApi = await isBackendAvailable();
  
  if (useApi) {
    try {
      const response = await apiClient.get<ApiResponse<QrAnchor[]>>('/qr-anchors', {
        params: { roomId },
      });
      return response.data.data;
    } catch (error) {
      console.warn('[QrAnchorService] API failed, falling back to static data');
    }
  }
  
  // Fallback to static data
  return Object.values(QR_ANCHOR_REGISTRY).filter((anchor) => anchor.roomId === roomId);
};

/**
 * Get QR anchors by floor
 */
export const getQrAnchorsByFloor = async (floor: number): Promise<QrAnchor[]> => {
  const useApi = await isBackendAvailable();
  
  if (useApi) {
    try {
      const response = await apiClient.get<ApiResponse<QrAnchor[]>>('/qr-anchors', {
        params: { floor },
      });
      return response.data.data;
    } catch (error) {
      console.warn('[QrAnchorService] API failed, falling back to static data');
    }
  }
  
  // Fallback to static data
  return Object.values(QR_ANCHOR_REGISTRY).filter((anchor) => anchor.floor === floor);
};

/**
 * Resolve QR code to anchor
 */
export const resolveQrCode = async (qrCode: string): Promise<QrAnchor | null> => {
  const useApi = await isBackendAvailable();
  
  if (useApi) {
    try {
      const response = await apiClient.post<ApiResponse<QrAnchor>>('/qr-anchors/resolve', {
        qrCode,
      });
      return response.data.data;
    } catch (error) {
      console.warn('[QrAnchorService] API failed, falling back to static data');
    }
  }
  
  // Fallback to static data
  return resolveQrAnchor(qrCode);
};

/**
 * Get QR anchor statistics
 */
export const getQrAnchorStats = async (): Promise<{
  total: number;
  byFloor: Record<string, number>;
  rooms: number;
}> => {
  const useApi = await isBackendAvailable();
  
  if (useApi) {
    try {
      const response = await apiClient.get('/qr-anchors/stats');
      return response.data.data;
    } catch (error) {
      console.warn('[QrAnchorService] API failed, falling back to static data');
    }
  }
  
  // Fallback to static data - calculate stats
  const anchors = Object.values(QR_ANCHOR_REGISTRY);
  const byFloor: Record<string, number> = {};
  
  anchors.forEach((anchor) => {
    const floor = String(anchor.floor);
    byFloor[floor] = (byFloor[floor] || 0) + 1;
  });

  return {
    total: anchors.length,
    byFloor,
    rooms: new Set(anchors.map((a) => a.roomId)).size,
  };
};

export const qrAnchorService = {
  getAllQrAnchors,
  getQrAnchorById,
  getQrAnchorsByRoomId,
  getQrAnchorsByFloor,
  resolveQrCode,
  getQrAnchorStats,
};

export default qrAnchorService;
