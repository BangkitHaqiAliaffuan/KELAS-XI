import React from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { useMarketplace } from '../context/MarketplaceContext';

const NAV_ITEMS = [
  { to: '/home',        label: 'Beranda',     icon: '🏠' },
  { to: '/pickup',      label: 'Pickup',      icon: '🚚' },
  { to: '/marketplace', label: 'Belanja',     icon: '🛍️' },
  { to: '/profile',     label: 'Profil',      icon: '👤' },
];

export default function BottomNav() {
  const { cartItems } = useMarketplace();
  const location = useLocation();
  const cartCount = cartItems.reduce((s, i) => s + i.quantity, 0);

  return (
    <nav className="fixed bottom-0 w-full max-w-app bg-white border-t border-divider z-50
                    flex items-stretch" style={{ maxWidth: 430 }}>
      {NAV_ITEMS.map(({ to, label, icon }) => {
        const active = location.pathname.startsWith(to);
        return (
          <NavLink key={to} to={to}
            className="flex-1 flex flex-col items-center justify-center py-2 gap-0.5 relative">
            <span className="text-xl leading-none">{icon}</span>
            {to === '/marketplace' && cartCount > 0 && (
              <span className="absolute top-1.5 right-[calc(50%-18px)] bg-orange-accent text-white
                               text-[9px] font-bold rounded-full min-w-[16px] h-4 flex items-center
                               justify-center px-1">
                {cartCount > 99 ? '99+' : cartCount}
              </span>
            )}
            <span className={`text-[10px] font-medium ${
              active ? 'text-green-primary' : 'text-text-hint'
            }`}>{label}</span>
            {active && (
              <span className="absolute bottom-0 left-1/2 -translate-x-1/2 w-5 h-0.5
                               bg-green-primary rounded-full" />
            )}
          </NavLink>
        );
      })}
    </nav>
  );
}
