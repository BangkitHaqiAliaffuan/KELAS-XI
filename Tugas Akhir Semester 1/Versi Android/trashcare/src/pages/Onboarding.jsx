import React from 'react';
import { useNavigate } from 'react-router-dom';

const SLIDES = [
  {
    emoji: '♻️',
    bg: 'from-green-primary to-green-medium',
    title: 'TrashCare',
    subtitle: 'Platform pengelolaan sampah pintar untuk lingkungan yang lebih bersih',
  },
  {
    emoji: '🚚',
    bg: 'from-green-medium to-green-dark',
    title: 'Pickup Sampah',
    subtitle: 'Jadwalkan pengambilan sampah daur ulang langsung dari rumahmu',
  },
  {
    emoji: '🛍️',
    bg: 'from-orange-accent to-orange-dark',
    title: 'Marketplace',
    subtitle: 'Jual & beli barang daur ulang berkualitas dengan mudah',
  },
];

export default function Onboarding() {
  const navigate = useNavigate();
  const [idx, setIdx] = React.useState(0);
  const slide = SLIDES[idx];
  const isLast = idx === SLIDES.length - 1;

  return (
    <div className="app-shell overflow-hidden">
      <div className={`flex-1 flex flex-col bg-gradient-to-b ${slide.bg} transition-all duration-500`}
           style={{ minHeight: '100dvh' }}>

        {/* Skip */}
        <div className="flex justify-end p-5">
          <button onClick={() => navigate('/login')}
            className="text-white/70 text-sm font-medium">
            Lewati
          </button>
        </div>

        {/* Illustration */}
        <div className="flex-1 flex flex-col items-center justify-center px-8 gap-6">
          <div className="text-[90px] leading-none drop-shadow-lg animate-bounce">
            {slide.emoji}
          </div>
          <div className="text-center">
            <h1 className="text-white text-3xl font-extrabold mb-3">{slide.title}</h1>
            <p className="text-white/80 text-base leading-relaxed">{slide.subtitle}</p>
          </div>
        </div>

        {/* Dots */}
        <div className="flex justify-center gap-2 pb-6">
          {SLIDES.map((_, i) => (
            <button key={i} onClick={() => setIdx(i)}
              className={`rounded-full transition-all duration-300 ${
                i === idx ? 'w-6 h-2 bg-white' : 'w-2 h-2 bg-white/40'
              }`} />
          ))}
        </div>

        {/* Buttons */}
        <div className="px-6 pb-10 flex flex-col gap-3">
          {isLast ? (
            <>
              <button onClick={() => navigate('/register')}
                className="w-full h-13 py-3.5 bg-white text-green-primary rounded-2xl font-bold text-base
                           active:opacity-80 transition-opacity shadow-lg">
                Daftar Sekarang 🚀
              </button>
              <button onClick={() => navigate('/login')}
                className="w-full py-3 text-white/90 font-semibold text-sm">
                Sudah punya akun? <span className="underline">Masuk</span>
              </button>
            </>
          ) : (
            <button onClick={() => setIdx(i => i + 1)}
              className="w-full py-3.5 bg-white text-green-primary rounded-2xl font-bold text-base
                         active:opacity-80 transition-opacity shadow-lg">
              Selanjutnya →
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
