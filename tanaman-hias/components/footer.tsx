"use client"

import { Instagram, Music } from "lucide-react"

export default function Footer() {
  return (
    <footer className="relative bg-primary-green text-white py-12 px-4 md:px-8">
      <div className="absolute inset-0 opacity-5">
        <div
          className="absolute inset-0"
          style={{
            backgroundImage: "radial-gradient(circle, #ffffff 1px, transparent 1px)",
            backgroundSize: "30px 30px",
          }}
        />
      </div>

      <div className="relative z-10 max-w-7xl mx-auto">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-8">
          {/* About Section */}
          <div>
            <h3 className="boldonse-regular text-2xl font-bold mb-4">Taman Hias XI-RPL</h3>
            <p className="text-cream-bg/80 leading-relaxed">
              Dokumentasi interaktif dan indah tentang membuat taman hias inspiratif dengan tanaman hias pilihan dari kelas XI-RPL.
            </p>
          </div>

          {/* Quick Links */}
          <div>
            <h3 className="font-bold text-lg mb-4">Navigasi</h3>
            <ul className="space-y-2">
              <li>
                <a href="#hero" className="text-cream-bg/80 hover:text-white transition-colors">
                  Beranda
                </a>
              </li>
              <li>
                <a href="#plants" className="text-cream-bg/80 hover:text-white transition-colors">
                  Tanaman Hias
                </a>
              </li>
              <li>
                <a href="#timeline" className="text-cream-bg/80 hover:text-white transition-colors">
                  Perjalanan
                </a>
              </li>
              <li>
                <a href="#gallery" className="text-cream-bg/80 hover:text-white transition-colors">
                  Galeri Foto
                </a>
              </li>
            </ul>
          </div>

          {/* Social Media */}
          <div>
            <h3 className="font-bold text-lg mb-4">Ikuti Kami</h3>
            <div className="space-y-4">
              <a
                href="https://tiktok.com/@codersgacoor"
                target="_blank"
                rel="noopener noreferrer"
                className="flex items-center gap-3 text-cream-bg/80 hover:text-white transition-colors group"
              >
                <div className="w-10 h-10 bg-white/10 rounded-full flex items-center justify-center group-hover:bg-white/20 transition-colors">
                  <Music className="w-5 h-5" />
                </div>
                <div>
                  <p className="text-sm font-semibold">TikTok</p>
                  <p className="text-xs">@codersgacoor</p>
                </div>
              </a>
              <a
                href="https://instagram.com/xixixierpeel"
                target="_blank"
                rel="noopener noreferrer"
                className="flex items-center gap-3 text-cream-bg/80 hover:text-white transition-colors group"
              >
                <div className="w-10 h-10 bg-white/10 rounded-full flex items-center justify-center group-hover:bg-white/20 transition-colors">
                  <Instagram className="w-5 h-5" />
                </div>
                <div>
                  <p className="text-sm font-semibold">Instagram</p>
                  <p className="text-xs">@xixixierpeel</p>
                </div>
              </a>
            </div>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="pt-8 border-t border-white/20 text-center">
          <p className="text-cream-bg/60 text-sm">
            © {new Date().getFullYear()} Taman Hias XI-RPL. Dibuat dengan ❤️ untuk generasi hijau.
          </p>
        </div>
      </div>
    </footer>
  )
}
