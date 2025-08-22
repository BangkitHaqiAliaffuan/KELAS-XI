import React, { useEffect } from 'react';
import { useClerkUserSync } from '../hooks/useClerkUserSync';

const ClerkSyncWrapper = ({ children }) => {
  const { syncStatus } = useClerkUserSync();

  useEffect(() => {
    if (syncStatus.synced) {
      console.log('✅ Clerk user successfully synced to MongoDB');
    }
    
    if (syncStatus.error) {
      console.error('❌ Failed to sync Clerk user to MongoDB:', syncStatus.error);
    }
  }, [syncStatus]);

  // Silent sync - no UI interruption
  // Only log to console for debugging purposes
  
  return (
    <>
      {children}
    </>
  );
};

export default ClerkSyncWrapper;
