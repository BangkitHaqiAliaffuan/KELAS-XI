import { useUser } from '@clerk/clerk-react';

const RoleBasedAccess = ({ allowedRoles = [], children, fallback = null }) => {
  const { user, isLoaded } = useUser();

  if (!isLoaded) {
    return <div className="animate-pulse bg-gray-200 rounded h-4 w-16"></div>;
  }

  if (!user) {
    return fallback;
  }

  const userRole = user?.publicMetadata?.role || 'student';
  
  if (allowedRoles.length === 0 || allowedRoles.includes(userRole)) {
    return children;
  }

  return fallback;
};

export default RoleBasedAccess;
