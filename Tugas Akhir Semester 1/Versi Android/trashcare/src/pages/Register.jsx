import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

export default function Register() {
  const { register, isLoading } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: '', email: '', password: '', password_confirmation: '', phone: '', role: 'user'
  });
  const [errors, setErrors] = useState({});
  const [showPass, setShowPass] = useState(false);

  const set = (key, val) => {
    setForm(p => ({ ...p, [key]: val }));
    setErrors(p => ({ ...p, [key]: '' }));
  };

  const validate = () => {
    const e = {};
    if (!form.name)     e.name = 'Nama wajib diisi';
    if (!form.email)    e.email = 'Email wajib diisi';
    else if (!/\S+@\S+\.\S+/.test(form.email)) e.email = 'Email tidak valid';
    if (!form.password) e.password = 'Password wajib diisi';
    else if (form.password.length < 8) e.password = 'Password minimal 8 karakter';
    if (form.password !== form.password_confirmation) e.password_confirmation = 'Password tidak cocok';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (ev) => {
    ev.preventDefault();
    if (!validate()) return;
    const res = await register(form);
    if (res.ok) {
      toast.success('Akun berhasil dibuat! 🎉');
      navigate('/home', { replace: true });
    } else {
      toast.error(res.message);
    }
  };

  return (
    <div className="app-shell">
      <div className="flex flex-col min-h-screen overflow-y-auto">
        {/* Header */}
        <div className="bg-green-primary px-6 pt-10 pb-8">
          <div className="text-4xl mb-2">♻️</div>
          <h1 className="text-white text-xl font-bold">TrashCare</h1>
          <p className="text-green-light text-xs mt-1">Buat akun baru</p>
        </div>

        <div className="flex-1 px-6 pt-6 pb-8">
          <h2 className="text-text-primary text-xl font-bold mb-1">Daftar</h2>
          <p className="text-text-secondary text-sm mb-5">Isi data diri kamu</p>

          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            {/* Role selector */}
            <div>
              <label className="label">Daftar sebagai</label>
              <div className="flex gap-3">
                {[['user', '🧑 Pengguna'], ['courier', '🚚 Kurir']].map(([val, label]) => (
                  <button
                    key={val} type="button"
                    onClick={() => set('role', val)}
                    className={`flex-1 py-3 rounded-xl border-2 text-sm font-semibold transition-colors ${
                      form.role === val
                        ? 'bg-green-primary text-white border-green-primary'
                        : 'bg-white text-text-secondary border-divider'
                    }`}
                  >{label}</button>
                ))}
              </div>
            </div>

            {/* Name */}
            <div>
              <label className="label">Nama Lengkap</label>
              <input className={`input-field ${errors.name ? 'border-red-500' : ''}`}
                placeholder="John Doe" value={form.name}
                onChange={e => set('name', e.target.value)} />
              {errors.name && <p className="text-red-500 text-xs mt-1">{errors.name}</p>}
            </div>

            {/* Email */}
            <div>
              <label className="label">Email</label>
              <input type="email" className={`input-field ${errors.email ? 'border-red-500' : ''}`}
                placeholder="email@contoh.com" value={form.email}
                onChange={e => set('email', e.target.value)} />
              {errors.email && <p className="text-red-500 text-xs mt-1">{errors.email}</p>}
            </div>

            {/* Phone */}
            <div>
              <label className="label">Nomor HP (opsional)</label>
              <input type="tel" className="input-field"
                placeholder="08xxxxxxxxxx" value={form.phone}
                onChange={e => set('phone', e.target.value)} />
            </div>

            {/* Password */}
            <div>
              <label className="label">Password</label>
              <div className="relative">
                <input type={showPass ? 'text' : 'password'}
                  className={`input-field pr-12 ${errors.password ? 'border-red-500' : ''}`}
                  placeholder="Min. 8 karakter" value={form.password}
                  onChange={e => set('password', e.target.value)} />
                <button type="button" onClick={() => setShowPass(p => !p)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-text-secondary text-sm">
                  {showPass ? '🙈' : '👁️'}
                </button>
              </div>
              {errors.password && <p className="text-red-500 text-xs mt-1">{errors.password}</p>}
            </div>

            {/* Confirm */}
            <div>
              <label className="label">Konfirmasi Password</label>
              <input type="password"
                className={`input-field ${errors.password_confirmation ? 'border-red-500' : ''}`}
                placeholder="Ulangi password" value={form.password_confirmation}
                onChange={e => set('password_confirmation', e.target.value)} />
              {errors.password_confirmation &&
                <p className="text-red-500 text-xs mt-1">{errors.password_confirmation}</p>}
            </div>

            <button type="submit" disabled={isLoading} className="btn-primary w-full h-12 mt-2">
              {isLoading
                ? <span className="flex items-center gap-2">
                    <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                    Mendaftar...
                  </span>
                : 'Daftar Sekarang'}
            </button>
          </form>

          <p className="text-center text-text-secondary text-sm mt-5">
            Sudah punya akun?{' '}
            <Link to="/login" className="text-green-primary font-semibold">Masuk</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
