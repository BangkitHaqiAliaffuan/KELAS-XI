import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

export default function Login() {
  const { login, isLoading } = useAuth();
  const navigate = useNavigate();

  const [form, setForm]   = useState({ email: '', password: '' });
  const [errors, setErrors] = useState({});
  const [showPass, setShowPass] = useState(false);

  const validate = () => {
    const e = {};
    if (!form.email)    e.email    = 'Email wajib diisi';
    else if (!/\S+@\S+\.\S+/.test(form.email)) e.email = 'Email tidak valid';
    if (!form.password) e.password = 'Password wajib diisi';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (ev) => {
    ev.preventDefault();
    if (!validate()) return;
    const res = await login(form.email, form.password);
    if (res.ok) {
      toast.success('Selamat datang! 👋');
      navigate(res.role === 'courier' ? '/courier' : '/home', { replace: true });
    } else {
      toast.error(res.message);
    }
  };

  return (
    <div className="app-shell">
      <div className="flex flex-col min-h-screen">
        {/* Header */}
        <div className="bg-green-primary px-6 pt-12 pb-10">
          <div className="text-5xl mb-3">♻️</div>
          <h1 className="text-white text-2xl font-bold">TrashCare</h1>
          <p className="text-green-light text-sm mt-1">Platform pengelolaan sampah pintar</p>
        </div>

        {/* Form */}
        <div className="flex-1 px-6 pt-8 pb-6">
          <h2 className="text-text-primary text-xl font-bold mb-1">Masuk</h2>
          <p className="text-text-secondary text-sm mb-6">Login untuk melanjutkan</p>

          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            {/* Email */}
            <div>
              <label className="label">Email</label>
              <input
                type="email"
                className={`input-field ${errors.email ? 'border-red-500' : ''}`}
                placeholder="email@contoh.com"
                value={form.email}
                onChange={e => { setForm(p => ({ ...p, email: e.target.value })); setErrors(p => ({ ...p, email: '' })); }}
                autoComplete="email"
              />
              {errors.email && <p className="text-red-500 text-xs mt-1">{errors.email}</p>}
            </div>

            {/* Password */}
            <div>
              <label className="label">Password</label>
              <div className="relative">
                <input
                  type={showPass ? 'text' : 'password'}
                  className={`input-field pr-12 ${errors.password ? 'border-red-500' : ''}`}
                  placeholder="••••••••"
                  value={form.password}
                  onChange={e => { setForm(p => ({ ...p, password: e.target.value })); setErrors(p => ({ ...p, password: '' })); }}
                  autoComplete="current-password"
                />
                <button
                  type="button"
                  onClick={() => setShowPass(p => !p)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-text-secondary text-sm"
                >
                  {showPass ? '🙈' : '👁️'}
                </button>
              </div>
              {errors.password && <p className="text-red-500 text-xs mt-1">{errors.password}</p>}
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="btn-primary w-full h-12 mt-2"
            >
              {isLoading ? (
                <span className="flex items-center gap-2">
                  <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  Masuk...
                </span>
              ) : 'Masuk'}
            </button>
          </form>

          <p className="text-center text-text-secondary text-sm mt-6">
            Belum punya akun?{' '}
            <Link to="/register" className="text-green-primary font-semibold">
              Daftar sekarang
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
