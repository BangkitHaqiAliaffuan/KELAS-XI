import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import AppLayout from '../components/AppLayout';
import Header from '../components/Header';
import toast from 'react-hot-toast';
import * as authSvc from '../services/authService';

const MENU = [
  { icon: '🛍️', label: 'Pesanan Saya',  to: '/profile/orders' },
  { icon: '🏪', label: 'Toko Saya',     to: '/profile/shop' },
  { icon: '❤️', label: 'Wishlist',      to: '/profile/wishlist' },
  { icon: '📍', label: 'Alamat',        to: '/profile/address' },
];

export default function Profile() {
  const { user, logout, refreshProfile } = useAuth();
  const navigate = useNavigate();

  const [editing,  setEditing]  = useState(false);
  const [name,     setName]     = useState(user?.name ?? '');
  const [phone,    setPhone]    = useState(user?.phone ?? '');
  const [saving,   setSaving]   = useState(false);
  const [loggingOut, setLogout] = useState(false);

  const handleSave = async e => {
    e.preventDefault();
    if (!name.trim()) { toast.error('Nama wajib diisi'); return; }
    setSaving(true);
    try {
      await authSvc.updateProfile({ name, phone });
      await refreshProfile?.();
      toast.success('Profil diperbarui!');
      setEditing(false);
    } catch (e) {
      toast.error(e?.message ?? 'Gagal memperbarui profil');
    } finally { setSaving(false); }
  };

  const handleLogout = async () => {
    if (!window.confirm('Keluar dari akun?')) return;
    setLogout(true);
    try {
      await logout();
      navigate('/login', { replace: true });
    } finally { setLogout(false); }
  };

  return (
    <AppLayout>
      <Header title="Profil" showBack={false} />

      <div className="page-content">
        {/* avatar + name hero */}
        <div className="bg-gradient-to-br from-green-primary to-green-medium px-5 pt-8 pb-8 flex flex-col items-center gap-2">
          <div className="w-20 h-20 bg-white/20 rounded-full flex items-center justify-center">
            <span className="text-4xl">👤</span>
          </div>
          <h2 className="text-white font-extrabold text-xl">{user?.name}</h2>
          <p className="text-white/70 text-sm">{user?.email}</p>
          <span className={`text-xs font-bold px-3 py-1 rounded-full mt-1
            ${user?.role === 'courier' ? 'bg-orange-accent text-white' : 'bg-white/20 text-white'}`}>
            {user?.role === 'courier' ? '🚚 Kurir' : '👤 Pengguna'}
          </span>
        </div>

        <div className="px-4 pt-5 flex flex-col gap-5 pb-4">

          {/* ── Edit profile ── */}
          {!editing ? (
            <div className="card p-4 flex flex-col gap-2">
              <div className="flex items-center justify-between">
                <p className="section-title">Informasi Akun</p>
                <button onClick={() => { setName(user?.name ?? ''); setPhone(user?.phone ?? ''); setEditing(true); }}
                  className="text-green-primary text-xs font-semibold">Edit ✏️</button>
              </div>
              {[
                { label: 'Nama',    value: user?.name  ?? '-' },
                { label: 'Email',   value: user?.email ?? '-' },
                { label: 'Telepon', value: user?.phone ?? '-' },
              ].map(r => (
                <div key={r.label} className="flex justify-between items-center py-1 border-b border-divider last:border-0">
                  <span className="text-text-secondary text-sm">{r.label}</span>
                  <span className="text-text-primary text-sm font-medium">{r.value}</span>
                </div>
              ))}
            </div>
          ) : (
            <form onSubmit={handleSave} className="card p-4 flex flex-col gap-3">
              <p className="section-title">Edit Profil</p>
              <div>
                <label className="label">Nama</label>
                <input value={name} onChange={e => setName(e.target.value)}
                  className="input-field mt-1" placeholder="Nama lengkap" />
              </div>
              <div>
                <label className="label">No. Telepon</label>
                <input value={phone} onChange={e => setPhone(e.target.value)}
                  className="input-field mt-1" placeholder="08xxxxxxxx" type="tel" />
              </div>
              <div className="flex gap-2 mt-1">
                <button type="button" onClick={() => setEditing(false)}
                  className="btn-outline flex-1 py-2.5">Batal</button>
                <button type="submit" disabled={saving}
                  className="btn-primary flex-1 py-2.5 disabled:opacity-60 flex items-center justify-center gap-1">
                  {saving ? <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" /> : null}
                  Simpan
                </button>
              </div>
            </form>
          )}

          {/* ── Menu ── */}
          <div className="card overflow-hidden">
            {MENU.map((m, i) => (
              <button key={m.to} onClick={() => navigate(m.to)}
                className={`w-full flex items-center gap-3 px-4 py-3.5 text-left active:bg-green-light/50 transition-colors
                  ${i < MENU.length - 1 ? 'border-b border-divider' : ''}`}>
                <span className="text-xl">{m.icon}</span>
                <span className="flex-1 text-text-primary font-medium text-sm">{m.label}</span>
                <span className="text-text-hint text-sm">›</span>
              </button>
            ))}
          </div>

          {/* ── Logout ── */}
          <button onClick={handleLogout} disabled={loggingOut}
            className="btn-danger py-3.5 disabled:opacity-60 flex items-center justify-center gap-2">
            {loggingOut
              ? <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
              : '🚪'}
            Keluar
          </button>
        </div>
      </div>
    </AppLayout>
  );
}
