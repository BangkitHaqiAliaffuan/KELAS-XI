"use client"

import { motion } from "framer-motion"
import { useInView } from "framer-motion"
import { useRef } from "react"
import { Quote } from "lucide-react"

export function KesimpulanSection() {
  const ref = useRef(null)
  const isInView = useInView(ref, { once: true, margin: "-100px" })

  return (
    <section ref={ref} className="relative py-24 px-4 md:px-8 lg:px-16 bg-gradient-to-br from-slate-900 via-emerald-900 to-teal-900 overflow-hidden">
      {/* Animated Background Elements */}
      <div className="absolute inset-0">
        <motion.div
          animate={{
            scale: [1, 1.2, 1],
            opacity: [0.1, 0.2, 0.1],
          }}
          transition={{
            duration: 8,
            repeat: Infinity,
            ease: "easeInOut"
          }}
          className="absolute top-20 left-20 w-96 h-96 bg-emerald-500/20 rounded-full blur-3xl"
        />
        <motion.div
          animate={{
            scale: [1.2, 1, 1.2],
            opacity: [0.1, 0.15, 0.1],
          }}
          transition={{
            duration: 10,
            repeat: Infinity,
            ease: "easeInOut"
          }}
          className="absolute bottom-20 right-20 w-96 h-96 bg-teal-500/20 rounded-full blur-3xl"
        />
      </div>

      {/* Decorative Pattern */}
      <div className="absolute inset-0 opacity-5" style={{
        backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
      }} />
      
      <motion.div
        initial={{ opacity: 0, y: 30 }}
        animate={isInView ? { opacity: 1, y: 0 } : { opacity: 0, y: 30 }}
        transition={{ duration: 0.8 }}
        className="max-w-5xl mx-auto relative z-10"
      >
        <div className="text-center mb-12">
          <motion.span
            initial={{ opacity: 0, scale: 0.8 }}
            animate={isInView ? { opacity: 1, scale: 1 } : { opacity: 0, scale: 0.8 }}
            transition={{ duration: 0.5 }}
            className="inline-block px-4 py-2 bg-emerald-500/20 backdrop-blur-sm border border-emerald-400/30 text-emerald-100 rounded-full text-sm font-semibold mb-4"
          >
            📜 RANGKUMAN
          </motion.span>
          <h2 className="text-5xl md:text-6xl font-bold text-white mb-4">
            Kesimpulan
          </h2>
        </div>

        <div className="relative">
          {/* Main Card */}
          <motion.div
            initial={{ scale: 0.95, opacity: 0 }}
            animate={isInView ? { scale: 1, opacity: 1 } : { scale: 0.95, opacity: 0 }}
            transition={{ delay: 0.2, duration: 0.6 }}
            className="relative bg-white/10 backdrop-blur-lg border border-white/20 rounded-3xl p-10 md:p-14 shadow-2xl overflow-hidden"
          >
            {/* Decorative glow */}
            <div className="absolute -top-24 -right-24 w-48 h-48 bg-emerald-400/20 rounded-full blur-3xl" />
            <div className="absolute -bottom-24 -left-24 w-48 h-48 bg-teal-400/20 rounded-full blur-3xl" />

            {/* Quote Icon */}
            <motion.div
              initial={{ scale: 0, rotate: -180 }}
              animate={isInView ? { scale: 1, rotate: 0 } : { scale: 0, rotate: -180 }}
              transition={{ delay: 0.3, duration: 0.6, type: "spring" }}
              className="flex justify-center mb-8"
            >
              <div className="w-20 h-20 bg-gradient-to-br from-emerald-400 to-teal-400 rounded-2xl flex items-center justify-center shadow-lg">
                <Quote size={40} className="text-white" />
              </div>
            </motion.div>

            {/* Content */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={isInView ? { opacity: 1, y: 0 } : { opacity: 0, y: 20 }}
              transition={{ delay: 0.5, duration: 0.8 }}
              className="relative z-10"
            >
              <p className="text-lg md:text-xl text-white/95 leading-relaxed text-center font-light">
                Kabupaten Seluma merupakan representasi harmoni antara kehidupan pesisir dan daratan Bengkulu. Suku
                Serawai sebagai mayoritas penduduk mempertahankan warisan budaya melalui kesenian Tari Andun, tenun
                songket, dan tradisi Berandai. Keberagaman mata pencaharian dari nelayan pantai hingga petani perkebunan
                menciptakan dinamika ekonomi yang saling melengkapi. Kuliner khas seperti Pendap dan Lemang Tapai menjadi
                identitas yang kuat, sementara nilai gotong royong dan keramahan tetap menjadi pegangan masyarakat Seluma
                dalam menjaga harmoni kehidupan berbangsa dan bernegara.
              </p>
            </motion.div>

            {/* Divider */}
            <motion.div
              initial={{ scaleX: 0 }}
              animate={isInView ? { scaleX: 1 } : { scaleX: 0 }}
              transition={{ delay: 0.7, duration: 0.6 }}
              className="mt-10 pt-10 border-t border-white/20"
            >
              <div className="flex items-center justify-center gap-3">
                <div className="w-12 h-1 bg-gradient-to-r from-transparent via-emerald-400 to-transparent rounded-full" />
                <p className="text-sm text-emerald-200 font-semibold tracking-wider">
                  HARMONI DALAM KEBERAGAMAN
                </p>
                <div className="w-12 h-1 bg-gradient-to-r from-transparent via-emerald-400 to-transparent rounded-full" />
              </div>
              <p className="text-center text-white/70 text-xs mt-4">PPKn Kelas XI RPL</p>
            </motion.div>
          </motion.div>

          {/* Footer Decoration */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={isInView ? { opacity: 1 } : { opacity: 0 }}
            transition={{ delay: 1, duration: 0.8 }}
            className="mt-8 text-center"
          >
            <div className="inline-flex items-center gap-2 text-white/50 text-sm">
              <div className="w-2 h-2 bg-emerald-400 rounded-full animate-pulse" />
              <span>Seluma - Bengkulu</span>
              <div className="w-2 h-2 bg-teal-400 rounded-full animate-pulse" />
            </div>
          </motion.div>
        </div>
      </motion.div>
    </section>
  )
}
