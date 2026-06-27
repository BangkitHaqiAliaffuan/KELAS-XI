/**
 * Route Cache Module
 * Provides caching for expensive pathfinding operations
 */

import type { RoomRouteResult } from "@/data/hospitalRouteGraph";

interface CacheEntry {
  result: RoomRouteResult;
  timestamp: number;
}

class RouteCache {
  private cache: Map<string, CacheEntry>;
  private maxSize: number;
  private ttl: number; // Time to live in milliseconds

  constructor(maxSize = 100, ttlMinutes = 30) {
    this.cache = new Map();
    this.maxSize = maxSize;
    this.ttl = ttlMinutes * 60 * 1000;
  }

  /**
   * Generate cache key from route parameters
   */
  private generateKey(
    startRoomId: string,
    endRoomId: string,
    floor?: number,
    useExactStart?: boolean
  ): string {
    return `${startRoomId}:${endRoomId}:${floor || "any"}:${useExactStart ? "exact" : "room"}`;
  }

  /**
   * Get cached route if available and not expired
   */
  get(
    startRoomId: string,
    endRoomId: string,
    floor?: number,
    useExactStart?: boolean
  ): RoomRouteResult | null {
    const key = this.generateKey(startRoomId, endRoomId, floor, useExactStart);
    const entry = this.cache.get(key);

    if (!entry) {
      return null;
    }

    // Check if entry has expired
    const now = Date.now();
    if (now - entry.timestamp > this.ttl) {
      this.cache.delete(key);
      return null;
    }

    return entry.result;
  }

  /**
   * Store route in cache
   */
  set(
    startRoomId: string,
    endRoomId: string,
    result: RoomRouteResult,
    floor?: number,
    useExactStart?: boolean
  ): void {
    // Enforce max size by removing oldest entries
    if (this.cache.size >= this.maxSize) {
      const firstKey = this.cache.keys().next().value;
      if (firstKey) {
        this.cache.delete(firstKey);
      }
    }

    const key = this.generateKey(startRoomId, endRoomId, floor, useExactStart);
    this.cache.set(key, {
      result,
      timestamp: Date.now(),
    });
  }

  /**
   * Clear all cached routes
   */
  clear(): void {
    this.cache.clear();
  }

  /**
   * Remove expired entries
   */
  cleanup(): void {
    const now = Date.now();
    for (const [key, entry] of this.cache.entries()) {
      if (now - entry.timestamp > this.ttl) {
        this.cache.delete(key);
      }
    }
  }

  /**
   * Get cache statistics
   */
  getStats(): { size: number; maxSize: number; hitRate: number } {
    return {
      size: this.cache.size,
      maxSize: this.maxSize,
      hitRate: 0, // TODO: Implement hit rate tracking
    };
  }
}

// Export singleton instance
export const routeCache = new RouteCache();

// Cleanup expired entries every 5 minutes
if (typeof window !== "undefined") {
  setInterval(() => {
    routeCache.cleanup();
  }, 5 * 60 * 1000);
}
