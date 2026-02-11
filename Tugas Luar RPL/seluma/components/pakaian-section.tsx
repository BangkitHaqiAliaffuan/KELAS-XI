"use client"

import { motion } from "framer-motion"
import { useInView } from "framer-motion"
import { useRef } from "react"

const pakaian = [
  {
    name: "Pakaian Adat Serawai Perempuan",
    description:
      "Dominan warna merah hati melambangkan keberanian. Busana terdiri dari baju kurung merah, kain songket dengan motif khas, pending perak, dan sanggul malang yang elegan.",
    image: "/pakaian-adat-serawai-perempuan-merah-songket.jpg",
  },
  {
    name: "Pakaian Adat Serawai Laki-laki",
    description:
      "Teluk belango merah dengan hiasan benang emas, celana komprang hitam, destar/ikat kepala, dan keris sebagai simbol keberanian dan kehormatan.",
    image: "/pakaian-adat-serawai-laki-laki-teluk-belango.jpg",
  },
]

export function PakaianSection() {
  const ref = useRef(null)
  const isInView = useInView(ref, { once: true, margin: "-100px" })

  return (
    <section ref={ref} className="relative py-24 px-4 md:px-8 lg:px-16 bg-gradient-to-br from-red-50 via-rose-50 to-pink-50 overflow-hidden">
      {/* Decorative Background Pattern */}
      <div className="absolute inset-0 opacity-30" style={{
        backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23dc2626' fill-opacity='0.1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
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
            className="inline-block px-4 py-2 bg-red-100 text-red-700 rounded-full text-sm font-semibold mb-4"
          >
            👘 WARISAN BUDAYA
          </motion.span>
          <h2 className="text-5xl md:text-6xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-red-600 via-rose-600 to-pink-600 mb-4">
            Busana Tradisional Seluma
          </h2>
          <p className="text-gray-600 text-lg max-w-2xl mx-auto">
            Keanggunan dan keberanian dalam setiap helai kain tradisional Serawai
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-10">
          {pakaian.map((item, index) => (
            <motion.div
              key={index}
              initial={{ opacity: 0, x: index === 0 ? -50 : 50 }}
              animate={isInView ? { opacity: 1, x: 0 } : { opacity: 0, x: index === 0 ? -50 : 50 }}
              whileHover={{ y: -15, transition: { duration: 0.3 } }}
              transition={{ duration: 0.7, delay: index * 0.2 }}
              className="group relative bg-white rounded-3xl overflow-hidden shadow-2xl hover:shadow-red-200/50 transition-all"
            >
              {/* Decorative Corner */}
              <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-red-500/20 to-transparent rounded-bl-full z-10" />
              
              <div className="relative h-96 overflow-hidden">
                <div className="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent z-10" />
                <img
                  src={item.image || "/placeholder.svg"}
                  alt={item.name}
                  className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                />
                
                {/* Badge */}
                <div className="absolute top-6 left-6 z-20">
                  <span className="bg-red-600 text-white px-4 py-2 rounded-full text-xs font-bold shadow-lg">
                    {index === 0 ? "PEREMPUAN" : "LAKI-LAKI"}
                  </span>
                </div>
              </div>
              
              <div className="p-8 relative">
                <div className="absolute top-0 left-8 w-16 h-1 bg-gradient-to-r from-red-500 to-pink-500 transform -translate-y-4" />
                <h3 className="text-2xl font-bold text-gray-800 mb-4 group-hover:text-red-600 transition-colors">
                  {item.name}
                </h3>
                <p className="text-gray-700 leading-relaxed">{item.description}</p>
              </div>
            </motion.div>
          ))}
        </div>
      </motion.div>
    </section>
  )
}
