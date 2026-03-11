import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AppLayout from '../components/AppLayout';
import Header from '../components/Header';
import { formatRupiah, formatDate } from '../services/api';
import * as marketSvc from '../services/marketplaceService';
import toast from 'react-hot-toast';

const TABS = ['Semua', 'Pending', 'Diproses', 'Selesai', 'Dibatal'];

const STATUS_MAP = {
  'Semua':    null,
  'Pending':  'pending',
  'Diproses': 'processing',
  'Selesai':  'completed',
  'Dibatal':  'cancelled',
};

const STATUS_STYLE = {
  pending:    { bg: 'bg-yellow-100', text: 'text-yellow-700',    emoji: '⏳', label: 'Menunggu' },
  processing: { bg: 'bg-blue-100',   text: 'text-blue-700',      emoji: '🔄', label: 'Diproses' },
  paid:       { bg: 'bg-green-light',text: 'text-green-primary', emoji: '✅', label: 'Dibayar' },
  completed:  { bg: 'bg-green-light',text: 'text-green-primary', emoji: '🎉', label: 'Selesai' },
  cancelled:  { bg: 'bg-red-100',    text: 'text-red-600',       emoji: '❌', label: 'Dibatal' },
};

export default function MyOrders() {
  const navigate  = useNavigate();
  const [tab,     setTab]     = useState('Semua');
  const [orders,  setOrders]  = useState([]);
  const [loading, setLoading] = useState(false);
  const [cancelling, setCancelling] = useState({});

  useEffect(() => {
    (async () => {
      setLoading(true);
      try {
        const res = await marketSvc.getOrders({ status: STATUS_MAP[tab] });
        setOrders(res.data?.data ?? []);
      } catch { toast.error('Gagal memuat pesanan'); }
      finally { setLoading(false); }
    })();
  }, [tab]);

  const handleCancel = async id => {
    if (!window.confirm('Batalkan pesanan?')) return;
    setCancelling(p => ({ ...p, [id]: true }));
    try {
      await marketSvc.cancelOrder(id);
      setOrders(p => p.map(o => o.id === id ? { ...o, status: 'cancelled' } : o));
      toast.success('Pesanan dibatalkan');
    } catch { toast.error('Gagal membatalkan'); }
    finally { setCancelling(p => ({ ...p, [id]: false })); }
  };

  return (
    <AppLayout hideNav>
      <Header title="Pesanan Saya" showBack />

      {/* tabs */}
      <div className="flex overflow-x-auto border-b border-divider bg-white px-2 scrollbar-hide">
        {TABS.map(t => (
          <button key={t} onClick={() => setTab(t)}
            className={`flex-shrink-0 px-4 py-3 text-sm font-semibold border-b-2 transition-colors
              ${tab === t
                ? 'border-green-primary text-green-primary'
                : 'border-transparent text-text-secondary'}`}>
            {t}
          </button>
        ))}
      </div>

      <div className="page-content px-4 pt-4 flex flex-col gap-3">
        {loading ? (
          <div className="flex justify-center pt-12">
            <span className="w-8 h-8 border-2 border-green-primary border-t-transparent rounded-full animate-spin" />
          </div>
        ) : orders.length === 0 ? (
          <div className="flex flex-col items-center gap-3 pt-16 text-center">
            <span className="text-5xl">📋</span>
            <p className="font-semibold text-text-primary">Tidak ada pesanan</p>
            <button onClick={() => navigate('/marketplace')} className="btn-outline mt-1 px-6">
              Belanja Sekarang
            </button>
          </div>
        ) : (
          orders.map(order => {
            const s = STATUS_STYLE[order.status] ?? STATUS_STYLE.pending;
            const isPending = ['pending', 'processing'].includes(order.status);
            return (
              <div key={order.id} className="card p-4 flex flex-col gap-3">
                <div className="flex items-center justify-between">
                  <p className="font-semibold text-text-primary text-sm">Order #{order.id}</p>
                  <span className={`${s.bg} ${s.text} text-[10px] font-bold px-2 py-0.5 rounded-full`}>
                    {s.emoji} {s.label}
                  </span>
                </div>

                <div className="flex flex-col gap-1">
                  {(order.items ?? []).slice(0, 2).map(item => (
                    <p key={item.id} className="text-text-secondary text-xs truncate">
                      • {item.listing?.name ?? item.name} ×{item.quantity ?? 1}
                    </p>
                  ))}
                  {(order.items?.length ?? 0) > 2 && (
                    <p className="text-text-hint text-xs">+ {order.items.length - 2} item lainnya</p>
                  )}
                </div>

                <div className="flex justify-between items-center text-sm">
                  <span className="text-text-hint">{formatDate(order.created_at)}</span>
                  <span className="font-bold text-green-primary">{formatRupiah(order.total_amount ?? 0)}</span>
                </div>

                <div className="flex gap-2">
                  <button onClick={() => navigate(`/payment/${order.id}`)}
                    className="btn-outline flex-1 py-2 text-xs">Detail</button>
                  {isPending && (
                    <>
                      {order.payment_url && (
                        <button onClick={() => window.open(order.payment_url, '_blank')}
                          className="btn-primary flex-1 py-2 text-xs">Bayar</button>
                      )}
                      <button onClick={() => handleCancel(order.id)} disabled={cancelling[order.id]}
                        className="btn-danger flex-1 py-2 text-xs disabled:opacity-60">
                        {cancelling[order.id] ? '...' : 'Batal'}
                      </button>
                    </>
                  )}
                </div>
              </div>
            );
          })
        )}
      </div>
    </AppLayout>
  );
}
