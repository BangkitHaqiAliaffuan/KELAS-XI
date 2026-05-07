import type { HospitalRoomInfo } from "@/data/hospitalRoomInfo";
import type { QrAnchor } from "@/data/hospitalRouteGraph";

export const roomsArrayToObject = (
  rooms: HospitalRoomInfo[],
): Record<string, HospitalRoomInfo> => {
  return rooms.reduce((acc, room) => {
    acc[room.id] = room;
    return acc;
  }, {} as Record<string, HospitalRoomInfo>);
};

export const qrAnchorsArrayToObject = (
  anchors: QrAnchor[],
): Record<string, QrAnchor> => {
  return anchors.reduce((acc, anchor) => {
    acc[anchor.qrId] = anchor;
    return acc;
  }, {} as Record<string, QrAnchor>);
};

export const resolveQrAnchorFromRegistry = (
  rawQr: string,
  registry: Record<string, QrAnchor>,
): QrAnchor | null => {
  const normalized = rawQr.trim().toUpperCase().replace(/\s+/g, "");
  if (!normalized) return null;

  const registryEntries = Object.values(registry);

  const directMatch = registryEntries.find(
    (anchor) => anchor.qrId.toUpperCase().replace(/\s+/g, "") === normalized,
  );
  if (directMatch) return directMatch;

  return (
    registryEntries.find((anchor) => {
      const key = anchor.qrId.toUpperCase().replace(/\s+/g, "");
      return normalized.startsWith(key) || key.startsWith(normalized);
    }) || null
  );
};
