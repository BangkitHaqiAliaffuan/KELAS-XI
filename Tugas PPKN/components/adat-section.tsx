"use client"

import { motion } from "framer-motion"

const traditions = [
  {
    number: "1",
    name: "Seren Taun",
    desc: "Upacara panen padi sebagai bentuk syukuran kepada Dewi Sri. Tradisi ini menunjukkan rasa terima kasih masyarakat Karawang terhadap hasil bumi yang melimpah.",
  },
  {
    number: "2",
    name: "Ruwatan",
    desc: "Ritual pembersihan spiritual untuk menolak bala dan energi negatif. Kepercayaan ini masih kuat dalam masyarakat Sunda Karawang hingga saat ini.",
  },
  {
    number: "3",
    name: "Sedekah Bumi",
    desc: "Ritual syukuran hasil bumi kepada laut dan alam semesta. Dilakukan terutama oleh masyarakat pesisir sebagai bentuk rasa syukur dan menjaga keseimbangan alam.",
  },
  {
    number: "4",
    name: "Mapag Sri",
    desc: "Ritual menjemput Dewi Sri saat musim tanam padi dimulai. Upacara penting yang membuka musim tanam dengan doa dan harapan untuk panen yang berlimpah.",
  },
  {
    number: "5",
    name: "Bahasa Sunda Karawang",
    desc: "Dialek khas dengan logat pesisir yang unik dan berbeda dari daerah Sunda lainnya. Bahasa lokal ini adalah identitas budaya yang patut dibanggakan.",
  },
]

export function AdatSection() {
  return (
    <section className="py-20 px-4 md:px-8 bg-gradient-to-b from-blue-50 to-white">
      <div className="max-w-4xl mx-auto">
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true, amount: 0.3 }}
        >
          <h2 className="text-4xl md:text-5xl font-bold mb-4" style={{ color: "#047857" }}>
            Tradisi yang Melekat
          </h2>
          <div className="h-1 w-20 mb-12" style={{ backgroundColor: "#fbbf24" }} />
        </motion.div>

        <div className="relative">
          {/* Timeline line */}
          <div
            className="absolute left-1/2 transform -translate-x-1/2 w-1 h-full"
            style={{ backgroundColor: "#fbbf24", zIndex: 1 }}
          />

          {/* Timeline items */}
          <div className="space-y-12">
            {traditions.map((tradition, index) => {
              const isEven = index % 2 === 0
              const isLeft = isEven

              return (
                <motion.div
                  key={index}
                  initial={{ opacity: 0, x: isLeft ? -100 : 100 }}
                  whileInView={{ opacity: 1, x: 0 }}
                  transition={{ duration: 0.6, delay: index * 0.1 }}
                  viewport={{ once: true, amount: 0.5 }}
                  className={`flex ${isLeft ? "flex-row" : "flex-row-reverse"}`}
                >
                  {/* Content */}
                  <div className={`w-1/2 ${isLeft ? "pr-12 text-right" : "pl-12"}`}>
                    <div className="bg-white p-6 rounded-lg shadow-md">
                      <h3 className="text-xl font-bold mb-2" style={{ color: "#047857" }}>
                        {tradition.name}
                      </h3>
                      <p className="text-gray-600 text-sm leading-relaxed">{tradition.desc}</p>
                    </div>
                  </div>

                  {/* Timeline dot */}
                  <div className="w-0 flex justify-center relative" style={{ zIndex: 10 }}>
                    <motion.div
                      className="flex items-center justify-center font-black text-white shadow-lg border-4 border-white"
                      style={{
                        width: "72px",
                        height: "72px",
                        borderRadius: "50%",
                        backgroundColor: "#047857",
                        fontSize: "32px",
                        boxShadow: "0 0 0 3px #047857, 0 0 0 7px #fbbf24, 0 8px 16px rgba(0, 0, 0, 0.2)",
                        position: "relative",
                        zIndex: 20,
                      }}
                      whileHover={{
                        scale: 1.15,
                        boxShadow: "0 0 0 3px #047857, 0 0 0 7px #fbbf24, 0 12px 24px rgba(0, 0, 0, 0.3)",
                      }}
                      transition={{ type: "spring", stiffness: 300, damping: 20 }}
                    >
                      {tradition.number}
                    </motion.div>
                  </div>

                  {/* Empty space */}
                  <div className="w-1/2" />
                </motion.div>
              )
            })}
          </div>
        </div>
      </div>
    </section>
  )
}
