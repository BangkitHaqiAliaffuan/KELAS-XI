import { motion } from 'framer-motion';
import { Shield, Users, MessageCircle, Scale } from 'lucide-react';
import './NilaiPage.css';

const NilaiPage = () => {
  const nilai = [
    {
      Icon: Shield,
      name: 'Keberanian & Ketangguhan',
      description: 'Nelayan hadapi ombak Laut Selatan, industri keras'
    },
    {
      Icon: Users,
      name: 'Gotong Royong',
      description: 'Sedekah laut, merti desa, nyadran bersama'
    },
    {
      Icon: MessageCircle,
      name: 'Kejujuran & Keterbukaan',
      description: 'Bahasa ngapak yang lugas, komunikasi terbuka'
    },
    {
      Icon: Scale,
      name: 'Harmoni Ekonomi-Lingkungan',
      description: 'Solusi konflik industri menciptakan keseimbangan win-win'
    }
  ];

  return (
    <div className="nilai-page page-container">
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
          Nilai yang Diwariskan
        </motion.h2>

        <div className="nilai-grid">
          {nilai.map((item, index) => {
            const Icon = item.Icon;
            return (
              <motion.div
                key={index}
                className="nilai-card"
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ delay: 0.3 + index * 0.15 }}
                whileHover={{ scale: 1.05, rotate: 2 }}
              >
                <div className="nilai-icon-wrapper">
                  <Icon size={48} />
                </div>
                <h3 className="nilai-name">{item.name}</h3>
                <p className="nilai-description">{item.description}</p>
              </motion.div>
            );
          })}
        </div>
      </motion.div>
    </div>
  );
};

export default NilaiPage;
