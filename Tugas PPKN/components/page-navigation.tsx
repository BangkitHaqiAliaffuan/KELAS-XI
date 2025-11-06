"use client"

import { motion, AnimatePresence } from "framer-motion"
import { ChevronLeft, ChevronRight } from "lucide-react"
import { useEffect, useCallback } from "react"

interface PageNavigationProps {
  currentPage: number
  totalPages: number
  onPageChange: (page: number) => void
  sections: { title: string; id: string }[]
}

export default function PageNavigation({ currentPage, totalPages, onPageChange, sections }: PageNavigationProps) {
  const handlePrevPage = useCallback(() => {
    if (currentPage > 0) {
      onPageChange(currentPage - 1)
    }
  }, [currentPage, onPageChange])

  const handleNextPage = useCallback(() => {
    if (currentPage < totalPages - 1) {
      onPageChange(currentPage + 1)
    }
  }, [currentPage, totalPages, onPageChange])

  // Keyboard navigation
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "ArrowRight" || e.key === " ") {
        e.preventDefault()
        handleNextPage()
      } else if (e.key === "ArrowLeft") {
        e.preventDefault()
        handlePrevPage()
      } else if (e.key === "Escape") {
        onPageChange(0) // Back to home
      }
    }

    window.addEventListener("keydown", handleKeyDown)
    return () => window.removeEventListener("keydown", handleKeyDown)
  }, [handleNextPage, handlePrevPage, onPageChange])

  return (
    <>
      {/* Left Arrow */}
      <AnimatePresence>
        {currentPage > 0 && (
          <motion.button
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            onClick={handlePrevPage}
            className="fixed left-4 lg:left-8 top-1/2 -translate-y-1/2 z-40 w-12 h-12 lg:w-14 lg:h-14 rounded-full backdrop-blur-md bg-white/10 border-2 border-[#FFD700]/50 flex items-center justify-center transition-all duration-300 hover:scale-110 hover:bg-white/20 hover:shadow-[0_0_20px_rgba(255,215,0,0.4)] group"
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.95 }}
            aria-label="Previous page"
          >
            <ChevronLeft className="w-6 h-6 lg:w-7 lg:h-7 text-[#FFD700] group-hover:text-white transition-colors" />
          </motion.button>
        )}
      </AnimatePresence>

      {/* Right Arrow */}
      <AnimatePresence>
        {currentPage < totalPages - 1 && (
          <motion.button
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: 20 }}
            onClick={handleNextPage}
            className="fixed right-4 lg:right-8 top-1/2 -translate-y-1/2 z-40 w-12 h-12 lg:w-14 lg:h-14 rounded-full backdrop-blur-md bg-white/10 border-2 border-[#FFD700]/50 flex items-center justify-center transition-all duration-300 hover:scale-110 hover:bg-white/20 hover:shadow-[0_0_20px_rgba(255,215,0,0.4)] group"
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.95 }}
            aria-label="Next page"
          >
            <ChevronRight className="w-6 h-6 lg:w-7 lg:h-7 text-[#FFD700] group-hover:text-white transition-colors" />
          </motion.button>
        )}
      </AnimatePresence>

      {/* Page Counter - Bottom Right */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="fixed bottom-6 right-6 lg:bottom-8 lg:right-8 z-40 backdrop-blur-md bg-[#800000]/80 border-2 border-[#FFD700]/50 rounded-2xl px-5 py-3 shadow-lg"
      >
        <div className="flex items-center gap-2">
          <span className="text-[#FFD700] font-bold text-2xl lg:text-3xl">{currentPage + 1}</span>
          <span className="text-white/50 text-lg">/</span>
          <span className="text-white/70 text-lg lg:text-xl">{totalPages}</span>
        </div>
      </motion.div>

      {/* Dot Navigation - Bottom Center */}
      <div className="fixed bottom-6 left-1/2 -translate-x-1/2 z-40 backdrop-blur-md bg-white/10 border border-[#FFD700]/30 rounded-full px-6 py-3 shadow-lg">
        <div className="flex items-center gap-3">
          {sections.map((section, index) => (
            <motion.button
              key={section.id}
              onClick={() => onPageChange(index)}
              className={`relative transition-all duration-300 rounded-full ${
                currentPage === index
                  ? "w-3 h-3 lg:w-4 lg:h-4 bg-[#FFD700] shadow-[0_0_15px_rgba(255,215,0,0.6)]"
                  : "w-2 h-2 lg:w-2.5 lg:h-2.5 bg-white/40 hover:bg-white/70"
              }`}
              whileHover={{ scale: 1.3 }}
              whileTap={{ scale: 0.9 }}
              aria-label={`${section.title} - Page ${index + 1}`}
              title={section.title}
            >
              {/* Tooltip */}
              <motion.div
                initial={{ opacity: 0, y: 10 }}
                whileHover={{ opacity: 1, y: 0 }}
                className="absolute bottom-full left-1/2 -translate-x-1/2 mb-2 px-3 py-1.5 bg-[#800000] text-white text-xs rounded-lg whitespace-nowrap pointer-events-none border border-[#FFD700]/30 shadow-lg"
              >
                {section.title}
                <div className="absolute top-full left-1/2 -translate-x-1/2 -mt-1 border-4 border-transparent border-t-[#800000]" />
              </motion.div>
            </motion.button>
          ))}
        </div>
      </div>
    </>
  )
}
