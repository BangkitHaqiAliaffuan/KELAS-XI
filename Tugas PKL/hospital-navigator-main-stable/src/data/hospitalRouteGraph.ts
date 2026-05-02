import { roomInfoBySvgId } from "@/data/hospitalRoomInfo";

export interface RoomRouteResult {
  startRoomId: string;
  endRoomId: string;
  checkpointIds: string[];
  points: Array<{ x: number; y: number }>;
  totalDistance: number;
  floorSegments?: Array<{
    floor: 1 | 2;
    checkpointIds: string[];
    points: Array<{ x: number; y: number }>;
    totalDistance: number;
  }>;
  floorsInvolved?: Array<1 | 2>;
  transitionLabel?: string;
}

export interface QrAnchor {
  qrId: string;
  roomId: string;
  svgX: number;
  svgY: number;
  label: string;
  floor: number;
  routeNodeId?: string;
}

export const QR_ANCHOR_REGISTRY: Record<string, QrAnchor> = {
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
  "QR-F1-N08": {
    qrId: "QR-F1-N08",
    roomId: "Rehab_Medik",
    svgX: 781.63397,
    svgY: 680.77075,
    label: "Persimpangan ke Rehab Medik",
    floor: 1,
  },
  "QR-F1-N09": {
    qrId: "QR-F1-N09",
    roomId: "Lab",
    svgX: 782.692,
    svgY: 517.453,
    label: "Persimpangan masuk ke lab dan Rehab Medik",
    floor: 1,
  },
  "QR-F1-N10": {
    qrId: "QR-F1-N10",
    roomId: "ICU",
    svgX: 463.823,
    svgY: 514.038,
    label: "Persimpangan arah jalan atas",
    floor: 1,
  },
  "QR-F1-N11": {
    qrId: "QR-F1-N11",
    roomId: "ICU",
    svgX: 651.808,
    svgY: 254.74,
    label: "Persimpangan depan ruang kebidanan atas",
    floor: 1,
  },
  "QR-F1-N12": {
    qrId: "QR-F1-N12",
    roomId: "R._Kebidanan",
    svgX: 652.549,
    svgY: 513.945,
    label: "Persimpangan depan ruang kebidanan atas",
    floor: 1,
  },
  "QR-F1-N13": {
    qrId: "QR-F1-N13",
    roomId: "R._Tunggu_Keluarga_Pasien",
    svgX: 780.409,
    svgY: 255.386,
    label: "Persimpangan ke Ruang Rawat Jantung",
    floor: 1,
  },
  "QR-F1-N14": {
    qrId: "QR-F1-N14",
    roomId: "R._Tunggu_Keluarga_Pasien",
    svgX: 864.351,
    svgY: 256.027,
    label: "Persimpangan masuk ke ruang anak",
    floor: 1,
  },
  "QR-F1-N15": {
    qrId: "QR-F1-N15",
    roomId: "R._Tunggu_Keluarga_Pasien",
    svgX: 1000.108,
    svgY: 256.118,
    label: "Persimpangan ke Ruang Rawat Jantung",
    floor: 1,
  },
  "QR-F1-N16": {
    qrId: "QR-F1-N16",
    roomId: "R._Laundry",
    svgX: 1203.5,
    svgY: 255.426,
    label: "Belok ke Ruang Artenis atau Gizi (Atas)",
    floor: 1,
  },
  "QR-F1-N17": {
    qrId: "QR-F1-N17",
    roomId: "R._Internis",
    svgX: 1203.5,
    svgY: 513.333,
    label: "Persimpangan masuk ke ruang anak",
    floor: 1,
  },
  "QR-F1-N18": {
    qrId: "QR-F1-N18",
    roomId: "R._JKN",
    svgX: 1294.753,
    svgY: 513.333,
    label: "Persimpangan masuk ke ruang anak",
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
  "QR-F2-N02": {
    qrId: "QR-F2-N02",
    roomId: "R._Prancis"      ,
    svgX: 999.847,
    svgY: 515.5,
    label: "Persimpangan Jalan R. Rawat Inap Kelas 1",
    floor: 2,
  },
  "QR-F2-N03": {
    qrId: "QR-F2-N03",
    roomId: "Terapi_Okupasi_Lanjutan",
    svgX: 863.9,
    svgY: 515.5,
    label: "Persimpangan Jalan Keluar R. IT & Server",
    floor: 2,
  },
  "QR-F2-N04": {
    qrId: "QR-F2-N04",
    roomId: "Terapi_Okupasi_Lanjutan",
    svgX: 780.732,
    svgY: 515.5,
    label: "Persimpangan Atas Terapi Okupasi Lanjutan",
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
  "QR-F2-N06": {
    qrId: "QR-F2-N06",
    roomId: "Radioterapi",
    svgX: 463.875,
    svgY: 514.5,
    label: "Belok ke Jalan Atas",
    floor: 2,
  },
  "QR-F2-N07": {
    qrId: "QR-F2-N07",
    roomId: "R._Direktur___Manajemen",
    svgX: 463.875,
    svgY: 256.942,
    label: "Belok ke Jalan Atas",
    floor: 2,
  },
  "QR-F2-N08": {
    qrId: "QR-F2-N08",
    roomId: "R._HRD___Kepegawaian",
    svgX: 653.031,
    svgY: 255.344,
    label: "Persimpangan Jalan Atas R. HRD & Kepegawaian",
    floor: 2,
    routeNodeId: "Persimpangan_Jalan_Atas_R._HRD___Kepegawaian",
  },
  "QR-F2-N09": {
    qrId: "QR-F2-N09",
    roomId: "R._Meeting",
    svgX: 780.188,
    svgY: 255.344,
    label: "Persimpangan Jalan Depan R. Tumbuh Kembang Anak",
    floor: 2,
  },
  "QR-F2-N10": {
    qrId: "QR-F2-N10",
    roomId: "R._Meeting",
    svgX: 863.756,
    svgY: 255.344,
    label: "Persimpangan Jalan Keluar R. Tumbuh Kembang Anak",
    floor: 2,
  },
  "QR-F2-N11": {
    qrId: "QR-F2-N11",
    roomId: "R._Meeting",
    svgX: 999.697,
    svgY: 255.344,
    label: "Persimpangan Jalan R. Rawat Inap Kelas 1 & VIP",
    floor: 2,
  },
  "QR-F2-N12": {
    qrId: "QR-F2-N12",
    roomId: "R._Laundry_2",
    svgX: 1202.698,
    svgY: 255.344,
    label: "Persimpangan Jalan Keluar R. Tumbuh Kembang Anak",
    floor: 2,
  },
  "QR-F2-N13": {
    qrId: "QR-F2-N13",
    roomId: "Lobby_Lantai_2",
    svgX: 1297.43,
    svgY: 681.151,
    label: "Persimpangan Jalan R. Rawat Inap Kelas 3",
    floor: 2,
  },
  "QR-F2-N14": {
    qrId: "QR-F2-N14",
    roomId: "Lobby_Lantai_2",
    svgX: 988.5,
    svgY: 681.151,
    label: "Persimpangan Jalan R. Rawat Inap Kelas 3",
    floor: 2,
  },
  "QR-F2-N15": {
    qrId: "QR-F2-N15",
    roomId: "Lobby_Lantai_2",
    svgX: 781.5,
    svgY: 681.151,
    label: "Persimpangan Jalan R. Rawat Inap Kelas 3",
    floor: 2,
  },
  // Lahan Parkir — floor: 0 (peta parkir terpisah, bukan lantai RS)
  "QR-PK-N01": {
    qrId: "QR-PK-N01",
    roomId: "Parking_Lantai_1",
    svgX: 929.478,
    svgY: 417.228,
    label: "Belok ke Area Parkir Khusus Tenaga Medis",
    floor: 0,
  },
  // Lahan Parkir Lantai 2 — floor: -1 (peta parkir lantai 2 terpisah)
  "QR-PK2-N01": {
    qrId: "QR-PK2-N01",
    roomId: "Parking_Lantai_2",
    svgX: 471.46345,
    svgY: 708,
    label: "Akses Jembatan ke Gedung Rumah Sakit Lantai 2",
    floor: -1,
  },
};

const qrCodeToRoomId: Record<string, string> = {
  "QR-IGD": "IGD",
  "QR-INFORMASI": "Informasi",
  "QR-ICU": "ICU",
  "QR-LAB": "Lab",
  "QR-FARMASI": "Farmasi",
  "QR-RAD": "Radiologi",
  "QR-RM": "Rekam_Medis",
  "QR-POLI": "Poliklinik",
  "QR-MUSHOLLA": "Musholla",
};

const ROOM_QR_PREFIXES = ["QR-R-", "QR-ROOM-"] as const;

const normalizeRoomToken = (value: string): string =>
  value.toUpperCase().replace(/[^A-Z0-9]/g, "");

const roomTokenToRoomId: Record<string, string> = Object.keys(roomInfoBySvgId).reduce(
  (acc, roomId) => {
    const token = normalizeRoomToken(roomId);
    if (!acc[token]) {
      acc[token] = roomId;
    }
    return acc;
  },
  {} as Record<string, string>,
);

export const buildRoomQrCode = (roomId: string): string => `QR-R-${roomId.toUpperCase()}`;

export const getAllRoomQrCodes = (): Record<string, string> =>
  Object.keys(roomInfoBySvgId).reduce(
    (acc, roomId) => {
      acc[roomId] = buildRoomQrCode(roomId);
      return acc;
    },
    {} as Record<string, string>,
  );

type GraphNode = { id: string; x: number; y: number };
type Graph = Record<string, Array<{ id: string; weight: number }>>;
type RouteEndpointResolution = {
  anchorNodeId: string;
  graphNodeId: string;
};

const NODE_SNAP_SIZE = 12;
const SAMPLE_STEP = 8;
const EXPLICIT_NODE_CONNECT_THRESHOLD = 56;

const distance = (
  from: { x: number; y: number },
  to: { x: number; y: number },
): number => {
  const dx = from.x - to.x;
  const dy = from.y - to.y;
  return Math.hypot(dx, dy);
};

const normalizeElementId = (raw: string): string => raw.toLowerCase().trim();

const normalizeElementLabel = (raw: string): string =>
  raw.toLowerCase().replace(/[_-]/g, " ").replace(/\s+/g, " ").trim();

const isBlockedIgdEntranceElement = (
  idRaw: string,
  labelRaw: string,
): boolean => {
  const normalizedId = normalizeElementId(idRaw);
  const normalizedLabel = normalizeElementLabel(labelRaw);

  return normalizedId === "masuk_ke_igd" || normalizedLabel === "masuk ke igd";
};

const EXPLICIT_ROUTE_PATH_IDS = new Set([
  "masuk_ke_kamar_mayat",
  "keluar_menuju_kamar_mayat",
  "belok_menuju_kamar_mayat",
  "path5",
  // Lantai 2 connector segments around lift/reception that don't include "jalan" in id/label.
  "masuk_ke_tangga-2",
  "masuk_ke_lift-7",
  "masuk_lobby_lantai_2_-_belakang_lift_",
  "path100",
  "masuk_ke_terapi_okupasi_lanjutan_menuju_ke_edukasi_keluarga",
  "menuju_ke_edukasi_keluarga_dan_pasien",
  // Parking SVG — paths that connect areas but don't have "jalan" in their id.
  "masuk_ke_area_parkir_sepeda_motor_pengunjung_atas",
  "masuk_ke_area_parkir_sepeda_motor_pengunjung_bawah",
  "keluar_dari_area_parkir_sepeda_motor_atas",
  "masuk_ke_area_parkir_sepeda_motor_khusus_tenaga_medis",
  "masuk_ke_tangga_pengunjung_ke_lantai_2_lahan_parkir",
  "naik_ke_parkir_lantai_2_khusus_mobil",
  "pertigaan_untuk_masuk_ke_area_parkir_sepeda_motor_bawah_atas",
  "belokan_keluar_dari_area_parkir_sepeda_motor_atas__opsional_",
  "keluar_dari_area_parkir_sepeda_motor_atas__opsional_",
  "belokan_keluar_area_parkir",
  "belok_ke_ramp_parkir",
  "persimpangan_keluar_dari_area_parkir_dan_tangga_pengunjung",
  // Parking L2 — bridge access path to hospital L2
  "akses_jembatan_menuju_gedung_rumah_sakit_lantai_2",
]);

const KAMAR_MAYAT_PREFERRED_NODE_IDS = [
  "Check_Point_Kamar_Mayat",
  "Belok_Masuk_ke_Kamar_Mayat",
  "Belok_ke_Kamar_Mayat",
] as const;

const ROOM_SPECIAL_ROUTE_NODE_IDS: Record<string, readonly string[]> = {
  "R._Direktur___Manajemen": ["Persimpangan_ke_R._Istirahat_Perawat"],
  Lift_Lantai_1: ["Check_Point_Lift"],
  "Lift_Lantai_1-2": ["Check_Point_Lift_Turun"],
  Tangga_Lantai_1: ["Check_Point_Tangga"],
  "Tangga_Lantai_1-7": ["Check_Point_Tangga_Turun"],
  // Keep evacuation stairs isolated from the main-stair checkpoint so floor transitions stay consistent.
  // Floor 1 SVG uses legacy id "path4" for evacuation checkpoint label.
  Tangga_Evakuasi_Lantai_1: ["path4", "Check_Point_Tangga_Evakuasi"],
  Tangga_Evakuasi_Lantai_2: ["Check_Point_Tangga_Evakuasi"],
  // Parking: route via the dedicated checkpoint node in the parking SVG.
  Parking_Lantai_1: ["Check_Point_Tangga_Pengunjung", "Persimpangan_Keluar_dari_Area_Parkir_dan_Tangga_Pengunjung"],
  // Parking L2 should stop on the QR anchor itself, not the bridge checkpoint.
  Parking_Lantai_2: ["node_room_Parking_Lantai_2"],
  // Virtual connector room used when routing hospital → parking; snaps to the parking exit checkpoint.
  Check_Point_Lahan_Parkir_Connector: ["Check_Point_Lahan_Parkir", "Belok_masuk_ke_Lahan_Parkir", "Belok_ke_Lahan_Parkir"],
};

const isRoadPath = (
  path: SVGPathElement,
  options?: { allowIgdEntrancePath?: boolean },
): boolean => {
  const pathId = path.id?.toLowerCase() || "";
  const pathLabel = (
    path.getAttribute("inkscape:label") ||
    path.getAttribute("label") ||
    ""
  ).toLowerCase();
  if (
    isBlockedIgdEntranceElement(pathId, pathLabel) &&
    !options?.allowIgdEntrancePath
  ) {
    return false;
  }
  if (EXPLICIT_ROUTE_PATH_IDS.has(pathId)) return true;
  return pathId.includes("jalan") || pathLabel.includes("jalan");
};

const isCenterlineRoadPath = (
  path: SVGPathElement,
  options?: { allowIgdEntrancePath?: boolean },
): boolean => {
  const pathId = path.id?.toLowerCase() || "";
  const pathLabel = (
    path.getAttribute("inkscape:label") ||
    path.getAttribute("label") ||
    ""
  ).toLowerCase();

  if (
    isBlockedIgdEntranceElement(pathId, pathLabel) &&
    !options?.allowIgdEntrancePath
  ) {
    return false;
  }

  return true;
};

const isElementNodeLike = (
  element: Element,
): element is SVGCircleElement | SVGEllipseElement => {
  const tag = element.tagName.toLowerCase();
  return tag === "circle" || tag === "ellipse";
};

const getNodeCenterFromSvgElement = (
  element: SVGCircleElement | SVGEllipseElement,
): { x: number; y: number } | null => {
  const cx = Number(element.getAttribute("cx") || "NaN");
  const cy = Number(element.getAttribute("cy") || "NaN");
  if (!Number.isFinite(cx) || !Number.isFinite(cy)) return null;
  return { x: cx, y: cy };
};

const toNodeId = (x: number, y: number): string => {
  const gx = Math.round(x / NODE_SNAP_SIZE);
  const gy = Math.round(y / NODE_SNAP_SIZE);
  return `n_${gx}_${gy}`;
};

const findNearestNodeWithin = (
  nodes: Record<string, GraphNode>,
  point: { x: number; y: number },
  threshold: number,
  predicate?: (node: GraphNode) => boolean,
): GraphNode | null => {
  const candidates = Object.values(nodes).filter((node) =>
    predicate ? predicate(node) : true,
  );

  let nearest: GraphNode | null = null;
  let nearestDistance = threshold;

  candidates.forEach((node) => {
    const d = distance(node, point);
    if (d <= nearestDistance) {
      nearest = node;
      nearestDistance = d;
    }
  });

  return nearest;
};

const ensureNode = (
  nodes: Record<string, GraphNode>,
  explicitNodeIds: Set<string>,
  x: number,
  y: number,
): GraphNode => {
  const explicitNearby = findNearestNodeWithin(
    nodes,
    { x, y },
    NODE_SNAP_SIZE * 3,
    (node) => explicitNodeIds.has(node.id) && !node.id.startsWith("node_room_"),
  );
  if (explicitNearby) return explicitNearby;

  const id = toNodeId(x, y);
  if (!nodes[id]) {
    nodes[id] = { id, x, y };
    return nodes[id];
  }

  nodes[id] = {
    id,
    x: (nodes[id].x + x) / 2,
    y: (nodes[id].y + y) / 2,
  };
  return nodes[id];
};

const addEdge = (
  graph: Graph,
  fromId: string,
  toId: string,
  weight: number,
) => {
  if (!graph[fromId]) graph[fromId] = [];
  if (!graph[toId]) graph[toId] = [];

  const existingForward = graph[fromId].find((edge) => edge.id === toId);
  if (!existingForward || existingForward.weight > weight) {
    if (existingForward) existingForward.weight = weight;
    else graph[fromId].push({ id: toId, weight });
  }

  const existingBackward = graph[toId].find((edge) => edge.id === fromId);
  if (!existingBackward || existingBackward.weight > weight) {
    if (existingBackward) existingBackward.weight = weight;
    else graph[toId].push({ id: fromId, weight });
  }
};

const connectExplicitNodesToNearestRoadNode = (
  graph: Graph,
  nodes: Record<string, GraphNode>,
  explicitNodeIds: Set<string>,
) => {
  const roadNodes = Object.values(nodes).filter(
    (node) =>
      !explicitNodeIds.has(node.id) && !node.id.startsWith("node_room_") && (graph[node.id]?.length || 0) > 0,
  );

  explicitNodeIds.forEach((nodeId) => {
    if (nodeId.startsWith("node_room_")) return;
    const explicitNode = nodes[nodeId];
    if (!explicitNode) return;
    if ((graph[nodeId]?.length || 0) > 0) return;

    let nearestRoadNode: GraphNode | null = null;
    let nearestDistance = EXPLICIT_NODE_CONNECT_THRESHOLD;

    roadNodes.forEach((roadNode) => {
      const d = distance(explicitNode, roadNode);
      if (d <= nearestDistance) {
        nearestRoadNode = roadNode;
        nearestDistance = d;
      }
    });

    if (!nearestRoadNode) return;
    addEdge(graph, nodeId, nearestRoadNode.id, nearestDistance);
  });
};

const buildRoadGraphFromSvg = (
  svgDoc: Document,
  options?: { allowIgdEntrancePath?: boolean },
): { graph: Graph; nodes: Record<string, GraphNode> } => {
  const graph: Graph = {};
  const nodes: Record<string, GraphNode> = {};

  const explicitNodeIds = new Set<string>();
  const isParkingFloor2Svg = Boolean(
    svgDoc.getElementById("Akses_Jembatan_Menuju_Gedung_Rumah_Sakit_Lantai_2"),
  );

  const nodeLayer = Array.from(svgDoc.querySelectorAll("g")).find((group) => {
    const layerLabel = (
      group.getAttribute("inkscape:label") || ""
    ).toLowerCase();
    return (
      layerLabel.includes("node jalan") ||
      layerLabel.includes("pathfinding node") ||
      (isParkingFloor2Svg && layerLabel === "node")
    );
  });

  const generatedNodeElements = isParkingFloor2Svg
    ? Array.from(
        svgDoc.querySelectorAll(
          `#${GENERATED_ROOM_NODE_LAYER_ID} circle, #${GENERATED_ROOM_NODE_LAYER_ID} ellipse`,
        ),
      )
    : [];

  const explicitNodeElements = nodeLayer
    ? Array.from(nodeLayer.querySelectorAll("circle, ellipse"))
    : Array.from(
        svgDoc.querySelectorAll("circle[id^='node_'], ellipse[id^='node_']"),
      );

  [...explicitNodeElements, ...generatedNodeElements].forEach((element) => {
    if (!isElementNodeLike(element)) return;
    const id = element.id;
    if (!id) return;
    const label =
      element.getAttribute("inkscape:label") ||
      element.getAttribute("label") ||
      "";
    if (isBlockedIgdEntranceElement(id, label)) return;
    const center = getNodeCenterFromSvgElement(element);
    if (!center) return;
    nodes[id] = { id, x: center.x, y: center.y };
    explicitNodeIds.add(id);
    if (!graph[id]) graph[id] = [];
  });

  const centerlineLayer = Array.from(svgDoc.querySelectorAll("g")).find(
    (group) => {
      const layerLabel = (
        group.getAttribute("inkscape:label") || ""
      ).toLowerCase();
      return (
        layerLabel.includes("centerline jalan") ||
        (isParkingFloor2Svg && layerLabel === "centerline")
      );
    },
  );

  const globalRoadPaths = Array.from(svgDoc.querySelectorAll("path")).filter(
    (path) => isRoadPath(path, options),
  );
  const centerlineRoadPaths = centerlineLayer
    ? Array.from(centerlineLayer.querySelectorAll("path")).filter((path) =>
        isCenterlineRoadPath(path, options),
      )
    : [];
  // If a dedicated centerline layer exists, use it as the single routing source.
  // Mixing thick road-area paths with centerlines lets Dijkstra drift to road edges.
  const roadPaths = centerlineRoadPaths.length
    ? centerlineRoadPaths
    : globalRoadPaths;

  roadPaths.forEach((path) => {
    const totalLength = path.getTotalLength();
    if (!Number.isFinite(totalLength) || totalLength <= 0) return;

    const samples = Math.max(2, Math.ceil(totalLength / SAMPLE_STEP));
    let previousNode: GraphNode | null = null;

    for (let i = 0; i <= samples; i += 1) {
      const t = i / samples;
      const point = path.getPointAtLength(totalLength * t);
      const currentNode = ensureNode(nodes, explicitNodeIds, point.x, point.y);

      if (previousNode && previousNode.id !== currentNode.id) {
        addEdge(
          graph,
          previousNode.id,
          currentNode.id,
          distance(previousNode, currentNode),
        );
      }

      previousNode = currentNode;
    }
  });

  connectExplicitNodesToNearestRoadNode(graph, nodes, explicitNodeIds);

  return { graph, nodes };
};

const dijkstra = (
  graph: Graph,
  startId: string,
  endId: string,
): { path: string[]; distance: number } | null => {
  if (!graph[startId] || !graph[endId]) return null;

  const distances: Record<string, number> = {};
  const previous: Record<string, string | null> = {};
  const unvisited = new Set(Object.keys(graph));

  Object.keys(graph).forEach((nodeId) => {
    distances[nodeId] = Number.POSITIVE_INFINITY;
    previous[nodeId] = null;
  });

  distances[startId] = 0;

  while (unvisited.size > 0) {
    let currentId: string | null = null;
    let currentDistance = Number.POSITIVE_INFINITY;

    unvisited.forEach((nodeId) => {
      if (distances[nodeId] < currentDistance) {
        currentDistance = distances[nodeId];
        currentId = nodeId;
      }
    });

    if (!currentId || currentDistance === Number.POSITIVE_INFINITY) break;
    if (currentId === endId) break;

    unvisited.delete(currentId);

    graph[currentId].forEach(({ id: neighborId, weight }) => {
      if (!unvisited.has(neighborId)) return;
      const alt = distances[currentId!] + weight;
      if (alt < distances[neighborId]) {
        distances[neighborId] = alt;
        previous[neighborId] = currentId;
      }
    });
  }

  if (distances[endId] === Number.POSITIVE_INFINITY) return null;

  const path: string[] = [];
  let current: string | null = endId;
  while (current) {
    path.unshift(current);
    current = previous[current];
  }

  return { path, distance: distances[endId] };
};

const GENERATED_ROOM_NODE_LAYER_ID = "mapviewer-generated-room-nodes";

const ensureGeneratedRoomAnchorNode = (
  svgDoc: Document,
  roomId: string,
  center: { x: number; y: number },
): void => {
  try {
    // If roomId already starts with "virtual_" or other special prefixes, use it as-is
    // Otherwise, add "node_room_" prefix for room-based nodes
    const nodeId = roomId.startsWith("virtual_") || roomId.startsWith("Check_Point_") 
      ? roomId 
      : `node_room_${roomId}`;
    const existing = svgDoc.getElementById(nodeId);
    if (existing) return;

    const rootSvg = svgDoc.querySelector("svg");
    if (!rootSvg) return;

    const namespace = "http://www.w3.org/2000/svg";

    let layer = svgDoc.getElementById(
      GENERATED_ROOM_NODE_LAYER_ID,
    ) as unknown as SVGGElement | null;
    if (!layer) {
      layer = svgDoc.createElementNS(namespace, "g");
      layer.setAttribute("id", GENERATED_ROOM_NODE_LAYER_ID);
      layer.setAttribute("style", "display:none;opacity:0");
      layer.setAttribute("data-generated", "true");
      rootSvg.appendChild(layer);
    }

    const roomNode = svgDoc.createElementNS(namespace, "circle");
    roomNode.setAttribute("id", nodeId);
    roomNode.setAttribute("cx", String(center.x));
    roomNode.setAttribute("cy", String(center.y));
    roomNode.setAttribute("r", "1");
    roomNode.setAttribute("data-generated", "true");
    roomNode.setAttribute("style", "display:none");
    layer.appendChild(roomNode);
  } catch {
    return;
  }
};

const getRoomCenter = (
  svgDoc: Document,
  roomId: string,
): { x: number; y: number } | null => {
  // Try to find the node with the exact ID first (for virtual nodes)
  let roomAnchor = svgDoc.getElementById(roomId);
  
  // If not found, try with node_room_ prefix (for regular room nodes)
  if (!roomAnchor) {
    roomAnchor = svgDoc.getElementById(`node_room_${roomId}`);
  }
  
  if (roomAnchor && roomAnchor.tagName.toLowerCase() === "circle") {
    const x = Number(roomAnchor.getAttribute("cx") || "NaN");
    const y = Number(roomAnchor.getAttribute("cy") || "NaN");
    if (Number.isFinite(x) && Number.isFinite(y)) {
      return { x, y };
    }
  }

  const target = svgDoc.getElementById(roomId);
  if (!target) return null;
  const maybeGraphics = target as unknown as { getBBox?: () => DOMRect };
  if (typeof maybeGraphics.getBBox !== "function") return null;

  const bbox = maybeGraphics.getBBox();
  const center = {
    x: bbox.x + bbox.width / 2,
    y: bbox.y + bbox.height / 2,
  };

  ensureGeneratedRoomAnchorNode(svgDoc, roomId, center);
  return center;
};

const getNearestNodeId = (
  nodes: Record<string, GraphNode>,
  graph: Graph,
  point: { x: number; y: number },
): string | null => {
  const nodeValues = Object.values(nodes).filter((node) => {
    if (node.id.toLowerCase().startsWith("node_room_")) return false;
    return (graph[node.id]?.length || 0) > 0;
  });
  if (!nodeValues.length) return null;

  let nearestId: string | null = null;
  let nearestDistance = Number.POSITIVE_INFINITY;

  nodeValues.forEach((node) => {
    const d = distance(node, point);
    if (d < nearestDistance) {
      nearestDistance = d;
      nearestId = node.id;
    }
  });

  return nearestId;
};

const isIgdRelatedRoom = (roomId: string): boolean => {
  const normalized = roomId.toLowerCase().replace(/[^a-z0-9]/g, "");
  return normalized.includes("igd");
};

const isKamarMayatRoom = (roomId: string): boolean => roomId === "K._Mayat";

const normalizeRouteToken = (value: string): string =>
  value
    .toLowerCase()
    .replace(/check[\s._-]*point/gi, "")
    .replace(/persimpangan/gi, "")
    .replace(/keluar/gi, "")
    .replace(/masuk/gi, "")
    .replace(/menuju/gi, "")
    .replace(/ruang|ruangan/gi, "r")
    .replace(/direktur/g, "manajemen")
    .replace(/[^a-z0-9]/g, "");

const getRoomCheckpointCandidateIds = (
  roomId: string,
  nodes: Record<string, GraphNode>,
): string[] => {
  const roomInfo = roomInfoBySvgId[roomId];
  const roomTokens = new Set(
    [roomId, roomInfo?.name ?? ""]
      .filter(Boolean)
      .map(normalizeRouteToken)
      .filter(Boolean),
  );

  return Object.keys(nodes).filter((nodeId) => {
    if (!/^check[_-]?point/i.test(nodeId)) return false;

    const normalizedNodeId = normalizeRouteToken(nodeId);
    if (!normalizedNodeId) return false;

    for (const roomToken of roomTokens) {
      if (!roomToken) continue;
      if (
        normalizedNodeId === roomToken ||
        normalizedNodeId.includes(roomToken) ||
        roomToken.includes(normalizedNodeId)
      ) {
        return true;
      }
    }

    return false;
  });
};

const getBestMatchingCheckpointNodeId = (
  roomId: string,
  candidateNodeIds: string[],
  nodes: Record<string, GraphNode>,
  roomCenter: { x: number; y: number },
): string | null => {
  const roomInfo = roomInfoBySvgId[roomId];
  const roomTokens = [roomId, roomInfo?.name ?? ""]
    .filter(Boolean)
    .map(normalizeRouteToken)
    .filter(Boolean);

  const scoredCandidates = candidateNodeIds
    .map((nodeId) => {
      const node = nodes[nodeId];
      if (!node) return null;

      const normalizedNodeId = normalizeRouteToken(nodeId);
      const exactScore = roomTokens.some((token) => normalizedNodeId === token)
        ? 2
        : roomTokens.some(
              (token) =>
                normalizedNodeId.includes(token) || token.includes(normalizedNodeId),
            )
          ? 1
          : 0;

      return {
        nodeId,
        exactScore,
        distanceToRoom: distance(node, roomCenter),
      };
    })
    .filter(
      (
        candidate,
      ): candidate is {
        nodeId: string;
        exactScore: number;
        distanceToRoom: number;
      } => Boolean(candidate),
    )
    .sort((a, b) => {
      if (b.exactScore !== a.exactScore) return b.exactScore - a.exactScore;
      return a.distanceToRoom - b.distanceToRoom;
    });

  return scoredCandidates[0]?.nodeId || null;
};

const resolveRoomCheckpointNodeId = (
  roomId: string,
  nodes: Record<string, GraphNode>,
  roomCenter: { x: number; y: number },
): string | null => {
  const specialNodeIds = ROOM_SPECIAL_ROUTE_NODE_IDS[roomId];
  if (specialNodeIds?.length) {
    for (const specialNodeId of specialNodeIds) {
      if (nodes[specialNodeId]) return specialNodeId;
    }
    return null;
  }

  const checkpointCandidateIds = getRoomCheckpointCandidateIds(roomId, nodes);
  if (!checkpointCandidateIds.length) return null;

  const bestCheckpointNodeId = getBestMatchingCheckpointNodeId(
    roomId,
    checkpointCandidateIds,
    nodes,
    roomCenter,
  );
  if (!bestCheckpointNodeId) return null;

  return bestCheckpointNodeId;
};

const resolveRouteEndpoint = (
  preferredAnchorNodeId: string | null,
  nodes: Record<string, GraphNode>,
  graph: Graph,
  fallbackPoint: { x: number; y: number },
): RouteEndpointResolution | null => {
  if (
    preferredAnchorNodeId &&
    nodes[preferredAnchorNodeId] &&
    (graph[preferredAnchorNodeId]?.length || 0) > 0
  ) {
    return {
      anchorNodeId: preferredAnchorNodeId,
      graphNodeId: preferredAnchorNodeId,
    };
  }

  if (preferredAnchorNodeId && nodes[preferredAnchorNodeId]) {
    const nearestGraphNodeId = getNearestNodeId(
      nodes,
      graph,
      nodes[preferredAnchorNodeId],
    );
    if (nearestGraphNodeId) {
      return {
        anchorNodeId: preferredAnchorNodeId,
        graphNodeId: nearestGraphNodeId,
      };
    }
  }

  const nearestFallbackNodeId = getNearestNodeId(nodes, graph, fallbackPoint);
  if (!nearestFallbackNodeId) return null;

  return {
    anchorNodeId: nearestFallbackNodeId,
    graphNodeId: nearestFallbackNodeId,
  };
};

const resolvePreferredNodeId = (
  preferredNodeIds: readonly string[],
  nodes: Record<string, GraphNode>,
  graph: Graph,
  fallbackPoint: { x: number; y: number },
): string | null => {
  for (const preferredNodeId of preferredNodeIds) {
    const preferredNode = nodes[preferredNodeId];
    if (!preferredNode) continue;
    if ((graph[preferredNodeId]?.length || 0) <= 0) continue;
    return preferredNodeId;
  }

  return getNearestNodeId(nodes, graph, fallbackPoint);
};

const resolvePreferredStartNodeId = (
  startRoomId: string,
  nodes: Record<string, GraphNode>,
  graph: Graph,
  fallbackStartPoint: { x: number; y: number },
  useExactPoint?: boolean,
  exactStartNodeId?: string,
): RouteEndpointResolution | null => {
  // If useExactPoint is true, ONLY use the exact coordinates provided (for QR scans)
  // Skip all checkpoint/room-based logic
  if (useExactPoint) {
    if (exactStartNodeId) {
      const exactStartNode = nodes[exactStartNodeId];
      if (exactStartNode) {
        console.log(`[Routing] Using exact start node: ${exactStartNodeId}`);
        return resolveRouteEndpoint(
          exactStartNodeId,
          nodes,
          graph,
          fallbackStartPoint,
        );
      }
      console.warn(`[Routing] Exact start node not found: ${exactStartNodeId}`);
    }

    console.log(`[Routing] Using exact point for start: (${fallbackStartPoint.x}, ${fallbackStartPoint.y})`);
    const nearestNodeId = getNearestNodeId(nodes, graph, fallbackStartPoint);
    if (nearestNodeId) {
      console.log(`[Routing] Found nearest node: ${nearestNodeId}`);
      return {
        anchorNodeId: nearestNodeId,
        graphNodeId: nearestNodeId,
      };
    }
    console.warn(`[Routing] No nearest node found for exact point`);
    return null;
  }

  // Normal flow: use checkpoint/room-based logic
  const roomCheckpointNodeId = resolveRoomCheckpointNodeId(
    startRoomId,
    nodes,
    fallbackStartPoint,
  );
  if (roomCheckpointNodeId) {
    return resolveRouteEndpoint(
      roomCheckpointNodeId,
      nodes,
      graph,
      fallbackStartPoint,
    );
  }

  if (isKamarMayatRoom(startRoomId)) {
    return resolveRouteEndpoint(
      resolvePreferredNodeId(
        KAMAR_MAYAT_PREFERRED_NODE_IDS,
        nodes,
        graph,
        fallbackStartPoint,
      ),
      nodes,
      graph,
      fallbackStartPoint,
    );
  }

  if (startRoomId === "IGD") {
    const igdExitNodeId = "Persimpangan_Keluar_IGD";
    const igdExitNode = nodes[igdExitNodeId];
    if (igdExitNode && (graph[igdExitNodeId]?.length || 0) > 0) {
      return {
        anchorNodeId: igdExitNodeId,
        graphNodeId: igdExitNodeId,
      };
    }
  }

  return resolveRouteEndpoint(null, nodes, graph, fallbackStartPoint);
};

const resolvePreferredEndNodeId = (
  endRoomId: string,
  nodes: Record<string, GraphNode>,
  graph: Graph,
  fallbackEndPoint: { x: number; y: number },
): RouteEndpointResolution | null => {
  const roomCheckpointNodeId = resolveRoomCheckpointNodeId(
    endRoomId,
    nodes,
    fallbackEndPoint,
  );
  if (roomCheckpointNodeId) {
    return resolveRouteEndpoint(
      roomCheckpointNodeId,
      nodes,
      graph,
      fallbackEndPoint,
    );
  }

  if (isKamarMayatRoom(endRoomId)) {
    return resolveRouteEndpoint(
      resolvePreferredNodeId(
        KAMAR_MAYAT_PREFERRED_NODE_IDS,
        nodes,
        graph,
        fallbackEndPoint,
      ),
      nodes,
      graph,
      fallbackEndPoint,
    );
  }

  return resolveRouteEndpoint(null, nodes, graph, fallbackEndPoint);
};

const shouldExcludeFromRouting = (roomId: string): boolean => {
  const normalized = roomId.toLowerCase().replace(/_/g, " ");
  return (
    normalized.includes("jalan") ||
    normalized.includes("background") ||
    normalized.includes("unamed") ||
    normalized.includes("area kamar operasi")
  );
};

export const getRoutingRoomIds = (svgDoc?: Document): string[] => {
  const ids = Object.keys(roomInfoBySvgId).filter(
    (roomId) => !shouldExcludeFromRouting(roomId),
  );
  
  // Always include parking in routing options (they have QR anchors even though they're not in the hospital SVG)
  if (!ids.includes("Parking_Lantai_1")) {
    ids.push("Parking_Lantai_1");
  }
  if (!ids.includes("Parking_Lantai_2")) {
    ids.push("Parking_Lantai_2");
  }
  
  if (!svgDoc) return ids;

  return ids.filter((roomId) => {
    // Parking is special — it's in a separate SVG, so skip the element check
    if (roomId === "Parking_Lantai_1" || roomId === "Parking_Lantai_2") return true;
    
    const element = svgDoc.getElementById(roomId);
    return Boolean(element && element.tagName.toLowerCase() === "path");
  });
};

export const resolveQrAnchor = (rawQr: string): QrAnchor | null => {
  const normalized = rawQr.trim().toUpperCase().replace(/\s+/g, "");
  if (!normalized) return null;

  const registryEntries = Object.values(QR_ANCHOR_REGISTRY);

  const directMatch = registryEntries.find(
    (anchor) => anchor.qrId.toUpperCase().replace(/\s+/g, "") === normalized,
  );
  if (directMatch) return directMatch;

  const fuzzyPrefixMatch = registryEntries.find((anchor) => {
    const key = anchor.qrId.toUpperCase().replace(/\s+/g, "");
    return normalized.startsWith(key) || key.startsWith(normalized);
  });
  if (fuzzyPrefixMatch) return fuzzyPrefixMatch;

  const roomIdFromLegacyQr = resolveRoomIdFromQrCode(rawQr);
  if (!roomIdFromLegacyQr) return null;

  return (
    registryEntries.find((anchor) => anchor.roomId === roomIdFromLegacyQr) ||
    null
  );
};

export const resolveRoomIdFromQrCode = (rawCode: string): string | null => {
  const normalized = rawCode.trim().toUpperCase();
  if (!normalized) return null;

  const legacyMatch = qrCodeToRoomId[normalized];
  if (legacyMatch) return legacyMatch;

  const compact = normalized.replace(/\s+/g, "");
  for (const prefix of ROOM_QR_PREFIXES) {
    if (!compact.startsWith(prefix)) continue;
    const payload = compact.slice(prefix.length);
    if (!payload) return null;
    const roomId = roomTokenToRoomId[normalizeRoomToken(payload)];
    return roomId || null;
  }

  return null;
};

export const buildRouteForRooms = (
  startRoomId: string,
  endRoomId: string,
  svgDoc: Document,
  options?: {
    startPoint?: { x: number; y: number };
    endPoint?: { x: number; y: number };
    useExactStartPoint?: boolean;
    startNodeId?: string;
  },
): RoomRouteResult | null => {
  console.log(`[buildRouteForRooms] startRoomId: ${startRoomId}, endRoomId: ${endRoomId}`);
  console.log(`[buildRouteForRooms] options:`, options);
  
  const startCenter = getRoomCenter(svgDoc, startRoomId);
  const endCenter = getRoomCenter(svgDoc, endRoomId);
  if (!startCenter || !endCenter) return null;

  const startSourcePoint = options?.startPoint ?? startCenter;
  const endSourcePoint = options?.endPoint ?? endCenter;

  console.log(`[buildRouteForRooms] startSourcePoint:`, startSourcePoint);
  console.log(`[buildRouteForRooms] useExactStartPoint:`, options?.useExactStartPoint);

  const allowIgdEntrancePath =
    isIgdRelatedRoom(startRoomId) || isIgdRelatedRoom(endRoomId);

  const { graph, nodes } = buildRoadGraphFromSvg(svgDoc, {
    allowIgdEntrancePath,
  });
  const startResolution = resolvePreferredStartNodeId(
    startRoomId,
    nodes,
    graph,
    startSourcePoint,
    options?.useExactStartPoint,
    options?.startNodeId,
  );
  const endResolution = resolvePreferredEndNodeId(
    endRoomId,
    nodes,
    graph,
    endSourcePoint,
  );
  if (!startResolution || !endResolution) return null;

  const shortest = dijkstra(
    graph,
    startResolution.graphNodeId,
    endResolution.graphNodeId,
  );
  if (!shortest) return null;

  const checkpointIds = [...shortest.path];
  if (checkpointIds[0] !== startResolution.anchorNodeId) {
    checkpointIds.unshift(startResolution.anchorNodeId);
  }
  if (checkpointIds[checkpointIds.length - 1] !== endResolution.anchorNodeId) {
    checkpointIds.push(endResolution.anchorNodeId);
  }

  const roadPoints = checkpointIds
    .map((nodeId) => nodes[nodeId])
    .filter(Boolean)
    .map((node) => ({ x: node.x, y: node.y }));

  const points = roadPoints;

  if (points.length < 2) return null;

  return {
    startRoomId,
    endRoomId,
    checkpointIds,
    points,
    totalDistance: shortest.distance,
  };
};

/**
 * Inject a virtual anchor node at the given SVG coordinates into svgDoc so
 * buildRouteForRooms can use it as a start/end point.  The injected node is
 * idempotent (safe to call multiple times with the same id).
 */
export const injectVirtualAnchorNode = (
  svgDoc: Document,
  nodeId: string,
  x: number,
  y: number,
): void => {
  ensureGeneratedRoomAnchorNode(svgDoc, nodeId, { x, y });
};

/**
 * Build a route from a raw SVG coordinate point to a room, or vice-versa,
 * or from point to point within a single SVG document.
 * The coordinate is injected as a virtual node so the normal buildRouteForRooms machinery can resolve it.
 */
export const buildRouteFromPoint = (
  pointRoomId: string,
  pointX: number,
  pointY: number,
  otherRoomId: string,
  svgDoc: Document,
  direction: "point_to_room" | "room_to_point" | "point_to_point" = "point_to_room",
): RoomRouteResult | null => {
  // Inject the virtual node so getRoomCenter can find it
  injectVirtualAnchorNode(svgDoc, pointRoomId, pointX, pointY);

  if (direction === "point_to_point") {
    // Both start and end are points (coordinates)
    // otherRoomId is treated as a node ID, and we need to get its coordinates
    const otherNode = svgDoc.getElementById(otherRoomId);
    if (!otherNode) {
      console.warn(`[buildRouteFromPoint] point_to_point: otherRoomId "${otherRoomId}" not found in SVG`);
      return null;
    }
    
    // Get coordinates of the other node
    let otherX: number, otherY: number;
    if (otherNode.tagName.toLowerCase() === "circle" || otherNode.tagName.toLowerCase() === "ellipse") {
      otherX = Number(otherNode.getAttribute("cx") || "NaN");
      otherY = Number(otherNode.getAttribute("cy") || "NaN");
    } else {
      // Try to get center from room
      const otherCenter = getRoomCenter(svgDoc, otherRoomId);
      if (!otherCenter) {
        console.warn(`[buildRouteFromPoint] point_to_point: Cannot get center for "${otherRoomId}"`);
        return null;
      }
      otherX = otherCenter.x;
      otherY = otherCenter.y;
    }
    
    if (!Number.isFinite(otherX) || !Number.isFinite(otherY)) {
      console.warn(`[buildRouteFromPoint] point_to_point: Invalid coordinates for "${otherRoomId}"`);
      return null;
    }
    
    // Inject virtual node for the other point as well
    injectVirtualAnchorNode(svgDoc, otherRoomId, otherX, otherY);
    
    // Route from first point to second point, using exact coordinates for both
    return buildRouteForRooms(pointRoomId, otherRoomId, svgDoc, {
      startPoint: { x: pointX, y: pointY },
      endPoint: { x: otherX, y: otherY },
      useExactStartPoint: true,
    });
  }

  const startId = direction === "point_to_room" ? pointRoomId : otherRoomId;
  const endId = direction === "point_to_room" ? otherRoomId : pointRoomId;

  return buildRouteForRooms(startId, endId, svgDoc, {
    startPoint: direction === "point_to_room" ? { x: pointX, y: pointY } : undefined,
    endPoint: direction === "room_to_point" ? { x: pointX, y: pointY } : undefined,
    useExactStartPoint: direction === "point_to_room",
  });
};
