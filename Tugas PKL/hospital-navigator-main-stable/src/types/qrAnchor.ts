/**
 * QR Anchor Type Definitions
 * Mirrors the backend QR anchor data model
 */

export interface QrAnchor {
  qrId: string;
  roomId: string;
  svgX: number;
  svgY: number;
  label: string;
  floor: number;
  routeNodeId?: string;
}

export interface QrAnchorStats {
  total: number;
  byFloor: Record<string, number>;
  rooms: number;
}
