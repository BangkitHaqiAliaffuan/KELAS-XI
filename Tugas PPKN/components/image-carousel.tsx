"use client"

import { useState, useEffect } from "react"
import { ChevronLeft, ChevronRight } from "lucide-react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"

interface CarouselImage {
  url: string
  caption: string
}

interface ImageCarouselProps {
  images: CarouselImage[]
}

export function ImageCarousel({ images }: ImageCarouselProps) {
  const [currentIndex, setCurrentIndex] = useState(0)
  const [isAutoplay, setIsAutoplay] = useState(true)

  useEffect(() => {
    if (!isAutoplay) return

    const interval = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % images.length)
    }, 4000)

    return () => clearInterval(interval)
  }, [isAutoplay, images.length])

  const goToPrevious = () => {
    setCurrentIndex((prev) => (prev - 1 + images.length) % images.length)
    setIsAutoplay(false)
  }

  const goToNext = () => {
    setCurrentIndex((prev) => (prev + 1) % images.length)
    setIsAutoplay(false)
  }

  return (
    <div className="relative">
      <div className="relative h-64 md:h-80 rounded-lg overflow-hidden">
        {images.map((image, index) => (
          <div
            key={index}
            className={cn(
              "absolute inset-0 transition-opacity duration-500",
              index === currentIndex ? "opacity-100" : "opacity-0",
            )}
          >
            <img src={image.url || "/placeholder.svg"} alt={image.caption} className="w-full h-full object-cover" />
            <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-primary/90 to-transparent p-4">
              <p className="text-primary-foreground font-medium text-center">{image.caption}</p>
            </div>
          </div>
        ))}

        <Button
          onClick={goToPrevious}
          size="icon"
          variant="secondary"
          className="absolute left-2 top-1/2 -translate-y-1/2 rounded-full opacity-80 hover:opacity-100"
        >
          <ChevronLeft className="h-5 w-5" />
        </Button>

        <Button
          onClick={goToNext}
          size="icon"
          variant="secondary"
          className="absolute right-2 top-1/2 -translate-y-1/2 rounded-full opacity-80 hover:opacity-100"
        >
          <ChevronRight className="h-5 w-5" />
        </Button>
      </div>

      <div className="flex justify-center gap-2 mt-4">
        {images.map((_, index) => (
          <button
            key={index}
            onClick={() => {
              setCurrentIndex(index)
              setIsAutoplay(false)
            }}
            className={cn(
              "h-2 rounded-full transition-all",
              index === currentIndex ? "w-8 bg-secondary" : "w-2 bg-secondary/30 hover:bg-secondary/50",
            )}
          />
        ))}
      </div>
    </div>
  )
}
