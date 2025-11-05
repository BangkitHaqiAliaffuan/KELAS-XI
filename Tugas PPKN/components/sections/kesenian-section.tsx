"use client"

import type React from "react"

import { motion, AnimatePresence } from "framer-motion"
import { useState, useEffect } from "react"
import { ChevronLeft, ChevronRight, Play, Pause } from "lucide-react"
import YouTubeModal from "../youtube-modal"

const kesenian = [
  {
    title: "Tari Gending Sriwijaya",
    description:
      "Tari penyambutan yang menampilkan gerakan gemulai dan anggun. Penari bergerak dengan lembut mengikuti irama musik tradisional. Tari ini sering ditampilkan dalam acara penyambutan tamu penting dan perayaan budaya.",
    image: "/tari-gending-sriwijaya-palembang.jpg",
    videoId: "dQw4w9WgXcQ", // Replace with actual Palembang dance video
  },
  {
    title: "Musik Gambus",
    description:
      "Musik tradisional dengan pengaruh Arab-Melayu yang kaya akan harmoni. Gambus adalah alat musik petik yang menghasilkan suara yang merdu dan menyentuh hati. Musik ini sering dimainkan dalam acara-acara sosial dan keagamaan.",
    image: "/musik-gambus-palembang.jpg",
    videoId: "dQw4w9WgXcQ", // Replace with actual Gambus music video
  },
  {
    title: "Seni Ukir Palembang",
    description:
      "Seni ukir tradisional dengan motif naga dan burung yang rumit dan indah. Setiap ukiran memiliki makna filosofis yang mendalam tentang kehidupan dan budaya. Karya ukir ini sering ditemukan pada furniture dan dekorasi rumah tradisional.",
    image: "/seni-ukir-palembang-motif-naga.jpg",
    videoId: "dQw4w9WgXcQ", // Replace with actual carving video
  },
  {
    title: "Kain Songket",
    description:
      "Tenun emas yang mendunia dengan motif tradisional yang memukau. Setiap helai benang emas ditenun dengan tangan oleh pengrajin berpengalaman. Kain songket Palembang telah diakui sebagai warisan budaya dunia.",
    image: "/kain-songket-palembang-tenun-emas.jpg",
    videoId: "dQw4w9WgXcQ", // Replace with actual songket weaving video
  },
]

export default function KesenianSection() {
  const [activeSlide, setActiveSlide] = useState(0)
  const [autoPlay, setAutoPlay] = useState(true)
  const [isModalOpen, setIsModalOpen] = useState(false)

  useEffect(() => {
    if (!autoPlay) return

    const timer = setInterval(() => {
      setActiveSlide((prev) => (prev + 1) % kesenian.length)
    }, 5000)

    return () => clearInterval(timer)
  }, [autoPlay])

  const nextSlide = () => {
    setActiveSlide((prev) => (prev + 1) % kesenian.length)
    setAutoPlay(false)
  }

  const prevSlide = () => {
    setActiveSlide((prev) => (prev - 1 + kesenian.length) % kesenian.length)
    setAutoPlay(false)
  }

  const toggleAutoPlay = () => {
    setAutoPlay(!autoPlay)
  }

  return (
    <section style={styles.section}>
      <div style={styles.container}>
        <motion.h2
          style={styles.sectionTitle}
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6 }}
        >
          Warisan Seni Budaya
        </motion.h2>
        <motion.div
          style={styles.titleUnderline}
          initial={{ scaleX: 0 }}
          whileInView={{ scaleX: 1 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6, delay: 0.2 }}
        />

        <div style={styles.controlsContainer}>
          <button
            onClick={toggleAutoPlay}
            style={styles.controlButton}
            aria-label={autoPlay ? "Pause carousel" : "Play carousel"}
            title={autoPlay ? "Pause" : "Play"}
          >
            {autoPlay ? <Pause size={20} /> : <Play size={20} />}
          </button>
          <button onClick={prevSlide} style={styles.controlButton} aria-label="Previous slide" title="Previous">
            <ChevronLeft size={20} />
          </button>
          <button onClick={nextSlide} style={styles.controlButton} aria-label="Next slide" title="Next">
            <ChevronRight size={20} />
          </button>
        </div>

        <div style={styles.carouselContainer}>
          <AnimatePresence mode="wait">
            <motion.div
              key={activeSlide}
              initial={{ x: 300, opacity: 0 }}
              animate={{ x: 0, opacity: 1 }}
              exit={{ x: -300, opacity: 0 }}
              transition={{ duration: 0.5 }}
              style={styles.carouselContent}
            >
              <div style={styles.carouselGrid}>
                <div style={styles.carouselImage}>
                  <img
                    src={kesenian[activeSlide].image || "/placeholder.svg"}
                    alt={kesenian[activeSlide].title}
                    style={{ width: "100%", height: "100%", objectFit: "cover" }}
                  />
                </div>
                <div style={styles.carouselText}>
                  <h3 style={styles.carouselTitle}>{kesenian[activeSlide].title}</h3>
                  <p style={styles.carouselDescription}>{kesenian[activeSlide].description}</p>
                  <button
                    onClick={() => setIsModalOpen(true)}
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
            </motion.div>
          </AnimatePresence>

          <button onClick={prevSlide} style={{ ...styles.navButton, left: "-80px" }} aria-label="Previous slide">
            <ChevronLeft size={24} />
          </button>
          <button onClick={nextSlide} style={{ ...styles.navButton, right: "-80px" }} aria-label="Next slide">
            <ChevronRight size={24} />
          </button>

          <div style={styles.dotsContainer}>
            {kesenian.map((_, index) => (
              <motion.button
                key={index}
                onClick={() => {
                  setActiveSlide(index)
                  setAutoPlay(false)
                }}
                style={{
                  ...styles.dot,
                  width: index === activeSlide ? "32px" : "12px",
                  backgroundColor: index === activeSlide ? "#800000" : "#f39c12",
                }}
                whileHover={{ scale: 1.2 }}
                aria-label={`Go to slide ${index + 1}`}
              />
            ))}
          </div>
        </div>
      </div>

      <YouTubeModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        videoId={kesenian[activeSlide].videoId}
        title={kesenian[activeSlide].title}
      />
    </section>
  )
}

const styles = {
  section: {
    padding: "var(--spacing-2xl) 0",
    backgroundColor: "#f8f6f1",
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
  controlsContainer: {
    display: "flex",
    justifyContent: "center",
    gap: "1rem",
    marginBottom: "2rem",
  },
  controlButton: {
    backgroundColor: "#800000",
    color: "white",
    border: "none",
    padding: "10px 14px",
    borderRadius: "50%",
    cursor: "pointer",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    transition: "all 0.3s ease",
    fontSize: "0",
  } as React.CSSProperties & { "&:hover": any },
  carouselContainer: {
    position: "relative" as const,
  },
  carouselContent: {
    backgroundColor: "white",
    borderRadius: "12px",
    overflow: "hidden",
    boxShadow: "0 12px 24px rgba(0,0,0,0.15)",
  },
  carouselGrid: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: "2rem",
    padding: "2rem",
  },
  carouselImage: {
    height: "320px",
    borderRadius: "8px",
    overflow: "hidden",
    backgroundColor: "#e0e0e0",
  },
  carouselText: {
    display: "flex",
    flexDirection: "column" as const,
    justifyContent: "center",
  },
  carouselTitle: {
    fontSize: "var(--font-size-2xl)",
    fontWeight: 700,
    color: "#8b6f47",
    marginBottom: "1rem",
  },
  carouselDescription: {
    fontSize: "var(--font-size-base)",
    color: "#555",
    lineHeight: 1.8,
  },
  dotsContainer: {
    display: "flex",
    justifyContent: "center",
    gap: "0.5rem",
    marginTop: "2rem",
  },
  dot: {
    height: "12px",
    borderRadius: "6px",
    border: "none",
    cursor: "pointer",
    transition: "all var(--transition-normal)",
  },
  videoButton: {
    marginTop: "1.5rem",
    padding: "0.75rem 1.5rem",
    backgroundColor: "#800000",
    color: "white",
    border: "none",
    borderRadius: "6px",
    fontSize: "var(--font-size-base)",
    fontWeight: 600,
    cursor: "pointer",
    transition: "all 0.3s ease",
  } as React.CSSProperties,
}
