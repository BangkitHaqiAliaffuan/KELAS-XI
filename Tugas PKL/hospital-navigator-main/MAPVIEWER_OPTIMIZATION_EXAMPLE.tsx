/**
 * MapViewer Optimization Example
 * 
 * Contoh implementasi optimisasi untuk MapViewer.tsx
 * File ini menunjukkan bagaimana mengintegrasikan optimisasi tanpa mengubah fungsionalitas
 */

import { useMemo, useCallback, useEffect } from "react";
import { routeCache } from "@/lib/routeCache";
import { debounce, getAdaptiveSettings, DOMBatcher } from "@/lib/performanceUtils";
import { useOptimizedRoute } from "@/hooks/useOptimizedRoute";
import { useDebouncedSvgRender, useSvgLayerManager } from "@/hooks/useDebouncedSvgRender";

// ============================================================================
// EXAMPLE 1: Optimized Route Calculation
// ============================================================================

// BEFORE (di dalam MapViewer component):
/*
const buildDebugRouteForRooms = useCallback((
  startRoomIdParam: string,
  endRoomIdParam: string,
  options?: { ... }
): RoomRouteResult | null => {
  // ... expensive pathfinding logic ...
  const route = buildRouteForRooms(startRoomIdParam, endRoomIdParam, svgDoc, options);
  return route;
}, [dependencies]);

useEffect(() => {
  if (startRoomId && endRoomId) {
    const route = buildDebugRouteForRooms(startRoomId, endRoomId);
    setActiveRoute(route);
  }
}, [startRoomId, endRoomId]);
*/

// AFTER (optimized with caching):
function OptimizedRouteCalculation() {
  const adaptiveSettings = useMemo(() => getAdaptiveSettings(), []);

  const { route: activeRoute, clearCache } = useOptimizedRoute({
    startRoomId,
    endRoomId,
    floor: activeFloor,
    useExactStart: preferRoomCenterStartRef.current,
    buildRouteFn: useCallback(() => {
      // Only called when cache miss
      return buildDebugRouteForRooms(startRoomId, endRoomId, {
        startPoint: liveSvgPoint || undefined,
        useExactStartPoint: !preferRoomCenterStartRef.current,
      });
    }, [startRoomId, endRoomId, liveSvgPoint]),
    enabled: isPathfindingDebugVisible,
  });

  // Clear cache when switching floors or maps
  useEffect(() => {
    clearCache();
  }, [activeFloor, showParkingMap, clearCache]);

  return activeRoute;
}

// ============================================================================
// EXAMPLE 2: Debounced SVG Rendering
// ============================================================================

// BEFORE:
/*
useEffect(() => {
  const svgDoc = objectRef.current?.contentDocument;
  if (!svgDoc) return;
  
  renderDynamicRoomLabels(svgDoc);
  renderRouteOverlay(svgDoc, activeRoute, activeFloor);
  renderQrAnchorHints(svgDoc, activeQrAnchors, lastQrAnchor?.qrId || null);
}, [activeFloor, showParkingMap, activeRoute, svgReadyVersion]);
*/

// AFTER (with debouncing):
function OptimizedSvgRendering() {
  const adaptiveSettings = useMemo(() => getAdaptiveSettings(), []);
  const { registerLayer, renderLayer, renderAllLayers } = useSvgLayerManager();

  // Register render functions for each layer
  useEffect(() => {
    const svgDoc = objectRef.current?.contentDocument;
    if (!svgDoc) return;

    registerLayer("labels", () => renderDynamicRoomLabels(svgDoc));
    registerLayer("route", () => renderRouteOverlay(svgDoc, activeRoute, activeFloor));
    registerLayer("qr", () => renderQrAnchorHints(svgDoc, activeQrAnchors, lastQrAnchor?.qrId || null));

    return () => {
      // Cleanup on unmount
    };
  }, [registerLayer]);

  // Debounced render for labels (changes frequently)
  useDebouncedSvgRender(
    () => renderLayer("labels"),
    [activeFloor, showParkingMap, svgReadyVersion],
    { delay: adaptiveSettings.debounceDelay }
  );

  // Immediate render for route (critical for navigation)
  useEffect(() => {
    renderLayer("route");
  }, [activeRoute, renderLayer]);

  // Debounced render for QR hints (non-critical)
  useDebouncedSvgRender(
    () => renderLayer("qr"),
    [activeQrAnchors, lastQrAnchor, showQrAnchorHints],
    { delay: adaptiveSettings.debounceDelay * 2 }
  );
}

// ============================================================================
// EXAMPLE 3: Batched DOM Updates
// ============================================================================

// BEFORE:
/*
const renderDynamicRoomLabels = useCallback((svgDoc: Document) => {
  const labelLayer = svgDoc.createElementNS(namespace, "g");
  
  roomTargets.forEach(({ path, room }) => {
    // Create and append label immediately
    const textNode = svgDoc.createElementNS(namespace, "text");
    // ... set attributes ...
    labelLayer.appendChild(textNode);  // DOM operation per room
  });
  
  svgRoot.appendChild(labelLayer);
}, []);
*/

// AFTER (with batching):
const renderDynamicRoomLabels = useCallback((svgDoc: Document) => {
  const batcher = new DOMBatcher();
  const labelLayer = svgDoc.createElementNS(namespace, "g");
  const fragment = document.createDocumentFragment();

  roomTargets.forEach(({ path, room }) => {
    batcher.add(() => {
      const textNode = svgDoc.createElementNS(namespace, "text");
      // ... set attributes ...
      fragment.appendChild(textNode);  // Append to fragment first
    });
  });

  // Single DOM operation after all labels are created
  batcher.add(() => {
    labelLayer.appendChild(fragment);
    svgRoot?.appendChild(labelLayer);
  });
}, []);

// ============================================================================
// EXAMPLE 4: Memoized Expensive Calculations
// ============================================================================

// BEFORE:
/*
const debugRoutingRooms = routingRoomOptions
  .map((room) => ({
    ...room,
    floor: resolveFloorForRoom(room),
  }))
  .sort((a, b) => a.floor - b.floor || a.name.localeCompare(b.name));
*/

// AFTER (with useMemo):
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

// ============================================================================
// EXAMPLE 5: Optimized Event Handlers
// ============================================================================

// BEFORE:
/*
const handleZoomIn = () => {
  setScale((prev) => Math.min(prev + ZOOM_STEP, MAX_SCALE));
};

const handleZoomOut = () => {
  setScale((prev) => Math.max(prev - ZOOM_STEP, MIN_SCALE));
};
*/

// AFTER (with useCallback):
const handleZoomIn = useCallback(() => {
  setScale((prev) => Math.min(prev + ZOOM_STEP, MAX_SCALE));
}, []); // No dependencies - stable reference

const handleZoomOut = useCallback(() => {
  setScale((prev) => Math.max(prev - ZOOM_STEP, MIN_SCALE));
}, []); // No dependencies - stable reference

// ============================================================================
// EXAMPLE 6: Adaptive Quality Settings
// ============================================================================

function AdaptiveQualitySettings() {
  const adaptiveSettings = useMemo(() => getAdaptiveSettings(), []);

  // Adjust features based on device capability
  const effectiveShowQrHints = adaptiveSettings.enableQrHints && showQrAnchorHints;
  const effectiveEnableAnimations = adaptiveSettings.enableAnimations;
  const maxVisibleLabels = adaptiveSettings.maxVisibleLabels;

  // Reduce label count on low-end devices
  const visibleRoomTargets = useMemo(() => {
    const targets = roomTargets.slice(0, maxVisibleLabels);
    return targets;
  }, [roomTargets, maxVisibleLabels]);

  return {
    effectiveShowQrHints,
    effectiveEnableAnimations,
    visibleRoomTargets,
  };
}

// ============================================================================
// EXAMPLE 7: Cleanup and Memory Management
// ============================================================================

// BEFORE:
/*
useEffect(() => {
  const handler = (event: MouseEvent) => {
    // ... handle event ...
  };
  
  document.addEventListener("mousedown", handler);
  
  // Missing cleanup!
}, []);
*/

// AFTER (with proper cleanup):
useEffect(() => {
  const handler = (event: MouseEvent) => {
    // ... handle event ...
  };
  
  document.addEventListener("mousedown", handler);
  
  return () => {
    document.removeEventListener("mousedown", handler);
    // Clear any caches or timers
    routeCache.cleanup();
  };
}, []);

// ============================================================================
// EXAMPLE 8: Performance Monitoring
// ============================================================================

function PerformanceMonitoring() {
  useEffect(() => {
    // Log cache statistics periodically
    const interval = setInterval(() => {
      const stats = routeCache.getStats();
      console.log("[Performance] Route Cache:", stats);
    }, 30000); // Every 30 seconds

    return () => clearInterval(interval);
  }, []);

  // Measure render time
  useEffect(() => {
    const start = performance.now();
    
    // Render operations
    renderDynamicRoomLabels(svgDoc);
    
    const duration = performance.now() - start;
    if (duration > 100) {
      console.warn(`[Performance] Slow render detected: ${duration.toFixed(2)}ms`);
    }
  }, [svgDoc]);
}

// ============================================================================
// COMPLETE OPTIMIZED COMPONENT STRUCTURE
// ============================================================================

export function OptimizedMapViewer(props: MapViewerProps) {
  // 1. Get adaptive settings once
  const adaptiveSettings = useMemo(() => getAdaptiveSettings(), []);

  // 2. Use optimized route calculation
  const { route: activeRoute } = useOptimizedRoute({
    startRoomId,
    endRoomId,
    floor: activeFloor,
    buildRouteFn: useCallback(() => buildDebugRouteForRooms(...), []),
    enabled: true,
  });

  // 3. Memoize expensive calculations
  const debugRoutingRooms = useMemo(() => {
    return routingRoomOptions.map(/* ... */).sort(/* ... */);
  }, [routingRoomOptions]);

  // 4. Use debounced SVG rendering
  useDebouncedSvgRender(
    () => renderDynamicRoomLabels(svgDoc),
    [activeFloor, svgReadyVersion],
    { delay: adaptiveSettings.debounceDelay }
  );

  // 5. Stable event handlers with useCallback
  const handleZoomIn = useCallback(() => {
    setScale((prev) => Math.min(prev + ZOOM_STEP, MAX_SCALE));
  }, []);

  // 6. Proper cleanup
  useEffect(() => {
    return () => {
      routeCache.clear();
      // ... other cleanup ...
    };
  }, []);

  return (
    // ... JSX ...
  );
}

export default OptimizedMapViewer;
