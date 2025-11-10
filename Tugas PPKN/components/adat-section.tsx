"use client"

import { cn } from "@/lib/utils"

import { motion } from "framer-motion"
import { useInView } from "react-intersection-observer"
import { useState } from "react"

const adatItems = [
  {
    year: 1,
    title: "Tepung Tawar",
    description: "Ritual perdamaian dengan menaburkan tepung beras dan air sebagai simbol kesucian dan penghormatan.",
    image: "/tepung-tawar-ceremony-palembang.jpg",
    details:
      "Digunakan dalam berbagai upacara penting seperti pernikahan, penyambutan tamu kehormatan, dan resolusi konflik. Ritual ini mencerminkan filosofi perdamaian dan harmoni.",
  },
  {
    year: 2,
    title: "Ngidang",
    description: "Tradisi penyajian makanan bertingkat yang menunjukkan kemewahan dan kehormatan dalam acara adat.",
    image: "/ngidang-traditional-food-presentation.jpg",
    details:
      "Hidangan disusun dalam dulang bertingkat dengan berbagai jenis makanan tradisional. Setiap tingkatan memiliki makna dan urutan penyajian yang khusus.",
  },
  {
    year: 3,
    title: "Mandi Kasai",
    description: "Upacara memandikan bayi dengan air rempah-rempah sebagai bentuk perlindungan dan berkah.",
    image: "/baby-traditional-bath-ceremony-indonesia.jpg",
    details:
      "Dilakukan pada usia tertentu dengan menggunakan ramuan tradisional. Dipercaya memberikan kesehatan dan perlindungan spiritual untuk sang bayi.",
  },
]

export function AdatSection() {
  const [ref, inView] = useInView({ triggerOnce: true, threshold: 0.1 })
  const [selectedIndex, setSelectedIndex] = useState<number | null>(null)

  return (
    <section id="adat-unik" className="py-20 lg:ml-64 bg-background songket-pattern">
      <div className="container mx-auto px-4">
        <motion.div
          ref={ref}
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-16"
        >
          <h2 className="font-serif text-4xl md:text-5xl font-bold text-primary mb-4">Adat Unik</h2>
          <div className="w-24 h-1 bg-secondary mx-auto mb-6" />
          <p className="text-lg text-muted-foreground max-w-3xl mx-auto leading-relaxed">
            Warisan tradisi yang menjaga harmoni sosial dan spiritual masyarakat Palembang.
          </p>
        </motion.div>

        <div className="max-w-4xl mx-auto relative">
          {/* Vertical Timeline Line */}
          <div className="absolute left-8 md:left-1/2 top-0 bottom-0 w-1 bg-primary" />

          {adatItems.map((item, index) => (
            <motion.div
              key={item.title}
              initial={{ opacity: 0, x: index % 2 === 0 ? -50 : 50 }}
              animate={inView ? { opacity: 1, x: 0 } : {}}
              transition={{ duration: 0.8, delay: index * 0.3 }}
              className={cn(
                "relative mb-12 md:mb-16",
                index % 2 === 0 ? "md:pr-1/2 md:text-right" : "md:pl-1/2 md:ml-auto",
              )}
            >
              {/* Timeline Node */}
              <div className="absolute left-8 md:left-1/2 w-12 h-12 -ml-6 bg-secondary rounded-full border-4 border-background flex items-center justify-center shadow-lg z-10">
                <span className="font-bold text-secondary-foreground text-lg">{item.year}</span>
              </div>

              {/* Content Card */}
              <div className="ml-20 md:ml-0 bg-card rounded-xl shadow-lg overflow-hidden border-2 border-secondary/20 hover:shadow-2xl hover:shadow-secondary/20 transition-all duration-300">
                <button
                  onClick={() => setSelectedIndex(selectedIndex === index ? null : index)}
                  className="w-full text-left"
                >
                  <div className="p-6">
                    <h3 className="font-serif text-2xl font-bold text-secondary mb-3">{item.title}</h3>
                    <p className="text-muted-foreground leading-relaxed">{item.description}</p>
                  </div>
                </button>

                <motion.div
                  initial={false}
                  animate={{
                    height: selectedIndex === index ? "auto" : 0,
                    opacity: selectedIndex === index ? 1 : 0,
                  }}
                  transition={{ duration: 0.3 }}
                  className="overflow-hidden"
                >
                  <div className="px-6 pb-6 space-y-4">
                    <img
                      src={item.image || "/placeholder.svg"}
                      alt={item.title}
                      className="w-full h-48 object-cover rounded-lg"
                      loading="lazy"
                    />
                    <div className="bg-accent/10 p-4 rounded-lg border-l-4 border-accent">
                      <p className="text-sm text-muted-foreground leading-relaxed">{item.details}</p>
                    </div>
                  </div>
                </motion.div>
              </div>
            </motion.div>
          ))}
        </div>

        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8, delay: 0.9 }}
          className="mt-12 bg-card rounded-xl p-8 shadow-lg border-2 border-secondary/20 max-w-4xl mx-auto text-center"
        >
          <p className="text-muted-foreground leading-relaxed">
            <span className="font-semibold text-primary">Catatan:</span> Klik pada setiap adat untuk mengetahui detail
            dan makna filosofisnya dalam kehidupan masyarakat Palembang.
          </p>
        </motion.div>
      </div>
    </section>
  )
}
