import { motion } from 'framer-motion';
import { useState, useEffect } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import './KesenianPage.css';

const KesenianPage = () => {
  const [currentIndex, setCurrentIndex] = useState(0);

  const kesenian = [
    {
      name: 'Ebeg/Kuda Lumping',
      description: 'Tarian kuda kepang energik gaya Banyumasan dengan trance',
      image: '/images/ebeg-kuda-lumping.jpg'
    },
    {
      name: 'Calung Banyumasan',
      description: 'Musik bambu dengan repertoar lagu khas pesisir',
      image: '/images/calung-banyumasan.jpg'
    },
    {
      name: 'Sintren',
      description: 'Kesenian mistis penari trance muncul dari kurungan bambu',
      image: '/images/sintren.jpg'
    },
    {
      name: 'Batik Cilacap',
      description: 'Motif "Gringsing Nusakambangan", "Ombak Samudra Hindia"',
      image: '/images/batik-cilacap.jpg'
    },
    {
      name: 'Kentongan Bumbung',
      description: 'Alat musik bambu untuk komunikasi dan seni, ritme cepat dinamis',
      image: '/images/kentongan-bumbung.jpg'
    }
  ];

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % kesenian.length);
    }, 4000);

    return () => clearInterval(interval);
  }, []);

  const handlePrev = () => {
    setCurrentIndex((prev) => (prev - 1 + kesenian.length) % kesenian.length);
  };

  const handleNext = () => {
    setCurrentIndex((prev) => (prev + 1) % kesenian.length);
  };

  return (
    <div className="kesenian-page page-container">
      <motion.div 
        className="page-content"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.6 }}
      >
        <motion.h2 
          className="page-title"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
        >
          Warisan Seni Budaya Cilacap
        </motion.h2>

        <div className="carousel-container">
          <button className="carousel-btn prev" onClick={handlePrev}>
            <ChevronLeft size={32} />
          </button>

          <motion.div 
            key={currentIndex}
            className="carousel-item"
            initial={{ opacity: 0, x: 100 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -100 }}
            transition={{ duration: 0.5 }}
          >
            <div className="carousel-image-placeholder">
              <img 
                src={kesenian[currentIndex].image} 
                alt={kesenian[currentIndex].name} 
                className="carousel-image"
                onError={(e) => {
                  e.target.style.display = 'none';
                  e.target.parentElement.style.background = 'linear-gradient(135deg, #fef3c7 0%, #fde68a 100%)';
                }}
              />
            </div>
            <div className="item-number">{currentIndex + 1} / {kesenian.length}</div>
            <h3 className="item-title">{kesenian[currentIndex].name}</h3>
            <p className="item-description">{kesenian[currentIndex].description}</p>
          </motion.div>

          <button className="carousel-btn next" onClick={handleNext}>
            <ChevronRight size={32} />
          </button>
        </div>

        <div className="carousel-dots">
          {kesenian.map((_, index) => (
            <button
              key={index}
              className={`dot ${index === currentIndex ? 'active' : ''}`}
              onClick={() => setCurrentIndex(index)}
            />
          ))}
        </div>
      </motion.div>
    </div>
  );
};

export default KesenianPage;
