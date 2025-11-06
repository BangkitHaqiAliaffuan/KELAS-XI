"use client"

import { motion } from "framer-motion"
import { useInView } from "framer-motion"
import { useRef } from "react"
import { Fish, TreePine, Wheat, Scissors, ShoppingCart, Apple } from "lucide-react"

const pekerjaan = [
  {
    name: "Nelayan Pantai",
    description: "Pesisir barat Seluma yang kaya hasil laut",
    icon: Fish,
  },
  {
    name: "Petani Sawit & Karet",
    description: "Perkebunan utama penggerak ekonomi",
    icon: TreePine,
  },
  {
    name: "Petani Padi",
    description: "Lahan sawah produktif menghasilkan pangan",
    icon: Wheat,
  },
  {
    name: "Pengrajin Songket",
    description: "Tenun tradisional warisan budaya",
    icon: Scissors,
  },
  {
    name: "Pedagang Ikan",
    description: "Pasar ikan segar pusat distribusi",
    icon: ShoppingCart,
  },
  {
    name: "Petani Durian",
    description: "Durian khas Bengkulu yang terkenal",
    icon: Apple,
  },
]

export function PekerjaanSection() {
  const ref = useRef(null)
  const isInView = useInView(ref, { once: true, margin: "-100px" })

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        delayChildren: 0.1,
        staggerChildren: 0.1,
      },
    },
  }

  const itemVariants = {
    hidden: { scale: 0, opacity: 0 },
    visible: {
      scale: 1,
      opacity: 1,
      transition: { duration: 0.5, ease: "easeOut" },
    },
  }

  return (
    <section ref={ref} className="relative py-24 px-4 md:px-8 lg:px-16 bg-gradient-to-br from-blue-50 via-cyan-50 to-teal-50 overflow-hidden">
      {/* Decorative Background */}
      <div className="absolute inset-0 opacity-20" style={{
        backgroundImage: `url("data:image/svg+xml,%3Csvg width='80' height='80' viewBox='0 0 80 80' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%230891b2' fill-opacity='0.15'%3E%3Cpath d='M50 50c0-5.523 4.477-10 10-10s10 4.477 10 10-4.477 10-10 10c0 5.523-4.477 10-10 10s-10-4.477-10-10 4.477-10 10-10zM10 10c0-5.523 4.477-10 10-10s10 4.477 10 10-4.477 10-10 10c0 5.523-4.477 10-10 10S0 25.523 0 20s4.477-10 10-10zm10 8c4.418 0 8-3.582 8-8s-3.582-8-8-8-8 3.582-8 8 3.582 8 8 8zm40 40c4.418 0 8-3.582 8-8s-3.582-8-8-8-8 3.582-8 8 3.582 8 8 8z' /%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
      }} />
      
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
            className="inline-block px-4 py-2 bg-blue-100 text-blue-700 rounded-full text-sm font-semibold mb-4"
          >
            💼 KEHIDUPAN EKONOMI
          </motion.span>
          <h2 className="text-5xl md:text-6xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 via-cyan-600 to-teal-600 mb-4">
            Dinamika Ekonomi Seluma
          </h2>
          <p className="text-gray-600 text-lg max-w-2xl mx-auto">
            Beragam profesi yang menjadi pilar perekonomian masyarakat
          </p>
        </div>

        <motion.div
          variants={containerVariants}
          initial="hidden"
          animate={isInView ? "visible" : "hidden"}
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8"
        >
          {pekerjaan.map((item, index) => {
            const Icon = item.icon
            return (
              <motion.div
                key={index}
                variants={itemVariants}
                whileHover={{
                  y: -10,
                  transition: { duration: 0.2 },
                }}
                className="group relative"
              >
                <div className="absolute inset-0 bg-gradient-to-br from-blue-400 to-teal-400 rounded-2xl blur opacity-25 group-hover:opacity-40 transition duration-300" />
                
                <div className="relative bg-white rounded-2xl p-8 shadow-lg hover:shadow-2xl transition-all">
                  <motion.div
                    className="w-20 h-20 mx-auto mb-6 rounded-2xl bg-gradient-to-br from-blue-500 to-teal-500 flex items-center justify-center shadow-lg"
                    whileHover={{ rotate: [0, -10, 10, -10, 0], scale: 1.1 }}
                    transition={{ duration: 0.5 }}
                  >
                    <Icon size={40} className="text-white" />
                  </motion.div>
                  
                  <div className="text-center">
                    <h3 className="text-xl font-bold text-gray-800 mb-3 group-hover:text-blue-600 transition-colors">
                      {item.name}
                    </h3>
                    <p className="text-gray-600 text-sm leading-relaxed">{item.description}</p>
                  </div>
                  
                  {/* Decorative corner */}
                  <div className="absolute top-0 right-0 w-16 h-16 bg-gradient-to-br from-blue-500/10 to-transparent rounded-tr-2xl rounded-bl-full" />
                </div>
              </motion.div>
            )
          })}
        </motion.div>
      </motion.div>
    </section>
  )
}
