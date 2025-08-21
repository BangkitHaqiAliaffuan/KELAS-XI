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
    console.log('Current state:', {
      isLoaded,
      userMembershipsLoaded: userMemberships?.isLoaded,
      userEmail: user?.emailAddresses[0]?.emailAddress
    });
    
    // Wait for user data to be loaded first
    if (!isLoaded) {
      console.log('⏳ User data not loaded yet');
      return;
    }

    // Add timeout logic untuk organization loading
    // Jika organization data tidak load dalam waktu tertentu, assume user adalah student
    const organizationTimeout = setTimeout(() => {
      if (!userMemberships?.isLoaded) {
        console.log('⏰ Organization loading timeout - Assuming student role (for non-educators)');
        setRoleData({
          isEducator: false,
          isStudent: true,
          currentRole: 'student',
          dataLoaded: true
        });
      }
    }, 2500); // 2.5 detik timeout - balance antara kecepatan dan akurasi

    // If organization data is loaded, clear timeout and proceed
    if (userMemberships?.isLoaded) {
      clearTimeout(organizationTimeout);
      console.log('✅ Organization data loaded, calculating roles...');

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
        return () => clearTimeout(organizationTimeout);
      }

      // Look for edemy organization membership
      console.log('🔍 Searching for edemy organization membership...');
      console.log('Available organizations:', orgData.map(m => ({ 
        name: m.organization.name, 
        role: m.role,
        id: m.organization.id 
      })));
      
      const educatorMembership = orgData.find(
        membership => membership.organization.name.toLowerCase() === 'edemy'
      );

      if (!educatorMembership) {
        console.log('❌ No edemy organization membership found - User is Student');
        setRoleData({
          isEducator: false,
          isStudent: true,
          currentRole: 'student',
          dataLoaded: true
        });
        return () => clearTimeout(organizationTimeout);
      }

      console.log('✅ Found edemy membership:', {
        orgName: educatorMembership.organization.name,
        role: educatorMembership.role,
        orgId: educatorMembership.organization.id
      });

      // Has edemy membership, check if educator role
      const role = educatorMembership.role;
      const hasEducatorRole = isEducatorRole(role);
      
      console.log('🎭 Checking educator role:', {
        rawRole: role,
        hasEducatorRole,
        validEducatorRoles: ['educator', 'org:educator', 'admin', 'org:admin']
      });
      
      if (hasEducatorRole) {
        console.log('✅ User has educator role in edemy organization - Access GRANTED');
        setRoleData({
          isEducator: true,
          isStudent: false,
          currentRole: 'educator',
          dataLoaded: true
        });
        return () => clearTimeout(organizationTimeout);
      }

      // Has edemy membership but not educator role = still student
      console.log('❌ User has edemy membership but not educator role - Student access only');
      console.log('Role details:', { role, hasEducatorRole });
      setRoleData({
        isEducator: false,
        isStudent: true,
        currentRole: 'student',
        dataLoaded: true
      });
      return () => clearTimeout(organizationTimeout);
    } else {
      // Organization data not loaded yet, but user is loaded
      // Don't set final role yet - wait for organization data
      console.log('⏳ Organization data still loading, waiting for complete data before setting role');
      setRoleData({
        isEducator: false,
        isStudent: true,
        currentRole: 'student',
        dataLoaded: false // Keep as false until we have complete organization data
      });
      return () => clearTimeout(organizationTimeout);
    }
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
