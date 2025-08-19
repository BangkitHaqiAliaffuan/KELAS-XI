import { useUser, useOrganizationList } from '@clerk/clerk-react';
import { useEffect } from 'react';

// Hook untuk auto-join organization "edemy" sebagai member setelah signup
export const useAutoJoinOrganization = () => {
  const { user, isLoaded } = useUser();
  const { createOrganization, userMemberships } = useOrganizationList({
    userMemberships: {
      infinite: true,
    },
  });

  useEffect(() => {
    const joinOrganization = async () => {
      if (!isLoaded || !user) return;

      // Check if user is already member of "edemy" organization
      const isAlreadyMember = userMemberships?.data?.some(
        membership => membership.organization.name.toLowerCase() === 'edemy'
      );

      if (isAlreadyMember) {
        console.log('User already member of edemy organization');
        return;
      }

      // Check if this is a new user (recently signed up)
      const userCreatedAt = new Date(user.createdAt);
      const now = new Date();
      const diffMinutes = (now - userCreatedAt) / (1000 * 60);

      // If user was created less than 5 minutes ago, try to join organization
      if (diffMinutes < 5) {
        try {
          // Note: This approach won't work directly since users can't join organizations without invitation
          // Instead, we'll set metadata to indicate they should be added to organization
          await user.update({
            publicMetadata: {
              ...user.publicMetadata,
              role: 'student',
              requestOrganizationJoin: true,
              joinRequestedAt: new Date().toISOString()
            }
          });
          
          console.log('User marked for organization membership');
        } catch (error) {
          console.error('Failed to mark user for organization:', error);
        }
      }
    };

    joinOrganization();
  }, [isLoaded, user, userMemberships]);

  return null;
};
