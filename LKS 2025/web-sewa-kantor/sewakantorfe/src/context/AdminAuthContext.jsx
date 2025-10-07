import React, { createContext, useState, useContext, useEffect } from 'react';
import { adminAuthApi } from '../services/api';

const AdminAuthContext = createContext(null);

export const AdminAuthProvider = ({ children }) => {
  const [admin, setAdmin] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    try {
      const token = localStorage.getItem('admin_token');
      if (token) {
        const response = await adminAuthApi.me();
        if (response.success) {
          setAdmin(response.data);
        } else {
          localStorage.removeItem('admin_token');
        }
      }
    } catch (error) {
      console.error('AdminAuthContext: Auth check failed:', error);
      localStorage.removeItem('admin_token');
    } finally {
      setLoading(false);
    }
  };

  const login = async (credentials) => {
    try {
      const response = await adminAuthApi.login(credentials);
      if (response.success) {
        const { token, admin } = response.data;
        localStorage.setItem('admin_token', token);
        setAdmin(admin);
        return { success: true, admin };
      }
      return { success: false, message: response.message };
    } catch (error) {
      console.error('AdminAuthContext: Login error:', error);
      return {
        success: false,
        message: error.response?.data?.message || 'Login failed'
      };
    }
  };

  const logout = async () => {
    try {
      await adminAuthApi.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      localStorage.removeItem('admin_token');
      setAdmin(null);
    }
  };

  const updateProfile = async (profileData) => {
    try {
      const response = await adminAuthApi.updateProfile(profileData);
      if (response.success) {
        setAdmin(response.data);
        return { success: true };
      }
      return { success: false, message: response.message };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || 'Update failed'
      };
    }
  };

  const value = {
    admin,
    loading,
    login,
    logout,
    updateProfile,
    isAuthenticated: !!admin,
  };

  return (
    <AdminAuthContext.Provider value={value}>
      {children}
    </AdminAuthContext.Provider>
  );
};

export const useAdminAuth = () => {
  const context = useContext(AdminAuthContext);
  if (!context) {
    throw new Error('useAdminAuth must be used within AdminAuthProvider');
  }
  return context;
};

export default AdminAuthContext;
