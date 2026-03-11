import React, { createContext, useContext, useState, useCallback } from 'react';
import * as svc from '../services/marketplaceService';
import { parseError } from '../services/api';
import toast from 'react-hot-toast';

const MarketplaceContext = createContext(null);

export function MarketplaceProvider({ children }) {
  // ── Listings ─────────────────────────────────
  const [listings,    setListings]    = useState([]);
  const [myListings,  setMyListings]  = useState([]);
  const [listing,     setListing]     = useState(null);
  const [isLoadingListings, setIsLoadingListings] = useState(false);
  const [listingsError,     setListingsError]     = useState(null);

  // ── Cart ─────────────────────────────────────
  const [cartItems, setCartItems] = useState([]);
  const cartTotal = cartItems.reduce((s, i) => s + i.product.price * i.quantity, 0);

  // ── Orders ───────────────────────────────────
  const [orders,          setOrders]         = useState([]);
  const [cartCheckoutGroups, setCartCheckoutGroups] = useState([]);
  const [isLoadingOrders, setIsLoadingOrders] = useState(false);

  // ── Wishlist ──────────────────────────────────
  const [wishlist, setWishlist] = useState([]);

  // ── Create/Edit listing ───────────────────────
  const [isCreating, setIsCreating] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);

  // ── Checkout ──────────────────────────────────
  const [isCheckingOut,   setIsCheckingOut]   = useState(false);
  const [checkoutError,   setCheckoutError]   = useState(null);
  const [cartCheckoutPolledStatus, setCartCheckoutPolledStatus] = useState(null);

  // ────────────────────────────────────────────
  const fetchListings = useCallback(async (params = {}) => {
    setIsLoadingListings(true); setListingsError(null);
    try {
      const res = await svc.getListings(params);
      setListings(res.data?.data ?? res.data ?? []);
    } catch (e) { setListingsError(parseError(e)); }
    finally { setIsLoadingListings(false); }
  }, []);

  const fetchListing = useCallback(async (id) => {
    try {
      const res = await svc.getListing(id);
      setListing(res.data?.data ?? res.data);
    } catch (e) { toast.error(parseError(e)); }
  }, []);

  const fetchMyListings = useCallback(async () => {
    try {
      const res = await svc.getMyListings();
      setMyListings(res.data?.data ?? []);
    } catch (e) { toast.error(parseError(e)); }
  }, []);

  const createListing = useCallback(async (formData, onSuccess) => {
    setIsCreating(true);
    try {
      const res = await svc.createListing(formData);
      const newItem = res.data?.data ?? res.data;
      setMyListings(prev => [newItem, ...prev]);
      toast.success('Barang berhasil dipasang! 🎉');
      onSuccess?.();
    } catch (e) { toast.error(parseError(e)); }
    finally { setIsCreating(false); }
  }, []);

  const updateListing = useCallback(async (id, formData, onSuccess) => {
    setIsUpdating(true);
    try {
      const res = await svc.updateListing(id, formData);
      const updated = res.data?.data ?? res.data;
      setMyListings(prev => prev.map(p => p.id === id ? updated : p));
      toast.success('Listing berhasil diperbarui! ✅');
      onSuccess?.();
    } catch (e) { toast.error(parseError(e)); }
    finally { setIsUpdating(false); }
  }, []);

  const deleteListing = useCallback(async (id) => {
    try {
      await svc.deleteListing(id);
      setMyListings(prev => prev.filter(p => p.id !== id));
      toast.success('Listing dihapus.');
    } catch (e) { toast.error(parseError(e)); }
  }, []);

  // ── Cart actions ──────────────────────────────
  const addToCart = useCallback((product, quantity = 1) => {
    setCartItems(prev => {
      const existing = prev.find(i => i.product.id === product.id);
      if (existing) {
        return prev.map(i => i.product.id === product.id
          ? { ...i, quantity: i.quantity + quantity }
          : i);
      }
      return [...prev, { product, quantity, subtotal: product.price * quantity }];
    });
    toast.success(`${product.name} ditambahkan ke keranjang 🛒`);
  }, []);

  const removeFromCart = useCallback((productId) => {
    setCartItems(prev => prev.filter(i => i.product.id !== productId));
  }, []);

  const updateCartQuantity = useCallback((productId, quantity) => {
    if (quantity <= 0) {
      setCartItems(prev => prev.filter(i => i.product.id !== productId));
      return;
    }
    setCartItems(prev => prev.map(i =>
      i.product.id === productId
        ? { ...i, quantity, subtotal: i.product.price * quantity }
        : i
    ));
  }, []);

  const clearCart = useCallback(() => setCartItems([]), []);

  // ── Checkout ──────────────────────────────────
  const checkoutCart = useCallback(async (shippingAddress, notes, onPaymentReady) => {
    setIsCheckingOut(true); setCheckoutError(null);
    try {
      const items = cartItems.map(i => ({
        listing_id: i.product.id,
        quantity:   i.quantity,
      }));
      const res = await svc.checkoutCart({
        items, shipping_address: shippingAddress, notes
      });
      const data = res.data?.data ?? res.data;
      onPaymentReady?.(data.payment_link, data.cart_checkout_id);
    } catch (e) {
      setCheckoutError(parseError(e));
    } finally {
      setIsCheckingOut(false);
    }
  }, [cartItems]);

  const pollCartCheckoutStatus = useCallback(async (cartCheckoutId) => {
    try {
      const res = await svc.pollCartStatus(cartCheckoutId);
      const status = res.data?.payment_status ?? res.data?.status;
      setCartCheckoutPolledStatus(status);
      if (status === 'paid') clearCart();
    } catch {}
  }, [clearCart]);

  const cancelCartCheckout = useCallback(async (cartCheckoutId, onDone) => {
    try {
      await svc.cancelCartCheckout(cartCheckoutId);
      toast.success('Pesanan dibatalkan.');
      onDone?.();
    } catch (e) { toast.error(parseError(e)); }
  }, []);

  const clearCartCheckoutPolledStatus = () => setCartCheckoutPolledStatus(null);

  // ── Orders ───────────────────────────────────
  const fetchOrders = useCallback(async () => {
    setIsLoadingOrders(true);
    try {
      const [ordRes, ccRes] = await Promise.all([
        svc.getOrders(),
        svc.getCartCheckouts(),
      ]);
      setOrders(ordRes.data?.data ?? []);
      setCartCheckoutGroups(ccRes.data?.data ?? []);
    } catch (e) { toast.error(parseError(e)); }
    finally { setIsLoadingOrders(false); }
  }, []);

  const createOrder = useCallback(async (data, onSuccess) => {
    try {
      const res = await svc.createOrder(data);
      toast.success('Pesanan berhasil dibuat!');
      onSuccess?.(res.data?.data ?? res.data);
    } catch (e) { toast.error(parseError(e)); }
  }, []);

  const payOrder = useCallback(async (id) => {
    try {
      const res = await svc.payOrder(id);
      return res.data;
    } catch (e) { toast.error(parseError(e)); return null; }
  }, []);

  const cancelOrder = useCallback(async (id, reason) => {
    try {
      await svc.cancelOrder(id, { reason });
      setOrders(prev => prev.map(o => o.id === id ? { ...o, status: 'cancelled' } : o));
      toast.success('Pesanan dibatalkan.');
    } catch (e) { toast.error(parseError(e)); }
  }, []);

  // ── Wishlist ──────────────────────────────────
  const fetchWishlist = useCallback(async () => {
    try {
      const res = await svc.getWishlist();
      setWishlist(res.data?.data ?? []);
    } catch {}
  }, []);

  const toggleWishlist = useCallback(async (listingId) => {
    try {
      const res = await svc.toggleWishlist(listingId);
      const wishlisted = res.data?.wishlisted;
      setListings(prev => prev.map(l =>
        l.id === listingId ? { ...l, is_wishlisted: wishlisted } : l
      ));
      toast.success(wishlisted ? 'Ditambahkan ke wishlist ❤️' : 'Dihapus dari wishlist');
    } catch (e) { toast.error(parseError(e)); }
  }, []);

  return (
    <MarketplaceContext.Provider value={{
      listings, myListings, listing, isLoadingListings, listingsError,
      cartItems, cartTotal,
      orders, cartCheckoutGroups, isLoadingOrders,
      wishlist,
      isCreating, isUpdating,
      isCheckingOut, checkoutError, cartCheckoutPolledStatus,
      fetchListings, fetchListing, fetchMyListings,
      createListing, updateListing, deleteListing,
      addToCart, removeFromCart, updateCartQuantity, clearCart,
      checkoutCart, pollCartCheckoutStatus, cancelCartCheckout, clearCartCheckoutPolledStatus,
      fetchOrders, createOrder, payOrder, cancelOrder,
      fetchWishlist, toggleWishlist,
      setCheckoutError,
    }}>
      {children}
    </MarketplaceContext.Provider>
  );
}

export const useMarketplace = () => {
  const ctx = useContext(MarketplaceContext);
  if (!ctx) throw new Error('useMarketplace must be inside MarketplaceProvider');
  return ctx;
};
