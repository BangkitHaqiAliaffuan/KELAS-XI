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
                style={{ 
                  borderRadius: "8px",
                  position: "absolute",
                  top: 0,
                  left: 0,
                  width: "100%",
                  height: "100%",
                }}
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
    backgroundColor: "rgba(0, 0, 0, 0.85)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    zIndex: 9999,
    backdropFilter: "blur(8px)",
    padding: "1rem",
  },
  modalContent: {
    backgroundColor: "white",
    borderRadius: "16px",
    overflow: "hidden",
    maxWidth: "1200px",
    width: "100%",
    maxHeight: "90vh",
    display: "flex",
    flexDirection: "column" as const,
    boxShadow: "0 25px 75px rgba(0,0,0,0.5)",
  },
  modalHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "1.5rem",
    borderBottom: "2px solid #FFD700",
    backgroundColor: "#800000",
  },
  modalTitle: {
    fontSize: "var(--font-size-xl)",
    fontWeight: 700,
    color: "#FFD700",
    margin: 0,
  },
  closeButton: {
    background: "rgba(255, 215, 0, 0.1)",
    border: "2px solid #FFD700",
    borderRadius: "50%",
    width: "40px",
    height: "40px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    cursor: "pointer",
    color: "#FFD700",
    padding: "0",
    transition: "all 0.3s ease",
  } as React.CSSProperties,
  videoContainer: {
    flex: 1,
    padding: "1.5rem",
    backgroundColor: "#000",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    position: "relative" as const,
    minHeight: "500px",
  },
}
