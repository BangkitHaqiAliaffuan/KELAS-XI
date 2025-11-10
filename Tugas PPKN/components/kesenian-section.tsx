"use client"

import { cn } from "@/lib/utils"

import { motion } from "framer-motion"
import { useInView } from "react-intersection-observer"
import { useState, useEffect } from "react"
import { ChevronLeft, ChevronRight, Play, Pause } from "lucide-react"
import { Button } from "@/components/ui/button"

const slides = [
  {
    image: "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1d/Gadispalembang.jpg/800px-Gadispalembang.jpg",
    title: "Tari Gending Sriwijaya",
    description: "Tarian penyambutan tamu dengan gerakan anggun yang merepresentasikan keramahan masyarakat Palembang.",
  },
  {
    image: "/palembang-traditional-music-melayu.jpg",
    title: "Musik Melayu Palembang",
    description: "Alunan musik tradisional dengan alat musik seperti gambus, rebana, dan biola khas Melayu.",
  },
  {
    image: "/songket-weaving-traditional-art-palembang.jpg",
    title: "Seni Tenun Songket",
    description: "Kerajinan tenun kain songket dengan benang emas yang memerlukan ketelitian dan kesabaran tinggi.",
  },
]

export function KesenianSection() {
  const [ref, inView] = useInView({ triggerOnce: true, threshold: 0.1 })
  const [currentSlide, setCurrentSlide] = useState(0)
  const [isPlaying, setIsPlaying] = useState(true)

  useEffect(() => {
    if (!isPlaying) return

    const interval = setInterval(() => {
      setCurrentSlide((prev) => (prev + 1) % slides.length)
    }, 5000)

    return () => clearInterval(interval)
  }, [isPlaying])

  const nextSlide = () => {
    setCurrentSlide((prev) => (prev + 1) % slides.length)
  }

  const prevSlide = () => {
    setCurrentSlide((prev) => (prev - 1 + slides.length) % slides.length)
  }

  return (
    <section id="kesenian-daerah" className="py-20 lg:ml-64 bg-background songket-pattern">
      <div className="container mx-auto px-4">
        <motion.div
          ref={ref}
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-12"
        >
          <h2 className="font-serif text-4xl md:text-5xl font-bold text-primary mb-4">Kesenian Daerah</h2>
          <div className="w-24 h-1 bg-secondary mx-auto mb-6" />
          <p className="text-lg text-muted-foreground max-w-3xl mx-auto leading-relaxed">
            Representasi sejarah Sriwijaya melalui perpaduan seni Hindu-Buddha dan Islam yang harmonis.
          </p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8, delay: 0.2 }}
          className="relative max-w-5xl mx-auto"
        >
          <div className="relative h-96 md:h-[500px] rounded-xl overflow-hidden shadow-2xl">
            {slides.map((slide, index) => (
              <div
                key={index}
                className={cn(
                  "absolute inset-0 transition-opacity duration-1000",
                  index === currentSlide ? "opacity-100" : "opacity-0",
                )}
              >
                <img src={slide.image || "/placeholder.svg"} alt={slide.title} className="w-full h-full object-cover" />
                <div className="absolute inset-0 bg-gradient-to-t from-primary via-primary/50 to-transparent" />
                <div className="absolute bottom-0 left-0 right-0 p-8 text-primary-foreground">
                  <h3 className="font-serif text-3xl md:text-4xl font-bold text-secondary mb-3">{slide.title}</h3>
                  <p className="text-lg leading-relaxed max-w-2xl">{slide.description}</p>
                </div>
              </div>
            ))}

            {/* Navigation Arrows */}
            <Button
              onClick={prevSlide}
              size="icon"
              className="absolute left-4 top-1/2 -translate-y-1/2 bg-secondary/90 hover:bg-secondary text-secondary-foreground rounded-full shadow-lg"
            >
              <ChevronLeft className="h-6 w-6" />
            </Button>
            <Button
              onClick={nextSlide}
              size="icon"
              className="absolute right-4 top-1/2 -translate-y-1/2 bg-secondary/90 hover:bg-secondary text-secondary-foreground rounded-full shadow-lg"
            >
              <ChevronRight className="h-6 w-6" />
            </Button>

            {/* Play/Pause Button */}
            <Button
              onClick={() => setIsPlaying(!isPlaying)}
              size="icon"
              className="absolute top-4 right-4 bg-secondary/90 hover:bg-secondary text-secondary-foreground rounded-full shadow-lg"
            >
              {isPlaying ? <Pause className="h-5 w-5" /> : <Play className="h-5 w-5" />}
            </Button>
          </div>

          {/* Slide Indicators */}
          <div className="flex justify-center gap-3 mt-6">
            {slides.map((_, index) => (
              <button
                key={index}
                onClick={() => setCurrentSlide(index)}
                className={cn(
                  "h-3 rounded-full transition-all duration-300",
                  index === currentSlide ? "w-12 bg-secondary" : "w-3 bg-secondary/30 hover:bg-secondary/50",
                )}
              />
            ))}
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8, delay: 0.4 }}
          className="mt-12 bg-card rounded-xl p-8 shadow-lg border-2 border-secondary/20 max-w-4xl mx-auto"
        >
          <h3 className="font-serif text-2xl font-bold text-center text-secondary mb-4">Keunikan Seni Budaya</h3>
          <p className="text-muted-foreground text-center leading-relaxed">
            Kesenian Palembang merupakan cerminan dari sejarah panjang kerajaan Sriwijaya, menggabungkan elemen
            Hindu-Buddha dengan pengaruh Islam yang datang kemudian. Perpaduan ini menciptakan identitas seni yang unik
            dan khas.
          </p>
        </motion.div>
      </div>
    </section>
  )
}
