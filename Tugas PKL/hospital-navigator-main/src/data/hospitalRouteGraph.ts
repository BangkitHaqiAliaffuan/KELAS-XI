import { roomInfoBySvgId } from "@/data/hospitalRoomInfo";

export interface RoomRouteResult {
  startRoomId: string;
  endRoomId: string;
  checkpointIds: string[];
  points: Array<{ x: number; y: number }>;
  totalDistance: number;
}

export interface QrAnchor {
  qrId: string;
  roomId: string;
  svgX: number;
  svgY: number;
  label: string;
  floor: number;
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
    roomId: "R._Prancis",
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

const NODE_SNAP_SIZE = 12;
const SAMPLE_STEP = 24;
const ROUTE_POINT_MERGE_EPSILON = 4;
const ROUTE_STRAIGHT_ANGLE_THRESHOLD = 18;
const ROUTE_LINE_DEVIATION_EPSILON = 3;
const ROUTE_AXIS_ALIGN_EPSILON = 6;

const distance = (
  from: { x: number; y: number },
  to: { x: number; y: number },
): number => {
  const dx = from.x - to.x;
  const dy = from.y - to.y;
  return Math.hypot(dx, dy);
};

const signedTurnAngleDegrees = (
  prev: { x: number; y: number },
  curr: { x: number; y: number },
  next: { x: number; y: number },
): number => {
  const v1 = { x: curr.x - prev.x, y: curr.y - prev.y };
  const v2 = { x: next.x - curr.x, y: next.y - curr.y };
  const cross = v1.x * v2.y - v1.y * v2.x;
  const dot = v1.x * v2.x + v1.y * v2.y;
  return (Math.atan2(cross, dot) * 180) / Math.PI;
};

const perpendicularDistanceToLine = (
  point: { x: number; y: number },
  lineStart: { x: number; y: number },
  lineEnd: { x: number; y: number },
): number => {
  const lineLength = distance(lineStart, lineEnd);
  if (lineLength === 0) return distance(point, lineStart);

  const area = Math.abs(
    (lineEnd.x - lineStart.x) * (lineStart.y - point.y) -
      (lineStart.x - point.x) * (lineEnd.y - lineStart.y),
  );
  return area / lineLength;
};

const mergeNearbyRoutePoints = (
  points: Array<{ x: number; y: number }>,
): Array<{ x: number; y: number }> => {
  if (points.length <= 1) return points;

  const merged = [points[0]];
  for (let i = 1; i < points.length; i += 1) {
    const current = points[i];
    const prev = merged[merged.length - 1];
    if (distance(prev, current) <= ROUTE_POINT_MERGE_EPSILON) continue;
    merged.push(current);
  }

  return merged;
};

const removeMinorRouteTurns = (
  points: Array<{ x: number; y: number }>,
): Array<{ x: number; y: number }> => {
  if (points.length <= 2) return points;

  const simplified = [points[0]];

  for (let i = 1; i < points.length - 1; i += 1) {
    const prev = simplified[simplified.length - 1];
    const curr = points[i];
    const next = points[i + 1];

    const turnAngle = Math.abs(signedTurnAngleDegrees(prev, curr, next));
    const lineDeviation = perpendicularDistanceToLine(curr, prev, next);

    if (
      turnAngle <= ROUTE_STRAIGHT_ANGLE_THRESHOLD &&
      lineDeviation <= ROUTE_LINE_DEVIATION_EPSILON
    ) {
      continue;
    }

    simplified.push(curr);
  }

  simplified.push(points[points.length - 1]);
  return simplified;
};

const alignAxisRuns = (
  points: Array<{ x: number; y: number }>,
): Array<{ x: number; y: number }> => {
  if (points.length <= 2) return points;

  const aligned = points.map((point) => ({ ...point }));

  const classifySegment = (
    a: { x: number; y: number },
    b: { x: number; y: number },
  ): "horizontal" | "vertical" | null => {
    const dx = Math.abs(b.x - a.x);
    const dy = Math.abs(b.y - a.y);

    if (dy <= ROUTE_AXIS_ALIGN_EPSILON && dx > dy) return "horizontal";
    if (dx <= ROUTE_AXIS_ALIGN_EPSILON && dy > dx) return "vertical";
    return null;
  };

  let i = 0;
  while (i < aligned.length - 1) {
    const segmentType = classifySegment(aligned[i], aligned[i + 1]);
    if (!segmentType) {
      i += 1;
      continue;
    }

    let j = i;
    while (j < aligned.length - 1) {
      const typeAtJ = classifySegment(aligned[j], aligned[j + 1]);
      if (typeAtJ !== segmentType) break;
      j += 1;
    }

    if (j > i) {
      if (segmentType === "horizontal") {
        const yAverage =
          aligned.slice(i, j + 2).reduce((sum, point) => sum + point.y, 0) /
          (j + 2 - i);
        for (let k = i; k <= j + 1; k += 1) {
          aligned[k].y = yAverage;
        }
      } else {
        const xAverage =
          aligned.slice(i, j + 2).reduce((sum, point) => sum + point.x, 0) /
          (j + 2 - i);
        for (let k = i; k <= j + 1; k += 1) {
          aligned[k].x = xAverage;
        }
      }
    }

    i = j + 1;
  }

  return aligned;
};

const smoothRoutePoints = (
  points: Array<{ x: number; y: number }>,
): Array<{ x: number; y: number }> => {
  if (points.length <= 2) return points;

  const merged = mergeNearbyRoutePoints(points);
  const coarseSimplified = removeMinorRouteTurns(merged);
  const axisAligned = alignAxisRuns(coarseSimplified);
  const finalSimplified = removeMinorRouteTurns(axisAligned);

  return finalSimplified.length >= 2 ? finalSimplified : points;
};

const polylineDistance = (points: Array<{ x: number; y: number }>): number => {
  if (points.length <= 1) return 0;
  let total = 0;
  for (let i = 1; i < points.length; i += 1) {
    total += distance(points[i - 1], points[i]);
  }
  return total;
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
]);

const KAMAR_MAYAT_PREFERRED_NODE_IDS = [
  "Check_Point_Kamar_Mayat",
  "Belok_Masuk_ke_Kamar_Mayat",
  "Belok_ke_Kamar_Mayat",
] as const;

const ROOM_SPECIAL_ROUTE_NODE_IDS: Record<string, readonly string[]> = {
  "R._Direktur___Manajemen": ["Persimpangan_ke_R._Istirahat_Perawat"],
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

const buildRoadGraphFromSvg = (
  svgDoc: Document,
  options?: { allowIgdEntrancePath?: boolean },
): { graph: Graph; nodes: Record<string, GraphNode> } => {
  const graph: Graph = {};
  const nodes: Record<string, GraphNode> = {};

  const explicitNodeIds = new Set<string>();

  const nodeLayer = Array.from(svgDoc.querySelectorAll("g")).find((group) => {
    const layerLabel = (
      group.getAttribute("inkscape:label") || ""
    ).toLowerCase();
    return (
      layerLabel.includes("node jalan") ||
      layerLabel.includes("pathfinding node")
    );
  });

  const explicitNodeElements = nodeLayer
    ? Array.from(nodeLayer.querySelectorAll("circle, ellipse"))
    : Array.from(
        svgDoc.querySelectorAll("circle[id^='node_'], ellipse[id^='node_']"),
      );

  explicitNodeElements.forEach((element) => {
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
      return layerLabel.includes("centerline jalan");
    },
  );

  const globalRoadPaths = Array.from(svgDoc.querySelectorAll("path")).filter(
    (path) => isRoadPath(path, options),
  );
  const centerlineRoadPaths = centerlineLayer
    ? Array.from(centerlineLayer.querySelectorAll("path")).filter((path) =>
        isRoadPath(path, options),
      )
    : [];
  const roadPaths = Array.from(
    new Set(
      centerlineRoadPaths.length
        ? [...centerlineRoadPaths, ...globalRoadPaths]
        : globalRoadPaths,
    ),
  );

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
    const nodeId = `node_room_${roomId}`;
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
  const roomAnchor = svgDoc.getElementById(`node_room_${roomId}`);
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
  graph: Graph,
  roomCenter: { x: number; y: number },
): string | null => {
  const specialNodeIds = ROOM_SPECIAL_ROUTE_NODE_IDS[roomId];
  if (specialNodeIds?.length) {
    return resolvePreferredNodeId(specialNodeIds, nodes, graph, roomCenter);
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
  if ((graph[bestCheckpointNodeId]?.length || 0) <= 0) return null;

  return bestCheckpointNodeId;
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
): string | null => {
  const roomCheckpointNodeId = resolveRoomCheckpointNodeId(
    startRoomId,
    nodes,
    graph,
    fallbackStartPoint,
  );
  if (roomCheckpointNodeId) {
    return roomCheckpointNodeId;
  }

  if (isKamarMayatRoom(startRoomId)) {
    return resolvePreferredNodeId(
      KAMAR_MAYAT_PREFERRED_NODE_IDS,
      nodes,
      graph,
      fallbackStartPoint,
    );
  }

  if (startRoomId === "IGD") {
    const igdExitNodeId = "Persimpangan_Keluar_IGD";
    const igdExitNode = nodes[igdExitNodeId];
    if (igdExitNode && (graph[igdExitNodeId]?.length || 0) > 0) {
      return igdExitNodeId;
    }
  }

  return getNearestNodeId(nodes, graph, fallbackStartPoint);
};

const resolvePreferredEndNodeId = (
  endRoomId: string,
  nodes: Record<string, GraphNode>,
  graph: Graph,
  fallbackEndPoint: { x: number; y: number },
): string | null => {
  const roomCheckpointNodeId = resolveRoomCheckpointNodeId(
    endRoomId,
    nodes,
    graph,
    fallbackEndPoint,
  );
  if (roomCheckpointNodeId) {
    return roomCheckpointNodeId;
  }

  if (isKamarMayatRoom(endRoomId)) {
    return resolvePreferredNodeId(
      KAMAR_MAYAT_PREFERRED_NODE_IDS,
      nodes,
      graph,
      fallbackEndPoint,
    );
  }

  return getNearestNodeId(nodes, graph, fallbackEndPoint);
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
  if (!svgDoc) return ids;

  return ids.filter((roomId) => {
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
  },
): RoomRouteResult | null => {
  const startCenter = getRoomCenter(svgDoc, startRoomId);
  const endCenter = getRoomCenter(svgDoc, endRoomId);
  if (!startCenter || !endCenter) return null;

  const startSourcePoint = options?.startPoint ?? startCenter;
  const endSourcePoint = options?.endPoint ?? endCenter;

  const allowIgdEntrancePath =
    isIgdRelatedRoom(startRoomId) || isIgdRelatedRoom(endRoomId);

  const { graph, nodes } = buildRoadGraphFromSvg(svgDoc, {
    allowIgdEntrancePath,
  });
  const startNodeId = resolvePreferredStartNodeId(
    startRoomId,
    nodes,
    graph,
    startSourcePoint,
  );
  const endNodeId = resolvePreferredEndNodeId(
    endRoomId,
    nodes,
    graph,
    endSourcePoint,
  );
  if (!startNodeId || !endNodeId) return null;

  const shortest = dijkstra(graph, startNodeId, endNodeId);
  if (!shortest) return null;

  const roadPoints = shortest.path
    .map((nodeId) => nodes[nodeId])
    .filter(Boolean)
    .map((node) => ({ x: node.x, y: node.y }));

  const points = smoothRoutePoints(roadPoints);

  if (points.length < 2) return null;

  return {
    startRoomId,
    endRoomId,
    checkpointIds: shortest.path,
    points,
    totalDistance: polylineDistance(points),
  };
};
