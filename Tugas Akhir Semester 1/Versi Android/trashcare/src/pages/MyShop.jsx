import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMarketplace } from '../context/MarketplaceContext';
import AppLayout from '../components/AppLayout';
import Header from '../components/Header';
import { formatRupiah, storageUrl } from '../services/api';

export default function MyShop() {
  const navigate = useNavigate();
  const { myListings, fetchMyListings } = useMarketplace();
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    (async () => {
      setLoading(true);
      await fetchMyListings?.();
      setLoading(false);
    })();
  // eslint-disable-next-line
  }, []);

  return (
    <AppLayout hideNav>
      <Header
        title="Toko Saya"
        showBack
        rightContent={
          <button onClick={() => navigate('/marketplace/add')}
            className="bg-white text-green-primary font-bold text-sm px-3 py-1 rounded-xl">
            ＋ Tambah
          </button>
        }
      />

      <div className="page-content px-4 pt-4">
        {loading ? (
          <div className="flex justify-center pt-12">
            <span className="w-8 h-8 border-2 border-green-primary border-t-transparent rounded-full animate-spin" />
          </div>
        ) : !myListings || myListings.length === 0 ? (
          <div className="flex flex-col items-center gap-3 pt-16 text-center">
            <span className="text-5xl">🏪</span>
            <p className="font-bold text-text-primary text-lg">Toko Masih Kosong</p>
            <p className="text-text-secondary text-sm">Mulai jual produk daur ulangmu!</p>
            <button onClick={() => navigate('/marketplace/add')} className="btn-primary mt-2 px-8">
              Tambah Produk
            </button>
          </div>
        ) : (
          <div className="flex flex-col gap-3 pb-6">
            <p className="text-text-secondary text-xs">{myListings.length} produk</p>
            {myListings.map(l => {
              const outOfStock = l.stock === 0;
              return (
                <div key={l.id} className="card p-3 flex gap-3">
                  {/* image */}
                  <div className="w-20 h-20 bg-green-light/30 rounded-xl overflow-hidden flex-shrink-0
                                  flex items-center justify-center relative">
                    {l.image_url
                      ? <img src={storageUrl(l.image_url)} alt={l.name}
                          className="w-full h-full object-cover" />
                      : <span className="text-3xl">📦</span>}
                    {outOfStock && (
                      <div className="absolute inset-0 bg-black/40 rounded-xl flex items-center justify-center">
                        <span className="text-white text-[9px] font-bold">Habis</span>
                      </div>
                    )}
                  </div>

                  {/* info */}
                  <div className="flex-1 min-w-0 flex flex-col justify-between">
                    <div>
                      <p className="font-semibold text-text-primary text-sm truncate">{l.name}</p>
                      <p className="text-green-primary font-bold">{formatRupiah(l.price)}</p>
                    </div>
                    <div className="flex items-center gap-2 mt-1">
                      {/* stock badge */}
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full
                        ${outOfStock ? 'bg-red-100 text-red-600' : 'bg-green-light text-green-primary'}`}>
                        {outOfStock ? '⚠️ Sold Out' : `📦 Stok: ${l.stock}`}
                      </span>
                      {/* active */}
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full
                        ${l.is_active ? 'bg-blue-100 text-blue-700' : 'bg-gray-100 text-gray-500'}`}>
                        {l.is_active ? 'Aktif' : 'Nonaktif'}
                      </span>
                    </div>
                  </div>

                  {/* edit button */}
                  <button onClick={() => navigate(`/marketplace/edit/${l.id}`)}
                    className="self-center text-green-primary bg-green-light px-3 py-2 rounded-xl text-xs font-semibold flex-shrink-0">
                    Edit
                  </button>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </AppLayout>
  );
}
