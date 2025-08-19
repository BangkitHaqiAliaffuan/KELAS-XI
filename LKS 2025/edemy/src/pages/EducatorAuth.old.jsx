import React, { useEffect } from 'react';
import { SignInButton, SignUpButton, useUser } from '@clerk/clerk-react';
import { Navigate } from 'react-router-dom';
import { BookOpen, Users, Award, TrendingUp } from 'lucide-react';
import { useRoleAssignment } from '../hooks/useUserRole.js';

const EducatorAuth = () => {
  const { isSignedIn, isLoaded, user } = useUser();
  
  // Use role assignment hook for educator
  useRoleAssignment('educator');

  // If already signed in, redirect to dashboard
  if (isLoaded && isSignedIn) {
    return <Navigate to="/educator/dashboard" replace />;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <div className="mx-auto h-12 w-12 bg-blue-600 rounded-lg flex items-center justify-center">
            <BookOpen className="h-8 w-8 text-white" />
          </div>
          <h2 className="mt-6 text-3xl font-extrabold text-gray-900">
            Educator Portal
          </h2>
          <p className="mt-2 text-sm text-gray-600">
            Join thousands of educators creating amazing courses
          </p>
        </div>

        <div className="bg-white py-8 px-6 shadow-lg rounded-lg space-y-6">
          <div className="space-y-4">
            <div className="flex items-center space-x-3">
              <Users className="h-5 w-5 text-blue-600" />
              <span className="text-sm text-gray-700">Teach students worldwide</span>
            </div>
            <div className="flex items-center space-x-3">
              <Award className="h-5 w-5 text-blue-600" />
              <span className="text-sm text-gray-700">Earn from your expertise</span>
            </div>
            <div className="flex items-center space-x-3">
              <TrendingUp className="h-5 w-5 text-blue-600" />
              <span className="text-sm text-gray-700">Track your success</span>
            </div>
          </div>

          <div className="space-y-4">
            <SignInButton 
              mode="modal" 
              forceRedirectUrl="/educator/dashboard"
              signUpForceRedirectUrl="/educator/dashboard"
            >
              <button className="w-full flex justify-center py-3 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors">
                Login as Educator
              </button>
            </SignInButton>

            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-gray-300" />
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-white text-gray-500">New to teaching?</span>
              </div>
            </div>

            <SignUpButton 
              mode="modal" 
              forceRedirectUrl="/educator/dashboard"
              signInForceRedirectUrl="/educator/dashboard"
            >
              <button className="w-full flex justify-center py-3 px-4 border-2 border-blue-600 rounded-md shadow-sm text-sm font-medium text-blue-600 bg-white hover:bg-blue-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors">
                Sign Up as Educator
              </button>
            </SignUpButton>
          </div>

          <div className="text-center">
            <a 
              href="/" 
              className="text-sm text-blue-600 hover:text-blue-500 transition-colors"
            >
              ← Back to main site
            </a>
          </div>
        </div>

        <div className="text-center">
          <p className="text-xs text-gray-500">
            By signing up, you agree to our Terms of Service and Privacy Policy
          </p>
        </div>
      </div>
    </div>
  );
};

export default EducatorAuth;
