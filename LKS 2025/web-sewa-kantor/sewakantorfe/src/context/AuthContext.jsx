import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../services/apiService';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Check if user is authenticated on app load
  useEffect(() => {
    const token = localStorage.getItem('auth_token');
    if (token) {
      fetchUser();
    } else {
      setLoading(false);
    }
  }, []);

  const fetchUser = async () => {
    try {
  const response = await authApi.user();
      
      if (response.data.success) {
        setUser(response.data.data);
        setIsAuthenticated(true);
      } else {
        throw new Error('Failed to get user data');
      }
    } catch (error) {
      console.error('Failed to fetch user:', error);
      localStorage.removeItem('auth_token');
      setIsAuthenticated(false);
    } finally {
      setLoading(false);
    }
  };

  const login = async (credentials) => {
    try {
  const response = await authApi.login(credentials);
      
      if (response.data.success) {
        const { token, user } = response.data.data;
        
        localStorage.setItem('auth_token', token);
        setUser(user);
        setIsAuthenticated(true);
        
        return { success: true, user };
      } else {
        throw new Error(response.data.message || 'Login failed');
      }
    } catch (error) {
      console.error('Login failed:', error);
      
      // Handle validation errors
      if (error.response?.data?.errors) {
        throw {
          errors: error.response.data.errors,
          message: error.response.data.message || 'Validation failed'
        };
      }
      
      throw {
        message: error.response?.data?.message || error.message || 'Login failed'
      };
    }
  };

  const register = async (userData) => {
    try {
  const response = await authApi.register(userData);
      
      if (response.data.success) {
        const { token, user } = response.data.data;
        
        localStorage.setItem('auth_token', token);
        setUser(user);
        setIsAuthenticated(true);
        
        return { success: true, user };
      } else {
        throw new Error(response.data.message || 'Registration failed');
      }
    } catch (error) {
      console.error('Registration failed:', error);
      
      // Handle validation errors
      if (error.response?.data?.errors) {
        throw {
          errors: error.response.data.errors,
          message: error.response.data.message || 'Validation failed'
        };
      }
      
      throw {
        message: error.response?.data?.message || error.message || 'Registration failed'
      };
    }
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      localStorage.removeItem('auth_token');
      setUser(null);
      setIsAuthenticated(false);
    }
  };

  const value = {
    user,
    loading,
    isAuthenticated,
    login,
    register,
    logout,
    fetchUser,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
