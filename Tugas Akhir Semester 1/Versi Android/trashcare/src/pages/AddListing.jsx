import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMarketplace } from '../context/MarketplaceContext';
import AppLayout from '../components/AppLayout';
import Header from '../components/Header';
import toast from 'react-hot-toast';
import * as marketSvc from '../services/marketplaceService';

const CATEGORIES  = ['Furnitur', 'Elektronik', 'Pakaian', 'Buku', 'Lainnya'];
const CONDITIONS  = ['Baru', 'Bekas'];

export default function AddListing() {
  const navigate = useNavigate();
  const { fetchMyListings } = useMarketplace();

  const [form, setForm] = useState({
    name: '', description: '', price: '', stock: '1', category: '', condition: '',
  });
  const [image,    setImage]   = useState(null);
  const [preview,  setPreview] = useState(null);
  const [saving,   setSaving]  = useState(false);

  const set = (k, v) => setForm(p => ({ ...p, [k]: v }));

  const handleImage = e => {
    const file = e.target.files?.[0];
    if (!file) return;
    if (file.size > 5 * 1024 * 1024) { toast.error('Ukuran gambar maks 5 MB'); return; }
    setImage(file);
    setPreview(URL.createObjectURL(file));
  };

  const validate = () => {
    if (!form.name.trim())        return 'Nama produk wajib diisi';
    if (!form.price || isNaN(Number(form.price)) || Number(form.price) <= 0)
                                  return 'Harga tidak valid';
    if (!form.stock || isNaN(Number(form.stock)) || Number(form.stock) < 0)
                                  return 'Stok tidak valid';
    if (!form.category)           return 'Pilih kategori';
    if (!form.condition)          return 'Pilih kondisi';
    return null;
  };

  const handleSubmit = async e => {
    e.preventDefault();
    const err = validate();
    if (err) { toast.error(err); return; }

    setSaving(true);
    try {
      const fd = new FormData();
      fd.append('name',        form.name.trim());
      fd.append('description', form.description.trim());
      fd.append('price',       form.price);
      fd.append('stock',       form.stock);
      fd.append('category',    form.category);
      fd.append('condition',   form.condition);
      if (image) fd.append('image', image);

      await marketSvc.createListing(fd);
      toast.success('Produk berhasil ditambahkan!');
      await fetchMyListings?.();
      navigate('/profile/shop', { replace: true });
    } catch (e) {
      toast.error(e?.message ?? 'Gagal menyimpan produk');
    } finally { setSaving(false); }
  };

  return (
    <AppLayout hideNav>
      <Header title="Tambah Produk" showBack />

      <form onSubmit={handleSubmit} className="page-content px-4 pt-4 pb-8 flex flex-col gap-5">

        {/* image picker */}
        <div>
          <label className="label">Foto Produk</label>
          <label className="mt-1 block cursor-pointer">
            <div className={`w-full h-44 rounded-2xl border-2 border-dashed flex items-center justify-center
                            overflow-hidden transition-colors
                            ${preview ? 'border-green-primary' : 'border-divider bg-green-light/30'}`}>
              {preview
                ? <img src={preview} alt="preview" className="w-full h-full object-cover rounded-2xl" />
                : <div className="flex flex-col items-center gap-2 text-text-hint">
                    <span className="text-4xl">📷</span>
                    <span className="text-sm">Ketuk untuk pilih foto</span>
                  </div>}
            </div>
            <input type="file" accept="image/*" className="hidden" onChange={handleImage} />
          </label>
        </div>

        {/* name */}
        <div>
          <label className="label">Nama Produk <span className="text-red-500">*</span></label>
          <input value={form.name} onChange={e => set('name', e.target.value)}
            placeholder="Contoh: Kursi Rotan Bekas" className="input-field mt-1" />
        </div>

        {/* description */}
        <div>
          <label className="label">Deskripsi</label>
          <textarea value={form.description} onChange={e => set('description', e.target.value)}
            rows={3} placeholder="Jelaskan kondisi & detail produk..."
            className="input-field mt-1 resize-none" />
        </div>

        {/* price + stock */}
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="label">Harga (Rp) <span className="text-red-500">*</span></label>
            <input type="number" value={form.price} onChange={e => set('price', e.target.value)}
              placeholder="50000" min="0" className="input-field mt-1" />
          </div>
          <div>
            <label className="label">Stok <span className="text-red-500">*</span></label>
            <div className="mt-1 flex items-center border border-divider rounded-xl overflow-hidden">
              <button type="button"
                onClick={() => set('stock', String(Math.max(0, Number(form.stock) - 1)))}
                className="w-10 h-11 flex items-center justify-center text-green-primary font-bold text-xl bg-green-light/50">−</button>
              <input type="number" value={form.stock} onChange={e => set('stock', e.target.value)}
                className="flex-1 h-11 text-center outline-none text-text-primary font-semibold bg-white" />
              <button type="button"
                onClick={() => set('stock', String(Number(form.stock) + 1))}
                className="w-10 h-11 flex items-center justify-center text-green-primary font-bold text-xl bg-green-light/50">＋</button>
            </div>
          </div>
        </div>

        {/* category */}
        <div>
          <label className="label">Kategori <span className="text-red-500">*</span></label>
          <div className="flex flex-wrap gap-2 mt-2">
            {CATEGORIES.map(c => (
              <button key={c} type="button" onClick={() => set('category', c)}
                className={`px-3 py-1.5 rounded-full text-xs font-semibold border transition-colors
                  ${form.category === c
                    ? 'bg-green-primary text-white border-green-primary'
                    : 'bg-white text-text-secondary border-divider'}`}>
                {c}
              </button>
            ))}
          </div>
        </div>

        {/* condition */}
        <div>
          <label className="label">Kondisi <span className="text-red-500">*</span></label>
          <div className="flex gap-3 mt-2">
            {CONDITIONS.map(c => (
              <button key={c} type="button" onClick={() => set('condition', c)}
                className={`flex-1 py-2.5 rounded-xl text-sm font-semibold border transition-colors
                  ${form.condition === c
                    ? 'bg-green-primary text-white border-green-primary'
                    : 'bg-white text-text-secondary border-divider'}`}>
                {c}
              </button>
            ))}
          </div>
        </div>

        <button type="submit" disabled={saving}
          className="btn-primary py-4 mt-2 disabled:opacity-60 disabled:cursor-not-allowed flex items-center justify-center gap-2">
          {saving
            ? <><span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" /> Menyimpan...</>
            : '✅ Tambah Produk'}
        </button>
      </form>
    </AppLayout>
  );
}
