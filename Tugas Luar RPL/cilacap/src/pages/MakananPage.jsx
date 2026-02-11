import { motion } from 'framer-motion';
import './MakananPage.css';

const MakananPage = () => {
  const makanan = [
    {
      name: 'Lontong Tuyuhan',
      description: 'Lontong kuah santan kental dengan sayuran, gurih creamy',
      badge: 'Favorit Pagi',
      image: '/images/lontong-tuyuhan.jpg'
    },
    {
      name: 'Sate Blengong',
      description: 'Sate bebek dengan bumbu kacang manis pedas khas',
      badge: 'Ikon Kuliner',
      image: '/images/sate-blengong.jpg'
    },
    {
      name: 'Mie Kopyok',
      description: 'Mie kuah kental dengan topping ayam/seafood, porsi besar',
      badge: 'Comfort Food',
      image: '/images/mie-kopyok.jpg'
    }
  ];

  return (
    <div className="makanan-page page-container">
      <div className="wave-pattern"></div>
      
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
          Kuliner Khas Cilacap
        </motion.h2>

        <div className="makanan-grid">
          {makanan.map((item, index) => (
            <motion.div
              key={index}
              className="makanan-card"
              initial={{ opacity: 0, y: 30 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3 + index * 0.1 }}
              whileHover={{ y: -10, scale: 1.02 }}
            >
              <div className="card-image-placeholder">
                <img 
                  src={item.image} 
                  alt={item.name} 
                  className="card-image"
                  onError={(e) => {
                    e.target.style.display = 'none';
                    e.target.parentElement.style.background = 'linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%)';
                  }}
                />
              </div>
              <div className="card-badge">{item.badge}</div>
              <h3 className="card-title">{item.name}</h3>
              <p className="card-description">{item.description}</p>
              <div className="card-border-gradient"></div>
            </motion.div>
          ))}
        </div>
      </motion.div>
    </div>
  );
};

export default MakananPage;
