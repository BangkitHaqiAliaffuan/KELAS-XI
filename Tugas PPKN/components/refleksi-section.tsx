"use client"

import { motion } from "framer-motion"
import { useInView } from "react-intersection-observer"
import { Check, X, ExternalLink } from "lucide-react"

const reflections = [
  {
    number: 1,
    statement: "Harmoni dalam keberagaman penting untuk lingkungan kerja inklusif",
    answer: "Benar",
    isCorrect: true,
    explanation: "Harmoni memungkinkan setiap individu merasa dihargai dan berkontribusi maksimal tanpa diskriminasi.",
  },
  {
    number: 2,
    statement: "Keberagaman pekerjaan tidak berkontribusi pada harmoni",
    answer: "Salah",
    isCorrect: false,
    explanation:
      "Keberagaman pekerjaan menciptakan saling ketergantungan dan apresiasi antar profesi, memperkuat harmoni sosial.",
  },
  {
    number: 3,
    statement: "Nilai harmoni mengurangi konflik dan meningkatkan produktivitas",
    answer: "Benar",
    isCorrect: true,
    explanation: "Harmoni menciptakan lingkungan kondusif untuk kolaborasi dan penyelesaian masalah secara damai.",
    hasConflictExample: true,
  },
  {
    number: 4,
    statement: "Harmoni hanya tanggung jawab pimpinan",
    answer: "Salah",
    isCorrect: false,
    explanation: "Harmoni adalah tanggung jawab bersama seluruh anggota masyarakat, bukan hanya pemimpin.",
  },
  {
    number: 5,
    statement: "Toleransi dan harmoni adalah hal yang sama",
    answer: "Salah",
    isCorrect: false,
    explanation:
      "Toleransi adalah menghargai perbedaan, sedangkan harmoni adalah hasil dari toleransi—hidup damai bersama.",
  },
]

export function RefleksiSection() {
  const [ref, inView] = useInView({ triggerOnce: true, threshold: 0.1 })

  return (
    <section id="refleksi-pernyataan" className="py-20 lg:ml-64 bg-primary/5">
      <div className="container mx-auto px-4">
        <motion.div
          ref={ref}
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-12"
        >
          <h2 className="font-serif text-4xl md:text-5xl font-bold text-primary mb-4">Refleksi Pernyataan</h2>
          <div className="w-24 h-1 bg-secondary mx-auto mb-6" />
          <p className="text-lg text-muted-foreground max-w-3xl mx-auto leading-relaxed">
            Memahami pentingnya harmoni dalam keberagaman melalui evaluasi pernyataan.
          </p>
        </motion.div>

        <div className="max-w-5xl mx-auto space-y-6">
          {reflections.map((item, index) => (
            <motion.div
              key={item.number}
              initial={{ opacity: 0, x: -30 }}
              animate={inView ? { opacity: 1, x: 0 } : {}}
              transition={{ duration: 0.8, delay: index * 0.15 }}
              className="bg-card rounded-xl shadow-lg overflow-hidden border-2 border-secondary/20"
            >
              <div className="p-6">
                <div className="flex items-start gap-4">
                  <div className="flex-shrink-0">
                    <div className="w-12 h-12 bg-secondary rounded-full flex items-center justify-center">
                      <span className="font-bold text-secondary-foreground text-lg">{item.number}</span>
                    </div>
                  </div>
                  <div className="flex-1">
                    <p className="text-lg font-medium text-foreground mb-4">{item.statement}</p>
                    <div className="flex items-center gap-3 mb-3">
                      {item.isCorrect ? (
                        <div className="flex items-center gap-2 bg-accent/10 px-4 py-2 rounded-full">
                          <Check className="h-5 w-5 text-accent" />
                          <span className="font-semibold text-accent">{item.answer}</span>
                        </div>
                      ) : (
                        <div className="flex items-center gap-2 bg-destructive/10 px-4 py-2 rounded-full">
                          <X className="h-5 w-5 text-destructive" />
                          <span className="font-semibold text-destructive">{item.answer}</span>
                        </div>
                      )}
                    </div>
                    <p className="text-muted-foreground leading-relaxed text-sm bg-muted/30 p-4 rounded-lg">
                      <span className="font-semibold text-primary">Penjelasan:</span> {item.explanation}
                    </p>

                    {item.hasConflictExample && (
                      <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={inView ? { opacity: 1, y: 0 } : {}}
                        transition={{ duration: 0.8, delay: index * 0.15 + 0.3 }}
                        className="mt-6 bg-gradient-to-br from-accent/10 to-secondary/10 rounded-lg p-6 border-2 border-accent/30"
                      >
                        <h4 className="font-serif text-xl font-bold text-primary mb-4 flex items-center gap-2">
                          <span className="w-2 h-2 bg-accent rounded-full animate-pulse" />
                          Contoh Nyata: Resolusi Konflik di Palembang
                        </h4>

                        <div className="space-y-4">
                          <div className="bg-card rounded-lg p-4 border-l-4 border-destructive/50">
                            <h5 className="font-semibold text-destructive mb-2">Konflik:</h5>
                            <p className="text-sm text-muted-foreground leading-relaxed">
                              Sengketa tanah dan perselisihan antar kelompok masyarakat yang berpotensi menimbulkan
                              konflik SARA di wilayah Sumatera Selatan, termasuk Palembang.
                            </p>
                          </div>

                          <div className="bg-card rounded-lg p-4 border-l-4 border-accent/50">
                            <h5 className="font-semibold text-accent mb-2">Solusi:</h5>
                            <p className="text-sm text-muted-foreground leading-relaxed">
                              Penyelesaian melalui musyawarah dan ritual kearifan lokal seperti{" "}
                              <span className="font-semibold text-primary">Tepung Tawar</span> (ritual perdamaian).
                              Pendekatan ini telah memastikan Sumatera Selatan tetap{" "}
                              <span className="font-semibold text-accent">zero konflik besar</span>, menjaga harmoni
                              sosial di tengah keberagaman etnis dan budaya.
                            </p>
                          </div>

                          <div className="bg-card rounded-lg p-4">
                            <h5 className="font-semibold text-primary mb-2">Dampak:</h5>
                            <p className="text-sm text-muted-foreground leading-relaxed mb-3">
                              Nilai harmoni bukan hanya konsep abstrak, tetapi praktik nyata yang mengurangi konflik
                              SARA dan meningkatkan produktivitas masyarakat melalui kolaborasi lintas budaya.
                            </p>

                            <div className="space-y-2 pt-3 border-t border-muted">
                              <h6 className="text-xs font-semibold text-primary uppercase tracking-wide mb-2">
                                Sumber Referensi Valid:
                              </h6>
                              <a
                                href="https://www.detik.com/sumbagsel/berita/d-8140530/herman-deru-paparkan-kearifan-lokal-pastikan-sumsel-zero-konflik"
                                target="_blank"
                                rel="noopener noreferrer"
                                className="flex items-start gap-2 text-accent hover:text-accent/80 transition-colors group text-xs"
                              >
                                <ExternalLink className="h-3.5 w-3.5 mt-0.5 flex-shrink-0 group-hover:scale-110 transition-transform" />
                                <span className="underline leading-tight">
                                  Herman Deru Paparkan Kearifan Lokal Pastikan Sumsel Zero Konflik - Detik Sumsel (2024)
                                </span>
                              </a>
                              <a
                                href="https://ejournal.umm.ac.id/index.php/JICC/article/view/26352"
                                target="_blank"
                                rel="noopener noreferrer"
                                className="flex items-start gap-2 text-accent hover:text-accent/80 transition-colors group text-xs"
                              >
                                <ExternalLink className="h-3.5 w-3.5 mt-0.5 flex-shrink-0 group-hover:scale-110 transition-transform" />
                                <span className="underline leading-tight">
                                  Studi Kearifan Lokal dalam Resolusi Konflik - Jurnal Ilmu Komunikasi UMM
                                </span>
                              </a>
                            </div>
                          </div>
                        </div>
                      </motion.div>
                    )}
                  </div>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  )
}
