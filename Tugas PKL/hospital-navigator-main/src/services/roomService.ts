import { roomsApi } from "@/services/api";
import type { HospitalRoomInfo } from "@/data/hospitalRoomInfo";

interface ApiResponse<T> {
  success: boolean;
  data: T;
  count?: number;
  message?: string;
}

const readData = <T>(response: { data: ApiResponse<T> }): T => response.data.data;

export const getAllRooms = async (): Promise<HospitalRoomInfo[]> => {
  return readData<HospitalRoomInfo[]>(await roomsApi.getAll());
};

export const getRoomById = async (roomId: string): Promise<HospitalRoomInfo | null> => {
  return readData<HospitalRoomInfo>(await roomsApi.getById(roomId));
};

export const getRoomsByCategory = async (category: string): Promise<HospitalRoomInfo[]> => {
  return readData<HospitalRoomInfo[]>(await roomsApi.getByCategory(category));
};

export const getRoomsByFloor = async (floor: number): Promise<HospitalRoomInfo[]> => {
  return readData<HospitalRoomInfo[]>(await roomsApi.getByFloor(floor));
};

export const searchRooms = async (query: string): Promise<HospitalRoomInfo[]> => {
  return readData<HospitalRoomInfo[]>(await roomsApi.search(query));
};

export const getCategories = async (): Promise<string[]> => {
  const rooms = await getAllRooms();
  return Array.from(new Set(rooms.map((room) => room.category))).sort();
};

export const roomService = {
  getAllRooms,
  getRoomById,
  getRoomsByCategory,
  getRoomsByFloor,
  searchRooms,
  getCategories,
};

export default roomService;
