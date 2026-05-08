import { useQuery } from "@tanstack/react-query";
import { roomsApi, qrAnchorsApi } from "@/services/api";
import { roomInfoBySvgId as staticRoomInfoBySvgId } from "@/data/hospitalRoomInfo";
import { QR_ANCHOR_REGISTRY as staticQrAnchorRegistry } from "@/data/hospitalRouteGraph";
import type { Room } from "@/types/room";
import type { QrAnchor } from "@/types/qrAnchor";

const FIVE_MINUTES = 5 * 60 * 1000;

// ─── Static data fallback (loaded lazily) ────────────────────────────────────
let staticRooms: Room[] | null = null;
let staticQrAnchors: QrAnchor[] | null = null;
let isUsingFallback = false;

const getStaticRooms = (): Room[] => Object.values(staticRoomInfoBySvgId) as Room[];
const getStaticQrAnchors = (): QrAnchor[] => Object.values(staticQrAnchorRegistry) as QrAnchor[];

const loadStaticData = async () => {
  if (!staticRooms || !staticQrAnchors) {
    staticRooms = getStaticRooms();
    staticQrAnchors = getStaticQrAnchors();
  }
};

export const isUsingStaticData = () => isUsingFallback;

// ─── Rooms ────────────────────────────────────────────────────────────────────
export const useRooms = () => {
  return useQuery<Room[]>({
    queryKey: ["rooms"],
    queryFn: async () => {
      try {
        const response = await roomsApi.getAll();
        isUsingFallback = false;
        return response.data.data as Room[];
      } catch (error) {
        console.warn("API failed, falling back to static data:", error);
        isUsingFallback = true;
        await loadStaticData();
        if (staticRooms) return staticRooms;
        throw error;
      }
    },
    placeholderData: getStaticRooms,
    staleTime: FIVE_MINUTES,
    retry: 1,
  });
};

export const useRoomById = (id: string) => {
  return useQuery<Room>({
    queryKey: ["room", id],
    queryFn: async () => {
      const response = await roomsApi.getById(id);
      return response.data.data as Room;
    },
    enabled: !!id,
    staleTime: FIVE_MINUTES,
  });
};

export const useRoomsByCategory = (category: string) => {
  return useQuery<Room[]>({
    queryKey: ["rooms", "category", category],
    queryFn: async () => {
      const response = await roomsApi.getByCategory(category);
      return response.data.data as Room[];
    },
    enabled: !!category,
    staleTime: FIVE_MINUTES,
  });
};

export const useRoomsByFloor = (floor: number) => {
  return useQuery<Room[]>({
    queryKey: ["rooms", "floor", floor],
    queryFn: async () => {
      const response = await roomsApi.getByFloor(floor);
      return response.data.data as Room[];
    },
    staleTime: FIVE_MINUTES,
  });
};

export const useSearchRooms = (query: string) => {
  return useQuery<Room[]>({
    queryKey: ["rooms", "search", query],
    queryFn: async () => {
      const response = await roomsApi.search(query);
      return response.data.data as Room[];
    },
    enabled: query.length > 0,
    staleTime: 60 * 1000,
  });
};

// ─── QR Anchors ───────────────────────────────────────────────────────────────
export const useQrAnchors = () => {
  return useQuery<QrAnchor[]>({
    queryKey: ["qrAnchors"],
    queryFn: async () => {
      try {
        const response = await qrAnchorsApi.getAll();
        isUsingFallback = false;
        return response.data.data as QrAnchor[];
      } catch (error) {
        console.warn("API failed, falling back to static data:", error);
        isUsingFallback = true;
        await loadStaticData();
        if (staticQrAnchors) return staticQrAnchors;
        throw error;
      }
    },
    placeholderData: getStaticQrAnchors,
    staleTime: FIVE_MINUTES,
    retry: 1,
  });
};

export const useQrAnchorById = (qrId: string) => {
  return useQuery<QrAnchor>({
    queryKey: ["qrAnchor", qrId],
    queryFn: async () => {
      const response = await qrAnchorsApi.getById(qrId);
      return response.data.data as QrAnchor;
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
      return response.data.data as QrAnchor[];
    },
    staleTime: FIVE_MINUTES,
  });
};

export const useResolveQrCode = (qrCode: string) => {
  return useQuery<QrAnchor>({
    queryKey: ["qrAnchor", "resolve", qrCode],
    queryFn: async () => {
      // POST /qr-anchors/resolve  { qrCode }
      const response = await qrAnchorsApi.resolve(qrCode);
      return response.data.data as QrAnchor;
    },
    enabled: !!qrCode,
    staleTime: FIVE_MINUTES,
  });
};
