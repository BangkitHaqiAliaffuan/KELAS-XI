/**
 * Hook to check and monitor backend status
 * Automatically checks backend availability on mount
 */

import { useState, useEffect, useCallback } from 'react';
import { checkBackendHealth, resetBackendCheck } from '@/lib/api';

export const useBackendStatus = () => {
  const [isChecking, setIsChecking] = useState(true);
  const [isAvailable, setIsAvailable] = useState<boolean | null>(null);
  const [lastChecked, setLastChecked] = useState<Date | null>(null);
  const [error, setError] = useState<string | null>(null);

  const checkStatus = useCallback(async () => {
    setIsChecking(true);
    setError(null);
    
    try {
      console.log('[useBackendStatus] Checking backend health...');
      resetBackendCheck(); // Force recheck
      const available = await checkBackendHealth();
      
      setIsAvailable(available);
      setLastChecked(new Date());
      
      console.log(`[useBackendStatus] Backend is ${available ? 'AVAILABLE ✅' : 'NOT AVAILABLE ❌'}`);
      console.log(`[useBackendStatus] Data source: ${available ? 'API (Real-time)' : 'Static (Fallback)'}`);
      
      if (available) {
        console.log('[useBackendStatus] 🎉 Using backend API for data');
      } else {
        console.log('[useBackendStatus] ⚠️ Using static data (backend not running)');
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Unknown error';
      setError(errorMessage);
      setIsAvailable(false);
      setLastChecked(new Date());
      console.error('[useBackendStatus] Error checking backend:', errorMessage);
    } finally {
      setIsChecking(false);
    }
  }, []);

  // Check on mount
  useEffect(() => {
    console.log('[useBackendStatus] Hook initialized, checking backend status...');
    checkStatus();
  }, [checkStatus]);

  return {
    isChecking,
    isAvailable,
    lastChecked,
    error,
    checkStatus,
  };
};

export default useBackendStatus;
