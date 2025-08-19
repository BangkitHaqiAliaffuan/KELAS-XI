import React from 'react';
import { SignInButton, useUser } from '@clerk/clerk-react';
import { Navigate } from 'react-router-dom';
import { GraduationCap, Users, BookOpen, BarChart } from 'lucide-react';
import { assets } from '../assets/assets.js';
import { useUserRole } from '../hooks/useUserRole.js';

const EducatorAuth = () => {
  const { isSignedIn, isLoaded } = useUser();
  const { isEducator, organizationMember } = useUserRole();

  // Redirect if already signed in as educator
  if (isLoaded && isSignedIn && isEducator) {
    return <Navigate to="/educator/dashboard" replace />;
  }

  // Redirect if signed in but not an educator (student)
  if (isLoaded && isSignedIn && !isEducator) {
    return <Navigate to="/courses" replace />;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-white rounded-2xl shadow-xl overflow-hidden">
        {/* Header */}
        <div className="bg-gradient-to-r from-indigo-600 to-purple-600 px-8 py-6">
          <div className="flex items-center justify-center mb-4">
            <img 
              src={assets.logo} 
              alt="Edemy" 
              className="h-8 w-auto"
            />
          </div>
          <h1 className="text-2xl font-bold text-white text-center">
            Educator Portal
          </h1>
          <p className="text-indigo-100 text-center mt-2">
            Sign in to manage your courses
          </p>
        </div>

        {/* Features */}
        <div className="px-8 py-6">
          <div className="grid grid-cols-2 gap-4 mb-6">
            <div className="text-center p-3 bg-blue-50 rounded-lg">
              <BookOpen className="h-8 w-8 text-blue-600 mx-auto mb-2" />
              <p className="text-sm font-medium text-gray-700">Manage Courses</p>
            </div>
            <div className="text-center p-3 bg-green-50 rounded-lg">
              <Users className="h-8 w-8 text-green-600 mx-auto mb-2" />
              <p className="text-sm font-medium text-gray-700">Student Management</p>
            </div>
            <div className="text-center p-3 bg-purple-50 rounded-lg">
              <BarChart className="h-8 w-8 text-purple-600 mx-auto mb-2" />
              <p className="text-sm font-medium text-gray-700">Analytics</p>
            </div>
            <div className="text-center p-3 bg-orange-50 rounded-lg">
              <GraduationCap className="h-8 w-8 text-orange-600 mx-auto mb-2" />
              <p className="text-sm font-medium text-gray-700">Teaching Tools</p>
            </div>
          </div>

          {/* Organization Notice */}
          <div className="bg-amber-50 border border-amber-200 rounded-lg p-4 mb-6">
            <div className="flex items-start">
              <div className="flex-shrink-0">
                <Users className="h-5 w-5 text-amber-600" />
              </div>
              <div className="ml-3">
                <h3 className="text-sm font-medium text-amber-800">
                  Educator Access Only
                </h3>
                <p className="text-sm text-amber-700 mt-1">
                  Only educators invited to the Edemy organization can access this portal. 
                  Contact admin for educator access.
                </p>
              </div>
            </div>
          </div>

          {/* Sign In Button */}
          <SignInButton 
            mode="modal"
            forceRedirectUrl="/educator/dashboard"
            fallbackRedirectUrl="/educator"
          >
            <button className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 px-4 rounded-lg transition duration-200 flex items-center justify-center space-x-2">
              <GraduationCap className="h-5 w-5" />
              <span>Sign In as Educator</span>
            </button>
          </SignInButton>

          {/* No Register Option - Organization Only */}
          <div className="mt-4 text-center">
            <p className="text-sm text-gray-600">
              Don't have access? 
              <span className="block text-indigo-600 font-medium mt-1">
                Contact admin for educator invitation
              </span>
            </p>
          </div>
          
          <div className="mt-2 text-center">
            <p className="text-sm text-gray-600">
              Looking for student access? 
              <a href="/" className="text-indigo-600 hover:text-indigo-700 font-medium ml-1">
                Go to main site
              </a>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EducatorAuth;
