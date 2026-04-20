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
]);

const KAMAR_MAYAT_PREFERRED_NODE_IDS = [
  "Check_Point_Kamar_Mayat",
  "Belok_Masuk_ke_Kamar_Mayat",
  "Belok_ke_Kamar_Mayat",
] as const;

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

  const roadPaths = centerlineLayer
    ? Array.from(centerlineLayer.querySelectorAll("path")).filter((path) =>
        isRoadPath(path, options),
      )
    : Array.from(svgDoc.querySelectorAll("path")).filter((path) =>
        isRoadPath(path, options),
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

  const points = roadPoints;

  if (points.length < 2) return null;

  return {
    startRoomId,
    endRoomId,
    checkpointIds: shortest.path,
    points,
    totalDistance: shortest.distance,
  };
};
