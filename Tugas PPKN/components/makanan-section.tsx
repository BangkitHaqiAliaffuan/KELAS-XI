"use client"

import { motion } from "framer-motion"
import Image from "next/image"

const foods = [
  {
    name: "Nasi Bogana",
    desc: "Nasi liwet khas dengan lauk ayam/ikan, tahu, tempe, dibungkus daun pisang. Hidangan sederhana namun penuh cita rasa yang menjadi identitas kuliner Karawang.",
    image: "/images/makanan/nasi-bogana.jpg",
  },
  {
    name: "Sate Bandeng",
    desc: "Sate dari daging ikan bandeng tanpa duri yang dibumbui khas. Cita rasa unik yang mencerminkan kekayaan maritim pesisir Karawang.",
    image: "/images/makanan/sate-bandeng.jpg",
  },
  {
    name: "Doclang",
    desc: "Ketupat dengan sayuran, tahu goreng, dan kuah oncom pedas. Makanan tradisional yang tetap diminati hingga kini.",
    image: "/images/makanan/doclang.jpg",
  },
  {
    name: "Soto Karawang",
    desc: "Kuah bening dengan daging sapi, koya kacang. Makanan yang hangat dan menggugah selera pada setiap musim.",
    image: "/images/makanan/soto-karawang.jpg",
  },
  {
    name: "Bugis Karawang",
    desc: "Jajanan roti goreng dengan gula merah. Camilan manis yang populer di pasaran tradisional Karawang.",
    image: "/images/makanan/bugis-karawang.jpg",
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
  hidden: { opacity: 0, x: -50 },
  visible: {
    opacity: 1,
    x: 0,
    transition: { duration: 0.6, ease: "easeOut" },
  },
}

export function MakananSection() {
  return (
    <section className="py-20 px-4 md:px-8">
      <div className="max-w-6xl mx-auto">
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true, amount: 0.3 }}
        >
          <h2 className="text-4xl md:text-5xl font-bold mb-4" style={{ color: "#047857" }}>
            Kuliner Khas Karawang
          </h2>
          <div className="h-1 w-20 mb-12" style={{ backgroundColor: "#fbbf24" }} />
        </motion.div>

        <motion.div
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-6"
          variants={containerVariants}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, amount: 0.2 }}
        >
          {foods.map((food, index) => (
            <motion.div
              key={index}
              variants={itemVariants}
              whileHover={{ scale: 1.05, borderColor: "#047857" }}
              className="bg-white rounded-lg overflow-hidden shadow-md border-2 border-transparent transition-all"
            >
              <div className="relative h-40 bg-gradient-to-br from-green-100 to-blue-100 flex items-center justify-center">
                <Image
                  src={food.image || "/placeholder.svg"}
                  alt={food.name}
                  width={200}
                  height={160}
                  className="w-full h-full object-cover"
                  priority={index < 2}
                />
              </div>
              <div className="p-4">
                <h3 className="font-bold text-xl mb-2" style={{ color: "#047857" }}>
                  {food.name}
                </h3>
                <p className="text-sm text-gray-600 leading-relaxed">{food.desc}</p>
              </div>
            </motion.div>
          ))}
        </motion.div>
      </div>
    </section>
  )
}
