import React, { useEffect, useState } from 'react';
import AppLayout from '../components/AppLayout';
import Header from '../components/Header';
import { formatDate } from '../services/api';
import * as pickupSvc from '../services/pickupService';
import toast from 'react-hot-toast';

const STATUS_STYLE = {
  pending:    { bg: 'bg-yellow-100', text: 'text-yellow-700',    emoji: '⏳', label: 'Menunggu' },
  accepted:   { bg: 'bg-blue-100',   text: 'text-blue-700',      emoji: '🚗', label: 'Diterima' },
  on_the_way: { bg: 'bg-purple-100', text: 'text-purple-700',    emoji: '🚚', label: 'Dalam Perjalanan' },
  completed:  { bg: 'bg-green-100',  text: 'text-green-700',     emoji: '✅', label: 'Selesai' },
  cancelled:  { bg: 'bg-red-100',    text: 'text-red-600',       emoji: '❌', label: 'Dibatal' },
};

const WASTE_TYPES = [
  { key: 'organic',    label: 'Organik',     emoji: '🌿' },
  { key: 'plastic',    label: 'Plastik',     emoji: '🧴' },
  { key: 'electronic', label: 'Elektronik',  emoji: '📱' },
  { key: 'glass',      label: 'Kaca',        emoji: '🪟' },
];

export default function Pickup() {
  const [tab,      setTab]      = useState('request'); // 'request' | 'history'
  const [pickups,  setPickups]  = useState([]);
  const [loading,  setLoading]  = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [cancelling, setCancelling] = useState({});

  const [form, setForm] = useState({
    address:              '',
    pickup_date:          '',
    pickup_time:          '',
    trash_types:          [],   // array of keys e.g. ['plastic','organic']
    estimated_weight_kg:  '',
    notes:                '',
  });

  const set = (k, v) => setForm(p => ({ ...p, [k]: v }));

  const toggleTrashType = (key) => {
    setForm(p => ({
      ...p,
      trash_types: p.trash_types.includes(key)
        ? p.trash_types.filter(t => t !== key)
        : [...p.trash_types, key],
    }));
  };

  const loadPickups = async () => {
    setLoading(true);
    try {
      const res = await pickupSvc.getPickups();
      setPickups(res.data?.data ?? []);
    } catch {
      toast.error('Gagal memuat riwayat pickup');
    } finally { setLoading(false); }
  };

  useEffect(() => {
    if (tab === 'history') loadPickups();
  }, [tab]);

  const handleSubmit = async e => {
    e.preventDefault();
    if (!form.address.trim())       { toast.error('Alamat wajib diisi'); return; }
    if (!form.pickup_date)          { toast.error('Tanggal pickup wajib diisi'); return; }
    if (!form.pickup_time)          { toast.error('Jam pickup wajib diisi'); return; }
    if (form.trash_types.length === 0) { toast.error('Pilih minimal 1 jenis sampah'); return; }

    setSubmitting(true);
    try {
      const payload = {
        address:             form.address,
        pickup_date:         form.pickup_date,
        pickup_time:         form.pickup_time,
        trash_types:         form.trash_types,
        notes:               form.notes || undefined,
        estimated_weight_kg: form.estimated_weight_kg ? Number(form.estimated_weight_kg) : undefined,
      };
      await pickupSvc.createPickup(payload);
      toast.success('Permintaan pickup berhasil dikirim! 🚚');
      setForm({ address: '', pickup_date: '', pickup_time: '', trash_types: [], estimated_weight_kg: '', notes: '' });
      setTab('history');
    } catch (e) {
      toast.error(e?.message ?? 'Gagal membuat pickup');
    } finally { setSubmitting(false); }
  };

  const handleCancel = async id => {
    if (!window.confirm('Batalkan pickup ini?')) return;
    setCancelling(p => ({ ...p, [id]: true }));
    try {
      await pickupSvc.cancelPickup(id);
      toast.success('Pickup dibatalkan');
      setPickups(p => p.map(x => x.id === id ? { ...x, status: 'cancelled' } : x));
    } catch { toast.error('Gagal membatalkan'); }
    finally { setCancelling(p => ({ ...p, [id]: false })); }
  };

  return (
    <AppLayout>
      <Header title="Pickup Sampah" showBack={false} />

      {/* tabs */}
      <div className="flex border-b border-divider bg-white">
        {[
          { key: 'request', label: '📋 Buat Pickup' },
          { key: 'history', label: '🕐 Riwayat' },
        ].map(t => (
          <button key={t.key} onClick={() => setTab(t.key)}
            className={`flex-1 py-3 text-sm font-semibold border-b-2 transition-colors
              ${tab === t.key
                ? 'border-green-primary text-green-primary'
                : 'border-transparent text-text-secondary'}`}>
            {t.label}
          </button>
        ))}
      </div>

      <div className="page-content px-4 pt-4">

        {/* ── TAB: REQUEST ── */}
        {tab === 'request' && (
          <form onSubmit={handleSubmit} className="flex flex-col gap-4 pb-6">

            {/* Info banner */}
            <div className="bg-green-light rounded-2xl p-4 flex gap-3">
              <span className="text-2xl flex-shrink-0">♻️</span>
              <div>
                <p className="font-bold text-green-primary text-sm">Jadwalkan Pickup Sampah</p>
                <p className="text-text-secondary text-xs mt-0.5">
                  Kurir kami akan datang mengambil sampah ke lokasi Anda.
                </p>
              </div>
            </div>

            {/* alamat */}
            <div>
              <label className="label">Alamat Pickup <span className="text-red-500">*</span></label>
              <textarea
                value={form.address}
                onChange={e => set('address', e.target.value)}
                rows={3}
                placeholder="Masukkan alamat lengkap tempat sampah diambil..."
                className="input-field mt-1 resize-none"
              />
            </div>

            {/* tanggal + jam */}
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="label">Tanggal Pickup <span className="text-red-500">*</span></label>
                <input
                  type="date"
                  value={form.pickup_date}
                  min={new Date().toISOString().split('T')[0]}
                  onChange={e => set('pickup_date', e.target.value)}
                  className="input-field mt-1"
                />
              </div>
              <div>
                <label className="label">Jam Pickup <span className="text-red-500">*</span></label>
                <input
                  type="time"
                  value={form.pickup_time}
                  onChange={e => set('pickup_time', e.target.value)}
                  className="input-field mt-1"
                />
              </div>
            </div>

            {/* jenis sampah */}
            <div>
              <label className="label">Jenis Sampah <span className="text-red-500">*</span> <span className="text-text-hint font-normal">(bisa pilih lebih dari 1)</span></label>
              <div className="flex flex-wrap gap-2 mt-2">
                {WASTE_TYPES.map(w => {
                  const selected = form.trash_types.includes(w.key);
                  return (
                    <button key={w.key} type="button" onClick={() => toggleTrashType(w.key)}
                      className={`px-3 py-1.5 rounded-full text-xs font-semibold border transition-colors flex items-center gap-1
                        ${selected
                          ? 'bg-green-primary text-white border-green-primary'
                          : 'bg-white text-text-secondary border-divider'}`}>
                      {w.emoji} {w.label}
                      {selected && <span className="ml-0.5">✓</span>}
                    </button>
                  );
                })}
              </div>
            </div>

            {/* berat estimasi */}
            <div>
              <label className="label">Estimasi Berat (kg)</label>
              <input
                type="number"
                min="0.1"
                step="0.5"
                value={form.estimated_weight_kg}
                onChange={e => set('estimated_weight_kg', e.target.value)}
                placeholder="Contoh: 5"
                className="input-field mt-1"
              />
            </div>

            {/* catatan */}
            <div>
              <label className="label">Catatan (opsional)</label>
              <input
                value={form.notes}
                onChange={e => set('notes', e.target.value)}
                placeholder="Misal: sampah di depan pintu..."
                className="input-field mt-1"
              />
            </div>

            <button type="submit" disabled={submitting}
              className="btn-primary py-4 mt-1 disabled:opacity-60 flex items-center justify-center gap-2">
              {submitting
                ? <><span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" /> Mengirim...</>
                : '🚚 Kirim Permintaan Pickup'}
            </button>
          </form>
        )}

        {/* ── TAB: HISTORY ── */}
        {tab === 'history' && (
          <div className="flex flex-col gap-3 pb-6">
            {loading ? (
              <div className="flex justify-center pt-12">
                <span className="w-8 h-8 border-2 border-green-primary border-t-transparent rounded-full animate-spin" />
              </div>
            ) : pickups.length === 0 ? (
              <div className="flex flex-col items-center gap-3 pt-16 text-center">
                <span className="text-5xl">🚚</span>
                <p className="font-bold text-text-primary">Belum ada pickup</p>
                <p className="text-text-secondary text-sm">Buat permintaan pickup pertamamu!</p>
                <button onClick={() => setTab('request')} className="btn-primary mt-2 px-8">
                  Buat Pickup
                </button>
              </div>
            ) : (
              pickups.map(p => {
                const s = STATUS_STYLE[p.status] ?? STATUS_STYLE.pending;
                return (
                  <div key={p.id} className="card p-4 flex flex-col gap-2">
                    <div className="flex items-start justify-between gap-2">
                      <div className="flex-1 min-w-0">
                        <p className="font-semibold text-text-primary text-sm">
                          Pickup #{p.id}
                        </p>
                        <p className="text-text-hint text-xs">{formatDate(p.created_at)}</p>
                      </div>
                      <span className={`${s.bg} ${s.text} text-[10px] font-bold px-2 py-0.5 rounded-full flex-shrink-0`}>
                        {s.emoji} {s.label}
                      </span>
                    </div>

                    {p.address && (
                      <p className="text-text-secondary text-xs flex gap-1.5 items-start">
                        <span>📍</span>
                        <span className="line-clamp-2">{p.address}</span>
                      </p>
                    )}

                    {p.waste_type && (
                      <span className="chip w-fit">♻️ {p.waste_type}</span>
                    )}

                    {p.status === 'pending' && (
                      <button
                        onClick={() => handleCancel(p.id)}
                        disabled={cancelling[p.id]}
                        className="btn-danger py-2 text-sm mt-1 disabled:opacity-60">
                        {cancelling[p.id] ? '...' : '❌ Batalkan'}
                      </button>
                    )}
                  </div>
                );
              })
            )}
          </div>
        )}
      </div>
    </AppLayout>
  );
}
