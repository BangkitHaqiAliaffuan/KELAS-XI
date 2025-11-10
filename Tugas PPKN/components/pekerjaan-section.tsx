"use client"

import { cn } from "@/lib/utils"

import { motion } from "framer-motion"
import { useInView } from "react-intersection-observer"
import { useState } from "react"

const jobs = [
  {
    title: "Nelayan Sungai Musi",
    image: "/fisherman-on-musi-river-palembang.jpg",
    description:
      "Menangkap ikan di Sungai Musi untuk bahan baku makanan khas seperti pempek dan tekwan. Tradisi turun-temurun yang menjadi tulang punggung kuliner Palembang.",
  },
  {
    title: "Pedagang Pasar",
    image: "/traditional-market-scene-palembang.jpg",
    description:
      "Menjual hasil bumi, ikan segar, dan produk lokal di pasar-pasar tradisional yang ramai. Menjadi pusat ekonomi masyarakat lokal.",
  },
  {
    title: "Petani dan Pekebun",
    image: "/farmer-in-rice-field-indonesia.jpg",
    description:
      "Mengolah lahan pertanian dan perkebunan karet, kelapa sawit, dan padi. Menopang ekonomi agraris di wilayah Palembang.",
  },
  {
    title: "Pekerja Industri Minyak",
    image: "/oil-industry-refinery-indonesia.jpg",
    description:
      "Bekerja di sektor pertambangan dan industri minyak yang berkembang di Sumatera Selatan. Memberikan kontribusi besar pada ekonomi daerah.",
  },
  {
    title: "Pengrajin Songket",
    image: "/songket-weaver-artisan-traditional.jpg",
    description:
      "Menenun kain songket dengan benang emas secara tradisional. Melestarikan warisan budaya dan menciptakan karya seni bernilai tinggi.",
  },
  {
    title: "Pembuat Pempek",
    image: "/making-pempek-traditional-food-process.jpg",
    description:
      "Membuat dan menjual pempek sebagai usaha keluarga. Menjaga resep turun-temurun dan melayani pelanggan lokal maupun wisatawan.",
  },
]

export function PekerjaanSection() {
  const [ref, inView] = useInView({ triggerOnce: true, threshold: 0.1 })
  const [flipped, setFlipped] = useState<number[]>([])

  const toggleFlip = (index: number) => {
    setFlipped((prev) => (prev.includes(index) ? prev.filter((i) => i !== index) : [...prev, index]))
  }

  return (
    <section id="pekerjaan-masyarakat" className="py-20 lg:ml-64 bg-primary/5">
      <div className="container mx-auto px-4">
        <motion.div
          ref={ref}
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-12"
        >
          <h2 className="font-serif text-4xl md:text-5xl font-bold text-primary mb-4">Pekerjaan Masyarakat</h2>
          <div className="w-24 h-1 bg-secondary mx-auto mb-6" />
          <p className="text-lg text-muted-foreground max-w-3xl mx-auto leading-relaxed">
            Keberagaman profesi masyarakat Palembang mencerminkan kekayaan sumber daya alam dan budaya yang harmonis.
          </p>
        </motion.div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {jobs.map((job, index) => (
            <motion.div
              key={job.title}
              initial={{ opacity: 0, y: 30 }}
              animate={inView ? { opacity: 1, y: 0 } : {}}
              transition={{ duration: 0.8, delay: index * 0.1 }}
              className="perspective-1000"
            >
              <div
                onClick={() => toggleFlip(index)}
                className={cn(
                  "relative h-80 cursor-pointer transition-transform duration-700 transform-style-3d",
                  flipped.includes(index) && "rotate-y-180",
                )}
              >
                {/* Front of card */}
                <div className="absolute inset-0 backface-hidden rounded-xl overflow-hidden shadow-lg border-2 border-secondary/20">
                  <img src={job.image || "/placeholder.svg"} alt={job.title} className="w-full h-full object-cover" />
                  <div className="absolute inset-0 bg-gradient-to-t from-primary via-primary/60 to-transparent flex items-end">
                    <h3 className="font-serif text-2xl font-bold text-secondary p-6">{job.title}</h3>
                  </div>
                </div>

                {/* Back of card */}
                <div className="absolute inset-0 backface-hidden rotate-y-180 rounded-xl bg-accent p-6 flex items-center justify-center shadow-lg border-2 border-secondary/20 shadow-accent/30">
                  <div className="text-center">
                    <h3 className="font-serif text-2xl font-bold text-accent-foreground mb-4">{job.title}</h3>
                    <p className="text-accent-foreground leading-relaxed">{job.description}</p>
                  </div>
                </div>
              </div>
            </motion.div>
          ))}
        </div>

        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8, delay: 0.6 }}
          className="mt-12 bg-card rounded-xl p-8 shadow-lg border-2 border-secondary/20 max-w-4xl mx-auto text-center"
        >
          <p className="text-muted-foreground leading-relaxed">
            <span className="font-semibold text-primary">Tip:</span> Klik kartu untuk melihat detail pekerjaan dan
            kontribusinya terhadap masyarakat Palembang.
          </p>
        </motion.div>
      </div>
    </section>
  )
}
