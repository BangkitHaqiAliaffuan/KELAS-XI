// Helper functions untuk role checking
// Mengatasi format role Clerk yang menggunakan "org:" prefix

/**
 * Check if a role is educator role
 * Handles both "educator", "org:educator", "admin", "org:admin" formats
 */
export const isEducatorRole = (role) => {
  if (!role) return false;
  
  const normalizedRole = role.toLowerCase();
  return normalizedRole === 'educator' || 
         normalizedRole === 'org:educator' || 
         normalizedRole === 'admin' || 
         normalizedRole === 'org:admin';
};

/**
 * Check if a role is admin role
 * Handles both "admin" and "org:admin" formats
 */
export const isAdminRole = (role) => {
  if (!role) return false;
  
  const normalizedRole = role.toLowerCase();
  return normalizedRole === 'admin' || normalizedRole === 'org:admin';
};

/**
 * Get clean role name without org: prefix
 */
export const getCleanRoleName = (role) => {
  if (!role) return null;
  
  return role.startsWith('org:') ? role.substring(4) : role;
};

/**
 * Check if user has educator access based on organization membership
 */
export const hasEducatorAccess = (organizationMemberships, organizationName = 'edemy') => {
  // Explicit checks for empty or invalid data
  if (!organizationMemberships || !Array.isArray(organizationMemberships) || organizationMemberships.length === 0) {
    console.log('📋 hasEducatorAccess: No organization memberships or empty array');
    return false;
  }
  
  console.log('📋 hasEducatorAccess: Checking', organizationMemberships.length, 'memberships for', organizationName);
  
  const membership = organizationMemberships.find(
    membership => membership.organization.name.toLowerCase() === organizationName.toLowerCase()
  );
  
  if (!membership) {
    console.log('📋 hasEducatorAccess: No membership found for', organizationName);
    return false;
  }
  
  const hasRole = isEducatorRole(membership.role);
  console.log('📋 hasEducatorAccess: Membership found, role:', membership.role, 'hasEducatorRole:', hasRole);
  return hasRole;
};

/**
 * Get user's role in organization
 */
export const getUserOrganizationRole = (organizationMemberships, organizationName = 'edemy') => {
  if (!organizationMemberships || !Array.isArray(organizationMemberships)) {
    return null;
  }
  
  const membership = organizationMemberships.find(
    membership => membership.organization.name.toLowerCase() === organizationName.toLowerCase()
  );
  
  return membership ? membership.role : null;
};
