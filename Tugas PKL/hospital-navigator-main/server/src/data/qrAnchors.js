/**
 * QR Anchor Registry Database
 * Registry QR code dengan koordinat SVG untuk navigasi
 */

export const qrAnchors = {
  "QR-F1-N01": {
    qrId: "QR-F1-N01",
    roomId: "IGD",
    svgX: 632.95538,
    svgY: 753.07831,
    label: "Persimpangan Area Pelayanan IGD",
    floor: 1,
  },
  "QR-F1-N03": {
    qrId: "QR-F1-N03",
    roomId: "Lab",
    svgX: 865.2005,
    svgY: 516.54614,
    label: "Persimpangan ke Lab",
    floor: 1,
    routeNodeId: "Persimpangan_ke_Lab",
  },
  "QR-F1-N04": {
    qrId: "QR-F1-N04",
    roomId: "Farmasi",
    svgX: 989.14642,
    svgY: 515.6015,
    label: "Persimpangan jalan ke Farmasi",
    floor: 1,
  },
  "QR-F1-N05": {
    qrId: "QR-F1-N05",
    roomId: "Poliklinik",
    svgX: 1297.0038,
    svgY: 681.64331,
    label: "Persimpangan dari Poliklinik",
    floor: 1,
  },
  "QR-F1-N06": {
    qrId: "QR-F1-N06",
    roomId: "Rekam_Medis",
    svgX: 988.59271,
    svgY: 681.49438,
    label: "Persimpangan ke Rekam Medis",
    floor: 1,
  },
  "QR-F1-N07": {
    qrId: "QR-F1-N07",
    roomId: "ICU",
    svgX: 465.359,
    svgY: 255.37,
    label: "Persimpangan ke ICU",
    floor: 1,
  },
  "QR-F2-N01": {
    qrId: "QR-F2-N01",
    roomId: "Lobby_Lantai_2",
    svgX: 1296.753,
    svgY: 515.5,
    label: "Testing",
    floor: 2,
  },
  "QR-F2-N05": {
    qrId: "QR-F2-N05",
    roomId: "R._PACS",
    svgX: 652.894,
    svgY: 515.5,
    label: "Persimpangan Jalan Bawah R. HRD & Kepegawaian",
    floor: 2,
    routeNodeId: "Persimpangan_Jalan_Bawah_R._HRD___Kepegawaian",
  },
  "QR-PK-N01": {
    qrId: "QR-PK-N01",
    roomId: "Parking_Lantai_1",
    svgX: 929.478,
    svgY: 417.228,
    label: "Belok ke Area Parkir Khusus Tenaga Medis",
    floor: 0,
    routeNodeId: "Belok_ke_Area_Parkir_Sepeda_Motor_Khusus_Tenaga_Medis",
  },
  "QR-PK2-N01": {
    qrId: "QR-PK2-N01",
    roomId: "Parking_Lantai_2",
    svgX: 471.46345,
    svgY: 708,
    label: "Akses Jembatan ke Gedung Rumah Sakit Lantai 2",
    floor: -1,
  },
};

/**
 * Get all QR anchors
 */
export const getAllQrAnchors = () => {
  return Object.values(qrAnchors);
};

/**
 * Get QR anchor by QR ID
 */
export const getQrAnchorById = (qrId) => {
  return qrAnchors[qrId] || null;
};

/**
 * Get QR anchors by room ID
 */
export const getQrAnchorsByRoomId = (roomId) => {
  return Object.values(qrAnchors).filter((anchor) => anchor.roomId === roomId);
};

/**
 * Get QR anchors by floor
 */
export const getQrAnchorsByFloor = (floor) => {
  return Object.values(qrAnchors).filter((anchor) => anchor.floor === floor);
};

/**
 * Search QR anchors by label
 */
export const searchQrAnchors = (query) => {
  const lowerQuery = query.toLowerCase();
  return Object.values(qrAnchors).filter(
    (anchor) =>
      anchor.label.toLowerCase().includes(lowerQuery) ||
      anchor.qrId.toLowerCase().includes(lowerQuery) ||
      anchor.roomId.toLowerCase().includes(lowerQuery)
  );
};

/**
 * Resolve QR code to anchor
 */
export const resolveQrCode = (rawQr) => {
  const normalized = rawQr.trim().toUpperCase().replace(/\s+/g, "");
  if (!normalized) return null;

  // Direct match
  const directMatch = Object.values(qrAnchors).find(
    (anchor) => anchor.qrId.toUpperCase().replace(/\s+/g, "") === normalized
  );
  if (directMatch) return directMatch;

  // Fuzzy prefix match
  const fuzzyMatch = Object.values(qrAnchors).find((anchor) => {
    const key = anchor.qrId.toUpperCase().replace(/\s+/g, "");
    return normalized.startsWith(key) || key.startsWith(normalized);
  });
  
  return fuzzyMatch || null;
};

/**
 * Add or update a QR anchor
 */
export const upsertQrAnchor = (anchorData) => {
  const { qrId, roomId, svgX, svgY, label, floor, routeNodeId } = anchorData;
  
  if (!qrId || !roomId || svgX === undefined || svgY === undefined) {
    throw new Error('QR anchor must have qrId, roomId, svgX, and svgY');
  }

  qrAnchors[qrId] = {
    qrId,
    roomId,
    svgX: Number(svgX),
    svgY: Number(svgY),
    label: label || '',
    floor: floor !== undefined ? Number(floor) : 1,
    ...(routeNodeId && { routeNodeId }),
  };

  return qrAnchors[qrId];
};

/**
 * Delete a QR anchor
 */
export const deleteQrAnchor = (qrId) => {
  if (!qrAnchors[qrId]) {
    return false;
  }
  delete qrAnchors[qrId];
  return true;
};

/**
 * Get statistics
 */
export const getQrAnchorStats = () => {
  const anchors = Object.values(qrAnchors);
  const byFloor = {};
  
  anchors.forEach((anchor) => {
    const floor = anchor.floor;
    byFloor[floor] = (byFloor[floor] || 0) + 1;
  });

  return {
    total: anchors.length,
    byFloor,
    rooms: new Set(anchors.map((a) => a.roomId)).size,
  };
};

export default {
  getAllQrAnchors,
  getQrAnchorById,
  getQrAnchorsByRoomId,
  getQrAnchorsByFloor,
  searchQrAnchors,
  resolveQrCode,
  upsertQrAnchor,
  deleteQrAnchor,
  getQrAnchorStats,
};
