"use client"

import { motion } from "framer-motion"
import { Zap, Users, Sparkles, TrendingUp } from "lucide-react"

const values = [
  {
    name: "Kerja Keras & Etos Tinggi",
    desc: "Budaya kerja yang kuat berasal dari tradisi agraris sawah padi dan dinamika industri modern yang menggerakkan ekonomi Karawang.",
    Icon: Zap,
  },
  {
    name: "Gotong Royong",
    desc: "Tradisi saling membantu dalam pertanian dan upacara adat tetap menjadi nilai utama yang merekatkan komunitas Karawang hingga hari ini.",
    Icon: Users,
  },
  {
    name: "Religius & Spiritual",
    desc: "Tradisi ritual adat seperti Seren Taun dan kepercayaan kepada Dewi Sri menunjukkan dimensi spiritual yang kuat dalam kehidupan bermasyarakat.",
    Icon: Sparkles,
  },
  {
    name: "Adaptasi & Keterbukaan",
    desc: "Karawang mampu menerima modernisasi industri tanpa melupakan akar budaya tradisionalnya, menciptakan keseimbangan yang unik.",
    Icon: TrendingUp,
  },
]

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      staggerChildren: 0.15,
      delayChildren: 0.2,
    },
  },
}

const itemVariants = {
  hidden: { opacity: 0, y: 20 },
  visible: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.6, ease: "easeOut" },
  },
}

export function NilaiSection() {
  return (
    <section className="py-20 px-4 md:px-8 bg-white">
      <div className="max-w-6xl mx-auto">
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true, amount: 0.3 }}
        >
          <h2 className="text-4xl md:text-5xl font-bold mb-4" style={{ color: "#047857" }}>
            Nilai yang Diwariskan
          </h2>
          <div className="h-1 w-20 mb-12" style={{ backgroundColor: "#fbbf24" }} />
        </motion.div>

        <motion.div
          className="grid grid-cols-1 md:grid-cols-2 gap-8"
          variants={containerVariants}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, amount: 0.2 }}
        >
          {values.map((value, index) => {
            const Icon = value.Icon
            return (
              <motion.div
                key={index}
                variants={itemVariants}
                whileHover={{ y: -5 }}
                className="p-8 rounded-lg bg-gradient-to-br from-green-50 to-blue-50"
              >
                <motion.div
                  className="w-16 h-16 rounded-lg flex items-center justify-center mb-4 text-white"
                  style={{ backgroundColor: "#047857" }}
                  whileHover={{ scale: 1.1 }}
                >
                  <Icon size={32} />
                </motion.div>
                <h3 className="text-2xl font-bold mb-3" style={{ color: "#047857" }}>
                  {value.name}
                </h3>
                <p className="text-gray-700 leading-relaxed">{value.desc}</p>
              </motion.div>
            )
          })}
        </motion.div>
      </div>
    </section>
  )
}
