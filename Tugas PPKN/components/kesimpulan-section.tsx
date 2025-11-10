"use client"

import { motion } from "framer-motion"
import { useInView } from "react-intersection-observer"

export function KesimpulanSection() {
  const [ref, inView] = useInView({ triggerOnce: true, threshold: 0.1 })

  return (
    <section id="kesimpulan" className="py-20 lg:ml-64 bg-background relative overflow-hidden">
      {/* Subtle Songket Pattern Background */}
      <div className="absolute inset-0 songket-pattern opacity-30" />

      <div className="container mx-auto px-4 relative z-10">
        <motion.div
          ref={ref}
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-12"
        >
          <h2 className="font-serif text-4xl md:text-5xl font-bold text-primary mb-4">Kesimpulan</h2>
          <div className="w-24 h-1 bg-secondary mx-auto" />
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8, delay: 0.2 }}
          className="max-w-4xl mx-auto"
        >
          <div className="bg-card/90 backdrop-blur-sm rounded-2xl p-10 md:p-12 shadow-2xl border-2 border-secondary/30">
            <div className="prose prose-lg max-w-none">
              <p className="text-muted-foreground text-lg leading-relaxed mb-6 text-center md:text-left">
                Keberagaman budaya Palembang merupakan cerminan dari sejarah panjang sebagai pusat kerajaan Sriwijaya
                yang menyatukan berbagai pengaruh budaya. Perpaduan budaya Melayu dengan unsur-unsur dari Cina, India,
                Arab, dan pengaruh kolonial menciptakan mosaik budaya yang kaya dan harmonis.
              </p>

              <div className="bg-gradient-to-r from-secondary/10 via-accent/10 to-secondary/10 p-6 rounded-xl my-8 border-l-4 border-secondary">
                <p className="text-muted-foreground leading-relaxed mb-4">
                  Dari makanan khas yang didominasi olahan ikan Sungai Musi, pakaian adat dengan songket bertenun emas,
                  kesenian yang memadukan Hindu-Buddha dan Islam, hingga berbagai profesi yang menopang ekonomi
                  lokal—semuanya menunjukkan bagaimana keberagaman menjadi kekuatan.
                </p>
                <p className="text-muted-foreground leading-relaxed">
                  Adat istiadat seperti Tepung Tawar menjadi simbol perdamaian dan resolusi konflik, sementara
                  nilai-nilai toleransi, gotong royong, dan harmoni sosial tertanam kuat dalam kehidupan sehari-hari.
                </p>
              </div>

              <p className="text-lg font-semibold text-primary text-center mt-8 leading-relaxed">
                Keberagaman Palembang bukan hanya tentang perbedaan yang hidup berdampingan, tetapi tentang bagaimana
                perbedaan tersebut disatukan menjadi identitas kolektif yang menciptakan masyarakat harmonis, produktif,
                dan inklusif—sebuah kekuatan Indonesia yang sesungguhnya.
              </p>
            </div>

            <div className="mt-10 grid grid-cols-1 md:grid-cols-3 gap-6 text-center">
              <div className="bg-accent/10 p-6 rounded-xl">
                <div className="text-3xl font-bold text-accent mb-2">🍜</div>
                <p className="text-sm font-semibold text-primary">Kuliner Kaya</p>
              </div>
              <div className="bg-secondary/10 p-6 rounded-xl">
                <div className="text-3xl font-bold text-secondary mb-2">👘</div>
                <p className="text-sm font-semibold text-primary">Warisan Sriwijaya</p>
              </div>
              <div className="bg-primary/10 p-6 rounded-xl">
                <div className="text-3xl font-bold text-primary mb-2">🤝</div>
                <p className="text-sm font-semibold text-primary">Harmoni Sosial</p>
              </div>
            </div>
          </div>
        </motion.div>
      </div>
    </section>
  )
}
