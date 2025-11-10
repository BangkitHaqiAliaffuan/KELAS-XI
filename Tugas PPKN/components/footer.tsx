"use client"

import { motion } from "framer-motion"

export function Footer() {
  return (
    <footer className="lg:ml-64 bg-primary py-8">
      <div className="container mx-auto px-4">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8 }}
          viewport={{ once: true }}
          className="text-center"
        >
          <p className="text-secondary font-medium text-lg mb-2">Harmoni Keberagaman Budaya Palembang</p>
          <p className="text-primary-foreground/80 text-sm">Dibuat untuk Tugas PPKN – © 2025</p>
          <div className="mt-6 flex items-center justify-center gap-2">
            <div className="w-16 h-1 bg-secondary rounded" />
            <span className="text-secondary text-2xl">✦</span>
            <div className="w-16 h-1 bg-secondary rounded" />
          </div>
        </motion.div>
      </div>
    </footer>
  )
}
