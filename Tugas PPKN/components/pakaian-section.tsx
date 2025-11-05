"use client"

import { motion } from "framer-motion"
import Image from "next/image"

const pakaian = [
  {
    name: "Kebaya Sunda",
    desc: "Busana tradisional wanita yang elegan dengan bahan brokat atau tile, dilengkapi selendang bermotif batik atau songket. Sanggul yang tertata rapi menambah keanggunan pemakainya.",
    gender: "Wanita",
    image: "/images/pakaian/kebaya-sunda.jpg",
  },
  {
    name: "Pangsi & Beskap",
    desc: "Busana tradisional pria Sunda dengan celana pangsi berwarna hitam, beskap (jaket), dan ikat kepala. Melambangkan kesederhanaan dan kemaskulinan dalam tradisi Sunda Karawang.",
    gender: "Pria",
    image: "/images/pakaian/pangsi-beskap.jpg",
  },
]

const itemVariants = {
  hidden: { opacity: 0, scale: 0.9 },
  visible: {
    opacity: 1,
    scale: 1,
    transition: { duration: 0.6, ease: "easeOut" },
  },
}

export function PakaianSection() {
  return (
    <section className="py-20 px-4 md:px-8 bg-gradient-to-b from-white to-green-50">
      <div className="max-w-6xl mx-auto">
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true, amount: 0.3 }}
        >
          <h2 className="text-4xl md:text-5xl font-bold mb-4" style={{ color: "#047857" }}>
            Busana Tradisional Karawang
          </h2>
          <div className="h-1 w-20 mb-12" style={{ backgroundColor: "#fbbf24" }} />
        </motion.div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {pakaian.map((item, index) => (
            <motion.div
              key={index}
              variants={itemVariants}
              initial="hidden"
              whileInView="visible"
              whileHover={{ scale: 1.05 }}
              viewport={{ once: true, amount: 0.3 }}
              className="bg-white rounded-xl shadow-lg overflow-hidden"
            >
              <div className="relative h-64 bg-gradient-to-br from-emerald-100 to-blue-100">
                <Image
                  src={item.image || "/placeholder.svg"}
                  alt={item.name}
                  width={400}
                  height={256}
                  className="w-full h-full object-cover"
                  priority={index === 0}
                />
              </div>
              <div className="p-6">
                <div className="flex items-center justify-between mb-2">
                  <h3 className="text-2xl font-bold" style={{ color: "#047857" }}>
                    {item.name}
                  </h3>
                  <span
                    className="text-sm px-3 py-1 rounded-full"
                    style={{ backgroundColor: "#fbbf24", color: "#1a1a1a" }}
                  >
                    {item.gender}
                  </span>
                </div>
                <p className="text-gray-700 leading-relaxed">{item.desc}</p>
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  )
}
