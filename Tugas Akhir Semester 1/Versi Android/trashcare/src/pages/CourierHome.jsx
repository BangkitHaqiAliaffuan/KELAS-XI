import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import AppLayout from '../components/AppLayout';
import Header from '../components/Header';
import { formatDate } from '../services/api';
import * as pickupSvc from '../services/pickupService';
import toast from 'react-hot-toast';

const STATUS_STYLE = {
  pending:   { bg: 'bg-yellow-100', text: 'text-yellow-700',    emoji: '⏳', label: 'Menunggu' },
  accepted:  { bg: 'bg-blue-100',   text: 'text-blue-700',      emoji: '🚗', label: 'Diterima' },
  on_the_way:{ bg: 'bg-purple-100', text: 'text-purple-700',    emoji: '🚚', label: 'Dalam Perjalanan' },
  completed: { bg: 'bg-green-light',text: 'text-green-primary', emoji: '✅', label: 'Selesai' },
  cancelled: { bg: 'bg-red-100',    text: 'text-red-600',       emoji: '❌', label: 'Dibatal' },
};

const TABS = ['Semua', 'Pending', 'Diterima', 'Selesai'];

export default function CourierHome() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [tab,      setTab]      = useState('Semua');
  const [pickups,  setPickups]  = useState([]);
  const [loading,  setLoading]  = useState(false);
  const [acting,   setActing]   = useState({});

  const STATUS_FILTER = {
    'Semua':    null,
    'Pending':  'pending',
    'Diterima': 'accepted',
    'Selesai':  'completed',
  };

  const load = async () => {
    setLoading(true);
    try {
      const res = await pickupSvc.getCourierPickups?.({ status: STATUS_FILTER[tab] });
      setPickups(res?.data?.data ?? []);
    } catch { toast.error('Gagal memuat data'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, [tab]); // eslint-disable-line

  const handleAccept = async id => {
    setActing(p => ({ ...p, [id]: 'accepting' }));
    try {
      await pickupSvc.acceptPickup?.(id);
      toast.success('Pickup diterima!');
      load();
    } catch { toast.error('Gagal'); }
    finally { setActing(p => ({ ...p, [id]: null })); }
  };

  const handleComplete = async id => {
    setActing(p => ({ ...p, [id]: 'completing' }));
    try {
      await pickupSvc.completePickup?.(id);
      toast.success('Pickup selesai!');
      load();
    } catch { toast.error('Gagal'); }
    finally { setActing(p => ({ ...p, [id]: null })); }
  };

  const stats = {
    total:     pickups.length,
    pending:   pickups.filter(p => p.status === 'pending').length,
    accepted:  pickups.filter(p => p.status === 'accepted').length,
    completed: pickups.filter(p => p.status === 'completed').length,
  };

  return (
    <AppLayout hideNav>
      <Header title="Kurir Dashboard" showBack={false} />

      <div className="page-content">
        {/* hero stats */}
        <div className="bg-gradient-to-br from-green-primary to-green-medium px-5 pt-6 pb-6">
          <p className="text-white/70 text-sm mb-1">Selamat datang,</p>
          <p className="text-white font-extrabold text-xl">{user?.name} 🚚</p>
          <div className="grid grid-cols-4 gap-2 mt-4">
            {[
              { label: 'Total',    value: stats.total },
              { label: 'Pending',  value: stats.pending },
              { label: 'Aktif',    value: stats.accepted },
              { label: 'Selesai',  value: stats.completed },
            ].map(s => (
              <div key={s.label} className="bg-white/15 rounded-xl py-2.5 px-1 text-center">
                <p className="text-white font-extrabold text-lg">{s.value}</p>
                <p className="text-white/70 text-[10px]">{s.label}</p>
              </div>
            ))}
          </div>
        </div>

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

        {/* list */}
        <div className="px-4 pt-4 flex flex-col gap-3 pb-6">
          {loading ? (
            <div className="flex justify-center pt-12">
              <span className="w-8 h-8 border-2 border-green-primary border-t-transparent rounded-full animate-spin" />
            </div>
          ) : pickups.length === 0 ? (
            <div className="flex flex-col items-center gap-2 pt-16 text-center">
              <span className="text-5xl">🚚</span>
              <p className="font-semibold text-text-primary">Tidak ada tugas</p>
            </div>
          ) : (
            pickups.map(pickup => {
              const s = STATUS_STYLE[pickup.status] ?? STATUS_STYLE.pending;
              return (
                <div key={pickup.id} className="card p-4 flex flex-col gap-3">
                  <div className="flex items-start justify-between gap-2">
                    <div className="flex-1 min-w-0">
                      <p className="font-semibold text-text-primary text-sm">
                        Pickup #{pickup.id}
                      </p>
                      <p className="text-text-secondary text-xs mt-0.5 truncate">
                        {pickup.user?.name ?? 'Pengguna'}
                      </p>
                      <p className="text-text-hint text-xs">{formatDate(pickup.created_at)}</p>
                    </div>
                    <span className={`${s.bg} ${s.text} text-[10px] font-bold px-2 py-0.5 rounded-full flex-shrink-0`}>
                      {s.emoji} {s.label}
                    </span>
                  </div>

                  {pickup.address && (
                    <div className="flex items-start gap-2">
                      <span className="text-lg flex-shrink-0">📍</span>
                      <p className="text-text-secondary text-xs leading-snug">{pickup.address}</p>
                    </div>
                  )}

                  {pickup.waste_type && (
                    <p className="text-xs">🗑️ <span className="text-text-secondary">{pickup.waste_type}</span></p>
                  )}

                  <div className="flex gap-2">
                    <button onClick={() => navigate(`/courier/route?id=${pickup.id}`)}
                      className="btn-outline flex-1 py-2 text-xs">🗺️ Rute</button>
                    {pickup.status === 'pending' && (
                      <button onClick={() => handleAccept(pickup.id)}
                        disabled={acting[pickup.id]}
                        className="btn-primary flex-1 py-2 text-xs disabled:opacity-60">
                        {acting[pickup.id] === 'accepting' ? '...' : '✅ Terima'}
                      </button>
                    )}
                    {pickup.status === 'accepted' && (
                      <button onClick={() => handleComplete(pickup.id)}
                        disabled={acting[pickup.id]}
                        className="btn-primary flex-1 py-2 text-xs disabled:opacity-60">
                        {acting[pickup.id] === 'completing' ? '...' : '🏁 Selesai'}
                      </button>
                    )}
                  </div>
                </div>
              );
            })
          )}
        </div>
      </div>
    </AppLayout>
  );
}
