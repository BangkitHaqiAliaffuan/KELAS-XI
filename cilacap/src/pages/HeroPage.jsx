import { motion } from 'framer-motion';
import { ChevronDown } from 'lucide-react';
import './HeroPage.css';

const HeroPage = () => {
  return (
    <div className="hero-page">
      <div className="hero-background"></div>
      <div className="hero-particles">
        {Array.from({ length: 20 }).map((_, i) => (
          <div key={i} className="particle" style={{
            left: `${Math.random() * 100}%`,
            animationDelay: `${Math.random() * 3}s`,
            animationDuration: `${3 + Math.random() * 2}s`
          }}></div>
        ))}
      </div>

      <motion.div 
        className="hero-content"
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
      >
        <motion.h1 
          className="hero-title"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2, duration: 0.8 }}
        >
          Keberagaman Kabupaten Cilacap
        </motion.h1>
        
        <motion.p 
          className="hero-subtitle"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4, duration: 0.8 }}
        >
          Gerbang Selatan Jawa: Dari Laut hingga Pegunungan
        </motion.p>

        <motion.button 
          className="hero-button"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.6, duration: 0.8 }}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={() => {
            const makananSection = document.getElementById('makanan');
            if (makananSection) {
              makananSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
          }}
        >
          Mulai Jelajahi
          <ChevronDown className="button-icon" />
        </motion.button>
      </motion.div>

      <div className="hero-decoration">
        <div className="decoration-harbor"></div>
        <div className="decoration-island"></div>
      </div>
    </div>
  );
};

export default HeroPage;
