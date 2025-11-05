"use client"

import { motion } from "framer-motion"
import { useInView } from "framer-motion"
import { useRef } from "react"
import { Quote } from "lucide-react"

export default function KesimpulanSection() {
  const ref = useRef(null)
  const isInView = useInView(ref, { once: true, margin: "-100px" })

  return (
    <section ref={ref} style={styles.section}>
      <div style={styles.container}>
        <motion.div
          style={styles.card}
          initial={{ opacity: 0, y: 30 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.8 }}
        >
          <div style={styles.quoteIconTop}>
            <motion.div
              initial={{ scale: 0, rotate: -180 }}
              whileInView={{ scale: 1, rotate: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.6, delay: 0.2 }}
            >
              <Quote size={48} color="#f39c12" />
            </motion.div>
          </div>

          <motion.p
            style={styles.cardText}
            initial={{ opacity: 0 }}
            whileInView={{ opacity: 1 }}
            viewport={{ once: true }}
            transition={{ duration: 0.8, delay: 0.3 }}
          >
            Palembang merupakan kota yang kaya akan keberagaman budaya, dari kuliner ikonik seperti pempek yang mendunia
            hingga seni songket yang menjadi kebanggaan. Nilai gotong royong yang terwujud dalam tradisi begawi dan
            keramahan masyarakat Palembang menjadi fondasi kokoh dalam menciptakan harmoni di tengah keberagaman.
            Warisan budaya yang dijaga hingga kini membuktikan kuatnya jati diri masyarakat Palembang.
          </motion.p>

          <div style={styles.quoteIconBottom}>
            <motion.div
              initial={{ scale: 0, rotate: 180 }}
              whileInView={{ scale: 1, rotate: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.6, delay: 0.4 }}
            >
              <Quote size={48} color="#f39c12" style={{ transform: "rotate(180deg)" }} />
            </motion.div>
          </div>
        </motion.div>

        <motion.div
          style={styles.footer}
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6, delay: 0.5 }}
        >
          <p style={styles.footerText}>Keberagaman adalah Kekuatan Palembang</p>
        </motion.div>
      </div>
    </section>
  )
}

const styles = {
  section: {
    padding: "var(--spacing-2xl) 0",
    background: "linear-gradient(to bottom, #FFF8DC, #FFE4B5)",
  },
  container: {
    maxWidth: "900px",
    margin: "0 auto",
    padding: "0 var(--spacing-lg)",
  },
  card: {
    backgroundColor: "white",
    borderRadius: "12px",
    border: "4px solid #f39c12",
    padding: "3rem",
    boxShadow: "0 12px 24px rgba(0,0,0,0.1)",
  },
  quoteIconTop: {
    display: "flex",
    justifyContent: "center",
    marginBottom: "1.5rem",
  },
  cardText: {
    fontSize: "var(--font-size-lg)",
    color: "#333",
    lineHeight: 1.8,
    textAlign: "center" as const,
    marginBottom: "1.5rem",
  },
  quoteIconBottom: {
    display: "flex",
    justifyContent: "center",
    marginTop: "1.5rem",
  },
  footer: {
    textAlign: "center" as const,
    marginTop: "3rem",
  },
  footerText: {
    fontSize: "var(--font-size-lg)",
    fontWeight: 600,
    color: "#8b6f47",
  },
}
