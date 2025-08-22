import { useUser } from '@clerk/clerk-react';
import { useEffect, useState, useRef } from 'react';

const API_BASE_URL = 'http://localhost:5000';

// Custom hook untuk auto-sync Clerk user ke MongoDB
export const useClerkUserSync = () => {
  const { user, isLoaded, isSignedIn } = useUser();
  const [syncStatus, setSyncStatus] = useState({
    loading: false,
    synced: false,
    error: null
  });
  
  // Track if sync has been attempted for this user session
  const syncAttempted = useRef(false);
  const prevUserRef = useRef(null);

  const syncUserToMongoDB = async (userData) => {
    try {
      setSyncStatus({ loading: true, synced: false, error: null });

      const response = await fetch(`${API_BASE_URL}/api/clerk/sync-clerk-user`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          clerkId: userData.id,
          email: userData.primaryEmailAddress?.emailAddress,
          firstName: userData.firstName,
          lastName: userData.lastName,
          imageUrl: userData.imageUrl,
          role: 'student' // Default role
        })
      });

      const result = await response.json();

      if (result.success) {
        console.log('✅ User synced to MongoDB:', result.user);
        setSyncStatus({ loading: false, synced: true, error: null });
        return result.user;
      } else {
        throw new Error(result.error || 'Failed to sync user');
      }

    } catch (error) {
      console.error('❌ Error syncing user to MongoDB:', error);
      setSyncStatus({ loading: false, synced: false, error: error.message });
      throw error;
    }
  };

  const checkUserExists = async (clerkId) => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/clerk/clerk/${clerkId}`);
      const result = await response.json();
      return result.success;
    } catch (error) {
      console.error('❌ Error checking user existence:', error);
      return false;
    }
  };

  // Auto-sync when user loads - only once per session
  useEffect(() => {
    // Sync hanya saat user berubah dari null ke ada (login/register) dan belum pernah dicoba
    if (isLoaded && isSignedIn && user && !prevUserRef.current && !syncAttempted.current) {
      console.log('🔄 New user session detected, attempting sync...');
      syncAttempted.current = true;
      
      const performSync = async () => {
        try {
          // Check if user already exists in MongoDB
          const userExists = await checkUserExists(user.id);
          
          if (!userExists) {
            console.log('🔄 User not found in MongoDB, syncing...');
            await syncUserToMongoDB(user);
          } else {
            console.log('✅ User already exists in MongoDB');
            setSyncStatus({ loading: false, synced: true, error: null });
          }
        } catch (error) {
          console.error('❌ Auto-sync failed:', error);
          setSyncStatus({ loading: false, synced: false, error: error.message });
        }
      };

      performSync();
    }
    
    // Update previous user reference
    prevUserRef.current = user;
    
    // Reset sync attempted when user logs out
    if (!user && prevUserRef.current) {
      syncAttempted.current = false;
    }
    // eslint-disable-next-line
  }, [isLoaded, isSignedIn, user]);

  return {
    syncStatus,
    syncUserToMongoDB,
    checkUserExists,
    user: isLoaded && isSignedIn ? user : null
  };
};

// Utility function untuk manual sync
export const manualSyncClerkUser = async (userData) => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/clerk/sync-clerk-user`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        clerkId: userData.id,
        email: userData.primaryEmailAddress?.emailAddress,
        firstName: userData.firstName,
        lastName: userData.lastName,
        imageUrl: userData.imageUrl,
        role: userData.role || 'student'
      })
    });

    const result = await response.json();

    if (result.success) {
      return result.user;
    } else {
      throw new Error(result.error || 'Failed to sync user');
    }

  } catch (error) {
    console.error('❌ Manual sync failed:', error);
    throw error;
  }
};

// Get user from MongoDB by Clerk ID
export const getMongoUserByClerkId = async (clerkId) => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/clerk/clerk/${clerkId}`);
    const result = await response.json();

    if (result.success) {
      return result.user;
    } else {
      throw new Error(result.error || 'User not found');
    }

  } catch (error) {
    console.error('❌ Error fetching user from MongoDB:', error);
    throw error;
  }
};
