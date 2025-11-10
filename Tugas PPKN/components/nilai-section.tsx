"use client"

import { motion } from "framer-motion"
import { useInView } from "react-intersection-observer"
import { Check, Heart, Users, Handshake, Sparkles } from "lucide-react"

const values = [
  {
    icon: Heart,
    title: "Toleransi",
    description:
      "Menghargai keberagaman etnis, agama, dan budaya dalam kehidupan sehari-hari. Saling menghormati perbedaan sebagai kekayaan.",
  },
  {
    icon: Users,
    title: "Gotong Royong",
    description:
      "Bekerja sama dalam menyelesaikan konflik dan membangun masyarakat. Musyawarah menjadi cara utama dalam pengambilan keputusan.",
  },
  {
    icon: Handshake,
    title: "Ramah dan Tertib",
    description:
      "Budaya menyambut tamu dengan hangat dan menjaga ketertiban sosial. Hospitalitas tinggi terhadap pendatang.",
  },
  {
    icon: Sparkles,
    title: "Harmoni Sosial",
    description:
      "Menjaga keseimbangan hubungan antar kelompok masyarakat untuk mengurangi konflik SARA dan menciptakan kedamaian.",
  },
]

export function NilaiSection() {
  const [ref, inView] = useInView({ triggerOnce: true, threshold: 0.1 })

  return (
    <section id="nilai-budaya" className="py-20 lg:ml-64 bg-primary/5">
      <div className="container mx-auto px-4">
        <motion.div
          ref={ref}
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-12"
        >
          <h2 className="font-serif text-4xl md:text-5xl font-bold text-primary mb-4">Nilai Budaya</h2>
          <div className="w-24 h-1 bg-secondary mx-auto mb-6" />
          <p className="text-lg text-muted-foreground max-w-3xl mx-auto leading-relaxed">
            Nilai-nilai luhur yang menjadi fondasi harmoni dan kedamaian masyarakat Palembang.
          </p>
        </motion.div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-5xl mx-auto mb-12">
          {values.map((value, index) => {
            const Icon = value.icon
            return (
              <motion.div
                key={value.title}
                initial={{ opacity: 0, y: 30 }}
                animate={inView ? { opacity: 1, y: 0 } : {}}
                transition={{ duration: 0.8, delay: index * 0.15 }}
                className="bg-card rounded-xl p-8 shadow-lg border-2 border-secondary/20 hover:shadow-2xl hover:shadow-accent/20 transition-all duration-300 hover:-translate-y-1"
              >
                <div className="flex items-start gap-4">
                  <div className="flex-shrink-0 w-14 h-14 bg-accent/10 rounded-lg flex items-center justify-center">
                    <Icon className="h-7 w-7 text-accent" />
                  </div>
                  <div className="flex-1">
                    <h3 className="font-serif text-2xl font-bold text-secondary mb-3">{value.title}</h3>
                    <p className="text-muted-foreground leading-relaxed">{value.description}</p>
                  </div>
                </div>
              </motion.div>
            )
          })}
        </div>

        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8, delay: 0.6 }}
          className="max-w-4xl mx-auto"
        >
          <div className="bg-gradient-to-br from-accent/10 to-secondary/10 rounded-xl p-8 shadow-lg border-2 border-accent/30">
            <div className="flex items-center gap-3 mb-6">
              <Check className="h-8 w-8 text-accent flex-shrink-0" />
              <h3 className="font-serif text-2xl font-bold text-primary">Kontribusi Nilai Harmoni</h3>
            </div>
            <ul className="space-y-4">
              {[
                "Menciptakan lingkungan kerja yang inklusif dan produktif",
                "Mengurangi konflik SARA melalui dialog dan musyawarah",
                "Meningkatkan solidaritas sosial antar kelompok masyarakat",
                "Memperkuat identitas budaya sambil terbuka pada perubahan",
                "Menjadi contoh kerukunan beragama dan toleransi di Indonesia",
              ].map((item, index) => (
                <motion.li
                  key={index}
                  initial={{ opacity: 0, x: -20 }}
                  animate={inView ? { opacity: 1, x: 0 } : {}}
                  transition={{ duration: 0.5, delay: 0.7 + index * 0.1 }}
                  className="flex items-start gap-3"
                >
                  <Check className="h-5 w-5 text-accent flex-shrink-0 mt-1" />
                  <span className="text-muted-foreground leading-relaxed">{item}</span>
                </motion.li>
              ))}
            </ul>
          </div>
        </motion.div>
      </div>
    </section>
  )
}
