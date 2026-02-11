"use client"

import { HeroSection } from "@/components/hero-section"
import { MakananSection } from "@/components/makanan-section"
import { PakaianSection } from "@/components/pakaian-section"
import { KesenianSection } from "@/components/kesenian-section"
import { PekerjaanSection } from "@/components/pekerjaan-section"
import { AdatUnikSection } from "@/components/adat-unik-section"
import { NilaiSection } from "@/components/nilai-section"
import { KesimpulanSection } from "@/components/kesimpulan-section"

export default function Home() {
  return (
    <main className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-gray-50 scroll-smooth overflow-x-hidden">
      <HeroSection />
      <MakananSection />
      <PakaianSection />
      <KesenianSection />
      <PekerjaanSection />
      <AdatUnikSection />
      <NilaiSection />
      <KesimpulanSection />
    </main>
  )
}
