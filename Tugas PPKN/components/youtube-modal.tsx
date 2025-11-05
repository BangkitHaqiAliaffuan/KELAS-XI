"use client"

import type React from "react"
import { motion, AnimatePresence } from "framer-motion"
import { X } from "lucide-react"

interface YouTubeModalProps {
  isOpen: boolean
  onClose: () => void
  videoId: string
  title: string
}

export default function YouTubeModal({ isOpen, onClose, videoId, title }: YouTubeModalProps) {
  return (
    <AnimatePresence>
      {isOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          onClick={onClose}
          style={styles.overlay}
        >
          <motion.div
            initial={{ scale: 0.9, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.9, opacity: 0 }}
            onClick={(e) => e.stopPropagation()}
            style={styles.modalContent}
          >
            <div style={styles.modalHeader}>
              <h3 style={styles.modalTitle}>{title}</h3>
              <button onClick={onClose} style={styles.closeButton} aria-label="Close modal">
                <X size={24} />
              </button>
            </div>
            <div style={styles.videoContainer}>
              <iframe
                width="100%"
                height="100%"
                src={`https://www.youtube.com/embed/${videoId}?autoplay=1`}
                title={title}
                frameBorder="0"
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                allowFullScreen
                style={{ borderRadius: "8px" }}
              />
            </div>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  )
}

const styles = {
  overlay: {
    position: "fixed" as const,
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: "rgba(0, 0, 0, 0.7)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    zIndex: 1000,
    backdropFilter: "blur(4px)",
  },
  modalContent: {
    backgroundColor: "white",
    borderRadius: "12px",
    overflow: "hidden",
    maxWidth: "900px",
    width: "90%",
    maxHeight: "90vh",
    display: "flex",
    flexDirection: "column" as const,
    boxShadow: "0 20px 60px rgba(0,0,0,0.3)",
  },
  modalHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "1.5rem",
    borderBottom: "1px solid #eee",
    backgroundColor: "#f8f6f1",
  },
  modalTitle: {
    fontSize: "var(--font-size-xl)",
    fontWeight: 700,
    color: "#8b6f47",
    margin: 0,
  },
  closeButton: {
    background: "none",
    border: "none",
    cursor: "pointer",
    color: "#8b6f47",
    padding: "0.5rem",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    transition: "all 0.3s ease",
  } as React.CSSProperties,
  videoContainer: {
    flex: 1,
    padding: "1.5rem",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    minHeight: "500px",
  },
}
