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
      resetBackendCheck(); // Force recheck
      const available = await checkBackendHealth();
      setIsAvailable(available);
      setLastChecked(new Date());
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Unknown error';
      setError(errorMessage);
      setIsAvailable(false);
      setLastChecked(new Date());
    } finally {
      setIsChecking(false);
    }
  }, []);

  // Check on mount
  useEffect(() => {
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
