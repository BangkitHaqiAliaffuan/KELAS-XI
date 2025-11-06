"use client"

import { motion } from "framer-motion"
import { ChevronDown } from "lucide-react"

export default function HeroSection() {
  return (
    <section className="relative min-h-screen flex items-center justify-center overflow-hidden">
      {/* Animated Gradient Background */}
      <motion.div
        animate={{
          background: [
            "linear-gradient(135deg, #800000 0%, #a52a2a 50%, #FFD700 100%)",
            "linear-gradient(135deg, #5c0000 0%, #800000 50%, #d4af37 100%)",
            "linear-gradient(135deg, #800000 0%, #a52a2a 50%, #FFD700 100%)",
          ],
        }}
        transition={{
          duration: 10,
          repeat: Infinity,
          ease: "linear",
        }}
        className="absolute inset-0"
      />

      {/* Floating Particles */}
      <div className="absolute inset-0 overflow-hidden">
        {[...Array(20)].map((_, i) => (
          <motion.div
            key={i}
            className="absolute w-2 h-2 bg-[#FFD700] rounded-full opacity-30"
            initial={{
              x: Math.random() * window.innerWidth,
              y: Math.random() * window.innerHeight,
            }}
            animate={{
              y: [null, Math.random() * -500],
              opacity: [0.3, 0.6, 0.3],
            }}
            transition={{
              duration: Math.random() * 10 + 10,
              repeat: Infinity,
              ease: "linear",
            }}
          />
        ))}
      </div>

      {/* Decorative Batik Pattern */}
      <div
        className="absolute inset-0 opacity-10"
        style={{
          backgroundImage: `url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="50" cy="50" r="30" fill="none" stroke="%23000" strokeWidth="1"/><circle cx="50" cy="50" r="20" fill="none" stroke="%23000" strokeWidth="0.5"/><circle cx="50" cy="50" r="10" fill="none" stroke="%23000" strokeWidth="0.3"/></svg>')`,
          backgroundSize: "100px 100px",
        }}
      />

      {/* Main Content */}
      <motion.div
        initial={{ opacity: 0, y: 50 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 1, ease: "easeOut" }}
        className="relative z-10 text-center px-4 max-w-5xl mx-auto"
      >
        {/* Decorative Top Line */}
        <motion.div
          initial={{ width: 0 }}
          animate={{ width: "100%" }}
          transition={{ delay: 0.5, duration: 1.5 }}
          className="h-1 bg-gradient-to-r from-transparent via-[#FFD700] to-transparent mb-8 mx-auto max-w-md"
        />

        {/* Title with Gradient Text */}
        <motion.h1
          initial={{ opacity: 0, scale: 0.8 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ delay: 0.3, duration: 0.8 }}
          className="text-5xl md:text-7xl lg:text-8xl font-bold mb-6 tracking-tight"
          style={{
            background: "linear-gradient(to right, #FFD700, #ffed4e, #FFD700)",
            WebkitBackgroundClip: "text",
            WebkitTextFillColor: "transparent",
            backgroundClip: "text",
            textShadow: "0 0 40px rgba(255,215,0,0.3)",
          }}
        >
          Keberagaman Kota Palembang
        </motion.h1>

        {/* Subtitle with Typing Effect */}
        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.8, duration: 1 }}
          className="text-2xl md:text-3xl lg:text-4xl text-white/90 font-light mb-12"
          style={{ textShadow: "2px 2px 8px rgba(0,0,0,0.5)" }}
        >
          Mutiara di Tepi Sungai Musi
        </motion.p>

        {/* Description */}
        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 1.2, duration: 1 }}
          className="text-lg md:text-xl text-white/80 max-w-2xl mx-auto mb-12 leading-relaxed"
        >
          Jelajahi kekayaan budaya, kuliner, dan tradisi yang menjadikan Palembang sebagai salah satu kota bersejarah
          terpenting di Indonesia
        </motion.p>

        {/* CTA Button */}
        <motion.button
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 1.5, duration: 0.8 }}
          whileHover={{
            scale: 1.05,
            boxShadow: "0 0 40px rgba(255,215,0,0.5)",
          }}
          whileTap={{ scale: 0.95 }}
          className="group relative bg-gradient-to-r from-[#FFD700] to-[#d4af37] text-[#800000] font-bold text-lg px-10 py-4 rounded-full shadow-2xl overflow-hidden border-2 border-[#FFD700] hover:border-white transition-all duration-300"
        >
          <span className="relative z-10 flex items-center gap-2">
            ✨ Mulai Jelajahi
          </span>
          <motion.div
            className="absolute inset-0 bg-white"
            initial={{ x: "-100%" }}
            whileHover={{ x: "100%" }}
            transition={{ duration: 0.5 }}
            style={{ opacity: 0.3 }}
          />
        </motion.button>

        {/* Decorative Bottom Line */}
        <motion.div
          initial={{ width: 0 }}
          animate={{ width: "100%" }}
          transition={{ delay: 0.5, duration: 1.5 }}
          className="h-1 bg-gradient-to-r from-transparent via-[#FFD700] to-transparent mt-8 mx-auto max-w-md"
        />
      </motion.div>

      {/* Scroll Indicator */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1, y: [0, 10, 0] }}
        transition={{
          opacity: { delay: 2, duration: 1 },
          y: { duration: 2, repeat: Infinity, ease: "easeInOut" },
        }}
        className="absolute bottom-8 left-1/2 -translate-x-1/2 flex flex-col items-center gap-2 z-10"
      >
        <span className="text-white/70 text-sm font-medium">Geser ke kanan untuk melanjutkan</span>
        <ChevronDown className="w-6 h-6 text-[#FFD700]" />
      </motion.div>

      {/* Decorative Corner Elements */}
      <motion.div
        animate={{ rotate: 360 }}
        transition={{ duration: 20, repeat: Infinity, ease: "linear" }}
        className="absolute top-10 left-10 w-32 h-32 border-4 border-[#FFD700]/20 rounded-full"
      />
      <motion.div
        animate={{ rotate: -360 }}
        transition={{ duration: 25, repeat: Infinity, ease: "linear" }}
        className="absolute bottom-10 right-10 w-40 h-40 border-4 border-white/10 rounded-full"
      />
    </section>
  )
}
