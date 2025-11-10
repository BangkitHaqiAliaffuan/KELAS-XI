"use client"

import { motion } from "framer-motion"
import { useInView } from "react-intersection-observer"
import { Card } from "@/components/ui/card"
import { ImageCarousel } from "@/components/image-carousel"

const foods = [
  {
    name: "Pempek",
    image: "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Pempek_Palembang.JPG/800px-Pempek_Palembang.JPG",
    description: "Fishcake khas dengan tekstur kenyal, disajikan dengan saus cuko yang asam-manis pedas.",
  },
  {
    name: "Tekwan",
    image: "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/Tekwan_Palembang.jpg/800px-Tekwan_Palembang.jpg",
    description: "Sup ikan dengan kuah kaldu bening yang menyegarkan, dilengkapi dengan jamur dan soun.",
  },
  {
    name: "Laksan",
    image: "/laksan-palembang-traditional-food-with-coconut-mil.jpg",
    description: "Kuah santan gurih dengan irisan pempek dan taburan ebi yang harum.",
  },
]

const carouselImages = [
  {
    url: "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Pempek_Palembang.JPG/800px-Pempek_Palembang.JPG",
    caption: "Pempek Kapal Selam",
  },
  {
    url: "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/Tekwan_Palembang.jpg/800px-Tekwan_Palembang.jpg",
    caption: "Tekwan Segar",
  },
  { url: "/model-palembang-traditional-dish.jpg", caption: "Model - Hidangan Khas" },
  { url: "/martabak-har-palembang.jpg", caption: "Martabak HAR" },
]

export function MakananSection() {
  const [ref, inView] = useInView({ triggerOnce: true, threshold: 0.1 })

  return (
    <section id="makanan-khas" className="py-20 lg:ml-64 bg-background songket-pattern">
      <div className="container mx-auto px-4">
        <motion.div
          ref={ref}
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-12"
        >
          <h2 className="font-serif text-4xl md:text-5xl font-bold text-primary mb-4">Makanan Khas</h2>
          <div className="w-24 h-1 bg-secondary mx-auto mb-6" />
          <p className="text-lg text-muted-foreground max-w-3xl mx-auto leading-relaxed">
            Makanan Palembang didominasi olahan ikan dari Sungai Musi, mencerminkan adaptasi budaya Melayu-Cina yang
            unik.
          </p>
        </motion.div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
          {foods.map((food, index) => (
            <motion.div
              key={food.name}
              initial={{ opacity: 0, y: 30 }}
              animate={inView ? { opacity: 1, y: 0 } : {}}
              transition={{ duration: 0.8, delay: index * 0.2 }}
            >
              <Card className="overflow-hidden group hover:shadow-2xl hover:shadow-secondary/30 transition-all duration-500 hover:-translate-y-2 border-2 border-secondary/20">
                <div className="relative h-64 overflow-hidden">
                  <img
                    src={food.image || "/placeholder.svg"}
                    alt={food.name}
                    className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-primary/80 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500" />
                </div>
                <div className="p-6">
                  <h3 className="font-serif text-2xl font-bold text-secondary mb-3">{food.name}</h3>
                  <p className="text-muted-foreground leading-relaxed">{food.description}</p>
                </div>
              </Card>
            </motion.div>
          ))}
        </div>

        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={inView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8, delay: 0.6 }}
          className="bg-card rounded-xl p-6 shadow-lg border-2 border-secondary/20"
        >
          <h3 className="font-serif text-2xl font-bold text-secondary mb-4 text-center">Keunikan Kuliner Palembang</h3>
          <p className="text-muted-foreground mb-6 text-center leading-relaxed max-w-3xl mx-auto">
            Penggunaan ikan segar yang digiling dengan bumbu khas, disajikan dengan saus cuko yang unik, mencerminkan
            perpaduan budaya Melayu dan Cina yang harmonis.
          </p>
          <ImageCarousel images={carouselImages} />
        </motion.div>
      </div>
    </section>
  )
}
