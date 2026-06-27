/**
 * Optimized Route Hook
 * Provides memoized and cached route calculations
 */

import { useMemo, useCallback, useRef } from "react";
import type { RoomRouteResult } from "@/data/hospitalRouteGraph";
import { routeCache } from "@/lib/routeCache";

interface UseOptimizedRouteOptions {
  startRoomId: string;
  endRoomId: string;
  floor?: number;
  useExactStart?: boolean;
  buildRouteFn: () => RoomRouteResult | null;
  enabled?: boolean;
}

export function useOptimizedRoute({
  startRoomId,
  endRoomId,
  floor,
  useExactStart,
  buildRouteFn,
  enabled = true,
}: UseOptimizedRouteOptions) {
  const lastRouteRef = useRef<RoomRouteResult | null>(null);
  const lastParamsRef = useRef<string>("");

  const route = useMemo(() => {
    if (!enabled || !startRoomId || !endRoomId) {
      return null;
    }

    // Generate params key for comparison
    const paramsKey = `${startRoomId}:${endRoomId}:${floor}:${useExactStart}`;

    // Return cached result if params haven't changed
    if (paramsKey === lastParamsRef.current && lastRouteRef.current) {
      return lastRouteRef.current;
    }

    // Check route cache first
    const cachedRoute = routeCache.get(startRoomId, endRoomId, floor, useExactStart);
    if (cachedRoute) {
      lastRouteRef.current = cachedRoute;
      lastParamsRef.current = paramsKey;
      return cachedRoute;
    }

    // Calculate new route
    const newRoute = buildRouteFn();

    // Cache the result
    if (newRoute) {
      routeCache.set(startRoomId, endRoomId, newRoute, floor, useExactStart);
      lastRouteRef.current = newRoute;
      lastParamsRef.current = paramsKey;
    }

    return newRoute;
  }, [startRoomId, endRoomId, floor, useExactStart, buildRouteFn, enabled]);

  const clearCache = useCallback(() => {
    routeCache.clear();
    lastRouteRef.current = null;
    lastParamsRef.current = "";
  }, []);

  return {
    route,
    clearCache,
  };
}
