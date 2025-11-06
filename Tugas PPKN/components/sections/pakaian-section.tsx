"use client"

import type React from "react"

import { motion } from "framer-motion"
import { useInView } from "framer-motion"
import { useRef, useState } from "react"
import YouTubeModal from "../youtube-modal"

const pakaian = [
  {
    name: "Aesan Gede",
    subtitle: "Busana Pengantin Wanita",
    keunikan: [
      "Songket emas yang berkilau dengan motif naga dan burung",
      "Mahkota emas (pending) yang megah dan berat",
      "Perhiasan emas yang melengkapi penampilan",
      "Warna dominan merah maroon dan emas",
    ],
    image: "/aesan-gede-busana-pengantin-wanita-palembang-songk.jpg",
    videoId: "A2xD26d68g0",
  },
  {
    name: "Aesan Pak Sangkong",
    subtitle: "Busana Pengantin Pria",
    keunikan: [
      "Songket merah maroon dengan benang emas yang indah",
      "Tanjak (mahkota pria) yang elegan dan berkilau",
      "Kain sarung dengan motif tradisional yang rumit",
      "Aksesori emas dan batu mulia sebagai pelengkap",
    ],
    image: "/aesan-pak-sangkong-busana-pengantin-pria-palembang.jpg",
    videoId: "KjBAyKDupdA",
  },
]

export default function PakaianSection() {
  const ref = useRef(null)
  const isInView = useInView(ref, { once: true, margin: "-100px" })
  const [selectedVideo, setSelectedVideo] = useState<{ id: string; title: string } | null>(null)

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
          Busana Tradisional Palembang
        </motion.h2>
        <motion.div
          style={styles.titleUnderline}
          initial={{ scaleX: 0 }}
          whileInView={{ scaleX: 1 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6, delay: 0.2 }}
        />

        <div style={styles.grid2}>
          {pakaian.map((item, index) => (
            <motion.div
              key={index}
              style={styles.card}
              initial={{ opacity: 0, scale: 0.9 }}
              whileInView={{ opacity: 1, scale: 1 }}
              whileHover={{ scale: 1.05 }}
              viewport={{ once: true }}
              transition={{ duration: 0.6, delay: index * 0.2 }}
            >
              <div style={styles.cardImage}>
                <img
                  src={item.image || "/placeholder.svg"}
                  alt={item.name}
                  style={{ width: "100%", height: "100%", objectFit: "cover" }}
                />
              </div>
              <div style={styles.cardContent}>
                <h3 style={styles.cardTitle}>{item.name}</h3>
                <p style={styles.cardSubtitle}>{item.subtitle}</p>
                <ul style={styles.list}>
                  {item.keunikan.map((keunikan, idx) => (
                    <li key={idx} style={styles.listItem}>
                      <span style={styles.bullet}>•</span>
                      <span>{keunikan}</span>
                    </li>
                  ))}
                </ul>
                <button
                  onClick={() => setSelectedVideo({ id: item.videoId, title: item.name })}
                  style={styles.videoButton}
                  onMouseEnter={(e) => {
                    const el = e.currentTarget as HTMLElement
                    el.style.backgroundColor = "#600000"
                  }}
                  onMouseLeave={(e) => {
                    const el = e.currentTarget as HTMLElement
                    el.style.backgroundColor = "#800000"
                  }}
                >
                  ▶ Lihat Video
                </button>
              </div>
            </motion.div>
          ))}
        </div>
      </div>

      {selectedVideo && (
        <YouTubeModal
          isOpen={!!selectedVideo}
          onClose={() => setSelectedVideo(null)}
          videoId={selectedVideo.id}
          title={selectedVideo.title}
        />
      )}
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
    gridTemplateColumns: "repeat(auto-fit, minmax(300px, 1fr))",
    gap: "var(--spacing-lg)",
  },
  card: {
    backgroundColor: "white",
    borderRadius: "12px",
    overflow: "hidden",
    boxShadow: "0 4px 6px rgba(0,0,0,0.1)",
    transition: "all var(--transition-normal)",
  },
  cardImage: {
    width: "100%",
    height: "320px",
    overflow: "hidden",
    backgroundColor: "#e0e0e0",
  },
  cardContent: {
    padding: "var(--spacing-lg)",
  },
  cardTitle: {
    fontSize: "var(--font-size-xl)",
    fontWeight: 700,
    color: "#8b6f47",
    marginBottom: "0.25rem",
  },
  cardSubtitle: {
    fontSize: "var(--font-size-base)",
    color: "#f39c12",
    fontWeight: 600,
    marginBottom: "1rem",
  },
  list: {
    listStyle: "none",
    padding: 0,
    margin: 0,
  },
  listItem: {
    display: "flex",
    gap: "0.75rem",
    marginBottom: "0.5rem",
    fontSize: "var(--font-size-sm)",
    color: "#555",
    lineHeight: 1.5,
  },
  bullet: {
    color: "#f39c12",
    fontWeight: 700,
    marginTop: "0.25rem",
  },
  videoButton: {
    marginTop: "1rem",
    padding: "0.6rem 1.2rem",
    backgroundColor: "#800000",
    color: "white",
    border: "none",
    borderRadius: "6px",
    fontSize: "var(--font-size-sm)",
    fontWeight: 600,
    cursor: "pointer",
    transition: "all 0.3s ease",
    width: "100%",
  } as React.CSSProperties,
}
