import React from 'react';
import { SignInButton, SignUpButton } from '@clerk/clerk-react';
import { useRoleAssignment } from '../hooks/useUserRole.js';

export const StudentSignInButton = ({ children, ...props }) => {
  useRoleAssignment('student');
  
  return (
    <SignInButton 
      mode="modal" 
      forceRedirectUrl="/courses"
      signUpForceRedirectUrl="/courses"
      {...props}
    >
      {children}
    </SignInButton>
  );
};

export const StudentSignUpButton = ({ children, ...props }) => {
  useRoleAssignment('student');
  
  return (
    <SignUpButton 
      mode="modal" 
      forceRedirectUrl="/courses"
      signInForceRedirectUrl="/courses"
      {...props}
    >
      {children}
    </SignUpButton>
  );
};
