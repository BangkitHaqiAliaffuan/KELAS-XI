import { roomInfoBySvgId } from "@/data/hospitalRoomInfo";

export interface RoomRouteResult {
  startRoomId: string;
  endRoomId: string;
  checkpointIds: string[];
  points: Array<{ x: number; y: number }>;
  totalDistance: number;
}

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

const isRoadPath = (path: SVGPathElement): boolean => {
  const pathId = path.id?.toLowerCase() || "";
  const pathLabel = (path.getAttribute("inkscape:label") || path.getAttribute("label") || "").toLowerCase();
  return pathId.includes("jalan") || pathLabel.includes("jalan");
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
  idPrefix?: string
): GraphNode | null => {
  const candidates = Object.values(nodes).filter((node) =>
    idPrefix ? node.id.startsWith(idPrefix) : true
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

const ensureNode = (nodes: Record<string, GraphNode>, x: number, y: number): GraphNode => {
  const explicitNearby = findNearestNodeWithin(nodes, { x, y }, NODE_SNAP_SIZE * 2.2, "node_j");
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

  const explicitNodes = Array.from(svgDoc.querySelectorAll("circle[id^='node_']")) as SVGCircleElement[];
  explicitNodes.forEach((circle) => {
    const id = circle.id;
    const x = Number(circle.getAttribute("cx") || "0");
    const y = Number(circle.getAttribute("cy") || "0");
    if (!id || !Number.isFinite(x) || !Number.isFinite(y)) return;
    nodes[id] = { id, x, y };
    if (!graph[id]) graph[id] = [];
  });

  const roadPaths = Array.from(svgDoc.querySelectorAll("path")).filter(isRoadPath);

  roadPaths.forEach((path) => {
    const totalLength = path.getTotalLength();
    if (!Number.isFinite(totalLength) || totalLength <= 0) return;

    const samples = Math.max(2, Math.ceil(totalLength / SAMPLE_STEP));
    let previousNode: GraphNode | null = null;

    for (let i = 0; i <= samples; i += 1) {
      const t = i / samples;
      const point = path.getPointAtLength(totalLength * t);
      const currentNode = ensureNode(nodes, point.x, point.y);

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
    if (node.id.startsWith("node_room_")) return false;
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

export const getRoutingRoomIds = (): string[] => Object.keys(roomInfoBySvgId);

export const resolveRoomIdFromQrCode = (rawCode: string): string | null => {
  const normalized = rawCode.trim().toUpperCase();
  return qrCodeToRoomId[normalized] || null;
};

export const buildRouteForRooms = (
  startRoomId: string,
  endRoomId: string,
  svgDoc: Document
): RoomRouteResult | null => {
  const startCenter = getRoomCenter(svgDoc, startRoomId);
  const endCenter = getRoomCenter(svgDoc, endRoomId);
  if (!startCenter || !endCenter) return null;

  const { graph, nodes } = buildRoadGraphFromSvg(svgDoc);
  const startNodeId = getNearestNodeId(nodes, graph, startCenter);
  const endNodeId = getNearestNodeId(nodes, graph, endCenter);
  if (!startNodeId || !endNodeId) return null;

  const shortest = dijkstra(graph, startNodeId, endNodeId);
  if (!shortest) return null;

  const roadPoints = shortest.path
    .map((nodeId) => nodes[nodeId])
    .filter(Boolean)
    .map((node) => ({ x: node.x, y: node.y }));

  const points = [startCenter, ...roadPoints, endCenter];

  if (points.length < 2) return null;

  const startConnector = startNodeId ? distance(startCenter, nodes[startNodeId]) : 0;
  const endConnector = endNodeId ? distance(endCenter, nodes[endNodeId]) : 0;
  const totalDistance = shortest.distance + startConnector + endConnector;

  return {
    startRoomId,
    endRoomId,
    checkpointIds: shortest.path,
    points,
    totalDistance,
  };
};
