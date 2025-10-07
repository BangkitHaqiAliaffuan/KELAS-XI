import React from 'react';
import { Routes, Route, useLocation } from 'react-router-dom';
import { AdminAuthProvider } from '../context/AdminAuthContext';
import AdminLoginPage from '../pages/AdminLoginPage';
import AdminLayout from '../components/AdminLayout';
import AdminDashboard from '../pages/admin/AdminDashboard';
import AdminOffices from '../pages/admin/AdminOffices';
import AdminTransactions from '../pages/admin/AdminTransactions';

const AdminRoutes = () => {
  const location = useLocation();
  
  React.useEffect(() => {
    // Effect to respond to location changes if needed
  }, [location.pathname]);
  
  return (
    <AdminAuthProvider>
      <Routes>
        <Route path="login" element={<AdminLoginPage />} />
        <Route path="" element={<AdminLayout />}>
          <Route index element={<AdminDashboard />} />
          <Route path="offices" element={<AdminOffices />} />
          <Route path="transactions" element={<AdminTransactions />} />
        </Route>
      </Routes>
    </AdminAuthProvider>
  );
};

export default AdminRoutes;
