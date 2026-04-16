import { useRef, useState, useCallback, useEffect } from "react";
import { Plus, Minus, Locate, X, Navigation } from "lucide-react";
import { roomInfoBySvgId, type HospitalRoomInfo } from "@/data/hospitalRoomInfo";

interface MapViewerProps {
  selectedLocation?: HospitalRoomInfo | null;
  onClearSelection?: () => void;
}

const MapViewer = ({ selectedLocation, onClearSelection }: MapViewerProps) => {
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

  const MIN_SCALE = 0.4;
  const MAX_SCALE = 5;
  const ZOOM_STEP = 0.2;
  const DRAG_THRESHOLD = 4; // px before we consider it a real drag

  // Keep refs in sync with state
  useEffect(() => { scaleRef.current = scale; }, [scale]);
  useEffect(() => { positionRef.current = position; }, [position]);

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

  const calculateMarkerPosition = useCallback(
    (target: SVGGraphicsElement | null) => {
      if (
        !target ||
        !containerRef.current ||
        !objectRef.current?.contentDocument
      ) {
        setActiveMarkerPosition(null);
        return;
      }

      const svgRoot =
        objectRef.current.contentDocument.querySelector("svg");
      if (!svgRoot) {
        setActiveMarkerPosition(null);
        return;
      }

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
        setActiveMarkerPosition(null);
        return;
      }

      const centerX = bbox.x + bbox.width / 2;
      const topY = bbox.y;

      const markerX =
        objectRect.left -
        containerRect.left +
        ((centerX - viewBox.x) / viewBox.width) * objectRect.width;
      const markerY =
        objectRect.top -
        containerRect.top +
        ((topY - viewBox.y) / viewBox.height) * objectRect.height;

      setActiveMarkerPosition({ x: markerX, y: markerY });
    },
    []
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
      if (
        source.includes("jalan") ||
        source.includes("background") ||
        source.includes("unamed")
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
        const targetElement = asSvgGraphicsElement(path);
        if (!targetElement) return;
        const bbox = targetElement.getBBox();
        if (!bbox.width || !bbox.height) return;

        const centerX = bbox.x + bbox.width / 2;
        const centerY = bbox.y + bbox.height / 2;
        const minDimension = Math.min(bbox.width, bbox.height);
        const area = bbox.width * bbox.height;

        const fontSize = Math.max(
          8,
          Math.min(28, Math.min(minDimension * 0.22, Math.sqrt(area) * 0.09))
        );
        const maxCharsPerLine = Math.max(4, Math.floor(bbox.width / (fontSize * 0.58)));

        const labelText = room.name || room.id;
        const lines = buildLabelLines(labelText, maxCharsPerLine);
        if (!lines.length) return;

        const textNode = svgDoc.createElementNS(namespace, "text");
        textNode.setAttribute("x", String(centerX));
        textNode.setAttribute("y", String(centerY));
        textNode.setAttribute("text-anchor", "middle");
        textNode.setAttribute("font-size", `${fontSize.toFixed(2)}px`);
        textNode.setAttribute("font-weight", "700");
        textNode.setAttribute("fill", "#ffffff");
        textNode.setAttribute("paint-order", "stroke");
        textNode.setAttribute("stroke", "#0f172a");
        textNode.setAttribute("stroke-width", `${Math.max(0.6, fontSize * 0.1).toFixed(2)}`);
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
  }, [roomFromPath, startDrag, handleWheel, renderDynamicRoomLabels]);

  useEffect(() => {
    const objectElement = objectRef.current;
    if (!objectElement) return;

    let cleanup: (() => void) | undefined;

    const onLoad = () => {
      cleanup?.();
      cleanup = setupSvgRoomInteraction();
    };

    if (objectElement.contentDocument) onLoad();

    objectElement.addEventListener("load", onLoad);
    return () => {
      objectElement.removeEventListener("load", onLoad);
      cleanup?.();
    };
  }, [setupSvgRoomInteraction]);

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

      // Convert SVG coords to container-space (how the <object> is currently rendered)
      const scaleSvgToObj = objectRect.width / viewBox.width;
      const objCenterX = (svgCenterX - viewBox.x) * scaleSvgToObj;
      const objCenterY = (svgCenterY - viewBox.y) * scaleSvgToObj;
      
      console.log(`[MapViewer] 🔍 scaleSvgToObj=${scaleSvgToObj}, objCenter=(${objCenterX}, ${objCenterY})`);

      // Desired zoom level
      const targetScale = 2.0;

      // The SVG element center (in container-space) needs to land at container center
      const containerCenterX = containerRect.width / 2;
      const containerCenterY = containerRect.height / 2;

      const newPos = {
        x: containerCenterX - objCenterX * targetScale,
        y: containerCenterY - objCenterY * targetScale,
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

      {/* Navigation hint */}
      <div className="absolute top-4 left-4 flex flex-col gap-1 pointer-events-none">
        <div className="px-3 py-1.5 bg-background/80 backdrop-blur-md border border-border rounded-full text-[10px] font-semibold uppercase tracking-wider text-muted-foreground shadow-sm">
          Desktop View • Mouse Wheel to Zoom
        </div>
      </div>

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

      {/* Legend */}
      <div className="absolute bottom-6 left-6 flex items-center gap-4 text-[10px] text-muted-foreground bg-background/50 backdrop-blur-sm px-3 py-1.5 rounded-full border border-border/50">
        <div className="flex items-center gap-1.5">
          <div className="w-2 h-2 rounded-full bg-primary" />
          <span>Your Location</span>
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
              onClick={() =>
                setScale((current) => Math.min(current + 0.2, MAX_SCALE))
              }
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