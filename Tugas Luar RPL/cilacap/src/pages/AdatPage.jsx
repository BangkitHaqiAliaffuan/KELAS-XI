import { motion } from 'framer-motion';
import './AdatPage.css';

const AdatPage = () => {
  const tradisi = [
    {
      name: 'Sedekah Laut',
      description: 'Upacara nelayan tolak bala, sesaji dilarung',
      nilai: 'Syukur, hormati laut'
    },
    {
      name: 'Nyadran',
      description: 'Ziarah makam leluhur sebelum Ramadan',
      nilai: 'Hormati leluhur, gotong royong'
    },
    {
      name: 'Merti Desa',
      description: 'Bersih desa dengan wayangan dan kenduri',
      nilai: 'Syukur panen, kebersamaan'
    },
    {
      name: 'Sintren Ritual',
      description: 'Pertunjukan sakral dengan ritual khusus',
      nilai: 'Kesadaran spiritual'
    },
    {
      name: 'Bahasa Ngapak',
      description: 'Dialek Jawa Banyumas/Cilacap ("opo", "kowe")',
      nilai: 'Identitas lokal, komunikasi jujur'
    }
  ];

  return (
    <div className="adat-page page-container">
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
          Tradisi yang Melekat
        </motion.h2>

        <div className="timeline">
          {tradisi.map((item, index) => (
            <motion.div
              key={index}
              className={`timeline-item ${index % 2 === 0 ? 'left' : 'right'}`}
              initial={{ opacity: 0, x: index % 2 === 0 ? -50 : 50 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.3 + index * 0.15 }}
            >
              <div className="timeline-content">
                <h3 className="tradisi-name">{item.name}</h3>
                <p className="tradisi-description">{item.description}</p>
                <div className="tradisi-nilai">
                  <strong>Nilai:</strong> {item.nilai}
                </div>
              </div>
              <div className="timeline-dot"></div>
            </motion.div>
          ))}
        </div>
      </motion.div>
    </div>
  );
};

export default AdatPage;
