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
  "QR-CORR-A1-J1": {
    qrId: "QR-CORR-A1-J1",
    roomId: "IGD",
    svgX: 632.95538,
    svgY: 753.07831,
    label: "Persimpangan Area Pelayanan IGD",
    floor: 1,
  },
  "QR-CORR-A2-J1": {
    qrId: "QR-CORR-A2-J1",
    roomId: "Informasi",
    svgX: 633.01105,
    svgY: 729.92206,
    label: "Persimpangan TRP RJ dan Informasi",
    floor: 1,
  },
  "QR-CORR-B1-J2": {
    qrId: "QR-CORR-B1-J2",
    roomId: "Lab",
    svgX: 865.2005,
    svgY: 516.54614,
    label: "Persimpangan ke Lab",
    floor: 1,
  },
  "QR-CORR-B2-J2": {
    qrId: "QR-CORR-B2-J2",
    roomId: "Farmasi",
    svgX: 989.14642,
    svgY: 515.6015,
    label: "Persimpangan jalan ke Farmasi",
    floor: 1,
  },
  "QR-CORR-C1-J1": {
    qrId: "QR-CORR-C1-J1",
    roomId: "Poliklinik",
    svgX: 1297.0038,
    svgY: 681.64331,
    label: "Persimpangan dari Poliklinik",
    floor: 1,
  },
  "QR-CORR-C2-J3": {
    qrId: "QR-CORR-C2-J3",
    roomId: "Rekam_Medis",
    svgX: 988.59271,
    svgY: 681.49438,
    label: "Persimpangan ke Rekam Medis",
    floor: 1,
  },
  "QR-CORR-D1-J1": {
    qrId: "QR-CORR-D1-J1",
    roomId: "ICU",
    svgX: 538.37146,
    svgY: 255.84256,
    label: "Persimpangan ke ICU",
    floor: 1,
  },
  "QR-CORR-D2-J1": {
    qrId: "QR-CORR-D2-J1",
    roomId: "Rehab_Medik",
    svgX: 781.63397,
    svgY: 680.77075,
    label: "Persimpangan ke Rehab Medik",
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

type GraphNode = { id: string; x: number; y: number };
type Graph = Record<string, Array<{ id: string; weight: number }>>;

const NODE_SNAP_SIZE = 12;
const SAMPLE_STEP = 24;

const distance = (from: { x: number; y: number }, to: { x: number; y: number }): number => {
  const dx = from.x - to.x;
  const dy = from.y - to.y;
  return Math.hypot(dx, dy);
};

const normalizeElementId = (raw: string): string => raw.toLowerCase().trim();

const normalizeElementLabel = (raw: string): string =>
  raw.toLowerCase().replace(/[_-]/g, " ").replace(/\s+/g, " ").trim();

const isBlockedIgdEntranceElement = (idRaw: string, labelRaw: string): boolean => {
  const normalizedId = normalizeElementId(idRaw);
  const normalizedLabel = normalizeElementLabel(labelRaw);

  return normalizedId === "masuk_ke_igd" || normalizedLabel === "masuk ke igd";
};

const isRoadPath = (path: SVGPathElement): boolean => {
  const pathId = path.id?.toLowerCase() || "";
  const pathLabel = (path.getAttribute("inkscape:label") || path.getAttribute("label") || "").toLowerCase();
  if (isBlockedIgdEntranceElement(pathId, pathLabel)) return false;
  return pathId.includes("jalan") || pathLabel.includes("jalan");
};

const isElementNodeLike = (element: Element): element is SVGCircleElement | SVGEllipseElement => {
  const tag = element.tagName.toLowerCase();
  return tag === "circle" || tag === "ellipse";
};

const getNodeCenterFromSvgElement = (element: SVGCircleElement | SVGEllipseElement): { x: number; y: number } | null => {
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
  predicate?: (node: GraphNode) => boolean
): GraphNode | null => {
  const candidates = Object.values(nodes).filter((node) =>
    predicate ? predicate(node) : true
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
  y: number
): GraphNode => {
  const explicitNearby = findNearestNodeWithin(
    nodes,
    { x, y },
    NODE_SNAP_SIZE * 3,
    (node) => explicitNodeIds.has(node.id) && !node.id.startsWith("node_room_")
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

const addEdge = (graph: Graph, fromId: string, toId: string, weight: number) => {
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

const buildRoadGraphFromSvg = (svgDoc: Document): { graph: Graph; nodes: Record<string, GraphNode> } => {
  const graph: Graph = {};
  const nodes: Record<string, GraphNode> = {};

  const explicitNodeIds = new Set<string>();

  const nodeLayer = Array.from(svgDoc.querySelectorAll("g"))
    .find((group) => {
      const layerLabel = (group.getAttribute("inkscape:label") || "").toLowerCase();
      return layerLabel.includes("node jalan") || layerLabel.includes("pathfinding node");
    });

  const explicitNodeElements = nodeLayer
    ? Array.from(nodeLayer.querySelectorAll("circle, ellipse"))
    : Array.from(svgDoc.querySelectorAll("circle[id^='node_'], ellipse[id^='node_']"));

  explicitNodeElements.forEach((element) => {
    if (!isElementNodeLike(element)) return;
    const id = element.id;
    if (!id) return;
    const label = (element.getAttribute("inkscape:label") || element.getAttribute("label") || "");
    if (isBlockedIgdEntranceElement(id, label)) return;
    const center = getNodeCenterFromSvgElement(element);
    if (!center) return;
    nodes[id] = { id, x: center.x, y: center.y };
    explicitNodeIds.add(id);
    if (!graph[id]) graph[id] = [];
  });

  const centerlineLayer = Array.from(svgDoc.querySelectorAll("g")).find((group) => {
    const layerLabel = (group.getAttribute("inkscape:label") || "").toLowerCase();
    return layerLabel.includes("centerline jalan");
  });

  const roadPaths = centerlineLayer
    ? Array.from(centerlineLayer.querySelectorAll("path")).filter(isRoadPath)
    : Array.from(svgDoc.querySelectorAll("path")).filter(isRoadPath);

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
        addEdge(graph, previousNode.id, currentNode.id, distance(previousNode, currentNode));
      }

      previousNode = currentNode;
    }
  });

  return { graph, nodes };
};

const dijkstra = (graph: Graph, startId: string, endId: string): { path: string[]; distance: number } | null => {
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

const getRoomCenter = (svgDoc: Document, roomId: string): { x: number; y: number } | null => {
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
  return {
    x: bbox.x + bbox.width / 2,
    y: bbox.y + bbox.height / 2,
  };
};

const getNearestNodeId = (
  nodes: Record<string, GraphNode>,
  graph: Graph,
  point: { x: number; y: number }
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

const shouldExcludeFromRouting = (roomId: string): boolean => {
  const normalized = roomId.toLowerCase().replace(/_/g, " ");
  return (
    normalized.includes("jalan") ||
    normalized.includes("background") ||
    normalized.includes("unamed") ||
    normalized.includes("area kamar operasi") ||
    normalized.includes("lift lantai 1") ||
    normalized.includes("tangga lantai 1")
  );
};

export const getRoutingRoomIds = (svgDoc?: Document): string[] => {
  const ids = Object.keys(roomInfoBySvgId).filter((roomId) => !shouldExcludeFromRouting(roomId));
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
    (anchor) => anchor.qrId.toUpperCase().replace(/\s+/g, "") === normalized
  );
  if (directMatch) return directMatch;

  const fuzzyPrefixMatch = registryEntries.find((anchor) => {
    const key = anchor.qrId.toUpperCase().replace(/\s+/g, "");
    return normalized.startsWith(key) || key.startsWith(normalized);
  });
  if (fuzzyPrefixMatch) return fuzzyPrefixMatch;

  const roomIdFromLegacyQr = resolveRoomIdFromQrCode(rawQr);
  if (!roomIdFromLegacyQr) return null;

  return registryEntries.find((anchor) => anchor.roomId === roomIdFromLegacyQr) || null;
};

export const resolveRoomIdFromQrCode = (rawCode: string): string | null => {
  const normalized = rawCode.trim().toUpperCase();
  return qrCodeToRoomId[normalized] || null;
};

export const buildRouteForRooms = (
  startRoomId: string,
  endRoomId: string,
  svgDoc: Document,
  options?: {
    startPoint?: { x: number; y: number };
    endPoint?: { x: number; y: number };
  }
): RoomRouteResult | null => {
  const startCenter = getRoomCenter(svgDoc, startRoomId);
  const endCenter = getRoomCenter(svgDoc, endRoomId);
  if (!startCenter || !endCenter) return null;

  const startSourcePoint = options?.startPoint ?? startCenter;
  const endSourcePoint = options?.endPoint ?? endCenter;

  const { graph, nodes } = buildRoadGraphFromSvg(svgDoc);
  const startNodeId = getNearestNodeId(nodes, graph, startSourcePoint);
  const endNodeId = getNearestNodeId(nodes, graph, endSourcePoint);
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
