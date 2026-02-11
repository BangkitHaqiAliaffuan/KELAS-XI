import { motion } from 'framer-motion';
import { AlertTriangle, Home, TrendingDown, Users } from 'lucide-react';
import './KonflikPage.css';

const KonflikPage = () => {
  const konflik = [
    {
      name: 'Pencemaran Industri',
      Icon: AlertTriangle,
      badge: 'FOKUS PEMBAHASAN',
      color: 'red',
      masalah: 'Kilang minyak dan pabrik semen → polusi udara, air, kebisingan',
      dampak: 'ISPA +45%, tangkapan ikan -35%, konflik warga vs industri',
      data: '15+ pabrik, 1.200+ komplain, produktivitas nelayan/petani turun 30-40%'
    },
    {
      name: 'Konflik Lahan',
      Icon: Home,
      badge: '',
      color: 'orange',
      masalah: 'Ekspansi industri, pembebasan lahan, harga tidak adil',
      dampak: 'Petani kehilangan mata pencaharian, urbanisasi meningkat',
      data: ''
    },
    {
      name: 'Kesenjangan Ekonomi',
      Icon: TrendingDown,
      badge: '',
      color: 'yellow',
      masalah: 'Gap pekerja industri vs nelayan/petani (gaji 3-5x)',
      dampak: 'Kecemburuan sosial, generasi muda tinggalkan tradisi',
      data: ''
    },
    {
      name: 'Konflik Sosial-Budaya',
      Icon: Users,
      badge: '',
      color: 'blue',
      masalah: 'Pendatang vs lokal, kesenian sepi, bahasa ngapak terkikis',
      dampak: 'Identitas lokal memudar, sanggar tutup',
      data: ''
    }
  ];

  return (
    <div className="konflik-page page-container">
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
          Konflik dalam Keberagaman di Cilacap
        </motion.h2>

        <div className="konflik-grid">
          {konflik.map((item, index) => {
            const Icon = item.Icon;
            return (
              <motion.div
                key={index}
                className={`konflik-card ${item.badge ? 'focus-card' : ''}`}
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.3 + index * 0.1 }}
                whileHover={{ y: -5 }}
              >
                {item.badge && (
                  <div className="focus-badge">{item.badge}</div>
                )}
                <div className={`icon-container ${item.color}`}>
                  <Icon size={36} />
                </div>
                <h3 className="konflik-name">{item.name}</h3>
                
                <div className="konflik-detail">
                  <strong>Masalah:</strong> {item.masalah}
                </div>
                
                <div className="konflik-detail">
                  <strong>Dampak:</strong> {item.dampak}
                </div>
                
                {item.data && (
                  <div className="konflik-data">
                    <strong>Data:</strong> {item.data}
                  </div>
                )}
              </motion.div>
            );
          })}
        </div>
      </motion.div>
    </div>
  );
};

export default KonflikPage;
