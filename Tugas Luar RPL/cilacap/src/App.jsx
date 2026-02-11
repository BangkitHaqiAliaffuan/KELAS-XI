import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import Navbar from './components/Navbar';
import HeroPage from './pages/HeroPage';
import MakananPage from './pages/MakananPage';
import PakaianPage from './pages/PakaianPage';
import KesenianPage from './pages/KesenianPage';
import PekerjaanPage from './pages/PekerjaanPage';
import AdatPage from './pages/AdatPage';
import NilaiPage from './pages/NilaiPage';
import KonflikPage from './pages/KonflikPage';
import SolusiPage from './pages/SolusiPage';
import KesimpulanPage from './pages/KesimpulanPage';
import RefleksiPage from './pages/RefleksiPage';
import './App.css';

function App() {
  const [activeSection, setActiveSection] = useState('hero');

  useEffect(() => {
    const observerOptions = {
      root: null,
      rootMargin: '-50% 0px -50% 0px',
      threshold: 0
    };

    const observerCallback = (entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          setActiveSection(entry.target.id);
        }
      });
    };

    const observer = new IntersectionObserver(observerCallback, observerOptions);

    const sections = document.querySelectorAll('section[id]');
    sections.forEach(section => observer.observe(section));

    return () => observer.disconnect();
  }, []);

  return (
    <div className="app">
      <Navbar activeSection={activeSection} />
      
      <main className="landing-container">
        <section id="hero" className="section-wrapper">
          <HeroPage />
        </section>

        <section id="makanan" className="section-wrapper">
          <MakananPage />
        </section>

        <section id="pakaian" className="section-wrapper">
          <PakaianPage />
        </section>

        <section id="kesenian" className="section-wrapper">
          <KesenianPage />
        </section>

        <section id="pekerjaan" className="section-wrapper">
          <PekerjaanPage />
        </section>

        <section id="adat" className="section-wrapper">
          <AdatPage />
        </section>

        <section id="nilai" className="section-wrapper">
          <NilaiPage />
        </section>

        <section id="konflik" className="section-wrapper">
          <KonflikPage />
        </section>

        <section id="solusi" className="section-wrapper">
          <SolusiPage />
        </section>

        <section id="kesimpulan" className="section-wrapper">
          <KesimpulanPage />
        </section>

        <section id="refleksi" className="section-wrapper">
          <RefleksiPage />
        </section>
      </main>
    </div>
  );
}

export default App;
