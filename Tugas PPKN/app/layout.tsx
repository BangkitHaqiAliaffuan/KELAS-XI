import type React from "react"
import type { Metadata } from "next"
import { Geist, Geist_Mono } from "next/font/google"
import { Analytics } from "@vercel/analytics/next"
import "./globals.css"

const geistSans = Geist({ subsets: ["latin"] })
const geistMono = Geist_Mono({ subsets: ["latin"] })

export const metadata: Metadata = {
  title: "Keberagaman Kota Palembang",
  description: "Presentasi PPKn tentang keberagaman budaya Palembang",
  generator: "v0.app",
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html lang="id">
      <body style={{ fontFamily: geistSans.style.fontFamily }}>
        {children}
        <Analytics />
      </body>
    </html>
  )
}
