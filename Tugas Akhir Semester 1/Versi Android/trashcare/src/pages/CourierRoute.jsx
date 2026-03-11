import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import AppLayout from '../components/AppLayout';
import Header from '../components/Header';
import * as pickupSvc from '../services/pickupService';
import toast from 'react-hot-toast';

export default function CourierRoute() {
  const navigate  = useNavigate();
  const [params]  = useSearchParams();
  const pickupId  = params.get('id');

  const [pickup,   setPickup]   = useState(null);
  const [loading,  setLoading]  = useState(true);
  const [completing, setCompleting] = useState(false);

  useEffect(() => {
    if (!pickupId) { navigate('/courier', { replace: true }); return; }
    (async () => {
      try {
        const res = await pickupSvc.getPickupDetail?.(pickupId);
        setPickup(res?.data?.data ?? res?.data);
      } catch { toast.error('Gagal memuat data pickup'); }
      finally { setLoading(false); }
    })();
  }, [pickupId]); // eslint-disable-line

  const openMaps = () => {
    if (!pickup?.address && !pickup?.latitude) {
      toast.error('Lokasi tidak tersedia');
      return;
    }
    const query = pickup.latitude && pickup.longitude
      ? `${pickup.latitude},${pickup.longitude}`
      : encodeURIComponent(pickup.address);
    window.open(`https://maps.google.com/?q=${query}`, '_blank');
  };

  const handleComplete = async () => {
    if (!window.confirm('Tandai pickup ini sebagai selesai?')) return;
    setCompleting(true);
    try {
      await pickupSvc.completePickup?.(pickupId);
      toast.success('Pickup selesai!');
      navigate('/courier', { replace: true });
    } catch { toast.error('Gagal menyelesaikan pickup'); }
    finally { setCompleting(false); }
  };

  if (loading) return (
    <AppLayout hideNav>
      <Header title="Rute Pickup" showBack />
      <div className="flex justify-center pt-20">
        <span className="w-8 h-8 border-2 border-green-primary border-t-transparent rounded-full animate-spin" />
      </div>
    </AppLayout>
  );

  return (
    <AppLayout hideNav>
      <Header title="Rute Pickup" showBack />

      <div className="page-content px-4 pt-4 pb-8 flex flex-col gap-4">

        {/* map placeholder */}
        <div className="bg-green-light/40 rounded-2xl h-52 flex flex-col items-center justify-center gap-2
                        border-2 border-dashed border-green-primary/30 cursor-pointer"
             onClick={openMaps}>
          <span className="text-5xl">🗺️</span>
          <p className="text-green-primary font-semibold text-sm">Buka di Google Maps</p>
          <p className="text-text-hint text-xs">Ketuk untuk navigasi</p>
        </div>

        {/* pickup info */}
        {pickup && (
          <div className="card p-4 flex flex-col gap-3">
            <p className="section-title">Info Pickup #{pickup.id}</p>

            {[
              { icon: '👤', label: 'Pelanggan',    value: pickup.user?.name ?? '-' },
              { icon: '📞', label: 'Telepon',      value: pickup.user?.phone ?? '-' },
              { icon: '📍', label: 'Alamat',       value: pickup.address ?? '-' },
              { icon: '🗑️', label: 'Jenis Sampah', value: pickup.waste_type ?? '-' },
              { icon: '📝', label: 'Catatan',      value: pickup.notes ?? '-' },
            ].map(r => (
              <div key={r.label} className="flex items-start gap-2">
                <span className="text-xl flex-shrink-0">{r.icon}</span>
                <div className="flex-1 min-w-0">
                  <p className="text-text-hint text-[10px]">{r.label}</p>
                  <p className="text-text-primary text-sm font-medium">{r.value}</p>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* actions */}
        <button onClick={openMaps}
          className="btn-primary py-4 flex items-center justify-center gap-2">
          🗺️ Navigasi ke Lokasi
        </button>

        {pickup?.status === 'accepted' && (
          <button onClick={handleComplete} disabled={completing}
            className="btn-outline py-3.5 flex items-center justify-center gap-2
                       border-green-primary text-green-primary disabled:opacity-60">
            {completing
              ? <span className="w-4 h-4 border-2 border-green-primary border-t-transparent rounded-full animate-spin" />
              : '🏁'}
            Tandai Selesai
          </button>
        )}
      </div>
    </AppLayout>
  );
}
