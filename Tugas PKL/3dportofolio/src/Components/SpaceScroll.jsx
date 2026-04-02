import React, { useEffect, useRef } from 'react';
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import spaceshipImg from '../assets/spaceship-window.png';
import earthImg from '../assets/earth.png';
import profilePhoto from '../assets/profile-photo.png';

gsap.registerPlugin(ScrollTrigger);

const SpaceScroll = () => {
  const sectionRef = useRef(null);
  const spaceshipRef = useRef(null);
  const earthRef = useRef(null);
  const lensFlareRef = useRef(null);
  const overlayRef = useRef(null);
  const profileRef = useRef(null);
  const profileTextRef = useRef(null);

  useEffect(() => {
    const section = sectionRef.current;
    const ctx = gsap.context(() => {

      /* ─── Master timeline ──────────────────────────────────────────── */
      const tl = gsap.timeline({
        scrollTrigger: {
          trigger: section,
          start: 'top top',
          end: '+=300%',
          scrub: 1.5,
          pin: true,
          anticipatePin: 1,
        },
      });

      /* ── Phase 1  (0% – 60%)  Zoom kamera masuk ke porthole ────────── */
      tl.to(
        spaceshipRef.current,
        {
          scale: 6,
          ease: 'power2.inOut',
          duration: 6,
        },
        0
      );

      /* ── Phase 2  (60% – 80%)  Spaceship fade out ───────────────────── */
      // Earth sudah ada di belakang — spaceship cukup menghilang
      tl.to(
        spaceshipRef.current,
        { opacity: 0, ease: 'power1.in', duration: 2 },
        6
      );

      // Earth sedikit zoom-out agar terasa "muncul" dari dalam porthole
      tl.fromTo(
        earthRef.current,
        { scale: 1.15 },
        { scale: 1, ease: 'power2.out', duration: 2 },
        6
      );

      tl.to(overlayRef.current, { opacity: 0, duration: 2 }, 6);

      /* ── Profile photo + text muncul bersamaan dengan Earth zoom-out ── */
      tl.fromTo(
        profileRef.current,
        { opacity: 0, scale: 0.6 },
        { opacity: 1, scale: 1, ease: 'back.out(1.4)', duration: 2 },
        7
      );

      tl.fromTo(
        profileTextRef.current,
        { opacity: 0, y: 18 },
        { opacity: 1, y: 0, ease: 'power2.out', duration: 1.5 },
        7.5
      );

      /* ── Phase 3  (80% – 100%)  Parallax + lens flare ──────────────── */
      tl.to(
        earthRef.current,
        { y: '-6%', ease: 'none', duration: 2 },
        8
      );

      tl.to(
        lensFlareRef.current,
        {
          opacity: 0.85,
          scale: 1.4,
          ease: 'sine.inOut',
          duration: 1,
          yoyo: true,
          repeat: 1,
        },
        8
      );

    }, section);

    return () => ctx.revert();
  }, []);

  return (
    <>
      <style>{`
        /* ── Reset ── */
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        .space-section {
          position: relative;
          width: 100%;
          height: 100vh;
          overflow: hidden;
          background: #000;
        }

        /* ── Layer Z1: Bumi — selalu di bawah spaceship ── */
        .earth-img {
          position: absolute;
          inset: 0;
          width: 100%;
          height: 100%;
          object-fit: cover;
          object-position: center;
          will-change: transform;
          transform-origin: center center;
          z-index: 1;
        }

        /* ── Layer Z2: Spaceship — pakai mask-image agar porthole transparan ── */
        .spaceship-img {
          --porthole-x: 50%;
          --porthole-y: 48%;
          --porthole-r1: 21vmin;
          --porthole-r2: 22.5vmin;
          --porthole-r3: 25vmin;

          position: absolute;
          inset: 0;
          width: 100%;
          height: 100%;
          object-fit: cover;
          object-position: center;
          will-change: transform, opacity;
          transform-origin: center center;
          z-index: 2;

          /*
           * CSS mask: lubang bulat di posisi porthole (50% dari kiri, 42% dari atas).
           * transparent = area yang dibiarkan tembus (porthole),
           * black       = area yang ditampilkan (dinding spaceship).
           * Nilai 22vmin ≈ radius porthole di layar.
           */
          -webkit-mask-image: radial-gradient(
            circle at var(--porthole-x) var(--porthole-y),
            transparent 0vmin,
            transparent var(--porthole-r1),
            rgba(0,0,0,0.35) var(--porthole-r2),
            black var(--porthole-r3)
          );
          mask-image: radial-gradient(
            circle at var(--porthole-x) var(--porthole-y),
            transparent 0vmin,
            transparent var(--porthole-r1),
            rgba(0,0,0,0.35) var(--porthole-r2),
            black var(--porthole-r3)
          );
        }

        @media (max-width: 640px) {
          .spaceship-img {
            --porthole-y: 49.2%;
            --porthole-r1: 26.6vmin;
            --porthole-r2: 50vmin;
            --porthole-r3: 25.8vmin;
          }

          .earth-img {
            object-position: center 45%;
          }
        }

        @media (max-width: 420px) {
          .spaceship-img {
            --porthole-y: 44.4%;
            --porthole-r1: 18.9vmin;
            --porthole-r2: 19.2vmin;
            --porthole-r3: 22vmin;
          }

          .earth-img {
            object-position: center 43.5%;
          }
        }

        @media (max-width: 360px) {
          .spaceship-img {
            --porthole-y: 44%;
            --porthole-r1: 17.4vmin;
            --porthole-r2: 18.8vmin;
            --porthole-r3: 21.6vmin;
          }

          .earth-img {
            object-position: center 43%;
          }
        }

        /* ── Vignette di atas segalanya ── */
        .vignette-overlay {
          position: absolute;
          inset: 0;
          background: radial-gradient(
            ellipse at center,
            transparent 28%,
            rgba(0, 0, 0, 0.80) 100%
          );
          pointer-events: none;
          z-index: 5;
        }

        /* ── Lens flare (di dalam bumi) ── */

        /* ── Profile photo ── */
        .profile-wrapper {
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 1rem;
          z-index: 4;
          pointer-events: none;
        }

        /*
         * ✏️  ATUR UKURAN FOTO DI SINI:
         * Ubah width & height sesuai keinginan kamu.
         * Contoh: width: 180px → lebih kecil  |  width: 320px → lebih besar
         */
        .profile-photo {
          width: 360px;          /* ← UBAH LEBAR FOTO DI SINI */
          height: 360px;         /* ← UBAH TINGGI FOTO DI SINI */
          object-fit: cover;
          object-position: top;
          border-radius: 50%;
          border: 3px solid rgba(255, 255, 255, 0.6);
          box-shadow: 0 0 32px rgba(120, 200, 255, 0.5), 0 0 8px rgba(255,255,255,0.3);
          will-change: transform, opacity;
        }

        /*
         * ✏️  ATUR TEKS DI SINI:
         * Ganti konten teks di JSX pada elemen <p className="profile-text">
         * Atur font-size, color, dll di bawah ini.
         */
        .profile-text {
          font-family: 'Dune Rise', 'Inter', sans-serif;
          color: rgba(255, 255, 255, 0.92);
          font-size: 10px;       /* ← UBAH UKURAN TEKS DI SINI */
          font-weight: 500;
          letter-spacing: 0.12em;
          text-align: center;
          margin-top: 10px;
          text-shadow: 0 2px 12px rgba(0, 0, 0, 0.7);
          will-change: transform, opacity;
        }

        /* ── Scroll hint ── */
        .scroll-hint {
          position: absolute;
          bottom: 2.5rem;
          left: 50%;
          transform: translateX(-50%);
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 0.5rem;
          z-index: 20;
          color: rgba(255, 255, 255, 0.55);
          font-family: 'Inter', sans-serif;
          font-size: 0.7rem;
          letter-spacing: 0.22em;
          text-transform: uppercase;
          animation: scrollBounce 2s ease-in-out infinite;
        }
        .scroll-hint svg { width: 22px; height: 22px; opacity: 0.55; }
        @keyframes scrollBounce {
          0%, 100% { opacity: 0.55; transform: translateX(-50%) translateY(0px); }
          50%       { opacity: 1;   transform: translateX(-50%) translateY(7px); }
        }
      `}</style>

      <section ref={sectionRef} className="space-section">

        {/* ── Z1: Earth — selalu di belakang, terlihat dari porthole ── */}
        <img
          ref={earthRef}
          src={earthImg}
          alt="Earth from space"
          className="earth-img"
          draggable={false}
        />

        {/* ── Lens flare di atas Earth ── */}
        <div ref={lensFlareRef} className="lens-flare" style={{ zIndex: 3 }} />

        {/* ── Profile photo + teks di tengah bumi ── */}
        <div className="profile-wrapper">
          <img
            ref={profileRef}
            src={profilePhoto}
            alt="Profile"
            className="profile-photo"
            draggable={false}
            style={{ opacity: 0, width: "360px", height: "360px" }}
          />
          {/*
           * ✏️  TEKS DI SINI — ganti isi <p> sesuai keinginan kamu.
           * Contoh: nama, profesi, tagline, dll.
           * Jika tidak ingin ada teks, hapus elemen <p> di bawah ini.
           */}
          <p ref={profileTextRef} className="profile-text" style={{ opacity: 0, fontSize: "30px", 'fontWeight':'bold'}}>
            Bangkit Haqi Aliaffuan
          </p>
        </div>

        {/* ── Z2: Spaceship — dengan mask-image porthole transparan ── */}
        <img
          ref={spaceshipRef}
          src={spaceshipImg}
          alt="Spaceship interior"
          className="spaceship-img"
          draggable={false}
        />


        {/* ── Scroll hint ── */}
        <div className="scroll-hint">
          <span>Scroll</span>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <polyline points="6 9 12 15 18 9" />
          </svg>
        </div>

      </section>
    </>
  );
};

export default SpaceScroll;
