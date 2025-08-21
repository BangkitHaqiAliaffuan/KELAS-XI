import { useUser } from '@clerk/clerk-react';
import { Navigate } from 'react-router-dom';
import { useUserRole } from '../hooks/useUserRole.js';

// Component khusus untuk memproteksi educator auth page
const EducatorOnlyAccess = ({ children }) => {
  const { isSignedIn, isLoaded } = useUser();
  const { isEducator, organizationData, roleCalculationLoaded } = useUserRole();

  // Show loading while Clerk is initializing or role calculation is in progress
  if (!isLoaded || !roleCalculationLoaded) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
        <div className="ml-4 text-gray-600">Loading...</div>
      </div>
    );
  }

  // If user is signed in and is NOT an educator in edemy organization, redirect to student portal
  if (isSignedIn) {
    const hasEdemyMembership = organizationData?.some(
      membership => membership.organization.name.toLowerCase() === 'edemy'
    );
    
    if (!isEducator || !hasEdemyMembership) {
      console.log('🚨 EducatorOnlyAccess: Blocking non-educator or non-edemy member');
      console.log('Details:', {
        isEducator,
        hasEdemyMembership,
        organizationCount: organizationData?.length || 0
      });
      return <Navigate to="/" replace />;
    }
  }

  // If not signed in OR is educator in edemy organization, show the children (EducatorAuth component)
  return children;
};

export default EducatorOnlyAccess;
