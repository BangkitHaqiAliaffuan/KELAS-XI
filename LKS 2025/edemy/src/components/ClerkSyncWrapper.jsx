import React, { useEffect } from 'react';
import { useClerkUserSync } from '../hooks/useClerkUserSync';

const ClerkSyncWrapper = ({ children }) => {
  const { syncStatus, user } = useClerkUserSync();

  useEffect(() => {
    if (syncStatus.synced && user) {
      console.log('✅ Clerk user successfully synced to MongoDB:', user.id);
    }
    
    if (syncStatus.error) {
      console.error('❌ Failed to sync Clerk user to MongoDB:', syncStatus.error);
    }
  }, [syncStatus, user]);

  // Show loading indicator while syncing
  if (syncStatus.loading) {
    return (
      <div className="fixed top-4 right-4 z-50">
        <div className="bg-blue-600 text-white px-4 py-2 rounded-lg shadow-lg flex items-center space-x-2">
          <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
          <span className="text-sm">Syncing user data...</span>
        </div>
      </div>
    );
  }

  // Show error notification if sync failed
  if (syncStatus.error && user) {
    return (
      <>
        {children}
        <div className="fixed top-4 right-4 z-50">
          <div className="bg-red-600 text-white px-4 py-2 rounded-lg shadow-lg">
            <p className="text-sm font-medium">Failed to sync user data</p>
            <p className="text-xs opacity-90">{syncStatus.error}</p>
          </div>
        </div>
      </>
    );
  }

  return children;
};

export default ClerkSyncWrapper;
