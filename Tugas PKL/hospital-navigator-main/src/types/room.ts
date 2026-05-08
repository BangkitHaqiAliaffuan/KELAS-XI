/**
 * Room Type Definitions
 * Mirrors the backend hospital room data model
 */

export interface Room {
  id: string;
  name: string;
  category: string;
  locationHint: string;
  description: string;
  floor: number;
}

export interface RoomStats {
  total: number;
  byFloor: Record<string, number>;
  byCategory: Record<string, number>;
  categories: string[];
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  count?: number;
  message?: string;
  error?: string;
}
