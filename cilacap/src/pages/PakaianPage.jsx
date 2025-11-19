import { motion } from 'framer-motion';
import './PakaianPage.css';

const PakaianPage = () => {
  return (
    <div className="pakaian-page page-container">
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
          Busana Tradisional Cilacap
        </motion.h2>

        <div className="pakaian-split">
          <motion.div 
            className="pakaian-section section-left"
            initial={{ opacity: 0, x: -50 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.4 }}
          >
            <div className="section-content">
              <h3 className="section-title">Busana Wanita</h3>
              <div className="costume-item">
                <h4>Kebaya Jawa Cilacap</h4>
                <p>Kebaya elegan dengan batik motif pesisiran (ombak, perahu, ikan)</p>
              </div>
              <div className="costume-item">
                <h4>Warna Khas</h4>
                <div className="color-palette">
                  <span className="color-box" style={{background: '#0369a1'}} title="Biru Laut"></span>
                  <span className="color-box" style={{background: '#92400e'}} title="Coklat"></span>
                  <span className="color-box" style={{background: '#15803d'}} title="Hijau"></span>
                </div>
              </div>
              <div className="costume-item">
                <h4>Aksesori</h4>
                <p>Sanggul bunga melati, selendang batik</p>
              </div>
              <div className="filosofi">
                <strong>Filosofi:</strong> Kesederhanaan anggun masyarakat pesisir
              </div>
            </div>
          </motion.div>

          <div className="divider"></div>

          <motion.div 
            className="pakaian-section section-right"
            initial={{ opacity: 0, x: 50 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.4 }}
          >
            <div className="section-content">
              <h3 className="section-title">Busana Pria</h3>
              <div className="costume-item">
                <h4>Beskap Banyumasan</h4>
                <p>Beskap dengan jarik batik parang/lereng</p>
              </div>
              <div className="costume-item">
                <h4>Aksesori</h4>
                <p>Blangkon khas Banyumas, keris</p>
              </div>
              <div className="costume-item">
                <h4>Gaya</h4>
                <p>Lebih santai dari Jogja/Solo, namun tetap elegan</p>
              </div>
              <div className="filosofi">
                <strong>Filosofi:</strong> Ketegasan namun rendah hati
              </div>
            </div>
          </motion.div>
        </div>

        <motion.div 
          className="info-center"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.6 }}
        >
          <p>
            <strong>Pengaruh:</strong> Budaya Banyumas dan pesisir selatan dengan motif maritim khas yang mencerminkan kehidupan masyarakat Cilacap
          </p>
        </motion.div>
      </motion.div>
    </div>
  );
};

export default PakaianPage;
