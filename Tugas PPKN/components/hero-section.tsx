"use client"

import { motion } from "framer-motion"
import { ChevronDown } from "lucide-react"
import { Button } from "@/components/ui/button"

export function HeroSection() {
  const scrollToContent = () => {
    document.getElementById("makanan-khas")?.scrollIntoView({ behavior: "smooth" })
  }

  return (
    <section id="home" className="relative h-screen flex items-center justify-center overflow-hidden">
      {/* Background Image with Parallax */}
      <div
        className="absolute inset-0 z-0"
        style={{
          backgroundImage:
            "url(https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Ampera_Bridge_Palembang_Indonesia.jpg/1280px-Ampera_Bridge_Palembang_Indonesia.jpg)",
          backgroundSize: "cover",
          backgroundPosition: "center",
          backgroundAttachment: "fixed",
        }}
      >
        {/* Maroon Gradient Overlay */}
        <div className="absolute inset-0 bg-gradient-to-b from-primary/80 via-primary/70 to-primary/90" />
      </div>

      {/* Content */}
      <motion.div
        className="relative z-10 text-center px-4 max-w-5xl mx-auto"
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 1, ease: "easeOut" }}
      >
        <motion.h1
          className="font-serif text-4xl md:text-6xl lg:text-7xl font-bold text-secondary mb-6 leading-tight text-balance"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 1, delay: 0.3 }}
        >
          Keberagaman Budaya Palembang: Harmoni dalam Keragaman Indonesia
        </motion.h1>

        <motion.p
          className="text-lg md:text-xl text-primary-foreground mb-10 leading-relaxed max-w-3xl mx-auto text-pretty"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 1, delay: 0.6 }}
        >
          Jelajahi kekayaan makanan, pakaian, kesenian, pekerjaan, adat, dan nilai budaya Palembang sebagai contoh
          keberagaman Indonesia (bukan daerah asal saya)
        </motion.p>

        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.8, delay: 0.9 }}
        >
          <Button
            size="lg"
            onClick={scrollToContent}
            className="bg-secondary text-secondary-foreground hover:bg-secondary/90 text-lg px-8 py-6 rounded-full shadow-lg hover:shadow-2xl hover:shadow-secondary/50 transition-all duration-300 hover:scale-105 font-medium"
          >
            Mulai Jelajahi
            <ChevronDown className="ml-2 h-5 w-5 animate-bounce" />
          </Button>
        </motion.div>
      </motion.div>

      {/* Scroll Indicator */}
      <motion.div
        className="absolute bottom-8 left-1/2 transform -translate-x-1/2"
        animate={{ y: [0, 10, 0] }}
        transition={{ duration: 2, repeat: Number.POSITIVE_INFINITY }}
      >
        <ChevronDown className="h-8 w-8 text-secondary opacity-70" />
      </motion.div>
    </section>
  )
}
