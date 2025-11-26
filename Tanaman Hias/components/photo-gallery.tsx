"use client"

import { useState, useEffect } from "react"

interface GalleryImage {
  id: string
  url: string
  title: string
  uploadedAt: string
}

export default function PhotoGallery() {
  const [images, setImages] = useState<GalleryImage[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchGalleryImages()
  }, [])

  const fetchGalleryImages = async () => {
    try {
      console.log("[v0] Fetching gallery images...")
      const response = await fetch("/api/gallery")
      if (response.ok) {
        const data = await response.json()
        console.log("[v0] Gallery images fetched:", data.length, "images")
        setImages(data)
      }
    } catch (error) {
      console.error("[v0] Failed to fetch gallery images:", error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="relative py-20 px-4 md:px-8 bg-gradient-to-b from-cream-bg/40 to-white">
      <div className="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-secondary-green via-accent-green to-secondary-green" />

      <div className="max-w-7xl mx-auto">
        <div className="text-center mb-16">
          <h2 className="text-5xl md:text-6xl font-bold gradient-text mb-4">Galeri Foto</h2>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            Koleksi foto taman hias inspiratif dari komunitas kami
          </p>
        </div>

        {loading ? (
          <div className="flex justify-center items-center py-20">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-green"></div>
          </div>
        ) : images.length === 0 ? (
          <div className="text-center py-20">
            <p className="text-gray-600 text-lg">Belum ada foto di galeri</p>
          </div>
        ) : (
          <div className="columns-1 md:columns-2 lg:columns-3 gap-6 space-y-6">
            {images.map((image, idx) => (
              <div
                key={image.id}
                className="relative break-inside-avoid mb-6 rounded-lg overflow-hidden shadow-lg hover:shadow-2xl transition-all duration-300 group"
              >
                <div className="relative w-full overflow-hidden bg-gray-200">
                  <img
                    src={image.url || "/placeholder.svg"}
                    alt={image.title || "Gallery image"}
                    className="w-full h-auto object-cover group-hover:scale-110 transition-transform duration-500"
                    loading="lazy"
                  />
                  <div className="absolute inset-0 bg-black/0 group-hover:bg-black/40 transition-colors duration-300 flex items-center justify-center">
                    <p className="text-white font-semibold text-lg opacity-0 group-hover:opacity-100 transition-opacity duration-300 px-4 text-center">
                      {image.title}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </section>
  )
}
