/**
 * Backend health check utilities
 * Re-exports the shared API instance from services/api for consistency.
 */

import { api, checkHealth } from "@/services/api";

export { api as apiClient };

// Flag to cache backend availability between checks
let backendAvailable: boolean | null = null;

export const checkBackendHealth = async (): Promise<boolean> => {
  try {
    const response = await checkHealth();
    return response.data.success === true;
  } catch {
    return false;
  }
};

export const isBackendAvailable = async (): Promise<boolean> => {
  if (backendAvailable !== null) return backendAvailable;
  backendAvailable = await checkBackendHealth();
  return backendAvailable;
};

export const resetBackendCheck = () => {
  backendAvailable = null;
};

export default api;
