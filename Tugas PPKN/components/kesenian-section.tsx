"use client"

import { motion, AnimatePresence } from "framer-motion"
import { useState } from "react"
import { ChevronLeft, ChevronRight } from "lucide-react"
import Image from "next/image"

const kesenian = [
  {
    name: "Tari Ronggeng Bugis",
    desc: "Tarian pergaulan masyarakat Karawang yang dinamis dan meriah. Gerakan yang energik mencerminkan kegembiraan dan solidaritas komunitas dalam setiap perayaan.",
    image: "/images/kesenian/tari-ronggeng.jpg",
  },
  {
    name: "Wayang Golek",
    desc: "Seni pertunjukan khas Sunda dengan boneka kayu yang bercerita. Warisan budaya yang masih dilestarikan sebagai media hiburan dan pendidikan nilai-nilai moral.",
    image: "/images/kesenian/wayang-golek.jpg",
  },
  {
    name: "Pencak Silat Cimande",
    desc: "Seni bela diri tradisional yang berasal dari daerah Karawang. Penggabungan sempurna antara fisik, spiritual, dan nilai-nilai kehidupan.",
    image: "/images/kesenian/pencak-silat.jpg",
  },
  {
    name: "Dogdog Lojor",
    desc: "Kesenian musik tradisional dengan menggunakan alat musik dogdog (gendang). Suara yang merdu menjadi bagian penting dalam upacara adat Karawang.",
    image: "/images/kesenian/dogdog-lojor.jpg",
  },
  {
    name: "Rengkong",
    desc: "Seni musik bambu tradisional yang menghasilkan nada-nada unik. Instrumen sederhana namun mampu menciptakan harmoni yang indah.",
    image: "/images/kesenian/rengkong.jpg",
  },
]

export function KesenianSection() {
  const [activeSlide, setActiveSlide] = useState(0)

  const nextSlide = () => {
    setActiveSlide((prev) => (prev + 1) % kesenian.length)
  }

  const prevSlide = () => {
    setActiveSlide((prev) => (prev - 1 + kesenian.length) % kesenian.length)
  }

  return (
    <section className="py-20 px-4 md:px-8" style={{ backgroundColor: "#f3f4f6" }}>
      <div className="max-w-4xl mx-auto">
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true, amount: 0.3 }}
        >
          <h2 className="text-4xl md:text-5xl font-bold mb-4" style={{ color: "#047857" }}>
            Warisan Seni Budaya
          </h2>
          <div className="h-1 w-20 mb-12" style={{ backgroundColor: "#fbbf24" }} />
        </motion.div>

        <div className="bg-white rounded-xl shadow-xl overflow-hidden">
          <AnimatePresence mode="wait">
            <motion.div
              key={activeSlide}
              initial={{ x: 300, opacity: 0 }}
              animate={{ x: 0, opacity: 1 }}
              exit={{ x: -300, opacity: 0 }}
              transition={{ duration: 0.5 }}
            >
              <div className="grid grid-cols-1 md:grid-cols-2">
                <div className="relative h-64 md:h-80 bg-gradient-to-br from-emerald-100 to-blue-100">
                  <Image
                    src={kesenian[activeSlide].image || "/placeholder.svg"}
                    alt={kesenian[activeSlide].name}
                    width={400}
                    height={320}
                    className="w-full h-full object-cover"
                    priority
                  />
                </div>
                <div className="p-8 flex flex-col justify-center">
                  <h3 className="text-3xl font-bold mb-4" style={{ color: "#047857" }}>
                    {kesenian[activeSlide].name}
                  </h3>
                  <p className="text-gray-700 text-lg leading-relaxed mb-6">{kesenian[activeSlide].desc}</p>
                  <div className="flex items-center gap-2 text-sm" style={{ color: "#047857" }}>
                    <div className="w-2 h-2 rounded-full" style={{ backgroundColor: "#047857" }} />
                    Bagian dari warisan budaya Karawang
                  </div>
                </div>
              </div>
            </motion.div>
          </AnimatePresence>

          {/* Controls */}
          <div className="p-6 bg-gray-50 flex items-center justify-between">
            <button onClick={prevSlide} className="p-2 rounded-full hover:bg-gray-200 transition-colors">
              <ChevronLeft size={24} style={{ color: "#047857" }} />
            </button>

            <div className="flex gap-2">
              {kesenian.map((_, index) => (
                <motion.button
                  key={index}
                  onClick={() => setActiveSlide(index)}
                  className="w-3 h-3 rounded-full transition-all"
                  style={{
                    backgroundColor: index === activeSlide ? "#047857" : "#d1d5db",
                  }}
                  whileHover={{ scale: 1.2 }}
                />
              ))}
            </div>

            <button onClick={nextSlide} className="p-2 rounded-full hover:bg-gray-200 transition-colors">
              <ChevronRight size={24} style={{ color: "#047857" }} />
            </button>
          </div>
        </div>

        <div className="text-center mt-8">
          <p className="text-gray-600">
            {activeSlide + 1} / {kesenian.length}
          </p>
        </div>
      </div>
    </section>
  )
}
