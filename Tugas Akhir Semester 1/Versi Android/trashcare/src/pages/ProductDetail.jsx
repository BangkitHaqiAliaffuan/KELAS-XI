import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useMarketplace } from '../context/MarketplaceContext';
import AppLayout from '../components/AppLayout';
import Header from '../components/Header';
import { formatRupiah, storageUrl } from '../services/api';
import * as marketSvc from '../services/marketplaceService';
import toast from 'react-hot-toast';

export default function ProductDetail() {
  const { id }    = useParams();
  const navigate  = useNavigate();
  const { user }  = useAuth();
  const { addToCart, toggleWishlist, wishlist } = useMarketplace();

  const [product,  setProduct]  = useState(null);
  const [loading,  setLoading]  = useState(true);
  const [addingCart, setAddingCart] = useState(false);

  const isWishlisted = wishlist?.some(w => w.listing_id === Number(id) || w.id === Number(id));
  const isOwner      = product?.seller_id === user?.id;

  useEffect(() => {
    (async () => {
      setLoading(true);
      try {
        const res = await marketSvc.getListing(id);
        setProduct(res.data?.data ?? res.data);
      } catch {
        toast.error('Gagal memuat produk');
      } finally { setLoading(false); }
    })();
  }, [id]);

  const handleAddToCart = async () => {
    if (!product || product.stock === 0) return;
    setAddingCart(true);
    try {
      await addToCart(product.id, 1);
      toast.success('Ditambahkan ke keranjang!');
    } catch (e) {
      toast.error(e?.message ?? 'Gagal menambahkan');
    } finally { setAddingCart(false); }
  };

  const handleBuyNow = async () => {
    if (!product || product.stock === 0) return;
    setAddingCart(true);
    try {
      await addToCart(product.id, 1);
      navigate('/cart');
    } catch (e) {
      toast.error(e?.message ?? 'Gagal');
    } finally { setAddingCart(false); }
  };

  if (loading) return (
    <AppLayout>
      <Header title="Detail Produk" showBack />
      <div className="flex justify-center pt-20">
        <span className="w-8 h-8 border-2 border-green-primary border-t-transparent rounded-full animate-spin" />
      </div>
    </AppLayout>
  );

  if (!product) return (
    <AppLayout>
      <Header title="Detail Produk" showBack />
      <div className="flex flex-col items-center gap-3 pt-16 text-center px-8">
        <span className="text-5xl">😕</span>
        <p className="font-semibold text-text-primary">Produk tidak ditemukan</p>
        <button onClick={() => navigate(-1)} className="btn-outline mt-2 px-6">Kembali</button>
      </div>
    </AppLayout>
  );

  const outOfStock = product.stock === 0;

  return (
    <AppLayout>
      <Header
        title=""
        showBack
        rightContent={
          <button onClick={() => toggleWishlist(product.id)} className="text-2xl">
            {isWishlisted ? '❤️' : '🤍'}
          </button>
        }
      />

      <div className="page-content">
        {/* image */}
        <div className="bg-green-light/30 h-64 flex items-center justify-center overflow-hidden relative">
          {product.image_url
            ? <img src={storageUrl(product.image_url)} alt={product.name}
                className="w-full h-full object-contain" />
            : <span className="text-8xl">📦</span>}
          {outOfStock && (
            <div className="absolute inset-0 bg-black/40 flex items-center justify-center">
              <span className="bg-red-500 text-white font-bold text-base px-4 py-1 rounded-full">
                Stok Habis
              </span>
            </div>
          )}
        </div>

        <div className="px-4 pt-4 flex flex-col gap-4 pb-32">
          {/* title + price */}
          <div>
            <h1 className="text-text-primary font-extrabold text-xl leading-tight">{product.name}</h1>
            <p className="text-green-primary font-bold text-2xl mt-1">{formatRupiah(product.price)}</p>
          </div>

          {/* chips */}
          <div className="flex flex-wrap gap-2">
            {product.category && (
              <span className="chip">{product.category}</span>
            )}
            {product.condition && (
              <span className="chip-orange">{product.condition}</span>
            )}
            <span className={`text-xs font-bold px-3 py-1 rounded-full
              ${outOfStock ? 'bg-red-100 text-red-600' : 'bg-green-light text-green-primary'}`}>
              {outOfStock ? '❌ Habis' : `📦 Stok: ${product.stock}`}
            </span>
          </div>

          {/* seller */}
          <div className="card p-3 flex items-center gap-3">
            <div className="w-10 h-10 bg-green-light rounded-full flex items-center justify-center flex-shrink-0">
              <span className="text-xl">👤</span>
            </div>
            <div className="flex-1">
              <p className="text-xs text-text-hint">Penjual</p>
              <p className="font-semibold text-text-primary text-sm">{product.seller?.name ?? 'Penjual'}</p>
            </div>
          </div>

          {/* description */}
          {product.description && (
            <div>
              <p className="section-title mb-1">Deskripsi</p>
              <p className="text-text-secondary text-sm leading-relaxed">{product.description}</p>
            </div>
          )}

          {/* owner controls */}
          {isOwner && (
            <div className="flex gap-3">
              <button onClick={() => navigate(`/marketplace/edit/${product.id}`)}
                className="btn-outline flex-1 py-3">✏️ Edit</button>
            </div>
          )}
        </div>

        {/* bottom CTA */}
        {!isOwner && (
          <div className="fixed bottom-20 left-0 right-0 mx-auto max-w-app px-4 z-40">
            <div className="bg-white border-t border-divider px-0 pt-3 pb-3 flex gap-3">
              <button
                onClick={handleAddToCart}
                disabled={outOfStock || addingCart}
                className="btn-outline flex-1 py-3 disabled:opacity-50 disabled:cursor-not-allowed">
                {addingCart ? (
                  <span className="w-4 h-4 border-2 border-green-primary border-t-transparent rounded-full animate-spin inline-block" />
                ) : '🛒 Keranjang'}
              </button>
              <button
                onClick={handleBuyNow}
                disabled={outOfStock || addingCart}
                className="btn-primary flex-1 py-3 disabled:opacity-50 disabled:cursor-not-allowed">
                {outOfStock ? 'Habis' : 'Beli Sekarang'}
              </button>
            </div>
          </div>
        )}
      </div>
    </AppLayout>
  );
}
