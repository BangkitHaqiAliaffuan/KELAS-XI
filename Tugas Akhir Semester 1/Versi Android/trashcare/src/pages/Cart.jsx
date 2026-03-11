import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMarketplace } from '../context/MarketplaceContext';
import AppLayout from '../components/AppLayout';
import Header from '../components/Header';
import { formatRupiah, storageUrl } from '../services/api';
import toast from 'react-hot-toast';

export default function Cart() {
  const navigate = useNavigate();
  const {
    cartItems, cartTotal,
    fetchCart, updateCartItem, removeFromCart,
  } = useMarketplace();

  useEffect(() => { fetchCart?.(); }, []);   // eslint-disable-line

  if (!cartItems || cartItems.length === 0) return (
    <AppLayout>
      <Header title="Keranjang" showBack />
      <div className="flex flex-col items-center gap-3 pt-20 px-8 text-center">
        <span className="text-6xl">🛒</span>
        <p className="font-bold text-text-primary text-lg">Keranjang Kosong</p>
        <p className="text-text-secondary text-sm">Yuk belanja dulu di marketplace!</p>
        <button onClick={() => navigate('/marketplace')} className="btn-primary mt-2 px-8">
          Ke Marketplace
        </button>
      </div>
    </AppLayout>
  );

  const handleQty = async (item, delta) => {
    const newQty = (item.quantity ?? 1) + delta;
    if (newQty < 1) {
      await handleRemove(item);
      return;
    }
    try {
      await updateCartItem?.(item.id, newQty);
    } catch (e) { toast.error(e?.message ?? 'Gagal update'); }
  };

  const handleRemove = async item => {
    try {
      await removeFromCart?.(item.id);
      toast.success('Item dihapus dari keranjang');
    } catch { toast.error('Gagal menghapus'); }
  };

  return (
    <AppLayout>
      <Header title="Keranjang" showBack />

      <div className="page-content px-4 pt-4 flex flex-col gap-3">
        {cartItems.map(item => {
          const listing = item.listing ?? item;
          const qty     = item.quantity ?? 1;
          return (
            <div key={item.id} className="card p-3 flex gap-3">
              {/* image */}
              <div className="w-20 h-20 bg-green-light/30 rounded-xl overflow-hidden flex-shrink-0
                              flex items-center justify-center">
                {listing.image_url
                  ? <img src={storageUrl(listing.image_url)} alt={listing.name}
                      className="w-full h-full object-cover" />
                  : <span className="text-3xl">📦</span>}
              </div>

              {/* info */}
              <div className="flex-1 min-w-0 flex flex-col justify-between">
                <div>
                  <p className="font-semibold text-text-primary text-sm truncate">
                    {listing.name ?? item.name}
                  </p>
                  <p className="text-green-primary font-bold text-base">
                    {formatRupiah(listing.price ?? item.price)}
                  </p>
                </div>

                {/* qty + remove */}
                <div className="flex items-center justify-between mt-1">
                  <div className="flex items-center border border-divider rounded-xl overflow-hidden">
                    <button onClick={() => handleQty(item, -1)}
                      className="w-8 h-8 flex items-center justify-center text-green-primary font-bold text-lg bg-green-light/50">−</button>
                    <span className="w-8 h-8 flex items-center justify-center text-text-primary font-semibold text-sm">{qty}</span>
                    <button onClick={() => handleQty(item, +1)}
                      className="w-8 h-8 flex items-center justify-center text-green-primary font-bold text-lg bg-green-light/50">＋</button>
                  </div>

                  <button onClick={() => handleRemove(item)}
                    className="text-red-400 text-xs font-semibold">Hapus</button>
                </div>
              </div>
            </div>
          );
        })}

        {/* summary */}
        <div className="card p-4 mt-1">
          <div className="flex justify-between items-center">
            <p className="text-text-secondary text-sm">Total ({cartItems.length} item)</p>
            <p className="text-green-primary font-bold text-lg">{formatRupiah(cartTotal ?? 0)}</p>
          </div>
        </div>

        {/* checkout button */}
        <button onClick={() => navigate('/checkout')}
          className="btn-primary py-4 mt-1 mb-4 flex items-center justify-center gap-2">
          🛍️ Checkout Sekarang
        </button>
      </div>
    </AppLayout>
  );
}
