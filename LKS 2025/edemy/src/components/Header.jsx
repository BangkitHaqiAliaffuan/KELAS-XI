import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Search, Menu, X, User, BookOpen, LogOut } from 'lucide-react';
import { UserButton, useUser, useClerk, SignInButton, SignUpButton } from '@clerk/clerk-react';
import { StudentSignInButton, StudentSignUpButton } from './StudentAuth.jsx';
import { useApp } from '../context/AppContext.jsx';
import { assets } from '../assets/assets.js';
import RoleBasedAccess from './RoleBasedAccess.jsx';
import RoleSelectionModal from './RoleSelectionModal.jsx';


const Header = () => {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isSearchOpen, setIsSearchOpen] = useState(false);
  const [showRoleModal, setShowRoleModal] = useState(false);
  const { searchQuery, searchCourses } = useApp();
  const { isSignedIn, user, isLoaded } = useUser();
  const { signOut } = useClerk();
  const location = useLocation();

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      searchCourses(searchQuery);
      // Navigate to courses page if not already there
      if (location.pathname !== '/courses') {
        window.location.href = '/courses';
      }
    }
  };

  // Navigation links for different user types
  const getNavigationLinks = () => {
    const baseLinks = [
      { name: 'Home', path: '/', icon: null },
      { name: 'Courses', path: '/courses', icon: BookOpen },
      { name: 'Dashboard', path: '/dashboard', icon: User },
    ];
    
    return baseLinks;
  };

  // Authentication nav items based on user state and role
  const getAuthNavItems = () => {
    if (!isLoaded || !isSignedIn) {
      return [];
    }
    
    const userRole = user?.publicMetadata?.role || 'student';
    
    if (userRole === 'org:educator') {
      return [
        { name: 'Dashboard', path: '/educator/dashboard', icon: GraduationCap },
        { name: 'My Courses', path: '/educator/my-courses', icon: BookOpen },
        { name: 'Add Course', path: '/educator/add-course', icon: BookOpen },
      ];
    } else {
      return [
        { name: 'My Learning', path: '/my-courses', icon: User },
      ];
    }
  };

  return (
    <header className="bg-white shadow-sm border-b border-gray-100 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2">
            <img src={assets.logo} alt="Edemy" className="h-8 w-auto" />
          </Link>

          {/* Desktop Navigation */}

          {/* Search Bar - Desktop */}
          <div className="hidden md:flex items-center flex-1 max-w-md mx-8">
            <form onSubmit={handleSearchSubmit} className="w-full">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                <input
                  type="text"
                  placeholder="Search for courses"
                  value={searchQuery}
                  onChange={(e) => searchCourses(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                />
              </div>
            </form>
          </div>

          {/* Right side buttons - Desktop */}
          <div className="hidden md:flex items-center space-x-4">
            {getNavigationLinks().map((link) => (
              <Link
                key={link.name}
                to={link.path}
                className="text-sm font-medium text-gray-700 hover:text-blue-600 transition-colors"
              >
                {link.name}
              </Link>
            ))}
            
            {getAuthNavItems().map((link) => (
              <Link
                key={link.name}
                to={link.path}
                className="text-sm font-medium text-gray-700 hover:text-blue-600 transition-colors"
              >
                {link.name}
              </Link>
            ))}

            {!isLoaded ? (
              <div className="flex space-x-2">
                <div className="w-16 h-8 bg-gray-200 animate-pulse rounded"></div>
                <div className="w-24 h-8 bg-gray-200 animate-pulse rounded"></div>
              </div>
            ) : !isSignedIn ? (
              <div className="flex space-x-2">
                <SignInButton mode="modal" afterSignInUrl="/courses">
                  <button className="text-sm font-medium text-gray-700 hover:text-blue-600 transition-colors">
                    Student Login
                  </button>
                </SignInButton>
                <SignUpButton mode="modal" afterSignUpUrl="/courses">
                  <button className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors">
                    Student Sign Up
                  </button>
                </SignUpButton>
              </div>
            ) : (
              <div className="flex items-center space-x-4">
                <RoleBasedAccess allowedRoles={['educator']}>
                  <span className="text-sm text-green-600 font-medium">
                    Educator
                  </span>
                </RoleBasedAccess>
                <RoleBasedAccess allowedRoles={['student']}>
                  <span className="text-sm text-blue-600 font-medium">
                    Student
                  </span>
                </RoleBasedAccess>
                <span className="text-sm text-gray-600">
                  {user?.firstName || user?.username || 'User'}
                </span>
                <UserButton 
                  appearance={{
                    elements: {
                      avatarBox: "w-8 h-8"
                    }
                  }}
                />
              </div>
            )}
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden flex items-center space-x-2">
            <button
              onClick={() => setIsSearchOpen(!isSearchOpen)}
              className="p-2 rounded-md text-gray-400 hover:text-gray-500"
            >
              <Search className="h-5 w-5" />
            </button>
            <button
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
              className="p-2 rounded-md text-gray-400 hover:text-gray-500"
            >
              {isMobileMenuOpen ? (
                <X className="h-6 w-6" />
              ) : (
                <Menu className="h-6 w-6" />
              )}
            </button>
          </div>
        </div>

        {/* Mobile Search */}
        {isSearchOpen && (
          <div className="md:hidden py-4 border-t border-gray-100">
            <form onSubmit={handleSearchSubmit}>
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                <input
                  type="text"
                  placeholder="Search for courses"
                  value={searchQuery}
                  onChange={(e) => searchCourses(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
                />
              </div>
            </form>
          </div>
        )}

        {/* Mobile Navigation */}
        {isMobileMenuOpen && (
          <div className="md:hidden">
            <div className="px-2 pt-2 pb-3 space-y-1 border-t border-gray-100">
              {getNavigationLinks().map((link) => {
                const Icon = link.icon;
                return (
                  <Link
                    key={link.path}
                    to={link.path}
                    className={`flex items-center space-x-3 px-3 py-2 rounded-md text-base font-medium transition-colors ${
                      location.pathname === link.path
                        ? 'text-blue-600 bg-blue-50'
                        : 'text-gray-700 hover:text-blue-600 hover:bg-gray-50'
                    }`}
                    onClick={() => setIsMobileMenuOpen(false)}
                  >
                    <Icon className="h-5 w-5" />
                    <span>{link.name}</span>
                  </Link>
                );
              })}

              {getAuthNavItems().map((link) => {
                const Icon = link.icon;
                return (
                  <Link
                    key={link.path}
                    to={link.path}
                    className={`flex items-center space-x-3 px-3 py-2 rounded-md text-base font-medium transition-colors ${
                      location.pathname === link.path
                        ? 'text-blue-600 bg-blue-50'
                        : 'text-gray-700 hover:text-blue-600 hover:bg-gray-50'
                    }`}
                    onClick={() => setIsMobileMenuOpen(false)}
                  >
                    <Icon className="h-5 w-5" />
                    <span>{link.name}</span>
                  </Link>
                );
              })}

              {/* Mobile Authentication */}
              {!isLoaded ? (
                <div className="px-3 py-2">
                  <div className="w-full h-8 bg-gray-200 animate-pulse rounded mb-2"></div>
                  <div className="w-full h-8 bg-gray-200 animate-pulse rounded"></div>
                </div>
              ) : !isSignedIn ? (
                <div className="px-3 py-2 space-y-2">
                  <SignInButton mode="modal" afterSignInUrl="/courses">
                    <button className="w-full text-left px-3 py-2 text-base font-medium text-gray-700 hover:text-blue-600 hover:bg-gray-50 rounded-md transition-colors">
                       Login
                    </button>
                  </SignInButton>
                  <SignUpButton mode="modal" afterSignUpUrl="/courses">
                    <button className="w-full text-left px-3 py-2 text-base font-medium bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors">
                       Sign Up
                    </button>
                  </SignUpButton>
                </div>
              ) : (
                <div className="px-3 py-2 border-t border-gray-100">
                  <div className="flex items-center space-x-3 py-2">
                    <UserButton 
                      appearance={{
                        elements: {
                          avatarBox: "w-8 h-8"
                        }
                      }}
                    />
                    <div className="flex flex-col">
                      <span className="text-base font-medium text-gray-700">
                        {user?.firstName || user?.username || 'User'}
                      </span>
                      <RoleBasedAccess allowedRoles={['educator']}>
                        <span className="text-sm text-green-600">Educator</span>
                      </RoleBasedAccess>
                      <RoleBasedAccess allowedRoles={['student']}>
                        <span className="text-sm text-blue-600">Student</span>
                      </RoleBasedAccess>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
      
      <RoleSelectionModal 
        isOpen={showRoleModal} 
        onClose={() => setShowRoleModal(false)} 
      />
    </header>
  );
};

export default Header;
