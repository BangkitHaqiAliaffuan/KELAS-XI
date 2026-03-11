import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMarketplace } from '../context/MarketplaceContext';
import AppLayout from '../components/AppLayout';
import Header from '../components/Header';
import { formatRupiah, storageUrl } from '../services/api';
import toast from 'react-hot-toast';

/* ── Step enum ── */
const STEP = { FORM: 'form', WAITING: 'waiting', DONE: 'done' };

const STATUS_STYLE = {
  pending:    { bg: 'bg-yellow-100', text: 'text-yellow-700', label: 'Menunggu Pembayaran' },
  processing: { bg: 'bg-blue-100',   text: 'text-blue-700',   label: 'Diproses' },
  paid:       { bg: 'bg-green-light',text: 'text-green-primary', label: 'Sudah Dibayar' },
  completed:  { bg: 'bg-green-light',text: 'text-green-primary', label: 'Selesai' },
  cancelled:  { bg: 'bg-red-100',    text: 'text-red-600',    label: 'Dibatal' },
};

export default function CartCheckout() {
  const navigate = useNavigate();
  const {
    cartItems, cartTotal,
    checkoutCart, pollCartCheckoutStatus,
    fetchCart,
  } = useMarketplace();

  const [step,     setStep]    = useState(STEP.FORM);
  const [address,  setAddress] = useState('');
  const [notes,    setNotes]   = useState('');
  const [loading,  setLoading] = useState(false);
  const [orders,   setOrders]  = useState([]);   // orders from checkout response
  const [cancelling, setCancelling] = useState({});

  useEffect(() => { fetchCart?.(); }, []); // eslint-disable-line

  /* ── helpers ── */
  const groupedItems = cartItems?.reduce((acc, item) => {
    const key = item.listing?.seller_id ?? item.seller_id ?? 'unknown';
    if (!acc[key]) acc[key] = { seller: item.listing?.seller ?? item.seller, items: [] };
    acc[key].items.push(item);
    return acc;
  }, {}) ?? {};

  const handleCheckout = async e => {
    e.preventDefault();
    if (!address.trim()) { toast.error('Alamat pengiriman wajib diisi'); return; }
    setLoading(true);
    try {
      const result = await checkoutCart?.({ shipping_address: address, notes });
      const ordersData = result?.data?.data ?? result?.data ?? [];
      setOrders(Array.isArray(ordersData) ? ordersData : [ordersData]);
      setStep(STEP.WAITING);
      toast.success('Checkout berhasil! Lakukan pembayaran.');
    } catch (e) {
      toast.error(e?.message ?? 'Checkout gagal');
    } finally { setLoading(false); }
  };

  const handlePay = (order) => {
    if (order?.payment_url) {
      window.open(order.payment_url, '_blank');
    } else {
      navigate(`/payment/${order.id}`);
    }
  };

  const handleCancel = async (orderId) => {
    if (!window.confirm('Batalkan pesanan ini?')) return;
    setCancelling(p => ({ ...p, [orderId]: true }));
    try {
      await pollCartCheckoutStatus?.(orderId, 'cancel');
      setOrders(p => p.map(o => o.id === orderId ? { ...o, status: 'cancelled' } : o));
      toast.success('Pesanan dibatalkan');
    } catch { toast.error('Gagal membatalkan'); }
    finally { setCancelling(p => ({ ...p, [orderId]: false })); }
  };

  /* ── empty ── */
  if ((!cartItems || cartItems.length === 0) && step === STEP.FORM) return (
    <AppLayout hideNav>
      <Header title="Checkout" showBack />
      <div className="flex flex-col items-center gap-3 pt-20 px-8 text-center">
        <span className="text-5xl">🛒</span>
        <p className="font-semibold text-text-primary">Keranjang kosong</p>
        <button onClick={() => navigate('/marketplace')} className="btn-primary mt-2 px-8">Belanja</button>
      </div>
    </AppLayout>
  );

  return (
    <AppLayout hideNav>
      <Header title={step === STEP.FORM ? 'Checkout' : 'Status Pembayaran'} showBack />

      <div className="page-content px-4 pt-4 pb-8 flex flex-col gap-4">

        {/* ══ STEP 1: FORM ══ */}
        {step === STEP.FORM && (
          <form onSubmit={handleCheckout} className="flex flex-col gap-4">

            {/* items per seller */}
            {Object.entries(groupedItems).map(([key, group]) => (
              <div key={key} className="card p-3">
                <p className="section-title mb-2">
                  🏪 {group.seller?.name ?? 'Penjual'}
                </p>
                {group.items.map(item => {
                  const listing = item.listing ?? item;
                  return (
                    <div key={item.id} className="flex gap-2 mb-2 last:mb-0">
                      <div className="w-12 h-12 bg-green-light/30 rounded-lg overflow-hidden flex-shrink-0 flex items-center justify-center">
                        {listing.image_url
                          ? <img src={storageUrl(listing.image_url)} alt="" className="w-full h-full object-cover" />
                          : <span className="text-xl">📦</span>}
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-text-primary text-xs font-semibold truncate">{listing.name}</p>
                        <p className="text-text-hint text-[10px]">×{item.quantity ?? 1}</p>
                      </div>
                      <p className="text-green-primary text-xs font-bold flex-shrink-0">
                        {formatRupiah((listing.price ?? 0) * (item.quantity ?? 1))}
                      </p>
                    </div>
                  );
                })}
              </div>
            ))}

            {/* address */}
            <div>
              <label className="label">Alamat Pengiriman <span className="text-red-500">*</span></label>
              <textarea value={address} onChange={e => setAddress(e.target.value)}
                rows={3} placeholder="Masukkan alamat lengkap..."
                className="input-field mt-1 resize-none" />
            </div>

            {/* notes */}
            <div>
              <label className="label">Catatan (opsional)</label>
              <input value={notes} onChange={e => setNotes(e.target.value)}
                placeholder="Catatan untuk penjual..." className="input-field mt-1" />
            </div>

            {/* total */}
            <div className="card p-4 flex justify-between items-center">
              <p className="text-text-secondary text-sm">Total Pembayaran</p>
              <p className="text-green-primary font-bold text-lg">{formatRupiah(cartTotal ?? 0)}</p>
            </div>

            <button type="submit" disabled={loading}
              className="btn-primary py-4 disabled:opacity-60 flex items-center justify-center gap-2">
              {loading
                ? <><span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" /> Memproses...</>
                : '🛍️ Buat Pesanan'}
            </button>
          </form>
        )}

        {/* ══ STEP 2: WAITING / ORDERS ══ */}
        {step === STEP.WAITING && (
          <div className="flex flex-col gap-4">
            <div className="card p-4 flex items-start gap-3 bg-green-light">
              <span className="text-2xl">✅</span>
              <div>
                <p className="font-bold text-green-primary text-sm">Pesanan Dibuat!</p>
                <p className="text-text-secondary text-xs mt-0.5">
                  Selesaikan pembayaran sebelum batas waktu.
                </p>
              </div>
            </div>

            {orders.map(order => {
              const s = STATUS_STYLE[order.status] ?? STATUS_STYLE.pending;
              return (
                <div key={order.id} className="card p-4 flex flex-col gap-3">
                  <div className="flex items-center justify-between">
                    <p className="font-semibold text-text-primary text-sm">
                      Order #{order.id}
                    </p>
                    <span className={`${s.bg} ${s.text} text-[10px] font-bold px-2 py-0.5 rounded-full`}>
                      {s.label}
                    </span>
                  </div>

                  <div className="flex justify-between items-center text-sm">
                    <span className="text-text-secondary">Total</span>
                    <span className="font-bold text-green-primary">{formatRupiah(order.total_amount ?? 0)}</span>
                  </div>

                  {['pending', 'processing'].includes(order.status) && (
                    <div className="flex gap-2">
                      <button onClick={() => handlePay(order)}
                        className="btn-primary flex-1 py-2.5 text-sm">
                        💳 Bayar Sekarang
                      </button>
                      <button onClick={() => handleCancel(order.id)}
                        disabled={cancelling[order.id]}
                        className="btn-danger flex-1 py-2.5 text-sm disabled:opacity-60">
                        {cancelling[order.id] ? '...' : 'Batalkan'}
                      </button>
                    </div>
                  )}
                </div>
              );
            })}

            <button onClick={() => navigate('/profile/orders')}
              className="btn-outline py-3">Lihat Semua Pesanan</button>
          </div>
        )}
      </div>
    </AppLayout>
  );
}
