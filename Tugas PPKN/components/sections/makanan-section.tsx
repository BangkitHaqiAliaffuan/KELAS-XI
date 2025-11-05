"use client"

import type React from "react"

import { useEffect, useRef, useState } from "react"
import { motion, useInView } from "framer-motion"
import YouTubeModal from "../youtube-modal"

const foods = [
  {
    name: "Pempek",
    description:
      "Makanan khas Palembang yang terbuat dari ikan dan tepung tapioka. Pempek kapal selam adalah varian paling terkenal dengan telur di dalamnya. Disajikan dengan kuah cuko yang gurih dan pedas.",
    image: "/pempek-kapal-selam-khas-palembang.jpg",
    videoId: "dQw4w9WgXcQ",
  },
  {
    name: "Model",
    description:
      "Sejenis pempek yang berbentuk bulat pipih dengan isian daging ikan. Model memiliki tekstur yang lebih padat dan gurih dibanding pempek biasa. Menjadi pilihan favorit untuk camilan atau lauk pauk.",
    image: "/model-makanan-palembang.jpg",
    videoId: "dQw4w9WgXcQ",
  },
  {
    name: "Tekwan",
    description:
      "Sup tradisional Palembang dengan ikan, udang, dan telur puyuh. Tekwan memiliki kuah yang gurih dengan aroma rempah yang khas. Sering disajikan sebagai hidangan pembuka di acara-acara khusus.",
    image: "/tekwan-sup-palembang.jpg",
    videoId: "dQw4w9WgXcQ",
  },
  {
    name: "Laksan",
    description:
      "Makanan berupa mie dalam kuah santan yang gurih dan lezat. Laksan dilengkapi dengan berbagai topping seperti telur, udang, dan daging. Hidangan ini sempurna untuk sarapan atau makan siang.",
    image: "/laksan-mie-palembang.jpg",
    videoId: "dQw4w9WgXcQ",
  },
  {
    name: "Martabak HAR",
    description:
      "Martabak terang bulan khas Palembang dengan isian yang beragam. HAR adalah singkatan dari Haji Amir Rasyid, penjual martabak legendaris. Kulit yang tipis dan isian yang melimpah menjadi ciri khasnya.",
    image: "/martabak-har-palembang.jpg",
    videoId: "dQw4w9WgXcQ",
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
  hidden: { opacity: 0, x: -50 },
  visible: {
    opacity: 1,
    x: 0,
    transition: { duration: 0.6, ease: "easeOut" },
  },
}

export default function MakananSection() {
  const ref = useRef(null)
  const scrollContainerRef = useRef<HTMLDivElement>(null)
  const isInView = useInView(ref, { once: true, margin: "-100px" })
  const [selectedVideo, setSelectedVideo] = useState<{ id: string; title: string } | null>(null)
  const [showLeftArrow, setShowLeftArrow] = useState(false)
  const [showRightArrow, setShowRightArrow] = useState(true)

  const handleScroll = () => {
    const container = scrollContainerRef.current
    if (container) {
      setShowLeftArrow(container.scrollLeft > 0)
      setShowRightArrow(container.scrollLeft < container.scrollWidth - container.clientWidth - 10)
    }
  }

  const scroll = (direction: "left" | "right") => {
    const container = scrollContainerRef.current
    if (container) {
      const scrollAmount = 400
      container.scrollBy({
        left: direction === "left" ? -scrollAmount : scrollAmount,
        behavior: "smooth",
      })
    }
  }

  useEffect(() => {
    handleScroll()
    const container = scrollContainerRef.current
    if (container) {
      container.addEventListener("scroll", handleScroll)
      return () => container.removeEventListener("scroll", handleScroll)
    }
  }, [])

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
          Kuliner Legendaris Palembang
        </motion.h2>
        <motion.div
          style={styles.titleUnderline}
          initial={{ scaleX: 0 }}
          whileInView={{ scaleX: 1 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6, delay: 0.2 }}
        />

        <div style={styles.scrollWrapper}>
          {showLeftArrow && (
            <button
              onClick={() => scroll("left")}
              style={styles.scrollButton}
              onMouseEnter={(e) => {
                const el = e.currentTarget as HTMLElement
                el.style.backgroundColor = "rgba(139, 111, 71, 0.9)"
              }}
              onMouseLeave={(e) => {
                const el = e.currentTarget as HTMLElement
                el.style.backgroundColor = "rgba(139, 111, 71, 0.7)"
              }}
            >
              &#8249;
            </button>
          )}

          <motion.div
            ref={scrollContainerRef}
            style={styles.scrollContainer}
            variants={containerVariants}
            initial="hidden"
            animate={isInView ? "visible" : "hidden"}
          >
            {foods.map((food, index) => (
              <motion.div
                key={index}
                variants={itemVariants}
                style={styles.card}
                whileHover={{ y: -5 }}
                onMouseEnter={(e) => {
                  const el = e.currentTarget as HTMLElement
                  el.style.boxShadow = "0 20px 40px rgba(0,0,0,0.2)"
                  el.style.borderColor = "#d4a574"
                }}
                onMouseLeave={(e) => {
                  const el = e.currentTarget as HTMLElement
                  el.style.boxShadow = "0 4px 6px rgba(0,0,0,0.1)"
                  el.style.borderColor = "#ddd"
                }}
              >
                <div style={styles.cardImage}>
                  <img
                    src={food.image || "/placeholder.svg"}
                    alt={food.name}
                    style={{ width: "100%", height: "100%", objectFit: "cover" }}
                  />
                </div>
                <div style={styles.cardContent}>
                  <h3 style={styles.cardTitle}>{food.name}</h3>
                  <p style={styles.cardDescription}>{food.description}</p>
                  <button
                    onClick={() => setSelectedVideo({ id: food.videoId, title: food.name })}
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
          </motion.div>

          {showRightArrow && (
            <button
              onClick={() => scroll("right")}
              style={styles.scrollButton}
              onMouseEnter={(e) => {
                const el = e.currentTarget as HTMLElement
                el.style.backgroundColor = "rgba(139, 111, 71, 0.9)"
              }}
              onMouseLeave={(e) => {
                const el = e.currentTarget as HTMLElement
                el.style.backgroundColor = "rgba(139, 111, 71, 0.7)"
              }}
            >
              &#8250;
            </button>
          )}
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
  scrollWrapper: {
    display: "flex",
    alignItems: "center",
    gap: "1rem",
    position: "relative" as const,
  },
  scrollContainer: {
    display: "flex",
    overflowX: "auto" as const,
    gap: "var(--spacing-lg)",
    flex: 1,
    scrollBehavior: "smooth",
    scrollbarWidth: "none",
    msOverflowStyle: "none",
    paddingBottom: "0.5rem",
  },
  card: {
    backgroundColor: "white",
    borderRadius: "12px",
    overflow: "hidden",
    boxShadow: "0 4px 6px rgba(0,0,0,0.1)",
    transition: "all var(--transition-normal)",
    border: "1px solid #ddd",
    cursor: "pointer",
    minWidth: "280px",
    flex: "0 0 280px",
  },
  cardImage: {
    width: "100%",
    height: "200px",
    overflow: "hidden",
    backgroundColor: "#e0e0e0",
  },
  cardContent: {
    padding: "var(--spacing-lg)",
  },
  cardTitle: {
    fontSize: "var(--font-size-lg)",
    fontWeight: 700,
    color: "#8b6f47",
    marginBottom: "0.5rem",
  },
  cardDescription: {
    fontSize: "var(--font-size-sm)",
    color: "#555",
    lineHeight: 1.6,
    maxHeight: "120px",
    overflow: "hidden",
    display: "-webkit-box",
    WebkitLineClamp: 3,
    WebkitBoxOrient: "vertical" as const,
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
  scrollButton: {
    backgroundColor: "rgba(139, 111, 71, 0.7)",
    color: "white",
    border: "none",
    borderRadius: "50%",
    width: "44px",
    height: "44px",
    fontSize: "28px",
    cursor: "pointer",
    transition: "all 0.3s ease",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    flexShrink: 0,
  } as React.CSSProperties,
}
