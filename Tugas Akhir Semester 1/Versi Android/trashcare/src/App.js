import React, { Suspense, lazy } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';
import { MarketplaceProvider } from './context/MarketplaceContext';
import { ProtectedRoute, PageLoader } from './components/AppLayout';

// ── Lazy-load pages ──────────────────────────────────────────────
const Onboarding     = lazy(() => import('./pages/Onboarding'));
const Login          = lazy(() => import('./pages/Login'));
const Register       = lazy(() => import('./pages/Register'));
const Home           = lazy(() => import('./pages/Home'));
const Marketplace    = lazy(() => import('./pages/Marketplace'));
const ProductDetail  = lazy(() => import('./pages/ProductDetail'));
const AddListing     = lazy(() => import('./pages/AddListing'));
const EditListing    = lazy(() => import('./pages/EditListing'));
const Cart           = lazy(() => import('./pages/Cart'));
const CartCheckout   = lazy(() => import('./pages/CartCheckout'));
const Payment        = lazy(() => import('./pages/Payment'));
const Profile        = lazy(() => import('./pages/Profile'));
const MyOrders       = lazy(() => import('./pages/MyOrders'));
const MyShop         = lazy(() => import('./pages/MyShop'));
const Wishlist       = lazy(() => import('./pages/Wishlist'));
const Address        = lazy(() => import('./pages/Address'));
const CourierHome    = lazy(() => import('./pages/CourierHome'));
const CourierRoute   = lazy(() => import('./pages/CourierRoute'));
const Pickup         = lazy(() => import('./pages/Pickup'));

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <MarketplaceProvider>
          <Toaster position="top-center" toastOptions={{ duration: 3000,
            style: { maxWidth: 380, fontSize: 13, borderRadius: 12 } }} />
          <Suspense fallback={<PageLoader />}>
            <Routes>
              <Route path="/"         element={<Onboarding />} />
              <Route path="/login"    element={<Login />} />
              <Route path="/register" element={<Register />} />

              <Route path="/home"     element={<ProtectedRoute><Home /></ProtectedRoute>} />
              <Route path="/marketplace"          element={<ProtectedRoute><Marketplace /></ProtectedRoute>} />
              <Route path="/marketplace/add"      element={<ProtectedRoute><AddListing /></ProtectedRoute>} />
              <Route path="/marketplace/edit/:id" element={<ProtectedRoute><EditListing /></ProtectedRoute>} />
              <Route path="/marketplace/:id"      element={<ProtectedRoute><ProductDetail /></ProtectedRoute>} />
              <Route path="/cart"     element={<ProtectedRoute><Cart /></ProtectedRoute>} />
              <Route path="/checkout" element={<ProtectedRoute><CartCheckout /></ProtectedRoute>} />
              <Route path="/payment/:orderId" element={<ProtectedRoute><Payment /></ProtectedRoute>} />
              <Route path="/profile"          element={<ProtectedRoute><Profile /></ProtectedRoute>} />
              <Route path="/profile/orders"   element={<ProtectedRoute><MyOrders /></ProtectedRoute>} />
              <Route path="/profile/shop"     element={<ProtectedRoute><MyShop /></ProtectedRoute>} />
              <Route path="/profile/wishlist" element={<ProtectedRoute><Wishlist /></ProtectedRoute>} />
              <Route path="/profile/address"  element={<ProtectedRoute><Address /></ProtectedRoute>} />
              <Route path="/pickup"           element={<ProtectedRoute><Pickup /></ProtectedRoute>} />
              <Route path="/courier"          element={<ProtectedRoute courierOnly><CourierHome /></ProtectedRoute>} />
              <Route path="/courier/route"    element={<ProtectedRoute courierOnly><CourierRoute /></ProtectedRoute>} />

              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </Suspense>
        </MarketplaceProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}
export default App;
