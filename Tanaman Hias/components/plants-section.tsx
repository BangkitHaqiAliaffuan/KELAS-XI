"use client"

import { useState, useEffect, useRef } from "react"
import { Leaf, Droplets, Sun } from "lucide-react"

interface Plant {
  id: number
  name: string
  scientific: string
  icon: "leaf" | "droplets" | "sun"
  tips: string[]
  color: string
  image: string
}

const plants: Plant[] = [
  {
    id: 1,
    name: "Tanaman Euphorbia Tithymaloides",
    scientific: "Euphorbia tithymaloides",
    icon: "sun",
    tips: ["Tanaman hias tropis", "Memerlukan cahaya penuh", "Tahan terhadap kekeringan"],
    color: "border-primary-green",
    image: "/tanaman-asli/tanaman-euphorbia-tithymaloides.jpg",
  },
  {
    id: 2,
    name: "Tanaman Melati Belanda",
    scientific: "Combretum indicum",
    icon: "droplets",
    tips: ["Bunga berwarna putih harum", "Menyukai sinar matahari", "Air cukup setiap hari"],
    color: "border-secondary-green",
    image: "/tanaman-asli/tanaman-melati-belanda.jpg",
  },
  {
    id: 3,
    name: "Tanaman Asoka",
    scientific: "Saraca asoca",
    icon: "leaf",
    tips: ["Sering disebut pohon tanpa kesedihan", "Memiliki bunga berwarna oranye kemerahan"],
    color: "border-accent-green",
    image: "/tanaman-asli/tanaman-asoka.jpg",
  },
  {
    id: 4,
    name: "Tanaman Taiwan",
    scientific: "Rhaphidophora decursiva",
    icon: "leaf",
    tips: ["Tanaman merambat indah", "Tahan naungan partial", "Suhu hangat diperlukan"],
    color: "border-primary-green",
    image: "/tanaman-asli/tanaman-taiwan.jpg",
  },
  {
    id: 5,
    name: "Tanaman Bulu Ayam",
    scientific: "Celosia plumosa",
    icon: "droplets",
    tips: ["Bunga berwarna cerah", "Memerlukan cahaya penuh", "Tahan panas dan kering"],
    color: "border-secondary-green",
    image: "/tanaman-asli/tanaman-bulu-ayam.jpg",
  },
  {
    id: 6,
    name: "Tanaman Pucuk Merah",
    scientific: "Syzygium podophyllum",
    icon: "sun",
    tips: ["Daun merah menarik", "Pertumbuhan cepat", "Cocok untuk pagar hidup"],
    color: "border-accent-green",
    image: "/tanaman-asli/tanaman-pucuk-merah.jpg",
  },
  {
    id: 7,
    name: "Tanaman Alore",
    scientific: "Ruellia simplex",
    icon: "sun",
    tips: ["Toleran naungan parsial.", "Adaptasi tanah luas.", "Minimal air diperlukan"],
    color: "border-primary-green",
    image: "/tanaman-asli/tanaman-alore.jpg",
  },
  {
    id: 8,
    name: "Tanaman Puring",
    scientific: "Codiaeum variegatum",
    icon: "droplets",
    tips: ["Daun beraneka warna", "Cahaya terang penting", "Penyiraman teratur"],
    color: "border-secondary-green",
    image: "/tanaman-asli/tanaman-puring.jpg",
  },
  {
    id: 9,
    name: "Tanaman Lavenia",
    scientific: "Ravenia Spectabilis",
    icon: "leaf",
    tips: ["Harum dan cantik", "Drainase baik diperlukan", "Tahan penyakit"],
    color: "border-accent-green",
    image: "/tanaman-asli/tanaman-lavenia.jpg",
  },
  {
    id: 10,
    name: "Tanaman Wali Songo",
    scientific: "Wali Songo",
    icon: "sun",
    tips: ["Tanaman bersejarah", "Cocok untuk taman tradisional", "Simbolis dan bermakna"],
    color: "border-primary-green",
    image: "/tanaman-asli/tanaman-walisongo.jpg",
  },
]

interface PlantCardProps {
  plant: Plant
  index: number
}

function PlantCard({ plant, index }: PlantCardProps) {
  const [isFlipped, setIsFlipped] = useState(false)

  const getIconComponent = (icon: string) => {
    switch (icon) {
      case "leaf":
        return <Leaf className="w-8 h-8" />
      case "droplets":
        return <Droplets className="w-8 h-8" />
      case "sun":
        return <Sun className="w-8 h-8" />
      default:
        return <Leaf className="w-8 h-8" />
    }
  }

  return (
    <div
      className="h-96 perspective cursor-pointer rounded-xl overflow-hidden shadow-lg transition-all duration-300 group"
      onClick={() => setIsFlipped(!isFlipped)}
      style={{
        animation: `slideIn 0.6s ease-out ${index * 0.1}s both`,
      }}
    >
      <style>{`
        @keyframes slideIn {
          from {
            opacity: 0;
            transform: translateY(20px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }
      `}</style>

      <div
        className="relative w-full h-full transition-transform duration-500"
        style={{
          transformStyle: "preserve-3d",
          transform: isFlipped ? "rotateY(180deg)" : "rotateY(0deg)",
        }}
      >
        {/* Front - Background Image with Overlay */}
        <div className="absolute w-full h-full rounded-xl overflow-hidden" style={{ backfaceVisibility: "hidden" }}>
          <div
            className="w-full h-full bg-cover bg-center flex flex-col items-center justify-between p-6 relative"
            style={{
              backgroundImage: `url(${plant.image || "/placeholder.svg"})`,
              backgroundPosition: "center",
              backgroundSize: "cover",
            }}
          >
            <div className="absolute inset-0 bg-black/50 rounded-xl" />

            {/* Content on top of background */}
            <div className="relative z-10 text-white text-center w-full flex flex-col justify-between h-full">
              <div />
              <div>
                <h3 className="text-2xl font-bold mb-2 text-balance">{plant.name}</h3>
                <p className="text-sm italic text-cream-bg/80 mb-6">{plant.scientific}</p>
              </div>
              <button className="bg-secondary-green hover:bg-primary-green text-white px-4 py-2 rounded-lg text-sm font-semibold hover:shadow-lg transition-all hover:scale-105 w-full">
                Lihat Detail
              </button>
            </div>
          </div>
        </div>

        {/* Back - Care Tips */}
        <div
          className="absolute w-full h-full bg-primary-green text-white flex flex-col p-6 justify-between rounded-xl"
          style={{ backfaceVisibility: "hidden", transform: "rotateY(180deg)" }}
        >
          <div>
            <p className="text-sm font-semibold text-accent-green mb-2">Perawatan</p>
            <ul className="text-sm space-y-2">
              {plant.tips.map((tip, idx) => (
                <li key={idx} className="text-cream-bg">
                  • {tip}
                </li>
              ))}
            </ul>
          </div>
          <button className="bg-secondary-green text-white px-4 py-2 rounded-lg text-sm font-semibold hover:shadow-lg transition-all w-full">
            Klik untuk balik
          </button>
        </div>
      </div>
    </div>
  )
}

export default function PlantsSection() {
  const sectionRef = useRef<HTMLDivElement>(null)
  const [isVisible, setIsVisible] = useState(false)

  useEffect(() => {
    const observer = new IntersectionObserver(([entry]) => setIsVisible(entry.isIntersecting), { threshold: 0.1 })
    if (sectionRef.current) observer.observe(sectionRef.current)
    return () => observer.disconnect()
  }, [])

  return (
    <section ref={sectionRef} className="relative py-20 px-4 md:px-8 bg-gradient-to-b from-white to-cream-bg/40">
      <div className="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-secondary-green via-accent-green to-secondary-green" />

      <div className="max-w-7xl mx-auto">
        <div className="text-center mb-16">
          <h2 className="text-5xl md:text-6xl font-bold gradient-text mb-4">Tanaman Hias Kami</h2>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            Koleksi lengkap tanaman hias Indonesia dengan informasi lengkap perawatan
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-6">
          {plants.map((plant, idx) => (
            <div key={plant.id}>
              <PlantCard plant={plant} index={idx} />
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
