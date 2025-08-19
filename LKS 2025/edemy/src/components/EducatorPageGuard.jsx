import React from 'react';
import { Navigate } from 'react-router-dom';
import { useUserRole } from '../hooks/useUserRole.js';
import { useUser } from '@clerk/clerk-react';

/**
 * Triple Security Wrapper untuk Educator Pages
 * Digunakan sebagai lapisan keamanan tambahan di setiap halaman educator
 */
const EducatorPageGuard = ({ children, pageName = 'EducatorPage' }) => {
  const { user, isLoaded } = useUser();
  const { isEducator, organizationLoaded, organizationData } = useUserRole();

  console.log(`🛡️ ${pageName} Security Guard:`, {
    isLoaded,
    organizationLoaded,
    isEducator,
    userEmail: user?.emailAddresses[0]?.emailAddress,
    organizationCount: organizationData?.length || 0
  });

  // Wait for all data to load
  if (!isLoaded || !organizationLoaded) {
    return (
      <div className="flex items-center justify-center p-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
        <span className="ml-2 text-gray-600">Loading...</span>
      </div>
    );
  }

  // Final security check - if not educator, block immediately
  if (!isEducator) {
    console.log(`🚨 ${pageName} BLOCKED: Non-educator detected`);
    console.log('User details:', {
      email: user?.emailAddresses[0]?.emailAddress,
      organizationMemberships: organizationData?.map(m => ({
        org: m.organization.name,
        role: m.role
      }))
    });
    return <Navigate to="/courses" replace />;
  }

  console.log(`✅ ${pageName} ACCESS GRANTED`);
  return children;
};

export default EducatorPageGuard;
