"use client"

import { motion } from "framer-motion"
import { useEffect, useState } from "react"

export function HeroSection() {
  const [scrollY, setScrollY] = useState(0)

  useEffect(() => {
    const handleScroll = () => setScrollY(window.scrollY)
    window.addEventListener("scroll", handleScroll)
    return () => window.removeEventListener("scroll", handleScroll)
  }, [])

  return (
    <section className="relative min-h-screen flex items-center justify-center overflow-hidden">
      {/* Animated Background Gradient */}
      <div className="absolute inset-0 bg-gradient-to-br from-emerald-700 via-teal-600 to-sky-700 -z-20" />
      
      {/* Animated Overlay Pattern */}
      <div 
        className="absolute inset-0 -z-10 opacity-20"
        style={{
          backgroundImage: `radial-gradient(circle at 50% 50%, rgba(255,255,255,0.2) 1px, transparent 1px)`,
          backgroundSize: '50px 50px',
          transform: `translateY(${scrollY * 0.5}px)`
        }}
      />

      {/* Floating Decorative Elements */}
      <motion.div
        animate={{
          y: [0, -30, 0],
          rotate: [0, 5, 0],
        }}
        transition={{
          duration: 6,
          repeat: Infinity,
          ease: "easeInOut"
        }}
        className="absolute top-20 left-10 w-32 h-32 bg-white/10 rounded-full blur-3xl -z-10"
      />
      
      <motion.div
        animate={{
          y: [0, 30, 0],
          rotate: [0, -5, 0],
        }}
        transition={{
          duration: 8,
          repeat: Infinity,
          ease: "easeInOut"
        }}
        className="absolute bottom-20 right-10 w-40 h-40 bg-emerald-400/20 rounded-full blur-3xl -z-10"
      />

      {/* Decorative Waves Pattern */}
      <div className="absolute inset-0 opacity-10 -z-10">
        <svg className="w-full h-full" viewBox="0 0 1200 120" preserveAspectRatio="none">
          <path d="M0,50 Q300,0 600,50 T1200,50 L1200,120 L0,120 Z" fill="white" />
        </svg>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 1, ease: "easeOut" }}
        className="text-center px-4 z-10 max-w-6xl mx-auto"
      >
        <motion.div
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ duration: 0.8, delay: 0.2 }}
          className="mb-8"
        >
          <span className="inline-block px-6 py-2 bg-white/20 backdrop-blur-sm rounded-full text-white/90 font-semibold text-sm tracking-wider border border-white/30">
            JELAJAHI BUDAYA NUSANTARA
          </span>
        </motion.div>

        <motion.h1 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.4 }}
          className="text-6xl md:text-8xl font-bold text-white mb-6 tracking-tight leading-tight"
          style={{
            textShadow: '0 4px 20px rgba(0,0,0,0.3), 0 0 40px rgba(255,255,255,0.1)'
          }}
        >
          Keberagaman
          <br />
          <span className="text-transparent bg-clip-text bg-gradient-to-r from-yellow-300 via-amber-200 to-orange-300">
            Kabupaten Seluma
          </span>
        </motion.h1>
        
        <motion.p 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.6 }}
          className="text-2xl md:text-3xl text-white/90 font-light tracking-wide"
          style={{
            textShadow: '0 2px 10px rgba(0,0,0,0.2)'
          }}
        >
          Pesona Pantai dan Budaya Serawai Bengkulu
        </motion.p>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.8 }}
          className="mt-12 flex gap-4 justify-center flex-wrap"
        >
          <button className="px-8 py-4 bg-white text-emerald-700 rounded-full font-bold hover:bg-emerald-50 transition-all shadow-lg hover:shadow-xl hover:scale-105 transform">
            Mulai Jelajah
          </button>
          
        </motion.div>
      </motion.div>

      {/* Scroll Indicator */}
      <motion.div
        animate={{ y: [0, 10, 0] }}
        transition={{ duration: 2, repeat: Infinity }}
        className="absolute bottom-10 left-1/2 transform -translate-x-1/2"
      >
        <div className="w-6 h-10 border-2 border-white/50 rounded-full flex items-start justify-center p-2">
          <motion.div
            animate={{ y: [0, 12, 0] }}
            transition={{ duration: 2, repeat: Infinity }}
            className="w-1.5 h-1.5 bg-white rounded-full"
          />
        </div>
      </motion.div>
    </section>
  )
}
