import React, { useState, useEffect, useRef } from 'react';
import { NavLink, Outlet, Navigate, useNavigate } from 'react-router-dom';
import { useUser, useClerk } from '@clerk/clerk-react';
import { useUserRole } from '../../hooks/useUserRole.js';
import { assets } from '../../assets/assets.js';

// Import ClerkSyncWrapper untuk auto-sync
import ClerkSyncWrapper from '../../components/ClerkSyncWrapper.jsx';

const SidebarLink = ({ to, icon, label }) => {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        `flex items-center gap-3 px-4 py-3 rounded-md transition-colors ${
          isActive
            ? 'bg-indigo-50 text-indigo-600 border-l-4 border-indigo-500 pl-3'
            : 'text-gray-700 hover:bg-gray-50'
        }`
      }
      end
    >
      <img src={icon} alt="" className="h-5 w-5" />
      <span className="text-sm font-medium">{label}</span>
    </NavLink>
  );
};

const EducatorLayout = () => {
  const { isLoaded, user } = useUser();
  const { signOut } = useClerk();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [syncStatus, setSyncStatus] = useState('checking'); // checking, synced, error
  const dropdownRef = useRef(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  // Check if user is synced with MongoDB
  useEffect(() => {
    const checkSyncStatus = async () => {
      if (!user?.id) return;

      try {
        const response = await fetch(`http://localhost:5000/api/clerk/check-user/${user.id}`);
        const result = await response.json();
        
        if (result.exists) {
          setSyncStatus('synced');
        } else {
          setSyncStatus('error');
        }
      } catch (error) {
        console.error('Error checking sync status:', error);
        setSyncStatus('error');
      }
    };

    if (user?.id) {
      checkSyncStatus();
    }
  }, [user?.id]);

  // Loading state while Clerk is initializing
  if (!isLoaded) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  // Get user display information
  const getUserDisplayInfo = () => {
    if (!user) return { name: 'Guest', email: '', avatar: null };
    
    const name = user.fullName || 
                 `${user.firstName || ''} ${user.lastName || ''}`.trim() ||
                 user.username ||
                 user.primaryEmailAddress?.emailAddress?.split('@')[0] ||
                 'User';
    
    const email = user.primaryEmailAddress?.emailAddress || '';
    const avatar = user.imageUrl || user.profileImageUrl;
    
    return { name, email, avatar };
  };

  const { name, email, avatar } = getUserDisplayInfo();

  // Get sync status indicator
  const getSyncStatusIndicator = () => {
    switch (syncStatus) {
      case 'synced':
        return (
          <div className="flex items-center gap-1 text-xs text-green-600">
            <div className="w-2 h-2 bg-green-500 rounded-full"></div>
            Synced
          </div>
        );
      case 'error':
        return (
          <div className="flex items-center gap-1 text-xs text-red-600">
            <div className="w-2 h-2 bg-red-500 rounded-full"></div>
            Sync Error
          </div>
        );
      default:
        return (
          <div className="flex items-center gap-1 text-xs text-yellow-600">
            <div className="w-2 h-2 bg-yellow-500 rounded-full animate-pulse"></div>
            Checking...
          </div>
        );
    }
  };

  // Handle logout
  const handleLogout = async () => {
    try {
      if (user) {
        await signOut();
      }
      navigate('/');
    } catch (error) {
      console.error('Logout error:', error);
    }
  };

  return (
    <ClerkSyncWrapper>
      <div className="min-h-screen bg-gray-50">
        <div className="flex">
          {/* Sidebar */}
          <aside className="w-56 bg-white border-r min-h-screen pt-6">
            <div className="px-4 pb-4">
              <div className="flex items-center gap-2">
                <img src={assets.logo} alt="Edemy" className="h-7" />
              </div>
            </div>
            <nav className="space-y-1 px-2">
              <SidebarLink to="/dashboard/home" icon={assets.home_icon} label="Dashboard" />
              <SidebarLink to="/dashboard/add-course" icon={assets.add_icon} label="Add Course" />
              <SidebarLink to="/dashboard/my-courses" icon={assets.my_course_icon} label="My Courses" />
              <SidebarLink to="/dashboard/students" icon={assets.person_tick_icon} label="Student Enrolled" />
            </nav>
          </aside>

          {/* Main */}
          <div className="flex-1 min-h-screen">
          {/* Topbar */}
          <div className="flex items-center justify-between px-6 py-4 border-b bg-white">
            <div className="flex items-center gap-2">
              <img src={assets.logo} alt="Edemy" className="h-6" />
              <span className="text-sm text-gray-500">Dashboard</span>
            </div>
            <div className="relative" ref={dropdownRef}>
              <button
                onClick={() => setOpen((p) => !p)}
                className="flex items-center gap-3 rounded-full px-3 py-2 hover:bg-gray-50 transition-colors"
              >
                <div className="text-right">
                  <div className="text-sm font-medium text-gray-700">
                    Hi, {name}!
                  </div>
                  {email && (
                    <div className="text-xs text-gray-500">
                      {email}
                    </div>
                  )}
                </div>
                <div className="h-8 w-8 rounded-full overflow-hidden border-2 border-gray-200">
                  {avatar ? (
                    <img 
                      src={avatar} 
                      alt={name}
                      className="h-full w-full object-cover"
                      onError={(e) => {
                        // Fallback jika avatar gagal load
                        e.target.src = assets.user_icon;
                      }}
                    />
                  ) : (
                    <div className="h-full w-full bg-gray-100 grid place-items-center">
                      <img src={assets.user_icon} alt="user" className="h-4 w-4" />
                    </div>
                  )}
                </div>
              </button>
              {open && (
                <div className="absolute right-0 mt-2 w-48 rounded-md border bg-white shadow-lg z-10">
                  {user && (
                    <>
                      <div className="px-4 py-3 border-b">
                        <div className="text-sm font-medium text-gray-700">{name}</div>
                        {email && (
                          <div className="text-xs text-gray-500">{email}</div>
                        )}
                        <div className="text-xs text-blue-600 mt-1">
                          ID: {user.id.slice(0, 8)}...
                        </div>
                        <div className="mt-2">
                          {getSyncStatusIndicator()}
                        </div>
                      </div>
                      <button 
                        onClick={() => {
                          navigate('/dashboard/profile');
                          setOpen(false);
                        }}
                        className="w-full text-left px-4 py-2 text-sm hover:bg-gray-50"
                      >
                        My Profile
                      </button>
                      <button 
                        onClick={() => {
                          navigate('/dashboard/settings');
                          setOpen(false);
                        }}
                        className="w-full text-left px-4 py-2 text-sm hover:bg-gray-50"
                      >
                        Settings
                      </button>
                      <hr className="my-1" />
                    </>
                  )}
                  <button 
                    onClick={() => {
                      handleLogout();
                      setOpen(false);
                    }}
                    className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                  >
                    Logout
                  </button>
                </div>
              )}
            </div>
          </div>
          
            {/* Content */}
            <div className="p-6">
              <Outlet />
            </div>
          </div>
        </div>
      </div>
    </ClerkSyncWrapper>
  );
};

export default EducatorLayout;
