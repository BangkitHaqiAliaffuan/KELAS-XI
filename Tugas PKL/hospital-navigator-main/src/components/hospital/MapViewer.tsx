import { useRef, useState, useCallback, useEffect } from "react";
import { Plus, Minus, Locate, X } from "lucide-react";
import type { HospitalLocation } from "@/data/hospitalLocations";
import { roomInfoBySvgId, type HospitalRoomInfo } from "@/data/hospitalRoomInfo";

interface MapViewerProps {
  selectedLocation?: HospitalLocation | null;
}

const MapViewer = ({ selectedLocation }: MapViewerProps) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const mapRef = useRef<HTMLDivElement>(null);
  const objectRef = useRef<HTMLObjectElement>(null);
  const [scale, setScale] = useState(1);
  const [position, setPosition] = useState({ x: 0, y: 0 });
  const [isDragging, setIsDragging] = useState(false);
  const [dragStart, setDragStart] = useState({ x: 0, y: 0 });
  const [activeRoomId, setActiveRoomId] = useState<string | null>(null);
  const [activeRoomInfo, setActiveRoomInfo] = useState<HospitalRoomInfo | null>(null);

  const MIN_SCALE = 0.4;
  const MAX_SCALE = 5;
  const ZOOM_STEP = 0.2;

  const roomFromPath = useCallback((path: SVGPathElement): HospitalRoomInfo | null => {
    const pathId = path.id?.trim();
    const pathLabel = (path.getAttribute("inkscape:label") || path.getAttribute("label") || "").trim();

    if (!pathId && !pathLabel) return null;

    const source = `${pathId} ${pathLabel}`.toLowerCase();
    if (source.includes("jalan") || source.includes("background") || source.includes("unamed")) {
      return null;
    }

    if (!pathLabel && pathId?.startsWith("path")) {
      return null;
    }

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
  }, []);

  const setupSvgRoomInteraction = useCallback(() => {
    const svgDoc = objectRef.current?.contentDocument;
    if (!svgDoc) return;

    const cleanupHandlers: Array<() => void> = [];
    const roomPaths = Array.from(svgDoc.querySelectorAll("path")) as SVGPathElement[];

    roomPaths.forEach((path) => {
      const room = roomFromPath(path);
      if (!room) return;

      path.classList.add("room-interactive");

      const clickHandler = (event: Event) => {
        event.stopPropagation();
        setActiveRoomId(room.id);
        setActiveRoomInfo(room);
      };

      path.addEventListener("click", clickHandler);
      cleanupHandlers.push(() => path.removeEventListener("click", clickHandler));
    });

    const rootClickHandler = () => {
      setActiveRoomInfo(null);
      setActiveRoomId(null);
    };

    svgDoc.documentElement.addEventListener("click", rootClickHandler);
    cleanupHandlers.push(() => svgDoc.documentElement.removeEventListener("click", rootClickHandler));

    return () => {
      cleanupHandlers.forEach((cleanup) => cleanup());
    };
  }, [roomFromPath]);

  useEffect(() => {
    const objectElement = objectRef.current;
    if (!objectElement) return;

    let cleanup: (() => void) | undefined;

    const onLoad = () => {
      cleanup?.();
      cleanup = setupSvgRoomInteraction();
    };

    if (objectElement.contentDocument) {
      onLoad();
    }

    objectElement.addEventListener("load", onLoad);

    return () => {
      objectElement.removeEventListener("load", onLoad);
      cleanup?.();
    };
  }, [setupSvgRoomInteraction]);

  // Highlight selected location or active room in SVG
  useEffect(() => {
    if (!objectRef.current) return;

    const svgDoc = objectRef.current.contentDocument;
    if (!svgDoc) return;

    // Clear previous active regions
    const activeRegions = svgDoc.querySelectorAll(".region-active");
    activeRegions.forEach((reg) => reg.classList.remove("region-active"));

    const targetByRoomClick = activeRoomId ? svgDoc.getElementById(activeRoomId) : null;
    const targetBySearch = selectedLocation?.svgId ? svgDoc.getElementById(selectedLocation.svgId) : null;

    const target = targetByRoomClick || targetBySearch;
    if (target) {
      target.classList.add("region-active");
    }
  }, [selectedLocation, activeRoomId]);

  // Center the map initially and on reset
  const centerMap = useCallback(() => {
    if (!containerRef.current || !mapRef.current) return;
    
    const containerRect = containerRef.current.getBoundingClientRect();
    const initialScale = 0.8; // Start slightly zoomed out to see the whole map
    
    setScale(initialScale);
    setPosition({
      x: (containerRect.width / 2) - (containerRect.width * initialScale / 2),
      y: (containerRect.height / 2) - (containerRect.height * initialScale / 2),
    });
  }, []);

  useEffect(() => {
    // Small delay to ensure dimensions are available
    const timer = setTimeout(centerMap, 100);
    return () => clearTimeout(timer);
  }, [centerMap]);

  const handleZoomIn = () => {
    setScale((s) => {
      const newScale = Math.min(s + ZOOM_STEP, MAX_SCALE);
      // Optional: keep center when using buttons
      return newScale;
    });
  };

  const handleZoomOut = () => {
    setScale((s) => {
      const newScale = Math.max(s - ZOOM_STEP, MIN_SCALE);
      return newScale;
    });
  };

  const handleReset = centerMap;

  const handleWheel = useCallback((e: WheelEvent) => {
    if (!containerRef.current) return;
    
    e.preventDefault();

    const delta = e.deltaY > 0 ? -0.1 : 0.1;
    
    // Using functional update to get current values without adding them to deps
    // which would cause the effect to re-run constantly
    setScale((prevScale) => {
      const newScale = Math.min(Math.max(prevScale + delta, MIN_SCALE), MAX_SCALE);
      
      if (newScale !== prevScale) {
        const rect = containerRef.current!.getBoundingClientRect();
        const mouseX = e.clientX - rect.left;
        const mouseY = e.clientY - rect.top;

        setPosition((prevPos) => {
          const mapX = (mouseX - prevPos.x) / prevScale;
          const mapY = (mouseY - prevPos.y) / prevScale;
          
          return {
            x: mouseX - mapX * newScale,
            y: mouseY - mapY * newScale,
          };
        });
      }
      return newScale;
    });
  }, []);

  useEffect(() => {
    const container = containerRef.current;
    if (container) {
      container.addEventListener('wheel', handleWheel, { passive: false });
      return () => container.removeEventListener('wheel', handleWheel);
    }
  }, [handleWheel]);

  const handleMouseDown = (e: React.MouseEvent) => {
    if (e.button !== 0) return;
    setIsDragging(true);
    setDragStart({ x: e.clientX - position.x, y: e.clientY - position.y });
  };

  const handleMouseMove = (e: React.MouseEvent) => {
    if (!isDragging) return;
    setPosition({ x: e.clientX - dragStart.x, y: e.clientY - dragStart.y });
  };

  const handleMouseUp = () => setIsDragging(false);

  return (
    <div className="relative flex-1 overflow-hidden bg-muted/20 rounded-xl border border-border shadow-inner">
      <div
        ref={containerRef}
        className={`w-full h-full ${isDragging ? "cursor-grabbing" : "cursor-grab"}`}
        onMouseDown={handleMouseDown}
        onMouseMove={handleMouseMove}
        onMouseUp={handleMouseUp}
        onMouseLeave={handleMouseUp}
      >
        <div
          ref={mapRef}
          style={{
            transform: `translate(${position.x}px, ${position.y}px) scale(${scale})`,
            transformOrigin: "0 0",
            transition: isDragging ? "none" : "transform 0.15s cubic-bezier(0.2, 0, 0, 1)",
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

      {/* Navigation overlay */}
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
        onClick={handleReset} 
        className="absolute bottom-6 right-6 w-11 h-11 bg-primary rounded-xl shadow-lg flex items-center justify-center text-primary-foreground hover:bg-primary/90 transition-all hover:scale-105 active:scale-95"
        title="Reset View"
      >
        <Locate className="h-5 w-5" />
      </button>

      {/* Legend indicator */}
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

      {activeRoomInfo && (
        <div className="absolute top-4 right-4 z-20 w-[320px] bg-background/90 backdrop-blur-md rounded-xl border border-border shadow-xl p-4">
          <div className="flex items-start justify-between gap-2">
            <div>
              <p className="text-[10px] uppercase tracking-wider text-muted-foreground font-semibold">
                {activeRoomInfo.category}
              </p>
              <h4 className="text-base font-bold text-foreground leading-tight mt-1">
                {activeRoomInfo.name}
              </h4>
            </div>
            <button
              onClick={() => {
                setActiveRoomInfo(null);
                setActiveRoomId(null);
              }}
              className="h-7 w-7 inline-flex items-center justify-center rounded-md hover:bg-muted text-muted-foreground"
              aria-label="Close room info"
            >
              <X className="h-4 w-4" />
            </button>
          </div>

          <p className="text-sm text-muted-foreground mt-2">📍 {activeRoomInfo.locationHint}</p>
          <p className="text-sm text-foreground/90 mt-2">{activeRoomInfo.description}</p>
        </div>
      )}
    </div>
  );
};

export default MapViewer;
