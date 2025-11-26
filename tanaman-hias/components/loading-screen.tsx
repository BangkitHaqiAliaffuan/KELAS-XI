"use client"

import { useState, useEffect } from "react"

export default function LoadingScreen({ onComplete }: { onComplete: () => void }) {
  const [isLoading, setIsLoading] = useState(true)
  const [progress, setProgress] = useState(0)

  useEffect(() => {
    const progressInterval = setInterval(() => {
      setProgress((prev) => {
        if (prev >= 95) {
          return prev
        }
        return prev + Math.random() * 30
      })
    }, 300)

    const timer = setTimeout(() => {
      setProgress(100)
      setTimeout(() => {
        setIsLoading(false)
        onComplete()
      }, 300)
    }, 2000)

    return () => {
      clearInterval(progressInterval)
      clearTimeout(timer)
    }
  }, [onComplete])

  if (!isLoading) return null

  return (
    <div className="fixed inset-0 z-50 bg-gradient-to-b from-primary-green via-secondary-green to-accent-green flex items-center justify-center overflow-hidden">
      {/* Animated background elements */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-10 left-10 w-40 h-40 rounded-full bg-white blur-3xl animate-pulse" />
        <div
          className="absolute bottom-20 right-20 w-56 h-56 rounded-full bg-white blur-3xl animate-pulse"
          style={{ animationDelay: "1s" }}
        />
      </div>

      {/* Main content */}
      <div className="relative z-10 text-center px-4">
        {/* Logo/Icon animation */}
        <div className="mb-8 inline-block">
          <div className="w-24 h-24 rounded-full bg-white/20 backdrop-blur-sm flex items-center justify-center mb-4 border-2 border-white/50">
            <svg
              className="w-12 h-12 text-cream-bg animate-spin"
              style={{ animationDuration: "3s" }}
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
          </div>
        </div>

        {/* Title */}
        <h1 className="text-4xl md:text-5xl font-bold text-cream-bg mb-2 drop-shadow-lg">TAMAN HIAS XI-RPL</h1>
        <p className="text-cream-bg/80 text-lg mb-12 drop-shadow-md">Mempersiapkan pengalaman inspiratif Anda...</p>

        {/* Progress bar */}
        <div className="max-w-md mx-auto mb-6">
          <div className="h-2 bg-white/30 rounded-full overflow-hidden backdrop-blur-sm border border-white/50">
            <div
              className="h-full bg-gradient-to-r from-cream-bg via-accent-green to-cream-bg rounded-full transition-all duration-300"
              style={{ width: `${progress}%` }}
            />
          </div>
          <p className="text-cream-bg/70 text-sm mt-3 font-medium">{Math.round(progress)}%</p>
        </div>

        {/* Loading dots */}
        <div className="flex justify-center gap-2">
          <div className="w-3 h-3 rounded-full bg-cream-bg/60 animate-bounce" style={{ animationDelay: "0s" }} />
          <div className="w-3 h-3 rounded-full bg-cream-bg/60 animate-bounce" style={{ animationDelay: "0.2s" }} />
          <div className="w-3 h-3 rounded-full bg-cream-bg/60 animate-bounce" style={{ animationDelay: "0.4s" }} />
        </div>
      </div>
    </div>
  )
}
