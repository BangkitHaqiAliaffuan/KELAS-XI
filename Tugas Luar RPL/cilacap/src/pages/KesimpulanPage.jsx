import { motion } from 'framer-motion';
import './KesimpulanPage.css';

const KesimpulanPage = () => {
  return (
    <div className="kesimpulan-page page-container">
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
          Kesimpulan
        </motion.h2>

        <motion.div 
          className="kesimpulan-card"
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
        >
          <p className="kesimpulan-text">
            Kabupaten Cilacap menunjukkan harmoni unik antara industri modern dan kehidupan tradisional. Sebagai pusat industri selatan Jawa dengan kilang minyak Pertamina dan pelabuhan Tanjung Intan, Cilacap membuktikan bahwa pembangunan ekonomi dapat berjalan seimbang dengan pelestarian lingkungan dan budaya. Melalui dialog, CSR yang tepat sasaran, dan regulasi ketat, konflik pencemaran industri berhasil dimitigasi.
          </p>
          <p className="kesimpulan-text">
            Kuliner khas seperti Sate Blengong dan Lontong Tuyuhan, kesenian Ebeg dan Sintren, serta tradisi Sedekah Laut tetap lestari. Bahasa Ngapak yang lugas mencerminkan kejujuran masyarakat Cilacap dalam menghadapi tantangan. Keberagaman pekerjaan dari nelayan hingga pekerja kilang minyak menciptakan dinamika sosial yang kaya, di mana harmoni tercipta melalui tanggung jawab bersama dan saling menghormati.
          </p>
        </motion.div>
      </motion.div>
    </div>
  );
};

export default KesimpulanPage;
