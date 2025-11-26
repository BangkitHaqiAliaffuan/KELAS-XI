"use client"

import { useEffect, useState } from "react"
import { Play } from "lucide-react"

export default function HeroSection() {
  const [leaf1, setLeaf1] = useState({ x: 10, y: 20 })
  const [leaf2, setLeaf2] = useState({ x: 80, y: 60 })

  useEffect(() => {
    const interval = setInterval(() => {
      setLeaf1((prev) => ({
        x: (prev.x + 0.5) % 100,
        y: (prev.y + 0.3) % 100,
      }))
      setLeaf2((prev) => ({
        x: (prev.x - 0.3) % 100,
        y: (prev.y + 0.4) % 100,
      }))
    }, 50)
    return () => clearInterval(interval)
  }, [])

  return (
    <section className="relative w-full min-h-screen flex flex-col items-center justify-center py-20 overflow-hidden">
      {/* Animated gradient background */}
      <div className="absolute inset-0 gradient-bg opacity-90" />

      {/* Floating decorative elements */}
      <div
        className="absolute w-32 h-32 rounded-full bg-secondary-green/20 blur-3xl float"
        style={{ left: `${leaf1.x}%`, top: `${leaf1.y}%` }}
      />
      <div
        className="absolute w-40 h-40 rounded-full bg-accent-green/20 blur-3xl float"
        style={{
          left: `${leaf2.x}%`,
          top: `${leaf2.y}%`,
          animationDelay: "2s",
        }}
      />

      {/* Leaf icons */}
      <svg
        className="absolute w-16 h-16 text-white/30 animate-spin-slow"
        style={{ top: "20%", right: "10%", animationDuration: "20s" }}
        viewBox="0 0 24 24"
        fill="currentColor"
      >
        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2m0 18c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8m-5-9h10v2H7z" />
      </svg>

      <div className="relative z-10 text-center px-4 md:px-8 w-full max-w-5xl mx-auto">
        <h1 className="text-4xl md:text-6xl lg:text-7xl font-bold text-cream-bg mb-4 drop-shadow-lg text-balance">
          MEMBUAT TAMAN HIAS INSPIRATIF XI-RPL
        </h1>
        <div className="h-1 w-32 mx-auto mb-6 bg-gradient-to-r from-accent-green to-cream-bg rounded" />
        <p className="text-base md:text-lg lg:text-xl text-cream-bg/90 mb-12 max-w-3xl mx-auto leading-relaxed">
          Jelajahi perjalanan menakjubkan dari penanaman hingga pertumbuhan penuh
        </p>

        {/* Video placeholder with play button */}
        <div className="mx-auto max-w-3xl mb-8">
          <div className="relative group cursor-pointer">
            <div className="relative w-full aspect-video rounded-2xl shadow-2xl overflow-hidden">
              <div className="absolute inset-0 bg-gradient-to-br from-primary-green/20 to-cream-bg/20 backdrop-blur-sm" />
              <img src="/school-garden-video.jpg" alt="Garden video" className="w-full h-full object-cover" />

              {/* Play button overlay */}
              <button className="absolute inset-0 flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                <div className="w-20 h-20 md:w-24 md:h-24 rounded-full bg-bronze-accent shadow-lg flex items-center justify-center pulse-animation group-hover:shadow-2xl transition-shadow">
                  <Play className="w-8 h-8 md:w-10 md:h-10 text-white fill-white" />
                </div>
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Scroll indicator */}
      <div className="absolute bottom-8 left-1/2 -translate-x-1/2 z-10">
        <div className="flex flex-col items-center animate-bounce">
          <svg
            className="w-6 h-6 text-cream-bg"
            fill="none"
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth="2"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path d="M19 14l-7 7m0 0l-7-7m7 7V3" />
          </svg>
        </div>
      </div>
    </section>
  )
}
