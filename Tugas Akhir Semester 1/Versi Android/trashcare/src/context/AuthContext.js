import React, { createContext, useContext, useState, useCallback } from 'react';
import { login as loginApi, register as registerApi, getProfile, logout as logoutApi } from '../services/authService';
import { parseError } from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user,      setUser]      = useState(() => {
    try { return JSON.parse(localStorage.getItem('user')); } catch { return null; }
  });
  const [token,     setToken]     = useState(() => localStorage.getItem('token'));
  const [isLoading, setIsLoading] = useState(false);
  const [error,     setError]     = useState(null);

  const saveSession = (token, user) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
    setToken(token);
    setUser(user);
  };

  const login = useCallback(async (email, password) => {
    setIsLoading(true); setError(null);
    try {
      const res = await loginApi({ email, password });
      saveSession(res.data.token, res.data.user);
      return { ok: true, role: res.data.user?.role };
    } catch (e) {
      const msg = parseError(e);
      setError(msg);
      return { ok: false, message: msg };
    } finally {
      setIsLoading(false);
    }
  }, []);

  const register = useCallback(async (data) => {
    setIsLoading(true); setError(null);
    try {
      const res = await registerApi(data);
      saveSession(res.data.token, res.data.user);
      return { ok: true };
    } catch (e) {
      const msg = parseError(e);
      setError(msg);
      return { ok: false, message: msg };
    } finally {
      setIsLoading(false);
    }
  }, []);

  const logout = useCallback(async () => {
    try { await logoutApi(); } catch {}
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  }, []);

  const refreshProfile = useCallback(async () => {
    try {
      const res = await getProfile();
      const u = res.data.data ?? res.data;
      localStorage.setItem('user', JSON.stringify(u));
      setUser(u);
    } catch {}
  }, []);

  const isLoggedIn = !!token;
  const isCourier  = user?.role === 'courier';

  return (
    <AuthContext.Provider value={{
      user, token, isLoggedIn, isCourier,
      isLoading, error, setError,
      login, register, logout, refreshProfile
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be inside AuthProvider');
  return ctx;
};
