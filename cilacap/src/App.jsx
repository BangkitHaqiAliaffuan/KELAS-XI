import { useState, useEffect } from 'react';
import { AnimatePresence, motion } from 'framer-motion';
import Navbar from './components/Navbar';
import NavigationControls from './components/NavigationControls';
import HeroPage from './pages/HeroPage';
import MakananPage from './pages/MakananPage';
import PakaianPage from './pages/PakaianPage';
import KesenianPage from './pages/KesenianPage';
import PekerjaanPage from './pages/PekerjaanPage';
import AdatPage from './pages/AdatPage';
import KonflikPage from './pages/KonflikPage';
import SolusiPage from './pages/SolusiPage';
import NilaiPage from './pages/NilaiPage';
import KesimpulanPage from './pages/KesimpulanPage';
import RefleksiPage from './pages/RefleksiPage';
import './App.css';

function App() {
  const [currentPage, setCurrentPage] = useState(0);
  const [direction, setDirection] = useState(0);

  const pages = [
    <HeroPage key="hero" />,
    <MakananPage key="makanan" />,
    <PakaianPage key="pakaian" />,
    <KesenianPage key="kesenian" />,
    <PekerjaanPage key="pekerjaan" />,
    <AdatPage key="adat" />,
    <KonflikPage key="konflik" />,
    <SolusiPage key="solusi" />,
    <NilaiPage key="nilai" />,
    <KesimpulanPage key="kesimpulan" />,
    <RefleksiPage key="refleksi" />
  ];

  const totalPages = pages.length;

  const handleNavigate = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      setDirection(newPage > currentPage ? 1 : -1);
      setCurrentPage(newPage);
    }
  };

  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'ArrowRight' || e.key === 'ArrowDown') {
        handleNavigate(currentPage + 1);
      } else if (e.key === 'ArrowLeft' || e.key === 'ArrowUp') {
        handleNavigate(currentPage - 1);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [currentPage]);

  const variants = {
    enter: (direction) => ({
      x: direction > 0 ? 1000 : -1000,
      opacity: 0
    }),
    center: {
      x: 0,
      opacity: 1
    },
    exit: (direction) => ({
      x: direction < 0 ? 1000 : -1000,
      opacity: 0
    })
  };

  return (
    <div className="app">
      <Navbar 
        currentPage={currentPage} 
        totalPages={totalPages}
        onPageChange={handleNavigate}
      />
      
      <NavigationControls
        currentPage={currentPage}
        totalPages={totalPages}
        onNavigate={handleNavigate}
      />

      <AnimatePresence initial={false} custom={direction} mode="wait">
        <motion.div
          key={currentPage}
          custom={direction}
          variants={variants}
          initial="enter"
          animate="center"
          exit="exit"
          transition={{
            x: { type: "spring", stiffness: 300, damping: 30 },
            opacity: { duration: 0.2 }
          }}
          className="page-wrapper"
        >
          {pages[currentPage]}
        </motion.div>
      </AnimatePresence>
    </div>
  );
}

export default App;
