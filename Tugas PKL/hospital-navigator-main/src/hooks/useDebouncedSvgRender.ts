/**
 * Debounced SVG Render Hook
 * Optimizes SVG DOM operations by debouncing render calls
 */

import { useEffect, useRef, useCallback } from "react";
import { debounce, DOMBatcher } from "@/lib/performanceUtils";

interface UseDebouncedSvgRenderOptions {
  delay?: number;
  enabled?: boolean;
}

export function useDebouncedSvgRender(
  renderFn: () => void,
  dependencies: unknown[],
  options: UseDebouncedSvgRenderOptions = {}
) {
  const { delay = 150, enabled = true } = options;
  const batcherRef = useRef<DOMBatcher>(new DOMBatcher());
  const debouncedRenderRef = useRef<ReturnType<typeof debounce>>();

  // Create debounced render function
  useEffect(() => {
    debouncedRenderRef.current = debounce(() => {
      if (enabled) {
        batcherRef.current.add(renderFn);
      }
    }, delay);

    return () => {
      batcherRef.current.clear();
    };
  }, [renderFn, delay, enabled]);

  // Trigger debounced render when dependencies change
  useEffect(() => {
    if (enabled && debouncedRenderRef.current) {
      debouncedRenderRef.current();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, dependencies);

  // Provide immediate render function for critical updates
  const renderImmediate = useCallback(() => {
    if (enabled) {
      batcherRef.current.add(renderFn);
    }
  }, [renderFn, enabled]);

  return {
    renderImmediate,
  };
}

/**
 * Hook for managing multiple SVG layers with optimized rendering
 */
export function useSvgLayerManager() {
  const layersRef = useRef<Map<string, () => void>>(new Map());
  const batcherRef = useRef<DOMBatcher>(new DOMBatcher());

  const registerLayer = useCallback((layerId: string, renderFn: () => void) => {
    layersRef.current.set(layerId, renderFn);
  }, []);

  const unregisterLayer = useCallback((layerId: string) => {
    layersRef.current.delete(layerId);
  }, []);

  const renderLayer = useCallback((layerId: string) => {
    const renderFn = layersRef.current.get(layerId);
    if (renderFn) {
      batcherRef.current.add(renderFn);
    }
  }, []);

  const renderAllLayers = useCallback(() => {
    layersRef.current.forEach((renderFn) => {
      batcherRef.current.add(renderFn);
    });
  }, []);

  const clearAllLayers = useCallback(() => {
    layersRef.current.clear();
    batcherRef.current.clear();
  }, []);

  return {
    registerLayer,
    unregisterLayer,
    renderLayer,
    renderAllLayers,
    clearAllLayers,
  };
}
