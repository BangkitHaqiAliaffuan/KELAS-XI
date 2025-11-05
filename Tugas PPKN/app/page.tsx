"use client"
import LoadingScreen from "@/components/loading-screen"
import HeroSection from "@/components/sections/hero-section"
import MakananSection from "@/components/sections/makanan-section"
import PakaianSection from "@/components/sections/pakaian-section"
import KesenianSection from "@/components/sections/kesenian-section"
import PekerjaanSection from "@/components/sections/pekerjaan-section"
import AdatUnikSection from "@/components/sections/adat-unik-section"
import NilaiSection from "@/components/sections/nilai-section"
import KesimpulanSection from "@/components/sections/kesimpulan-section"

export default function Home() {
  return (
    <>
      <LoadingScreen />
      <main style={{ overflowX: "hidden" }}>
        <HeroSection />
        <MakananSection />
        <PakaianSection />
        <KesenianSection />
        <PekerjaanSection />
        <AdatUnikSection />
        <NilaiSection />
        <KesimpulanSection />
      </main>
    </>
  )
}
