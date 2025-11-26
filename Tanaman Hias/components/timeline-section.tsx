"use client"

interface TimelineDay {
  day: number
  title: string
  description: string
  icon: string
  color: string
  image: string
}

const timelineDays: TimelineDay[] = [
  {
    day: 1,
    title: "Persiapan",
    description: "Sosialisasi Kegiatan Kokulikuler, Pembagian Kelompok",
    icon: "Seed",
    color: "bg-primary-green",
    image: "/days/day1.JPG",
  },
  {
    day: 2,
    title: "Pembuatan Design",
    description: "Pembuatan Design Digital dan Taman Manual",
    icon: "Sprout",
    color: "bg-secondary-green",
    image: "/days/day2.JPG",
  },
  {
    day: 3,
    title: "Penyusunan Proposal",
    description: "Penyusunan Proposal dan Survei Tanaman",
    icon: "Droplets",
    color: "bg-accent-green",
    image: "/days/day3.JPG",
  },
  {
    day: 4,
    title: "Pengolahan Tanah",
    description: "Daun pertama muncul, tanaman mulai berkembang dengan baik",
    icon: "Leaf",
    color: "bg-primary-green",
    image: "/days/day4.JPG",
  },
  {
    day: 5,
    title: "Pemupukan",
    description: "Memberikan nutrisi tambahan untuk mendukung pertumbuhan optimal",
    icon: "Flower",
    color: "bg-secondary-green",
    image: "/days/day5.JPG",
  },
  {
    day: 6,
    title: "Panen",
    description: "Hasil panen yang memuaskan! Tanaman siap untuk dipetik dan dinikmati",
    icon: "Trophy",
    color: "bg-accent-green",
    image: "/days/day6.JPG",
  },
]

export default function TimelineSection() {
  return (
    <section className="relative py-20 px-4 md:px-8 bg-cream-bg">
      <div className="absolute inset-0 opacity-5">
        <div
          className="absolute inset-0"
          style={{
            backgroundImage: "radial-gradient(circle, #1B5E3F 1px, transparent 1px)",
            backgroundSize: "30px 30px",
          }}
        />
      </div>

      <div className="relative z-20 max-w-3xl mx-auto">
        <div className="text-center mb-16">
          <h2 className="text-5xl md:text-6xl font-bold text-primary-green mb-4">Perjalanan Tanam hingga Panen</h2>
          <p className="text-lg text-gray-700 max-w-2xl mx-auto">
            Saksikan transformasi luar biasa dari benih menjadi tanaman yang berbuah
          </p>
        </div>

        <div className="relative">
          <div className="absolute left-1/2 top-0 bottom-0 w-1 bg-gradient-to-b from-primary-green via-secondary-green to-accent-green transform -translate-x-1/2 z-0" />

          <div className="space-y-12">
            {timelineDays.map((item, idx) => (
              <div key={idx} className={`flex gap-6 md:gap-8 ${idx % 2 === 0 ? "md:flex-row" : "md:flex-row-reverse"}`}>
                <div className="flex flex-col items-center flex-shrink-0">
                  <div
                    className={`${item.color} w-16 h-16 rounded-full flex items-center justify-center text-white font-bold text-xl shadow-lg border-4 border-cream-bg relative z-10`}
                  >
                    {item.day}
                  </div>
                </div>

                <div className={`flex-1 ${idx % 2 === 0 ? "md:text-right" : "md:text-left"} relative z-20`}>
                  <div
                    className={`${item.color} rounded-2xl p-6 text-white shadow-lg hover-lift transform transition-all duration-300 group`}
                  >
                    <div className="relative mb-4 w-full aspect-video rounded-lg overflow-hidden shadow-md">
                      <img
                        src={item.image || "/placeholder.svg"}
                        alt={item.title}
                        className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                      />
                    </div>

                    <h3 className="text-2xl font-bold mb-2">{item.title}</h3>
                    <p className="text-sm leading-relaxed mb-4 text-cream-bg/90">{item.description}</p>

                    <div className="w-full bg-white/30 rounded-full h-1">
                      <div
                        className="bg-bronze-accent h-1 rounded-full transition-all duration-500"
                        style={{ width: `${(item.day / timelineDays.length) * 100}%` }}
                      />
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  )
}
