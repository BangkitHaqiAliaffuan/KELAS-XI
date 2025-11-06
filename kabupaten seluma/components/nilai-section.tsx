"use client"

import { motion } from "framer-motion"
import { useInView } from "framer-motion"
import { useRef } from "react"
import { Users, Heart, Shield, Sparkles } from "lucide-react"

const nilai = [
  {
    title: "Kebersamaan & Gotong Royong",
    description:
      "Dari tradisi begawi dan sedekah laut, masyarakat Seluma menjunjung tinggi nilai tolong menolong dalam setiap aspek kehidupan.",
    icon: Users,
  },
  {
    title: "Keramahan & Keterbukaan",
    description:
      "Budaya berandai menyambut tamu mencerminkan sifat ramah dan terbuka masyarakat Serawai terhadap pengunjung.",
    icon: Heart,
  },
  {
    title: "Keberanian & Keteguhan",
    description:
      "Dari seni beregek dan simbol keris, nilai keberanian dan keteguhan jiwa tertanam dalam karakter masyarakat.",
    icon: Shield,
  },
  {
    title: "Religius & Toleran",
    description:
      "Harmoni tradisi adat dan nilai Islam menciptakan masyarakat yang religius namun tetap menghargai kearifan lokal.",
    icon: Sparkles,
  },
]

export function NilaiSection() {
  const ref = useRef(null)
  const isInView = useInView(ref, { once: true, margin: "-100px" })

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        delayChildren: 0.2,
        staggerChildren: 0.1,
      },
    },
  }

  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: {
      opacity: 1,
      y: 0,
      transition: { duration: 0.6 },
    },
  }

  return (
    <section ref={ref} className="relative py-24 px-4 md:px-8 lg:px-16 bg-gradient-to-br from-yellow-50 via-orange-50 to-amber-50 overflow-hidden">
      {/* Decorative Background */}
      <div className="absolute inset-0 opacity-20">
        <div className="absolute top-0 right-0 w-96 h-96 bg-yellow-300/30 rounded-full blur-3xl" />
        <div className="absolute bottom-0 left-0 w-96 h-96 bg-orange-300/30 rounded-full blur-3xl" />
      </div>
      
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={isInView ? { opacity: 1, y: 0 } : { opacity: 0, y: 20 }}
        transition={{ duration: 0.6 }}
        className="max-w-7xl mx-auto relative z-10"
      >
        <div className="text-center mb-16">
          <motion.span
            initial={{ opacity: 0, scale: 0.8 }}
            animate={isInView ? { opacity: 1, scale: 1 } : { opacity: 0, scale: 0.8 }}
            transition={{ duration: 0.5 }}
            className="inline-block px-4 py-2 bg-yellow-100 text-yellow-800 rounded-full text-sm font-semibold mb-4"
          >
            ✨ NILAI LUHUR
          </motion.span>
          <h2 className="text-5xl md:text-6xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-yellow-600 via-orange-600 to-amber-600 mb-4">
            Nilai yang Diwariskan
          </h2>
          <p className="text-gray-600 text-lg max-w-2xl mx-auto">
            Filosofi hidup yang menjadi pedoman dalam kehidupan bermasyarakat
          </p>
        </div>

        <motion.div
          variants={containerVariants}
          initial="hidden"
          animate={isInView ? "visible" : "hidden"}
          className="grid grid-cols-1 md:grid-cols-2 gap-8"
        >
          {nilai.map((item, index) => {
            const Icon = item.icon
            return (
              <motion.div
                key={index}
                variants={itemVariants}
                whileHover={{
                  y: -10,
                  transition: { duration: 0.3 },
                }}
                className="group relative"
              >
                {/* Glow effect */}
                <div className="absolute inset-0 bg-gradient-to-br from-yellow-400 to-orange-400 rounded-3xl blur opacity-20 group-hover:opacity-30 transition duration-300" />
                
                {/* Card content */}
                <div className="relative bg-white/90 backdrop-blur-sm rounded-3xl p-8 shadow-xl hover:shadow-2xl transition-all overflow-hidden">
                  {/* Top decorative corner */}
                  <div className="absolute -top-16 -right-16 w-32 h-32 bg-gradient-to-br from-yellow-400/20 to-orange-400/20 rounded-full blur-2xl" />
                  
                  {/* Icon container */}
                  <div className="relative mb-6">
                    <motion.div
                      whileHover={{ rotate: [0, -10, 10, -10, 0], scale: 1.1 }}
                      transition={{ duration: 0.5 }}
                      className="inline-flex items-center justify-center w-20 h-20 rounded-2xl bg-gradient-to-br from-yellow-500 to-orange-500 shadow-lg"
                    >
                      <Icon size={40} className="text-white" />
                    </motion.div>
                    
                    {/* Number badge */}
                    <div className="absolute -top-2 -right-2 w-8 h-8 bg-amber-600 text-white rounded-full flex items-center justify-center text-sm font-bold shadow-lg">
                      {index + 1}
                    </div>
                  </div>
                  
                  {/* Content */}
                  <div className="relative">
                    <h3 className="text-2xl font-bold text-gray-800 mb-4 group-hover:text-orange-600 transition-colors">
                      {item.title}
                    </h3>
                    <div className="w-16 h-1 bg-gradient-to-r from-yellow-500 to-orange-500 rounded-full mb-4" />
                    <p className="text-gray-700 leading-relaxed">{item.description}</p>
                  </div>
                  
                  {/* Bottom decorative element */}
                  <div className="absolute bottom-0 right-0 w-24 h-24 bg-gradient-to-tl from-yellow-100/50 to-transparent rounded-tl-full" />
                </div>
              </motion.div>
            )
          })}
        </motion.div>
      </motion.div>
    </section>
  )
}
