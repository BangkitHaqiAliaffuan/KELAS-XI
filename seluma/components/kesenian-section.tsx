"use client"

import { motion, AnimatePresence } from "framer-motion"
import { useInView } from "framer-motion"
import { useRef, useState } from "react"
import { ChevronLeft, ChevronRight } from "lucide-react"

const kesenian = [
  {
    name: "Tari Andun",
    description:
      "Tarian penyambutan tamu dengan gerakan lembut dan penuh keanggunan yang menampilkan keramahan budaya Serawai.",
    image: "/tari-andun-tarian-tradisional-serawai.jpg",
  },
  {
    name: "Dol",
    description:
      "Alat musik tradisional sejenis gendang besar yang dimainkan dalam upacara adat dan hiburan masyarakat.",
    image: "/dol-alat-musik-gendang-tradisional.jpg",
  },
  {
    name: "Seni Beregek",
    description:
      "Seni pencak silat khas Serawai yang menggabungkan unsur bela diri dan seni pertunjukan dengan gerakan dinamis.",
    image: "/seni-beregek-pencak-silat-tradisional.jpg",
  },
  {
    name: "Tabut",
    description:
      "Tradisi kesenian Islam warisan Bengkulu yang meriah, menampilkan perpaduan budaya Islam dan lokal dalam perayaan.",
    image: "/tabut-tradisi-perayaan-islam-bengkulu.jpg",
  },
  {
    name: "Kain Songket Serawai",
    description:
      "Tenun tradisional dengan motif khas Seluma yang menggunakan benang emas, simbol kekayaan budaya Serawai.",
    image: "/kain-songket-serawai-tenun-motif-emas.jpg",
  },
]

export function KesenianSection() {
  const ref = useRef(null)
  const isInView = useInView(ref, { once: true, margin: "-100px" })
  const [activeSlide, setActiveSlide] = useState(0)

  const nextSlide = () => {
    setActiveSlide((prev) => (prev + 1) % kesenian.length)
  }

  const prevSlide = () => {
    setActiveSlide((prev) => (prev - 1 + kesenian.length) % kesenian.length)
  }

  const slideVariants = {
    enter: (direction: number) => ({
      x: direction > 0 ? 300 : -300,
      opacity: 0,
    }),
    center: {
      zIndex: 1,
      x: 0,
      opacity: 1,
    },
    exit: (direction: number) => ({
      zIndex: 0,
      x: direction < 0 ? 300 : -300,
      opacity: 0,
    }),
  }

  return (
    <section ref={ref} className="relative py-24 px-4 md:px-8 lg:px-16 bg-gradient-to-br from-purple-50 via-violet-50 to-indigo-50 overflow-hidden">
      {/* Decorative Elements */}
      <div className="absolute top-20 left-10 w-64 h-64 bg-purple-300/20 rounded-full blur-3xl" />
      <div className="absolute bottom-20 right-10 w-80 h-80 bg-indigo-300/20 rounded-full blur-3xl" />
      
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={isInView ? { opacity: 1, y: 0 } : { opacity: 0, y: 20 }}
        transition={{ duration: 0.6 }}
        className="max-w-6xl mx-auto relative z-10"
      >
        <div className="text-center mb-16">
          <motion.span
            initial={{ opacity: 0, scale: 0.8 }}
            animate={isInView ? { opacity: 1, scale: 1 } : { opacity: 0, scale: 0.8 }}
            transition={{ duration: 0.5 }}
            className="inline-block px-4 py-2 bg-purple-100 text-purple-700 rounded-full text-sm font-semibold mb-4"
          >
            🎭 SENI & BUDAYA
          </motion.span>
          <h2 className="text-5xl md:text-6xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-purple-600 via-violet-600 to-indigo-600 mb-4">
            Warisan Seni Budaya
          </h2>
          <p className="text-gray-600 text-lg max-w-2xl mx-auto">
            Keanggunan dalam setiap gerakan, irama dalam setiap nada
          </p>
        </div>

        {/* Carousel */}
        <div className="relative bg-white/80 backdrop-blur-sm rounded-3xl shadow-2xl overflow-hidden">
          <AnimatePresence initial={false} custom={activeSlide}>
            <motion.div
              key={activeSlide}
              custom={activeSlide}
              variants={slideVariants}
              initial="enter"
              animate="center"
              exit="exit"
              transition={{ duration: 0.5, ease: "easeOut" }}
              className="relative"
            >
              <div className="grid grid-cols-1 md:grid-cols-2 gap-8 p-10">
                <div className="flex items-center justify-center">
                  <div className="relative group">
                    <div className="absolute -inset-2 bg-gradient-to-r from-purple-600 to-indigo-600 rounded-2xl blur opacity-25 group-hover:opacity-50 transition duration-300" />
                    <img
                      src={kesenian[activeSlide].image || "/placeholder.svg"}
                      alt={kesenian[activeSlide].name}
                      className="relative rounded-2xl w-full h-80 object-cover shadow-xl"
                    />
                  </div>
                </div>
                <div className="flex flex-col justify-center">
                  <div className="mb-4">
                    <span className="inline-block px-4 py-1 bg-purple-100 text-purple-700 rounded-full text-xs font-bold">
                      {activeSlide + 1} / {kesenian.length}
                    </span>
                  </div>
                  <h3 className="text-4xl font-bold bg-gradient-to-r from-purple-600 to-indigo-600 bg-clip-text text-transparent mb-6">
                    {kesenian[activeSlide].name}
                  </h3>
                  <p className="text-lg text-gray-700 leading-relaxed mb-6">{kesenian[activeSlide].description}</p>
                  
                  {/* Small Gallery Preview */}
                  <div className="flex gap-2 mt-4">
                    {kesenian.map((item, idx) => (
                      <button
                        key={idx}
                        onClick={() => setActiveSlide(idx)}
                        className={`w-16 h-16 rounded-lg overflow-hidden transition-all ${
                          idx === activeSlide ? "ring-4 ring-purple-600 scale-110" : "opacity-50 hover:opacity-100"
                        }`}
                      >
                        <img src={item.image || "/placeholder.svg"} alt={item.name} className="w-full h-full object-cover" />
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            </motion.div>
          </AnimatePresence>

          {/* Controls */}
          <div className="absolute inset-0 flex items-center justify-between px-4 pointer-events-none">
            <button
              onClick={prevSlide}
              className="pointer-events-auto p-3 bg-purple-600 text-white rounded-full hover:bg-purple-700 transition-all shadow-lg hover:scale-110"
            >
              <ChevronLeft size={24} />
            </button>
            <button
              onClick={nextSlide}
              className="pointer-events-auto p-3 bg-purple-600 text-white rounded-full hover:bg-purple-700 transition-all shadow-lg hover:scale-110"
            >
              <ChevronRight size={24} />
            </button>
          </div>

          {/* Dots Indicator */}
          <div className="flex justify-center gap-2 py-6 bg-gradient-to-r from-purple-50 to-indigo-50">
            {kesenian.map((_, index) => (
              <button
                key={index}
                onClick={() => setActiveSlide(index)}
                className={`h-2 rounded-full transition-all ${
                  index === activeSlide ? "w-12 bg-gradient-to-r from-purple-600 to-indigo-600" : "w-2 bg-gray-400 hover:bg-purple-400"
                }`}
              />
            ))}
          </div>
        </div>
      </motion.div>
    </section>
  )
}
