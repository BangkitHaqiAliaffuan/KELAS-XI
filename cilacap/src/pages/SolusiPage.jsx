import { motion } from 'framer-motion';
import { Shield, Heart, Handshake, Leaf, DollarSign, Scale, Users, Target, TrendingUp, CheckCircle } from 'lucide-react';
import './SolusiPage.css';

const SolusiPage = () => {
  const stats = [
    { label: 'Emisi Turun', value: '-40%', color: 'green' },
    { label: 'Mangrove', value: '+500 ha', color: 'green' },
    { label: 'Pencemaran', value: '-60%', color: 'green' },
    { label: 'ISPA Turun', value: '-30%', color: 'blue' },
    { label: 'Warga Dilayani', value: '5.000+', color: 'blue' },
    { label: 'Kompensasi', value: 'Rp 200M', color: 'orange' },
    { label: 'Pendapatan', value: '+15%', color: 'orange' },
    { label: 'Rekrutmen Lokal', value: '70%', color: 'orange' },
    { label: 'Komplain', value: '-65%', color: 'purple' },
    { label: 'Dialog Sukses', value: '90%', color: 'purple' },
    { label: 'Beasiswa', value: '5.000+ anak', color: 'purple' }
  ];

  const testimoni = [
    { name: 'Pak Suharto', role: 'Nelayan', text: 'Ikan mulai banyak lagi setelah terumbu buatan, dapat bantuan alat tangkap' },
    { name: 'Bu Siti', role: 'Petani Tambak', text: 'Dapat kompensasi dan bibit, udang bisa panen, anak beasiswa kuliah' },
    { name: 'Ir. Budi', role: 'Pertamina', text: 'IPAL standar internasional, dialog rutin, win-win solution' },
    { name: 'Pak Camat', role: 'Pemerintah', text: 'Forum dialog efektif, konflik diselesaikan dengan kepala dingin' }
  ];

  const successFactors = [
    { Icon: Shield, label: 'Regulasi Ketat' },
    { Icon: Heart, label: 'Empati' },
    { Icon: Handshake, label: 'Dialog Terbuka' },
    { Icon: Leaf, label: 'Restorasi Alam' },
    { Icon: DollarSign, label: 'CSR Tepat Sasaran' },
    { Icon: Scale, label: 'Keadilan' },
    { Icon: Users, label: 'Kolaborasi' },
    { Icon: Target, label: 'Target Jelas' },
    { Icon: TrendingUp, label: 'Monitoring' }
  ];

  return (
    <div className="solusi-page page-container">
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
          Solusi Harmoni dalam Keberagaman
        </motion.h2>

        <motion.div 
          className="solusi-main-card"
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
        >
          <div className="main-badge">SOLUSI KOMPREHENSIF</div>
          <h3 className="main-title">Solusi Pencemaran Industri</h3>

          <div className="solusi-sections">
            <div className="solusi-section">
              <h4>A. Regulasi & Pengawasan</h4>
              <ul>
                <li>AMDAL wajib, audit berkala, IPAL modern (Pertamina, Holcim)</li>
                <li>Zona penyangga 1-2 km, greenbelt, monitoring real-time</li>
                <li><strong>Hasil:</strong> Emisi -40%, kualitas air +35%, ISPA -30%</li>
              </ul>
            </div>

            <div className="solusi-section">
              <h4>B. CSR & Pemberdayaan</h4>
              <ul>
                <li>Klinik gratis, kompensasi Rp 50M/tahun untuk nelayan/petani</li>
                <li>Bantuan alat tangkap, subsidi pakan, beasiswa 1.000+/tahun</li>
                <li>Prioritas rekrutmen lokal (70% pekerja industri)</li>
                <li><strong>Hasil:</strong> 5.000+ warga akses kesehatan, pendapatan stabil +15%</li>
              </ul>
            </div>

            <div className="solusi-section">
              <h4>C. Restorasi Lingkungan</h4>
              <ul>
                <li>Mangrove 500 ha, terumbu buatan 10 titik, restocking ikan</li>
                <li>Penghijauan 100.000 pohon/tahun, energi terbarukan (solar panel)</li>
                <li><strong>Hasil:</strong> Populasi ikan +25%, tutupan hijau +15%</li>
              </ul>
            </div>

            <div className="solusi-section">
              <h4>D. Forum Dialog</h4>
              <ul>
                <li>Multi-stakeholder (Pemkab, industri, nelayan, LSM, tokoh)</li>
                <li>Pertemuan 3 bulan, mediasi konflik, transparansi data</li>
                <li><strong>Hasil:</strong> Komplain -65%, 90% konflik selesai melalui dialog</li>
              </ul>
            </div>

            <div className="solusi-section">
              <h4>E. Pembebasan Lahan Adil</h4>
              <ul>
                <li>Harga NJOP + 20-30%, rumah relokasi gratis, legal aid</li>
                <li><strong>Hasil:</strong> Konflik lahan -70%, proses lebih cepat dan adil</li>
              </ul>
            </div>
          </div>

          <div className="stats-dashboard">
            <h4 className="stats-title">📊 Statistik Keberhasilan</h4>
            <div className="stats-grid">
              {stats.map((stat, index) => (
                <motion.div
                  key={index}
                  className={`stat-item ${stat.color}`}
                  initial={{ opacity: 0, scale: 0.5 }}
                  animate={{ opacity: 1, scale: 1 }}
                  transition={{ delay: 0.5 + index * 0.05 }}
                >
                  <div className="stat-value">{stat.value}</div>
                  <div className="stat-label">{stat.label}</div>
                </motion.div>
              ))}
            </div>
          </div>

          <div className="timeline-progress">
            <h4>Timeline Perbaikan</h4>
            <div className="timeline-bar">
              <div className="timeline-step red">2015-2018<br/>Puncak Konflik</div>
              <div className="timeline-step yellow">2019<br/>Mediasi</div>
              <div className="timeline-step lightgreen">2020-2021<br/>Implementasi</div>
              <div className="timeline-step green">2022-2023<br/>Perbaikan</div>
              <div className="timeline-step darkgreen">2024<br/>Berkelanjutan</div>
            </div>
          </div>

          <div className="testimoni-section">
            <h4>💬 Testimoni</h4>
            <div className="testimoni-grid">
              {testimoni.map((item, index) => (
                <motion.div
                  key={index}
                  className="testimoni-card"
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.8 + index * 0.1 }}
                >
                  <p className="testimoni-text">"{item.text}"</p>
                  <div className="testimoni-author">
                    <strong>{item.name}</strong>
                    <span>{item.role}</span>
                  </div>
                </motion.div>
              ))}
            </div>
          </div>

          <div className="before-after">
            <div className="before-after-item before">
              <h5>Sebelum</h5>
              <p>Pencemaran tinggi, 1.200 komplain, konflik sengit</p>
            </div>
            <div className="arrow">→</div>
            <div className="before-after-item after">
              <h5>Sesudah</h5>
              <p>Emisi -40%, 420 komplain, trust membaik, kompensasi Rp 200M</p>
            </div>
          </div>

          <div className="success-factors">
            <h4>🎯 Faktor Kunci Keberhasilan</h4>
            <div className="factors-grid">
              {successFactors.map((item, index) => {
                const Icon = item.Icon;
                return (
                  <motion.div
                    key={index}
                    className="factor-item"
                    initial={{ opacity: 0, scale: 0.5 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ delay: 1 + index * 0.05 }}
                    whileHover={{ scale: 1.1 }}
                  >
                    <Icon size={24} />
                    <span>{item.label}</span>
                  </motion.div>
                );
              })}
            </div>
          </div>
        </motion.div>
      </motion.div>
    </div>
  );
};

export default SolusiPage;
