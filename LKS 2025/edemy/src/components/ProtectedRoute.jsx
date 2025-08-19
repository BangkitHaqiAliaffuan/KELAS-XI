import { useUser, useOrganizationList } from '@clerk/clerk-react';
import { Navigate, useLocation } from 'react-router-dom';
import { useUserRole } from '../hooks/useUserRole.js';

const ProtectedRoute = ({ children, requiredRole = null }) => {
  const { isSignedIn, isLoaded, user } = useUser();
  const { userMemberships } = useOrganizationList({
    userMemberships: { infinite: true },
  });
  const { isEducator, isStudent, organizationData, organizationLoaded } = useUserRole();
  const location = useLocation();

  console.log('🛡️ ProtectedRoute Debug:', {
    path: location.pathname,
    requiredRole,
    isLoaded,
    isSignedIn,
    organizationLoaded,
    isEducator,
    isStudent,
    userEmail: user?.emailAddresses[0]?.emailAddress,
    organizationCount: organizationData?.length || 0
  });

  // Show loading while Clerk is initializing OR organization data is loading
  if (!isLoaded || !organizationLoaded) {
    console.log('⏳ ProtectedRoute: Still loading data...');
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
        <div className="ml-4 text-gray-600">Loading user data...</div>
      </div>
    );
  }

  // If not signed in, redirect to appropriate auth page
  if (!isSignedIn) {
    // Check if it's an educator route
    if (location.pathname.startsWith('/educator')) {
      return <Navigate to="/educator" replace />;
    }
    // For other routes, redirect to home
    return <Navigate to="/" state={{ from: location }} replace />;
  }

  // Check role if required
  if (requiredRole) {
    console.log('� STRICT ROLE CHECK for:', requiredRole);
    console.log('User organization data:', organizationData);
    
    if (requiredRole === 'org:educator') {
      if (!isEducator) {
        console.log('🚨 SECURITY BLOCK: Non-educator attempting to access educator route');
        console.log('Redirecting to student portal...');
        return <Navigate to="/courses" replace />;
      }
      console.log('✅ EDUCATOR ACCESS GRANTED');
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
