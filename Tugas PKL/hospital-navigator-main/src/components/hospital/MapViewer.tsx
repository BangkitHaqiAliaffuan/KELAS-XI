import { useRef, useState, useCallback, useEffect } from "react";
import {
  Plus,
  Minus,
  Locate,
  X,
  Navigation,
  QrCode,
} from "lucide-react";
import {
  roomInfoBySvgId,
  roomLabelConfigBySvgId,
  type HospitalRoomInfo,
} from "@/data/hospitalRoomInfo";
import {
  buildRouteForRooms,
  QR_ANCHOR_REGISTRY,
  getRoutingRoomIds,
  resolveQrAnchor,
  resolveRoomIdFromQrCode,
  type QrAnchor,
  type RoomRouteResult,
} from "@/data/hospitalRouteGraph";
import {
  buildNavigationSteps,
  getActiveStepIndex,
  type NavigationStep,
  type TurnType,
} from "@/lib/navigationInstructions";

interface MapViewerProps {
  selectedLocation?: HospitalRoomInfo | null;
  onClearSelection?: () => void;
  highlightCategory?: "departments" | "facilities" | "emergency" | null;
  onStartNavigation?: (options?: { mode?: "manual" | "qr" }) => void;
  navigationStartRequest?: {
    requestId: number;
    roomId: string;
    source: "manual" | "qr";
  } | null;
  onNavigationStartRequestHandled?: (requestId: number) => void;
}

const MapViewer = ({
  selectedLocation,
  onClearSelection,
  highlightCategory,
  onStartNavigation,
  navigationStartRequest,
  onNavigationStartRequestHandled,
}: MapViewerProps) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const mapRef = useRef<HTMLDivElement>(null);
  const objectRef = useRef<HTMLObjectElement>(null);

  const [scale, setScale] = useState(1);
  const [position, setPosition] = useState({ x: 0, y: 0 });
  const [isDragging, setIsDragging] = useState(false);

  // Single source of truth for drag state — stored in refs to avoid stale closures
  const isDraggingRef = useRef(false);
  const dragOriginRef = useRef({ x: 0, y: 0 });
  const positionRef = useRef({ x: 0, y: 0 });     // always mirrors position state
  const positionAtDragStart = useRef({ x: 0, y: 0 });
  const hasDraggedRef = useRef(false);              // true once mouse moved > DRAG_THRESHOLD
  const scaleRef = useRef(scale);

  // Shared handlers stored in refs so setupSvgRoomInteraction can attach the
  // EXACT same function instances to svgDoc (no duplicate logic, no stale closures)
  const applyDragMoveRef = useRef<(hostX: number, hostY: number, buttons: number) => void>(() => {});
  const applyDragEndRef = useRef<() => void>(() => {});

  const [activeRoomId, setActiveRoomId] = useState<string | null>(null);
  const [activeRoomInfo, setActiveRoomInfo] = useState<HospitalRoomInfo | null>(null);
  const [activeMarkerPosition, setActiveMarkerPosition] = useState<{
    x: number;
    y: number;
  } | null>(null);
  const [currentUserMarkerPosition, setCurrentUserMarkerPosition] = useState<{
    x: number;
    y: number;
  } | null>(null);
  const [showCurrentUserMarker, setShowCurrentUserMarker] = useState(false);
  const [svgReadyVersion, setSvgReadyVersion] = useState(0);

  const [routingRoomIds, setRoutingRoomIds] = useState<string[]>(() => getRoutingRoomIds());
  const routingRoomOptions = routingRoomIds
    .map((roomId) => roomInfoBySvgId[roomId])
    .filter((room): room is HospitalRoomInfo => Boolean(room))
    .sort((a, b) => a.name.localeCompare(b.name));

  const [locationInputMode, setLocationInputMode] = useState<"dropdown" | "qr">("dropdown");
  const [isPathfindingDebugVisible, setIsPathfindingDebugVisible] = useState(true);
  const [startRoomId, setStartRoomId] = useState<string>(routingRoomOptions[0]?.id || "");
  const [endRoomId, setEndRoomId] = useState<string>(routingRoomOptions[1]?.id || routingRoomOptions[0]?.id || "");
  const [qrCodeInput, setQrCodeInput] = useState("");
  const [routeDebugMessage, setRouteDebugMessage] = useState("");
  const [activeRoute, setActiveRoute] = useState<RoomRouteResult | null>(null);
  const [isLiveMode, setIsLiveMode] = useState(false);
  const [liveModeStatus, setLiveModeStatus] = useState("");
  const [liveSvgPoint, setLiveSvgPoint] = useState<{ x: number; y: number } | null>(null);
  const [showQrAnchorHints, setShowQrAnchorHints] = useState(true);
  const [lastQrAnchor, setLastQrAnchor] = useState<QrAnchor | null>(null);
  const [qrCalibrationHistory, setQrCalibrationHistory] = useState<QrAnchor[]>([]);
  const [navSteps, setNavSteps] = useState<NavigationStep[]>([]);
  const [activeStepIndex, setActiveStepIndex] = useState(0);
  const hasAppliedAutoStartRef = useRef(false);
  const geoWatchIdRef = useRef<number | null>(null);
  const liveOriginRef = useRef<{ lat: number; lng: number; svgX: number; svgY: number } | null>(null);
  const liveSvgPointRef = useRef<{ x: number; y: number } | null>(null);
  const gpsBufferRef = useRef<Array<{ x: number; y: number }>>([]);
  const startRoomIdRef = useRef(startRoomId);
  const endRoomIdRef = useRef(endRoomId);
  const lastRerouteAtRef = useRef(0);
  const preferRoomCenterStartRef = useRef(false);

  const MIN_SCALE = 0.4;
  const MAX_SCALE = 5;
  const ZOOM_STEP = 0.2;
  const DRAG_THRESHOLD = 4; // px before we consider it a real drag
  const LIVE_METERS_TO_SVG_PX = 2.2;
  const LIVE_REROUTE_INTERVAL_MS = 1200;
  const LIVE_NEAREST_ROOM_MAX_DISTANCE = 240;
  const GPS_BUFFER_SIZE = 5;
  const HIDDEN_DYNAMIC_LABEL_ROOM_IDS = new Set(["Lift_Lantai_1", "Tangga_Lantai_1", "Tangga_Lantai_1-2"]);
  const qrAnchors = Object.values(QR_ANCHOR_REGISTRY).sort((a, b) => a.qrId.localeCompare(b.qrId));

  // Keep refs in sync with state
  useEffect(() => { scaleRef.current = scale; }, [scale]);
  useEffect(() => { positionRef.current = position; }, [position]);
  useEffect(() => { liveSvgPointRef.current = liveSvgPoint; }, [liveSvgPoint]);
  useEffect(() => { startRoomIdRef.current = startRoomId; }, [startRoomId]);
  useEffect(() => { endRoomIdRef.current = endRoomId; }, [endRoomId]);

  useEffect(() => {
    if (!routingRoomOptions.length) return;

    const validRoomIds = new Set(routingRoomOptions.map((room) => room.id));
    if (!startRoomId || !validRoomIds.has(startRoomId)) {
      setStartRoomId(routingRoomOptions[0].id);
    }
    if (!endRoomId || !validRoomIds.has(endRoomId)) {
      setEndRoomId(routingRoomOptions[Math.min(1, routingRoomOptions.length - 1)].id);
    }
  }, [routingRoomOptions, startRoomId, endRoomId]);

  useEffect(() => {
    if (hasAppliedAutoStartRef.current) return;
    if (!routingRoomOptions.length) return;
    if (typeof window === "undefined") return;

    const params = new URLSearchParams(window.location.search);
    const startParamRaw = params.get("start");
    const qrParamRaw = params.get("qr") || params.get("code");

    const validRoomIds = new Set(routingRoomOptions.map((room) => room.id));

    const resolveStartRoomId = (raw: string | null): string | null => {
      if (!raw) return null;
      const normalized = raw.trim();
      if (!normalized) return null;
      if (validRoomIds.has(normalized)) return normalized;

      const lower = normalized.toLowerCase();
      const match = routingRoomOptions.find((room) => room.id.toLowerCase() === lower);
      return match?.id || null;
    };

    const startFromQuery = resolveStartRoomId(startParamRaw);
    const startFromQr = qrParamRaw ? resolveRoomIdFromQrCode(qrParamRaw) : null;
    const resolvedStart = startFromQuery || startFromQr;

    hasAppliedAutoStartRef.current = true;
    if (!resolvedStart || !validRoomIds.has(resolvedStart)) return;

    setStartRoomId(resolvedStart);
    setShowCurrentUserMarker(true);
    preferRoomCenterStartRef.current = Boolean(startFromQr);

    if (startFromQr) {
      setLocationInputMode("qr");
      setQrCodeInput(qrParamRaw || "");
      setRouteDebugMessage(
        `Start point otomatis dari QR URL: ${roomInfoBySvgId[resolvedStart]?.name || resolvedStart}`
      );
    } else {
      setRouteDebugMessage(
        `Start point otomatis dari URL: ${roomInfoBySvgId[resolvedStart]?.name || resolvedStart}`
      );
    }

    if (endRoomId === resolvedStart) {
      const alternative = routingRoomOptions.find((room) => room.id !== resolvedStart)?.id;
      if (alternative) setEndRoomId(alternative);
    }
  }, [routingRoomOptions, endRoomId]);

  const toShortDescription = useCallback((value: string) => {
    if (value.length <= 92) return value;
    return `${value.slice(0, 89)}...`;
  }, []);

  const asSvgGraphicsElement = useCallback((element: unknown): SVGGraphicsElement | null => {
    if (!element || typeof element !== "object") return null;
    const maybeGraphics = element as { getBBox?: unknown };
    if (typeof maybeGraphics.getBBox !== "function") return null;
    return element as SVGGraphicsElement;
  }, []);

  // ---------------------------------------------------------------------------
  // Drag — startDrag snapshots positionRef (always current), window listeners
  // handle all move/up. SVG <object> iframe forwards only mousedown.
  // ---------------------------------------------------------------------------

  const startDrag = useCallback((hostClientX: number, hostClientY: number, source: "host" | "svg") => {
    isDraggingRef.current = true;
    hasDraggedRef.current = false;
    dragOriginRef.current = { x: hostClientX, y: hostClientY };
    positionAtDragStart.current = { ...positionRef.current };
    // Kill CSS transition IMMEDIATELY — synchronous DOM write, no React cycle delay.
    if (mapRef.current) mapRef.current.style.transition = 'none';
    console.log(`[MapViewer] 🟢 startDrag from <${source}> hostCoords=(${hostClientX.toFixed(0)}, ${hostClientY.toFixed(0)}), posSnapshot=`, positionAtDragStart.current);
    setIsDragging(true);
  }, []);

  // applyDragMove / applyDragEnd accept HOST-PAGE coordinates explicitly.
  // This is critical because SVG <object> iframe mouse events fire with clientX/Y
  // relative to the IFRAME viewport, not the host page — callers must convert first.
  useEffect(() => {
    const applyDragMove = (hostX: number, hostY: number, buttons: number) => {
      if (!isDraggingRef.current) return;

      if ((buttons & 1) === 0) {
        console.log("[MapViewer] 🔴 button released outside — stopping drag");
        isDraggingRef.current = false;
        hasDraggedRef.current = false;
        if (mapRef.current) mapRef.current.style.transition = 'transform 0.15s cubic-bezier(0.2, 0, 0, 1)';
        setIsDragging(false);
        return;
      }

      const dx = hostX - dragOriginRef.current.x;
      const dy = hostY - dragOriginRef.current.y;

      if (!hasDraggedRef.current && (Math.abs(dx) > DRAG_THRESHOLD || Math.abs(dy) > DRAG_THRESHOLD)) {
        hasDraggedRef.current = true;
        console.log(`[MapViewer] ↔️  drag threshold exceeded (dx=${dx.toFixed(1)}, dy=${dy.toFixed(1)})`);
      }

      const newPos = {
        x: positionAtDragStart.current.x + dx,
        y: positionAtDragStart.current.y + dy,
      };
      positionRef.current = newPos;
      setPosition(newPos);
    };

    const applyDragEnd = () => {
      if (!isDraggingRef.current) return;
      const wasDrag = hasDraggedRef.current;
      isDraggingRef.current = false;
      if (mapRef.current) mapRef.current.style.transition = 'transform 0.15s cubic-bezier(0.2, 0, 0, 1)';
      setIsDragging(false);
      console.log(`[MapViewer] 🔵 mouseup — resolved as: ${wasDrag ? "DRAG" : "CLICK"}`);
    };

    applyDragMoveRef.current = applyDragMove;
    applyDragEndRef.current = applyDragEnd;

    // Host window listeners — clientX/Y are already in host-page space.
    const onMouseMove = (e: MouseEvent) => applyDragMove(e.clientX, e.clientY, e.buttons);
    const onMouseUp = () => applyDragEnd();

    window.addEventListener("mousemove", onMouseMove);
    window.addEventListener("mouseup", onMouseUp);
    return () => {
      window.removeEventListener("mousemove", onMouseMove);
      window.removeEventListener("mouseup", onMouseUp);
    };
  }, []);

  const handleMouseDown = useCallback(
    (e: React.MouseEvent) => {
      if (e.button !== 0) return;
      e.preventDefault();
      // Host div — coordinates are already in host-page space.
      startDrag(e.clientX, e.clientY, "host");
    },
    [startDrag]
  );

  // ---------------------------------------------------------------------------
  // Wheel / zoom — single handler on the container only
  // ---------------------------------------------------------------------------

  const handleWheel = useCallback((e: WheelEvent) => {
    e.preventDefault();
    if (!containerRef.current) return;

    const delta = e.deltaY > 0 ? -0.1 : 0.1;

    setScale((prevScale) => {
      const newScale = Math.min(Math.max(prevScale + delta, MIN_SCALE), MAX_SCALE);
      if (newScale === prevScale) return prevScale;

      const rect = containerRef.current!.getBoundingClientRect();
      const mouseX = e.clientX - rect.left;
      const mouseY = e.clientY - rect.top;

      const prevPos = positionRef.current; // use ref, not stale closure
      const mapX = (mouseX - prevPos.x) / prevScale;
      const mapY = (mouseY - prevPos.y) / prevScale;
      const newPos = {
        x: mouseX - mapX * newScale,
        y: mouseY - mapY * newScale,
      };
      positionRef.current = newPos;
      setPosition(newPos);

      return newScale;
    });
  }, []);

  useEffect(() => {
    const container = containerRef.current;
    if (!container) return;
    container.addEventListener("wheel", handleWheel, { passive: false });
    return () => container.removeEventListener("wheel", handleWheel);
  }, [handleWheel]);

  // ---------------------------------------------------------------------------
  // SVG interaction setup — room clicks + forward mousedown/wheel into host
  // ---------------------------------------------------------------------------

  const calculateOverlayPosition = useCallback(
    (target: SVGGraphicsElement | null, anchor: "top" | "center") => {
      if (
        !target ||
        !containerRef.current ||
        !objectRef.current?.contentDocument
      ) {
        return null;
      }

      const svgRoot = objectRef.current.contentDocument.querySelector("svg");
      if (!svgRoot) return null;

      const objectRect = objectRef.current.getBoundingClientRect();
      const containerRect = containerRef.current.getBoundingClientRect();
      const bbox = target.getBBox();
      const viewBox = svgRoot.viewBox.baseVal;

      if (
        !viewBox.width ||
        !viewBox.height ||
        !objectRect.width ||
        !objectRect.height
      ) {
        return null;
      }

      const anchorX = bbox.x + bbox.width / 2;
      const anchorY = anchor === "center" ? bbox.y + bbox.height / 2 : bbox.y;

      const overlayX =
        objectRect.left -
        containerRect.left +
        ((anchorX - viewBox.x) / viewBox.width) * objectRect.width;
      const overlayY =
        objectRect.top -
        containerRect.top +
        ((anchorY - viewBox.y) / viewBox.height) * objectRect.height;

      return { x: overlayX, y: overlayY };
    },
    []
  );

  const projectSvgPointToOverlay = useCallback(
    (svgPoint: { x: number; y: number } | null) => {
      if (
        !svgPoint ||
        !containerRef.current ||
        !objectRef.current?.contentDocument
      ) {
        return null;
      }

      const svgRoot = objectRef.current.contentDocument.querySelector("svg");
      if (!svgRoot) return null;

      const objectRect = objectRef.current.getBoundingClientRect();
      const containerRect = containerRef.current.getBoundingClientRect();
      const viewBox = svgRoot.viewBox.baseVal;

      if (
        !viewBox.width ||
        !viewBox.height ||
        !objectRect.width ||
        !objectRect.height
      ) {
        return null;
      }

      const overlayX =
        objectRect.left -
        containerRect.left +
        ((svgPoint.x - viewBox.x) / viewBox.width) * objectRect.width;
      const overlayY =
        objectRect.top -
        containerRect.top +
        ((svgPoint.y - viewBox.y) / viewBox.height) * objectRect.height;

      return { x: overlayX, y: overlayY };
    },
    []
  );

  const getRoomCenterById = useCallback(
    (svgDoc: Document, roomId: string): { x: number; y: number } | null => {
      const anchor = svgDoc.getElementById(`node_room_${roomId}`);
      if (anchor && anchor.tagName.toLowerCase() === "circle") {
        const x = Number(anchor.getAttribute("cx") || "NaN");
        const y = Number(anchor.getAttribute("cy") || "NaN");
        if (Number.isFinite(x) && Number.isFinite(y)) return { x, y };
      }

      const target = asSvgGraphicsElement(svgDoc.getElementById(roomId));
      if (!target) return null;
      const bbox = target.getBBox();
      return { x: bbox.x + bbox.width / 2, y: bbox.y + bbox.height / 2 };
    },
    [asSvgGraphicsElement]
  );

  const getNearestRoutingRoom = useCallback(
    (svgDoc: Document, point: { x: number; y: number }) => {
      let nearestRoomId: string | null = null;
      let nearestDistance = Number.POSITIVE_INFINITY;

      routingRoomOptions.forEach((room) => {
        const center = getRoomCenterById(svgDoc, room.id);
        if (!center) return;
        const d = Math.hypot(center.x - point.x, center.y - point.y);
        if (d < nearestDistance) {
          nearestDistance = d;
          nearestRoomId = room.id;
        }
      });

      return { roomId: nearestRoomId, distance: nearestDistance };
    },
    [routingRoomOptions, getRoomCenterById]
  );

  const calculateMarkerPosition = useCallback(
    (target: SVGGraphicsElement | null) => {
      const markerPos = calculateOverlayPosition(target, "top");
      if (!markerPos) {
        setActiveMarkerPosition(null);
        return;
      }
      setActiveMarkerPosition(markerPos);
    },
    [calculateOverlayPosition]
  );

  const roomFromPath = useCallback(
    (path: SVGPathElement): HospitalRoomInfo | null => {
      const pathId = path.id?.trim();
      const pathLabel = (
        path.getAttribute("inkscape:label") ||
        path.getAttribute("label") ||
        ""
      ).trim();

      if (!pathId && !pathLabel) return null;

      const source = `${pathId} ${pathLabel}`.toLowerCase();
      const normalizedSource = source.replace(/_/g, " ");
      if (
        normalizedSource.includes("jalan") ||
        normalizedSource.includes("background") ||
        normalizedSource.includes("unamed") ||
        normalizedSource.includes("area kamar operasi")
      ) {
        return null;
      }

      if (!pathLabel && pathId?.startsWith("path")) return null;

      const mapped = pathId ? roomInfoBySvgId[pathId] : undefined;
      if (mapped) return mapped;

      const readable = (pathLabel || pathId || "Ruangan")
        .replace(/_/g, " ")
        .replace(/\./g, "")
        .trim();

      return {
        id: pathId || readable,
        name: readable,
        category: "Room",
        locationHint: "Lihat posisi pada peta",
        description: `Informasi detail untuk ruangan ${readable}.`,
      };
    },
    []
  );

  const buildLabelLines = useCallback(
    (rawLabel: string, maxCharsPerLine: number): string[] => {
      const compact = rawLabel.replace(/_/g, " ").replace(/\s+/g, " ").trim();
      if (compact.length <= maxCharsPerLine) return [compact];

      const words = compact.split(" ");
      const lines: string[] = [];
      let currentLine = "";

      for (const word of words) {
        const tentative = currentLine ? `${currentLine} ${word}` : word;
        if (tentative.length <= maxCharsPerLine) {
          currentLine = tentative;
          continue;
        }

        if (currentLine) lines.push(currentLine);
        currentLine = word;
      }

      if (currentLine) lines.push(currentLine);
      return lines.slice(0, 2);
    },
    []
  );

  const roomMatchesHighlightCategory = useCallback(
    (room: HospitalRoomInfo, category: "departments" | "facilities" | "emergency") => {
      const roomCategory = room.category.trim();

      if (category === "emergency") {
        const match = roomCategory === "Emergency";
        console.log(`[Category] room="${room.id}" cat="${roomCategory}" → emergency match: ${match}`);
        return match;
      }

      if (category === "facilities") {
        const match = ["Facility", "Service", "Administration"].includes(roomCategory);
        console.log(`[Category] room="${room.id}" cat="${roomCategory}" → facilities match: ${match}`);
        return match;
      }

      // departments — includes wards, treatment, outpatient, diagnostic, surgery, critical
      const match = ["Outpatient", "Critical Care", "Diagnostic", "Surgery", "Ward", "Treatment"].includes(roomCategory);
      console.log(`[Category] room="${room.id}" cat="${roomCategory}" → departments match: ${match}`);
      return match;
    },
    []
  );

  const ensureDynamicHighlightStyle = useCallback((svgDoc: Document) => {
    if (svgDoc.getElementById("mapviewer-dynamic-highlight-style")) {
      console.log("[Category] ensureDynamicHighlightStyle: style already injected");
      return;
    }

    // IMPORTANT: <style> must be a direct child of <svg> root, NOT inside <defs>.
    // CSS inside <defs> is not applied by browsers to SVG elements.
    const svgRoot = svgDoc.querySelector("svg");
    if (!svgRoot) {
      console.warn("[Category] ensureDynamicHighlightStyle: no <svg> root found");
      return;
    }

    const style = svgDoc.createElementNS("http://www.w3.org/2000/svg", "style");
    style.id = "mapviewer-dynamic-highlight-style";
    style.textContent = `
      .region-category-active:not(.region-active) {
        fill: #fde047 !important;
        fill-opacity: 0.96 !important;
        stroke: #92400e !important;
        stroke-width: 2.8 !important;
        stroke-linejoin: round !important;
        filter: drop-shadow(0 0 2px rgba(146, 64, 14, 0.6));
      }
    `;

    svgRoot.insertBefore(style, svgRoot.firstChild);
    console.log("[Category] ensureDynamicHighlightStyle: style injected into SVG root");
  }, []);

  const ensureRouteStyle = useCallback((svgDoc: Document) => {
    if (svgDoc.getElementById("mapviewer-route-style")) return;

    const svgRoot = svgDoc.querySelector("svg");
    if (!svgRoot) return;

    const style = svgDoc.createElementNS("http://www.w3.org/2000/svg", "style");
    style.id = "mapviewer-route-style";
    style.textContent = `
      .route-base-line {
        fill: none;
        stroke: #1d4ed8;
        stroke-width: 10;
        stroke-linecap: round;
        stroke-linejoin: round;
        opacity: 0.35;
      }
      .route-arrow-line {
        fill: none;
        stroke: #38bdf8;
        stroke-width: 5;
        stroke-linecap: round;
        stroke-linejoin: round;
        stroke-dasharray: 16 10;
        animation: routeDashMotion 1.2s linear infinite;
      }
      .route-start-arrow {
        fill: #38bdf8;
        stroke: #0f172a;
        stroke-width: 1.6;
        stroke-linejoin: round;
      }
      @keyframes routeDashMotion {
        to {
          stroke-dashoffset: -52;
        }
      }
    `;

    svgRoot.insertBefore(style, svgRoot.firstChild);
  }, []);

  const ensureTurnArrowStyle = useCallback((svgDoc: Document) => {
    if (svgDoc.getElementById("mapviewer-turn-arrow-style")) return;

    const svgRoot = svgDoc.querySelector("svg");
    if (!svgRoot) return;

    const style = svgDoc.createElementNS("http://www.w3.org/2000/svg", "style");
    style.id = "mapviewer-turn-arrow-style";
    style.textContent = `
      .turn-arrow-group {
        transform-box: fill-box;
        transform-origin: center;
        transition: transform 0.25s ease, opacity 0.25s ease;
      }
      .turn-arrow-active {
        opacity: 1;
        transform: scale(1.3);
        filter: drop-shadow(0 0 6px rgba(15, 23, 42, 0.45));
      }
      .turn-arrow-passed {
        opacity: 0.3;
      }
      .turn-arrow-upcoming {
        opacity: 0.85;
      }
      .turn-arrive-pulse {
        animation: turnArrivePulse 1.2s ease-in-out infinite;
      }
      @keyframes turnArrivePulse {
        0% { r: 10; opacity: 0.85; }
        50% { r: 15; opacity: 0.45; }
        100% { r: 10; opacity: 0.85; }
      }
    `;

    svgRoot.insertBefore(style, svgRoot.firstChild);
  }, []);

  const ensureQrAnchorStyle = useCallback((svgDoc: Document) => {
    if (svgDoc.getElementById("mapviewer-qr-anchor-style")) return;

    const svgRoot = svgDoc.querySelector("svg");
    if (!svgRoot) return;

    const style = svgDoc.createElementNS("http://www.w3.org/2000/svg", "style");
    style.id = "mapviewer-qr-anchor-style";
    style.textContent = `
      .qr-anchor-point {
        fill: #f59e0b;
        stroke: #ffffff;
        stroke-width: 0.3;
      }
      .qr-anchor-point-active {
        fill: #16a34a;
      }
      .qr-anchor-label-bg {
        fill: rgba(17, 24, 39, 0.85);
        stroke: rgba(255, 255, 255, 0.45);
        stroke-width: 0.1;
      }
      .qr-anchor-label-text {
        fill: #ffffff;
        font-size: 8px;
        font-weight: 700;
        font-family: Helvetica, Arial, sans-serif;
      }
    `;

    svgRoot.insertBefore(style, svgRoot.firstChild);
  }, []);

  const renderQrAnchorHints = useCallback((svgDoc: Document, anchors: QrAnchor[], activeQrId: string | null) => {
    const namespace = "http://www.w3.org/2000/svg";
    svgDoc.getElementById("dynamic-qr-anchor-layer")?.remove();
    if (!showQrAnchorHints || !anchors.length) return;

    ensureQrAnchorStyle(svgDoc);

    const layer = svgDoc.createElementNS(namespace, "g");
    layer.setAttribute("id", "dynamic-qr-anchor-layer");
    layer.setAttribute("pointer-events", "none");

    anchors.forEach((anchor) => {
      const group = svgDoc.createElementNS(namespace, "g");
      group.setAttribute("transform", `translate(${anchor.svgX} ${anchor.svgY})`);

      const point = svgDoc.createElementNS(namespace, "circle");
      point.setAttribute("cx", "0");
      point.setAttribute("cy", "0");
      point.setAttribute("r", "7");
      point.setAttribute("class", `qr-anchor-point ${activeQrId === anchor.qrId ? "qr-anchor-point-active" : ""}`.trim());
      group.appendChild(point);

      const qrText = svgDoc.createElementNS(namespace, "text");
      qrText.setAttribute("x", "0");
      qrText.setAttribute("y", "2.4");
      qrText.setAttribute("text-anchor", "middle");
      qrText.setAttribute("fill", "#111827");
      qrText.setAttribute("font-size", "5.8");
      qrText.setAttribute("font-weight", "800");
      qrText.textContent = "QR";
      group.appendChild(qrText);

      const labelWidth = Math.max(52, anchor.qrId.length * 4.5 + 12);
      const labelBg = svgDoc.createElementNS(namespace, "rect");
      labelBg.setAttribute("x", String(14));
      labelBg.setAttribute("y", String(-20));
      labelBg.setAttribute("width", String(labelWidth));
      labelBg.setAttribute("height", "14");
      labelBg.setAttribute("rx", "4");
      labelBg.setAttribute("class", "qr-anchor-label-bg");
      group.appendChild(labelBg);

      const labelText = svgDoc.createElementNS(namespace, "text");
      labelText.setAttribute("x", String(19));
      labelText.setAttribute("y", String(-10));
      labelText.setAttribute("class", "qr-anchor-label-text");
      labelText.textContent = anchor.qrId;
      group.appendChild(labelText);

      layer.appendChild(group);
    });

    svgDoc.querySelector("svg")?.appendChild(layer);
  }, [ensureQrAnchorStyle, showQrAnchorHints]);

  const renderTurnArrows = useCallback((
    svgDoc: Document,
    steps: NavigationStep[],
    activeIdx: number
  ) => {
    const namespace = "http://www.w3.org/2000/svg";
    svgDoc.getElementById("dynamic-turn-arrow-layer")?.remove();
    if (!steps.length) return;

    ensureTurnArrowStyle(svgDoc);

    const layer = svgDoc.createElementNS(namespace, "g");
    layer.setAttribute("id", "dynamic-turn-arrow-layer");
    layer.setAttribute("pointer-events", "none");

    steps.forEach((step, idx) => {
      if (step.type === "straight" || step.type === "arrive") return;

      const g = svgDoc.createElementNS(namespace, "g");
      const stateClass = idx < activeIdx ? "turn-arrow-passed" : idx === activeIdx ? "turn-arrow-active" : "turn-arrow-upcoming";
      g.setAttribute("class", `turn-arrow-group ${stateClass}`);
      g.setAttribute("transform", `translate(${step.pivotPoint.x} ${step.pivotPoint.y})`);

      const bg = svgDoc.createElementNS(namespace, "circle");
      bg.setAttribute("r", "20");
      bg.setAttribute("cx", "0");
      bg.setAttribute("cy", "0");
      const bgColor = step.type === "turn_left" ? "#3b82f6" : step.type === "turn_right" ? "#f59e0b" : "#ef4444";
      bg.setAttribute("fill", bgColor);
      bg.setAttribute("stroke", "#ffffff");
      bg.setAttribute("stroke-width", "2");
      g.appendChild(bg);

      const arrow = svgDoc.createElementNS(namespace, "path");
      arrow.setAttribute("d", "M 8,-6 L -6,0 L 8,6 L 4,0 Z");
      arrow.setAttribute("fill", "#ffffff");

      let rotation = 0;
      if (step.type === "turn_left") rotation = 0;
      else if (step.type === "turn_right") rotation = 180;
      else rotation = 180;

      arrow.setAttribute("transform", `rotate(${rotation})`);
      g.appendChild(arrow);

      const distanceLabel = svgDoc.createElementNS(namespace, "text");
      distanceLabel.setAttribute("x", "0");
      distanceLabel.setAttribute("y", "35");
      distanceLabel.setAttribute("text-anchor", "middle");
      distanceLabel.setAttribute("fill", "#0f172a");
      distanceLabel.setAttribute("font-size", "10");
      distanceLabel.setAttribute("font-weight", "700");
      distanceLabel.textContent = `${Math.max(1, Math.round(step.distanceToNext / 3.5))}m`;
      g.appendChild(distanceLabel);

      if (idx === activeIdx && step.nextQrHint) {
        const hintGroup = svgDoc.createElementNS(namespace, "g");
        hintGroup.setAttribute("transform", "translate(0 52)");

        const hintBg = svgDoc.createElementNS(namespace, "rect");
        hintBg.setAttribute("x", "-56");
        hintBg.setAttribute("y", "-10");
        hintBg.setAttribute("width", "112");
        hintBg.setAttribute("height", "20");
        hintBg.setAttribute("rx", "8");
        hintBg.setAttribute("fill", "#fef3c7");
        hintBg.setAttribute("stroke", "#f59e0b");
        hintGroup.appendChild(hintBg);

        const hintText = svgDoc.createElementNS(namespace, "text");
        hintText.setAttribute("x", "0");
        hintText.setAttribute("y", "4");
        hintText.setAttribute("text-anchor", "middle");
        hintText.setAttribute("fill", "#92400e");
        hintText.setAttribute("font-size", "8");
        hintText.setAttribute("font-weight", "700");
        hintText.textContent = "Scan QR di simpang";
        hintGroup.appendChild(hintText);
        g.appendChild(hintGroup);
      }

      layer.appendChild(g);
    });

    const lastPoint = steps[steps.length - 1]?.pivotPoint;
    if (lastPoint) {
      const arriveGroup = svgDoc.createElementNS(namespace, "g");
      arriveGroup.setAttribute("transform", `translate(${lastPoint.x} ${lastPoint.y})`);

      const arrivePulse = svgDoc.createElementNS(namespace, "circle");
      arrivePulse.setAttribute("class", "turn-arrive-pulse");
      arrivePulse.setAttribute("cx", "0");
      arrivePulse.setAttribute("cy", "0");
      arrivePulse.setAttribute("r", "14");
      arrivePulse.setAttribute("fill", "#22c55e");
      arriveGroup.appendChild(arrivePulse);

      const arriveDot = svgDoc.createElementNS(namespace, "circle");
      arriveDot.setAttribute("cx", "0");
      arriveDot.setAttribute("cy", "0");
      arriveDot.setAttribute("r", "8");
      arriveDot.setAttribute("fill", "#16a34a");
      arriveDot.setAttribute("stroke", "#ffffff");
      arriveDot.setAttribute("stroke-width", "2");
      arriveGroup.appendChild(arriveDot);

      const arriveText = svgDoc.createElementNS(namespace, "text");
      arriveText.setAttribute("x", "0");
      arriveText.setAttribute("y", "-18");
      arriveText.setAttribute("text-anchor", "middle");
      arriveText.setAttribute("fill", "#166534");
      arriveText.setAttribute("font-size", "9");
      arriveText.setAttribute("font-weight", "800");
      arriveText.textContent = "";
      arriveGroup.appendChild(arriveText);

      layer.appendChild(arriveGroup);
    }

    svgDoc.querySelector("svg")?.appendChild(layer);
  }, [ensureTurnArrowStyle]);

  const renderRouteOverlay = useCallback((svgDoc: Document, route: RoomRouteResult | null) => {
    const svgRoot = svgDoc.querySelector("svg");
    if (!svgRoot) return;

    const namespace = "http://www.w3.org/2000/svg";
    const oldLayer = svgDoc.getElementById("dynamic-route-layer");
    oldLayer?.remove();

    if (!route || route.points.length < 2) return;

    ensureRouteStyle(svgDoc);

    const pointsAttr = route.points.map((point) => `${point.x},${point.y}`).join(" ");

    const layer = svgDoc.createElementNS(namespace, "g");
    layer.setAttribute("id", "dynamic-route-layer");
    layer.setAttribute("pointer-events", "none");

    const baseLine = svgDoc.createElementNS(namespace, "polyline");
    baseLine.setAttribute("points", pointsAttr);
    baseLine.setAttribute("class", "route-base-line");
    layer.appendChild(baseLine);

    const arrowLine = svgDoc.createElementNS(namespace, "polyline");
    arrowLine.setAttribute("points", pointsAttr);
    arrowLine.setAttribute("class", "route-arrow-line");
    layer.appendChild(arrowLine);

    const startPoint = route.points[0];
    const endPoint = route.points[route.points.length - 1];

    const nextPoint = route.points.find((point, index) => {
      if (index === 0) return false;
      return Math.hypot(point.x - startPoint.x, point.y - startPoint.y) > 0.5;
    }) || route.points[1];

    if (nextPoint) {
      const angle = Math.atan2(nextPoint.y - startPoint.y, nextPoint.x - startPoint.x) * (180 / Math.PI);
      const startArrow = svgDoc.createElementNS(namespace, "path");
      startArrow.setAttribute("class", "route-start-arrow");
      startArrow.setAttribute("d", "M -12 -8 L 12 0 L -12 8 L -4 0 Z");
      startArrow.setAttribute("transform", `translate(${startPoint.x} ${startPoint.y}) rotate(${angle})`);
      layer.appendChild(startArrow);
    }

    const pinWidth = 30;
    const pinHeight = 36;
    const endPin = svgDoc.createElementNS(namespace, "image");
    endPin.setAttribute("x", String(endPoint.x - pinWidth / 2));
    endPin.setAttribute("y", String(endPoint.y - pinHeight));
    endPin.setAttribute("width", String(pinWidth));
    endPin.setAttribute("height", String(pinHeight));
    endPin.setAttribute("href", "/images/location-pin.png");
    endPin.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "/images/location-pin.png");
    layer.appendChild(endPin);

    svgRoot.appendChild(layer);
  }, [ensureRouteStyle]);

  const renderDynamicRoomLabels = useCallback(
    (svgDoc: Document) => {
      const svgRoot = svgDoc.querySelector("svg");
      if (!svgRoot) return;

      const namespace = "http://www.w3.org/2000/svg";
      const oldLayer = svgDoc.getElementById("dynamic-room-label-layer");
      oldLayer?.remove();

      const labelLayer = svgDoc.createElementNS(namespace, "g");
      labelLayer.setAttribute("id", "dynamic-room-label-layer");
      labelLayer.setAttribute("pointer-events", "none");
      labelLayer.setAttribute("aria-hidden", "true");

      const roomTargets = Array.from(svgDoc.querySelectorAll("path"))
        .map((path) => ({ path, room: roomFromPath(path) }))
        .filter(
          (item): item is { path: SVGPathElement; room: HospitalRoomInfo } =>
            Boolean(item.room)
        );

      roomTargets.forEach(({ path, room }) => {
        if (HIDDEN_DYNAMIC_LABEL_ROOM_IDS.has(room.id) || HIDDEN_DYNAMIC_LABEL_ROOM_IDS.has(path.id)) {
          return;
        }

        const targetElement = asSvgGraphicsElement(path);
        if (!targetElement) return;
        const bbox = targetElement.getBBox();
        if (!bbox.width || !bbox.height) return;

        const labelConfig =
          roomLabelConfigBySvgId[room.id] ||
          roomLabelConfigBySvgId[path.id] ||
          {};
        const configuredWidth =
          typeof labelConfig.width === "number" && labelConfig.width > 0
            ? labelConfig.width
            : bbox.width;
        const offsetX =
          typeof labelConfig.x === "number" ? labelConfig.x : 0;
        const offsetY =
          typeof labelConfig.y === "number" ? labelConfig.y : 0;

        const centerX = bbox.x + bbox.width / 2 + offsetX;
        const centerY = bbox.y + bbox.height / 2 + offsetY;
        const minDimension = Math.min(configuredWidth, bbox.height);
        const area = configuredWidth * bbox.height;

        const autoFontSize = Math.max(
          8,
          Math.min(28, Math.min(minDimension * 0.22, Math.sqrt(area) * 0.09))
        );
        const fontSize =
          typeof labelConfig.fontSize === "number" && labelConfig.fontSize > 0
            ? labelConfig.fontSize
            : autoFontSize;
        const fontWeight =
          typeof labelConfig.fontWeight === "number" || typeof labelConfig.fontWeight === "string"
            ? String(labelConfig.fontWeight)
            : "600";
        const fillColor = labelConfig.fill || "#ffffff";
        const strokeColor = labelConfig.stroke;
        const strokeWidth =
          typeof labelConfig.strokeWidth === "number" && labelConfig.strokeWidth >= 0
            ? labelConfig.strokeWidth
            : Math.max(0.6, fontSize * 0.1);
        const fontFamily = labelConfig.fontFamily || "Helvetica, Arial, sans-serif";

        const maxCharsPerLine = Math.max(4, Math.floor(configuredWidth / (fontSize * 0.58)));

        const labelText = room.name || room.id;
        const lines = buildLabelLines(labelText, maxCharsPerLine);
        if (!lines.length) return;

        const textNode = svgDoc.createElementNS(namespace, "text");
        textNode.setAttribute("x", String(centerX));
        textNode.setAttribute("y", String(centerY));
        textNode.setAttribute("text-anchor", "middle");
        textNode.setAttribute("font-size", `${fontSize.toFixed(2)}px`);
        textNode.setAttribute("font-family", fontFamily);
        textNode.setAttribute("font-weight", fontWeight);
        textNode.setAttribute("fill", fillColor);
        if (strokeColor && strokeColor !== "none") {
          textNode.setAttribute("paint-order", "stroke");
          textNode.setAttribute("stroke", strokeColor);
          textNode.setAttribute("stroke-width", `${strokeWidth.toFixed(2)}`);
        } else {
          textNode.setAttribute("stroke", "none");
          textNode.setAttribute("stroke-width", "0");
        }
        textNode.setAttribute("stroke-linejoin", "round");
        textNode.setAttribute("style", "pointer-events:none;user-select:none;");

        const lineHeight = fontSize * 1.12;
        if (lines.length === 1) {
          textNode.setAttribute("dominant-baseline", "middle");
          textNode.textContent = lines[0];
        } else {
          lines.forEach((line, index) => {
            const tspan = svgDoc.createElementNS(namespace, "tspan");
            tspan.setAttribute("x", String(centerX));
            tspan.setAttribute("dy", index === 0 ? `${-lineHeight * 0.42}` : `${lineHeight}`);
            tspan.textContent = line;
            textNode.appendChild(tspan);
          });
        }

        labelLayer.appendChild(textNode);
      });

      svgRoot.appendChild(labelLayer);
    },
    [buildLabelLines, asSvgGraphicsElement, roomFromPath]
  );

  const setupSvgRoomInteraction = useCallback(() => {
    const svgDoc = objectRef.current?.contentDocument;
    if (!svgDoc) return;

    ensureDynamicHighlightStyle(svgDoc);
    renderDynamicRoomLabels(svgDoc);

    const cleanupHandlers: Array<() => void> = [];
    const roomPaths = Array.from(
      svgDoc.querySelectorAll("path")
    ) as SVGPathElement[];

    roomPaths.forEach((path) => {
      const room = roomFromPath(path);
      if (!room) return;

      path.classList.add("room-interactive");

      const clickHandler = (event: Event) => {
        event.stopPropagation();
        // Don't treat drag-end as a room click
        if (hasDraggedRef.current) return;
        setActiveRoomId(room.id);
        setActiveRoomInfo(room);
      };

      path.addEventListener("click", clickHandler);
      cleanupHandlers.push(() =>
        path.removeEventListener("click", clickHandler)
      );
    });

    // SVG <object> lives in a separate browsing context — its mouse events fire
    // with clientX/Y relative to the IFRAME viewport, NOT the host page.
    // We MUST offset by the <object> element's bounding rect before calling any
    // drag logic that works in host-page coordinates.
    const getObjectOffset = () => {
      const r = objectRef.current?.getBoundingClientRect();
      return r ? { left: r.left, top: r.top } : { left: 0, top: 0 };
    };

    const svgMouseDown = (e: MouseEvent) => {
      if (e.button !== 0) return;
      const { left, top } = getObjectOffset();
      // Convert iframe coords → host-page coords
      startDrag(e.clientX + left, e.clientY + top, "svg");
    };

    const svgMouseMove = (e: MouseEvent) => {
      const { left, top } = getObjectOffset();
      applyDragMoveRef.current(e.clientX + left, e.clientY + top, e.buttons);
    };

    const svgMouseUp = () => applyDragEndRef.current();
    const svgWheel   = (e: WheelEvent) => handleWheel(e);

    svgDoc.documentElement.addEventListener("mousedown", svgMouseDown);
    svgDoc.documentElement.addEventListener("mousemove", svgMouseMove);
    svgDoc.documentElement.addEventListener("mouseup",   svgMouseUp);
    svgDoc.documentElement.addEventListener("wheel",     svgWheel, { passive: false });
    cleanupHandlers.push(() => svgDoc.documentElement.removeEventListener("mousedown", svgMouseDown));
    cleanupHandlers.push(() => svgDoc.documentElement.removeEventListener("mousemove", svgMouseMove));
    cleanupHandlers.push(() => svgDoc.documentElement.removeEventListener("mouseup",   svgMouseUp));
    cleanupHandlers.push(() => svgDoc.documentElement.removeEventListener("wheel",     svgWheel));

    // Deselect room on background click (only if not a drag)
    const rootClickHandler = () => {
      if (hasDraggedRef.current) return;
      console.log("[MapViewer] 🖱️  background click — deselecting room");
      setActiveRoomInfo(null);
      setActiveRoomId(null);
    };
    svgDoc.documentElement.addEventListener("click", rootClickHandler);
    cleanupHandlers.push(() =>
      svgDoc.documentElement.removeEventListener("click", rootClickHandler)
    );

    return () => cleanupHandlers.forEach((fn) => fn());
  }, [roomFromPath, startDrag, handleWheel, renderDynamicRoomLabels, ensureDynamicHighlightStyle]);

  useEffect(() => {
    const objectElement = objectRef.current;
    if (!objectElement) return;

    let cleanup: (() => void) | undefined;

    const onLoad = () => {
      cleanup?.();
      cleanup = setupSvgRoomInteraction();
      const svgDoc = objectElement.contentDocument || undefined;
      setRoutingRoomIds(getRoutingRoomIds(svgDoc));
      setSvgReadyVersion((prev) => prev + 1);
    };

    if (objectElement.contentDocument) onLoad();

    objectElement.addEventListener("load", onLoad);
    return () => {
      objectElement.removeEventListener("load", onLoad);
      cleanup?.();
    };
  }, [setupSvgRoomInteraction]);

  useEffect(() => {
    if (!showCurrentUserMarker) {
      setCurrentUserMarkerPosition(null);
      return;
    }

    if (liveSvgPoint) {
      const livePos = projectSvgPointToOverlay(liveSvgPoint);
      setCurrentUserMarkerPosition(livePos);
      return;
    }

    if (!startRoomId) {
      setCurrentUserMarkerPosition(null);
      return;
    }

    const svgDoc = objectRef.current?.contentDocument;
    if (!svgDoc) {
      setCurrentUserMarkerPosition(null);
      return;
    }

    const target = asSvgGraphicsElement(svgDoc.getElementById(startRoomId));
    const markerPos = calculateOverlayPosition(target, "center");
    if (!markerPos) {
      setCurrentUserMarkerPosition(null);
      return;
    }

    setCurrentUserMarkerPosition(markerPos);
  }, [showCurrentUserMarker, liveSvgPoint, startRoomId, scale, position, svgReadyVersion, asSvgGraphicsElement, calculateOverlayPosition, projectSvgPointToOverlay]);

  // ---------------------------------------------------------------------------
  // Zoom to SVG element — centers the map on a given SVG element
  // ---------------------------------------------------------------------------

  const zoomToSvgElement = useCallback(
    (elementId: string) => {
      console.log(`[MapViewer] 🔍 zoomToSvgElement called with elementId="${elementId}"`);
      
      const svgDoc = objectRef.current?.contentDocument;
      const container = containerRef.current;
      
      console.log(`[MapViewer] 🔍 svgDoc=`, svgDoc ? 'exists' : 'NULL');
      console.log(`[MapViewer] 🔍 container=`, container ? 'exists' : 'NULL');
      
      if (!svgDoc || !container) {
        console.log(`[MapViewer] ❌ zoomToSvgElement ABORT: missing svgDoc or container`);
        return;
      }

      const target = asSvgGraphicsElement(svgDoc.getElementById(elementId));
      console.log(`[MapViewer] 🔍 target element=`, target ? `found (tag=${target.tagName})` : 'NOT FOUND');
      
      if (!target) {
        console.log(`[MapViewer] ❌ zoomToSvgElement ABORT: target is not an SVGGraphicsElement (no getBBox)`);
        return;
      }

      const svgRoot = svgDoc.querySelector("svg");
      if (!svgRoot) {
        console.log(`[MapViewer] ❌ zoomToSvgElement ABORT: no svg root`);
        return;
      }

      const viewBox = svgRoot.viewBox.baseVal;
      console.log(`[MapViewer] 🔍 viewBox=`, { x: viewBox.x, y: viewBox.y, w: viewBox.width, h: viewBox.height });
      
      if (!viewBox.width || !viewBox.height) {
        console.log(`[MapViewer] ❌ zoomToSvgElement ABORT: invalid viewBox dimensions`);
        return;
      }

      const objectRect = objectRef.current!.getBoundingClientRect();
      const containerRect = container.getBoundingClientRect();
      
      console.log(`[MapViewer] 🔍 objectRect=`, { left: objectRect.left, top: objectRect.top, w: objectRect.width, h: objectRect.height });
      console.log(`[MapViewer] 🔍 containerRect=`, { left: containerRect.left, top: containerRect.top, w: containerRect.width, h: containerRect.height });

      // Get the SVG-space bbox of the target element
      const bbox = target.getBBox();
      const svgCenterX = bbox.x + bbox.width / 2;
      const svgCenterY = bbox.y + bbox.height / 2;
      
      console.log(`[MapViewer] 🔍 target bbox=`, { x: bbox.x, y: bbox.y, w: bbox.width, h: bbox.height });
      console.log(`[MapViewer] 🔍 svgCenter=(${svgCenterX}, ${svgCenterY})`);

      // We need everything in "unscaled map-space" — the coordinate system of mapRef
      // before the CSS transform (translate + scale) is applied.
      const currentScale = scaleRef.current;
      const currentPos   = positionRef.current;

      // objectRect.width is the RENDERED (scaled) width of the <object>.
      // Divide by currentScale to get the natural (unscaled) width.
      const objNaturalWidth = objectRect.width / currentScale;
      const scaleSvgToObjNatural = objNaturalWidth / viewBox.width;

      // Offset of target centre within the <object> — in unscaled pixels
      const objCenterX = (svgCenterX - viewBox.x) * scaleSvgToObjNatural;
      const objCenterY = (svgCenterY - viewBox.y) * scaleSvgToObjNatural;

      console.log(`[MapViewer] 🔍 scaleSvgToObjNatural=${scaleSvgToObjNatural.toFixed(4)}, objCenter=(${objCenterX.toFixed(1)}, ${objCenterY.toFixed(1)})`);

      // Position of the <object>'s top-left corner in unscaled map-space.
      // screenLeft = containerLeft + currentPos.x + mapLeft * currentScale  →  mapLeft = (screenLeft - containerLeft - currentPos.x) / currentScale
      const objOffsetInMapX = (objectRect.left - containerRect.left - currentPos.x) / currentScale;
      const objOffsetInMapY = (objectRect.top  - containerRect.top  - currentPos.y) / currentScale;

      // Both terms are now in unscaled map-space — safe to add
      const elementInMapX = objOffsetInMapX + objCenterX;
      const elementInMapY = objOffsetInMapY + objCenterY;

      console.log(`[MapViewer] 🔍 objOffsetInMap=(${objOffsetInMapX.toFixed(1)}, ${objOffsetInMapY.toFixed(1)}), elementInMap=(${elementInMapX.toFixed(1)}, ${elementInMapY.toFixed(1)})`);

      // Desired zoom level
      const targetScale = 2.0;

      // Place the element centre exactly at the container centre
      const containerCenterX = containerRect.width / 2;
      const containerCenterY = containerRect.height / 2;

      const newPos = {
        x: containerCenterX - elementInMapX * targetScale,
        y: containerCenterY - elementInMapY * targetScale,
      };
      
      console.log(`[MapViewer] ✅ ZOOMING to scale=${targetScale}, pos=`, newPos, `current scale=${scaleRef.current}`);

      // Smooth zoom with CSS transition
      if (mapRef.current) {
        mapRef.current.style.transition = 'transform 0.45s cubic-bezier(0.25, 0.46, 0.45, 0.94)';
      }

      positionRef.current = newPos;
      setScale(targetScale);
      setPosition(newPos);

      // Reset transition after animation completes, then recalculate marker position
      setTimeout(() => {
        if (mapRef.current) {
          mapRef.current.style.transition = 'transform 0.15s cubic-bezier(0.2, 0, 0, 1)';
        }
        // Recalculate marker position now that the CSS transition has completed
        // and the DOM reflects the final transform
        if (target) calculateMarkerPosition(target);
      }, 500);
    },
    [calculateMarkerPosition, asSvgGraphicsElement]
  );

  // ---------------------------------------------------------------------------
  // Sync selectedLocation → activeRoomInfo (from search) + zoom to element
  // ---------------------------------------------------------------------------

  useEffect(() => {
    console.log(`[MapViewer] 📡 selectedLocation effect fired:`, selectedLocation ? `id="${selectedLocation.id}" name="${selectedLocation.name}"` : 'null');
    if (selectedLocation) {
      setActiveRoomId(selectedLocation.id);
      setActiveRoomInfo(selectedLocation);
      // Delay zoom slightly to ensure SVG highlight renders first
      const timer = setTimeout(() => {
        console.log(`[MapViewer] 📡 calling zoomToSvgElement for id="${selectedLocation.id}"`);
        zoomToSvgElement(selectedLocation.id);
      }, 100);
      return () => {
        console.log(`[MapViewer] 📡 selectedLocation effect cleanup, clearing timer`);
        clearTimeout(timer);
      };
    }
  }, [selectedLocation, zoomToSvgElement]);

  // ---------------------------------------------------------------------------
  // Highlight all rooms by selected sidebar category
  useEffect(() => {
    console.log(`[Category] highlightCategory effect fired: category="${highlightCategory}"`);

    if (!objectRef.current) {
      console.warn("[Category] ABORT: objectRef.current is null");
      return;
    }
    const svgDoc = objectRef.current.contentDocument;
    if (!svgDoc) {
      console.warn("[Category] ABORT: contentDocument is null — SVG may not be loaded yet");
      return;
    }

    // Make sure the highlight CSS is in the SVG document
    ensureDynamicHighlightStyle(svgDoc);

    // Clear previous category highlights
    const prevHighlighted = svgDoc.querySelectorAll(".region-category-active");
    console.log(`[Category] clearing ${prevHighlighted.length} previously highlighted elements`);
    prevHighlighted.forEach((el) => el.classList.remove("region-category-active"));

    if (!highlightCategory) {
      console.log("[Category] highlightCategory is null/empty — cleared only");
      return;
    }

    const roomPaths = Array.from(svgDoc.querySelectorAll("path")) as SVGPathElement[];
    console.log(`[Category] scanning ${roomPaths.length} paths in SVG...`);

    let matchCount = 0;
    roomPaths.forEach((path) => {
      const room = roomFromPath(path);
      if (!room) return;
      if (roomMatchesHighlightCategory(room, highlightCategory)) {
        path.classList.add("region-category-active");
        matchCount++;
        console.log(`[Category] ✅ highlighted path id="${path.id}" room="${room.name}" (${room.category})`);
      }
    });

    console.log(`[Category] done — ${matchCount} rooms highlighted for category="${highlightCategory}"`);
  }, [highlightCategory, roomFromPath, roomMatchesHighlightCategory, ensureDynamicHighlightStyle]);

  // Highlight active room / selected location in SVG
  // ---------------------------------------------------------------------------

  useEffect(() => {
    if (!objectRef.current) return;
    const svgDoc = objectRef.current.contentDocument;
    if (!svgDoc) return;

    svgDoc
      .querySelectorAll(".region-active")
      .forEach((el) => el.classList.remove("region-active"));

    const targetByRoomClick = activeRoomId
      ? svgDoc.getElementById(activeRoomId)
      : null;
    const targetBySearch = selectedLocation?.id
      ? svgDoc.getElementById(selectedLocation.id)
      : null;

    const target = asSvgGraphicsElement(targetByRoomClick || targetBySearch);
    if (target) {
      target.classList.add("region-active");
      calculateMarkerPosition(target);
    } else {
      setActiveMarkerPosition(null);
    }
  }, [selectedLocation, activeRoomId, scale, position, calculateMarkerPosition, asSvgGraphicsElement]);

  const handleResolveQrLocation = useCallback(() => {
    const svgDoc = objectRef.current?.contentDocument;
    if (!svgDoc) {
      setRouteDebugMessage("SVG belum siap. Coba lagi beberapa detik.");
      return;
    }

    const resolvedRoomId = resolveRoomIdFromQrCode(qrCodeInput);
    if (resolvedRoomId) {
      gpsBufferRef.current = [];
      liveSvgPointRef.current = null;
      setLiveSvgPoint(null);
      setShowCurrentUserMarker(true);
      setStartRoomId(resolvedRoomId);
      startRoomIdRef.current = resolvedRoomId;
      preferRoomCenterStartRef.current = true;
      setLastQrAnchor(null);
      setLocationInputMode("qr");

      const roomCenter = getRoomCenterById(svgDoc, resolvedRoomId);
      const routeAfterRoomScan =
        endRoomIdRef.current && endRoomIdRef.current !== resolvedRoomId
          ? buildRouteForRooms(resolvedRoomId, endRoomIdRef.current, svgDoc, {
              startPoint: roomCenter ?? undefined,
            })
          : null;

      if (routeAfterRoomScan) {
        setActiveRoute(routeAfterRoomScan);
      } else {
        setActiveRoute(null);
      }

      setRouteDebugMessage(`✅ Start point dari QR ruangan: ${roomInfoBySvgId[resolvedRoomId]?.name || resolvedRoomId}`);
      return;
    }

    const anchor = resolveQrAnchor(qrCodeInput);
    if (!anchor) {
      setRouteDebugMessage("QR tidak dikenali. Contoh: QR-F-N01, QR-R-IGD, atau QR-IGD.");
      return;
    }

    gpsBufferRef.current = [];
    liveSvgPointRef.current = { x: anchor.svgX, y: anchor.svgY };
    setLiveSvgPoint({ x: anchor.svgX, y: anchor.svgY });
    setShowCurrentUserMarker(true);
    setStartRoomId(anchor.roomId);
    startRoomIdRef.current = anchor.roomId;
    preferRoomCenterStartRef.current = false;
    setLastQrAnchor(anchor);
    setQrCalibrationHistory((prev) => [...prev, anchor]);
    setLocationInputMode("qr");

    const routeAfterCalibration =
      endRoomIdRef.current && endRoomIdRef.current !== anchor.roomId
        ? buildRouteForRooms(anchor.roomId, endRoomIdRef.current, svgDoc, {
            startPoint: { x: anchor.svgX, y: anchor.svgY },
          })
        : null;

    if (routeAfterCalibration) {
      setActiveRoute(routeAfterCalibration);
    }

    setRouteDebugMessage(`✅ Posisi dikalibrasi: ${anchor.label}`);
    setLiveModeStatus(`✅ Posisi dikalibrasi: ${anchor.label}`);
  }, [qrCodeInput, getRoomCenterById]);

  const stopLiveMode = useCallback((statusMessage?: string) => {
    if (geoWatchIdRef.current !== null && typeof navigator !== "undefined" && navigator.geolocation) {
      navigator.geolocation.clearWatch(geoWatchIdRef.current);
    }
    geoWatchIdRef.current = null;
    liveOriginRef.current = null;
    liveSvgPointRef.current = null;
    gpsBufferRef.current = [];
    setLiveSvgPoint(null);
    setIsLiveMode(false);
    setLiveModeStatus(statusMessage || "Live navigation dihentikan.");
  }, []);

  const startLiveMode = useCallback(() => {
    if (typeof navigator === "undefined" || !navigator.geolocation) {
      setLiveModeStatus("Geolocation tidak didukung browser ini.");
      return;
    }

    const svgDoc = objectRef.current?.contentDocument;
    if (!svgDoc) {
      setLiveModeStatus("SVG belum siap. Coba lagi beberapa detik.");
      return;
    }

    const startCenter = getRoomCenterById(svgDoc, startRoomIdRef.current);
    if (!startCenter) {
      setLiveModeStatus("Titik start belum valid di SVG.");
      return;
    }

    if (geoWatchIdRef.current !== null) {
      navigator.geolocation.clearWatch(geoWatchIdRef.current);
      geoWatchIdRef.current = null;
    }

    liveOriginRef.current = null;
    gpsBufferRef.current = [];
    liveSvgPointRef.current = startCenter;
    preferRoomCenterStartRef.current = false;
    setLiveSvgPoint(startCenter);
    setShowCurrentUserMarker(true);
    setIsLiveMode(true);
    setLiveModeStatus("Mode live aktif. Menunggu update lokasi perangkat...");

    const watchId = navigator.geolocation.watchPosition(
      (position) => {
        const svgDocLocal = objectRef.current?.contentDocument;
        if (!svgDocLocal) return;

        const latitude = position.coords.latitude;
        const longitude = position.coords.longitude;

        const origin = liveOriginRef.current;
        if (!origin) {
          liveOriginRef.current = {
            lat: latitude,
            lng: longitude,
            svgX: startCenter.x,
            svgY: startCenter.y,
          };
          setLiveModeStatus("Lokasi terkunci. Mengikuti pergerakan user...");
          return;
        }

        const metersPerDegLat = 111320;
        const metersPerDegLng = 111320 * Math.cos((origin.lat * Math.PI) / 180);
        const eastMeters = (longitude - origin.lng) * metersPerDegLng;
        const northMeters = (latitude - origin.lat) * metersPerDegLat;

        const rawSvgPoint = {
          x: origin.svgX + eastMeters * LIVE_METERS_TO_SVG_PX,
          y: origin.svgY - northMeters * LIVE_METERS_TO_SVG_PX,
        };

        const buffer = gpsBufferRef.current;
        buffer.push(rawSvgPoint);
        if (buffer.length > GPS_BUFFER_SIZE) buffer.shift();
        const smoothedPoint = {
          x: buffer.reduce((sum, point) => sum + point.x, 0) / buffer.length,
          y: buffer.reduce((sum, point) => sum + point.y, 0) / buffer.length,
        };

        liveSvgPointRef.current = smoothedPoint;
        setLiveSvgPoint(smoothedPoint);

        const nearest = getNearestRoutingRoom(svgDocLocal, smoothedPoint);
        if (!nearest.roomId || !Number.isFinite(nearest.distance)) return;

        if (nearest.distance > LIVE_NEAREST_ROOM_MAX_DISTANCE) {
          setLiveModeStatus("Posisi user terlalu jauh dari area ruangan terdeteksi.");
          return;
        }

        const currentEnd = endRoomIdRef.current;
        if (nearest.roomId !== startRoomIdRef.current) {
          setStartRoomId(nearest.roomId);
          startRoomIdRef.current = nearest.roomId;
        }

        if (!currentEnd || nearest.roomId === currentEnd) {
          setActiveRoute(null);
          return;
        }

        const now = Date.now();
        if (now - lastRerouteAtRef.current < LIVE_REROUTE_INTERVAL_MS) return;
        lastRerouteAtRef.current = now;

        const dynamicRoute = buildRouteForRooms(nearest.roomId, currentEnd, svgDocLocal, {
          startPoint: smoothedPoint,
        });
        if (dynamicRoute) {
          setActiveRoute(dynamicRoute);
          setRouteDebugMessage(
            `Rute live: ${roomInfoBySvgId[nearest.roomId]?.name || nearest.roomId} → ${roomInfoBySvgId[currentEnd]?.name || currentEnd}`
          );
          setLiveModeStatus("Navigasi live aktif dan rute diperbarui otomatis.");
        } else {
          setRouteDebugMessage("Rute live belum ditemukan dari posisi terbaru.");
        }
      },
      (error) => {
        const message =
          error.code === error.PERMISSION_DENIED
            ? "Izin lokasi ditolak. Aktifkan location permission browser."
            : error.code === error.POSITION_UNAVAILABLE
              ? "Lokasi tidak tersedia. Coba pindah area atau cek sensor."
              : error.code === error.TIMEOUT
                ? "Timeout membaca lokasi. Coba lagi."
                : "Gagal membaca lokasi perangkat.";
        stopLiveMode(message);
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 1000,
      }
    );

    geoWatchIdRef.current = watchId;
  }, [getRoomCenterById, getNearestRoutingRoom, stopLiveMode]);

  useEffect(() => {
    if (!navigationStartRequest) return;

    const { requestId, roomId, source } = navigationStartRequest;
    const finish = () => onNavigationStartRequestHandled?.(requestId);

    if (isLiveMode) {
      stopLiveMode("Live mode dimatikan karena start point diubah.");
    }

    gpsBufferRef.current = [];
    liveSvgPointRef.current = null;
    setLiveSvgPoint(null);
    setLastQrAnchor(null);
    preferRoomCenterStartRef.current = true;
    setLocationInputMode(source === "qr" ? "qr" : "dropdown");
    setStartRoomId(roomId);
    startRoomIdRef.current = roomId;
    setShowCurrentUserMarker(true);

    const svgDoc = objectRef.current?.contentDocument;
    if (!svgDoc) {
      setRouteDebugMessage(`Start point diset ke ${roomInfoBySvgId[roomId]?.name || roomId}.`);
      finish();
      return;
    }

    const targetEnd = endRoomIdRef.current;
    if (!targetEnd || targetEnd === roomId) {
      setActiveRoute(null);
      setRouteDebugMessage(`Start point diset ke ${roomInfoBySvgId[roomId]?.name || roomId}. Pilih tujuan berbeda.`);
      finish();
      return;
    }

    const startCenter = getRoomCenterById(svgDoc, roomId);
    const immediateRoute = buildRouteForRooms(roomId, targetEnd, svgDoc, {
      startPoint: startCenter ?? undefined,
    });

    if (!immediateRoute) {
      setActiveRoute(null);
      setRouteDebugMessage("Rute tidak ditemukan setelah set titik awal. Coba scan ulang QR atau pilih start lain.");
      finish();
      return;
    }

    setActiveRoute(immediateRoute);
    setRouteDebugMessage(
      `Rute otomatis: ${roomInfoBySvgId[roomId]?.name || roomId} → ${roomInfoBySvgId[targetEnd]?.name || targetEnd}`
    );
    zoomToSvgElement(targetEnd);
    finish();
  }, [
    navigationStartRequest,
    onNavigationStartRequestHandled,
    isLiveMode,
    stopLiveMode,
    getRoomCenterById,
    zoomToSvgElement,
  ]);

  const handleFindRoute = useCallback(() => {
    const effectiveStartRoomId = startRoomIdRef.current || startRoomId;
    const effectiveEndRoomId = endRoomIdRef.current || endRoomId;

    if (!effectiveStartRoomId || !effectiveEndRoomId) {
      setRouteDebugMessage("Pilih titik awal dan tujuan terlebih dahulu.");
      return;
    }

    if (effectiveStartRoomId === effectiveEndRoomId) {
      setRouteDebugMessage("Titik awal dan tujuan sama. Pilih tujuan yang berbeda.");
      setActiveRoute(null);
      return;
    }

    const svgDoc = objectRef.current?.contentDocument;
    if (!svgDoc) {
      setRouteDebugMessage("SVG belum siap. Coba beberapa detik lagi.");
      return;
    }

    const roomCenterStart = preferRoomCenterStartRef.current
      ? getRoomCenterById(svgDoc, effectiveStartRoomId)
      : null;

    if (preferRoomCenterStartRef.current && !roomCenterStart) {
      setRouteDebugMessage("Start ruangan tidak valid di SVG. Scan ulang atau pilih start dari dropdown.");
      setActiveRoute(null);
      return;
    }

    const result = buildRouteForRooms(effectiveStartRoomId, effectiveEndRoomId, svgDoc, {
      startPoint: preferRoomCenterStartRef.current
        ? roomCenterStart ?? undefined
        : (liveSvgPointRef.current ?? undefined),
    });
    if (!result) {
      setRouteDebugMessage("Rute tidak ditemukan pada jalur 'jalan' di denah.");
      setActiveRoute(null);
      return;
    }

    setActiveRoute(result);
    setRouteDebugMessage(
      `Rute ditemukan: ${roomInfoBySvgId[effectiveStartRoomId]?.name || effectiveStartRoomId} → ${roomInfoBySvgId[effectiveEndRoomId]?.name || effectiveEndRoomId}`
    );

    zoomToSvgElement(effectiveEndRoomId);
  }, [startRoomId, endRoomId, zoomToSvgElement, getRoomCenterById]);

  const handleClearRoute = useCallback(() => {
    setActiveRoute(null);
    setRouteDebugMessage("Rute dibersihkan.");
    setNavSteps([]);
    setActiveStepIndex(0);
  }, []);

  useEffect(() => {
    if (!activeRoute || activeRoute.points.length < 2) {
      setNavSteps([]);
      setActiveStepIndex(0);
      return;
    }

    const steps = buildNavigationSteps(activeRoute.points);
    setNavSteps(steps);
    setActiveStepIndex(0);
  }, [activeRoute]);

  useEffect(() => {
    if (!liveSvgPoint || !navSteps.length) return;
    const idx = getActiveStepIndex(liveSvgPoint, navSteps);
    setActiveStepIndex(idx);
  }, [liveSvgPoint, navSteps]);

  useEffect(() => {
    return () => {
      if (geoWatchIdRef.current !== null && typeof navigator !== "undefined" && navigator.geolocation) {
        navigator.geolocation.clearWatch(geoWatchIdRef.current);
      }
      geoWatchIdRef.current = null;
    };
  }, []);

  useEffect(() => {
    const objectElement = objectRef.current;
    if (!objectElement) return;

    const applyRoute = () => {
      const svgDoc = objectElement.contentDocument;
      if (!svgDoc) return;
      renderRouteOverlay(svgDoc, activeRoute);
    };

    applyRoute();
    objectElement.addEventListener("load", applyRoute);
    return () => objectElement.removeEventListener("load", applyRoute);
  }, [activeRoute, renderRouteOverlay]);

  useEffect(() => {
    const svgDoc = objectRef.current?.contentDocument;
    if (!svgDoc) return;
    renderTurnArrows(svgDoc, navSteps, activeStepIndex);
  }, [navSteps, activeStepIndex, renderTurnArrows, svgReadyVersion]);

  useEffect(() => {
    const svgDoc = objectRef.current?.contentDocument;
    if (!svgDoc) return;
    renderQrAnchorHints(svgDoc, qrAnchors, lastQrAnchor?.qrId || null);
  }, [renderQrAnchorHints, qrAnchors, lastQrAnchor, svgReadyVersion]);

  // ---------------------------------------------------------------------------
  // Center / reset
  // ---------------------------------------------------------------------------

  const centerMap = useCallback(() => {
    if (!containerRef.current) return;
    const containerRect = containerRef.current.getBoundingClientRect();
    const initialScale = 0.8;
    const nextPos = {
      x: (containerRect.width / 2) - (containerRect.width * initialScale) / 2,
      y:
        (containerRect.height / 2) -
        (containerRect.height * initialScale) / 2,
    };
    positionRef.current = nextPos;
    positionAtDragStart.current = nextPos;
    setScale(initialScale);
    setPosition(nextPos);
  }, []);

  useEffect(() => {
    const timer = setTimeout(centerMap, 100);
    return () => clearTimeout(timer);
  }, [centerMap]);

  const handleZoomIn = () =>
    setScale((s) => Math.min(s + ZOOM_STEP, MAX_SCALE));
  const handleZoomOut = () =>
    setScale((s) => Math.max(s - ZOOM_STEP, MIN_SCALE));

  const currentNavStep = navSteps.length
    ? navSteps[Math.min(activeStepIndex, navSteps.length - 1)]
    : null;

  const currentNavInstruction = (() => {
    if (!currentNavStep) return "";

    switch (currentNavStep.type) {
      case "turn_left":
        return "Belok kiri";
      case "turn_right":
        return "Belok kanan";
      case "straight":
        return "Lurus";
      case "u_turn":
        return "Ke belakang (putar balik)";
      case "arrive":
        return "Anda telah tiba di tujuan";
      default:
        return currentNavStep.label;
    }
  })();

  // ---------------------------------------------------------------------------
  // Render
  // ---------------------------------------------------------------------------

  return (
    <div className="relative flex-1 overflow-hidden bg-muted/20 rounded-xl border border-border shadow-inner">
      <div
        ref={containerRef}
        className={`w-full h-full ${isDragging ? "cursor-grabbing" : "cursor-grab"}`}
        onMouseDown={handleMouseDown}
      >
        <div
          ref={mapRef}
          style={{
            transform: `translate(${position.x}px, ${position.y}px) scale(${scale})`,
            transformOrigin: "0 0",
            // Transition is managed imperatively in startDrag / onMouseUp
            // so it's always in sync with the drag state without any React cycle delay.
            transition: "transform 0.15s cubic-bezier(0.2, 0, 0, 1)",
          }}
          className="w-full h-full select-none"
        >
          <div className="w-full h-full flex items-center justify-center">
            <object
              ref={objectRef}
              data="/images/hospital-map.svg"
              type="image/svg+xml"
              className="max-w-[90%] max-h-[90%]"
              aria-label="Hospital interactive map"
            />
          </div>
        </div>
      </div>

      {isPathfindingDebugVisible ? (
        <div className="absolute top-4 right-4 z-30 w-[320px] max-w-[calc(100vw-2rem)] rounded-xl border border-border bg-background/90 backdrop-blur-md shadow-lg p-3 space-y-2">
          <div className="flex items-center justify-between">
            <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">Navigation HUD</p>
            <div className="flex items-center gap-2">
              <span className="text-[10px] text-muted-foreground">Lantai 1</span>
              <button
                onClick={() => setIsPathfindingDebugVisible(false)}
                className="inline-flex h-5 w-5 items-center justify-center rounded border border-border text-muted-foreground hover:bg-muted"
                title="Tutup debug mode"
                aria-label="Tutup debug mode"
              >
                <X className="h-3.5 w-3.5" />
              </button>
            </div>
          </div>

        <div className="grid grid-cols-2 gap-2">
          <button
            onClick={() => {
              setLocationInputMode("dropdown");
              setLastQrAnchor(null);
              if (!isLiveMode) {
                liveSvgPointRef.current = null;
                gpsBufferRef.current = [];
                setLiveSvgPoint(null);
              }
            }}
            className={`rounded-md px-2 py-1 text-xs font-medium border transition-colors ${
              locationInputMode === "dropdown"
                ? "bg-primary text-primary-foreground border-primary"
                : "bg-background text-foreground border-border hover:bg-muted"
            }`}
          >
            Lokasi via Dropdown
          </button>
          <button
            onClick={() => setLocationInputMode("qr")}
            className={`rounded-md px-2 py-1 text-xs font-medium border transition-colors ${
              locationInputMode === "qr"
                ? "bg-primary text-primary-foreground border-primary"
                : "bg-background text-foreground border-border hover:bg-muted"
            }`}
          >
            Lokasi via QR
          </button>
        </div>

        {locationInputMode === "dropdown" ? (
          <div className="space-y-1">
            <label className="text-[11px] text-muted-foreground">Saya di sini (Start)</label>
            <select
              value={startRoomId}
              onChange={(event) => {
                if (isLiveMode) stopLiveMode("Live mode dimatikan karena start dipilih manual.");
                preferRoomCenterStartRef.current = false;
                liveSvgPointRef.current = null;
                gpsBufferRef.current = [];
                setLiveSvgPoint(null);
                setLastQrAnchor(null);
                setStartRoomId(event.target.value);
                startRoomIdRef.current = event.target.value;
                setShowCurrentUserMarker(false);
              }}
              className="w-full rounded-md border border-border bg-background px-2 py-1.5 text-xs"
            >
              {routingRoomOptions.map((room) => (
                <option key={room.id} value={room.id}>
                  {room.name}
                </option>
              ))}
            </select>
          </div>
        ) : (
          <div className="space-y-1">
            <label className="text-[11px] text-muted-foreground">Scan QR (simulasi)</label>
            <div className="flex gap-2">
              <input
                value={qrCodeInput}
                onChange={(event) => setQrCodeInput(event.target.value)}
                placeholder="Contoh: QR-F-N01 / QR-R-IGD"
                className="flex-1 rounded-md border border-border bg-background px-2 py-1.5 text-xs"
              />
              <button
                onClick={handleResolveQrLocation}
                className="rounded-md border border-border bg-muted px-2 py-1.5 text-xs font-medium hover:bg-muted/80"
              >
                Set
              </button>
            </div>

            <div className="flex items-center justify-between mt-1">
              <p className="text-[10px] text-muted-foreground">Tampilkan titik QR di peta</p>
              <button
                onClick={() => setShowQrAnchorHints((prev) => !prev)}
                className={`rounded px-2 py-0.5 text-[10px] font-semibold border ${
                  showQrAnchorHints
                    ? "bg-emerald-500 text-white border-emerald-500"
                    : "bg-background text-foreground border-border"
                }`}
              >
                {showQrAnchorHints ? "ON" : "OFF"}
              </button>
            </div>

            <div className="max-h-28 overflow-y-auto rounded-md border border-border bg-muted/30 p-1.5 space-y-1">
              {qrAnchors.map((anchor) => (
                <button
                  key={anchor.qrId}
                  onClick={() => setQrCodeInput(anchor.qrId)}
                  className={`w-full text-left rounded px-1.5 py-1 text-[10px] transition-colors ${
                    lastQrAnchor?.qrId === anchor.qrId
                      ? "bg-emerald-100 text-emerald-800"
                      : "hover:bg-muted"
                  }`}
                >
                  <span className="font-semibold">{anchor.qrId}</span>
                  <span className="text-muted-foreground"> — {anchor.label}</span>
                </button>
              ))}
            </div>
          </div>
        )}

        <div className="space-y-1">
          <label className="text-[11px] text-muted-foreground">Tujuan (End)</label>
          <select
            value={endRoomId}
            onChange={(event) => setEndRoomId(event.target.value)}
            className="w-full rounded-md border border-border bg-background px-2 py-1.5 text-xs"
          >
            {routingRoomOptions.map((room) => (
              <option key={room.id} value={room.id}>
                {room.name}
              </option>
            ))}
          </select>
        </div>

        <div className="flex gap-2">
          <button
            onClick={handleFindRoute}
            className="flex-1 rounded-md bg-primary px-3 py-1.5 text-xs font-semibold text-primary-foreground hover:bg-primary/90"
          >
            Cari Rute
          </button>
          <button
            onClick={handleClearRoute}
            className="rounded-md border border-border px-3 py-1.5 text-xs font-medium hover:bg-muted"
          >
            Bersihkan
          </button>
        </div>

        <div className="flex gap-2">
          <button
            onClick={() => {
              if (isLiveMode) stopLiveMode();
              else startLiveMode();
            }}
            className={`flex-1 rounded-md px-3 py-1.5 text-xs font-semibold transition-colors ${
              isLiveMode
                ? "bg-rose-500 text-white hover:bg-rose-600"
                : "bg-emerald-500 text-white hover:bg-emerald-600"
            }`}
          >
            {isLiveMode ? "Hentikan Navigasi Live" : "Mulai Navigasi Live"}
          </button>
        </div>

        {activeRoute && (
          <p className="text-[11px] text-muted-foreground">
            Jarak koridor: {Math.round(activeRoute.totalDistance)} px • {activeRoute.checkpointIds.length} checkpoint
          </p>
        )}
        {routeDebugMessage && (
          <p className="text-[11px] text-muted-foreground">{routeDebugMessage}</p>
        )}
        {liveModeStatus && (
          <p className="text-[11px] text-muted-foreground">{liveModeStatus}</p>
        )}
        </div>
      ) : (
        <button
          onClick={() => setIsPathfindingDebugVisible(true)}
          className="absolute top-4 right-4 z-30 rounded-md border border-border bg-background/90 px-2.5 py-1.5 text-[11px] font-semibold text-muted-foreground shadow"
          title="Buka pathfinding debug mode"
        >
          Buka Debug Mode
        </button>
      )}

      {(activeRoute || isLiveMode) && currentNavStep && (
        <div className="absolute top-4 left-4 z-30 w-[min(92vw,320px)] rounded-xl border border-border bg-background/90 px-3 py-2 shadow-lg backdrop-blur-md">
          <p className="text-[10px] font-semibold uppercase tracking-wide text-muted-foreground">
            Instruksi Navigasi
          </p>
          <p className="mt-1 text-sm font-bold text-foreground">
            {currentNavInstruction}
          </p>
          {currentNavStep.type !== "arrive" && (
            <p className="mt-0.5 text-[11px] text-muted-foreground">
              Sekitar {Math.max(1, Math.round(currentNavStep.distanceToNext))} px lagi
            </p>
          )}
          <div className="mt-2 flex items-center justify-between gap-2">
            <p className="text-[10px] text-muted-foreground">
              Langkah {Math.min(activeStepIndex + 1, navSteps.length)} / {navSteps.length}
            </p>
            <button
              onClick={() => onStartNavigation?.({ mode: "qr" })}
              className="rounded-md border border-border bg-background px-2.5 py-1 text-[10px] font-semibold text-foreground hover:bg-muted"
              title="Kalibrasi QR saat navigasi"
            >
              Kalibrasi QR
            </button>
          </div>
        </div>
      )}

      {/* Zoom controls */}
      <div className="absolute bottom-6 right-16 flex items-center gap-2 bg-background/80 backdrop-blur-md p-1.5 border border-border rounded-xl shadow-lg">
        <button
          onClick={handleZoomOut}
          disabled={scale <= MIN_SCALE}
          className="w-9 h-9 flex items-center justify-center rounded-lg text-foreground hover:bg-muted disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
          title="Zoom Out"
        >
          <Minus className="h-4.5 w-4.5" />
        </button>

        <div className="w-12 text-center text-[11px] font-bold border-x border-border">
          {Math.round(scale * 100)}%
        </div>

        <button
          onClick={handleZoomIn}
          disabled={scale >= MAX_SCALE}
          className="w-9 h-9 flex items-center justify-center rounded-lg text-foreground hover:bg-muted disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
          title="Zoom In"
        >
          <Plus className="h-4.5 w-4.5" />
        </button>
      </div>

      <button
        onClick={centerMap}
        className="absolute bottom-6 right-6 w-11 h-11 bg-primary rounded-xl shadow-lg flex items-center justify-center text-primary-foreground hover:bg-primary/90 transition-all hover:scale-105 active:scale-95"
        title="Reset View"
      >
        <Locate className="h-5 w-5" />
      </button>

      {qrCalibrationHistory.length > 0 && (
        <div className="absolute bottom-20 left-6 z-20 max-w-[280px] rounded-lg border border-primary/20 bg-background/90 px-3 py-2 text-[11px] text-muted-foreground shadow-md backdrop-blur-sm">
          <p className="font-semibold text-foreground">🎯 {qrCalibrationHistory.length}x dikalibrasi via QR</p>
          {lastQrAnchor && <p className="mt-0.5">Terakhir: {lastQrAnchor.label}</p>}
        </div>
      )}

      {/* Legend */}
      <div className="absolute bottom-6 left-6 flex items-center gap-4 text-[10px] text-muted-foreground bg-background/50 backdrop-blur-sm px-3 py-1.5 rounded-full border border-border/50">
        <div className="flex items-center gap-1.5">
          <div className="w-2 h-2 rounded-full bg-primary" />
          <span>Posisi Anda</span>
        </div>
        <div className="flex items-center gap-1.5">
          <div className="w-2 h-2 rounded-full bg-orange-400" />
          <span>Emergency</span>
        </div>
      </div>

      {/* Map pin marker */}
      {activeMarkerPosition && (
        <div
          className="absolute z-20 pointer-events-none -translate-x-1/2 -translate-y-full"
          style={{
            left: activeMarkerPosition.x,
            top: activeMarkerPosition.y,
          }}
        >
          <img
            src="/images/location-pin.png"
            alt="Location pin"
            className="h-8 w-8 drop-shadow-lg"
            draggable={false}
          />
        </div>
      )}

      {/* Current user marker (QR start point) */}
      {showCurrentUserMarker && currentUserMarkerPosition && (
        <div
          className="absolute z-20 pointer-events-none -translate-x-1/2 -translate-y-1/2"
          style={{
            left: currentUserMarkerPosition.x,
            top: currentUserMarkerPosition.y,
            transition: "left 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94), top 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94)",
          }}
        >
          <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 h-8 w-8 rounded-full bg-primary/30 animate-ping" />
          <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 h-5 w-5 rounded-full bg-primary/20" />
          <div className="relative h-4.5 w-4.5 rounded-full bg-primary border-2 border-white shadow-[0_0_0_4px_rgba(37,99,235,0.30)]" />
        </div>
      )}

      {/* Room info card */}
      {activeRoomInfo && (
        <div className="fixed left-1/2 -translate-x-1/2 z-50 w-[min(92vw,420px)] max-h-[calc(100vh-1.5rem)] overflow-y-auto bg-card/95 backdrop-blur-lg rounded-2xl border border-border/80 shadow-2xl p-3.5 bottom-3 sm:bottom-4">
          <div className="flex items-start justify-between gap-2">
            <div className="min-w-0">
              <p className="inline-flex items-center rounded-full bg-primary/10 px-2 py-0.5 text-[10px] font-semibold tracking-wide text-primary">
                {activeRoomInfo.category}
              </p>
              <h4 className="mt-1 text-sm font-bold text-foreground truncate">
                {activeRoomInfo.name}
              </h4>
            </div>
            <button
              onClick={() => {
                setActiveRoomInfo(null);
                setActiveRoomId(null);
                onClearSelection?.();
              }}
              className="h-7 w-7 inline-flex items-center justify-center rounded-md hover:bg-muted text-muted-foreground transition-colors"
              aria-label="Close room info"
            >
              <X className="h-4 w-4" />
            </button>
          </div>

          <p className="mt-1.5 text-xs text-muted-foreground">
            {toShortDescription(activeRoomInfo.description)}
          </p>

          <div className="mt-2.5 flex items-center justify-between gap-2">
            <span className="text-[11px] text-muted-foreground truncate">
              📍 {activeRoomInfo.locationHint}
            </span>
            <button
              onClick={() => {
                const destinationRoomId = activeRoomInfo.id;
                setEndRoomId(destinationRoomId);
                endRoomIdRef.current = destinationRoomId;
                setRouteDebugMessage(
                  `Tujuan dipilih: ${activeRoomInfo.name}. Pilih titik awal via QR atau dropdown.`
                );
                setIsPathfindingDebugVisible(true);
                onStartNavigation?.({ mode: "qr" });
              }}
              className="inline-flex items-center gap-1.5 rounded-lg bg-primary px-3 py-1.5 text-xs font-semibold text-primary-foreground hover:bg-primary/90 transition-colors"
            >
              <Navigation className="h-3.5 w-3.5" />
              Navigate Here
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default MapViewer;