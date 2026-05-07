import { useQuery } from "@tanstack/react-query";
import { roomsApi, qrAnchorsApi } from "@/services/api";
import type { HospitalRoomInfo } from "@/data/hospitalRoomInfo";
import type { QrAnchor } from "@/data/hospitalRouteGraph";

const FIVE_MINUTES = 5 * 60 * 1000;

// Fallback: Import static data
let staticRoomInfo: HospitalRoomInfo[] | null = null;
let staticQrAnchors: QrAnchor[] | null = null;
let isUsingFallback = false;

const loadStaticData = async () => {
  if (!staticRoomInfo || !staticQrAnchors) {
    try {
      const [roomModule, qrModule] = await Promise.all([
        import("@/data/hospitalRoomInfo"),
        import("@/data/hospitalRouteGraph"),
      ]);
      staticRoomInfo = Object.values(roomModule.hospitalRoomInfo);
      staticQrAnchors = Object.values(qrModule.QR_ANCHOR_REGISTRY);
    } catch (error) {
      console.error("Failed to load static data:", error);
    }
  }
};

export const isUsingStaticData = () => isUsingFallback;

export const useRooms = () => {
  return useQuery<HospitalRoomInfo[]>({
    queryKey: ["rooms"],
    queryFn: async () => {
      try {
        const response = await roomsApi.getAll();
        isUsingFallback = false;
        return response.data.data;
      } catch (error) {
        console.warn("API failed, falling back to static data:", error);
        isUsingFallback = true;
        await loadStaticData();
        if (staticRoomInfo) {
          return staticRoomInfo;
        }
        throw error;
      }
    },
    staleTime: FIVE_MINUTES,
    retry: 1, // Only retry once before falling back
  });
};

export const useRoomById = (id: string) => {
  return useQuery<HospitalRoomInfo>({
    queryKey: ["room", id],
    queryFn: async () => {
      const response = await roomsApi.getById(id);
      return response.data.data;
    },
    enabled: !!id,
    staleTime: FIVE_MINUTES,
  });
};

export const useRoomsByCategory = (category: string) => {
  return useQuery<HospitalRoomInfo[]>({
    queryKey: ["rooms", "category", category],
    queryFn: async () => {
      const response = await roomsApi.getByCategory(category);
      return response.data.data;
    },
    enabled: !!category,
    staleTime: FIVE_MINUTES,
  });
};

export const useRoomsByFloor = (floor: number) => {
  return useQuery<HospitalRoomInfo[]>({
    queryKey: ["rooms", "floor", floor],
    queryFn: async () => {
      const response = await roomsApi.getByFloor(floor);
      return response.data.data;
    },
    staleTime: FIVE_MINUTES,
  });
};

export const useSearchRooms = (query: string) => {
  return useQuery<HospitalRoomInfo[]>({
    queryKey: ["rooms", "search", query],
    queryFn: async () => {
      const response = await roomsApi.search(query);
      return response.data.data;
    },
    enabled: query.length > 0,
    staleTime: 60 * 1000,
  });
};

export const useQrAnchors = () => {
  return useQuery<QrAnchor[]>({
    queryKey: ["qrAnchors"],
    queryFn: async () => {
      try {
        const response = await qrAnchorsApi.getAll();
        isUsingFallback = false;
        return response.data.data;
      } catch (error) {
        console.warn("API failed, falling back to static data:", error);
        isUsingFallback = true;
        await loadStaticData();
        if (staticQrAnchors) {
          return staticQrAnchors;
        }
        throw error;
      }
    },
    staleTime: FIVE_MINUTES,
    retry: 1, // Only retry once before falling back
  });
};

export const useQrAnchorById = (qrId: string) => {
  return useQuery<QrAnchor>({
    queryKey: ["qrAnchor", qrId],
    queryFn: async () => {
      const response = await qrAnchorsApi.getById(qrId);
      return response.data.data;
    },
    enabled: !!qrId,
    staleTime: FIVE_MINUTES,
  });
};

export const useQrAnchorsByFloor = (floor: number) => {
  return useQuery<QrAnchor[]>({
    queryKey: ["qrAnchors", "floor", floor],
    queryFn: async () => {
      const response = await qrAnchorsApi.getByFloor(floor);
      return response.data.data;
    },
    staleTime: FIVE_MINUTES,
  });
};

export const useResolveQrCode = (qrCode: string) => {
  return useQuery<QrAnchor>({
    queryKey: ["qrAnchor", "resolve", qrCode],
    queryFn: async () => {
      const response = await qrAnchorsApi.resolve(qrCode);
      return response.data.data;
    },
    enabled: !!qrCode,
    staleTime: FIVE_MINUTES,
  });
};
