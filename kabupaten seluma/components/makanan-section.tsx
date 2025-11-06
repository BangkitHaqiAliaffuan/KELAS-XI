"use client"

import { motion } from "framer-motion"
import { useInView } from "framer-motion"
import { useRef } from "react"

const foods = [
  {
    name: "Pendap",
    description: "Ikan laut segar dibumbui kunyit, kelapa parut, dibungkus daun dan dibakar",
    image: "/pendap-ikan-laut-tradisional-seluma.jpg",
  },
  {
    name: "Lemang Tapai",
    description: "Ketan dengan tapai dalam bambu, makanan khas musim panen",
    image: "/lemang-tapai-ketan-bambu.jpg",
  },
  {
    name: "Bagar Hiu",
    description: "Gulai ikan hiu dengan bumbu khas Bengkulu yang kaya rempah",
    image: "/bagar-hiu-gulai-ikan.jpg",
  },
  {
    name: "Gulai Ikan Patin",
    description: "Kuah kuning santan dengan ikan patin sungai",
    image: "/gulai-ikan-patin-santan.jpg",
  },
  {
    name: "Lempuk Durian",
    description: "Dodol durian khas Bengkulu yang manis legit",
    image: "/lempuk-durian-dodol.jpg",
  },
]

export function MakananSection() {
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
    hidden: { opacity: 0, y: 50 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.6, ease: "easeOut" } },
  }

  return (
    <section ref={ref} className="relative py-24 px-4 md:px-8 lg:px-16 bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50 overflow-hidden">
      {/* Decorative Background Elements */}
      <div className="absolute top-0 right-0 w-96 h-96 bg-orange-200/30 rounded-full blur-3xl -z-10" />
      <div className="absolute bottom-0 left-0 w-96 h-96 bg-amber-200/30 rounded-full blur-3xl -z-10" />
      
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={isInView ? { opacity: 1, y: 0 } : { opacity: 0, y: 20 }}
        transition={{ duration: 0.6 }}
        className="max-w-7xl mx-auto"
      >
        <div className="text-center mb-16">
          <motion.span
            initial={{ opacity: 0, scale: 0.8 }}
            animate={isInView ? { opacity: 1, scale: 1 } : { opacity: 0, scale: 0.8 }}
            transition={{ duration: 0.5 }}
            className="inline-block px-4 py-2 bg-orange-100 text-orange-700 rounded-full text-sm font-semibold mb-4"
          >
            🍽️ CITA RASA NUSANTARA
          </motion.span>
          <h2 className="text-5xl md:text-6xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-orange-600 via-amber-600 to-yellow-600 mb-4">
            Kuliner Khas Seluma
          </h2>
          <p className="text-gray-600 text-lg max-w-2xl mx-auto">
            Nikmati kelezatan masakan tradisional yang kaya akan rempah dan warisan budaya
          </p>
        </div>

        <motion.div
          variants={containerVariants}
          initial="hidden"
          animate={isInView ? "visible" : "hidden"}
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-6"
        >
          {foods.map((food, index) => (
            <motion.div
              key={index}
              variants={itemVariants}
              whileHover={{ y: -10, transition: { duration: 0.3 } }}
              className="group relative bg-white rounded-2xl overflow-hidden shadow-xl hover:shadow-2xl transition-all"
            >
              <div className="absolute inset-0 bg-gradient-to-br from-orange-500/0 to-amber-500/0 group-hover:from-orange-500/10 group-hover:to-amber-500/10 transition-all duration-300 z-10" />
              
              <div className="relative h-48 overflow-hidden">
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-black/0 to-black/0 z-10" />
                <img
                  src={food.image || "/placeholder.svg"}
                  alt={food.name}
                  className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                />
                <div className="absolute top-3 right-3 bg-white/90 backdrop-blur-sm px-3 py-1 rounded-full z-20">
                  <span className="text-xs font-bold text-orange-600">#{index + 1}</span>
                </div>
              </div>
              
              <div className="p-5 relative z-10">
                <h3 className="text-xl font-bold text-gray-800 mb-3 group-hover:text-orange-600 transition-colors">
                  {food.name}
                </h3>
                <p className="text-sm text-gray-600 leading-relaxed">{food.description}</p>
              </div>
            </motion.div>
          ))}
        </motion.div>
      </motion.div>
    </section>
  )
}
