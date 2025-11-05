"use client"

import { motion } from "framer-motion"

export default function HeroSection() {
  return (
    <section style={styles.heroSection}>
      <div style={styles.heroBackground} />

      {/* Decorative batik pattern */}
      <div style={styles.batikPattern} />

      <motion.div
        style={styles.heroContent}
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8, ease: "easeOut" }}
      >
        <h1 style={styles.heroTitle}>Keberagaman Kota Palembang</h1>
        <p style={styles.heroSubtitle}>Mutiara di Tepi Sungai Musi</p>
      </motion.div>
    </section>
  )
}

const styles = {
  heroSection: {
    position: "relative" as const,
    minHeight: "100vh",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    overflow: "hidden",
  },
  heroBackground: {
    position: "absolute" as const,
    inset: 0,
    background: "linear-gradient(to bottom, #800000, #FFD700)",
  },
  batikPattern: {
    position: "absolute" as const,
    inset: 0,
    opacity: 0.05,
    backgroundImage: `url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="50" cy="50" r="30" fill="none" stroke="%23000" strokeWidth="1"/><circle cx="50" cy="50" r="20" fill="none" stroke="%23000" strokeWidth="0.5"/></svg>')`,
  },
  heroContent: {
    position: "relative" as const,
    zIndex: 10,
    textAlign: "center" as const,
    padding: "0 1rem",
  },
  heroTitle: {
    fontSize: "clamp(2rem, 8vw, 4rem)",
    fontWeight: 700,
    color: "white",
    marginBottom: "1rem",
    textShadow: "2px 2px 4px rgba(0,0,0,0.3)",
  },
  heroSubtitle: {
    fontSize: "clamp(1.25rem, 4vw, 2rem)",
    color: "rgba(255,255,255,0.9)",
    fontWeight: 300,
    textShadow: "1px 1px 2px rgba(0,0,0,0.3)",
  },
}
