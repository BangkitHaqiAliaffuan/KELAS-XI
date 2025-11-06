"use client"

import { motion, useScroll, useMotionValueEvent } from "framer-motion"
import { Menu, X } from "lucide-react"
import { useState } from "react"
import Image from "next/image"

interface NavbarProps {
  currentPage: number
  totalPages: number
  onPageChange: (page: number) => void
  sections: { title: string; id: string }[]
}

export default function Navbar({ currentPage, totalPages, onPageChange, sections }: NavbarProps) {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const [isVisible, setIsVisible] = useState(true)
  const { scrollY } = useScroll()

  useMotionValueEvent(scrollY, "change", (latest) => {
    const previous = scrollY.getPrevious()
    if (latest > previous && latest > 150) {
      setIsVisible(false)
    } else {
      setIsVisible(true)
    }
  })

  return (
    <>
      <motion.nav
        initial={{ y: -100 }}
        animate={{ y: isVisible ? 0 : -100 }}
        transition={{ duration: 0.3 }}
        className="fixed top-0 left-0 right-0 z-[100] backdrop-blur-lg bg-[#800000]/90 border-b-2 border-gradient-gold shadow-lg"
        style={{
          borderImage: "linear-gradient(to right, #FFD700, #d4af37, #FFD700) 1",
        }}
      >
        <div className="container mx-auto px-4 lg:px-8">
          <div className="flex items-center justify-between h-16 lg:h-20">
            {/* Logo & Title */}
            <div className="flex items-center gap-3 lg:gap-4">
              <div className="w-10 h-10 lg:w-12 lg:h-12 rounded-full bg-gradient-to-br from-[#FFD700] to-[#d4af37] flex items-center justify-center shadow-lg">
                <span className="text-[#800000] font-bold text-lg lg:text-xl">P</span>
              </div>
              <div className="hidden sm:block">
                <h1 className="text-white font-bold text-base lg:text-xl tracking-wide mt-5">
                  Keberagaman Kota Palembang
                </h1>
             
              </div>
              
            </div>

            {/* Desktop Navigation Dots */}
            <div className="hidden lg:flex items-center gap-2">
              {Array.from({ length: totalPages }).map((_, index) => (
                <motion.button
                  key={index}
                  onClick={() => onPageChange(index)}
                  className={`transition-all duration-300 rounded-full ${
                    currentPage === index
                      ? "w-3 h-3 bg-[#FFD700] shadow-[0_0_10px_rgba(255,215,0,0.5)]"
                      : "w-2 h-2 bg-white/50 hover:bg-white/80"
                  }`}
                  whileHover={{ scale: 1.2 }}
                  whileTap={{ scale: 0.9 }}
                  aria-label={`Go to page ${index + 1}`}
                />
              ))}
            </div>

            {/* Page Counter & Mobile Menu */}
            <div className="flex items-center gap-4">
              <div className="hidden md:flex items-center gap-2 bg-white/10 backdrop-blur-sm px-4 py-2 rounded-full border border-[#FFD700]/30">
                <span className="text-[#FFD700] font-bold text-lg">{currentPage + 1}</span>
                <span className="text-white/70">/</span>
                <span className="text-white/70 text-sm">{totalPages}</span>
              </div>

              {/* Mobile Hamburger */}
              <button
                onClick={() => setIsMenuOpen(!isMenuOpen)}
                className="lg:hidden p-2 rounded-lg bg-white/10 backdrop-blur-sm border border-[#FFD700]/30 hover:bg-white/20 transition-colors"
                aria-label="Toggle menu"
              >
                {isMenuOpen ? <X className="w-6 h-6 text-white" /> : <Menu className="w-6 h-6 text-white" />}
              </button>
            </div>
          </div>
        </div>
      </motion.nav>

      {/* Mobile Menu Overlay */}
      <motion.div
        initial={{ opacity: 0, x: "100%" }}
        animate={{
          opacity: isMenuOpen ? 1 : 0,
          x: isMenuOpen ? 0 : "100%",
        }}
        transition={{ duration: 0.3 }}
        className="fixed inset-0 z-[90] lg:hidden"
        style={{ pointerEvents: isMenuOpen ? "auto" : "none" }}
      >
        <div className="absolute inset-0 bg-black/50 backdrop-blur-sm" onClick={() => setIsMenuOpen(false)} />
        <motion.div
          className="absolute right-0 top-0 bottom-0 w-80 max-w-[85vw] bg-[#800000] shadow-2xl overflow-y-auto"
          initial={{ x: "100%" }}
          animate={{ x: isMenuOpen ? 0 : "100%" }}
          transition={{ duration: 0.3, ease: "easeOut" }}
        >
          <div className="p-6 pt-24">
            <h2 className="text-[#FFD700] font-bold text-xl mb-6 border-b border-[#FFD700]/30 pb-3">
              Navigasi Halaman
            </h2>
            <div className="space-y-2">
              {sections.map((section, index) => (
                <motion.button
                  key={section.id}
                  onClick={() => {
                    onPageChange(index)
                    setIsMenuOpen(false)
                  }}
                  className={`w-full text-left p-4 rounded-lg transition-all duration-200 ${
                    currentPage === index
                      ? "bg-[#FFD700] text-[#800000] font-semibold shadow-lg"
                      : "bg-white/5 text-white hover:bg-white/10 border border-white/10"
                  }`}
                  whileHover={{ x: 5 }}
                  whileTap={{ scale: 0.98 }}
                >
                  <div className="flex items-center gap-3">
                    <span
                      className={`text-sm font-bold ${
                        currentPage === index ? "text-[#800000]" : "text-[#FFD700]"
                      }`}
                    >
                      {String(index + 1).padStart(2, "0")}
                    </span>
                    <span>{section.title}</span>
                  </div>
                </motion.button>
              ))}
            </div>
          </div>
        </motion.div>
      </motion.div>
    </>
  )
}
