"use client"

import { useRef, useState } from "react"
import { motion, useInView } from "framer-motion"
import YouTubeModal from "../youtube-modal"
import { Play } from "lucide-react"

const foods = [
  {
    name: "Pempek",
    description:
      "Makanan khas Palembang yang terbuat dari ikan dan tepung tapioka. Pempek kapal selam adalah varian paling terkenal dengan telur di dalamnya. Disajikan dengan kuah cuko yang gurih dan pedas.",
    image: "/pempek-kapal-selam-khas-palembang.jpg",
    videoId: "usLzSetiMkc",
    badge: "Kuliner Khas",
  },
  {
    name: "Model",
    description:
      "Sejenis pempek yang berbentuk bulat pipih dengan isian daging ikan. Model memiliki tekstur yang lebih padat dan gurih dibanding pempek biasa. Menjadi pilihan favorit untuk camilan atau lauk pauk.",
    image: "/model-makanan-palembang.jpg",
    videoId: "7WDDOBvFQkI",
    badge: "Kuliner Khas",
  },
  {
    name: "Tekwan",
    description:
      "Sup tradisional Palembang dengan ikan, udang, dan telur puyuh. Tekwan memiliki kuah yang gurih dengan aroma rempah yang khas. Sering disajikan sebagai hidangan pembuka di acara-acara khusus.",
    image: "/tekwan-sup-palembang.jpg",
    videoId: "eJ-VVsf06fA",
    badge: "Kuliner Khas",
  },
  {
    name: "Laksan",
    description:
      "Makanan berupa mie dalam kuah santan yang gurih dan lezat. Laksan dilengkapi dengan berbagai topping seperti telur, udang, dan daging. Hidangan ini sempurna untuk sarapan atau makan siang.",
    image: "/laksan-mie-palembang.jpg",
    videoId: "VjqiDfNqVhI",
    badge: "Kuliner Khas",
  },
  {
    name: "Martabak HAR",
    description:
      "Martabak terang bulan khas Palembang dengan isian yang beragam. HAR adalah singkatan dari Haji Amir Rasyid, penjual martabak legendaris. Kulit yang tipis dan isian yang melimpah menjadi ciri khasnya.",
    image: "/martabak-har-palembang.jpg",
    videoId: "i6k3Tp1l8Nc",
    badge: "Kuliner Khas",
  },
]

export default function MakananSection() {
  const ref = useRef(null)
  const isInView = useInView(ref, { once: true, margin: "-100px" })
  const [selectedVideo, setSelectedVideo] = useState<{ id: string; title: string } | null>(null)

  return (
    <section ref={ref} className="min-h-screen relative flex items-center justify-center py-20 overflow-hidden bg-gradient-to-br from-[#FFF8DC] via-[#f8f6f1] to-[#FFD700]/20">
      {/* Background Pattern */}
      <div 
        className="absolute inset-0 opacity-5"
        style={{
          backgroundImage: `url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="60" height="60"><path d="M30 15 L45 30 L30 45 L15 30 Z" fill="%23800000"/></svg>')`,
          backgroundSize: "60px 60px",
        }}
      />

      <div className="container mx-auto px-4 lg:px-8 relative z-10">
        {/* Section Title */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-16"
        >
          <motion.div
            initial={{ width: 0 }}
            animate={isInView ? { width: "100px" } : {}}
            transition={{ delay: 0.3, duration: 0.8 }}
            className="h-1 bg-gradient-to-r from-[#800000] to-[#FFD700] mx-auto mb-6"
          />
          <h2 className="text-4xl lg:text-6xl font-bold text-[#800000] mb-4">
            Kuliner Legendaris Palembang
          </h2>
          <p className="text-lg lg:text-xl text-[#800000]/70 max-w-2xl mx-auto">
            Nikmati kelezatan makanan khas yang telah menjadi warisan turun-temurun
          </p>
        </motion.div>

        {/* Food Cards Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 max-w-7xl mx-auto">
          {foods.map((food, index) => (
            <motion.div
              key={index}
              initial={{ opacity: 0, y: 50 }}
              animate={isInView ? { opacity: 1, y: 0 } : {}}
              transition={{ delay: index * 0.1, duration: 0.6 }}
              className="group relative"
            >
              {/* Glassmorphism Card */}
              <div className="relative h-full rounded-2xl overflow-hidden backdrop-blur-md bg-white/40 border-2 border-[#FFD700]/30 hover:border-[#FFD700]/60 transition-all duration-500 hover:shadow-[0_20px_60px_rgba(255,215,0,0.3)] hover:-translate-y-3">
                {/* Badge */}
                <div className="absolute top-4 right-4 z-10 bg-gradient-to-r from-[#800000] to-[#a52a2a] text-white text-xs font-bold px-3 py-1 rounded-full shadow-lg">
                  {food.badge}
                </div>

                {/* Image Container */}
                <div className="relative h-64 overflow-hidden">
                  <motion.div
                    className="absolute inset-0 bg-gradient-to-t from-[#800000]/80 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500 z-10"
                  />
                  <motion.img
                    src={food.image || "/placeholder.svg"}
                    alt={food.name}
                    className="w-full h-full object-cover"
                    whileHover={{ scale: 1.1 }}
                    transition={{ duration: 0.5 }}
                  />
                  
                  {/* Play Button Overlay */}
                  <motion.button
                    onClick={() => setSelectedVideo({ id: food.videoId, title: food.name })}
                    className="absolute inset-0 z-20 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300"
                    whileHover={{ scale: 1.1 }}
                    whileTap={{ scale: 0.95 }}
                  >
                    <div className="w-16 h-16 rounded-full bg-[#FFD700] flex items-center justify-center shadow-2xl">
                      <Play className="w-8 h-8 text-[#800000] ml-1" fill="currentColor" />
                    </div>
                  </motion.button>
                </div>

                {/* Card Content */}
                <div className="p-6">
                  <h3 className="text-2xl font-bold text-[#800000] mb-3 group-hover:text-[#a52a2a] transition-colors">
                    {food.name}
                  </h3>
                  <p className="text-sm text-gray-700 leading-relaxed mb-4 line-clamp-3">
                    {food.description}
                  </p>
                  
                  {/* Video Button */}
                  <motion.button
                    onClick={() => setSelectedVideo({ id: food.videoId, title: food.name })}
                    whileHover={{ scale: 1.05, boxShadow: "0 0 20px rgba(255,215,0,0.5)" }}
                    whileTap={{ scale: 0.95 }}
                    className="w-full bg-gradient-to-r from-[#800000] to-[#a52a2a] text-white font-semibold py-3 px-6 rounded-lg flex items-center justify-center gap-2 shadow-lg hover:from-[#5c0000] hover:to-[#800000] transition-all duration-300"
                  >
                    <Play className="w-4 h-4" fill="currentColor" />
                    Tonton Video
                  </motion.button>
                </div>

                {/* Decorative Corner */}
                <div className="absolute bottom-0 right-0 w-20 h-20 bg-gradient-to-br from-transparent to-[#FFD700]/20 rounded-tl-full" />
              </div>
            </motion.div>
          ))}
        </div>

        {/* Decorative Elements */}
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ duration: 30, repeat: Infinity, ease: "linear" }}
          className="absolute top-20 right-10 w-32 h-32 border-4 border-[#FFD700]/20 rounded-full -z-10"
        />
        <motion.div
          animate={{ rotate: -360 }}
          transition={{ duration: 40, repeat: Infinity, ease: "linear" }}
          className="absolute bottom-20 left-10 w-40 h-40 border-4 border-[#800000]/10 rounded-full -z-10"
        />
      </div>

      {/* YouTube Modal */}
      {selectedVideo && (
        <YouTubeModal
          isOpen={!!selectedVideo}
          onClose={() => setSelectedVideo(null)}
          videoId={selectedVideo.id}
          title={selectedVideo.title}
        />
      )}
    </section>
  )
}
