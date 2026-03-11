import React, { useEffect, useState, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import AppLayout from '../components/AppLayout';
import Header from '../components/Header';
import { formatRupiah } from '../services/api';
import * as marketSvc from '../services/marketplaceService';
import toast from 'react-hot-toast';

const STATUS_STYLE = {
  pending:    { bg: 'bg-yellow-100', text: 'text-yellow-700',     emoji: '⏳', label: 'Menunggu Pembayaran' },
  processing: { bg: 'bg-blue-100',   text: 'text-blue-700',       emoji: '🔄', label: 'Diproses' },
  paid:       { bg: 'bg-green-light',text: 'text-green-primary',  emoji: '✅', label: 'Sudah Dibayar' },
  completed:  { bg: 'bg-green-light',text: 'text-green-primary',  emoji: '🎉', label: 'Selesai' },
  cancelled:  { bg: 'bg-red-100',    text: 'text-red-600',        emoji: '❌', label: 'Dibatalkan' },
};

export default function Payment() {
  const { orderId } = useParams();
  const navigate    = useNavigate();

  const [order,     setOrder]     = useState(null);
  const [loading,   setLoading]   = useState(true);
  const [polling,   setPolling]   = useState(false);
  const [cancelling, setCancelling] = useState(false);

  const fetchOrder = useCallback(async () => {
    try {
      const res = await marketSvc.getOrder(orderId);
      setOrder(res.data?.data ?? res.data);
    } catch {
      toast.error('Gagal memuat pesanan');
    } finally {
      setLoading(false);
    }
  }, [orderId]);

  useEffect(() => {
    fetchOrder();
    const interval = setInterval(async () => {
      if (polling) return;
      setPolling(true);
      await fetchOrder();
      setPolling(false);
    }, 5000);
    return () => clearInterval(interval);
  }, [fetchOrder]); // eslint-disable-line

  const handlePay = () => {
    if (order?.payment_url) {
      window.open(order.payment_url, '_blank');
    } else {
      toast.error('Link pembayaran tidak tersedia');
    }
  };

  const handleCancel = async () => {
    if (!window.confirm('Batalkan pesanan ini?')) return;
    setCancelling(true);
    try {
      await marketSvc.cancelOrder(orderId);
      toast.success('Pesanan dibatalkan');
      await fetchOrder();
    } catch {
      toast.error('Gagal membatalkan pesanan');
    } finally { setCancelling(false); }
  };

  if (loading) return (
    <AppLayout hideNav>
      <Header title="Pembayaran" showBack />
      <div className="flex justify-center pt-20">
        <span className="w-8 h-8 border-2 border-green-primary border-t-transparent rounded-full animate-spin" />
      </div>
    </AppLayout>
  );

  if (!order) return (
    <AppLayout hideNav>
      <Header title="Pembayaran" showBack />
      <div className="flex flex-col items-center gap-3 pt-16 text-center px-8">
        <span className="text-5xl">😕</span>
        <p className="font-semibold">Pesanan tidak ditemukan</p>
        <button onClick={() => navigate(-1)} className="btn-outline mt-2 px-6">Kembali</button>
      </div>
    </AppLayout>
  );

  const s = STATUS_STYLE[order.status] ?? STATUS_STYLE.pending;
  const isPending = ['pending', 'processing'].includes(order.status);

  return (
    <AppLayout hideNav>
      <Header title="Detail Pembayaran" showBack />

      <div className="page-content px-4 pt-5 flex flex-col gap-4">

        {/* status card */}
        <div className={`card p-5 flex flex-col items-center gap-2 text-center ${s.bg}`}>
          <span className="text-5xl">{s.emoji}</span>
          <p className={`font-extrabold text-xl ${s.text}`}>{s.label}</p>
          <p className="text-text-secondary text-sm">Order #{order.id}</p>
          {polling && (
            <span className="text-[10px] text-text-hint mt-1 flex items-center gap-1">
              <span className="w-2 h-2 border border-text-hint border-t-transparent rounded-full animate-spin" />
              Memperbarui...
            </span>
          )}
        </div>

        {/* order details */}
        <div className="card p-4 flex flex-col gap-3">
          <p className="section-title">Detail Pesanan</p>
          <div className="flex flex-col gap-2">
            {[
              { label: 'No. Pesanan', value: `#${order.id}` },
              { label: 'Total',       value: formatRupiah(order.total_amount ?? 0) },
              { label: 'Pengiriman ke', value: order.shipping_address ?? '-' },
              { label: 'Catatan',     value: order.notes ?? '-' },
            ].map(r => (
              <div key={r.label} className="flex justify-between items-start gap-2">
                <span className="text-text-secondary text-sm flex-shrink-0">{r.label}</span>
                <span className="text-text-primary text-sm font-semibold text-right">{r.value}</span>
              </div>
            ))}
          </div>
        </div>

        {/* items */}
        {order.items?.length > 0 && (
          <div className="card p-4">
            <p className="section-title mb-3">Item Pesanan</p>
            {order.items.map(item => (
              <div key={item.id} className="flex justify-between items-center mb-2 last:mb-0">
                <span className="text-text-primary text-sm flex-1 truncate pr-2">
                  {item.listing?.name ?? item.name} ×{item.quantity ?? 1}
                </span>
                <span className="text-green-primary font-semibold text-sm flex-shrink-0">
                  {formatRupiah((item.price ?? 0) * (item.quantity ?? 1))}
                </span>
              </div>
            ))}
          </div>
        )}

        {/* actions */}
        {isPending && (
          <div className="flex flex-col gap-3 pb-4">
            <button onClick={handlePay} className="btn-primary py-4 flex items-center justify-center gap-2">
              💳 Bayar Sekarang
            </button>
            <button onClick={handleCancel} disabled={cancelling}
              className="btn-danger py-3 disabled:opacity-60 flex items-center justify-center gap-2">
              {cancelling
                ? <><span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" /> Membatalkan...</>
                : '❌ Batalkan Pesanan'}
            </button>
          </div>
        )}

        {!isPending && (
          <button onClick={() => navigate('/profile/orders')}
            className="btn-outline py-3 mb-4">Lihat Semua Pesanan</button>
        )}
      </div>
    </AppLayout>
  );
}
