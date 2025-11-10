"use client"

import { HeroSection } from "@/components/hero-section"
import { SidebarNav } from "@/components/sidebar-nav"
import { MakananSection } from "@/components/makanan-section"
import { PakaianSection } from "@/components/pakaian-section"
import { KesenianSection } from "@/components/kesenian-section"
import { PekerjaanSection } from "@/components/pekerjaan-section"
import { AdatSection } from "@/components/adat-section"
import { NilaiSection } from "@/components/nilai-section"
import { KesimpulanSection } from "@/components/kesimpulan-section"
import { RefleksiSection } from "@/components/refleksi-section"
import { Footer } from "@/components/footer"
import { BackToTop } from "@/components/back-to-top"

export default function Home() {
  return (
    <main className="relative">
      <SidebarNav />
      <HeroSection />
      <MakananSection />
      <PakaianSection />
      <KesenianSection />
      <PekerjaanSection />
      <AdatSection />
      <NilaiSection />
      <KesimpulanSection />
      <RefleksiSection />
      <Footer />
      <BackToTop />
    </main>
  )
}
