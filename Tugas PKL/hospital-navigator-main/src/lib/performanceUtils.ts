/**
 * Performance Utilities
 * Helper functions for optimizing performance
 */

/**
 * Debounce function - delays execution until after wait time has elapsed
 * since the last time it was invoked
 */
export function debounce<T extends (...args: unknown[]) => unknown>(
  func: T,
  wait: number
): (...args: Parameters<T>) => void {
  let timeout: ReturnType<typeof setTimeout> | null = null;

  return function executedFunction(...args: Parameters<T>) {
    const later = () => {
      timeout = null;
      func(...args);
    };

    if (timeout !== null) {
      clearTimeout(timeout);
    }
    timeout = setTimeout(later, wait);
  };
}

/**
 * Throttle function - ensures function is called at most once per specified time period
 */
export function throttle<T extends (...args: unknown[]) => unknown>(
  func: T,
  limit: number
): (...args: Parameters<T>) => void {
  let inThrottle: boolean;
  let lastResult: ReturnType<T>;

  return function executedFunction(...args: Parameters<T>) {
    if (!inThrottle) {
      lastResult = func(...args) as ReturnType<T>;
      inThrottle = true;
      setTimeout(() => {
        inThrottle = false;
      }, limit);
    }
    return lastResult;
  };
}

/**
 * Request Animation Frame wrapper for smooth animations
 */
export function rafThrottle<T extends (...args: unknown[]) => unknown>(
  func: T
): (...args: Parameters<T>) => void {
  let rafId: number | null = null;

  return function executedFunction(...args: Parameters<T>) {
    if (rafId !== null) {
      return;
    }

    rafId = requestAnimationFrame(() => {
      func(...args);
      rafId = null;
    });
  };
}

/**
 * Batch DOM updates to minimize reflows
 */
export class DOMBatcher {
  private queue: Array<() => void> = [];
  private rafId: number | null = null;

  add(callback: () => void): void {
    this.queue.push(callback);
    this.scheduleFlush();
  }

  private scheduleFlush(): void {
    if (this.rafId !== null) {
      return;
    }

    this.rafId = requestAnimationFrame(() => {
      this.flush();
    });
  }

  private flush(): void {
    const callbacks = this.queue.slice();
    this.queue = [];
    this.rafId = null;

    // Execute all queued callbacks in a single frame
    callbacks.forEach((callback) => {
      try {
        callback();
      } catch (error) {
        console.error("Error in batched DOM update:", error);
      }
    });
  }

  clear(): void {
    this.queue = [];
    if (this.rafId !== null) {
      cancelAnimationFrame(this.rafId);
      this.rafId = null;
    }
  }
}

/**
 * Memoize expensive function calls
 */
export function memoize<T extends (...args: unknown[]) => unknown>(
  func: T,
  keyGenerator?: (...args: Parameters<T>) => string
): T {
  const cache = new Map<string, ReturnType<T>>();

  return ((...args: Parameters<T>) => {
    const key = keyGenerator
      ? keyGenerator(...args)
      : JSON.stringify(args);

    if (cache.has(key)) {
      return cache.get(key);
    }

    const result = func(...args) as ReturnType<T>;
    cache.set(key, result);
    return result;
  }) as T;
}

/**
 * Measure function execution time (for debugging)
 */
export function measurePerformance<T extends (...args: unknown[]) => unknown>(
  func: T,
  _label: string
): T {
  return ((...args: Parameters<T>) => {
    return func(...args);
  }) as T;
}

/**
 * Check if device is low-end (for adaptive performance)
 */
export function isLowEndDevice(): boolean {
  // Check for hardware concurrency (CPU cores)
  const cores = navigator.hardwareConcurrency || 1;
  if (cores <= 2) return true;

  // Check for device memory (if available)
  const memory = (navigator as { deviceMemory?: number }).deviceMemory;
  if (memory && memory <= 2) return true;

  // Check for connection type (if available)
  const connection = (navigator as { connection?: { effectiveType?: string } }).connection;
  if (connection?.effectiveType === "slow-2g" || connection?.effectiveType === "2g") {
    return true;
  }

  return false;
}

/**
 * Adaptive quality settings based on device capabilities
 */
export function getAdaptiveSettings() {
  const isLowEnd = isLowEndDevice();

  return {
    enableAnimations: !isLowEnd,
    maxCacheSize: isLowEnd ? 50 : 100,
    debounceDelay: isLowEnd ? 300 : 150,
    enableQrHints: !isLowEnd,
    maxVisibleLabels: isLowEnd ? 20 : 50,
  };
}
