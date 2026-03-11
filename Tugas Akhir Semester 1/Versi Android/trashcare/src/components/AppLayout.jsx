import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import BottomNav from './BottomNav';

// Wrapper for pages that need login
export function ProtectedRoute({ children, courierOnly = false }) {
  const { isLoggedIn, isCourier } = useAuth();
  const location = useLocation();

  if (!isLoggedIn) return <Navigate to="/login" state={{ from: location }} replace />;
  if (courierOnly && !isCourier) return <Navigate to="/home" replace />;
  return children;
}

// Main layout with BottomNav
export default function AppLayout({ children, showNav = true }) {
  return (
    <div className="app-shell">
      <div className="flex-1 flex flex-col min-h-0 overflow-hidden">
        {children}
      </div>
      {showNav && <BottomNav />}
    </div>
  );
}

// Loading spinner
export function PageLoader() {
  return (
    <div className="app-shell flex items-center justify-center">
      <div className="flex flex-col items-center gap-3">
        <div className="w-10 h-10 border-4 border-green-light border-t-green-primary
                        rounded-full animate-spin" />
        <span className="text-text-secondary text-sm">Memuat...</span>
      </div>
    </div>
  );
}

// Empty state
export function EmptyState({ emoji = '📭', title, subtitle, action }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 px-6 text-center gap-3">
      <span className="text-5xl">{emoji}</span>
      <h3 className="font-bold text-text-primary text-base">{title}</h3>
      {subtitle && <p className="text-text-secondary text-sm">{subtitle}</p>}
      {action}
    </div>
  );
}

// Error state
export function ErrorState({ message, onRetry }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 px-6 text-center gap-3">
      <span className="text-4xl">⚠️</span>
      <p className="text-text-secondary text-sm">{message ?? 'Terjadi kesalahan.'}</p>
      {onRetry && (
        <button onClick={onRetry} className="btn-outline py-2 px-5 text-sm">Coba Lagi</button>
      )}
    </div>
  );
}
