"use client"

import { motion } from "framer-motion"
import { useInView } from "framer-motion"
import { useRef, useState, useEffect } from "react"
import { Quote } from "lucide-react"

export default function KesimpulanSection() {
  const ref = useRef(null)
  const isInView = useInView(ref, { once: true, margin: "-100px" })
  const [displayedText, setDisplayedText] = useState("")
  const [isTypingComplete, setIsTypingComplete] = useState(false)

  const fullText =
    "Palembang merupakan kota yang kaya akan keberagaman budaya, dari kuliner ikonik seperti pempek yang mendunia hingga seni songket yang menjadi kebanggaan. Nilai gotong royong yang terwujud dalam tradisi begawi dan keramahan masyarakat Palembang menjadi fondasi kokoh dalam menciptakan harmoni di tengah keberagaman. Warisan budaya yang dijaga hingga kini membuktikan kuatnya jati diri masyarakat Palembang."

  useEffect(() => {
    if (!isInView) return

    let currentIndex = 0
    const typingSpeed = 30 // milliseconds per character

    const typingInterval = setInterval(() => {
      if (currentIndex <= fullText.length) {
        setDisplayedText(fullText.slice(0, currentIndex))
        currentIndex++
      } else {
        setIsTypingComplete(true)
        clearInterval(typingInterval)
      }
    }, typingSpeed)

    return () => clearInterval(typingInterval)
  }, [isInView])

  return (
    <section ref={ref} className="min-h-screen relative flex items-center justify-center py-20 overflow-hidden bg-gradient-to-br from-[#FFF8DC] via-[#FFE4B5] to-[#FFD700]/20">
      {/* Background Pattern */}
      <div
        className="absolute inset-0 opacity-5"
        style={{
          backgroundImage: `url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80"><circle cx="40" cy="40" r="30" fill="none" stroke="%23800000" stroke-width="2"/><circle cx="40" cy="40" r="20" fill="none" stroke="%23FFD700" stroke-width="1.5"/></svg>')`,
          backgroundSize: "80px 80px",
        }}
      />

      <div className="container mx-auto px-4 lg:px-8 relative z-10 max-w-5xl">
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="relative"
        >
          {/* Ornament Border Card */}
          <div className="relative backdrop-blur-lg bg-white/60 border-4 border-[#FFD700] rounded-3xl p-8 lg:p-16 shadow-2xl ornament-border">
            {/* Top Quote Icon */}
            <div className="flex justify-center mb-8">
              <motion.div
                initial={{ scale: 0, rotate: -180 }}
                animate={isInView ? { scale: 1, rotate: 0 } : {}}
                transition={{ duration: 0.6, delay: 0.2 }}
                className="w-20 h-20 rounded-full bg-gradient-to-br from-[#FFD700] to-[#d4af37] flex items-center justify-center shadow-lg"
              >
                <Quote size={40} className="text-[#800000]" />
              </motion.div>
            </div>

            {/* Typing Text */}
            <div className="relative">
              <p className="text-lg lg:text-2xl text-gray-800 leading-relaxed text-center font-serif mb-8 min-h-[200px]">
                {displayedText}
                {!isTypingComplete && (
                  <motion.span
                    animate={{ opacity: [1, 0] }}
                    transition={{ duration: 0.8, repeat: Infinity }}
                    className="inline-block w-1 h-6 lg:h-8 bg-[#800000] ml-1"
                  />
                )}
              </p>
            </div>

            {/* Bottom Quote Icon */}
            <div className="flex justify-center mb-8">
              <motion.div
                initial={{ scale: 0, rotate: 180 }}
                animate={isInView ? { scale: 1, rotate: 0 } : {}}
                transition={{ duration: 0.6, delay: 0.4 }}
                className="w-20 h-20 rounded-full bg-gradient-to-br from-[#d4af37] to-[#FFD700] flex items-center justify-center shadow-lg"
              >
                <Quote size={40} className="text-[#800000] rotate-180" />
              </motion.div>
            </div>

            {/* Decorative Line */}
            <motion.div
              initial={{ scaleX: 0 }}
              animate={isInView ? { scaleX: 1 } : {}}
              transition={{ delay: 0.6, duration: 1 }}
              className="h-1 bg-gradient-to-r from-transparent via-[#800000] to-transparent mx-auto max-w-md mb-8"
            />

            {/* Footer Text */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={isInView ? { opacity: 1 } : {}}
              transition={{ duration: 0.6, delay: 1 }}
              className="text-center"
            >
              <p className="text-2xl lg:text-3xl font-bold text-[#800000] mb-2">
                Keberagaman adalah Kekuatan Palembang
              </p>
              <p className="text-lg text-[#800000]/70 italic">
                "Bhinneka Tunggal Ika"
              </p>
            </motion.div>
          </div>
        </motion.div>

        {/* Floating Decorative Elements */}
        <motion.div
          animate={{
            y: [0, -20, 0],
            rotate: [0, 10, 0],
          }}
          transition={{
            duration: 5,
            repeat: Infinity,
            ease: "easeInOut",
          }}
          className="absolute top-10 left-10 w-24 h-24 border-4 border-[#FFD700]/30 rounded-full -z-10"
        />
        <motion.div
          animate={{
            y: [0, 20, 0],
            rotate: [0, -10, 0],
          }}
          transition={{
            duration: 6,
            repeat: Infinity,
            ease: "easeInOut",
          }}
          className="absolute bottom-10 right-10 w-32 h-32 border-4 border-[#800000]/20 rounded-full -z-10"
        />
      </div>
    </section>
  )
}
