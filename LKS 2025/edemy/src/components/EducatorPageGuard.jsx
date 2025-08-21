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
  const { isEducator, roleCalculationLoaded, organizationData } = useUserRole();

  console.log(`🛡️ ${pageName} Security Guard:`, {
    isLoaded,
    roleCalculationLoaded,
    isEducator,
    userEmail: user?.emailAddresses[0]?.emailAddress,
    organizationCount: organizationData?.length || 0
  });

  // Wait for user data to load
  if (!isLoaded) {
    return (
      <div className="flex items-center justify-center p-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
        <span className="ml-2 text-gray-600">Loading user data...</span>
      </div>
    );
  }

  // For students, don't wait for organization data - redirect immediately
  // For potential educators, wait a bit for organization data to load
  if (!roleCalculationLoaded) {
    // Show loading only briefly for potential educators
    return (
      <div className="flex items-center justify-center p-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
        <span className="ml-2 text-gray-600">Checking permissions...</span>
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
    return <Navigate to="/" replace />;
  }

  // Additional check: Must be member of edemy organization
  const hasEdemyMembership = organizationData?.some(
    membership => membership.organization.name.toLowerCase() === 'edemy'
  );

  if (!hasEdemyMembership) {
    console.log(`🚨 ${pageName} BLOCKED: User is not a member of edemy organization`);
    console.log('User details:', {
      email: user?.emailAddresses[0]?.emailAddress,
      isEducator,
      organizationCount: organizationData?.length || 0,
      organizations: organizationData?.map(m => ({ name: m.organization.name, role: m.role })) || []
    });
    return <Navigate to="/" replace />;
  }

  console.log(`✅ ${pageName} ACCESS GRANTED - User is educator in edemy organization`);
  return children;
};

export default EducatorPageGuard;
