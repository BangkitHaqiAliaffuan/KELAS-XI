import React, { useState } from 'react';
import { NavLink, Outlet, Navigate, useNavigate } from 'react-router-dom';
import { useUser, useClerk } from '@clerk/clerk-react';
import { useUserRole } from '../../hooks/useUserRole.js';
import { assets } from '../../assets/assets.js';

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
                  <button 
                    onClick={handleLogout}
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
  );
};

export default EducatorLayout;
