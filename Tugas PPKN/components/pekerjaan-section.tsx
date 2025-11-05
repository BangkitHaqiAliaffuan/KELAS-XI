"use client"

import { motion } from "framer-motion"
import { Wheat, Factory, Fish, Package, ShoppingBag, Waves } from "lucide-react"

const pekerjaan = [
  {
    name: "Petani Padi",
    desc: "Karawang sebagai lumbung padi nasional dengan ribuan petani yang menjaga ketahanan pangan.",
    Icon: Wheat,
  },
  {
    name: "Buruh Industri",
    desc: "Banyak pabrik otomotif & elektronik yang memberikan lapangan kerja bagi jutaan tenaga kerja.",
    Icon: Factory,
  },
  {
    name: "Nelayan",
    desc: "Pesisir utara Karawang menjadi sumber kehidupan bagi komunitas nelayan yang tangguh.",
    Icon: Fish,
  },
  {
    name: "Pengrajin Bambu",
    desc: "Keahlian mengolah bambu menjadi produk bernilai tinggi tetap dilestarikan hingga kini.",
    Icon: Package,
  },
  {
    name: "Pedagang Pasar",
    desc: "Aktivitas perdagangan di pasar tradisional menjadi jantung ekonomi lokal Karawang.",
    Icon: ShoppingBag,
  },
  {
    name: "Petambak Udang",
    desc: "Tambak udang dan ikan air tawar menjadi industri ekonomi yang berkembang pesat.",
    Icon: Waves,
  },
]

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      staggerChildren: 0.1,
      delayChildren: 0.2,
    },
  },
}

const itemVariants = {
  hidden: { opacity: 0, scale: 0 },
  visible: {
    opacity: 1,
    scale: 1,
    transition: { duration: 0.5, ease: "easeOut" },
  },
}

export function PekerjaanSection() {
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
            Dinamika Ekonomi Karawang
          </h2>
          <div className="h-1 w-20 mb-12" style={{ backgroundColor: "#fbbf24" }} />
        </motion.div>

        <motion.div
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8"
          variants={containerVariants}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, amount: 0.2 }}
        >
          {pekerjaan.map((job, index) => {
            const Icon = job.Icon
            return (
              <motion.div key={index} variants={itemVariants} className="flex flex-col items-center text-center">
                <motion.div
                  className="w-24 h-24 rounded-full flex items-center justify-center mb-6 text-white"
                  style={{ backgroundColor: "#047857" }}
                  whileHover={{ scale: 1.1 }}
                >
                  <Icon size={40} />
                </motion.div>
                <h3 className="text-xl font-bold mb-2" style={{ color: "#047857" }}>
                  {job.name}
                </h3>
                <p className="text-gray-600 leading-relaxed">{job.desc}</p>
              </motion.div>
            )
          })}
        </motion.div>
      </div>
    </section>
  )
}
