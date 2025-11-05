"use client"

import { HeroSection } from "@/components/hero-section"
import { MakananSection } from "@/components/makanan-section"
import { PakaianSection } from "@/components/pakaian-section"
import { KesenianSection } from "@/components/kesenian-section"
import { PekerjaanSection } from "@/components/pekerjaan-section"
import { AdatSection } from "@/components/adat-section"
import { NilaiSection } from "@/components/nilai-section"
import { ConclusionSection } from "@/components/conclusion-section"

export default function Home() {
  return (
    <main className="overflow-x-hidden">
      <HeroSection />
      <MakananSection />
      <PakaianSection />
      <KesenianSection />
      <PekerjaanSection />
      <AdatSection />
      <NilaiSection />
      <ConclusionSection />
    </main>
  )
}
