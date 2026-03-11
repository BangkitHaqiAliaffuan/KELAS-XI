import React, { useEffect, useState } from 'react';
import AppLayout from '../components/AppLayout';
import Header from '../components/Header';
import * as authSvc from '../services/authService';
import toast from 'react-hot-toast';

const BLANK = { label: '', street: '', city: '', province: '', postal_code: '', is_default: false };

export default function Address() {
  const [addresses,  setAddresses]  = useState([]);
  const [loading,    setLoading]    = useState(false);
  const [showForm,   setShowForm]   = useState(false);
  const [editing,    setEditing]    = useState(null);   // address obj or null
  const [form,       setForm]       = useState({ ...BLANK });
  const [saving,     setSaving]     = useState(false);
  const [deleting,   setDeleting]   = useState({});

  const set = (k, v) => setForm(p => ({ ...p, [k]: v }));

  const load = async () => {
    setLoading(true);
    try {
      const res = await authSvc.getAddresses?.();
      setAddresses(res?.data?.data ?? res?.data ?? []);
    } catch {} finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const openAdd = () => { setEditing(null); setForm({ ...BLANK }); setShowForm(true); };
  const openEdit = a => { setEditing(a); setForm({ ...a }); setShowForm(true); };

  const handleSubmit = async e => {
    e.preventDefault();
    if (!form.label.trim() || !form.street.trim()) {
      toast.error('Label dan alamat wajib diisi'); return;
    }
    setSaving(true);
    try {
      if (editing) {
        await authSvc.updateAddress?.(editing.id, form);
        toast.success('Alamat diperbarui!');
      } else {
        await authSvc.createAddress?.(form);
        toast.success('Alamat ditambahkan!');
      }
      setShowForm(false);
      await load();
    } catch (e) { toast.error(e?.message ?? 'Gagal menyimpan'); }
    finally { setSaving(false); }
  };

  const handleDelete = async id => {
    if (!window.confirm('Hapus alamat ini?')) return;
    setDeleting(p => ({ ...p, [id]: true }));
    try {
      await authSvc.deleteAddress?.(id);
      toast.success('Alamat dihapus');
      await load();
    } catch { toast.error('Gagal menghapus'); }
    finally { setDeleting(p => ({ ...p, [id]: false })); }
  };

  return (
    <AppLayout hideNav>
      <Header
        title="Alamat Saya"
        showBack
        rightContent={
          <button onClick={openAdd}
            className="bg-white text-green-primary font-bold text-sm px-3 py-1 rounded-xl">
            ＋ Tambah
          </button>
        }
      />

      <div className="page-content px-4 pt-4 pb-6 flex flex-col gap-3">
        {/* ── Form ── */}
        {showForm && (
          <form onSubmit={handleSubmit} className="card p-4 flex flex-col gap-3">
            <p className="section-title">{editing ? 'Edit Alamat' : 'Alamat Baru'}</p>

            {[
              { key: 'label',       label: 'Label (cth: Rumah)',  type: 'text',   placeholder: 'Rumah / Kantor' },
              { key: 'street',      label: 'Jalan / Alamat',      type: 'text',   placeholder: 'Jl. Merdeka No.1' },
              { key: 'city',        label: 'Kota',                type: 'text',   placeholder: 'Jakarta' },
              { key: 'province',    label: 'Provinsi',            type: 'text',   placeholder: 'DKI Jakarta' },
              { key: 'postal_code', label: 'Kode Pos',            type: 'number', placeholder: '12345' },
            ].map(f => (
              <div key={f.key}>
                <label className="label">{f.label}</label>
                <input value={form[f.key]} onChange={e => set(f.key, e.target.value)}
                  type={f.type} placeholder={f.placeholder} className="input-field mt-1" />
              </div>
            ))}

            <label className="flex items-center gap-2 cursor-pointer">
              <input type="checkbox" checked={form.is_default}
                onChange={e => set('is_default', e.target.checked)}
                className="accent-green-primary w-4 h-4" />
              <span className="text-sm text-text-secondary">Jadikan alamat utama</span>
            </label>

            <div className="flex gap-2 mt-1">
              <button type="button" onClick={() => setShowForm(false)}
                className="btn-outline flex-1 py-2.5">Batal</button>
              <button type="submit" disabled={saving}
                className="btn-primary flex-1 py-2.5 disabled:opacity-60 flex items-center justify-center gap-1">
                {saving ? <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" /> : null}
                Simpan
              </button>
            </div>
          </form>
        )}

        {/* ── List ── */}
        {loading ? (
          <div className="flex justify-center pt-10">
            <span className="w-8 h-8 border-2 border-green-primary border-t-transparent rounded-full animate-spin" />
          </div>
        ) : addresses.length === 0 && !showForm ? (
          <div className="flex flex-col items-center gap-3 pt-16 text-center">
            <span className="text-5xl">📍</span>
            <p className="font-bold text-text-primary">Belum ada alamat</p>
            <p className="text-text-secondary text-sm">Tambahkan alamat pengiriman</p>
            <button onClick={openAdd} className="btn-primary mt-2 px-8">Tambah Alamat</button>
          </div>
        ) : (
          addresses.map(a => (
            <div key={a.id} className="card p-4 flex gap-3">
              <div className="w-10 h-10 bg-green-light rounded-xl flex items-center justify-center flex-shrink-0">
                <span className="text-xl">📍</span>
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2">
                  <p className="font-semibold text-text-primary text-sm">{a.label}</p>
                  {a.is_default && (
                    <span className="bg-green-light text-green-primary text-[9px] font-bold px-2 py-0.5 rounded-full">
                      Utama
                    </span>
                  )}
                </div>
                <p className="text-text-secondary text-xs mt-0.5">{a.street}</p>
                <p className="text-text-hint text-xs">{[a.city, a.province, a.postal_code].filter(Boolean).join(', ')}</p>
              </div>
              <div className="flex flex-col gap-1 flex-shrink-0">
                <button onClick={() => openEdit(a)}
                  className="text-green-primary text-xs font-semibold">Edit</button>
                <button onClick={() => handleDelete(a.id)} disabled={deleting[a.id]}
                  className="text-red-400 text-xs font-semibold disabled:opacity-50">
                  {deleting[a.id] ? '...' : 'Hapus'}
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </AppLayout>
  );
}
