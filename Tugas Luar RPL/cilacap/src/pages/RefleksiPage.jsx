import { motion } from 'framer-motion';
import { CheckCircle, XCircle, AlertTriangle, Users, TrendingDown, TrendingUp, Factory, Droplets, Fish } from 'lucide-react';
import './RefleksiPage.css';

const RefleksiPage = () => {
  return (
    <div className="refleksi-page page-container">
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
          Refleksi: Harmoni dalam Keberagaman
        </motion.h2>

        <div className="refleksi-cards">
          {/* Card 1 */}
          <motion.div
            className="refleksi-card"
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
          >
            <div className="card-number">1</div>
            <p className="card-statement">
              Harmoni dalam keberagaman sangat penting untuk menciptakan lingkungan kerja yang inklusif dan produktif.
            </p>
            <div className="card-answer correct">
              <div className="answer-header">
                <CheckCircle size={32} />
                <span className="answer-label">✓ BENAR</span>
              </div>
              <p className="answer-explanation">
                Di Cilacap, harmoni antara pekerja industri (Pertamina, Holcim), nelayan, petani, dan pedagang menciptakan ekosistem ekonomi produktif. Forum dialog multi-stakeholder membuktikan lingkungan kerja inklusif meningkatkan produktivitas - konflik diselesaikan 90% melalui dialog, komplain turun 65%.
              </p>
            </div>
          </motion.div>

          {/* Card 2 */}
          <motion.div
            className="refleksi-card"
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4 }}
          >
            <div className="card-number">2</div>
            <p className="card-statement">
              Keberagaman pekerjaan tidak berkontribusi pada penciptaan harmoni di lingkungan kerja.
            </p>
            <div className="card-answer wrong">
              <div className="answer-header">
                <XCircle size={32} />
                <span className="answer-label">✗ SALAH</span>
              </div>
              <p className="answer-explanation">
                Pernyataan keliru. Keberagaman pekerjaan di Cilacap justru menciptakan interdependensi harmonis. Industri butuh nelayan untuk supply makanan pekerja, nelayan dapat CSR dan kompensasi dari industri, petani dapat pasar dari pekerja industri. Kolaborasi ini terbukti meningkatkan pendapatan semua pihak 15%.
              </p>
            </div>
          </motion.div>

          {/* Card 3 - FOKUS */}
          <motion.div
            className="refleksi-card focus-card"
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5 }}
          >
            <div className="card-number focus">3</div>
            <p className="card-statement">
              Menerapkan nilai harmoni dalam keberagaman dapat membantu mengurangi konflik dan meningkatkan produktivitas di lingkungan masyarakat dan lingkungan kerja.
            </p>
            <div className="card-answer correct focus">
              <div className="answer-header">
                <CheckCircle size={40} />
                <span className="answer-label large">✓ BENAR</span>
              </div>
              <div className="focus-badge">Studi Kasus: Konflik Pencemaran Industri Cilacap</div>

              <div className="detail-sections">
                <div className="detail-section">
                  <div className="section-icon red">
                    <AlertTriangle size={24} />
                  </div>
                  <h4>1. Konflik yang Terjadi</h4>
                  <p>
                    Pencemaran industri (kilang minyak Pertamina, pabrik semen Holcim) menyebabkan konflik tajam: polusi udara dan air, ISPA naik 45%, tangkapan nelayan turun 35%, komplain 1.200+ kasus/tahun. Ketegangan warga vs industri sangat tinggi, produktivitas nelayan dan petani tambak turun 30-40%.
                  </p>
                </div>

                <div className="detail-section">
                  <div className="section-icon blue">
                    <Users size={24} />
                  </div>
                  <h4>2. Penerapan Nilai Harmoni</h4>
                  <p>
                    Dibentuk Forum Dialog Multi-Stakeholder (Pemkab, industri, nelayan, petani, LSM, tokoh masyarakat). Prinsip gotong royong dan komunikasi terbuka diterapkan - semua pihak berkontribusi solusi, bukan saling menyalahkan. Dialog rutin setiap 3 bulan dengan transparansi data.
                  </p>
                </div>

                <div className="detail-section">
                  <div className="section-icon green">
                    <CheckCircle size={24} />
                  </div>
                  <h4>3. Langkah Konkret</h4>
                  <ul className="check-list">
                    <li><CheckCircle size={16} /> Industri pasang IPAL modern dan kurangi emisi 40%</li>
                    <li><CheckCircle size={16} /> CSR: kompensasi Rp 50M/tahun, klinik gratis, beasiswa 1.000+ anak</li>
                    <li><CheckCircle size={16} /> Masyarakat ikut monitoring lingkungan (relawan)</li>
                    <li><CheckCircle size={16} /> Nelayan/petani dapat bantuan alat tangkap dan subsidi pakan</li>
                    <li><CheckCircle size={16} /> Pemerintah tegakkan regulasi AMDAL ketat dengan sanksi</li>
                    <li><CheckCircle size={16} /> Restorasi: mangrove 500 ha, terumbu buatan, penghijauan 100.000 pohon</li>
                  </ul>
                </div>

                <div className="detail-section">
                  <div className="section-icon purple">
                    <TrendingDown size={24} />
                  </div>
                  <h4>4. Hasil - Konflik Berkurang</h4>
                  <div className="stats-mini">
                    <div className="stat-mini">Komplain turun 65% (1.200 → 420 kasus/tahun)</div>
                    <div className="stat-mini">90% konflik diselesaikan melalui dialog (mediasi sukses)</div>
                    <div className="stat-mini">Trust antara warga dan industri membaik signifikan</div>
                    <div className="stat-mini">Tidak ada lagi protes massa atau bentrok</div>
                  </div>
                </div>

                <div className="detail-section">
                  <div className="section-icon orange">
                    <TrendingUp size={24} />
                  </div>
                  <h4>5. Hasil - Produktivitas Meningkat</h4>
                  <div className="stats-mini">
                    <div className="stat-mini">Emisi industri turun 40%, kualitas air naik 35%</div>
                    <div className="stat-mini">ISPA turun 30%, kesehatan warga membaik</div>
                    <div className="stat-mini">Populasi ikan naik 25% (restorasi ekosistem laut)</div>
                    <div className="stat-mini">Pendapatan nelayan/petani stabil, bahkan naik 15%</div>
                    <div className="stat-mini">70% pekerja industri adalah warga lokal (penyerapan tenaga kerja)</div>
                    <div className="stat-mini">Ekonomi daerah tumbuh tanpa korbankan lingkungan</div>
                  </div>
                </div>

                <div className="detail-section conclusion">
                  <h4>6. Kesimpulan Kasus</h4>
                  <div className="quote-box">
                    <p>
                      Nilai harmoni dalam keberagaman terbukti mengurangi konflik dan meningkatkan produktivitas. Ketika industri, nelayan, petani, dan pemerintah bekerja sama dengan prinsip saling menghormati dan dialog terbuka, semua pihak diuntungkan - industri tetap produksi, lingkungan membaik, masyarakat sejahtera. Cilacap membuktikan pembangunan dan lingkungan bisa harmonis.
                    </p>
                  </div>
                </div>

                <div className="visual-flow">
                  <div className="flow-item">
                    <Factory size={28} />
                    <span>Industri</span>
                  </div>
                  <span className="arrow">→</span>
                  <div className="flow-item">
                    <Droplets size={28} />
                    <span>Polusi</span>
                  </div>
                  <span className="arrow">→</span>
                  <div className="flow-item">
                    <Fish size={28} />
                    <span>Nelayan</span>
                  </div>
                  <span className="arrow">→</span>
                  <div className="flow-item">
                    <Users size={28} />
                    <span>Dialog</span>
                  </div>
                  <span className="arrow">→</span>
                  <div className="flow-item">
                    <TrendingUp size={28} />
                    <span>Harmoni</span>
                  </div>
                </div>
              </div>
            </div>
          </motion.div>

          {/* Card 4 */}
          <motion.div
            className="refleksi-card"
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.6 }}
          >
            <div className="card-number">4</div>
            <p className="card-statement">
              Menciptakan harmoni dalam keberagaman merupakan tanggung jawab pimpinan di tempat kerja, bukan tanggung jawab karyawan.
            </p>
            <div className="card-answer wrong">
              <div className="answer-header">
                <XCircle size={32} />
                <span className="answer-label">✗ SALAH</span>
              </div>
              <p className="answer-explanation">
                Tradisi gotong royong di Cilacap (sedekah laut, merti desa) menunjukkan harmoni adalah tanggung jawab bersama. Dalam penyelesaian konflik industri, bukan hanya CEO Pertamina/Holcim, tapi juga nelayan biasa, petani, dan warga aktif berpartisipasi di forum dialog. Relawan monitoring lingkungan adalah warga biasa. Harmoni tercipta ketika semua berperan aktif.
              </p>
            </div>
          </motion.div>

          {/* Card 5 */}
          <motion.div
            className="refleksi-card"
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.7 }}
          >
            <div className="card-number">5</div>
            <p className="card-statement">
              Toleransi dan harmoni dalam keberagaman adalah konsep yang sama dan dapat digunakan secara bergantian.
            </p>
            <div className="card-answer wrong">
              <div className="answer-header">
                <XCircle size={32} />
                <span className="answer-label">✗ SALAH</span>
              </div>
              <p className="answer-explanation">
                Toleransi berbeda dengan harmoni. Toleransi hanya 'menerima' perbedaan secara pasif, harmoni adalah 'kolaborasi aktif' untuk tujuan bersama. Di Cilacap, masyarakat tidak hanya mentolerir keberadaan industri yang berbeda budaya kerja dengan nelayan, tetapi aktif berkolaborasi - dialog, CSR, kompensasi, restorasi bersama. Harmoni lebih tinggi dari toleransi karena ada aksi nyata dan saling mendukung.
              </p>
            </div>
          </motion.div>
        </div>
      </motion.div>
    </div>
  );
};

export default RefleksiPage;
