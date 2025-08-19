import React, { useState } from 'react';
import { NavLink, Outlet, Navigate } from 'react-router-dom';
import { useUser } from '@clerk/clerk-react';
import { useUserRole } from '../../hooks/useUserRole.js';
import { assets, dummyEducatorData } from '../../assets/assets.js';

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
  // Extra security check - double protection
  const { isLoaded } = useUser();
  const { isEducator } = useUserRole();

  // If not loaded yet, show loading
  if (!isLoaded) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  // If not educator, redirect immediately
  if (!isEducator) {
    console.log('🚨 SECURITY BREACH ATTEMPT: Non-educator trying to access EducatorLayout');
    return <Navigate to="/courses" replace />;
  }

  const [open, setOpen] = useState(false);

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="flex">
        {/* Sidebar */}
        <aside className="w-56 bg-white border-r min-h-screen pt-6">
          <div className="px-4 pb-4">
            <div className="flex items-center gap-2">
              <img src={assets.logo} alt="Edemy" className="h-7" />
              <span className="text-lg font-semibold">Edemy</span>
            </div>
          </div>
          <nav className="space-y-1 px-2">
            <SidebarLink to="/educator/dashboard" icon={assets.home_icon} label="Dashboard" />
            <SidebarLink to="/educator/add-course" icon={assets.add_icon} label="Add Course" />
            <SidebarLink to="/educator/my-courses" icon={assets.my_course_icon} label="My Courses" />
            <SidebarLink to="/educator/students" icon={assets.person_tick_icon} label="Student Enrolled" />
          </nav>
        </aside>

        {/* Main */}
        <div className="flex-1 min-h-screen">
          {/* Topbar */}
          <div className="flex items-center justify-between px-6 py-4 border-b bg-white">
            <div className="flex items-center gap-2">
              <img src={assets.logo} alt="Edemy" className="h-6" />
              <span className="font-semibold">Edemy</span>
            </div>
            <div className="relative">
              <button
                onClick={() => setOpen((p) => !p)}
                className="flex items-center gap-3 rounded-full px-3 py-2 hover:bg-gray-50"
              >
                <span className="text-sm text-gray-600">Hi! Educator</span>
                <div className="h-8 w-8 rounded-full bg-gray-100 grid place-items-center border">
                  <img src={assets.user_icon} alt="user" className="h-4 w-4" />
                </div>
              </button>
              {open && (
                <div className="absolute right-0 mt-2 w-44 rounded-md border bg-white shadow-lg z-10">
                  <button className="w-full text-left px-4 py-2 text-sm hover:bg-gray-50">My Profile</button>
                  <button className="w-full text-left px-4 py-2 text-sm hover:bg-gray-50">Logout</button>
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
  );
};
        </div>
      </div>
    </div>
  );
};

export default Layout;


