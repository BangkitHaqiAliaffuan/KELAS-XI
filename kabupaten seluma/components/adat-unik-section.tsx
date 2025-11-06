"use client"

import { motion } from "framer-motion"
import { useInView } from "framer-motion"
import { useRef } from "react"

const tradisi = [
  {
    name: "Berandai",
    description:
      "Upacara adat penyambutan tamu besar dengan prosesi khusus yang menunjukkan keramahan masyarakat Serawai",
  },
  {
    name: "Tabot",
    description: "Perayaan keagamaan Islam yang meriah dengan pengaruh India-Arab, menjadi identitas budaya Bengkulu",
  },
  {
    name: "Sedekah Laut",
    description:
      "Ritual nelayan sebelum melaut untuk keselamatan, menjaga tradisi dan spiritualitas masyarakat pesisir",
  },
  {
    name: "Begawi",
    description: "Tradisi gotong royong dalam acara hajatan yang memperkuat ikatan sosial dan kebersamaan",
  },
  {
    name: "Bahasa Serawai",
    description: "Dialek khas yang berbeda dengan Bengkulu lainnya dengan pengaruh kuat dari bahasa Melayu",
  },
]

export function AdatUnikSection() {
  const ref = useRef(null)
  const isInView = useInView(ref, { once: true, margin: "-100px" })

  const itemVariants = (index: number) => ({
    hidden: { opacity: 0, x: index % 2 === 0 ? -50 : 50 },
    visible: {
      opacity: 1,
      x: 0,
      transition: { duration: 0.6, delay: index * 0.15 },
    },
  })

  return (
    <section ref={ref} className="relative py-24 px-4 md:px-8 lg:px-16 bg-gradient-to-br from-green-50 via-emerald-50 to-teal-50 overflow-hidden">
      {/* Decorative Elements */}
      <div className="absolute top-0 left-0 w-full h-full overflow-hidden">
        <div className="absolute top-20 -left-20 w-72 h-72 bg-green-300/20 rounded-full blur-3xl" />
        <div className="absolute bottom-20 -right-20 w-96 h-96 bg-emerald-300/20 rounded-full blur-3xl" />
      </div>
      
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={isInView ? { opacity: 1, y: 0 } : { opacity: 0, y: 20 }}
        transition={{ duration: 0.6 }}
        className="max-w-5xl mx-auto relative z-10"
      >
        <div className="text-center mb-20">
          <motion.span
            initial={{ opacity: 0, scale: 0.8 }}
            animate={isInView ? { opacity: 1, scale: 1 } : { opacity: 0, scale: 0.8 }}
            transition={{ duration: 0.5 }}
            className="inline-block px-4 py-2 bg-green-100 text-green-700 rounded-full text-sm font-semibold mb-4"
          >
            🌿 TRADISI & ADAT
          </motion.span>
          <h2 className="text-5xl md:text-6xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-green-600 via-emerald-600 to-teal-600 mb-4">
            Tradisi yang Melekat
          </h2>
          <p className="text-gray-600 text-lg max-w-2xl mx-auto">
            Nilai-nilai luhur yang diwariskan dari generasi ke generasi
          </p>
        </div>

        {/* Timeline */}
        <div className="relative">
          {/* Center Line */}
          <div className="absolute left-1/2 transform -translate-x-1/2 w-1 h-full bg-gradient-to-b from-green-300 via-emerald-400 to-teal-300 rounded-full" />

          {/* Timeline Items */}
          <div className="space-y-16">
            {tradisi.map((item, index) => (
              <motion.div
                key={index}
                variants={itemVariants(index)}
                initial="hidden"
                animate={isInView ? "visible" : "hidden"}
                className={`relative flex ${index % 2 === 0 ? "md:flex-row" : "md:flex-row-reverse"}`}
              >
                {/* Content */}
                <div className={`w-full md:w-1/2 ${index % 2 === 0 ? "md:pr-16" : "md:pl-16"}`}>
                  <motion.div 
                    whileHover={{ scale: 1.02, y: -5 }}
                    className="bg-white/90 backdrop-blur-sm p-8 rounded-2xl shadow-xl hover:shadow-2xl transition-all relative group"
                  >
                    {/* Decorative gradient border */}
                    <div className="absolute inset-0 bg-gradient-to-br from-green-400 to-teal-400 rounded-2xl blur opacity-0 group-hover:opacity-20 transition duration-300" />
                    
                    <div className="relative">
                      <div className="flex items-center gap-3 mb-4">
                        <div className="w-12 h-1 bg-gradient-to-r from-green-500 to-teal-500 rounded-full" />
                        <span className="text-green-600 font-bold text-sm">TRADISI #{index + 1}</span>
                      </div>
                      <h3 className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-green-600 to-teal-600 mb-4">
                        {item.name}
                      </h3>
                      <p className="text-gray-700 leading-relaxed text-lg">{item.description}</p>
                    </div>
                  </motion.div>
                </div>

                {/* Timeline Point */}
                <motion.div 
                  whileHover={{ scale: 1.3, rotate: 360 }}
                  transition={{ duration: 0.5 }}
                  className="absolute left-1/2 transform -translate-x-1/2 top-8 md:top-12"
                >
                  <div className="relative">
                    <div className="absolute inset-0 bg-gradient-to-r from-green-500 to-teal-500 rounded-full blur-md opacity-50" />
                    <div className="relative w-16 h-16 bg-gradient-to-br from-green-500 to-teal-500 rounded-full border-4 border-white shadow-xl flex items-center justify-center">
                      <span className="text-white font-bold text-xl">{index + 1}</span>
                    </div>
                  </div>
                </motion.div>
              </motion.div>
            ))}
          </div>
          
          {/* End decoration */}
          <motion.div
            initial={{ scale: 0 }}
            animate={isInView ? { scale: 1 } : { scale: 0 }}
            transition={{ delay: 1, duration: 0.5 }}
            className="absolute left-1/2 transform -translate-x-1/2 -bottom-8"
          >
            <div className="w-8 h-8 bg-gradient-to-br from-green-500 to-teal-500 rounded-full border-4 border-white shadow-lg" />
          </motion.div>
        </div>
      </motion.div>
    </section>
  )
}
