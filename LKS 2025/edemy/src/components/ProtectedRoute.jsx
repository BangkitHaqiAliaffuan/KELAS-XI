import { useUser, useOrganizationList } from '@clerk/clerk-react';
import { Navigate, useLocation } from 'react-router-dom';
import { useUserRole } from '../hooks/useUserRole.js';

const ProtectedRoute = ({ children, requiredRole = null }) => {
  const { isSignedIn, isLoaded, user } = useUser();
  const { userMemberships } = useOrganizationList({
    userMemberships: { infinite: true },
  });
  const { isEducator, isStudent, organizationData, organizationLoaded, roleCalculationLoaded } = useUserRole();
  const location = useLocation();

  console.log('🛡️ ProtectedRoute Debug:', {
    path: location.pathname,
    requiredRole,
    isLoaded,
    isSignedIn,
    organizationLoaded,
    roleCalculationLoaded,
    isEducator,
    isStudent,
    userEmail: user?.emailAddresses[0]?.emailAddress,
    organizationCount: organizationData?.length || 0
  });

  // Show loading only while Clerk is initializing
  if (!isLoaded) {
    console.log('⏳ ProtectedRoute: Still loading user data...');
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
        <div className="ml-4 text-gray-600">Loading user data...</div>
      </div>
    );
  }

  // Fast path: if role calculation is done, proceed immediately
  if (roleCalculationLoaded) {
    console.log('⚡ Role calculation complete, proceeding with access check');
  } else {
    // For educator routes, we need to wait longer for organization data
    if (requiredRole === 'educator') {
      console.log('⏳ Educator route - waiting for organization data to complete...');
      return (
        <div className="min-h-screen flex items-center justify-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <div className="ml-2 text-gray-600">Verifying educator access...</div>
        </div>
      );
    } else {
      // For non-educator routes, proceed faster
      console.log('⏳ Role calculation in progress...');
      return (
        <div className="min-h-screen flex items-center justify-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <div className="ml-2 text-gray-600">Checking access...</div>
        </div>
      );
    }
  }

  // If not signed in, redirect to home
  if (!isSignedIn) {
    console.log('🚨 User not signed in, redirecting to home');
    return <Navigate to="/" state={{ from: location }} replace />;
  }

  // Check role if required
  if (requiredRole) {
    console.log('� STRICT ROLE CHECK for:', requiredRole);
    console.log('User organization data:', organizationData);
    
    if (requiredRole === 'educator') {
      // Triple check for educator access:
      // 1. Must be identified as educator by useUserRole
      // 2. Must have organization membership
      // 3. Must be member of "edemy" organization specifically
      const hasEdemyMembership = organizationData?.some(
        membership => membership.organization.name.toLowerCase() === 'edemy'
      );
      
      if (!isEducator || !hasEdemyMembership) {
        console.log('🚨 SECURITY BLOCK: Non-educator or non-edemy member attempting to access educator route');
        console.log('Details:', {
          isEducator,
          hasEdemyMembership,
          organizationCount: organizationData?.length || 0,
          organizations: organizationData?.map(m => ({ name: m.organization.name, role: m.role })) || []
        });
        console.log('Redirecting to home page...');
        return <Navigate to="/" replace />;
      }
      console.log('✅ EDUCATOR ACCESS GRANTED - User is educator in edemy organization');
    }
    
    if (requiredRole === 'student') {
      if (!isStudent) {
        console.log('🚨 SECURITY BLOCK: Educator attempting to access student-only route');
        console.log('Redirecting to educator dashboard...');
        return <Navigate to="/educator/dashboard" replace />;
      }
      console.log('✅ STUDENT ACCESS GRANTED');
    }
  }

  return children;
};

export default ProtectedRoute;
