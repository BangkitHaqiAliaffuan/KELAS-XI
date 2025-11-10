"use client"

import { motion } from "framer-motion"
import { useInView } from "react-intersection-observer"
import { useState } from "react"
import { ChevronDown } from "lucide-react"
import { cn } from "@/lib/utils"

const pakaianData = [
  {
    title: "Aesan Gede",
    description:
      "Pakaian pengantin tradisional dengan baju kurung, kain songket bertenun emas, dan aksesoris emas yang mewah. Dikenakan dalam upacara pernikahan adat Palembang.",
    image: "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4a/Aesan_Gede.jpg/800px-Aesan_Gede.jpg",
    details:
      "Aesan Gede merupakan simbol kemewahan dan keagungan kerajaan Sriwijaya. Setiap detail ornamen memiliki makna filosofis yang dalam.",
  },
  {
    title: "Aesan Paksangko",
    description:
      "Pakaian adat dengan dodot dan selendang bermotif songket yang dikenakan untuk acara resmi dan upacara adat tertentu.",
    image: "/aesan-paksangko-palembang-traditional-costume.jpg",
    details:
      "Paksangko menunjukkan tingkatan sosial dan dikenakan oleh keluarga bangsawan atau tokoh masyarakat dalam acara penting.",
  },
]

export function PakaianSection() {
  const [ref, inView] = useInView({ triggerOnce: true, threshold: 0.1 })
  const [openIndex, setOpenIndex] = useState<number | null>(0)

  return (
    <section id="pakaian-adat" className="py-20 lg:ml-64 bg-primary/5">
      <div className="container mx-auto px-4">
        <motion.div
          ref={ref}
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-12"
        >
          <h2 className="font-serif text-4xl md:text-5xl font-bold text-primary mb-4">Pakaian Adat</h2>
          <div className="w-24 h-1 bg-secondary mx-auto mb-6" />
          <p className="text-lg text-muted-foreground max-w-3xl mx-auto leading-relaxed">
            Kain songket dengan tenun emas dan motif simbolis merupakan warisan kerajaan Sriwijaya yang masih lestari
            hingga kini.
          </p>
        </motion.div>

        <div className="max-w-4xl mx-auto space-y-4">
          {pakaianData.map((item, index) => (
            <motion.div
              key={item.title}
              initial={{ opacity: 0, x: -30 }}
              animate={inView ? { opacity: 1, x: 0 } : {}}
              transition={{ duration: 0.8, delay: index * 0.2 }}
              className="bg-card rounded-xl shadow-lg overflow-hidden border-2 border-secondary/20"
            >
              <button
                onClick={() => setOpenIndex(openIndex === index ? null : index)}
                className="w-full flex items-center justify-between p-6 text-left hover:bg-secondary/10 transition-colors"
              >
                <h3 className="font-serif text-2xl font-bold text-secondary">{item.title}</h3>
                <ChevronDown
                  className={cn(
                    "h-6 w-6 text-secondary transition-transform duration-300",
                    openIndex === index && "rotate-180",
                  )}
                />
              </button>

              <motion.div
                initial={false}
                animate={{
                  height: openIndex === index ? "auto" : 0,
                  opacity: openIndex === index ? 1 : 0,
                }}
                transition={{ duration: 0.3 }}
                className="overflow-hidden"
              >
                <div className="p-6 pt-0 grid md:grid-cols-2 gap-6">
                  <div>
                    <p className="text-muted-foreground mb-4 leading-relaxed">{item.description}</p>
                    <div className="bg-accent/10 p-4 rounded-lg border-l-4 border-accent">
                      <h4 className="font-semibold text-accent mb-2">Keunikan:</h4>
                      <p className="text-sm text-muted-foreground leading-relaxed">{item.details}</p>
                    </div>
                  </div>
                  <div className="relative h-64 md:h-auto rounded-lg overflow-hidden group cursor-pointer">
                    <img
                      src={item.image || "/placeholder.svg"}
                      alt={item.title}
                      className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                      loading="lazy"
                    />
                  </div>
                </div>
              </motion.div>
            </motion.div>
          ))}
        </div>

        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8, delay: 0.4 }}
          className="mt-12 bg-card rounded-xl p-8 shadow-lg border-2 border-secondary/20 max-w-4xl mx-auto"
        >
          <h3 className="font-serif text-2xl font-bold text-center text-secondary mb-4">Warisan Songket Sriwijaya</h3>
          <p className="text-muted-foreground text-center leading-relaxed">
            Kain songket Palembang terkenal dengan tenun emas yang rumit dan motif-motif simbolis yang merepresentasikan
            keagungan kerajaan Sriwijaya. Setiap benang emas yang ditenun mengandung makna filosofis tentang kemakmuran
            dan kehormatan.
          </p>
        </motion.div>
      </div>
    </section>
  )
}
