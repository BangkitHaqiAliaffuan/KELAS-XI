import { useUser, useClerk, useOrganizationList } from '@clerk/clerk-react';
import { useEffect, useState, useCallback } from 'react';
import { isEducatorRole, hasEducatorAccess, getUserOrganizationRole } from '../utils/roleHelpers.js';

// Hook untuk mengatur role user dengan Organization support
export const useUserRole = () => {
  const { user, isLoaded } = useUser();
  const { userMemberships } = useOrganizationList({
    userMemberships: {
      infinite: true,
    },
  });
  const clerk = useClerk();

  // Use state untuk menyimpan hasil calculation
  const [roleData, setRoleData] = useState({
    isEducator: false,
    isStudent: false,
    currentRole: null,
    dataLoaded: false
  });

  // Calculate roles dengan useEffect langsung
  useEffect(() => {
    console.log('🧮 useEffect calculateRoles triggered');
    
    // Wait for both user and organization data to be loaded
    if (!isLoaded || !userMemberships?.isLoaded) {
      console.log('⏳ Data not fully loaded yet');
      return; // Don't update state if still loading
    }

    console.log('✅ Data loaded, calculating roles...');

    // Check if user has any organization memberships at all
    const orgData = userMemberships?.data || [];
    if (orgData.length === 0) {
      console.log('✅ No organization memberships found - Student only');
      setRoleData({
        isEducator: false,
        isStudent: true,
        currentRole: 'student',
        dataLoaded: true
      });
      return;
    }

    // Look for edemy organization membership
    const educatorMembership = orgData.find(
      membership => membership.organization.name.toLowerCase() === 'edemy'
    );

    if (!educatorMembership) {
      console.log('✅ Has organizations but not edemy member - Student');
      setRoleData({
        isEducator: false,
        isStudent: true,
        currentRole: 'student',
        dataLoaded: true
      });
      return;
    }

    // Has edemy membership, check if educator role
    const role = educatorMembership.role;
    const hasEducatorRole = isEducatorRole(role);
    
    if (hasEducatorRole) {
      console.log('✅ Has educator role - Educator');
      setRoleData({
        isEducator: true,
        isStudent: false,
        currentRole: 'educator',
        dataLoaded: true
      });
      return;
    }

    // Has edemy membership but not educator role = still student
    console.log('✅ Has edemy membership but not educator role - Student');
    setRoleData({
      isEducator: false,
      isStudent: true,
      currentRole: 'student',
      dataLoaded: true
    });
  }, [isLoaded, userMemberships?.isLoaded, userMemberships?.data]);

  const setUserRole = async (role) => {
    if (!user) return;
    
    try {
      await user.update({
        publicMetadata: {
          ...user.publicMetadata,
          role: role
        }
      });
      
      // Reload user to get updated metadata
      await user.reload();
    } catch (error) {
      console.error('Error setting user role:', error);
    }
  };

  const getUserRole = () => {
    console.log('📋 getUserRole called, returning cached result:', roleData.currentRole);
    return roleData.currentRole;
  };

  const isEducator = () => {
    console.log('🔍 isEducator called, returning cached result:', roleData.isEducator);
    return roleData.isEducator;
  };

  const isStudent = () => {
    console.log('👥 isStudent called, returning cached result:', roleData.isStudent);
    return roleData.isStudent;
  };

  const getOrganizationRole = () => {
    if (!userMemberships?.data) return null;
    return getUserOrganizationRole(userMemberships.data);
  };

  const isOrganizationMember = () => {
    if (userMemberships?.data) {
      return userMemberships.data.some(
        membership => membership.organization.name.toLowerCase() === 'edemy'
      );
    }
    return false;
  };

  const refreshOrganizationData = async () => {
    try {
      if (user) {
        await user.reload();
        console.log('🔄 Organization data refreshed');
      }
    } catch (error) {
      console.error('Error refreshing organization data:', error);
    }
  };

  return {
    user,
    isLoaded,
    setUserRole,
    getUserRole,
    isEducator,
    isStudent,
    currentRole: roleData.currentRole,
    organizationRole: getOrganizationRole(),
    isOrganizationMember: isOrganizationMember(),
    refreshOrganizationData,
    // Debug info
    organizationData: userMemberships?.data,
    organizationLoaded: userMemberships?.isLoaded,
    // Additional debug info
    roleCalculationLoaded: roleData.dataLoaded
  };
};

// Hook untuk auto-assign role based on sign-up context
export const useRoleAssignment = (intendedRole = 'student') => {
  const { user, isLoaded } = useUser();
  const { setUserRole, getUserRole } = useUserRole();

  useEffect(() => {
    const assignRole = async () => {
      if (isLoaded && user && !getUserRole()) {
        // Only assign role if user doesn't have one yet (first time signup)
        await setUserRole(intendedRole);
      }
    };

    assignRole();
  }, [isLoaded, user, intendedRole]);
};
