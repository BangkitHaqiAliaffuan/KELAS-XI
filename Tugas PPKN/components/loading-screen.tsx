"use client"

import { useEffect, useState } from "react"

export default function LoadingScreen() {
  const [isVisible, setIsVisible] = useState(true)

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsVisible(false)
    }, 2500)

    return () => clearTimeout(timer)
  }, [])

  if (!isVisible) return null

  return (
    <div style={styles.container}>
      <div style={styles.backgroundImage}></div>
      <div style={styles.overlay}></div>

      <div style={styles.content}>
        {/* Animated circles */}
        <div style={styles.circlesContainer}>
          <div style={{ ...styles.circle, ...styles.circle1 }}></div>
          <div style={{ ...styles.circle, ...styles.circle2 }}></div>
          <div style={{ ...styles.circle, ...styles.circle3 }}></div>
        </div>

        {/* Loading text */}
        <h1 style={styles.title}>Keberagaman Palembang</h1>
        <p style={styles.subtitle}>Memuat konten budaya...</p>

        {/* Progress bar */}
        <div style={styles.progressBar}>
          <div style={styles.progressFill}></div>
        </div>
      </div>
    </div>
  )
}

const styles = {
  container: {
    position: "fixed" as const,
    top: 0,
    left: 0,
    width: "100%",
    height: "100%",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    zIndex: 9999,
    animation: "fadeOut 0.5s ease-in-out 2s forwards",
  },
  backgroundImage: {
    position: "absolute" as const,
    top: 0,
    left: 0,
    width: "100%",
    height: "100%",
    backgroundImage: "url('/traditional-palembang-architecture-with-golden-orn.jpg')",
    backgroundSize: "cover",
    backgroundPosition: "center",
    backgroundAttachment: "fixed",
  },
  overlay: {
    position: "absolute" as const,
    top: 0,
    left: 0,
    width: "100%",
    height: "100%",
    background: "linear-gradient(135deg, rgba(26, 26, 46, 0.85) 0%, rgba(22, 33, 62, 0.85) 100%)",
    backdropFilter: "blur(2px)",
  },
  content: {
    position: "relative" as const,
    zIndex: 10,
    textAlign: "center" as const,
    display: "flex",
    flexDirection: "column" as const,
    alignItems: "center",
    gap: "30px",
  },
  circlesContainer: {
    position: "relative" as const,
    width: "120px",
    height: "120px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  circle: {
    position: "absolute" as const,
    borderRadius: "50%",
    border: "3px solid transparent",
    borderTopColor: "#d4af37",
  },
  circle1: {
    width: "80px",
    height: "80px",
    animation: "spin 2s linear infinite",
  },
  circle2: {
    width: "60px",
    height: "60px",
    animation: "spinReverse 2.5s linear infinite",
    borderTopColor: "#8b4513",
  },
  circle3: {
    width: "40px",
    height: "40px",
    animation: "spin 3s linear infinite",
    borderTopColor: "#c41e3a",
  },
  title: {
    fontSize: "48px",
    fontWeight: "700",
    color: "#d4af37",
    margin: 0,
    fontFamily: "'Geist', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
    letterSpacing: "2px",
    textShadow: "0 4px 12px rgba(0, 0, 0, 0.5)",
  },
  subtitle: {
    fontSize: "18px",
    color: "#e8d4a8",
    margin: 0,
    fontFamily: "'Geist', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
    fontWeight: "400",
    animation: "pulse 1.5s ease-in-out infinite",
    textShadow: "0 2px 8px rgba(0, 0, 0, 0.5)",
  },
  progressBar: {
    width: "200px",
    height: "4px",
    background: "rgba(212, 175, 55, 0.2)",
    borderRadius: "2px",
    overflow: "hidden",
  },
  progressFill: {
    height: "100%",
    background: "linear-gradient(90deg, #d4af37, #8b4513, #c41e3a)",
    animation: "progress 2s ease-in-out forwards",
  },
}

// Add global styles for animations
if (typeof document !== "undefined") {
  const style = document.createElement("style")
  style.textContent = `
    @keyframes spin {
      from { transform: rotate(0deg); }
      to { transform: rotate(360deg); }
    }
    
    @keyframes spinReverse {
      from { transform: rotate(360deg); }
      to { transform: rotate(0deg); }
    }
    
    @keyframes pulse {
      0%, 100% { opacity: 0.6; }
      50% { opacity: 1; }
    }
    
    @keyframes progress {
      from { width: 0%; }
      to { width: 100%; }
    }
    
    @keyframes fadeOut {
      from { opacity: 1; }
      to { opacity: 0; }
    }
  `
  document.head.appendChild(style)
}
