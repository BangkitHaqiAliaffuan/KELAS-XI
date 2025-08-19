import { useUser } from '@clerk/clerk-react';
import { Navigate } from 'react-router-dom';
import { useUserRole } from '../hooks/useUserRole.js';

// Component khusus untuk memproteksi educator auth page
const EducatorOnlyAccess = ({ children }) => {
  const { isSignedIn, isLoaded } = useUser();
  const { isEducator } = useUserRole();

  // Show loading while Clerk is initializing
  if (!isLoaded) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  // If user is signed in and is NOT an educator, redirect to student portal
  if (isSignedIn && !isEducator) {
    return <Navigate to="/courses" replace />;
  }

  // If not signed in OR is educator, show the children (EducatorAuth component)
  return children;
};

export default EducatorOnlyAccess;
