import { qrAnchorsApi } from "@/services/api";
import type { QrAnchor, QrAnchorStats } from "@/types/qrAnchor";

interface ApiResponse<T> {
  success: boolean;
  data: T;
  count?: number;
  message?: string;
}

const readData = <T>(response: { data: ApiResponse<T> }): T => response.data.data;

export const getAllQrAnchors = async (): Promise<QrAnchor[]> => {
  return readData<QrAnchor[]>(await qrAnchorsApi.getAll());
};

export const getQrAnchorById = async (qrId: string): Promise<QrAnchor | null> => {
  return readData<QrAnchor>(await qrAnchorsApi.getById(qrId));
};

export const getQrAnchorsByRoomId = async (roomId: string): Promise<QrAnchor[]> => {
  return readData<QrAnchor[]>(await qrAnchorsApi.getByRoomId(roomId));
};

export const getQrAnchorsByFloor = async (floor: number): Promise<QrAnchor[]> => {
  return readData<QrAnchor[]>(await qrAnchorsApi.getByFloor(floor));
};

/**
 * Resolve a QR code to its anchor via POST /qr-anchors/resolve
 */
export const resolveQrCode = async (qrCode: string): Promise<QrAnchor | null> => {
  return readData<QrAnchor>(await qrAnchorsApi.resolve(qrCode));
};

/**
 * Get QR anchor statistics from the backend stats endpoint
 */
export const getQrAnchorStats = async (): Promise<QrAnchorStats> => {
  return readData<QrAnchorStats>(await qrAnchorsApi.getStats());
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
