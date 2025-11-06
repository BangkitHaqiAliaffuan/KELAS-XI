"use client"

import { motion } from "framer-motion"
import { useInView } from "framer-motion"
import { useRef } from "react"
import { ShoppingBag, Scissors, TreePine, Fish, Factory, Store } from "lucide-react"

const pekerjaan = [
  {
    name: "Pedagang Pempek",
    deskripsi:
      "Menjual pempek dan makanan khas Palembang di pasar atau warung. Pedagang pempek adalah tulang punggung ekonomi kuliner Palembang.",
    icon: ShoppingBag,
  },
  {
    name: "Pengrajin Songket",
    deskripsi:
      "Membuat kain songket dengan tangan menggunakan benang emas. Pengrajin songket menjaga warisan budaya melalui karya mereka yang indah.",
    icon: Scissors,
  },
  {
    name: "Petani Karet & Sawit",
    deskripsi:
      "Mengelola perkebunan karet dan kelapa sawit di sekitar Palembang. Hasil pertanian ini menjadi komoditas ekspor penting Indonesia.",
    icon: TreePine,
  },
  {
    name: "Nelayan Sungai Musi",
    deskripsi:
      "Menangkap ikan di Sungai Musi untuk memenuhi kebutuhan pangan. Nelayan menyediakan bahan baku untuk makanan khas Palembang.",
    icon: Fish,
  },
  {
    name: "Pegawai Industri Pusri & Pertamina",
    deskripsi:
      "Bekerja di industri petrokimia dan minyak bumi yang besar. Industri ini menjadi penggerak ekonomi utama Palembang.",
    icon: Factory,
  },
  {
    name: "Pedagang Pasar 16 Ilir",
    deskripsi:
      "Berdagang di pasar tradisional terbesar di Palembang. Pasar 16 Ilir adalah jantung perdagangan dan kehidupan ekonomi lokal.",
    icon: Store,
  },
]

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      staggerChildren: 0.1,
      delayChildren: 0.2,
    },
  },
}

const itemVariants = {
  hidden: { scale: 0, opacity: 0 },
  visible: {
    scale: 1,
    opacity: 1,
    transition: { duration: 0.5, ease: "easeOut" },
  },
}

export default function PekerjaanSection() {
  const ref = useRef(null)
  const isInView = useInView(ref, { once: true, margin: "-100px" })

  return (
    <section ref={ref} style={styles.section}>
      <div style={styles.container}>
        <motion.h2
          style={styles.sectionTitle}
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6 }}
        >
          Dinamika Ekonomi Palembang
        </motion.h2>
        <motion.div
          style={styles.titleUnderline}
          initial={{ scaleX: 0 }}
          whileInView={{ scaleX: 1 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6, delay: 0.2 }}
        />

        <motion.div
          style={styles.grid3}
          variants={containerVariants}
          initial="hidden"
          animate={isInView ? "visible" : "hidden"}
        >
          {pekerjaan.map((item, index) => {
            const Icon = item.icon
            return (
              <motion.div
                key={index}
                variants={itemVariants}
                style={styles.card}
                whileHover={{ y: -5 }}
                onMouseEnter={(e) => {
                  const el = e.currentTarget as HTMLElement
                  el.style.boxShadow = "0 20px 40px rgba(0,0,0,0.2)"
                }}
                onMouseLeave={(e) => {
                  const el = e.currentTarget as HTMLElement
                  el.style.boxShadow = "0 4px 6px rgba(0,0,0,0.1)"
                }}
              >
                <div style={styles.iconContainer}>
                  <div style={styles.iconBox}>
                    <Icon size={32} color="#800000" />
                  </div>
                </div>
                <h3 style={styles.cardTitle}>{item.name}</h3>
                <p style={styles.cardDescription}>{item.deskripsi}</p>
              </motion.div>
            )
          })}
        </motion.div>
      </div>
    </section>
  )
}

const styles = {
  section: {
    padding: "var(--spacing-2xl) 0",
    backgroundColor: "#FFF8DC",
    marginTop: "60px",
  },
  container: {
    maxWidth: "1200px",
    margin: "0 auto",
    padding: "0 var(--spacing-lg)",
  },
  sectionTitle: {
    fontSize: "var(--font-size-3xl)",
    fontWeight: 700,
    color: "#8b6f47",
    marginBottom: "1rem",
    textAlign: "center" as const,
  },
  titleUnderline: {
    height: "4px",
    width: "60px",
    background: "linear-gradient(90deg, #d4a574, #f39c12)",
    borderRadius: "2px",
    margin: "0 auto 3rem",
    display: "block",
  },
  grid3: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))",
    gap: "var(--spacing-lg)",
  },
  card: {
    backgroundColor: "white",
    borderRadius: "12px",
    padding: "var(--spacing-lg)",
    boxShadow: "0 4px 6px rgba(0,0,0,0.1)",
    transition: "all var(--transition-normal)",
    textAlign: "center" as const,
  },
  iconContainer: {
    display: "flex",
    justifyContent: "center",
    marginBottom: "1rem",
  },
  iconBox: {
    backgroundColor: "#f39c12",
    borderRadius: "50%",
    padding: "1rem",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  cardTitle: {
    fontSize: "var(--font-size-lg)",
    fontWeight: 700,
    color: "#8b6f47",
    marginBottom: "0.75rem",
  },
  cardDescription: {
    fontSize: "var(--font-size-sm)",
    color: "#555",
    lineHeight: 1.6,
  },
}
