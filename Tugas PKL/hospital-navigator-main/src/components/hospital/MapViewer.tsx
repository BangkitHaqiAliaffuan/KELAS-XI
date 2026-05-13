import { useRef, useState, useCallback, useEffect, useMemo } from "react";
import {
  Plus,
  Minus,
  Locate,
  X,
  Navigation,
  QrCode,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";
import {
  roomLabelConfigBySvgId,
  type HospitalRoomInfo,
} from "@/data/hospitalRoomInfo";
import {
  buildRouteForRooms,
  buildRouteFromPoint,
  injectVirtualAnchorNode,
  getRoutingRoomIds,
  resolveRoomIdFromQrCode,
  computeElementCenterWithoutLayout,
  type QrAnchor,
  type RoomRouteResult,
} from "@/data/hospitalRouteGraph";
import { useRooms, useQrAnchors } from "@/hooks/useHospitalData";
import {
  roomsArrayToObject,
  qrAnchorsArrayToObject,
  resolveQrAnchorFromRegistry,
} from "@/utils/apiHelpers";
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
  onStartNavigation?: (options?: { mode?: "manual" | "qr" | "calibrate"; destinationRoomId?: string }) => void;
  language?: "id" | "en";
  navigationStartRequest?: {
    requestId: number;
    roomId: string;
    destinationRoomId: string;
    source: "manual" | "qr" | "calibrate";
    qrPayload?: string;
  } | null;
  onNavigationStartRequestHandled?: (requestId: number) => void;
}

const MapViewer = ({
  selectedLocation,
  onClearSelection,
  highlightCategory,
  onStartNavigation,
  language = "id",
  navigationStartRequest,
  onNavigationStartRequestHandled,
}: MapViewerProps) => {
  const floorCopy = language === "id"
    ? {
        floor1: "Lantai 1",
        floor2: "Lantai 2",
        parking1: "Parkir L1",
        parking2: "Parkir L2",
        parking1Title: "Tampilkan peta lahan parkir lantai 1",
        parking2Title: "Tampilkan peta lahan parkir lantai 2",
      }
    : {
        floor1: "Floor 1",
        floor2: "Floor 2",
        parking1: "Parking L1",
        parking2: "Parking L2",
        parking1Title: "Show parking map level 1",
        parking2Title: "Show parking map level 2",
      };
  const containerRef = useRef<HTMLDivElement>(null);
  const mapRef = useRef<HTMLDivElement>(null);
  const objectRef = useRef<HTMLObjectElement>(null);
  const { data: rooms, isLoading: roomsLoading, error: roomsError } = useRooms();
  const { data: qrAnchors, isLoading: qrLoading, error: qrError } = useQrAnchors();
  const roomInfoBySvgId = useMemo(() => roomsArrayToObject(rooms || []), [rooms]);
  const QR_ANCHOR_REGISTRY = useMemo(() => qrAnchorsArrayToObject(qrAnchors || []), [qrAnchors]);

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
  const [svgLoadTick, setSvgLoadTick] = useState(0);
  const [activeFloor, setActiveFloor] = useState<1 | 2>(1);

  const [routingRoomIds, setRoutingRoomIds] = useState<string[]>(() => getRoutingRoomIds());
  const routingRoomOptions = routingRoomIds
    .map((roomId) => roomInfoBySvgId[roomId])
    .filter((room): room is HospitalRoomInfo => Boolean(room))
    .sort((a, b) => a.name.localeCompare(b.name));

  const [locationInputMode, setLocationInputMode] = useState<"dropdown" | "qr">("dropdown");
  const [isPathfindingDebugVisible, setIsPathfindingDebugVisible] = useState(false);
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
  const [showParkingMap, setShowParkingMap] = useState(false);
  const [parkingFloor, setParkingFloor] = useState<1 | 2>(1); // Track which parking floor to show
  const [qrCalibrationHistory, setQrCalibrationHistory] = useState<QrAnchor[]>([]);
  const [navSteps, setNavSteps] = useState<NavigationStep[]>([]);
  const [activeStepIndex, setActiveStepIndex] = useState(0);
  const hasAppliedAutoStartRef = useRef(false);
  const geoWatchIdRef = useRef<number | null>(null);
  const [isCalibrationMode, setIsCalibrationMode] = useState(false);
  const liveOriginRef = useRef<{ lat: number; lng: number; svgX: number; svgY: number } | null>(null);
  const liveSvgPointRef = useRef<{ x: number; y: number } | null>(null);
  const gpsBufferRef = useRef<Array<{ x: number; y: number }>>([]);
  const startRoomIdRef = useRef(startRoomId);
  const endRoomIdRef = useRef(endRoomId);
  const lastRerouteAtRef = useRef(0);
  const preferRoomCenterStartRef = useRef(false);
  const pendingSearchZoomRoomIdRef = useRef<string | null>(null);
  const lastLoggedRouteRef = useRef<string | null>(null); // Track last logged route to avoid duplicate logs
  const lastNavStepsKeyRef = useRef<string | null>(null);

  const MIN_SCALE = 0.4;
  const MAX_SCALE = 5;
  const ZOOM_STEP = 0.2;
  const DRAG_THRESHOLD = 4; // px before we consider it a real drag
  const LIVE_METERS_TO_SVG_PX = 2.2;
  const LIVE_REROUTE_INTERVAL_MS = 1200;
  const LIVE_NEAREST_ROOM_MAX_DISTANCE = 240;
  const GPS_BUFFER_SIZE = 5;
  const HIDDEN_DYNAMIC_LABEL_ROOM_IDS = new Set([
    "Lift_Lantai_1", 
    "Tangga_Lantai_1", 
    "Tangga_Evakuasi_Lantai_1",
    "Area_Pelayanan_IGD",
    "Jembatan_ke_Lahan_Parkir__Pengunjung_"
  ]);
  const HIDDEN_DYNAMIC_LABEL_KEYWORDS_FLOOR_2 = [
    "area gudang alat medis steril",
    "lift",
    "tangga",
  ];
  const allRegisteredQrAnchors = useMemo(
    () => Object.values(QR_ANCHOR_REGISTRY).sort((a, b) => a.qrId.localeCompare(b.qrId)),
    [QR_ANCHOR_REGISTRY],
  );
  const activeQrAnchors = showParkingMap
    ? allRegisteredQrAnchors.filter((anchor) => anchor.floor === (parkingFloor === 1 ? 0 : -1))
    : allRegisteredQrAnchors.filter((anchor) => anchor.floor === activeFloor);
  const activeMapSvgPath = showParkingMap
    ? (parkingFloor === 1 ? "/images/Lahan%20Parkir%20Lantai%201.svg" : "/images/Lahan%20Parkir%20Lantai%202.svg")
    : activeFloor === 1
    ? "/images/hospital-map.svg"
    : "/images/hospital-map-lantai-2.svg";
  const roomFloorById = useMemo(
    () =>
      allRegisteredQrAnchors.reduce<Record<string, 1 | 2>>((acc, anchor) => {
        if (anchor.floor === 1 || anchor.floor === 2) {
          acc[anchor.roomId] = anchor.floor;
        }
        return acc;
      }, {}),
    [allRegisteredQrAnchors],
  );
  const [multiFloorSvgDocs, setMultiFloorSvgDocs] = useState<Partial<Record<1 | 2, Document>>>({});
  const [parkingSvgDoc, setParkingSvgDoc] = useState<Document | null>(null);
  const [parking2SvgDoc, setParking2SvgDoc] = useState<Document | null>(null);
  const multiFloorConnectors = [
    {
      id: "lift",
      label: "Lift",
      rooms: {
        1: "Lift_Lantai_1",
        2: "Lift_Lantai_2",
      } as const,
    },
    {
      id: "main_stairs",
      label: "Tangga utama",
      rooms: {
        1: "Tangga_Lantai_1",
        2: "Tangga_Lantai_2",
      } as const,
    },
    {
      id: "evac_stairs",
      label: "Tangga evakuasi",
      rooms: {
        1: "Tangga_Evakuasi_Lantai_1",
        2: "Tangga_Evakuasi_Lantai_2",
      } as const,
    },
    {
      id: "parking_stairs",
      label: "Tangga Pengunjung Parkir",
      rooms: {
        0: "Tangga_Pengunjung_di_Lahan_Parkir_lantai_1",
        "-1": "Tangga_Pengunjung_di_Lahan_Parkir_lantai_2",
      } as const,
    },
  ] as const;

  // Keep refs in sync with state
  useEffect(() => { scaleRef.current = scale; }, [scale]);
  useEffect(() => { positionRef.current = position; }, [position]);
  useEffect(() => { liveSvgPointRef.current = liveSvgPoint; }, [liveSvgPoint]);
  useEffect(() => { startRoomIdRef.current = startRoomId; }, [startRoomId]);
  useEffect(() => { endRoomIdRef.current = endRoomId; }, [endRoomId]);

  useEffect(() => {
    let isCancelled = false;

    const loadMultiFloorSvgDocs = async () => {
      if (typeof window === "undefined" || typeof DOMParser === "undefined") return;

      const parser = new DOMParser();
      const nextDocs: Partial<Record<1 | 2, Document>> = {};

      for (const floor of [1, 2] as const) {
        const liveDoc = floor === activeFloor && !showParkingMap ? objectRef.current?.contentDocument ?? null : null;
        if (liveDoc) {
          nextDocs[floor] = liveDoc;
          continue;
        }

        try {
          const response = await fetch(
            floor === 1 ? "/images/hospital-map.svg" : "/images/hospital-map-lantai-2.svg"
          );
          if (!response.ok) continue;
          const svgText = await response.text();
          nextDocs[floor] = parser.parseFromString(svgText, "image/svg+xml");
        } catch (error) {
          // SVG preload failed
        }
      }

      if (!isCancelled) {
        setMultiFloorSvgDocs((prev) => ({
          ...prev,
          ...nextDocs,
        }));
      }

      // Load parking SVG for pathfinding
      try {
        const res = await fetch("/images/Lahan%20Parkir%20Lantai%201.svg");
        if (res.ok) {
          const text = await res.text();
          if (!isCancelled) setParkingSvgDoc(parser.parseFromString(text, "image/svg+xml"));
        }
      } catch {
        // parking SVG optional
      }

      // Load parking floor 2 SVG for pathfinding
      try {
        const res = await fetch("/images/Lahan%20Parkir%20Lantai%202.svg");
        if (res.ok) {
          const text = await res.text();
          if (!isCancelled) setParking2SvgDoc(parser.parseFromString(text, "image/svg+xml"));
        }
      } catch {
        // parking floor 2 SVG optional
      }
    };

    void loadMultiFloorSvgDocs();

    return () => {
      isCancelled = true;
    };
  }, [activeFloor, showParkingMap, parkingFloor, svgReadyVersion]);

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
    setRouteDebugMessage((prev) => {
      if (activeRoute?.floorsInvolved?.length) {
        return `Menampilkan denah lantai ${activeFloor}. Rute debug tetap aktif untuk ${activeRoute.floorsInvolved.join(" & ")} lantai.`;
      }
      return `Menampilkan denah lantai ${activeFloor}.`;
    });
    setNavSteps([]);
    setActiveStepIndex(0);
    setActiveRoomInfo(null);
    setActiveRoomId(null);
    setShowCurrentUserMarker(false);
  }, [activeFloor, activeRoute]);

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

  const resolveFloorForRoom = useCallback((room: HospitalRoomInfo): 1 | 2 => {
    const floorFromAnchor = roomFloorById[room.id];
    if (floorFromAnchor) return floorFromAnchor;

    const normalizedHint = room.locationHint.toLowerCase();
    if (normalizedHint.includes("lantai 2")) return 2;
    return 1;
  }, [roomFloorById]);

  const debugRoutingRooms = useMemo(
    () =>
      routingRoomOptions
        .map((room) => ({
          ...room,
          floor: resolveFloorForRoom(room),
        }))
        .sort((a, b) => a.floor - b.floor || a.name.localeCompare(b.name)),
    [routingRoomOptions, resolveFloorForRoom]
  );

  const debugRoutingRoomsByFloor = useMemo(
    () => ({
      1: debugRoutingRooms.filter((room) => room.floor === 1),
      2: debugRoutingRooms.filter((room) => room.floor === 2),
    }),
    [debugRoutingRooms]
  );

  const getFloorSvgDoc = useCallback((floor: 1 | 2): Document | null => {
    if (floor === activeFloor) {
      return objectRef.current?.contentDocument || multiFloorSvgDocs[floor] || null;
    }
    return multiFloorSvgDocs[floor] || null;
  }, [activeFloor, multiFloorSvgDocs]);

  const getRouteSegmentForFloor = useCallback((route: RoomRouteResult | null, floor: 1 | 2) => {
    if (!route) return null;
    const visibleFloor: -1 | 0 | 1 | 2 = showParkingMap ? (parkingFloor === 1 ? 0 : -1) : floor;
    const segment = route.floorSegments?.find((item) => item.floor === visibleFloor);
    if (segment) return segment;

    if (showParkingMap) {
      const startFloor = Object.values(QR_ANCHOR_REGISTRY).find((anchor) => anchor.roomId === route.startRoomId)?.floor;
      const endFloor = Object.values(QR_ANCHOR_REGISTRY).find((anchor) => anchor.roomId === route.endRoomId)?.floor;
      const parkingFloorValue = parkingFloor === 1 ? 0 : -1;
      const involvesActiveParkingFloor = startFloor === parkingFloorValue || endFloor === parkingFloorValue;
      if (involvesActiveParkingFloor && (!route.floorSegments || route.floorSegments.length === 0)) {
        return {
          floor,
          checkpointIds: route.checkpointIds,
          points: route.points,
          totalDistance: route.totalDistance,
        };
      }
    }

    // If no specific segment for this floor, check if route is single-floor
    // Only show route if it doesn't have floorSegments (meaning it's a single-floor route)
    if (!route.floorSegments || route.floorSegments.length === 0) {
      return {
        floor,
        checkpointIds: route.checkpointIds,
        points: route.points,
        totalDistance: route.totalDistance,
      };
    }
    
    // Route exists but not on this floor - don't show anything
    return null;
  }, [parkingFloor, showParkingMap]);

  // Resolve floor including parking (floor 0 for parking L1, floor -1 for parking L2)
  const resolveFloorForRoomExtended = useCallback((roomId: string): -1 | 0 | 1 | 2 => {
    const anchor = Object.values(QR_ANCHOR_REGISTRY).find((a) => a.roomId === roomId);
    if (anchor?.floor === 0) return 0;   // Parking L1
    if (anchor?.floor === -1) return -1; // Parking L2
    const room = roomInfoBySvgId[roomId];
    if (!room) return 1;
    return resolveFloorForRoom(room);
  }, [resolveFloorForRoom]);

  // Physical connection points between the parking SVG and hospital floor-1 SVG.
  // Hospital SVG: Check_Point_Lahan_Parkir  cx=704.67902  cy=108.32964
  // Parking SVG:  Check_Point_Keluar_dari_Lahan_Parkir  cx=1181.4375  cy=751.0
  const PARKING_CONN = useMemo(() => ({
    hospitalNodeId: "Check_Point_Lahan_Parkir",
    hospitalX: 704.67902,
    hospitalY: 108.32964,
    parkingNodeId: "Check_Point_Keluar_dari_Lahan_Parkir",
    parkingX: 1181.4375,
    parkingY: 751.0,
  }), []);

  // Physical connection points between parking floor 2 SVG and hospital floor-2 SVG.
  // Hospital Floor 2: Jalan_menuju_ke_Lahan_Parkir__Pengunjung_ path starts at (463.4375, 105.25)
  // Parking Floor 2: Akses_Jembatan_Menuju_Gedung_Rumah_Sakit_Lantai_2 path ends at (471.46345, 766.68053)
  const PARKING2_CONN = useMemo(() => ({
    hospitalNodeId: "Check_Point_Jembatan_Parkir_L2",
    hospitalX: 463.4375,
    hospitalY: 105.25,
    parkingNodeId: "Check_Point_Jembatan_Parkir_L2_Parking",
    parkingX: 471.46345,
    parkingY: 766.68053,
  }), []);

  const buildDebugRouteForRooms = useCallback((
    startRoomIdParam: string,
    endRoomIdParam: string,
    options?: {
      startPoint?: { x: number; y: number };
      endPoint?: { x: number; y: number };
      useExactStartPoint?: boolean;
      startNodeId?: string;
    },
  ): RoomRouteResult | null => {
    const startFloor = resolveFloorForRoomExtended(startRoomIdParam);
    const endFloor   = resolveFloorForRoomExtended(endRoomIdParam);

    const getDoc = (floor: -1 | 0 | 1 | 2): Document | null => {
      if (floor === 0) return parkingSvgDoc;
      if (floor === -1) return parking2SvgDoc;
      return getFloorSvgDoc(floor);
    };

    // ── Same floor ────────────────────────────────────────────────────────────
    if (startFloor === endFloor) {
      const svgDoc = getDoc(startFloor);
      if (!svgDoc) return null;

      // Inject QR anchor coords as virtual nodes for parking-only routes
      if (startFloor === 0 || startFloor === -1) {
        const sa = Object.values(QR_ANCHOR_REGISTRY).find((a) => a.roomId === startRoomIdParam && a.floor === startFloor);
        const ea = Object.values(QR_ANCHOR_REGISTRY).find((a) => a.roomId === endRoomIdParam   && a.floor === startFloor);
        if (sa) injectVirtualAnchorNode(svgDoc, startRoomIdParam, sa.svgX, sa.svgY);
        if (ea) injectVirtualAnchorNode(svgDoc, endRoomIdParam,   ea.svgX, ea.svgY);
      }

      // If using exact start point (QR scan), inject virtual node at QR coordinates
      if (options?.useExactStartPoint && options?.startPoint) {
        const route = buildRouteForRooms(startRoomIdParam, endRoomIdParam, svgDoc, options);
        if (!route) return null;
        return {
          ...route,
          startRoomId: startRoomIdParam,
          endRoomId: endRoomIdParam,
          floorSegments: (startFloor !== 0 && startFloor !== -1) ? [{ floor: startFloor as 1 | 2, checkpointIds: route.checkpointIds, points: route.points, totalDistance: route.totalDistance }] : undefined,
          floorsInvolved: (startFloor !== 0 && startFloor !== -1) ? [startFloor as 1 | 2] : undefined,
        };
      }

      const route = buildRouteForRooms(startRoomIdParam, endRoomIdParam, svgDoc, {
        ...options,
        useExactStartPoint: !preferRoomCenterStartRef.current && Boolean(options?.startPoint),
      });
      if (!route) return null;
      return {
        ...route,
        floorSegments: (startFloor !== 0 && startFloor !== -1) ? [{ floor: startFloor as 1 | 2, checkpointIds: route.checkpointIds, points: route.points, totalDistance: route.totalDistance }] : undefined,
        floorsInvolved: (startFloor !== 0 && startFloor !== -1) ? [startFloor as 1 | 2] : undefined,
      };
    }

    // Parking L1 <-> Parking L2 uses the visitor stair between the two parking maps.
    if ((startFloor === 0 && endFloor === -1) || (startFloor === -1 && endFloor === 0)) {
      const parkL1Doc = getDoc(0);
      const parkL2Doc = getDoc(-1);
      if (!parkL1Doc || !parkL2Doc) return null;

      const isL1Start = startFloor === 0;
      const parkingL1RoomId = isL1Start ? startRoomIdParam : endRoomIdParam;
      const parkingL2RoomId = isL1Start ? endRoomIdParam : startRoomIdParam;

      const l1Anchor = Object.values(QR_ANCHOR_REGISTRY).find(
        (a) => a.roomId === parkingL1RoomId && a.floor === 0
      );
      const l2Anchor = Object.values(QR_ANCHOR_REGISTRY).find(
        (a) => a.roomId === parkingL2RoomId && a.floor === -1
      );
      if (!l1Anchor || !l2Anchor) return null;

      // L1 stair checkpoint: Start point for routing from Parking L1
      const l1StairCheckpoint = {
        nodeId: "Check_Point_Tangga_Pengunjung",
        x: 1293.9375,
        y: 644.75,
      };
      // L1 stair entrance: path runs at y≈644.625; node for entering the stair path
      const l1StairEntrance = {
        nodeId: "Masuk_ke_Tangga_Pengunjung_ke_Lantai_2_Lahan_Parkir",
        x: 1181.125,
        y: 644.625,
      };
      // L2 stair checkpoint: Must stop here when transitioning between floors
      const l2StairCheckpoint = {
        nodeId: "Check_Point_Tangga_Pengunjung_Parkir_Lantai_2",
        x: 1293.6519,
        y: 651.59888,
      };
      // L2 stair entrance: ELLIPSE node Persimpangan_Khusus_Pengunjung_RS_Untuk_ke_Tangga_Pengunjung
      // cx=1246.0989, cy=651.422 — sits at start of path Masuk_ke_Tangga_Pengunjung in Parking L2 SVG
      const l2StairExit = {
        nodeId: "Persimpangan_Khusus_Pengunjung_RS_Untuk_ke_Tangga_Pengunjung",
        x: 1246.0989,
        y: 651.42212,
      };

      const l1AnchorNodeId = l1Anchor.routeNodeId || parkingL1RoomId;
      const l2AnchorNodeId = l2Anchor.routeNodeId || parkingL2RoomId;

      injectVirtualAnchorNode(parkL1Doc, l1AnchorNodeId, l1Anchor.svgX, l1Anchor.svgY);
      injectVirtualAnchorNode(parkL1Doc, l1StairCheckpoint.nodeId, l1StairCheckpoint.x, l1StairCheckpoint.y);
      injectVirtualAnchorNode(parkL1Doc, l1StairEntrance.nodeId, l1StairEntrance.x, l1StairEntrance.y);
      injectVirtualAnchorNode(parkL2Doc, l2StairCheckpoint.nodeId, l2StairCheckpoint.x, l2StairCheckpoint.y);
      injectVirtualAnchorNode(parkL2Doc, l2StairExit.nodeId, l2StairExit.x, l2StairExit.y);
      injectVirtualAnchorNode(parkL2Doc, l2AnchorNodeId, l2Anchor.svgX, l2Anchor.svgY);

      // Route in L1: direct anchor ↔ stair point (no intermediate checkpoint detour).
      // Forward (L1→L2): anchor → stairEntrance (shortest path to stair path)
      // Reverse (L2→L1): checkpoint → anchor (user exits stair at Check_Point_Tangga_Pengunjung)
      const parkingL1DirectRoute = isL1Start
        ? buildRouteFromPoint(
            l1AnchorNodeId,
            l1Anchor.svgX,
            l1Anchor.svgY,
            l1StairEntrance.nodeId,
            parkL1Doc,
            "point_to_point",
          )
        : buildRouteFromPoint(
            l1StairCheckpoint.nodeId,
            l1StairCheckpoint.x,
            l1StairCheckpoint.y,
            l1AnchorNodeId,
            parkL1Doc,
            "point_to_point",
          );

      if (!parkingL1DirectRoute) return null;

      const parkingL1Segment: RoomRouteResult = {
        startRoomId: isL1Start ? l1AnchorNodeId : l1StairCheckpoint.nodeId,
        endRoomId:   isL1Start ? l1StairEntrance.nodeId : l1AnchorNodeId,
        checkpointIds: parkingL1DirectRoute.checkpointIds,
        points:        parkingL1DirectRoute.points,
        totalDistance: parkingL1DirectRoute.totalDistance,
      };

      // Route in L2:
      // Forward (L1→L2): start from checkpoint directly → destination (mirror of L1 reverse fix)
      // Reverse (L2→L1): destination → checkpoint → stair exit
      const parkingL2ForwardRoute = isL1Start
        ? buildRouteFromPoint(
            l2StairCheckpoint.nodeId,
            l2StairCheckpoint.x,
            l2StairCheckpoint.y,
            l2AnchorNodeId,
            parkL2Doc,
            "point_to_point",
          )
        : buildRouteFromPoint(
            l2AnchorNodeId,
            l2Anchor.svgX,
            l2Anchor.svgY,
            l2StairCheckpoint.nodeId,
            parkL2Doc,
            "point_to_point",
          );

      const parkingL2StairToCheckpoint = !isL1Start
        ? buildRouteFromPoint(
            l2StairExit.nodeId,
            l2StairExit.x,
            l2StairExit.y,
            l2StairCheckpoint.nodeId,
            parkL2Doc,
            "point_to_point",
          )
        : null;

      if (!parkingL2ForwardRoute) return null;
      if (!isL1Start && !parkingL2StairToCheckpoint) return null;

      // Combine L2 segments
      const parkingL2Segment: RoomRouteResult = isL1Start
        ? {
            startRoomId: l2StairCheckpoint.nodeId,
            endRoomId: l2AnchorNodeId,
            checkpointIds: parkingL2ForwardRoute.checkpointIds,
            points:        parkingL2ForwardRoute.points,
            totalDistance: parkingL2ForwardRoute.totalDistance,
          }
        : {
            startRoomId: l2AnchorNodeId,
            endRoomId: l2StairExit.nodeId,
            checkpointIds: [
              ...parkingL2ForwardRoute.checkpointIds,
              ...parkingL2StairToCheckpoint!.checkpointIds.slice(1),
            ],
            points: [
              ...parkingL2ForwardRoute.points,
              ...parkingL2StairToCheckpoint!.points.slice(1),
            ],
            totalDistance: parkingL2ForwardRoute.totalDistance + parkingL2StairToCheckpoint!.totalDistance,
          };

      const activeSegment = showParkingMap && parkingFloor === 2 ? parkingL2Segment : parkingL1Segment;

      return {
        startRoomId: startRoomIdParam,
        endRoomId: endRoomIdParam,
        checkpointIds: isL1Start
          ? [
              ...parkingL1Segment.checkpointIds,
              "transition_parking_l1_stair",
              ...parkingL2Segment.checkpointIds,
            ]
          : [
              ...parkingL2Segment.checkpointIds,
              "transition_parking_l2_stair",
              ...parkingL1Segment.checkpointIds,
            ],
        points: activeSegment.points,
        totalDistance: parkingL1Segment.totalDistance + parkingL2Segment.totalDistance,
        floorSegments: [
          {
            floor: 0,
            checkpointIds: parkingL1Segment.checkpointIds,
            points: parkingL1Segment.points,
            totalDistance: parkingL1Segment.totalDistance,
          },
          {
            floor: -1,
            checkpointIds: parkingL2Segment.checkpointIds,
            points: parkingL2Segment.points,
            totalDistance: parkingL2Segment.totalDistance,
          },
        ],
        floorsInvolved: [0, -1],
        transitionLabel: "Tangga Pengunjung Parkir L1 ke Parkir L2",
      };
    }

    // Parking L1 -> Hospital L2 must go through the visitor stair to Parking L2,
    // then continue across the Parking L2 bridge into the hospital.
    if (startFloor === 0 && endFloor === 2) {
      const parkL1Doc = getDoc(0);
      const parkL2Doc = getDoc(-1);
      const hospitalL2Doc = getDoc(2);
      if (!parkL1Doc || !parkL2Doc || !hospitalL2Doc) return null;

      const parkAnchor = Object.values(QR_ANCHOR_REGISTRY).find(
        (a) => a.roomId === startRoomIdParam && a.floor === 0
      );
      if (!parkAnchor) return null;

      // L1 stair checkpoint: Start point for routing from Parking L1
      const l1StairCheckpoint = {
        nodeId: "Check_Point_Tangga_Pengunjung",
        x: 1293.9375,
        y: 644.75,
      };
      // L1 stair entrance: path runs at y≈644.625; node for entering the stair path
      const l1StairEntrance = {
        nodeId: "Masuk_ke_Tangga_Pengunjung_ke_Lantai_2_Lahan_Parkir",
        x: 1181.125,
        y: 644.625,
      };
      // L2 stair checkpoint: Must stop here when transitioning between floors
      const l2StairCheckpoint = {
        nodeId: "Check_Point_Tangga_Pengunjung_Parkir_Lantai_2",
        x: 1293.6519,
        y: 651.59888,
      };
      // L2 stair entrance: ELLIPSE node Persimpangan_Khusus_Pengunjung_RS_Untuk_ke_Tangga_Pengunjung
      // cx=1246.0989, cy=651.422 — sits at start of path Masuk_ke_Tangga_Pengunjung in Parking L2 SVG
      const l2StairExit = {
        nodeId: "Persimpangan_Khusus_Pengunjung_RS_Untuk_ke_Tangga_Pengunjung",
        roomId: "Tangga_Pengunjung_Lahan_Parkir_ke_Lantai_2-2",
        x: 1246.0989,
        y: 651.42212,
      };
      const l2BridgeTurn = {
        nodeId: "Persimpangan_Khusus_Pengunjung_RS",
        x: 471.99377,
        y: 652.48279,
      };

      const startNodeId = parkAnchor.routeNodeId || startRoomIdParam;
      injectVirtualAnchorNode(parkL1Doc, startNodeId, parkAnchor.svgX, parkAnchor.svgY);
      injectVirtualAnchorNode(
        parkL1Doc,
        l1StairCheckpoint.nodeId,
        l1StairCheckpoint.x,
        l1StairCheckpoint.y,
      );
      injectVirtualAnchorNode(
        parkL1Doc,
        l1StairEntrance.nodeId,
        l1StairEntrance.x,
        l1StairEntrance.y,
      );
      injectVirtualAnchorNode(parkL2Doc, l2StairCheckpoint.nodeId, l2StairCheckpoint.x, l2StairCheckpoint.y);
      injectVirtualAnchorNode(parkL2Doc, l2StairExit.nodeId, l2StairExit.x, l2StairExit.y);
      injectVirtualAnchorNode(parkL2Doc, l2BridgeTurn.nodeId, l2BridgeTurn.x, l2BridgeTurn.y);
      injectVirtualAnchorNode(
        parkL2Doc,
        PARKING2_CONN.parkingNodeId,
        PARKING2_CONN.parkingX,
        PARKING2_CONN.parkingY,
      );
      injectVirtualAnchorNode(
        hospitalL2Doc,
        PARKING2_CONN.hospitalNodeId,
        PARKING2_CONN.hospitalX,
        PARKING2_CONN.hospitalY,
      );

      // Route in L1: directly to stair entrance (no intermediate checkpoint detour).
      const parkingL1DirectRoute = buildRouteFromPoint(
        startNodeId,
        parkAnchor.svgX,
        parkAnchor.svgY,
        l1StairEntrance.nodeId,
        parkL1Doc,
        "point_to_point",
      );
      if (!parkingL1DirectRoute) return null;

      const parkingL1Segment: RoomRouteResult = {
        startRoomId: startNodeId,
        endRoomId: l1StairEntrance.nodeId,
        checkpointIds: parkingL1DirectRoute.checkpointIds,
        points:        parkingL1DirectRoute.points,
        totalDistance: parkingL1DirectRoute.totalDistance,
      };

      // Route in L2 (L1→Hospital L2): start from checkpoint directly (no stairExit→checkpoint detour)
      // l2StairExit (x≈1246) sits before checkpoint (x≈1293); start from checkpoint for consistency.
      const parkingL2CheckpointToBridgeTurn = buildRouteFromPoint(
        l2StairCheckpoint.nodeId,
        l2StairCheckpoint.x,
        l2StairCheckpoint.y,
        l2BridgeTurn.nodeId,
        parkL2Doc,
        "point_to_point",
      );
      const parkingL2ToBridgeExit = buildRouteFromPoint(
        l2BridgeTurn.nodeId,
        l2BridgeTurn.x,
        l2BridgeTurn.y,
        PARKING2_CONN.parkingNodeId,
        parkL2Doc,
        "point_to_point",
      );
      if (!parkingL2CheckpointToBridgeTurn || !parkingL2ToBridgeExit) return null;

      const parkingL2Segment: RoomRouteResult = {
        startRoomId: l2StairCheckpoint.nodeId,
        endRoomId: PARKING2_CONN.parkingNodeId,
        checkpointIds: [
          ...parkingL2CheckpointToBridgeTurn.checkpointIds,
          ...parkingL2ToBridgeExit.checkpointIds.slice(1),
        ],
        points: [
          ...parkingL2CheckpointToBridgeTurn.points,
          ...parkingL2ToBridgeExit.points.slice(1),
        ],
        totalDistance: parkingL2CheckpointToBridgeTurn.totalDistance + parkingL2ToBridgeExit.totalDistance,
      };

      const hospitalSegment = buildRouteFromPoint(
        PARKING2_CONN.hospitalNodeId,
        PARKING2_CONN.hospitalX,
        PARKING2_CONN.hospitalY,
        endRoomIdParam,
        hospitalL2Doc,
        "point_to_room",
      );
      if (!hospitalSegment) return null;

      const activeSegment =
        showParkingMap && parkingFloor === 1
          ? parkingL1Segment
          : showParkingMap && parkingFloor === 2
            ? parkingL2Segment
            : hospitalSegment;

      return {
        startRoomId: startRoomIdParam,
        endRoomId: endRoomIdParam,
        checkpointIds: [
          ...parkingL1Segment.checkpointIds,
          "transition_parking_l1_stair",
          ...parkingL2Segment.checkpointIds,
          "transition_parking_l2_bridge",
          ...hospitalSegment.checkpointIds,
        ],
        points: activeSegment.points,
        totalDistance:
          parkingL1Segment.totalDistance +
          parkingL2Segment.totalDistance +
          hospitalSegment.totalDistance,
        floorSegments: [
          {
            floor: 0,
            checkpointIds: parkingL1Segment.checkpointIds,
            points: parkingL1Segment.points,
            totalDistance: parkingL1Segment.totalDistance,
          },
          {
            floor: -1,
            checkpointIds: parkingL2Segment.checkpointIds,
            points: parkingL2Segment.points,
            totalDistance: parkingL2Segment.totalDistance,
          },
          {
            floor: 2,
            checkpointIds: hospitalSegment.checkpointIds,
            points: hospitalSegment.points,
            totalDistance: hospitalSegment.totalDistance,
          },
        ],
        floorsInvolved: [0, -1, 2],
        transitionLabel: "Tangga Pengunjung Parkir L1 ke Jembatan Parkir L2",
      };
    }

    // ── Cross: parking L1 (0) ↔ hospital L1 ──────────────────────────────────
    // NOTE: Exclude Hospital L2 (2) → Parking L1 (0) — that case must go through Parking L2
    // as an intermediary (bridge + staircase), handled by isParkingL1Involved branch below.
    if ((startFloor === 0 || endFloor === 0) && !(startFloor === 2 && endFloor === 0)) {
      const hospitalFloor: 1 | 2 = startFloor === 0 ? (endFloor as 1 | 2) : (startFloor as 1 | 2);
      const hospitalRoomId = startFloor === 0 ? endRoomIdParam   : startRoomIdParam;
      const parkingRoomId  = startFloor === 0 ? startRoomIdParam : endRoomIdParam;
      const isParkingStart = startFloor === 0;

      const hospitalDoc = getDoc(hospitalFloor);
      const parkDoc     = getDoc(0);
      if (!hospitalDoc || !parkDoc) return null;

      const conn = PARKING_CONN;

      // Inject the parking QR anchor as a virtual node so the graph can find it
      const parkAnchor = Object.values(QR_ANCHOR_REGISTRY).find(
        (a) => a.roomId === parkingRoomId && a.floor === 0
      );
      if (parkAnchor) injectVirtualAnchorNode(parkDoc, parkingRoomId, parkAnchor.svgX, parkAnchor.svgY);

      // Inject connector nodes into both SVGs
      injectVirtualAnchorNode(parkDoc,     conn.parkingNodeId,  conn.parkingX,  conn.parkingY);
      injectVirtualAnchorNode(hospitalDoc, conn.hospitalNodeId, conn.hospitalX, conn.hospitalY);

      let parkingSegment, hospitalSegment;

      if (isParkingStart) {
        // Parking → Hospital: Start from QR anchor, route to Check_Point_Keluar_dari_Lahan_Parkir
        const parkAnchor = Object.values(QR_ANCHOR_REGISTRY).find(
          (a) => a.roomId === parkingRoomId && a.floor === 0
        );

        if (!parkAnchor) {
          return null;
        }

        // Use the routeNodeId if available, otherwise use parkingRoomId
        const startNodeId = parkAnchor.routeNodeId || parkingRoomId;

        // Inject both start and end nodes
        injectVirtualAnchorNode(parkDoc, startNodeId, parkAnchor.svgX, parkAnchor.svgY);
        injectVirtualAnchorNode(parkDoc, conn.parkingNodeId, conn.parkingX, conn.parkingY);

        // Use buildRouteFromPoint with point_to_point mode
        parkingSegment = buildRouteFromPoint(
          startNodeId,
          parkAnchor.svgX,
          parkAnchor.svgY,
          conn.parkingNodeId,
          parkDoc,
          "point_to_point",
        );

        if (parkingSegment) {
          // ✅ FORCE OVERRIDE: Ensure first & last checkpoints are correct
          const ids = parkingSegment.checkpointIds;
          if (ids[0] !== startNodeId) ids.unshift(startNodeId);
          if (ids[ids.length - 1] !== conn.parkingNodeId) ids.push(conn.parkingNodeId);
        }
        
        hospitalSegment = buildRouteFromPoint(
          conn.hospitalNodeId, conn.hospitalX, conn.hospitalY,
          hospitalRoomId, hospitalDoc,
          "point_to_room",
        );
      } else {
        // Hospital → Parking: hospitalRoom → connector, connector → parkingRoom
        if (options?.useExactStartPoint && options?.startPoint) {
          hospitalSegment = buildRouteForRooms(
            hospitalRoomId,
            conn.hospitalNodeId,
            hospitalDoc,
            options,
          );
          if (!hospitalSegment) {
            const virtualStartNodeId = `virtual_qr_start_${hospitalRoomId}`;
            injectVirtualAnchorNode(
              hospitalDoc,
              virtualStartNodeId,
              options.startPoint.x,
              options.startPoint.y,
            );

            hospitalSegment = buildRouteFromPoint(
              virtualStartNodeId,
              options.startPoint.x,
              options.startPoint.y,
              conn.hospitalNodeId,
              hospitalDoc,
              "point_to_point",
            );
          }
          if (!hospitalSegment) {
            hospitalSegment = buildRouteFromPoint(
              conn.hospitalNodeId,
              conn.hospitalX,
              conn.hospitalY,
              hospitalRoomId,
              hospitalDoc,
              "room_to_point",
            );
          }
        } else {
          hospitalSegment = buildRouteFromPoint(
            conn.hospitalNodeId, conn.hospitalX, conn.hospitalY,
            hospitalRoomId, hospitalDoc,
            "room_to_point",
          );
        }
        
        // For Hospital → Parking L1, ALWAYS route from Check_Point_Keluar_dari_Lahan_Parkir
        // to the specific QR anchor location using point-to-point routing
        const parkAnchor = Object.values(QR_ANCHOR_REGISTRY).find(
          (a) => a.roomId === parkingRoomId && a.floor === 0
        );

        if (!parkAnchor) {
          return null;
        }

        // Use the routeNodeId if available, otherwise use parkingRoomId
        const endpointNodeId = parkAnchor.routeNodeId || parkingRoomId;

        // Inject both start and end nodes to ensure they exist
        injectVirtualAnchorNode(parkDoc, conn.parkingNodeId, conn.parkingX, conn.parkingY);
        injectVirtualAnchorNode(parkDoc, endpointNodeId, parkAnchor.svgX, parkAnchor.svgY);

        // Use buildRouteFromPoint with point_to_point mode to ensure exact coordinates are used
        parkingSegment = buildRouteFromPoint(
          conn.parkingNodeId,
          conn.parkingX,
          conn.parkingY,
          endpointNodeId,
          parkDoc,
          "point_to_point",
        );

        if (parkingSegment) {
          // ✅ FORCE OVERRIDE: Ensure first & last checkpoints are correct
          const ids = parkingSegment.checkpointIds;
          if (ids[0] !== conn.parkingNodeId) ids.unshift(conn.parkingNodeId);
          if (ids[ids.length - 1] !== endpointNodeId) ids.push(endpointNodeId);
        }
      }

      // Show whichever segment matches the currently visible map
      const activeSegment = (showParkingMap && parkingFloor === 1) ? parkingSegment : hospitalSegment;
      if (!activeSegment) return null;

      return {
        startRoomId: startRoomIdParam,
        endRoomId: endRoomIdParam,
        checkpointIds: [
          ...(isParkingStart ? parkingSegment?.checkpointIds ?? [] : hospitalSegment?.checkpointIds ?? []),
          "transition_parking",
          ...(isParkingStart ? hospitalSegment?.checkpointIds ?? [] : parkingSegment?.checkpointIds ?? []),
        ],
        points: activeSegment.points,
        totalDistance: (parkingSegment?.totalDistance ?? 0) + (hospitalSegment?.totalDistance ?? 0),
        floorSegments: [
          ...(parkingSegment ? [{
            floor: 0 as const,
            checkpointIds: parkingSegment.checkpointIds,
            points: parkingSegment.points,
            totalDistance: parkingSegment.totalDistance,
          }] : []),
          ...(hospitalSegment ? [{
            floor: hospitalFloor,
            checkpointIds: hospitalSegment.checkpointIds,
            points: hospitalSegment.points,
            totalDistance: hospitalSegment.totalDistance,
          }] : []),
        ],
        floorsInvolved: [hospitalFloor],
        transitionLabel: "Tangga Pengunjung Parkir",
      };
    }

    // ── Cross: parking L2 (-1) ↔ hospital (must involve parking L2 explicitly) ──
    // Also handles Hospital L2 (2) ↔ Parking L1 (0), which must route THROUGH Parking L2 as intermediary
    if ((startFloor === -1 && endFloor !== 2) || (endFloor === -1 && startFloor !== 2) ||
        (startFloor === 2 && endFloor === 0) || (startFloor === 0 && endFloor === 2)) {
      // Special case: Parking L2 ↔ Hospital L1 (must go through Hospital L2 first, then down to L1)
      // OR Hospital L2 ↔ Parking L1 (must go through Parking L2 first, then down to L1)
      if ((startFloor === -1 && endFloor === 1) || (startFloor === 1 && endFloor === -1) ||
          (startFloor === 2 && endFloor === 0) || (startFloor === 0 && endFloor === 2)) {
        
        // Determine if this is a parking route
        const isParkingL2Involved = startFloor === -1 || endFloor === -1;
        const isParkingL1Involved = startFloor === 0 || endFloor === 0;
        
        if (isParkingL2Involved) {
          // Parking L2 ↔ Hospital L1
          const isParkingStart = startFloor === -1;
          const parkingRoomId = isParkingStart ? startRoomIdParam : endRoomIdParam;
          const hospitalL1RoomId = isParkingStart ? endRoomIdParam : startRoomIdParam;

          const parkDoc = getDoc(-1);
          const hospitalL2Doc = getDoc(2);
          const hospitalL1Doc = getDoc(1);
          if (!parkDoc || !hospitalL2Doc || !hospitalL1Doc) return null;

          const conn = PARKING2_CONN;

          // Inject the parking L2 QR anchor as a virtual node
          const parkAnchor = Object.values(QR_ANCHOR_REGISTRY).find(
            (a) => a.roomId === parkingRoomId && a.floor === -1
          );
          if (parkAnchor) injectVirtualAnchorNode(parkDoc, parkingRoomId, parkAnchor.svgX, parkAnchor.svgY);

          // Inject connector nodes
          injectVirtualAnchorNode(parkDoc, conn.parkingNodeId, conn.parkingX, conn.parkingY);
          injectVirtualAnchorNode(hospitalL2Doc, conn.hospitalNodeId, conn.hospitalX, conn.hospitalY);

          // Find best connector between L2 and L1
          let bestL2L1Route: RoomRouteResult | null = null;
          let bestConnectorLabel: string | null = null;

          multiFloorConnectors.forEach((connector) => {
            const l2ConnectorRoomId = connector.rooms[2];
            const l1ConnectorRoomId = connector.rooms[1];

            const l2Segment = isParkingStart
              ? buildRouteForRooms(conn.hospitalNodeId, l2ConnectorRoomId, hospitalL2Doc)
              : buildRouteForRooms(l2ConnectorRoomId, conn.hospitalNodeId, hospitalL2Doc);

            const l1Segment = isParkingStart
              ? buildRouteForRooms(
                  l1ConnectorRoomId,
                  hospitalL1RoomId,
                  hospitalL1Doc,
                  { endPoint: options?.endPoint },
                )
              : buildRouteForRooms(
                  hospitalL1RoomId,
                  l1ConnectorRoomId,
                  hospitalL1Doc,
                  {
                    startPoint: options?.startPoint,
                    useExactStartPoint: options?.useExactStartPoint,
                    startNodeId: options?.startNodeId,
                  },
                );
            if (!l2Segment || !l1Segment) return;

            const candidateDistance = l2Segment.totalDistance + l1Segment.totalDistance;
            if (bestL2L1Route && bestL2L1Route.totalDistance <= candidateDistance) return;

            bestL2L1Route = {
              startRoomId: isParkingStart ? conn.hospitalNodeId : hospitalL1RoomId,
              endRoomId: isParkingStart ? hospitalL1RoomId : conn.hospitalNodeId,
              checkpointIds: isParkingStart
                ? [...l2Segment.checkpointIds, `transition_${connector.id}`, ...l1Segment.checkpointIds]
                : [...l1Segment.checkpointIds, `transition_${connector.id}`, ...l2Segment.checkpointIds],
              points: isParkingStart ? l1Segment.points : l2Segment.points,
              totalDistance: candidateDistance,
              floorSegments: [
                { floor: 2, checkpointIds: l2Segment.checkpointIds, points: l2Segment.points, totalDistance: l2Segment.totalDistance },
                { floor: 1, checkpointIds: l1Segment.checkpointIds, points: l1Segment.points, totalDistance: l1Segment.totalDistance },
              ],
              floorsInvolved: [2, 1],
              transitionLabel: connector.label,
            };
            bestConnectorLabel = connector.label;
          });

          if (!bestL2L1Route || !bestConnectorLabel) return null;

          // Build parking L2 segment
          let parkingSegment: RoomRouteResult | null = null;
          if (isParkingStart) {
            if (parkAnchor) {
              parkingSegment = buildRouteForRooms(
                parkingRoomId,
                conn.parkingNodeId,
                parkDoc,
                {
                  startPoint: { x: parkAnchor.svgX, y: parkAnchor.svgY },
                  useExactStartPoint: true,
                  startNodeId: parkAnchor.routeNodeId,
                },
              );
            }
            if (!parkingSegment) {
              parkingSegment = buildRouteFromPoint(
                conn.parkingNodeId, conn.parkingX, conn.parkingY,
                parkingRoomId, parkDoc,
                "room_to_point",
              );
            }
          } else {
            parkingSegment = buildRouteFromPoint(
              conn.parkingNodeId, conn.parkingX, conn.parkingY,
              parkingRoomId, parkDoc,
              "point_to_room",
            );
          }

          if (!parkingSegment) return null;

          // Determine which segment to show based on current map view
          let activeSegment: RoomRouteResult | null = null;
          if (showParkingMap && parkingFloor === 2) {
            activeSegment = parkingSegment;
          } else if (activeFloor === 2) {
            activeSegment = bestL2L1Route.floorSegments?.[0] ? {
              ...bestL2L1Route,
              points: bestL2L1Route.floorSegments[0].points,
              checkpointIds: bestL2L1Route.floorSegments[0].checkpointIds,
              totalDistance: bestL2L1Route.floorSegments[0].totalDistance,
            } : null;
          } else if (activeFloor === 1) {
            activeSegment = bestL2L1Route.floorSegments?.[1] ? {
              ...bestL2L1Route,
              points: bestL2L1Route.floorSegments[1].points,
              checkpointIds: bestL2L1Route.floorSegments[1].checkpointIds,
              totalDistance: bestL2L1Route.floorSegments[1].totalDistance,
            } : null;
          }

          if (!activeSegment) return null;

          return {
            startRoomId: startRoomIdParam,
            endRoomId: endRoomIdParam,
            checkpointIds: isParkingStart
              ? [
                  ...(parkingSegment.checkpointIds ?? []),
                  "transition_parking_l2",
                  ...(bestL2L1Route.checkpointIds ?? []),
                ]
              : [
                  ...(bestL2L1Route.checkpointIds ?? []),
                  "transition_parking_l2",
                  ...(parkingSegment.checkpointIds ?? []),
                ],
            points: activeSegment.points,
            totalDistance: parkingSegment.totalDistance + bestL2L1Route.totalDistance,
            floorSegments: [
              {
                floor: -1,
                checkpointIds: parkingSegment.checkpointIds,
                points: parkingSegment.points,
                totalDistance: parkingSegment.totalDistance,
              },
              ...(bestL2L1Route.floorSegments ?? []),
            ],
            floorsInvolved: [-1, 2, 1],
            transitionLabel: `Jembatan Parkir L2 → ${bestConnectorLabel}`,
          };
        } else if (isParkingL1Involved) {
          // Hospital L2 ↔ Parking L1 (must route through Parking L2)
          const isParkingStart = startFloor === 0;
          const parkingL1RoomId = isParkingStart ? startRoomIdParam : endRoomIdParam;
          const hospitalL2RoomId = isParkingStart ? endRoomIdParam : startRoomIdParam;

          const parkL1Doc = getDoc(0);
          const parkL2Doc = getDoc(-1);
          const hospitalL2Doc = getDoc(2);
          const hospitalL1Doc = getDoc(1);
          if (!parkL1Doc || !parkL2Doc || !hospitalL2Doc || !hospitalL1Doc) return null;

          // Step 1: Route within Parking L1 to stair entrance
          // L1 stair checkpoint: Start/end point for routing in Parking L1
          const l1StairCheckpoint = { nodeId: "Check_Point_Tangga_Pengunjung", x: 1293.9375, y: 644.75, roomId: "Parking_Lantai_1" };
          // L1 stair entrance: path Masuk_ke_Tangga_Pengunjung_ke_Lantai_2_Lahan_Parkir runs at y≈644.625
          const l1StairEntrance = { nodeId: "Masuk_ke_Tangga_Pengunjung_ke_Lantai_2_Lahan_Parkir", x: 1181.125, y: 644.625, roomId: "Parking_Lantai_1" };
          injectVirtualAnchorNode(parkL1Doc, l1StairCheckpoint.nodeId, l1StairCheckpoint.x, l1StairCheckpoint.y);
          injectVirtualAnchorNode(parkL1Doc, l1StairEntrance.nodeId, l1StairEntrance.x, l1StairEntrance.y);

          const parkL1Anchor = Object.values(QR_ANCHOR_REGISTRY).find(
            (a) => a.roomId === parkingL1RoomId && a.floor === 0
          );
          if (parkL1Anchor) injectVirtualAnchorNode(parkL1Doc, parkingL1RoomId, parkL1Anchor.svgX, parkL1Anchor.svgY);

          // Route in L1: directly anchor ↔ stair point.
          // Forward (isParkingStart): anchor → stairEntrance
          // Reverse: checkpoint → parkingL1 (user exits stair at Check_Point_Tangga_Pengunjung)
          const L1_STAIR_CHECKPOINT_NODE = "Check_Point_Tangga_Pengunjung";
          const L1_STAIR_CHECKPOINT_X = 1293.9375;
          const L1_STAIR_CHECKPOINT_Y = 644.75;
          injectVirtualAnchorNode(parkL1Doc, L1_STAIR_CHECKPOINT_NODE, L1_STAIR_CHECKPOINT_X, L1_STAIR_CHECKPOINT_Y);

          const parkingL1DirectRoute = isParkingStart
            ? buildRouteForRooms(parkingL1RoomId, l1StairEntrance.nodeId, parkL1Doc, {
                startPoint: parkL1Anchor ? { x: parkL1Anchor.svgX, y: parkL1Anchor.svgY } : undefined,
                useExactStartPoint: parkL1Anchor ? true : false,
                startNodeId: parkL1Anchor?.routeNodeId,
              })
            : buildRouteForRooms(L1_STAIR_CHECKPOINT_NODE, parkingL1RoomId, parkL1Doc);

          if (!parkingL1DirectRoute) return null;

          const parkingL1Segment: RoomRouteResult = {
            startRoomId: isParkingStart ? parkingL1RoomId : L1_STAIR_CHECKPOINT_NODE,
            endRoomId:   isParkingStart ? l1StairEntrance.nodeId : parkingL1RoomId,
            checkpointIds: parkingL1DirectRoute.checkpointIds,
            points:        parkingL1DirectRoute.points,
            totalDistance: parkingL1DirectRoute.totalDistance,
          };

          // Step 2: Route within Parking L2 from stair exit to bridge
          // L2 stair entrance: ELLIPSE node Persimpangan_Khusus_Pengunjung_RS_Untuk_ke_Tangga_Pengunjung
          // cx=1246.0989, cy=651.422 — sits at start of path Masuk_ke_Tangga_Pengunjung in Parking L2 SVG
          // L2 bridge check: Persimpangan_Khusus_Pengunjung_RS at cx=471.99, cy=652.48
          // IMPORTANT: Must stop at Check_Point_Tangga_Pengunjung_Parkir_Lantai_2 first
          const l2StairCheckpoint = { nodeId: "Check_Point_Tangga_Pengunjung_Parkir_Lantai_2", x: 1293.6519, y: 651.59888, roomId: "Tangga_Pengunjung_di_Lahan_Parkir_lantai_2" };
          const l2StairExit = { nodeId: "Persimpangan_Khusus_Pengunjung_RS_Untuk_ke_Tangga_Pengunjung", x: 1246.0989, y: 651.42212, roomId: "Parking_Lantai_2" };
          const l2BridgeTurn = { nodeId: "Persimpangan_Khusus_Pengunjung_RS", x: 471.99377, y: 652.48279, roomId: "Parking_Lantai_2" };
          injectVirtualAnchorNode(parkL2Doc, l2StairCheckpoint.nodeId, l2StairCheckpoint.x, l2StairCheckpoint.y);
          injectVirtualAnchorNode(parkL2Doc, l2StairExit.nodeId, l2StairExit.x, l2StairExit.y);
          injectVirtualAnchorNode(parkL2Doc, l2BridgeTurn.nodeId, l2BridgeTurn.x, l2BridgeTurn.y);

          const conn = PARKING2_CONN;
          injectVirtualAnchorNode(parkL2Doc, conn.parkingNodeId, conn.parkingX, conn.parkingY);

          // Route from stair exit to checkpoint, then to bridge turn, then to bridge exit
          const parkingL2ToCheckpoint = buildRouteFromPoint(
            l2StairExit.nodeId,
            l2StairExit.x,
            l2StairExit.y,
            l2StairCheckpoint.nodeId,
            parkL2Doc,
            "point_to_point",
          );
          const parkingL2CheckpointToBridgeTurn = buildRouteFromPoint(
            l2StairCheckpoint.nodeId,
            l2StairCheckpoint.x,
            l2StairCheckpoint.y,
            l2BridgeTurn.nodeId,
            parkL2Doc,
            "point_to_point",
          );
          const parkingL2ToBridgeExit = buildRouteFromPoint(
            l2BridgeTurn.nodeId,
            l2BridgeTurn.x,
            l2BridgeTurn.y,
            conn.parkingNodeId,
            parkL2Doc,
            "point_to_point",
          );
          if (!parkingL2ToCheckpoint || !parkingL2CheckpointToBridgeTurn || !parkingL2ToBridgeExit) return null;

          const parkingL2Segment: RoomRouteResult = {
            startRoomId: l2StairExit.roomId,
            endRoomId: conn.parkingNodeId,
            checkpointIds: [
              ...parkingL2ToCheckpoint.checkpointIds,
              ...parkingL2CheckpointToBridgeTurn.checkpointIds.slice(1),
              ...parkingL2ToBridgeExit.checkpointIds.slice(1),
            ],
            points: [
              ...parkingL2ToCheckpoint.points,
              ...parkingL2CheckpointToBridgeTurn.points.slice(1),
              ...parkingL2ToBridgeExit.points.slice(1),
            ],
            totalDistance: parkingL2ToCheckpoint.totalDistance + parkingL2CheckpointToBridgeTurn.totalDistance + parkingL2ToBridgeExit.totalDistance,
          };

          // Step 3: Route within Hospital L2 from bridge to L2 room or connector
          injectVirtualAnchorNode(hospitalL2Doc, conn.hospitalNodeId, conn.hospitalX, conn.hospitalY);

          // Find best connector between L2 and L1
          let bestL2L1Route: RoomRouteResult | null = null;
          let bestConnectorLabel: string | null = null;

          multiFloorConnectors.forEach((connector) => {
            const l2ConnectorRoomId = connector.rooms[2];
            const l1ConnectorRoomId = connector.rooms[1];

            const l2Segment = isParkingStart
              ? buildRouteForRooms(conn.hospitalNodeId, l2ConnectorRoomId, hospitalL2Doc)
              : buildRouteForRooms(hospitalL2RoomId, l2ConnectorRoomId, hospitalL2Doc, {
                  startPoint: options?.startPoint,
                  useExactStartPoint: options?.useExactStartPoint,
                  startNodeId: options?.startNodeId,
                });

            const l1Segment = buildRouteForRooms(l1ConnectorRoomId, PARKING_CONN.hospitalNodeId, hospitalL1Doc);
            if (!l2Segment || !l1Segment) return;

            const candidateDistance = l2Segment.totalDistance + l1Segment.totalDistance;
            if (bestL2L1Route && bestL2L1Route.totalDistance <= candidateDistance) return;

            bestL2L1Route = {
              startRoomId: isParkingStart ? conn.hospitalNodeId : hospitalL2RoomId,
              endRoomId: PARKING_CONN.hospitalNodeId,
              checkpointIds: [...l2Segment.checkpointIds, `transition_${connector.id}`, ...l1Segment.checkpointIds],
              points: l2Segment.points,
              totalDistance: candidateDistance,
              floorSegments: [
                { floor: 2, checkpointIds: l2Segment.checkpointIds, points: l2Segment.points, totalDistance: l2Segment.totalDistance },
                { floor: 1, checkpointIds: l1Segment.checkpointIds, points: l1Segment.points, totalDistance: l1Segment.totalDistance },
              ],
              floorsInvolved: [2, 1],
              transitionLabel: connector.label,
            };
            bestConnectorLabel = connector.label;
          });

          if (!bestL2L1Route || !bestConnectorLabel) return null;

          // Step 4: Route within Parking L1 from hospital entrance to parking room
          injectVirtualAnchorNode(parkL1Doc, PARKING_CONN.parkingNodeId, PARKING_CONN.parkingX, PARKING_CONN.parkingY);

          const parkingL1ToHospitalSegment = isParkingStart
            ? buildRouteForRooms(l1StairEntrance.nodeId, PARKING_CONN.parkingNodeId, parkL1Doc)
            : buildRouteForRooms(PARKING_CONN.parkingNodeId, parkingL1RoomId, parkL1Doc);

          if (!parkingL1ToHospitalSegment) return null;

          // Determine active segment based on current view
          let activeSegment: RoomRouteResult;
          if (showParkingMap && parkingFloor === 1) {
            activeSegment = isParkingStart ? parkingL1Segment : parkingL1ToHospitalSegment;
          } else if (showParkingMap && parkingFloor === 2) {
            activeSegment = parkingL2Segment;
          } else if (activeFloor === 2) {
            activeSegment = bestL2L1Route.floorSegments?.[0] ? {
              ...bestL2L1Route,
              points: bestL2L1Route.floorSegments[0].points,
              checkpointIds: bestL2L1Route.floorSegments[0].checkpointIds,
              totalDistance: bestL2L1Route.floorSegments[0].totalDistance,
            } : bestL2L1Route;
          } else {
            activeSegment = bestL2L1Route.floorSegments?.[1] ? {
              ...bestL2L1Route,
              points: bestL2L1Route.floorSegments[1].points,
              checkpointIds: bestL2L1Route.floorSegments[1].checkpointIds,
              totalDistance: bestL2L1Route.floorSegments[1].totalDistance,
            } : bestL2L1Route;
          }

          return {
            startRoomId: startRoomIdParam,
            endRoomId: endRoomIdParam,
            checkpointIds: isParkingStart
              ? [
                  ...parkingL1Segment.checkpointIds,
                  "transition_parking_l1_stair",
                  ...parkingL2Segment.checkpointIds,
                  "transition_parking_l2_bridge",
                  ...bestL2L1Route.checkpointIds,
                  "transition_hospital_l1_parking",
                  ...parkingL1ToHospitalSegment.checkpointIds,
                ]
              : [
                  ...bestL2L1Route.checkpointIds,
                  "transition_hospital_l1_parking",
                  ...parkingL1ToHospitalSegment.checkpointIds,
                  "transition_parking_l1_stair",
                  ...parkingL2Segment.checkpointIds,
                  "transition_parking_l2_bridge",
                  ...parkingL1Segment.checkpointIds,
                ],
            points: activeSegment.points,
            totalDistance: parkingL1Segment.totalDistance + parkingL2Segment.totalDistance + bestL2L1Route.totalDistance + parkingL1ToHospitalSegment.totalDistance,
            floorSegments: [
              { floor: 0, checkpointIds: isParkingStart ? parkingL1Segment.checkpointIds : parkingL1ToHospitalSegment.checkpointIds, points: isParkingStart ? parkingL1Segment.points : parkingL1ToHospitalSegment.points, totalDistance: isParkingStart ? parkingL1Segment.totalDistance : parkingL1ToHospitalSegment.totalDistance },
              { floor: -1, checkpointIds: parkingL2Segment.checkpointIds, points: parkingL2Segment.points, totalDistance: parkingL2Segment.totalDistance },
              ...(bestL2L1Route.floorSegments ?? []),
            ],
            floorsInvolved: [0, -1, 2, 1],
            transitionLabel: `Tangga Parkir L1→L2 → Jembatan → ${bestConnectorLabel} → Parkir L1`,
          };
        }
      }
    }

    // ── Direct: Parking L2 (-1) ↔ Hospital L2 (same floor, direct bridge) ────
    if ((startFloor === -1 && endFloor === 2) || (startFloor === 2 && endFloor === -1)) {
      const hospitalFloor = 2;
      const hospitalRoomId = startFloor === -1 ? endRoomIdParam   : startRoomIdParam;
      const parkingRoomId  = startFloor === -1 ? startRoomIdParam : endRoomIdParam;
      const isParkingStart = startFloor === -1;

      const hospitalDoc = getDoc(2);
      const parkDoc     = getDoc(-1);
      if (!hospitalDoc || !parkDoc) return null;

      const conn = PARKING2_CONN;

      // Inject the parking L2 QR anchor as a virtual node
      const parkAnchor = Object.values(QR_ANCHOR_REGISTRY).find(
        (a) => a.roomId === parkingRoomId && a.floor === -1
      );
      if (parkAnchor) injectVirtualAnchorNode(parkDoc, parkingRoomId, parkAnchor.svgX, parkAnchor.svgY);

      // Inject connector nodes into both SVGs
      injectVirtualAnchorNode(parkDoc,     conn.parkingNodeId,  conn.parkingX,  conn.parkingY);
      injectVirtualAnchorNode(hospitalDoc, conn.hospitalNodeId, conn.hospitalX, conn.hospitalY);

      let parkingSegment, hospitalSegment;

      if (isParkingStart) {
        // Parking L2 → Hospital L2: parkingRoom → connector, connector → hospitalRoom
        if (parkAnchor) {
          parkingSegment = buildRouteForRooms(
            parkingRoomId,
            conn.parkingNodeId,
            parkDoc,
            {
              startPoint: { x: parkAnchor.svgX, y: parkAnchor.svgY },
              useExactStartPoint: true,
              startNodeId: parkAnchor.routeNodeId,
            },
          );
        }
        if (!parkingSegment) {
          parkingSegment = buildRouteFromPoint(
            conn.parkingNodeId, conn.parkingX, conn.parkingY,
            parkingRoomId, parkDoc,
            "room_to_point",
          );
        }

        hospitalSegment = buildRouteFromPoint(
          conn.hospitalNodeId, conn.hospitalX, conn.hospitalY,
          hospitalRoomId, hospitalDoc,
          "point_to_room",
        );
      } else {
        // Hospital L2 → Parking L2: hospitalRoom → connector, connector → parkingRoom
        
        // If using exact start point (QR scan at hospital), route from QR to connector
        if (options?.useExactStartPoint && options?.startPoint) {
          hospitalSegment = buildRouteForRooms(
            hospitalRoomId,
            conn.hospitalNodeId,
            hospitalDoc,
            options,
          );
          if (!hospitalSegment) {
          const virtualStartNodeId = `virtual_qr_start_${hospitalRoomId}`;
          injectVirtualAnchorNode(hospitalDoc, virtualStartNodeId, options.startPoint.x, options.startPoint.y);
          
          // Route from virtual QR node (exact coordinates) to connector node (exact coordinates)
          hospitalSegment = buildRouteFromPoint(
            virtualStartNodeId,
            options.startPoint.x,
            options.startPoint.y,
            conn.hospitalNodeId,
            hospitalDoc,
            "point_to_point"
          );
          }
          if (!hospitalSegment) {
            // Fallback to normal routing
            hospitalSegment = buildRouteFromPoint(
              conn.hospitalNodeId, conn.hospitalX, conn.hospitalY,
              hospitalRoomId, hospitalDoc,
              "room_to_point",
            );
          }
        } else {
          hospitalSegment = buildRouteFromPoint(
            conn.hospitalNodeId, conn.hospitalX, conn.hospitalY,
            hospitalRoomId, hospitalDoc,
            "room_to_point",
          );
        }
        
        parkingSegment = buildRouteFromPoint(
          conn.parkingNodeId, conn.parkingX, conn.parkingY,
          parkingRoomId, parkDoc,
          "point_to_room",
        );
      }

      // Determine which segment to show based on current map view
      const activeSegment = (showParkingMap && parkingFloor === 2) ? parkingSegment : hospitalSegment;
      if (!activeSegment) return null;

      return {
        startRoomId: startRoomIdParam,
        endRoomId: endRoomIdParam,
        checkpointIds: [
          ...(isParkingStart ? parkingSegment?.checkpointIds ?? [] : hospitalSegment?.checkpointIds ?? []),
          "transition_parking_l2",
          ...(isParkingStart ? hospitalSegment?.checkpointIds ?? [] : parkingSegment?.checkpointIds ?? []),
        ],
        points: activeSegment.points,
        totalDistance: (parkingSegment?.totalDistance ?? 0) + (hospitalSegment?.totalDistance ?? 0),
        floorSegments: [
          ...(parkingSegment ? [{
            floor: -1 as const,
            checkpointIds: parkingSegment.checkpointIds,
            points: parkingSegment.points,
            totalDistance: parkingSegment.totalDistance,
          }] : []),
          ...(hospitalSegment ? [{
            floor: 2 as const,
            checkpointIds: hospitalSegment.checkpointIds,
            points: hospitalSegment.points,
            totalDistance: hospitalSegment.totalDistance,
          }] : []),
        ],
        floorsInvolved: [2],
        transitionLabel: "Jembatan Parkir Lantai 2",
      };
    }
    // ── Both hospital floors (1 ↔ 2) ─────────────────────────────────────────
    const startDoc = getDoc(startFloor as 1 | 2);
    const endDoc   = getDoc(endFloor   as 1 | 2);
    if (!startDoc || !endDoc) return null;

    let bestRoute: RoomRouteResult | null = null;

    multiFloorConnectors.forEach((connector) => {
      const startConnectorRoomId = connector.rooms[startFloor as 1 | 2];
      const endConnectorRoomId   = connector.rooms[endFloor   as 1 | 2];

      let startSegment: RoomRouteResult | null = null;

      if (options?.useExactStartPoint && options?.startPoint) {
        startSegment = buildRouteForRooms(startRoomIdParam, startConnectorRoomId, startDoc, options);
      } else {
        startSegment = buildRouteForRooms(startRoomIdParam, startConnectorRoomId, startDoc, { 
          startPoint: options?.startPoint,
          useExactStartPoint: options?.useExactStartPoint,
        });
      }

      const endSegment = buildRouteForRooms(endConnectorRoomId, endRoomIdParam, endDoc, { endPoint: options?.endPoint });
      
      if (!startSegment || !endSegment) return;
      
      // ✅ FIX: Validate endSegment actually routes to the correct destination
      if (endSegment.endRoomId !== endRoomIdParam) {
        console.warn(`⚠️ Connector ${connector.label}: endSegment routes to ${endSegment.endRoomId} instead of ${endRoomIdParam}, skipping`);
        return;
      }

      const candidateDistance = startSegment.totalDistance + endSegment.totalDistance;
      if (bestRoute && bestRoute.totalDistance <= candidateDistance) return;

      bestRoute = {
        startRoomId: startRoomIdParam,
        endRoomId: endRoomIdParam,
        checkpointIds: [...startSegment.checkpointIds, `transition_${connector.id}`, ...endSegment.checkpointIds],
        points: activeFloor === startFloor ? startSegment.points : endSegment.points,
        totalDistance: candidateDistance,
        floorSegments: [
          { floor: startFloor as 1 | 2, checkpointIds: startSegment.checkpointIds, points: startSegment.points, totalDistance: startSegment.totalDistance },
          { floor: endFloor   as 1 | 2, checkpointIds: endSegment.checkpointIds,   points: endSegment.points,   totalDistance: endSegment.totalDistance   },
        ],
        floorsInvolved: [startFloor as 1 | 2, endFloor as 1 | 2],
        transitionLabel: connector.label,
      };
    });

    return bestRoute;
  }, [activeFloor, showParkingMap, parkingFloor, parkingSvgDoc, parking2SvgDoc, getFloorSvgDoc, multiFloorConnectors, resolveFloorForRoomExtended, PARKING_CONN, PARKING2_CONN]);

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
    setIsDragging(true);
  }, []);

  // applyDragMove / applyDragEnd accept HOST-PAGE coordinates explicitly.
  // This is critical because SVG <object> iframe mouse events fire with clientX/Y
  // relative to the IFRAME viewport, not the host page — callers must convert first.
  useEffect(() => {
    const applyDragMove = (hostX: number, hostY: number, buttons: number) => {
      if (!isDraggingRef.current) return;

      if ((buttons & 1) === 0) {
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

  const handleTouchStart = useCallback(
    (e: React.TouchEvent) => {
      if (e.touches.length !== 1) return;
      const touch = e.touches[0];
      startDrag(touch.clientX, touch.clientY, "host");
    },
    [startDrag]
  );

  const handleTouchMove = useCallback((e: React.TouchEvent) => {
    if (e.touches.length !== 1) return;
    const touch = e.touches[0];
    e.preventDefault();
    applyDragMoveRef.current(touch.clientX, touch.clientY, 1);
  }, []);

  const handleTouchEnd = useCallback(() => {
    applyDragEndRef.current();
  }, []);

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

      const target = svgDoc.getElementById(roomId);
      if (!target) return null;

      // Use layout-independent center computation so this works on
      // pre-fetched / display:none SVG documents (critical for cross-floor
      // routing — see hospitalRouteGraph.ts::computeElementCenterWithoutLayout).
      return computeElementCenterWithoutLayout(target);
    },
    []
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
      const normalizedSource = source.replace(/_/g, " ").replace(/\s+/g, " ").trim();
      if (
        normalizedSource.includes("jalan") ||
        normalizedSource.includes("background") ||
        normalizedSource.includes("unamed") ||
        normalizedSource.includes("area kamar operasi") ||
        normalizedSource.includes("area gudang alat medis steril") ||
        normalizedSource.includes("area gudang alat meedis steril")
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
        floor: activeFloor, // ✅ FIX: Add floor property to match HospitalRoomInfo type
      };
    },
    [roomInfoBySvgId, activeFloor]
  );

  const resolveActiveQrAnchor = useCallback(
    (rawQr: string): QrAnchor | null => {
      const directOrFuzzyMatch = resolveQrAnchorFromRegistry(rawQr, QR_ANCHOR_REGISTRY);
      if (directOrFuzzyMatch) return directOrFuzzyMatch;

      const legacyRoomId = resolveRoomIdFromQrCode(rawQr);
      if (!legacyRoomId) return null;

      return allRegisteredQrAnchors.find((anchor) => anchor.roomId === legacyRoomId) || null;
    },
    [QR_ANCHOR_REGISTRY, allRegisteredQrAnchors]
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
        return match;
      }

      if (category === "facilities") {
        const match = ["Facility", "Service", "Administration"].includes(roomCategory);
        return match;
      }

      // departments — includes wards, treatment, outpatient, diagnostic, surgery, critical
      const match = ["Outpatient", "Critical Care", "Diagnostic", "Surgery", "Ward", "Treatment"].includes(roomCategory);
      return match;
    },
    []
  );

  const ensureDynamicHighlightStyle = useCallback((svgDoc: Document) => {
    if (svgDoc.getElementById("mapviewer-dynamic-highlight-style")) {
      return;
    }

    // IMPORTANT: <style> must be a direct child of <svg> root, NOT inside <defs>.
    // CSS inside <defs> is not applied by browsers to SVG elements.
    const svgRoot = svgDoc.querySelector("svg");
    if (!svgRoot) {
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

  const renderQrAnchorHints = useCallback((svgDoc: Document, anchors: QrAnchor[], activeQrId: string | null, showLabel = true) => {
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

      if (showLabel) {
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
      }

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

  const renderRouteOverlay = useCallback((svgDoc: Document, route: RoomRouteResult | null, floor: 1 | 2) => {
    const svgRoot = svgDoc.querySelector("svg");
    if (!svgRoot) return;

    const namespace = "http://www.w3.org/2000/svg";
    const oldLayer = svgDoc.getElementById("dynamic-route-layer");
    oldLayer?.remove();

    const routeSegment = getRouteSegmentForFloor(route, floor);
    if (!routeSegment || routeSegment.points.length < 2) return;

    ensureRouteStyle(svgDoc);

    const pointsAttr = routeSegment.points.map((point) => `${point.x},${point.y}`).join(" ");

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

    const startPoint = routeSegment.points[0];
    const endPoint = routeSegment.points[routeSegment.points.length - 1];

    const nextPoint = routeSegment.points.find((point, index) => {
      if (index === 0) return false;
      return Math.hypot(point.x - startPoint.x, point.y - startPoint.y) > 0.5;
    }) || routeSegment.points[1];

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
  }, [ensureRouteStyle, getRouteSegmentForFloor]);

  const renderDynamicRoomLabels = useCallback(
    (svgDoc: Document) => {
      const svgRoot = svgDoc.querySelector("svg");
      if (!svgRoot) return;

      // Parking map has its own built-in labels — skip all dynamic labels.
      if (showParkingMap) {
        svgDoc.getElementById("dynamic-room-label-layer")?.remove();
        return;
      }

      // ✅ FIX: Don't render labels if room data hasn't loaded yet
      if (!rooms || rooms.length === 0) {
        return;
      }

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

        if (activeFloor === 2) {
          const normalizedFloor2LabelSource = `${room.id} ${room.name} ${path.id}`
            .toLowerCase()
            .replace(/[_-]/g, " ")
            .replace(/\s+/g, " ")
            .trim();
          if (
            HIDDEN_DYNAMIC_LABEL_KEYWORDS_FLOOR_2.some((keyword) =>
              normalizedFloor2LabelSource.includes(keyword)
            )
          ) {
            return;
          }
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
    [showParkingMap, activeFloor, buildLabelLines, asSvgGraphicsElement, roomFromPath, rooms, roomInfoBySvgId]
  );

  const setupSvgRoomInteraction = useCallback(() => {
    const svgDoc = objectRef.current?.contentDocument;
    if (!svgDoc) return;
    const svgRoot = svgDoc.querySelector("svg");
    if (!svgRoot) return;

    ensureDynamicHighlightStyle(svgDoc);
    if (rooms && rooms.length > 0) {
      renderDynamicRoomLabels(svgDoc);
    }

    const cleanupHandlers: Array<() => void> = [];
    const roomPaths = Array.from(
      svgDoc.querySelectorAll("path")
    ) as SVGPathElement[];

    roomPaths.forEach((path) => {
      const room = roomFromPath(path);
      if (!room) return;

      // Skip click handler for R._Tunggu
      if (room.id === "R._Tunggu" || room.id === "Nurse_Station") {
        return;
      }

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

    const svgTouchStart = (e: TouchEvent) => {
      if (e.touches.length !== 1) return;
      const touch = e.touches[0];
      const { left, top } = getObjectOffset();
      startDrag(touch.clientX + left, touch.clientY + top, "svg");
    };

    const svgTouchMove = (e: TouchEvent) => {
      if (e.touches.length !== 1) return;
      const touch = e.touches[0];
      const { left, top } = getObjectOffset();
      e.preventDefault();
      applyDragMoveRef.current(touch.clientX + left, touch.clientY + top, 1);
    };

    const svgTouchEnd = () => applyDragEndRef.current();

    svgDoc.documentElement.addEventListener("mousedown", svgMouseDown);
    svgDoc.documentElement.addEventListener("mousemove", svgMouseMove);
    svgDoc.documentElement.addEventListener("mouseup",   svgMouseUp);
    svgDoc.documentElement.addEventListener("wheel",     svgWheel, { passive: false });
    svgDoc.documentElement.addEventListener("touchstart", svgTouchStart, { passive: true });
    svgDoc.documentElement.addEventListener("touchmove",  svgTouchMove, { passive: false });
    svgDoc.documentElement.addEventListener("touchend",   svgTouchEnd);
    svgDoc.documentElement.addEventListener("touchcancel", svgTouchEnd);
    cleanupHandlers.push(() => svgDoc.documentElement.removeEventListener("mousedown", svgMouseDown));
    cleanupHandlers.push(() => svgDoc.documentElement.removeEventListener("mousemove", svgMouseMove));
    cleanupHandlers.push(() => svgDoc.documentElement.removeEventListener("mouseup",   svgMouseUp));
    cleanupHandlers.push(() => svgDoc.documentElement.removeEventListener("wheel",     svgWheel));
    cleanupHandlers.push(() => svgDoc.documentElement.removeEventListener("touchstart", svgTouchStart));
    cleanupHandlers.push(() => svgDoc.documentElement.removeEventListener("touchmove",  svgTouchMove));
    cleanupHandlers.push(() => svgDoc.documentElement.removeEventListener("touchend",   svgTouchEnd));
    cleanupHandlers.push(() => svgDoc.documentElement.removeEventListener("touchcancel", svgTouchEnd));

    // Deselect room on background click (only if not a drag)
    const rootClickHandler = () => {
      if (hasDraggedRef.current) return;
      setActiveRoomInfo(null);
      setActiveRoomId(null);
    };
    svgDoc.documentElement.addEventListener("click", rootClickHandler);
    cleanupHandlers.push(() =>
      svgDoc.documentElement.removeEventListener("click", rootClickHandler)
    );

    return () => cleanupHandlers.forEach((fn) => fn());
  }, [roomFromPath, startDrag, handleWheel, renderDynamicRoomLabels, ensureDynamicHighlightStyle, rooms]);

  useEffect(() => {
    const objectElement = objectRef.current;
    if (!objectElement) return;

    let cleanup: (() => void) | undefined;
    let retryTimer: number | undefined;

    const onLoad = () => {
      if (retryTimer !== undefined) {
        window.clearTimeout(retryTimer);
        retryTimer = undefined;
      }
      cleanup?.();
      cleanup = setupSvgRoomInteraction();
      if (!cleanup) {
        retryTimer = window.setTimeout(() => {
          setSvgLoadTick((prev) => prev + 1);
        }, 50);
        return;
      }

      setRoutingRoomIds(getRoutingRoomIds());
      setSvgReadyVersion((prev) => prev + 1);
    };

    if (objectElement.contentDocument) onLoad();

    objectElement.addEventListener("load", onLoad);
    return () => {
      if (retryTimer !== undefined) {
        window.clearTimeout(retryTimer);
      }
      objectElement.removeEventListener("load", onLoad);
      cleanup?.();
    };
  }, [activeMapSvgPath, setupSvgRoomInteraction, rooms, svgLoadTick]);

  // Re-setup SVG interaction when rooms data is loaded
  useEffect(() => {
    // ✅ FIX: Wait for both rooms data and SVG document to be ready
    if (!rooms || rooms.length === 0) return;
    if (!objectRef.current?.contentDocument) return;
    
    // Re-run setupSvgRoomInteraction to render labels with loaded room data
    const cleanup = setupSvgRoomInteraction();
    
    // ✅ FIX: Update routing room IDs and trigger SVG ready version update
    setRoutingRoomIds(getRoutingRoomIds());
    setSvgReadyVersion((prev) => prev + 1);
    
    return cleanup;
  }, [rooms, setupSvgRoomInteraction]);

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
      
      const svgDoc = objectRef.current?.contentDocument;
      const container = containerRef.current;
      
      
      if (!svgDoc || !container) {
        return;
      }

      const target = asSvgGraphicsElement(svgDoc.getElementById(elementId));
      
      if (!target) {
        return;
      }

      const svgRoot = svgDoc.querySelector("svg");
      if (!svgRoot) {
        return;
      }

      const viewBox = svgRoot.viewBox.baseVal;
      
      if (!viewBox.width || !viewBox.height) {
        return;
      }

      const objectRect = objectRef.current!.getBoundingClientRect();
      const containerRect = container.getBoundingClientRect();
      

      // Get the SVG-space bbox of the target element
      const bbox = target.getBBox();
      const svgCenterX = bbox.x + bbox.width / 2;
      const svgCenterY = bbox.y + bbox.height / 2;
      

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


      // Position of the <object>'s top-left corner in unscaled map-space.
      // screenLeft = containerLeft + currentPos.x + mapLeft * currentScale  →  mapLeft = (screenLeft - containerLeft - currentPos.x) / currentScale
      const objOffsetInMapX = (objectRect.left - containerRect.left - currentPos.x) / currentScale;
      const objOffsetInMapY = (objectRect.top  - containerRect.top  - currentPos.y) / currentScale;

      // Both terms are now in unscaled map-space — safe to add
      const elementInMapX = objOffsetInMapX + objCenterX;
      const elementInMapY = objOffsetInMapY + objCenterY;


      // Desired zoom level
      const targetScale = 2.0;

      // Place the element centre exactly at the container centre
      const containerCenterX = containerRect.width / 2;
      const containerCenterY = containerRect.height / 2;

      const newPos = {
        x: containerCenterX - elementInMapX * targetScale,
        y: containerCenterY - elementInMapY * targetScale,
      };
      

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

  const zoomToPoint = useCallback(
    (svgX: number, svgY: number) => {
      const svgDoc = objectRef.current?.contentDocument;
      const container = containerRef.current;
      
      if (!svgDoc || !container) {
        return;
      }

      const svgRoot = svgDoc.querySelector("svg");
      if (!svgRoot) {
        return;
      }

      const viewBox = svgRoot.viewBox.baseVal;
      
      if (!viewBox.width || !viewBox.height) {
        return;
      }

      const objectRect = objectRef.current!.getBoundingClientRect();
      const containerRect = container.getBoundingClientRect();

      const currentScale = scaleRef.current;
      const currentPos   = positionRef.current;

      const objNaturalWidth = objectRect.width / currentScale;
      const scaleSvgToObjNatural = objNaturalWidth / viewBox.width;

      // Offset of point within the <object> — in unscaled pixels
      const objCenterX = (svgX - viewBox.x) * scaleSvgToObjNatural;
      const objCenterY = (svgY - viewBox.y) * scaleSvgToObjNatural;

      const objOffsetInMapX = (objectRect.left - containerRect.left - currentPos.x) / currentScale;
      const objOffsetInMapY = (objectRect.top  - containerRect.top  - currentPos.y) / currentScale;

      const pointInMapX = objOffsetInMapX + objCenterX;
      const pointInMapY = objOffsetInMapY + objCenterY;

      // Desired zoom level
      const targetScale = 2.0;

      const containerCenterX = containerRect.width / 2;
      const containerCenterY = containerRect.height / 2;

      const newPos = {
        x: containerCenterX - pointInMapX * targetScale,
        y: containerCenterY - pointInMapY * targetScale,
      };

      // Smooth zoom with CSS transition
      if (mapRef.current) {
        mapRef.current.style.transition = 'transform 0.45s cubic-bezier(0.25, 0.46, 0.45, 0.94)';
      }

      positionRef.current = newPos;
      setScale(targetScale);
      setPosition(newPos);

      // Reset transition after animation completes
      setTimeout(() => {
        if (mapRef.current) {
          mapRef.current.style.transition = 'transform 0.15s cubic-bezier(0.2, 0, 0, 1)';
        }
      }, 500);
    },
    []
  );

  // ---------------------------------------------------------------------------
  // Sync selectedLocation → activeRoomInfo (from search) + zoom to element
  // ---------------------------------------------------------------------------

  useEffect(() => {
    if (!selectedLocation) {
      pendingSearchZoomRoomIdRef.current = null;
      return;
    }

    setActiveRoomId(selectedLocation.id);
    setActiveRoomInfo(selectedLocation);
    pendingSearchZoomRoomIdRef.current = selectedLocation.id;

    const targetFloor = resolveFloorForRoom(selectedLocation);
    if (targetFloor !== activeFloor) {
      setActiveFloor(targetFloor);
      return;
    }

    const svgDoc = objectRef.current?.contentDocument;
    const targetElement = svgDoc?.getElementById(selectedLocation.id);
    if (!targetElement) return;

    // Delay zoom slightly to ensure SVG highlight renders first.
    const timer = setTimeout(() => {
      if (pendingSearchZoomRoomIdRef.current !== selectedLocation.id) return;
      zoomToSvgElement(selectedLocation.id);
      pendingSearchZoomRoomIdRef.current = null;
    }, 100);

    return () => {
      clearTimeout(timer);
    };
  }, [selectedLocation, activeFloor, svgReadyVersion, zoomToSvgElement, resolveFloorForRoom]);

  // ---------------------------------------------------------------------------
  // Highlight all rooms by selected sidebar category
  useEffect(() => {

    if (!objectRef.current) {
      return;
    }
    const svgDoc = objectRef.current.contentDocument;
    if (!svgDoc) {
      return;
    }

    // Make sure the highlight CSS is in the SVG document
    ensureDynamicHighlightStyle(svgDoc);

    // Clear previous category highlights
    const prevHighlighted = svgDoc.querySelectorAll(".region-category-active");
    prevHighlighted.forEach((el) => el.classList.remove("region-category-active"));

    if (!highlightCategory) {
      return;
    }

    const roomPaths = Array.from(svgDoc.querySelectorAll("path")) as SVGPathElement[];

    let matchCount = 0;
    roomPaths.forEach((path) => {
      const room = roomFromPath(path);
      if (!room) return;
      if (roomMatchesHighlightCategory(room, highlightCategory)) {
        path.classList.add("region-category-active");
        matchCount++;
      }
    });

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

    const validRoutingRoomIds = new Set(routingRoomOptions.map((room) => room.id));

    const anchor = resolveActiveQrAnchor(qrCodeInput);
    if (anchor) {
      gpsBufferRef.current = [];
      
      // For Parking L1, override the liveSvgPoint to use the checkpoint instead of QR anchor
      let effectiveSvgPoint = { x: anchor.svgX, y: anchor.svgY };
      if (anchor.floor === 0 && anchor.roomId === "Parking_Lantai_1") {
        // Use Check_Point_Tangga_Pengunjung as the start point for Parking L1
        effectiveSvgPoint = { x: 1293.9375, y: 644.75 };
      }
      
      liveSvgPointRef.current = effectiveSvgPoint;
      setLiveSvgPoint(effectiveSvgPoint);
      setShowCurrentUserMarker(true);
      setStartRoomId(anchor.roomId);
      startRoomIdRef.current = anchor.roomId;
      preferRoomCenterStartRef.current = false;
      setLastQrAnchor(anchor);
      setQrCalibrationHistory((prev) => [...prev, anchor]);
      setLocationInputMode("qr");

      if ((anchor.floor === 1 || anchor.floor === 2) && anchor.floor !== activeFloor) {
        setActiveFloor(anchor.floor);
      }
      if (anchor.floor === 0) {
        setShowParkingMap(true);
        setParkingFloor(1);
      } else if (anchor.floor === -1) {
        setShowParkingMap(true);
        setParkingFloor(2);
      }

      const routeAfterCalibration =
        endRoomIdRef.current && endRoomIdRef.current !== anchor.roomId
          ? buildDebugRouteForRooms(anchor.roomId, endRoomIdRef.current, {
              startPoint: effectiveSvgPoint,
              useExactStartPoint: true,
              startNodeId: anchor.routeNodeId,
            })
          : null;

      if (routeAfterCalibration) {
        // Auto-switch floor to match route start
        const routeStartFloor = routeAfterCalibration.floorsInvolved?.[0];
        if (routeStartFloor !== undefined) {
          if (routeStartFloor === 0) {
            setShowParkingMap(true);
            setParkingFloor(1);
          } else if (routeStartFloor === -1) {
            setShowParkingMap(true);
            setParkingFloor(2);
          } else if (routeStartFloor === 1 || routeStartFloor === 2) {
            setShowParkingMap(false);
            setActiveFloor(routeStartFloor);
          }
        }
        setActiveRoute(routeAfterCalibration);
      } else {
        setActiveRoute(null);
      }

      setRouteDebugMessage(`✅ Posisi dikalibrasi: ${anchor.label}`);
      setLiveModeStatus(`✅ Posisi dikalibrasi: ${anchor.label}`);
      
      // Zoom to calibrated start point
      setTimeout(() => {
        zoomToPoint(anchor.svgX, anchor.svgY);
      }, 100);
      
      return;
    }

    const resolvedRoomId = resolveRoomIdFromQrCode(qrCodeInput);
    if (resolvedRoomId) {
      if (!validRoutingRoomIds.has(resolvedRoomId)) {
        setRouteDebugMessage("QR ruangan ini belum terdaftar sebagai titik routing.");
        return;
      }

      const resolvedRoomInfo = roomInfoBySvgId[resolvedRoomId];
      const targetFloor = resolvedRoomInfo ? resolveFloorForRoom(resolvedRoomInfo) : activeFloor;

      gpsBufferRef.current = [];
      liveSvgPointRef.current = null;
      setLiveSvgPoint(null);
      setShowCurrentUserMarker(true);
      setStartRoomId(resolvedRoomId);
      startRoomIdRef.current = resolvedRoomId;
      preferRoomCenterStartRef.current = true;
      setLastQrAnchor(null);
      setLocationInputMode("qr");

      if (targetFloor !== activeFloor) {
        setActiveFloor(targetFloor);
      }

      const roomCenter = getRoomCenterById(getFloorSvgDoc(targetFloor) || svgDoc, resolvedRoomId);
      const routeAfterRoomScan =
        endRoomIdRef.current && endRoomIdRef.current !== resolvedRoomId
          ? buildDebugRouteForRooms(resolvedRoomId, endRoomIdRef.current, {
              startPoint: roomCenter ?? undefined,
            })
          : null;

      if (routeAfterRoomScan) {
        setActiveRoute(routeAfterRoomScan);
      } else {
        setActiveRoute(null);
      }

      setRouteDebugMessage(`✅ Start point dari QR ruangan: ${roomInfoBySvgId[resolvedRoomId]?.name || resolvedRoomId}`);
      
      // Zoom to start point
      if (roomCenter) {
        setTimeout(() => {
          zoomToPoint(roomCenter.x, roomCenter.y);
        }, 100);
      }
      
      return;
    }
    setRouteDebugMessage("QR tidak dikenali pada registry multi-floor.");
  }, [qrCodeInput, getRoomCenterById, resolveActiveQrAnchor, routingRoomOptions, activeFloor, resolveFloorForRoom, getFloorSvgDoc, buildDebugRouteForRooms, zoomToPoint]);

  const handleCalibrateQrOnly = useCallback((overrideQrCode?: string) => {
    // Fungsi khusus untuk kalibrasi QR - hanya update start point, keep end point
    // overrideQrCode: pass the QR string directly to avoid reading stale qrCodeInput state
    const svgDoc = objectRef.current?.contentDocument;
    if (!svgDoc) {
      setRouteDebugMessage("SVG belum siap. Coba lagi beberapa detik.");
      return;
    }

    if (!endRoomIdRef.current) {
      setRouteDebugMessage("Belum ada tujuan yang dipilih. Buat navigasi terlebih dahulu.");
      return;
    }

    const anchor = resolveActiveQrAnchor(overrideQrCode ?? qrCodeInput);
    if (anchor) {
      gpsBufferRef.current = [];
      
      // For Parking L1, override the liveSvgPoint to use the checkpoint instead of QR anchor
      let effectiveSvgPoint = { x: anchor.svgX, y: anchor.svgY };
      if (anchor.floor === 0 && anchor.roomId === "Parking_Lantai_1") {
        // Use Check_Point_Tangga_Pengunjung as the start point for Parking L1
        effectiveSvgPoint = { x: 1293.9375, y: 644.75 };
      }
      
      liveSvgPointRef.current = effectiveSvgPoint;
      setLiveSvgPoint(effectiveSvgPoint);
      setShowCurrentUserMarker(true);
      setStartRoomId(anchor.roomId);
      startRoomIdRef.current = anchor.roomId;
      preferRoomCenterStartRef.current = false;
      setLastQrAnchor(anchor);
      setQrCalibrationHistory((prev) => [...prev, anchor]);

      if ((anchor.floor === 1 || anchor.floor === 2) && anchor.floor !== activeFloor) {
        setActiveFloor(anchor.floor);
      }
      if (anchor.floor === 0) {
        setShowParkingMap(true);
        setParkingFloor(1);
      } else if (anchor.floor === -1) {
        setShowParkingMap(true);
        setParkingFloor(2);
      }

      // Build route with existing destination
      // Use effectiveSvgPoint (already overridden to checkpoint for Parking L1) so
      // the route start matches exactly where the user arrow is displayed.
      // ✅ FIX: Store the destination BEFORE building route to ensure it doesn't change
      const destinationRoomId = endRoomIdRef.current;
      const destinationRoomName = roomInfoBySvgId[destinationRoomId]?.name || destinationRoomId;
      
      const routeAfterCalibration = buildDebugRouteForRooms(anchor.roomId, destinationRoomId, {
        startPoint: effectiveSvgPoint,
        useExactStartPoint: true,
        startNodeId: anchor.routeNodeId,
      });

      if (routeAfterCalibration) {
        const routeStartFloor = routeAfterCalibration.floorsInvolved?.[0];
        if (routeStartFloor !== undefined) {
          if (routeStartFloor === 0) {
            setShowParkingMap(true);
            setParkingFloor(1);
          } else if (routeStartFloor === -1) {
            setShowParkingMap(true);
            setParkingFloor(2);
          } else if (routeStartFloor === 1 || routeStartFloor === 2) {
            setShowParkingMap(false);
            setActiveFloor(routeStartFloor);
          }
        }
        setActiveRoute(routeAfterCalibration);
        setRouteDebugMessage(`✅ Posisi dikalibrasi: ${anchor.label} → ${destinationRoomName}`);
      } else {
        setActiveRoute(null);
        setRouteDebugMessage(`❌ Tidak dapat membuat route dari ${anchor.label} ke ${destinationRoomName}`);
      }
      
      // Zoom to calibrated start point (use effectiveSvgPoint so we zoom to the
      // same location where the user arrow appears, not the raw QR anchor).
      setTimeout(() => {
        zoomToPoint(effectiveSvgPoint.x, effectiveSvgPoint.y);
      }, 100);
      
      return;
    }

    setRouteDebugMessage("QR tidak dikenali. Scan QR code yang valid.");
  }, [qrCodeInput, resolveActiveQrAnchor, activeFloor, buildDebugRouteForRooms, zoomToPoint]);

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

    const roomCenter = getRoomCenterById(svgDoc, startRoomIdRef.current);
    const calibratedStartPoint = liveSvgPointRef.current;
    const startPoint = calibratedStartPoint ?? roomCenter;
    if (!startPoint) {
      setLiveModeStatus("Titik start belum valid di SVG.");
      return;
    }

    if (geoWatchIdRef.current !== null) {
      navigator.geolocation.clearWatch(geoWatchIdRef.current);
      geoWatchIdRef.current = null;
    }

    liveOriginRef.current = null;
    gpsBufferRef.current = [];
    liveSvgPointRef.current = startPoint;
    preferRoomCenterStartRef.current = false;
    setLiveSvgPoint(startPoint);
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
            svgX: startPoint.x,
            svgY: startPoint.y,
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

        const dynamicRoute = buildDebugRouteForRooms(nearest.roomId, currentEnd, {
          startPoint: smoothedPoint,
        });
        if (dynamicRoute) {
          // Debug: Log route in live mode (already throttled by LIVE_REROUTE_INTERVAL_MS, so always log)
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
  }, [getRoomCenterById, getNearestRoutingRoom, stopLiveMode, buildDebugRouteForRooms]);

  useEffect(() => {
    if (!navigationStartRequest) return;

    const { requestId, roomId, destinationRoomId, source, qrPayload } = navigationStartRequest;
    const finish = () => onNavigationStartRequestHandled?.(requestId);

    // Mode "calibrate" - hanya update start point, keep end point
    if (source === "calibrate") {
      if (!qrPayload) {
        setRouteDebugMessage("QR payload tidak ditemukan untuk kalibrasi.");
        finish();
        return;
      }

      setQrCodeInput(qrPayload);
      // Pass qrPayload directly to avoid reading stale qrCodeInput state
      // (setQrCodeInput is async and won't update before handleCalibrateQrOnly reads it)
      handleCalibrateQrOnly(qrPayload);
      finish();
      return;
    }

    // Mode "manual" atau "qr" - set start + end point (navigasi baru)
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
    setEndRoomId(destinationRoomId);
    endRoomIdRef.current = destinationRoomId;
    setShowCurrentUserMarker(true);

    const requestedRoomInfo = roomInfoBySvgId[roomId];
    const requestedFloor = requestedRoomInfo ? resolveFloorForRoom(requestedRoomInfo) : activeFloor;
    if (requestedFloor !== activeFloor) {
      setActiveFloor(requestedFloor);
    }

    const svgDoc = getFloorSvgDoc(requestedFloor);
    if (!svgDoc) {
      setRouteDebugMessage(`Start point diset ke ${roomInfoBySvgId[roomId]?.name || roomId}.`);
      finish();
      return;
    }

    if (source === "qr" && qrPayload) {
      setQrCodeInput(qrPayload);
      const anchor = resolveActiveQrAnchor(qrPayload);
      if (anchor) {
        if ((anchor.floor === 1 || anchor.floor === 2) && anchor.floor !== activeFloor) {
          setActiveFloor(anchor.floor);
        }
        setStartRoomId(anchor.roomId);
        startRoomIdRef.current = anchor.roomId;
        preferRoomCenterStartRef.current = false;
        setLastQrAnchor(anchor);
        
        // For Parking L1, override the liveSvgPoint to use the checkpoint instead of QR anchor
        let effectiveSvgPoint = { x: anchor.svgX, y: anchor.svgY };
        if (anchor.floor === 0 && anchor.roomId === "Parking_Lantai_1") {
          // Use Check_Point_Tangga_Pengunjung as the start point for Parking L1
          effectiveSvgPoint = { x: 1293.9375, y: 644.75 };
        }
        
        liveSvgPointRef.current = effectiveSvgPoint;
        setLiveSvgPoint(effectiveSvgPoint);
        setQrCalibrationHistory((prev) => [...prev, anchor]);

        const targetEndFromAnchor = destinationRoomId;
        if (targetEndFromAnchor && targetEndFromAnchor !== anchor.roomId) {
          const anchorRoute = buildDebugRouteForRooms(anchor.roomId, targetEndFromAnchor, {
            startPoint: effectiveSvgPoint,
            useExactStartPoint: true,
            startNodeId: anchor.routeNodeId,
          });
          if (anchorRoute) {
            setActiveRoute(anchorRoute);
            setRouteDebugMessage(
              `Rute otomatis (QR): ${roomInfoBySvgId[anchor.roomId]?.name || anchor.roomId} → ${roomInfoBySvgId[targetEndFromAnchor]?.name || targetEndFromAnchor}`
            );
            zoomToSvgElement(targetEndFromAnchor);
            finish();
            return;
          }
        }
      } else {
        const validRoutingRoomIds = new Set(routingRoomOptions.map((room) => room.id));
        const resolvedRoomByQr = resolveRoomIdFromQrCode(qrPayload);
        if (resolvedRoomByQr && validRoutingRoomIds.has(resolvedRoomByQr)) {
          setStartRoomId(resolvedRoomByQr);
          startRoomIdRef.current = resolvedRoomByQr;
          preferRoomCenterStartRef.current = true;
        }
      }
    }

    const targetEnd = destinationRoomId;
    if (!targetEnd || targetEnd === roomId) {
      setActiveRoute(null);
      setRouteDebugMessage(`Start point diset ke ${roomInfoBySvgId[roomId]?.name || roomId}. Pilih tujuan berbeda.`);
      finish();
      return;
    }

    const startCenter = getRoomCenterById(getFloorSvgDoc(requestedFloor) || svgDoc, roomId);
    const immediateRoute = buildDebugRouteForRooms(roomId, targetEnd, {
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
    routingRoomOptions,
    resolveActiveQrAnchor,
    resolveFloorForRoom,
    activeFloor,
    buildDebugRouteForRooms,
    getFloorSvgDoc,
    handleCalibrateQrOnly,
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

    // Check if parking is involved in the route
    const startFloorExtended = resolveFloorForRoomExtended(effectiveStartRoomId);
    const endFloorExtended = resolveFloorForRoomExtended(effectiveEndRoomId);
    const isParkingInvolved = startFloorExtended === 0 || startFloorExtended === -1 || endFloorExtended === 0 || endFloorExtended === -1;

    const startRoomInfo = roomInfoBySvgId[effectiveStartRoomId];
    const startFloor = startRoomInfo ? resolveFloorForRoom(startRoomInfo) : activeFloor;
    const targetStartDoc = (startFloorExtended === 0 || startFloorExtended === -1) 
      ? (startFloorExtended === 0 ? parkingSvgDoc : parking2SvgDoc)
      : getFloorSvgDoc(startFloor);
    if (!targetStartDoc && startFloorExtended !== 0 && startFloorExtended !== -1) {
      setRouteDebugMessage("Data SVG multi-floor belum siap. Coba beberapa detik lagi.");
      return;
    }

    const roomCenterStart = preferRoomCenterStartRef.current && startFloorExtended !== 0 && startFloorExtended !== -1
      ? getRoomCenterById(targetStartDoc!, effectiveStartRoomId)
      : null;

    if (preferRoomCenterStartRef.current && !roomCenterStart && startFloorExtended !== 0 && startFloorExtended !== -1) {
      setRouteDebugMessage("Start ruangan tidak valid di SVG. Scan ulang atau pilih start dari dropdown.");
      setActiveRoute(null);
      return;
    }

    // Determine start point: use QR anchor coordinates if available, otherwise use room center or liveSvgPoint
    let startPoint: { x: number; y: number } | undefined = undefined;
    const exactQrStartNodeId =
      !preferRoomCenterStartRef.current &&
      lastQrAnchor?.roomId === effectiveStartRoomId
        ? lastQrAnchor.routeNodeId
        : undefined;
    
    // When routing FROM parking (start is parking), use exact QR coordinates
    if (startFloorExtended === 0 || startFloorExtended === -1) {
      // Start is parking - use liveSvgPoint which contains the exact QR coordinates that were scanned
      if (liveSvgPointRef.current) {
        startPoint = liveSvgPointRef.current;
      } else {
        // Fallback: try to get QR anchor coordinates from registry
        const startAnchor = Object.values(QR_ANCHOR_REGISTRY).find(
          (a) => a.roomId === effectiveStartRoomId && a.floor === startFloorExtended
        );
        if (startAnchor) {
          startPoint = { x: startAnchor.svgX, y: startAnchor.svgY };
        }
      }
    } else if (endFloorExtended === 0 || endFloorExtended === -1) {
      // Destination is parking - use liveSvgPoint which contains the exact QR coordinates that were scanned
      if (liveSvgPointRef.current) {
        startPoint = liveSvgPointRef.current;
      } else {
        // Fallback: try to get QR anchor coordinates from registry
        const startAnchor = Object.values(QR_ANCHOR_REGISTRY).find(
          (a) => a.roomId === effectiveStartRoomId && a.floor === startFloorExtended
        );
        if (startAnchor) {
          startPoint = { x: startAnchor.svgX, y: startAnchor.svgY };
        }
      }
    } else if (!preferRoomCenterStartRef.current && liveSvgPointRef.current) {
      // User scanned QR code - use the exact QR anchor coordinates
      startPoint = liveSvgPointRef.current;
    } else if (preferRoomCenterStartRef.current && roomCenterStart) {
      // User selected from dropdown - use room center
      startPoint = roomCenterStart;
    } else {
      // Fallback: try to get QR anchor coordinates from registry
      const startAnchor = Object.values(QR_ANCHOR_REGISTRY).find(
        (a) => a.roomId === effectiveStartRoomId && a.floor === startFloorExtended
      );
      if (startAnchor) {
        startPoint = { x: startAnchor.svgX, y: startAnchor.svgY };
      }
    }
    
    // When routing FROM/TO parking, ALWAYS use exact start point (QR anchor coordinates)
    // Otherwise, use the normal logic based on preferRoomCenterStartRef
    const useExactStartPoint = (startFloorExtended === 0 || startFloorExtended === -1 || endFloorExtended === 0 || endFloorExtended === -1)
      ? true  // Always use exact point when start OR destination is parking
      : !preferRoomCenterStartRef.current && Boolean(startPoint);

    const result = buildDebugRouteForRooms(effectiveStartRoomId, effectiveEndRoomId, {
      startPoint,
      useExactStartPoint,
      startNodeId: exactQrStartNodeId,
    });
    if (!result) {
      setRouteDebugMessage("Rute tidak ditemukan pada jalur 'jalan' di denah.");
      setActiveRoute(null);
      lastLoggedRouteRef.current = null;
      return;
    }

    const endRoomInfo = roomInfoBySvgId[effectiveEndRoomId];
    const endFloor = endRoomInfo ? resolveFloorForRoom(endRoomInfo) : activeFloor;

    setActiveRoute(result);
    
    // Auto-switch map view to the START point floor so the user sees their
    // position (user arrow) immediately after starting navigation.
    if (startFloorExtended === 0) {
      // Start is Parking L1
      setShowParkingMap(true);
      setParkingFloor(1);
    } else if (startFloorExtended === -1) {
      // Start is Parking L2
      setShowParkingMap(true);
      setParkingFloor(2);
    } else if (startFloorExtended === 1 || startFloorExtended === 2) {
      // Start is hospital floor 1 or 2
      setShowParkingMap(false);
      if (startFloorExtended !== activeFloor) {
        setActiveFloor(startFloorExtended);
      }
    }
    
    setRouteDebugMessage(
      `Rute ditemukan: ${roomInfoBySvgId[effectiveStartRoomId]?.name || effectiveStartRoomId} → ${roomInfoBySvgId[effectiveEndRoomId]?.name || effectiveEndRoomId}`
    );

    if (endFloor === startFloor && !isParkingInvolved) {
      zoomToSvgElement(effectiveEndRoomId);
    }
  }, [startRoomId, endRoomId, zoomToSvgElement, getRoomCenterById, resolveFloorForRoom, activeFloor, getFloorSvgDoc, buildDebugRouteForRooms, resolveFloorForRoomExtended, parkingSvgDoc, parking2SvgDoc, lastQrAnchor]);

  const handleClearRoute = useCallback(() => {
    setActiveRoute(null);
    setRouteDebugMessage("Rute dibersihkan.");
    setNavSteps([]);
    setActiveStepIndex(0);
  }, []);

  const buildParkingL1HospitalEntrySteps = useCallback((points: Array<{ x: number; y: number }>): NavigationStep[] => {
    const pointDistance = (
      from: { x: number; y: number },
      to: { x: number; y: number },
    ) => Math.hypot(from.x - to.x, from.y - to.y);
    const isMainEntryCorridorStep = (step: NavigationStep) =>
      Math.abs(step.pivotPoint.x - 465.359) <= 80 &&
      Math.abs(step.toPoint.x - 465.359) <= 80 &&
      step.distanceToNext >= 120;
    const relabelCorridorSteps = (steps: NavigationStep[]) =>
      steps.map((step, index) =>
        index > 0 && step.type === "straight" && isMainEntryCorridorStep(step)
          ? { ...step, label: "Lanjut mengikuti koridor utama" }
          : step
      );
    const fallbackSteps = buildNavigationSteps(points);
    if (points.length < 3) return relabelCorridorSteps(fallbackSteps);

    const hospitalEntryPoint = { x: 465.359, y: 255.37 }; // QR-F1-N07: masuk koridor gedung lantai 1
    let entryPointIndex = 0;
    let nearestDistance = Number.POSITIVE_INFINITY;

    points.forEach((point, index) => {
      const d = pointDistance(point, hospitalEntryPoint);
      if (d < nearestDistance) {
        nearestDistance = d;
        entryPointIndex = index;
      }
    });

    if (nearestDistance > 90 || entryPointIndex <= 0 || entryPointIndex >= points.length - 1) {
      return relabelCorridorSteps(fallbackSteps);
    }

    const distanceToEntry = points
      .slice(0, entryPointIndex)
      .reduce((total, point, index) => total + pointDistance(point, points[index + 1]), 0);
    const firstPoint = points[0];
    const entryPoint = points[entryPointIndex];
    const remainingSteps = buildNavigationSteps(points.slice(entryPointIndex)).map((step) => ({
      ...step,
      label: step.type === "straight" && isMainEntryCorridorStep(step)
        ? "Lanjut mengikuti koridor utama"
        : step.label,
    }));

    const firstStep: NavigationStep = {
      index: 0,
      type: "straight",
      fromPoint: firstPoint,
      pivotPoint: firstPoint,
      toPoint: entryPoint,
      distanceToNext: distanceToEntry,
      cumulativeDistance: distanceToEntry,
      angleChange: 0,
      label: "Ikuti jalur utama menuju gedung rumah sakit",
      nextQrHint: "Cari QR di titik masuk gedung untuk kalibrasi posisi.",
    };

    return [firstStep, ...remainingSteps].map((step, index) => ({
      ...step,
      index,
    }));
  }, []);

  const buildHospitalParkingL1ExitSteps = useCallback((points: Array<{ x: number; y: number }>): NavigationStep[] => {
    const pointDistance = (
      from: { x: number; y: number },
      to: { x: number; y: number },
    ) => Math.hypot(from.x - to.x, from.y - to.y);
    const fallbackSteps = buildNavigationSteps(points);
    const isMainEntryCorridorStep = (step: NavigationStep) =>
      Math.abs(step.pivotPoint.x - 465.359) <= 80 &&
      Math.abs(step.toPoint.x - 465.359) <= 80 &&
      step.distanceToNext >= 120;
    if (points.length < 3) return fallbackSteps;

    const hospitalExitPoint = { x: 465.359, y: 255.37 }; // QR-F1-N07: koridor sebelum akses keluar gedung
    let exitPointIndex = 0;
    let nearestDistance = Number.POSITIVE_INFINITY;

    points.forEach((point, index) => {
      const d = pointDistance(point, hospitalExitPoint);
      if (d < nearestDistance) {
        nearestDistance = d;
        exitPointIndex = index;
      }
    });

    if (nearestDistance > 90 || exitPointIndex <= 0 || exitPointIndex >= points.length - 1) {
      return fallbackSteps.map((step) =>
        step.type === "straight" && step.label === "Jalan lurus" && isMainEntryCorridorStep(step)
          ? { ...step, label: "Lanjut mengikuti koridor utama" }
          : step
      );
    }

    const beforeExitSteps = buildNavigationSteps(points.slice(0, exitPointIndex + 1)).map((step) => ({
      ...step,
      label: step.type === "straight" && isMainEntryCorridorStep(step)
        ? "Lanjut mengikuti koridor utama"
        : step.label,
    }));
    const distanceToParkingAccess = points
      .slice(exitPointIndex, -1)
      .reduce((total, point, index) => total + pointDistance(point, points[exitPointIndex + index + 1]), 0);
    const exitPoint = points[exitPointIndex];
    const finalPoint = points[points.length - 1];
    const accessStep: NavigationStep = {
      index: beforeExitSteps.length,
      type: "straight",
      fromPoint: exitPoint,
      pivotPoint: exitPoint,
      toPoint: finalPoint,
      distanceToNext: distanceToParkingAccess,
      cumulativeDistance:
        (beforeExitSteps[beforeExitSteps.length - 1]?.cumulativeDistance ?? 0) + distanceToParkingAccess,
      angleChange: 0,
      label: "Keluar mengikuti jalur utama menuju lahan parkir",
      nextQrHint: "Cari QR di area parkir untuk kalibrasi posisi.",
    };
    const arriveStep: NavigationStep = {
      index: beforeExitSteps.length + 1,
      type: "arrive",
      fromPoint: exitPoint,
      pivotPoint: finalPoint,
      toPoint: finalPoint,
      distanceToNext: 0,
      cumulativeDistance: accessStep.cumulativeDistance,
      angleChange: 0,
      label: "Anda telah tiba di tujuan",
    };

    return [...beforeExitSteps, accessStep, arriveStep].map((step, index) => ({
      ...step,
      index,
    }));
  }, []);

  const improvePostConnectorSteps = useCallback((steps: NavigationStep[], transitionLabel?: string): NavigationStep[] => {
    if (steps.length < 2) return steps;
    // Fix A: cek jarak spasial — belokan hanya diberi konteks jika pivotPoint-nya
    // berada dalam radius 120px dari titik awal route (tepat di area exit lift/tangga).
    // Belokan yang lebih jauh dianggap belokan normal, bukan belokan keluar lift/tangga.
    const routeOrigin = steps[0].fromPoint;
    const firstTurnIndex = steps.findIndex(
      (step) =>
        step.type !== "straight" &&
        step.type !== "arrive" &&
        Math.hypot(step.pivotPoint.x - routeOrigin.x, step.pivotPoint.y - routeOrigin.y) <= 120,
    );
    if (firstTurnIndex < 0) return steps;

    const normalizedTransition = transitionLabel?.toLowerCase() || "";
    const exitContext = normalizedTransition.includes("lift")
      ? " (setelah keluar dari lift)"
      : normalizedTransition.includes("tangga")
        ? " (setelah keluar dari tangga)"
        : "";

    return steps.map((step, index) => {
      const isFirstExitTurn =
        index === firstTurnIndex &&
        exitContext &&
        (step.type === "turn_left" || step.type === "turn_right");

      return {
        ...step,
        label: isFirstExitTurn ? `${step.label}${exitContext}` : step.label,
        index,
      };
    });
  }, []);

  const enhanceInstructionContext = useCallback((
    steps: NavigationStep[],
    _checkpointIds: string[],
    visibleFloor: -1 | 0 | 1 | 2,
    transitionLabel?: string,
    visibleFloorIndex = -1,
    previousVisibleFloor: -1 | 0 | 1 | 2 | null = null,
  ): NavigationStep[] => {
    if (!steps.length) return steps;

    const distanceBetween = (
      from: { x: number; y: number },
      to: { x: number; y: number },
    ) => Math.hypot(from.x - to.x, from.y - to.y);

    const distancePointToSegment = (
      point: { x: number; y: number },
      start: { x: number; y: number },
      end: { x: number; y: number },
    ) => {
      const lengthSquared = (end.x - start.x) ** 2 + (end.y - start.y) ** 2;
      if (lengthSquared === 0) return distanceBetween(point, start);

      const rawT =
        ((point.x - start.x) * (end.x - start.x) + (point.y - start.y) * (end.y - start.y)) /
        lengthSquared;
      const t = Math.max(0, Math.min(1, rawT));
      const projection = {
        x: start.x + t * (end.x - start.x),
        y: start.y + t * (end.y - start.y),
      };

      return {
        distance: distanceBetween(point, projection),
        t,
      };
    };

    const routeQrAnchors = Object.values(QR_ANCHOR_REGISTRY)
      .filter((anchor) => anchor.floor === visibleFloor)
      .sort((a, b) => a.qrId.localeCompare(b.qrId));

    const getQrAnchorsAlongStep = (step: NavigationStep) =>
      routeQrAnchors
        .map((anchor) => {
          const result = distancePointToSegment(
            { x: anchor.svgX, y: anchor.svgY },
            step.fromPoint,
            step.toPoint,
          );
          return { anchor, ...result };
        })
        .filter(({ distance, t }) => distance <= 45 && t > 0.08 && t <= 1)
        .sort((a, b) => a.t - b.t);

    const isNear = (
      point: { x: number; y: number },
      target: { x: number; y: number },
      tolerance = 70,
    ) => distanceBetween(point, target) <= tolerance;

    const getStraightLandmarkLabel = (step: NavigationStep): string | null => {
      if (step.type !== "straight" || step.distanceToNext < 110) return null;

      const floorLandmarks =
        visibleFloor === 2
          ? [
              { label: "area lift", point: { x: 1015, y: 517 } },
              { label: "area tangga", point: { x: 1060, y: 517 } },
              { label: "area tangga", point: { x: 805, y: 255 } },
            ]
          : visibleFloor === 1
            ? [
                { label: "area lift", point: { x: 1015, y: 523 } },
                { label: "area tangga", point: { x: 1060, y: 523 } },
                { label: "area tangga", point: { x: 652, y: 255 } },
              ]
            : [];

      const landmark = floorLandmarks.find(({ point }) => isNear(step.toPoint, point, 85));
      if (landmark) return `Lurus sampai ${landmark.label}`;

      // Bug 2 Fix: deteksi persimpangan untuk koridor vertikal menggunakan metode berbeda.
      // getQrAnchorsAlongStep memakai threshold 45px yang terlalu kecil untuk koridor vertikal
      // di mana anchor persimpangan horizontal bisa berada 100–200px secara horizontal.
      const isVerticalStep =
        Math.abs(step.toPoint.y - step.fromPoint.y) > Math.abs(step.toPoint.x - step.fromPoint.x) * 2;

      if (isVerticalStep) {
        const minY = Math.min(step.fromPoint.y, step.toPoint.y);
        const maxY = Math.max(step.fromPoint.y, step.toPoint.y);
        const stepX = (step.fromPoint.x + step.toPoint.x) / 2;

        const verticalCrossings = routeQrAnchors
          .filter((anchor) => {
            const inYRange = anchor.svgY >= minY - 40 && anchor.svgY <= maxY + 40;
            const notTooFarX = Math.abs(anchor.svgX - stepX) <= 300;
            const notAtEndpoints =
              Math.abs(anchor.svgY - step.fromPoint.y) > 40 &&
              Math.abs(anchor.svgY - step.toPoint.y) > 40;
            return inYRange && notTooFarX && notAtEndpoints;
          })
          .sort((a, b) => a.svgY - b.svgY);

        if (verticalCrossings.length >= 2) {
          return `Lurus melewati ${verticalCrossings.length} persimpangan`;
        }
        if (verticalCrossings.length === 1) {
          return `Lurus hingga ${verticalCrossings[0].qrId}`;
        }
      } else {
        const qrAnchors = getQrAnchorsAlongStep(step);
        if (qrAnchors.length >= 3) {
          return `Lurus melewati ${qrAnchors.length} persimpangan`;
        }
        if (qrAnchors.length >= 1) {
          return `Lurus hingga ${qrAnchors[qrAnchors.length - 1].anchor.qrId}`;
        }
      }

      return null;
    };

    const normalizedTransition = transitionLabel?.toLowerCase() || "";
    const isVerticalFloorTransition =
      previousVisibleFloor !== null &&
      previousVisibleFloor > 0 &&
      visibleFloor > 0 &&
      previousVisibleFloor !== visibleFloor;
    const exitContext = normalizedTransition.includes("lift")
      ? "lift"
      : normalizedTransition.includes("evakuasi")
        ? "tangga evakuasi"
        : normalizedTransition.includes("tangga")
          ? "tangga"
          : "";
    const firstTurnIndex = steps.findIndex((step) => step.type === "turn_left" || step.type === "turn_right");
    const routeStartPoint = steps[0].fromPoint;

    // Fix C: guard jalur QR-F1-N11→N12 — koridor vertikal lurus di x≈652 yang bukan
    // belokan keluar lift/tangga. Dipakai di isNearRouteStart dan needsExitOrientationStep.
    const isQrF1N11ToN12Corridor = (step: NavigationStep) => {
      const isNearX652 = Math.abs(step.fromPoint.x - 652) <= 70;
      const isVerticalDown = step.toPoint.y > step.fromPoint.y;
      const isLongEnough = Math.abs(step.toPoint.y - step.fromPoint.y) >= 120;
      const isNarrowX = Math.abs(step.toPoint.x - step.fromPoint.x) <= 45;
      return isNearX652 && isVerticalDown && isLongEnough && isNarrowX;
    };

    const isNearRouteStart = (step: NavigationStep) =>
      distanceBetween(routeStartPoint, step.pivotPoint) <= 90 &&
      !isQrF1N11ToN12Corridor(step);

    const labeledSteps = steps.map((step, index) => {
      const shouldAddExitContext =
        visibleFloorIndex > 0 &&
        isVerticalFloorTransition &&
        index === firstTurnIndex &&
        isNearRouteStart(step) &&
        exitContext &&
        (step.type === "turn_left" || step.type === "turn_right") &&
        !step.label.includes("setelah keluar");

      const straightLandmarkLabel =
        step.label === "Jalan lurus" || step.label === "Lurus"
          ? getStraightLandmarkLabel(step)
          : null;

      return {
        ...step,
        label: shouldAddExitContext
          ? `${step.label} (setelah keluar dari ${exitContext})`
          : straightLandmarkLabel || step.label,
        index,
      };
    });

    const firstStep = labeledSteps[0];
    // Fix B: tambah batas atas distanceToNext (step lurus pertama harus pendek — tepat di area exit)
    // dan guard isQrF1N11ToN12Corridor agar jalur N11→N12 tidak dipaksa diberi step orientasi.
    const needsExitOrientationStep =
      visibleFloorIndex > 0 &&
      isVerticalFloorTransition &&
      exitContext &&
      firstStep?.type === "straight" &&
      firstStep.distanceToNext >= 100 &&
      firstStep.distanceToNext <= 200 &&
      !firstStep.label.includes("setelah keluar") &&
      !isQrF1N11ToN12Corridor(firstStep);

    if (!needsExitOrientationStep) {
      return labeledSteps;
    }

    const dx = firstStep.toPoint.x - firstStep.fromPoint.x;
    const dy = firstStep.toPoint.y - firstStep.fromPoint.y;
    // Arah mata angin: Atas SVG = Utara (dy<0), Bawah = Selatan (dy>0),
    // Kanan = Timur (dx>0), Kiri = Barat (dx<0)
    const getCardinalDirection = (vx: number, vy: number): string => {
      const absDx = Math.abs(vx);
      const absDy = Math.abs(vy);
      if (absDx >= absDy) return vx >= 0 ? "Timur" : "Barat";
      return vy >= 0 ? "Selatan" : "Utara";
    };
    const cardinalDir = getCardinalDirection(dx, dy);
    const exitTurnType: "turn_left" | "turn_right" =
      Math.abs(dx) >= Math.abs(dy)
        ? dx >= 0 ? "turn_right" : "turn_left"
        : dy >= 0 ? "turn_left" : "turn_right";
    const exitOrientationStep: NavigationStep = {
      ...firstStep,
      type: exitTurnType,
      angleChange: exitTurnType === "turn_right" ? 90 : -90,
      distanceToNext: 0,
      cumulativeDistance: 0,
      toPoint: firstStep.fromPoint,
      label: `Menuju ke arah ${cardinalDir} (setelah keluar dari ${exitContext})`,
      nextQrHint: undefined,
      index: 0,
    };

    return [exitOrientationStep, ...labeledSteps].map((step, index) => ({
      ...step,
      index,
    }));
  }, [QR_ANCHOR_REGISTRY]);

  const buildParkingL2BridgeEntrySteps = useCallback((steps: NavigationStep[]): NavigationStep[] => {
    if (!steps.length) return steps;

    const bridgeLabel = "Gunakan jembatan penghubung menuju gedung rumah sakit lantai 2";
    if (steps[0].type === "straight") {
      return steps.map((step, index) => ({
        ...step,
        label: index === 0 ? bridgeLabel : step.label,
        index,
      }));
    }

    const firstStep = steps[0];
    const bridgeStep: NavigationStep = {
      ...firstStep,
      index: 0,
      type: "straight",
      fromPoint: firstStep.fromPoint,
      pivotPoint: firstStep.fromPoint,
      toPoint: firstStep.pivotPoint,
      distanceToNext: pointDistance(firstStep.fromPoint, firstStep.pivotPoint),
      angleChange: 0,
      label: bridgeLabel,
      nextQrHint: undefined,
    };

    return [bridgeStep, ...steps].map((step, index) => ({
      ...step,
      index,
    }));
  }, []);

  const mergeConsecutiveStraightSteps = useCallback((steps: NavigationStep[]): NavigationStep[] => {
    if (!steps.length) return steps;

    const merged: NavigationStep[] = [];

    steps.forEach((step) => {
      const previous = merged[merged.length - 1];

      if (previous && previous.type === "straight" && step.type === "straight") {
        previous.toPoint = step.toPoint;
        previous.distanceToNext += step.distanceToNext;
        previous.cumulativeDistance = step.cumulativeDistance;
        if (previous.label === "Jalan lurus" && step.label !== "Jalan lurus") {
          previous.label = step.label;
        }
        return;
      }

      merged.push({ ...step });
    });

    return merged.map((step, index) => ({
      ...step,
      index,
    }));
  }, []);

  const normalizeFloor2ConnectorExitSteps = useCallback((steps: NavigationStep[]): NavigationStep[] => {
    if (!steps.length) return steps;

    const isApproachingFloor2StairArea = (step: NavigationStep) =>
      step.type === "straight" &&
      Math.abs(step.fromPoint.y - step.toPoint.y) <= 40 &&
      step.toPoint.y >= 230 &&
      step.toPoint.y <= 285 &&
      step.toPoint.x >= 760 &&
      step.toPoint.x <= 1090 &&
      step.fromPoint.x <= step.toPoint.x;

    return mergeConsecutiveStraightSteps(steps.map((step, index) => {
      if (isApproachingFloor2StairArea(step)) {
        return {
          ...step,
          index,
          label: "Lurus sampai bertemu area tangga",
        };
      }

      return {
        ...step,
        index,
      };
    }));
  }, [mergeConsecutiveStraightSteps]);

  const normalizeFloor1StairExitSteps = useCallback((
    steps: NavigationStep[],
    transitionLabel?: string
  ): NavigationStep[] => {
    if (!steps.length) return steps;

    const isNearQrF1N11 = (point: SvgPoint) =>
      Math.abs(point.x - 652) <= 70 && point.y >= 245 && point.y <= 395;

    const isJalanKebidananAxis = (point: SvgPoint) =>
      Math.abs(point.x - 654) <= 20 && point.y >= 260 && point.y <= 520;

    const isAllowedMainCorridorAxis = (point: SvgPoint) =>
      isNearQrF1N11(point) || isJalanKebidananAxis(point);

    const isQrF1N11ToN12Corridor = (step: NavigationStep) =>
      step.type === "straight" &&
      isAllowedMainCorridorAxis(step.fromPoint) &&
      Math.abs(step.toPoint.x - step.fromPoint.x) <= 45 &&
      step.toPoint.y > step.fromPoint.y &&
      Math.abs(step.toPoint.y - step.fromPoint.y) >= 120;

    const normalized = isQrF1N11ToN12Corridor(steps[0])
      ? steps.map((step, index) => ({
          ...step,
          index,
        }))
      : improvePostConnectorSteps(steps, transitionLabel);

    const isVerticalMainCorridorStep = (step: NavigationStep) =>
      isAllowedMainCorridorAxis(step.pivotPoint) &&
      Math.abs(step.toPoint.x - step.pivotPoint.x) <= 50 &&
      step.toPoint.y > step.pivotPoint.y &&
      Math.abs(step.toPoint.y - step.pivotPoint.y) >= 90;

    const isTopCorridorDrop = (step: NavigationStep) =>
      Math.abs(step.fromPoint.y - step.pivotPoint.y) <= 50 &&
      step.pivotPoint.y <= 340 &&
      Math.abs(step.toPoint.x - step.pivotPoint.x) <= 45 &&
      step.toPoint.y > step.pivotPoint.y &&
      Math.abs(step.toPoint.y - step.pivotPoint.y) >= 90;

    const relabelTurn = (
      step: NavigationStep,
      type: "turn_left" | "turn_right"
    ): NavigationStep => ({
      ...step,
      type,
      angleChange: type === "turn_left" ? -90 : 90,
      label: type === "turn_left" ? "Belok kiri" : "Belok kanan",
    });

    return mergeConsecutiveStraightSteps(normalized.map((step, index) => {
      if (
        index === 0 &&
        (step.type === "turn_left" || step.type === "turn_right") &&
        isVerticalMainCorridorStep(step)
      ) {
        return {
          ...step,
          index,
          type: "straight",
          angleChange: 0,
          label: "Jalan lurus",
        };
      }

      if (
        (step.type === "turn_left" || step.type === "turn_right") &&
        isTopCorridorDrop(step)
      ) {
        if (step.fromPoint.x > step.pivotPoint.x + 40) {
          return {
            ...relabelTurn(step, "turn_left"),
            index,
          };
        }

        if (step.fromPoint.x < step.pivotPoint.x - 40) {
          return {
            ...relabelTurn(step, "turn_right"),
            index,
          };
        }
      }

      return {
        ...step,
        index,
      };
    }));
  }, [improvePostConnectorSteps, mergeConsecutiveStraightSteps]);

  // Rebuild route when user switches between parking map and hospital floors
  // (only if there's an active route involving parking)
  useEffect(() => {
    if (!activeRoute) return;
    
    // Check if this is a parking ↔ hospital route
    const startFloor = resolveFloorForRoomExtended(activeRoute.startRoomId);
    const endFloor   = resolveFloorForRoomExtended(activeRoute.endRoomId);
    const isParkingRoute = startFloor === 0 || startFloor === -1 || endFloor === 0 || endFloor === -1;
    
    if (!isParkingRoute) return;

    const startPoint = liveSvgPointRef.current ?? undefined;
    const scannedQrStartNodeId =
      !preferRoomCenterStartRef.current &&
      lastQrAnchor?.roomId === activeRoute.startRoomId
        ? lastQrAnchor.routeNodeId
        : undefined;

    // Rebuild to get the correct segment for the current view
    const rebuilt = buildDebugRouteForRooms(
      activeRoute.startRoomId,
      activeRoute.endRoomId,
      {
        startPoint,
        useExactStartPoint: !preferRoomCenterStartRef.current && Boolean(startPoint),
        startNodeId: scannedQrStartNodeId,
      }
    );
    
    const routePointsKey = (route: RoomRouteResult) =>
      route.points.map((point) => `${point.x.toFixed(2)},${point.y.toFixed(2)}`).join("|");

    // Update when either the graph path or the visible segment points change.
    if (
      rebuilt &&
      (
        rebuilt.checkpointIds.join(',') !== activeRoute.checkpointIds.join(',') ||
        routePointsKey(rebuilt) !== routePointsKey(activeRoute)
      )
    ) {
      setActiveRoute(rebuilt);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    showParkingMap,
    parkingFloor,
    activeFloor,
    svgReadyVersion,
    parkingSvgDoc,
    parking2SvgDoc,
    resolveFloorForRoomExtended,
    lastQrAnchor,
  ]);
  // Note: activeRoute and buildDebugRouteForRooms are intentionally NOT in deps to avoid infinite loop

  useEffect(() => {
    const activeFloorSegment = getRouteSegmentForFloor(activeRoute, activeFloor);
    if (!activeFloorSegment || activeFloorSegment.points.length < 2) {
      setNavSteps([]);
      setActiveStepIndex(0);
      lastNavStepsKeyRef.current = null;
      return;
    }

    let steps = buildNavigationSteps(activeFloorSegment.points);
    const visibleFloor: -1 | 0 | 1 | 2 = showParkingMap ? (parkingFloor === 1 ? 0 : -1) : activeFloor;
    const visibleFloorIndex = activeRoute?.floorsInvolved?.indexOf(visibleFloor) ?? -1;
    const previousVisibleFloor =
      activeRoute?.floorsInvolved && visibleFloorIndex > 0
        ? activeRoute.floorsInvolved[visibleFloorIndex - 1]
        : null;
    const isParkingL2BridgeEntry =
      activeRoute?.floorsInvolved?.[0] === -1 &&
      visibleFloor === 2 &&
      !showParkingMap;

    if (
      activeRoute?.startRoomId === "Parking_Lantai_1" &&
      activeRoute.endRoomId &&
      resolveFloorForRoomExtended(activeRoute.endRoomId) === 1 &&
      !showParkingMap &&
      activeFloor === 1
    ) {
      steps = buildParkingL1HospitalEntrySteps(activeFloorSegment.points);
    } else if (
      activeRoute?.endRoomId === "Parking_Lantai_1" &&
      resolveFloorForRoomExtended(activeRoute.startRoomId) === 1 &&
      !showParkingMap &&
      activeFloor === 1
    ) {
      steps = buildHospitalParkingL1ExitSteps(activeFloorSegment.points);
    }

    if (isParkingL2BridgeEntry) {
      steps = buildParkingL2BridgeEntrySteps(steps);
    }

    steps = enhanceInstructionContext(
      steps,
      activeFloorSegment.checkpointIds,
      visibleFloor,
      activeRoute?.transitionLabel,
      visibleFloorIndex,
      previousVisibleFloor,
    );

    const nextNavStepsKey = [
      activeRoute?.startRoomId || "",
      activeRoute?.endRoomId || "",
      showParkingMap ? `parking-${parkingFloor}` : `floor-${activeFloor}`,
      activeFloorSegment.points.map((point) => `${point.x.toFixed(2)},${point.y.toFixed(2)}`).join("|"),
    ].join("::");
    const shouldResetStepIndex = lastNavStepsKeyRef.current !== nextNavStepsKey;
    lastNavStepsKeyRef.current = nextNavStepsKey;

    setNavSteps(steps);
    setActiveStepIndex((index) =>
      shouldResetStepIndex ? 0 : Math.min(index, Math.max(steps.length - 1, 0))
    );
  }, [
    activeRoute,
    activeFloor,
    getRouteSegmentForFloor,
    resolveFloorForRoomExtended,
    showParkingMap,
    parkingFloor,
    buildParkingL1HospitalEntrySteps,
    buildHospitalParkingL1ExitSteps,
    buildParkingL2BridgeEntrySteps,
    enhanceInstructionContext,
  ]);

  // Auto-close room info popup when navigation route becomes active
  useEffect(() => {
    if (activeRoute) {
      setActiveRoomInfo(null);
      setActiveRoomId(null);
    }
  }, [activeRoute]);

  useEffect(() => {
    if (!isLiveMode || !liveSvgPoint || !navSteps.length) return;
    const idx = getActiveStepIndex(liveSvgPoint, navSteps);
    setActiveStepIndex(idx);
  }, [isLiveMode, liveSvgPoint, navSteps]);

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
      renderRouteOverlay(svgDoc, activeRoute, activeFloor);
    };

    applyRoute();
    objectElement.addEventListener("load", applyRoute);
    return () => objectElement.removeEventListener("load", applyRoute);
  }, [activeRoute, renderRouteOverlay, activeFloor, showParkingMap, parkingFloor, svgReadyVersion]);

  useEffect(() => {
    const svgDoc = objectRef.current?.contentDocument;
    if (!svgDoc) return;
    svgDoc.getElementById("dynamic-turn-arrow-layer")?.remove();
  }, [navSteps, activeStepIndex, svgReadyVersion]);

  useEffect(() => {
    const svgDoc = objectRef.current?.contentDocument;
    if (!svgDoc) return;
    renderQrAnchorHints(svgDoc, activeQrAnchors, lastQrAnchor?.qrId || null, true);
  }, [renderQrAnchorHints, activeQrAnchors, lastQrAnchor, svgReadyVersion, showParkingMap]);

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
  const currentVisibleFloor: -1 | 0 | 1 | 2 = showParkingMap ? (parkingFloor === 1 ? 0 : -1) : activeFloor;

  const isParkingL1ToHospitalFloor1Route =
    activeRoute?.startRoomId === "Parking_Lantai_1" &&
    activeRoute?.endRoomId &&
    resolveFloorForRoomExtended(activeRoute.endRoomId) === 1;
  const nextTransitionFloor = (() => {
    if (!activeRoute?.floorsInvolved || activeRoute.floorsInvolved.length <= 1) return null;
    if (!currentNavStep || currentNavStep.type !== "arrive") return null;

    const currentFloorIndex = activeRoute.floorsInvolved.indexOf(currentVisibleFloor);
    if (currentFloorIndex < 0 || currentFloorIndex >= activeRoute.floorsInvolved.length - 1) return null;

    return activeRoute.floorsInvolved[currentFloorIndex + 1];
  })();
  const nextTransitionFloorLabel = (() => {
    switch (nextTransitionFloor) {
      case -1:
        return "parkir lantai 2";
      case 0:
        return "parkir lantai 1";
      case 1:
        return "lantai 1";
      case 2:
        return "lantai 2";
      default:
        return "";
    }
  })();
  const connectorInstruction = (() => {
    if (!nextTransitionFloorLabel || !activeRoute?.transitionLabel) return "";
    if (activeRoute.endRoomId === "Parking_Lantai_1" && showParkingMap && parkingFloor === 1) return "";

    const normalizedTransition = activeRoute.transitionLabel.toLowerCase();
    const isBridgeTransitionToHospitalFloor2 = currentVisibleFloor === -1 && nextTransitionFloor === 2;
    if (isBridgeTransitionToHospitalFloor2) {
      return "Gunakan jembatan penghubung menuju gedung rumah sakit lantai 2";
    }
    if (normalizedTransition.includes("lift")) {
      return `Masuk ke lift untuk menuju ${nextTransitionFloorLabel}`;
    }
    if (normalizedTransition.includes("tangga")) {
      return `Gunakan tangga untuk menuju ${nextTransitionFloorLabel}`;
    }

    return "";
  })();

  const currentNavInstruction = (() => {
    if (!currentNavStep) return "";

    if (isParkingL1ToHospitalFloor1Route && activeStepIndex === 0) {
      return "Ikuti jalur utama menuju gedung rumah sakit";
    }

    if (connectorInstruction) {
      return connectorInstruction;
    }

    switch (currentNavStep.type) {
      case "turn_left":
        if (currentNavStep.label && currentNavStep.label !== "Belok kiri") {
          return currentNavStep.label;
        }
        return "Belok kiri";
      case "turn_right":
        if (currentNavStep.label && currentNavStep.label !== "Belok kanan") {
          return currentNavStep.label;
        }
        return "Belok kanan";
      case "straight":
        if (currentNavStep.label && currentNavStep.label !== "Jalan lurus") {
          return currentNavStep.label;
        }
        return "Lurus";
      case "u_turn":
        return "Ke belakang (putar balik)";
      case "arrive":
        return "Anda telah tiba di tujuan";
      default:
        return currentNavStep.label;
    }
  })();

  const activeStepNumber = navSteps.length
    ? Math.min(activeStepIndex, navSteps.length - 1) + 1
    : 0;
  const canGoToPreviousStep = activeStepIndex > 0;
  const canGoToNextStep = activeStepIndex < navSteps.length - 1;
  const handlePreviousNavStep = useCallback(() => {
    setActiveStepIndex((index) => Math.max(index - 1, 0));
  }, []);
  const handleNextNavStep = useCallback(() => {
    setActiveStepIndex((index) => Math.min(index + 1, Math.max(navSteps.length - 1, 0)));
  }, [navSteps.length]);

  // ---------------------------------------------------------------------------
  // Render
  // ---------------------------------------------------------------------------

  if (roomsLoading || qrLoading) {
    return (
      <div className="relative flex-1 overflow-hidden rounded-xl border border-border bg-muted/20 shadow-inner">
        <div className="flex h-full min-h-[420px] items-center justify-center text-sm text-muted-foreground">
          Loading map data...
        </div>
      </div>
    );
  }

  if (roomsError || qrError) {
    return (
      <div className="relative flex-1 overflow-hidden rounded-xl border border-destructive/30 bg-destructive/10 shadow-inner">
        <div className="flex h-full min-h-[420px] items-center justify-center px-4 text-center text-sm text-destructive">
          Failed to load map data. Please refresh the page.
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-1 min-h-0 gap-3">
      {/* ── Map container ── */}
      <div className="relative flex-1 min-h-0 overflow-hidden bg-muted/20 rounded-xl border border-border shadow-inner pb-[0px] md:pb-0">

      {/* Compass overlay — di luar mapRef agar tidak ikut zoom/pan (R-06) */}
      <div className="absolute top-4 right-4 z-20 pointer-events-none">
        <img
          src="/images/arah%20mata%20angin.png"
          alt="Kompas arah mata angin"
          className="w-16 h-16 opacity-85 drop-shadow-md select-none"
          draggable={false}
        />
      </div>

      <div
        ref={containerRef}
        className={`w-full h-full ${isDragging ? "cursor-grabbing" : "cursor-grab"}`}
        onMouseDown={handleMouseDown}
        onTouchStart={handleTouchStart}
        onTouchMove={handleTouchMove}
        onTouchEnd={handleTouchEnd}
        onTouchCancel={handleTouchEnd}
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
              key={activeMapSvgPath}
              ref={objectRef}
              data={activeMapSvgPath}
              type="image/svg+xml"
              className="max-w-[90%] max-h-[90%]"
              onLoad={() => setSvgLoadTick((prev) => prev + 1)}
              aria-label={showParkingMap ? "Peta lahan parkir" : "Hospital interactive map"}
            />
          </div>
        </div>
      </div>

      {isPathfindingDebugVisible ? (
        <div className="absolute top-4 right-4 z-30 w-[320px] max-w-[calc(100vw-2rem)] rounded-xl border border-border bg-background/90 backdrop-blur-md shadow-lg p-3 space-y-2">
          <div className="flex items-center justify-between">
            <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">Navigation HUD</p>
            <div className="flex items-center gap-2">
              <span className="text-[10px] text-muted-foreground">Map aktif: Lantai {activeFloor}</span>
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
                preferRoomCenterStartRef.current = true;
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
              {debugRoutingRooms.map((room) => (
                <option key={room.id} value={room.id}>
                  [L{room.floor}] {room.name}
                </option>
              ))}
            </select>
          </div>
        ) : (
          <div className="space-y-1">
            <label className="text-[11px] text-muted-foreground">
              {isCalibrationMode ? "Kalibrasi Posisi via QR" : "Scan QR (simulasi)"}
            </label>
            <div className="flex gap-2">
              <input
                value={qrCodeInput}
                onChange={(event) => setQrCodeInput(event.target.value)}
                placeholder="Contoh: QR-F-N01 / QR-R-IGD"
                className="flex-1 rounded-md border border-border bg-background px-2 py-1.5 text-xs"
              />
              <button
                onClick={() => {
                  if (isCalibrationMode) {
                    handleCalibrateQrOnly();
                    setIsCalibrationMode(false);
                  } else {
                    handleResolveQrLocation();
                  }
                }}
                className="rounded-md border border-border bg-muted px-2 py-1.5 text-xs font-medium hover:bg-muted/80"
              >
                Set
              </button>
              {isCalibrationMode && (
                <button
                  onClick={() => {
                    setIsCalibrationMode(false);
                    setQrCodeInput("");
                  }}
                  className="rounded-md border border-border bg-background px-2 py-1.5 text-xs font-medium hover:bg-muted/80"
                >
                  Batal
                </button>
              )}
            </div>

            <div className="flex items-center justify-between mt-1">
              <p className="text-[10px] text-muted-foreground">Tampilkan titik QR di peta • daftar semua lantai</p>
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
              {allRegisteredQrAnchors.map((anchor) => (
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
            {debugRoutingRooms.map((room) => (
              <option key={room.id} value={room.id}>
                [L{room.floor}] {room.name}
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
            {activeRoute.floorsInvolved && activeRoute.floorsInvolved.length > 1
              ? ` • multi-floor via ${activeRoute.transitionLabel || "connector"}`
              : ""}
          </p>
        )}
        {routeDebugMessage && (
          <p className="text-[11px] text-muted-foreground">{routeDebugMessage}</p>
        )}
        {liveModeStatus && (
          <p className="text-[11px] text-muted-foreground">{liveModeStatus}</p>
        )}
        <div className="space-y-1 rounded-md border border-border bg-muted/20 p-2">
          <div className="flex items-center justify-between">
            <p className="text-[11px] font-semibold text-foreground">Data Ruangan Debug</p>
            <p className="text-[10px] text-muted-foreground">
              Total {debugRoutingRooms.length} ruangan • L1 {debugRoutingRoomsByFloor[1].length} • L2 {debugRoutingRoomsByFloor[2].length}
            </p>
          </div>
          <div className="max-h-32 overflow-y-auto space-y-1 text-[10px]">
            {debugRoutingRooms.map((room) => (
              <div
                key={room.id}
                className="flex items-center justify-between rounded bg-background/70 px-1.5 py-1"
              >
                <span className="font-medium text-foreground">[L{room.floor}] {room.name}</span>
                <span className="text-muted-foreground">{room.id}</span>
              </div>
            ))}
          </div>
        </div>
        </div>
      ) : null}

      <div className="absolute top-4 left-4 z-30 inline-flex items-center rounded-md border border-border bg-background/90 shadow">
        <button
          onClick={() => { setActiveFloor(1); setShowParkingMap(false); }}
          className={`px-3 py-1.5 text-[11px] font-semibold ${
            activeFloor === 1 && !showParkingMap
              ? "bg-primary text-primary-foreground"
              : "text-foreground hover:bg-muted"
          }`}
        >
          {floorCopy.floor1}
        </button>
        <button
          onClick={() => { setActiveFloor(2); setShowParkingMap(false); }}
          className={`px-3 py-1.5 text-[11px] font-semibold ${
            activeFloor === 2 && !showParkingMap
              ? "bg-primary text-primary-foreground"
              : "text-foreground hover:bg-muted"
          }`}
        >
          {floorCopy.floor2}
        </button>
        <button
          onClick={() => { setShowParkingMap(true); setParkingFloor(1); setActiveFloor(1); }}
          className={`border-l border-border px-3 py-1.5 text-[11px] font-semibold transition-colors ${
            showParkingMap && parkingFloor === 1
              ? "bg-emerald-600 text-white"
              : "text-foreground hover:bg-muted"
          }`}
          title={floorCopy.parking1Title}
        >
          🅿 {floorCopy.parking1}
        </button>
        <button
          onClick={() => { setShowParkingMap(true); setParkingFloor(2); setActiveFloor(2); }}
          className={`px-3 py-1.5 text-[11px] font-semibold transition-colors ${
            showParkingMap && parkingFloor === 2
              ? "bg-emerald-600 text-white"
              : "text-foreground hover:bg-muted"
          }`}
          title={floorCopy.parking2Title}
        >
          🅿 {floorCopy.parking2}
        </button>
      </div>

      {(activeRoute || isLiveMode) && navSteps.length > 0 && currentNavStep && (
        <div className="hidden" aria-hidden="true" />
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
      {activeMarkerPosition && activeMarkerPosition.x >= -16 && activeMarkerPosition.y >= -32 && (
        <div
          className="absolute z-10 pointer-events-none -translate-x-1/2 -translate-y-full"
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
      {showCurrentUserMarker && currentUserMarkerPosition && currentUserMarkerPosition.x >= -16 && currentUserMarkerPosition.y >= -16 && (
        <div
          className="absolute z-10 pointer-events-none -translate-x-1/2 -translate-y-1/2"
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
                onStartNavigation?.({ mode: "qr", destinationRoomId });
              }}
              className="inline-flex items-center gap-1.5 rounded-lg bg-primary px-3 py-1.5 text-xs font-semibold text-primary-foreground hover:bg-primary/90 transition-colors"
            >
              <Navigation className="h-3.5 w-3.5" />
              Navigate Here
            </button>
          </div>
        </div>
      )}
      </div>{/* end map container */}

      {/* ── Nav panel: desktop = right column, mobile = fixed bottom sheet ── */}
      {(activeRoute || isLiveMode) && navSteps.length > 0 && currentNavStep && (
        <>
          {/* Desktop: right column (hidden on mobile) */}
          <div className="hidden md:flex flex-col w-[260px] shrink-0 overflow-hidden rounded-2xl border border-border bg-card shadow-xl self-start sticky top-0">
            {/* Header */}
            <div className="flex items-center justify-between border-b border-border bg-card px-3 py-2.5">
              <div className="flex items-center gap-2 min-w-0">
                <div className="flex h-6 w-6 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                  <Navigation className="h-3.5 w-3.5 text-primary" />
                </div>
                <div className="min-w-0">
                  <p className="mb-0.5 text-[9px] font-semibold uppercase tracking-widest leading-none text-muted-foreground">Navigasi Aktif</p>
                  <p className="truncate text-[12px] font-bold leading-tight text-foreground">
                    {roomInfoBySvgId[endRoomId]?.name || endRoomId || "Tujuan"}
                  </p>
                </div>
              </div>
              <button
                onClick={handleClearRoute}
                className="flex h-6 w-6 shrink-0 items-center justify-center rounded-lg text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
                title="Hentikan navigasi"
                aria-label="Hentikan navigasi"
              >
                <X className="h-3.5 w-3.5" />
              </button>
            </div>
            {/* Step */}
            <div className="px-3 pt-3 pb-2">
              <div className="flex items-start gap-3">
                <div className={`flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-xl shadow-sm ${
                  currentNavStep.type === "arrive" ? "bg-emerald-50 text-emerald-600" : "bg-muted text-foreground"
                }`}>
                  {currentNavStep.type === "arrive" ? "🏁"
                    : currentNavStep.type === "turn_left" ? "↰"
                    : currentNavStep.type === "turn_right" ? "↱"
                    : currentNavStep.type === "u_turn" ? "↩"
                    : "↑"}
                </div>
                <div className="flex-1 min-w-0 pt-0.5">
                  <p className="text-[14px] font-extrabold leading-snug text-foreground">{currentNavInstruction}</p>
                  <p className="mt-1 text-xs text-muted-foreground">Ikuti arah panah</p>
                </div>
              </div>
            </div>
            <div className="px-3 pb-3">
              <div className="grid grid-cols-[1fr_auto_1fr] items-center gap-2">
                <button
                  onClick={handlePreviousNavStep}
                  disabled={!canGoToPreviousStep}
                  className="inline-flex h-9 items-center justify-center gap-1.5 rounded-lg border border-border px-2 text-[11px] font-semibold text-foreground transition-colors hover:bg-muted disabled:cursor-not-allowed disabled:opacity-40"
                  title="Lihat navigasi sebelumnya"
                  aria-label="Lihat navigasi sebelumnya"
                >
                  <ChevronLeft className="h-3.5 w-3.5" />
                  Sebelum
                </button>
                <span className="min-w-[42px] text-center text-[11px] font-semibold text-muted-foreground">
                  {activeStepNumber}/{navSteps.length}
                </span>
                <button
                  onClick={handleNextNavStep}
                  disabled={!canGoToNextStep}
                  className="inline-flex h-9 items-center justify-center gap-1.5 rounded-lg border border-border px-2 text-[11px] font-semibold text-foreground transition-colors hover:bg-muted disabled:cursor-not-allowed disabled:opacity-40"
                  title="Lihat navigasi selanjutnya"
                  aria-label="Lihat navigasi selanjutnya"
                >
                  Setelah
                  <ChevronRight className="h-3.5 w-3.5" />
                </button>
              </div>
            </div>
            {/* QR Calibration */}
            {activeRoute && endRoomIdRef.current && (
              <div className="px-3 pb-3">
                <button
                  onClick={() => onStartNavigation?.({ mode: "calibrate", destinationRoomId: endRoomIdRef.current || undefined })}
                  className="w-full flex items-center justify-center gap-2 rounded-xl bg-emerald-500 hover:bg-emerald-600 active:scale-95 px-3 py-2.5 text-white font-bold text-xs shadow-lg shadow-emerald-500/25 transition-all"
                  title="Kalibrasi posisi via QR Code"
                  style={{ animation: 'navQrPulse 2.4s ease-in-out infinite' }}
                >
                  <QrCode className="h-4 w-4 shrink-0" />
                  Kalibrasi via QR
                </button>
              </div>
            )}
          </div>

          {/* Mobile: fixed bottom sheet (hidden on desktop) */}
          <div className="md:hidden fixed bottom-0 left-0 right-0 z-40 rounded-t-2xl border-t border-border bg-card shadow-2xl">
            {/* Drag handle */}
            <div className="flex justify-center pt-2 pb-1">
              <div className="w-10 h-1 rounded-full bg-muted-foreground/30" />
            </div>
            {/* Header row */}
            <div className="flex items-center justify-between px-4 pb-2">
              <div className="flex items-center gap-2 min-w-0">
                <div className="flex h-6 w-6 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                  <Navigation className="h-3.5 w-3.5 text-primary" />
                </div>
                <div className="min-w-0">
                  <p className="text-[9px] font-semibold uppercase tracking-widest text-muted-foreground">Navigasi Aktif</p>
                  <p className="truncate text-[12px] font-bold text-foreground">
                    {roomInfoBySvgId[endRoomId]?.name || endRoomId || "Tujuan"}
                  </p>
                </div>
              </div>
              <button
                onClick={handleClearRoute}
                className="flex h-7 w-7 shrink-0 items-center justify-center rounded-lg text-muted-foreground hover:bg-muted hover:text-foreground transition-colors"
                title="Hentikan navigasi"
                aria-label="Hentikan navigasi"
              >
                <X className="h-4 w-4" />
              </button>
            </div>
            {/* Step + QR row */}
            <div className="flex items-center gap-3 px-4 pb-4">
              <div className={`flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-xl shadow-sm ${
                currentNavStep.type === "arrive" ? "bg-emerald-50 text-emerald-600" : "bg-muted text-foreground"
              }`}>
                {currentNavStep.type === "arrive" ? "🏁"
                  : currentNavStep.type === "turn_left" ? "↰"
                  : currentNavStep.type === "turn_right" ? "↱"
                  : currentNavStep.type === "u_turn" ? "↩"
                  : "↑"}
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-[15px] font-extrabold leading-snug text-foreground">{currentNavInstruction}</p>
                <p className="text-xs text-muted-foreground mt-0.5">Ikuti arah panah</p>
              </div>
              {activeRoute && endRoomIdRef.current && (
                <button
                  onClick={() => onStartNavigation?.({ mode: "calibrate", destinationRoomId: endRoomIdRef.current || undefined })}
                  className="shrink-0 flex flex-col items-center justify-center gap-1 rounded-xl bg-emerald-500 hover:bg-emerald-600 active:scale-95 px-3 py-2 text-white shadow-lg shadow-emerald-500/25 transition-all"
                  title="Kalibrasi posisi via QR Code"
                  style={{ animation: 'navQrPulse 2.4s ease-in-out infinite' }}
                >
                  <QrCode className="h-5 w-5" />
                  <span className="text-[10px] font-bold leading-none">QR</span>
                </button>
              )}
            </div>
            <div className="grid grid-cols-[1fr_auto_1fr] items-center gap-2 px-4 pb-4">
              <button
                onClick={handlePreviousNavStep}
                disabled={!canGoToPreviousStep}
                className="inline-flex h-9 items-center justify-center gap-1 rounded-lg border border-border px-2 text-[11px] font-semibold text-foreground transition-colors hover:bg-muted disabled:cursor-not-allowed disabled:opacity-40"
                title="Lihat navigasi sebelumnya"
                aria-label="Lihat navigasi sebelumnya"
              >
                <ChevronLeft className="h-3.5 w-3.5" />
                Sebelum
              </button>
              <span className="min-w-[42px] text-center text-[11px] font-semibold text-muted-foreground">
                {activeStepNumber}/{navSteps.length}
              </span>
              <button
                onClick={handleNextNavStep}
                disabled={!canGoToNextStep}
                className="inline-flex h-9 items-center justify-center gap-1 rounded-lg border border-border px-2 text-[11px] font-semibold text-foreground transition-colors hover:bg-muted disabled:cursor-not-allowed disabled:opacity-40"
                title="Lihat navigasi selanjutnya"
                aria-label="Lihat navigasi selanjutnya"
              >
                Next
                <ChevronRight className="h-3.5 w-3.5" />
              </button>
            </div>
          </div>

          <style>{`
            @keyframes navQrPulse {
              0%, 100% { box-shadow: 0 4px 14px rgba(16,185,129,0.25); }
              50% { box-shadow: 0 4px 24px rgba(16,185,129,0.55); }
            }
          `}</style>
        </>
      )}
    </div>
  );
};

export default MapViewer;
