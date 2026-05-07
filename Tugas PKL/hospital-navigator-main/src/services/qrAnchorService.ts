import { qrAnchorsApi } from "@/services/api";
import type { QrAnchor } from "@/data/hospitalRouteGraph";

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

export const resolveQrCode = async (qrCode: string): Promise<QrAnchor | null> => {
  return readData<QrAnchor>(await qrAnchorsApi.resolve(qrCode));
};

export const getQrAnchorStats = async (): Promise<{
  total: number;
  byFloor: Record<string, number>;
  rooms: number;
}> => {
  const anchors = await getAllQrAnchors();
  const byFloor: Record<string, number> = {};

  anchors.forEach((anchor) => {
    const floor = String(anchor.floor);
    byFloor[floor] = (byFloor[floor] || 0) + 1;
  });

  return {
    total: anchors.length,
    byFloor,
    rooms: new Set(anchors.map((anchor) => anchor.roomId)).size,
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
