"use client"

import type React from "react"

import { motion } from "framer-motion"
import { useInView } from "framer-motion"
import { useRef, useState } from "react"
import YouTubeModal from "../youtube-modal"

const traditions = [
  {
    title: "Ngembang",
    description:
      "Mandi di Sungai Musi menjelang Lebaran sebagai simbol pembersihan diri. Tradisi ini dilakukan oleh ribuan masyarakat Palembang setiap tahunnya. Ngembang memiliki makna spiritual untuk memulai hidup baru yang lebih baik.",
    videoId: "dQw4w9WgXcQ", // Replace with actual Ngembang video
  },
  {
    title: "Akikah dengan Pempek",
    description:
      "Tradisi unik menyajikan pempek dalam acara akikah, bukan hanya kambing. Pempek menjadi hidangan utama yang dibagikan kepada keluarga dan tetangga. Ini menunjukkan bagaimana budaya kuliner terintegrasi dalam tradisi keagamaan.",
    videoId: "dQw4w9WgXcQ", // Replace with actual Akikah video
  },
  {
    title: "Begawi",
    description:
      "Pesta besar untuk hajatan yang melibatkan seluruh masyarakat dalam gotong royong. Begawi adalah wujud nyata dari semangat kebersamaan dan saling membantu. Acara ini memperkuat ikatan sosial dan keluarga di masyarakat Palembang.",
    videoId: "dQw4w9WgXcQ", // Replace with actual Begawi video
  },
  {
    title: "Bahasa Khas Palembang",
    description:
      'Menggunakan kata-kata unik seperti "galo" (saya), "gaek" (kamu), "kito" (kita). Bahasa Palembang memiliki dialek yang kaya dan penuh dengan keunikan. Bahasa ini adalah identitas budaya yang perlu dilestarikan untuk generasi mendatang.',
    videoId: "dQw4w9WgXcQ", // Replace with actual language video
  },
]

export default function AdatUnikSection() {
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
          Tradisi yang Melekat
        </motion.h2>
        <motion.div
          style={styles.titleUnderline}
          initial={{ scaleX: 0 }}
          whileInView={{ scaleX: 1 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6, delay: 0.2 }}
        />

        <div style={styles.timelineContainer}>
          {/* Timeline line */}
          <div style={styles.timelineLine} />

          <div style={styles.timelineContent}>
            {traditions.map((tradition, index) => (
              <motion.div
                key={index}
                style={{
                  ...styles.timelineItem,
                  flexDirection: index % 2 === 0 ? "row" : "row-reverse",
                }}
                initial={{ opacity: 0, x: index % 2 === 0 ? -50 : 50 }}
                whileInView={{ opacity: 1, x: 0 }}
                viewport={{ once: true }}
                transition={{ duration: 0.6, delay: index * 0.1 }}
              >
                {/* Content */}
                <div style={styles.timelineContentBox}>
                  <div style={styles.timelineCard}>
                    <h3 style={styles.timelineTitle}>{tradition.title}</h3>
                    <p style={styles.timelineDescription}>{tradition.description}</p>
                    <button
                      onClick={() => setSelectedVideo({ id: tradition.videoId, title: tradition.title })}
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
                </div>

                {/* Timeline point */}
                <div style={styles.timelinePointContainer}>
                  <motion.div style={styles.timelinePoint} whileHover={{ scale: 1.2 }}>
                    {index + 1}
                  </motion.div>
                </div>

                {/* Empty space for alternating layout */}
                <div style={styles.timelineEmpty} />
              </motion.div>
            ))}
          </div>
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
    backgroundColor: "#f8f6f1",
  },
  container: {
    maxWidth: "1000px",
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
  timelineContainer: {
    position: "relative" as const,
  },
  timelineLine: {
    position: "absolute" as const,
    left: "50%",
    top: 0,
    bottom: 0,
    width: "4px",
    backgroundColor: "#f39c12",
    transform: "translateX(-50%)",
  },
  timelineContent: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "3rem",
  },
  timelineItem: {
    display: "flex",
    gap: "2rem",
  },
  timelineContentBox: {
    flex: 1,
  },
  timelineCard: {
    backgroundColor: "white",
    borderRadius: "8px",
    padding: "1.5rem",
    boxShadow: "0 4px 6px rgba(0,0,0,0.1)",
    borderLeft: "4px solid #800000",
  },
  timelineTitle: {
    fontSize: "var(--font-size-xl)",
    fontWeight: 700,
    color: "#8b6f47",
    marginBottom: "0.75rem",
  },
  timelineDescription: {
    fontSize: "var(--font-size-sm)",
    color: "#555",
    lineHeight: 1.6,
  },
  timelinePointContainer: {
    display: "flex",
    justifyContent: "center",
    alignItems: "flex-start",
    paddingTop: "0.5rem",
  },
  timelinePoint: {
    width: "48px",
    height: "48px",
    backgroundColor: "#800000",
    borderRadius: "50%",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    color: "white",
    fontWeight: 700,
    fontSize: "var(--font-size-lg)",
    boxShadow: "0 4px 12px rgba(0,0,0,0.2)",
    zIndex: 10,
  },
  timelineEmpty: {
    flex: 1,
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
  } as React.CSSProperties,
}
