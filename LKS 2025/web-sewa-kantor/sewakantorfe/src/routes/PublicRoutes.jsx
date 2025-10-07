import React from 'react';
import { Routes, Route, useLocation, Navigate } from 'react-router-dom';
import { AuthProvider } from '../context/AuthContext';
import PublicLayout from '../components/PublicLayout';
import HomePage from '../pages/HomePage';
import OfficesPage from '../pages/OfficesPage';
import OfficeDetailPage from '../pages/OfficeDetailPage';
import BookingPage from '../pages/BookingPage';
import LoginPage from '../pages/LoginPage';
import RegisterPage from '../pages/RegisterPage';
import DashboardPage from '../pages/DashboardPage';

const PublicRoutes = () => {
  const location = useLocation();
  
  // CRITICAL: Prevent public routes from handling /admin paths
  React.useEffect(() => {
    if (location.pathname.startsWith('/admin')) {
      console.error('❌ PublicRoutes: Should NOT handle /admin paths! Path:', location.pathname);
    } else {
      console.log('✅ PublicRoutes: Rendering for path:', location.pathname);
    }
  }, [location.pathname]);
  
  // Early return if admin path - DO NOT RENDER ANYTHING
  if (location.pathname.startsWith('/admin')) {
    return null;
  }
  
  return (
    <AuthProvider>
      <Routes>
        <Route element={<PublicLayout />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/offices" element={<OfficesPage />} />
          <Route path="/offices/:id" element={<OfficeDetailPage />} />
          <Route path="/booking" element={<BookingPage />} />
          <Route path="/booking/:officeId" element={<BookingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/dashboard" element={<DashboardPage />} />
        </Route>
      </Routes>
    </AuthProvider>
  );
};

export default PublicRoutes;
