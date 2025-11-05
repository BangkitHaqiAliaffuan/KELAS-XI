"use client"

import { motion } from "framer-motion"
import { Quote } from "lucide-react"

export function ConclusionSection() {
  return (
    <section className="py-20 px-4 md:px-8 bg-amber-50">
      <div className="max-w-4xl mx-auto">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true, amount: 0.3 }}
          className="bg-white rounded-xl p-8 md:p-12 border-4"
          style={{ borderColor: "#047857" }}
        >
          <motion.div
            initial={{ opacity: 0, scale: 0.8 }}
            whileInView={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.6 }}
            className="mb-6"
          >
            <Quote size={48} style={{ color: "#fbbf24" }} />
          </motion.div>

          <motion.p
            initial={{ opacity: 0 }}
            whileInView={{ opacity: 1 }}
            transition={{ duration: 0.8, delay: 0.2 }}
            className="text-lg md:text-xl leading-relaxed text-gray-800 mb-8 text-justify"
          >
            Kabupaten Karawang menunjukkan harmoni unik antara tradisi agraris dan modernitas industri. Sebagai lumbung
            padi Jawa Barat dan pusat industri otomotif nasional, Karawang membuktikan bahwa kemajuan ekonomi dapat
            berjalan beriringan dengan pelestarian budaya. Tradisi Seren Taun dan nilai gotong royong tetap terjaga di
            tengah pesatnya perkembangan industri. Keberagaman pekerjaan dari petani hingga buruh pabrik menciptakan
            dinamika sosial yang kaya, sementara kuliner khas seperti Nasi Bogana dan Sate Bandeng terus menjadi
            identitas yang membanggakan.
          </motion.p>

          <motion.div
            initial={{ opacity: 0, x: -20 }}
            whileInView={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.6, delay: 0.4 }}
            className="pt-6 border-t-2"
            style={{ borderColor: "#fbbf24" }}
          >
            <p className="font-bold text-lg" style={{ color: "#047857" }}>
              Keberagaman Dalam Harmoni
            </p>
            <p className="text-gray-600 text-sm mt-2">Kabupaten Karawang - Jawa Barat</p>
          </motion.div>
        </motion.div>
      </div>
    </section>
  )
}
