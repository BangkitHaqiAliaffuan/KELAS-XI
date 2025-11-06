"use client"

import { motion } from "framer-motion"
import { useInView } from "framer-motion"
import { useRef } from "react"
import { Heart, Users, Award, Home } from "lucide-react"

const nilai = [
  {
    title: "Keramahan & Keterbukaan",
    description:
      "Masyarakat Palembang terkenal dengan keramahan dan keterbukaan mereka terhadap tamu dan pendatang. Nilai ini tercermin dalam setiap interaksi sosial dan kehidupan sehari-hari. Keramahan adalah identitas yang membuat Palembang istimewa.",
    icon: Heart,
    bgColor: "#ffe0e6",
  },
  {
    title: "Gotong Royong dalam Begawi",
    description:
      "Semangat saling membantu dan bekerja sama dalam setiap acara hajatan. Gotong royong adalah nilai fundamental yang menjaga kohesi sosial masyarakat. Tradisi ini membuktikan kekuatan kebersamaan dalam menghadapi tantangan.",
    icon: Users,
    bgColor: "#e0f2fe",
  },
  {
    title: "Pelestarian Warisan Budaya",
    description:
      "Komitmen untuk menjaga dan mewariskan budaya kepada generasi mendatang. Setiap tradisi, seni, dan kuliner dijaga dengan penuh dedikasi dan cinta. Pelestarian budaya adalah tanggung jawab bersama semua masyarakat Palembang.",
    icon: Award,
    bgColor: "#fef3c7",
  },
  {
    title: "Semangat Kekeluargaan",
    description:
      "Hubungan yang erat dan saling peduli antar anggota masyarakat. Semangat kekeluargaan membuat Palembang terasa seperti satu keluarga besar. Nilai ini adalah fondasi dari semua interaksi sosial yang harmonis.",
    icon: Home,
    bgColor: "#dcfce7",
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
  hidden: { opacity: 0, y: 20 },
  visible: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.6, ease: "easeOut" },
  },
}

export default function NilaiSection() {
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
          Nilai yang Diwariskan
        </motion.h2>
        <motion.div
          style={styles.titleUnderline}
          initial={{ scaleX: 0 }}
          whileInView={{ scaleX: 1 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6, delay: 0.2 }}
        />

        <motion.div
          style={styles.grid2}
          variants={containerVariants}
          initial="hidden"
          animate={isInView ? "visible" : "hidden"}
        >
          {nilai.map((item, index) => {
            const Icon = item.icon
            return (
              <motion.div
                key={index}
                variants={itemVariants}
                style={{ ...styles.card, backgroundColor: item.bgColor }}
                whileHover={{ y: -8, boxShadow: "0 20px 40px rgba(0,0,0,0.1)" }}
              >
                <div style={styles.cardHeader}>
                  <div style={styles.iconBox}>
                    <Icon size={28} color="white" />
                  </div>
                  <h3 style={styles.cardTitle}>{item.title}</h3>
                </div>
                <p style={styles.cardDescription}>{item.description}</p>
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
    marginTop: "60px"
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
  grid2: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(280px, 1fr))",
    gap: "var(--spacing-lg)",
  },
  card: {
    borderRadius: "12px",
    padding: "var(--spacing-lg)",
    boxShadow: "0 4px 6px rgba(0,0,0,0.1)",
    transition: "all var(--transition-normal)",
  },
  cardHeader: {
    display: "flex",
    alignItems: "flex-start",
    gap: "1rem",
    marginBottom: "1rem",
  },
  iconBox: {
    backgroundColor: "#800000",
    borderRadius: "50%",
    padding: "0.75rem",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    flexShrink: 0,
  },
  cardTitle: {
    fontSize: "var(--font-size-lg)",
    fontWeight: 700,
    color: "#8b6f47",
  },
  cardDescription: {
    fontSize: "var(--font-size-sm)",
    color: "#555",
    lineHeight: 1.6,
  },
}
