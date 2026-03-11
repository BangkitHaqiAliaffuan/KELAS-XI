import React, { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useMarketplace } from '../context/MarketplaceContext';
import AppLayout from '../components/AppLayout';
import { formatRupiah, formatDate } from '../services/api';
import * as pickupSvc from '../services/pickupService';

const PICKUP_STATUS_STYLE = {
  pending:   { bg: 'bg-yellow-100',  text: 'text-yellow-700',  label: 'Menunggu' },
  accepted:  { bg: 'bg-blue-100',    text: 'text-blue-700',    label: 'Diproses' },
  completed: { bg: 'bg-green-light', text: 'text-green-primary',label: 'Selesai' },
  cancelled: { bg: 'bg-red-100',     text: 'text-red-600',     label: 'Dibatal' },
};

export default function Home() {
  const { user } = useAuth();
  const { fetchListings, listings } = useMarketplace();
  const navigate = useNavigate();

  const [pickups, setPickups]   = useState([]);
  const [isLoading, setLoading] = useState(false);

  useEffect(() => {
    fetchListings({ per_page: 4 });
    loadPickups();
    // eslint-disable-next-line
  }, []);

  const loadPickups = async () => {
    setLoading(true);
    try {
      const res = await pickupSvc.getPickups();
      setPickups((res.data?.data ?? []).slice(0, 3));
    } catch {}
    finally { setLoading(false); }
  };

  const firstName = user?.name?.split(' ')[0] ?? 'Pengguna';
  const greeting  = (() => {
    const h = new Date().getHours();
    if (h < 11) return 'Selamat pagi';
    if (h < 15) return 'Selamat siang';
    if (h < 18) return 'Selamat sore';
    return 'Selamat malam';
  })();

  return (
    <AppLayout>
      <div className="page-content">
        {/* ── Hero banner ── */}
        <div className="bg-gradient-to-br from-green-primary to-green-medium px-5 pt-10 pb-8">
          <p className="text-green-light text-sm mb-1">{greeting},</p>
          <h1 className="text-white text-2xl font-extrabold">{firstName} 👋</h1>
          <p className="text-white/70 text-xs mt-1">Yuk jaga bumi hari ini!</p>

          {/* Quick stats */}
          <div className="flex gap-3 mt-5">
            {[
              { icon: '🚚', label: 'Total Pickup', value: pickups.length },
              { icon: '✅', label: 'Selesai',      value: pickups.filter(p => p.status === 'completed').length },
            ].map(s => (
              <div key={s.label} className="flex-1 bg-white/15 rounded-2xl px-4 py-3">
                <p className="text-white/70 text-xs">{s.icon} {s.label}</p>
                <p className="text-white text-xl font-bold">{s.value}</p>
              </div>
            ))}
          </div>
        </div>

        <div className="px-4 pt-5 flex flex-col gap-5">

          {/* ── Quick Actions ── */}
          <section>
            <h2 className="section-title mb-3">Aksi Cepat</h2>
            <div className="grid grid-cols-4 gap-2">
              {[
                { emoji: '🚚', label: 'Pickup',      to: '/pickup' },
                { emoji: '🛍️', label: 'Belanja',     to: '/marketplace' },
                { emoji: '🛒', label: 'Keranjang',   to: '/cart' },
                { emoji: '👤', label: 'Profil',      to: '/profile' },
              ].map(a => (
                <Link key={a.label} to={a.to}
                  className="card flex flex-col items-center py-4 gap-1.5 active:bg-green-light transition-colors">
                  <span className="text-2xl">{a.emoji}</span>
                  <span className="text-text-secondary text-[10px] font-medium">{a.label}</span>
                </Link>
              ))}
            </div>
          </section>

          {/* ── Request Pickup banner ── */}
          <div className="bg-gradient-to-r from-orange-accent to-orange-dark rounded-2xl p-4
                          flex items-center gap-4 shadow-sm">
            <span className="text-4xl flex-shrink-0">🗑️</span>
            <div className="flex-1 min-w-0">
              <p className="text-white font-bold text-sm">Sampah menumpuk?</p>
              <p className="text-white/80 text-xs">Jadwalkan pickup sekarang</p>
            </div>
            <button
              onClick={() => navigate('/pickup')}
              className="bg-white text-orange-accent font-bold text-xs px-3 py-2 rounded-xl flex-shrink-0">
              Pesan
            </button>
          </div>

          {/* ── Riwayat Pickup ── */}
          <section>
            <div className="flex items-center justify-between mb-3">
              <h2 className="section-title">Pickup Terbaru</h2>
              <Link to="/profile/orders" className="text-green-primary text-xs font-semibold">Lihat semua</Link>
            </div>

            {isLoading ? (
              <div className="flex justify-center py-6">
                <span className="w-6 h-6 border-2 border-green-primary border-t-transparent rounded-full animate-spin" />
              </div>
            ) : pickups.length === 0 ? (
              <div className="card px-4 py-8 flex flex-col items-center gap-2 text-center">
                <span className="text-3xl">🚚</span>
                <p className="text-text-secondary text-sm">Belum ada pickup</p>
                <button onClick={() => navigate('/profile/orders')}
                  className="chip mt-1 cursor-pointer">Pesan Sekarang</button>
              </div>
            ) : (
              <div className="flex flex-col gap-2">
                {pickups.map(p => {
                  const s = PICKUP_STATUS_STYLE[p.status] ?? PICKUP_STATUS_STYLE.pending;
                  return (
                    <div key={p.id} className="card px-4 py-3 flex items-center gap-3">
                      <div className="w-10 h-10 bg-green-light rounded-xl flex items-center justify-center flex-shrink-0">
                        <span className="text-xl">🗑️</span>
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="font-semibold text-text-primary text-sm truncate">
                          {p.waste_type ?? 'Sampah Umum'}
                        </p>
                        <p className="text-text-hint text-xs">{formatDate(p.created_at)}</p>
                      </div>
                      <span className={`${s.bg} ${s.text} text-[10px] font-bold px-2 py-1 rounded-full`}>
                        {s.label}
                      </span>
                    </div>
                  );
                })}
              </div>
            )}
          </section>

          {/* ── Marketplace preview ── */}
          <section className="pb-4">
            <div className="flex items-center justify-between mb-3">
              <h2 className="section-title">Produk Terbaru</h2>
              <Link to="/marketplace" className="text-green-primary text-xs font-semibold">Lihat semua</Link>
            </div>
            <div className="grid grid-cols-2 gap-3">
              {listings.slice(0, 4).map(l => (
                <Link key={l.id} to={`/marketplace/${l.id}`}
                  className="card overflow-hidden active:opacity-80 transition-opacity">
                  <div className="h-28 bg-divider flex items-center justify-center overflow-hidden">
                    {l.image_url
                      ? <img src={l.image_url} alt={l.name}
                          className="w-full h-full object-cover" />
                      : <span className="text-4xl">📦</span>}
                  </div>
                  <div className="p-2.5">
                    <p className="text-text-primary font-semibold text-xs truncate">{l.name}</p>
                    <p className="text-green-primary font-bold text-sm mt-0.5">{formatRupiah(l.price)}</p>
                  </div>
                </Link>
              ))}
            </div>
          </section>
        </div>
      </div>
    </AppLayout>
  );
}
