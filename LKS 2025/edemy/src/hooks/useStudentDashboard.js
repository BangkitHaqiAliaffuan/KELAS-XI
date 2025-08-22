import { useState, useEffect, useRef } from 'react';
import { useUser } from '@clerk/clerk-react';

const API_BASE_URL = 'http://localhost:5000';

export const useStudentDashboard = () => {
  const { user, isLoaded } = useUser();
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // Prevent multiple simultaneous fetches
  const fetchingRef = useRef(false);
  const fetchAttempted = useRef(false);

  const fetchDashboardData = async () => {
    if (!user?.id || fetchingRef.current) return;

    try {
      fetchingRef.current = true;
      setLoading(true);
      setError(null);

      console.log('📊 Fetching dashboard data for:', user.id);

      const response = await fetch(`${API_BASE_URL}/api/student-dashboard/${user.id}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }

      const result = await response.json();

      if (result.success) {
        setDashboardData(result.data);
        setError(null);
      } else {
        setError(result.message || 'Failed to fetch dashboard data');
      }
    } catch (err) {
      console.error('Error fetching dashboard data:', err);
      setError(`Network error: ${err.message}`);
    } finally {
      setLoading(false);
      fetchingRef.current = false;
    }
  };

  const refreshDashboard = () => {
    fetchAttempted.current = false;
    fetchDashboardData();
  };

  useEffect(() => {
    // Only fetch once when user is loaded and available, and not already attempted
    if (isLoaded && user?.id && !fetchAttempted.current) {
      fetchAttempted.current = true;
      fetchDashboardData();
    }
    
    // Reset when user changes
    if (!user?.id && fetchAttempted.current) {
      fetchAttempted.current = false;
      setDashboardData(null);
      setError(null);
      setLoading(false);
    }
  }, [isLoaded, user?.id]);

  return {
    dashboardData,
    loading,
    error,
    refreshDashboard
  };
};

export default useStudentDashboard;
