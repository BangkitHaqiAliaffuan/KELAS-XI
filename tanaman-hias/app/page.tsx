"use client"

import { useState } from "react"
import HeroSection from "@/components/hero-section"
import PlantsSection from "@/components/plants-section"
import TimelineSection from "@/components/timeline-section"
import PhotoGallery from "@/components/photo-gallery"
import Footer from "@/components/footer"
import ScrollProgress from "@/components/scroll-progress"
import ScrollToTop from "@/components/scroll-to-top"
import LoadingScreen from "@/components/loading-screen"

export default function Home() {
  const [isLoading, setIsLoading] = useState(true)

  return (
    <>
      <LoadingScreen onComplete={() => setIsLoading(false)} />
      <main className="w-full overflow-hidden">
        <ScrollProgress />
        <HeroSection />
        <PlantsSection />
        <TimelineSection />
        <PhotoGallery />
        <Footer />
        <ScrollToTop />
      </main>
    </>
  )
}
