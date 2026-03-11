import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useMarketplace } from '../context/MarketplaceContext';
import AppLayout from '../components/AppLayout';
import Header from '../components/Header';
import { formatRupiah, storageUrl } from '../services/api';

const CATEGORIES = ['Semua', 'Furnitur', 'Elektronik', 'Pakaian', 'Buku', 'Lainnya'];

export default function Marketplace() {
  const navigate  = useNavigate();
  const { isCourier } = useAuth();
  const { listings, fetchListings, cartItems } = useMarketplace();

  const [search,   setSearch]   = useState('');
  const [category, setCategory] = useState('Semua');
  const [loading,  setLoading]  = useState(false);
  const [debouncedSearch, setDebouncedSearch] = useState('');

  // debounce search
  useEffect(() => {
    const t = setTimeout(() => setDebouncedSearch(search), 400);
    return () => clearTimeout(t);
  }, [search]);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      await fetchListings({
        search:   debouncedSearch || undefined,
        category: category !== 'Semua' ? category : undefined,
        per_page: 20,
      });
    } finally { setLoading(false); }
  }, [debouncedSearch, category, fetchListings]);

  useEffect(() => { load(); }, [load]);

  const cartCount = cartItems?.reduce((s, i) => s + (i.quantity ?? 1), 0) ?? 0;

  return (
    <AppLayout>
      <Header
        title="Marketplace"
        showBack={false}
        rightContent={
          <button onClick={() => navigate('/cart')} className="relative p-1">
            <span className="text-2xl">🛒</span>
            {cartCount > 0 && (
              <span className="absolute -top-1 -right-1 bg-orange-accent text-white text-[9px]
                               font-bold w-4 h-4 rounded-full flex items-center justify-center">
                {cartCount}
              </span>
            )}
          </button>
        }
      />

      <div className="page-content">
        {/* search */}
        <div className="px-4 pt-3 pb-2">
          <div className="flex items-center gap-2 bg-white border border-divider rounded-2xl px-3 py-2 shadow-sm">
            <span className="text-text-hint">🔍</span>
            <input
              value={search}
              onChange={e => setSearch(e.target.value)}
              placeholder="Cari produk daur ulang..."
              className="flex-1 outline-none text-sm text-text-primary bg-transparent"
            />
            {search && (
              <button onClick={() => setSearch('')} className="text-text-hint text-lg leading-none">×</button>
            )}
          </div>
        </div>

        {/* categories */}
        <div className="flex gap-2 overflow-x-auto px-4 pb-3 scrollbar-hide">
          {CATEGORIES.map(c => (
            <button
              key={c}
              onClick={() => setCategory(c)}
              className={`flex-shrink-0 px-3 py-1.5 rounded-full text-xs font-semibold border transition-colors
                ${category === c
                  ? 'bg-green-primary text-white border-green-primary'
                  : 'bg-white text-text-secondary border-divider'}`}
            >
              {c}
            </button>
          ))}
        </div>

        {/* listing grid */}
        {loading ? (
          <div className="flex justify-center pt-16">
            <span className="w-8 h-8 border-2 border-green-primary border-t-transparent rounded-full animate-spin" />
          </div>
        ) : listings.length === 0 ? (
          <div className="flex flex-col items-center gap-3 pt-16 px-8 text-center">
            <span className="text-5xl">🛍️</span>
            <p className="font-semibold text-text-primary">Tidak ada produk</p>
            <p className="text-text-secondary text-sm">Coba ubah kata kunci atau filter</p>
          </div>
        ) : (
          <div className="grid grid-cols-2 gap-3 px-4 pb-4">
            {listings.map(l => (
              <Link key={l.id} to={`/marketplace/${l.id}`}
                className="card overflow-hidden active:opacity-80 transition-opacity">
                {/* image */}
                <div className="h-32 bg-green-light/40 flex items-center justify-center overflow-hidden relative">
                  {l.imageUrl
                    ? <img src={storageUrl(l.imageUrl)} alt={l.name}
                        className="w-full h-full object-cover" />
                    : <span className="text-5xl">📦</span>}
                  {l.stock === 0 && (
                    <div className="absolute inset-0 bg-black/40 flex items-center justify-center">
                      <span className="bg-red-500 text-white text-[10px] font-bold px-2 py-0.5 rounded-full">
                        Habis
                      </span>
                    </div>
                  )}
                </div>
                {/* info */}
                <div className="p-2.5">
                  <p className="text-text-primary font-semibold text-xs leading-tight line-clamp-2">{l.name}</p>
                  <p className="text-green-primary font-bold text-sm mt-1">{formatRupiah(l.price)}</p>
                  <div className="flex items-center justify-between mt-1.5">
                    <p className="text-text-hint text-[10px] truncate max-w-[70%]">
                      {l.sellerName ?? 'Penjual'}
                    </p>
                    <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded-full
                      ${l.stock > 0 ? 'bg-green-light text-green-primary' : 'bg-red-100 text-red-600'}`}>
                      {l.stock > 0 ? `Stok ${l.stock}` : 'Habis'}
                    </span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        )}

        {/* FAB: add listing (if not courier) */}
        {!isCourier && (
          <button
            onClick={() => navigate('/marketplace/add')}
            className="fixed bottom-24 right-4 z-50 w-14 h-14 bg-green-primary text-white
                       rounded-full shadow-lg flex items-center justify-center text-2xl
                       active:bg-green-medium transition-colors">
            ＋
          </button>
        )}
      </div>
    </AppLayout>
  );
}
