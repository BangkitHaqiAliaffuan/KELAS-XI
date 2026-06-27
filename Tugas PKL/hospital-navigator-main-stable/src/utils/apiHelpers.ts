import type { Room } from "@/types/room";
import type { QrAnchor } from "@/types/qrAnchor";

export const roomsArrayToObject = (
  rooms: Room[],
): Record<string, Room> => {
  return rooms.reduce((acc, room) => {
    acc[room.id] = room;
    return acc;
  }, {} as Record<string, Room>);
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
