"use client"

import { useState, useEffect } from "react"
import { motion, AnimatePresence } from "framer-motion"
import LoadingScreen from "@/components/loading-screen"
import Navbar from "@/components/navbar"
import PageNavigation from "@/components/page-navigation"
import HeroSection from "@/components/sections/hero-section"
import MakananSection from "@/components/sections/makanan-section"
import PakaianSection from "@/components/sections/pakaian-section"
import KesenianSection from "@/components/sections/kesenian-section"
import PekerjaanSection from "@/components/sections/pekerjaan-section"
import AdatUnikSection from "@/components/sections/adat-unik-section"
import NilaiSection from "@/components/sections/nilai-section"
import KesimpulanSection from "@/components/sections/kesimpulan-section"
import ThankYouSection from "@/components/sections/thank-you-section"

const sections = [
  { id: "hero", title: "Beranda", component: HeroSection },
  { id: "makanan", title: "Kuliner Khas", component: MakananSection },
  { id: "pakaian", title: "Pakaian Adat", component: PakaianSection },
  { id: "kesenian", title: "Kesenian", component: KesenianSection },
  { id: "pekerjaan", title: "Mata Pencaharian", component: PekerjaanSection },
  { id: "adat-unik", title: "Adat Istiadat", component: AdatUnikSection },
  { id: "nilai", title: "Nilai-Nilai Luhur", component: NilaiSection },
  { id: "kesimpulan", title: "Kesimpulan", component: KesimpulanSection },
  { id: "thank-you", title: "Penutup", component: ThankYouSection },
]

export default function Home() {
  const [currentPage, setCurrentPage] = useState(0)
  const [direction, setDirection] = useState(0)
  const [isLoading, setIsLoading] = useState(true)

  const handlePageChange = (newPage: number) => {
    if (newPage >= 0 && newPage < sections.length) {
      setDirection(newPage > currentPage ? 1 : -1)
      setCurrentPage(newPage)
      // Smooth scroll to top when changing pages
      window.scrollTo({ top: 0, behavior: "smooth" })
    }
  }

  useEffect(() => {
    // Hide loading screen after delay
    const timer = setTimeout(() => {
      setIsLoading(false)
    }, 2500)

    return () => clearTimeout(timer)
  }, [])

  const CurrentSection = sections[currentPage].component

  // Page transition variants
  const pageVariants = {
    enter: (direction: number) => ({
      x: direction > 0 ? 1000 : -1000,
      opacity: 0,
    }),
    center: {
      x: 0,
      opacity: 1,
    },
    exit: (direction: number) => ({
      x: direction < 0 ? 1000 : -1000,
      opacity: 0,
    }),
  }

  const pageTransition = {
    type: "tween",
    ease: "anticipate",
    duration: 0.5,
  }

  return (
    <>
      {isLoading && <LoadingScreen />}

      {!isLoading && (
        <>
          <Navbar
            currentPage={currentPage}
            totalPages={sections.length}
            onPageChange={handlePageChange}
            sections={sections}
          />

          <PageNavigation
            currentPage={currentPage}
            totalPages={sections.length}
            onPageChange={handlePageChange}
            sections={sections}
          />

          <main className="overflow-hidden">
            <AnimatePresence initial={false} custom={direction} mode="wait">
              <motion.div
                key={currentPage}
                custom={direction}
                variants={pageVariants}
                initial="enter"
                animate="center"
                exit="exit"
                transition={pageTransition}
              >
                {currentPage === sections.length - 1 ? (
                  <CurrentSection onBackToStart={() => handlePageChange(0)} />
                ) : (
                  <CurrentSection />
                )}
              </motion.div>
            </AnimatePresence>
          </main>
        </>
      )}
    </>
  )
}
