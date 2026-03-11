import React, { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useMarketplace } from '../context/MarketplaceContext';
import AppLayout from '../components/AppLayout';
import Header from '../components/Header';
import { formatRupiah, storageUrl } from '../services/api';
import toast from 'react-hot-toast';

export default function Wishlist() {
  const navigate = useNavigate();
  const { wishlist, fetchWishlist, toggleWishlist } = useMarketplace();
  const [loading, setLoading]   = useState(false);
  const [removing, setRemoving] = useState({});

  useEffect(() => {
    (async () => {
      setLoading(true);
      await fetchWishlist?.();
      setLoading(false);
    })();
  // eslint-disable-next-line
  }, []);

  const handleRemove = async item => {
    const id = item.listing_id ?? item.id;
    setRemoving(p => ({ ...p, [id]: true }));
    try {
      await toggleWishlist?.(id);
      toast.success('Dihapus dari wishlist');
    } catch { toast.error('Gagal menghapus'); }
    finally { setRemoving(p => ({ ...p, [id]: false })); }
  };

  return (
    <AppLayout hideNav>
      <Header title="Wishlist" showBack />

      <div className="page-content px-4 pt-4">
        {loading ? (
          <div className="flex justify-center pt-12">
            <span className="w-8 h-8 border-2 border-green-primary border-t-transparent rounded-full animate-spin" />
          </div>
        ) : !wishlist || wishlist.length === 0 ? (
          <div className="flex flex-col items-center gap-3 pt-16 text-center">
            <span className="text-5xl">❤️</span>
            <p className="font-bold text-text-primary text-lg">Wishlist Kosong</p>
            <p className="text-text-secondary text-sm">Simpan produk yang kamu suka!</p>
            <button onClick={() => navigate('/marketplace')} className="btn-primary mt-2 px-8">
              Jelajahi Marketplace
            </button>
          </div>
        ) : (
          <div className="flex flex-col gap-3 pb-6">
            <p className="text-text-secondary text-xs">{wishlist.length} produk</p>
            {wishlist.map(item => {
              const listing = item.listing ?? item;
              const id      = item.listing_id ?? item.id;
              return (
                <div key={id} className="card p-3 flex gap-3">
                  <Link to={`/marketplace/${listing.id ?? id}`}
                    className="w-20 h-20 bg-green-light/30 rounded-xl overflow-hidden flex-shrink-0
                                flex items-center justify-center">
                    {listing.image_url
                      ? <img src={storageUrl(listing.image_url)} alt={listing.name}
                          className="w-full h-full object-cover" />
                      : <span className="text-3xl">📦</span>}
                  </Link>

                  <div className="flex-1 min-w-0 flex flex-col justify-between">
                    <div>
                      <p className="font-semibold text-text-primary text-sm truncate">
                        {listing.name ?? 'Produk'}
                      </p>
                      <p className="text-green-primary font-bold">{formatRupiah(listing.price ?? 0)}</p>
                      <p className="text-text-hint text-xs">{listing.seller?.name ?? ''}</p>
                    </div>
                    <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full w-fit
                      ${listing.stock === 0 ? 'bg-red-100 text-red-600' : 'bg-green-light text-green-primary'}`}>
                      {listing.stock === 0 ? 'Habis' : `Stok ${listing.stock}`}
                    </span>
                  </div>

                  <button onClick={() => handleRemove(item)}
                    disabled={removing[id]}
                    className="self-start text-red-400 text-xl pl-2 disabled:opacity-50">
                    {removing[id] ? '...' : '🗑️'}
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
