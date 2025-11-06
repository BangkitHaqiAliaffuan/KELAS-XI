"use client"

import { motion } from "framer-motion"
import { useEffect } from "react"
import confetti from "canvas-confetti"

interface ThankYouPageProps {
  onBackToStart: () => void
}

export default function ThankYouPage({ onBackToStart }: ThankYouPageProps) {
  useEffect(() => {
    // Confetti animation on load
    const duration = 3000
    const animationEnd = Date.now() + duration
    const defaults = { startVelocity: 30, spread: 360, ticks: 60, zIndex: 100 }

    function randomInRange(min: number, max: number) {
      return Math.random() * (max - min) + min
    }

    const interval = setInterval(function () {
      const timeLeft = animationEnd - Date.now()

      if (timeLeft <= 0) {
        return clearInterval(interval)
      }

      const particleCount = 50 * (timeLeft / duration)

      confetti({
        ...defaults,
        particleCount,
        origin: { x: randomInRange(0.1, 0.3), y: Math.random() - 0.2 },
        colors: ["#FFD700", "#800000", "#d4af37", "#FFF8DC"],
      })
      confetti({
        ...defaults,
        particleCount,
        origin: { x: randomInRange(0.7, 0.9), y: Math.random() - 0.2 },
        colors: ["#FFD700", "#800000", "#d4af37", "#FFF8DC"],
      })
    }, 250)

    return () => clearInterval(interval)
  }, [])

  return (
    <section className="min-h-screen flex items-center justify-center relative overflow-hidden bg-gradient-to-br from-[#800000] via-[#a52a2a] to-[#FFD700] mt-15">
      {/* Decorative Background */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-1/4 left-1/4 w-64 h-64 bg-[#FFD700] rounded-full blur-3xl animate-pulse" />
        <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-white rounded-full blur-3xl animate-pulse delay-1000" />
      </div>

      {/* Batik Pattern */}
      <div
        className="absolute inset-0 opacity-5"
        style={{
          backgroundImage: `url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="50" cy="50" r="30" fill="none" stroke="%23000" strokeWidth="1"/><circle cx="50" cy="50" r="20" fill="none" stroke="%23000" strokeWidth="0.5"/><circle cx="50" cy="50" r="10" fill="none" stroke="%23000" strokeWidth="0.3"/></svg>')`,
          backgroundSize: "100px 100px",
        }}
      />

      <motion.div
        initial={{ opacity: 0, scale: 0.8 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.8, ease: "easeOut" }}
        className="relative z-10 text-center px-4 max-w-4xl mx-auto"
      >
        {/* Decorative Top Border */}
        <motion.div
          initial={{ width: 0 }}
          animate={{ width: "100%" }}
          transition={{ delay: 0.5, duration: 1 }}
          className="h-1 bg-gradient-to-r from-transparent via-[#FFD700] to-transparent mb-12 mx-auto max-w-md"
        />

        {/* Main Content */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3, duration: 0.8 }}
        >
          {/* Thank You Text */}
          <h1 className="text-5xl lg:text-7xl font-bold text-white mb-6 tracking-wider" style={{ fontFamily: "serif" }}>
            Terima Kasih
          </h1>

          <div className="my-12 relative">
            <div className="absolute left-0 top-1/2 w-full h-px bg-gradient-to-r from-transparent via-[#FFD700] to-transparent" />
            <div className="relative inline-block px-6 bg-[#800000]">
              <svg
                className="w-16 h-16 mx-auto text-[#FFD700] opacity-60"
                fill="currentColor"
                viewBox="0 0 24 24"
              >
                <path d="M14.017 21v-7.391c0-5.704 3.731-9.57 8.983-10.609l.995 2.151c-2.432.917-3.995 3.638-3.995 5.849h4v10h-9.983zm-14.017 0v-7.391c0-5.704 3.748-9.57 9-10.609l.996 2.151c-2.433.917-3.996 3.638-3.996 5.849h3.983v10h-9.983z" />
              </svg>
            </div>
          </div>

          {/* Quote Box */}
          <div className="backdrop-blur-lg bg-white/10 border-2 border-[#FFD700]/30 rounded-2xl p-8 lg:p-12 mb-12 shadow-2xl">
            <p className="text-white text-xl lg:text-2xl leading-relaxed mb-6" style={{ fontFamily: "serif" }}>
              Keberagaman adalah kekayaan budaya yang harus kita jaga dan lestarikan. Kota Palembang dengan segala
              pesona budayanya adalah bukti nyata betapa indahnya Indonesia.
            </p>
            <div className="h-px bg-gradient-to-r from-transparent via-[#FFD700] to-transparent my-6" />
            <p className="text-[#FFD700] text-lg lg:text-xl italic">
              "Bhinneka Tunggal Ika - Berbeda-beda tetapi tetap satu"
            </p>
          </div>

          {/* Credits */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.8, duration: 0.8 }}
            className="mb-12"
          >
            <div className="backdrop-blur-md bg-white/5 border border-[#FFD700]/20 rounded-xl p-6 inline-block">
              <p className="text-white/90 text-lg mb-2">Dipersembahkan oleh:</p>
              <p className="text-[#FFD700] font-bold text-2xl mb-1">Kelas XI RPL</p>
              <p className="text-white/70 text-sm">Tugas PPKn - Keberagaman Budaya Indonesia</p>
            </div>
          </motion.div>

          {/* Back Button */}
          <motion.button
            onClick={onBackToStart}
            whileHover={{ scale: 1.05, boxShadow: "0 0 30px rgba(255,215,0,0.4)" }}
            whileTap={{ scale: 0.95 }}
            className="bg-gradient-to-r from-[#FFD700] to-[#d4af37] text-[#800000] font-bold text-lg px-10 py-4 rounded-full shadow-lg border-2 border-[#FFD700] hover:from-[#ffed4e] hover:to-[#FFD700] transition-all duration-300"
          >
            🏠 Kembali ke Awal
          </motion.button>
        </motion.div>

        {/* Decorative Bottom Border */}
        <motion.div
          initial={{ width: 0 }}
          animate={{ width: "100%" }}
          transition={{ delay: 0.5, duration: 1 }}
          className="h-1 bg-gradient-to-r from-transparent via-[#FFD700] to-transparent mt-12 mx-auto max-w-md"
        />
      </motion.div>

      {/* Floating Ornaments */}
      <motion.div
        animate={{
          y: [0, -20, 0],
          rotate: [0, 5, 0],
        }}
        transition={{
          duration: 4,
          repeat: Infinity,
          ease: "easeInOut",
        }}
        className="absolute top-20 left-10 w-16 h-16 border-4 border-[#FFD700]/30 rounded-full"
      />
      <motion.div
        animate={{
          y: [0, 20, 0],
          rotate: [0, -5, 0],
        }}
        transition={{
          duration: 5,
          repeat: Infinity,
          ease: "easeInOut",
        }}
        className="absolute bottom-20 right-10 w-20 h-20 border-4 border-white/20 rounded-full"
      />
    </section>
  )
}
