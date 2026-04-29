/**
 * Room Service - Hybrid data source (API + Static fallback)
 * Automatically uses backend API if available, otherwise falls back to static data
 */

import apiClient, { isBackendAvailable } from '@/lib/api';
import { roomInfoBySvgId, type HospitalRoomInfo } from '@/data/hospitalRoomInfo';

interface ApiResponse<T> {
  success: boolean;
  data: T;
  count?: number;
  message?: string;
}

/**
 * Get all rooms
 * Uses API if available, otherwise returns static data
 */
export const getAllRooms = async (): Promise<HospitalRoomInfo[]> => {
  const useApi = await isBackendAvailable();
  
  if (useApi) {
    try {
      const response = await apiClient.get<ApiResponse<HospitalRoomInfo[]>>('/rooms');
      console.log('[RoomService] Fetched rooms from API:', response.data.count);
      return response.data.data;
    } catch (error) {
      console.warn('[RoomService] API failed, falling back to static data');
    }
  }
  
  // Fallback to static data
  return Object.values(roomInfoBySvgId);
};

/**
 * Get room by ID
 */
export const getRoomById = async (roomId: string): Promise<HospitalRoomInfo | null> => {
  const useApi = await isBackendAvailable();
  
  if (useApi) {
    try {
      const response = await apiClient.get<ApiResponse<HospitalRoomInfo>>(`/rooms/${roomId}`);
      return response.data.data;
    } catch (error) {
      console.warn('[RoomService] API failed, falling back to static data');
    }
  }
  
  // Fallback to static data
  return roomInfoBySvgId[roomId] || null;
};

/**
 * Get rooms by category
 */
export const getRoomsByCategory = async (category: string): Promise<HospitalRoomInfo[]> => {
  const useApi = await isBackendAvailable();
  
  if (useApi) {
    try {
      const response = await apiClient.get<ApiResponse<HospitalRoomInfo[]>>('/rooms', {
        params: { category },
      });
      return response.data.data;
    } catch (error) {
      console.warn('[RoomService] API failed, falling back to static data');
    }
  }
  
  // Fallback to static data
  return Object.values(roomInfoBySvgId).filter(
    (room) => room.category.toLowerCase() === category.toLowerCase()
  );
};

/**
 * Get rooms by floor
 */
export const getRoomsByFloor = async (floor: number): Promise<HospitalRoomInfo[]> => {
  const useApi = await isBackendAvailable();
  
  if (useApi) {
    try {
      const response = await apiClient.get<ApiResponse<HospitalRoomInfo[]>>('/rooms', {
        params: { floor },
      });
      return response.data.data;
    } catch (error) {
      console.warn('[RoomService] API failed, falling back to static data');
    }
  }
  
  // Fallback to static data - filter by floor if floor info exists
  // Note: Static data doesn't have floor info, so this returns all rooms
  return Object.values(roomInfoBySvgId);
};

/**
 * Search rooms by query
 */
export const searchRooms = async (query: string): Promise<HospitalRoomInfo[]> => {
  const useApi = await isBackendAvailable();
  
  if (useApi) {
    try {
      const response = await apiClient.get<ApiResponse<HospitalRoomInfo[]>>('/rooms', {
        params: { search: query },
      });
      return response.data.data;
    } catch (error) {
      console.warn('[RoomService] API failed, falling back to static data');
    }
  }
  
  // Fallback to static data
  const lowerQuery = query.toLowerCase();
  return Object.values(roomInfoBySvgId).filter(
    (room) =>
      room.name.toLowerCase().includes(lowerQuery) ||
      room.description.toLowerCase().includes(lowerQuery) ||
      room.id.toLowerCase().includes(lowerQuery)
  );
};

/**
 * Get all categories
 */
export const getCategories = async (): Promise<string[]> => {
  const useApi = await isBackendAvailable();
  
  if (useApi) {
    try {
      const response = await apiClient.get<ApiResponse<string[]>>('/rooms/categories');
      return response.data.data;
    } catch (error) {
      console.warn('[RoomService] API failed, falling back to static data');
    }
  }
  
  // Fallback to static data
  const categories = new Set<string>();
  Object.values(roomInfoBySvgId).forEach((room) => {
    categories.add(room.category);
  });
  return Array.from(categories).sort();
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
