import { motion } from 'framer-motion';
import { Fish, Anchor, Factory, Flame, Wheat, Waves, Palette, ShoppingBag } from 'lucide-react';
import './PekerjaanPage.css';

const PekerjaanPage = () => {
  const pekerjaan = [
    { name: 'Nelayan Laut Selatan', description: 'Tangkap ikan Samudra Hindia, hadapi ombak besar', Icon: Fish },
    { name: 'Pekerja Pelabuhan', description: 'Pelabuhan Tanjung Intan, ekspor-impor regional', Icon: Anchor },
    { name: 'Buruh Industri Semen', description: 'Holcim Indonesia, zona industri besar', Icon: Factory },
    { name: 'Pekerja Pertamina', description: 'Kilang minyak terbesar Indonesia', Icon: Flame },
    { name: 'Petani Padi & Palawija', description: 'Wilayah utara subur', Icon: Wheat },
    { name: 'Pembudidaya Ikan', description: 'Tambak udang dan ikan pesisir', Icon: Waves },
    { name: 'Pengrajin Batik', description: 'Motif pesisiran khas', Icon: Palette },
    { name: 'Pedagang & UMKM', description: 'Pasar Wage dan kuliner khas', Icon: ShoppingBag }
  ];

  return (
    <div className="pekerjaan-page page-container">
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
          Dinamika Ekonomi Cilacap
        </motion.h2>

        <div className="pekerjaan-grid">
          {pekerjaan.map((item, index) => {
            const Icon = item.Icon;
            return (
              <motion.div
                key={index}
                className="pekerjaan-hexagon"
                initial={{ opacity: 0, scale: 0.5 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ delay: 0.3 + index * 0.1 }}
                whileHover={{ scale: 1.05 }}
              >
                <div className="hexagon-content">
                  <div className="icon-wrapper">
                    <Icon size={40} />
                  </div>
                  <h3 className="hexagon-title">{item.name}</h3>
                  <p className="hexagon-description">{item.description}</p>
                </div>
              </motion.div>
            );
          })}
        </div>

        <motion.div 
          className="info-box"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.8 }}
        >
          <p>
            <strong>Cilacap sebagai Pusat Industri Selatan Jawa:</strong> Kilang Pertamina terbesar Indonesia, Semen Holcim, Pelabuhan Tanjung Intan, dan petrokimia menjadikan Cilacap sebagai kota industri penting dengan keberagaman mata pencaharian yang harmonis.
          </p>
        </motion.div>
      </motion.div>
    </div>
  );
};

export default PekerjaanPage;
