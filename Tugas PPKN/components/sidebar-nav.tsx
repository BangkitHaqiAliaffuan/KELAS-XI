"use client"

import { useState, useEffect } from "react"
import { motion, AnimatePresence } from "framer-motion"
import {
  Home,
  Utensils,
  Shirt,
  Music,
  Briefcase,
  Sparkles,
  Heart,
  BookOpen,
  MessageSquare,
  Menu,
  X,
} from "lucide-react"
import { cn } from "@/lib/utils"

const navItems = [
  { id: "home", label: "Home", icon: Home },
  { id: "makanan-khas", label: "Makanan Khas", icon: Utensils },
  { id: "pakaian-adat", label: "Pakaian Adat", icon: Shirt },
  { id: "kesenian-daerah", label: "Kesenian Daerah", icon: Music },
  { id: "pekerjaan-masyarakat", label: "Pekerjaan Masyarakat", icon: Briefcase },
  { id: "adat-unik", label: "Adat Unik", icon: Sparkles },
  { id: "nilai-budaya", label: "Nilai Budaya", icon: Heart },
  { id: "kesimpulan", label: "Kesimpulan", icon: BookOpen },
  { id: "refleksi-pernyataan", label: "Refleksi Pernyataan", icon: MessageSquare },
]

export function SidebarNav() {
  const [activeSection, setActiveSection] = useState("home")
  const [isOpen, setIsOpen] = useState(false)

  useEffect(() => {
    const handleScroll = () => {
      const sections = navItems.map((item) => document.getElementById(item.id))
      const scrollPosition = window.scrollY + 200

      for (let i = sections.length - 1; i >= 0; i--) {
        const section = sections[i]
        if (section && section.offsetTop <= scrollPosition) {
          setActiveSection(navItems[i].id)
          break
        }
      }
    }

    window.addEventListener("scroll", handleScroll)
    return () => window.removeEventListener("scroll", handleScroll)
  }, [])

  const scrollToSection = (id: string) => {
    document.getElementById(id)?.scrollIntoView({ behavior: "smooth" })
    setIsOpen(false)
  }

  return (
    <>
      {/* Mobile Menu Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="lg:hidden fixed top-4 left-4 z-50 bg-primary text-primary-foreground p-3 rounded-full shadow-lg hover:bg-primary/90 transition-colors"
      >
        {isOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
      </button>

      {/* Desktop Sidebar */}
      <motion.aside
        className="hidden lg:block fixed left-0 top-0 h-screen w-64 bg-primary/95 backdrop-blur-sm z-40 overflow-y-auto shadow-xl"
        initial={{ x: -100, opacity: 0 }}
        animate={{ x: 0, opacity: 1 }}
        transition={{ duration: 0.5 }}
      >
        <div className="p-6">
          <h2 className="font-serif text-2xl font-bold text-secondary mb-8 text-center">Navigasi</h2>
          <nav className="space-y-2">
            {navItems.map((item) => {
              const Icon = item.icon
              const isActive = activeSection === item.id

              return (
                <button
                  key={item.id}
                  onClick={() => scrollToSection(item.id)}
                  className={cn(
                    "w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-300 text-left group",
                    isActive
                      ? "bg-secondary text-secondary-foreground shadow-md"
                      : "text-primary-foreground hover:bg-primary-foreground/10",
                  )}
                >
                  <Icon
                    className={cn(
                      "h-5 w-5 transition-transform duration-300 group-hover:scale-110",
                      isActive ? "text-secondary-foreground" : "text-secondary",
                    )}
                  />
                  <span className="font-medium">{item.label}</span>
                  {isActive && (
                    <motion.div className="ml-auto h-2 w-2 rounded-full bg-accent" layoutId="activeIndicator" />
                  )}
                </button>
              )
            })}
          </nav>
        </div>
      </motion.aside>

      {/* Mobile Sidebar */}
      <AnimatePresence>
        {isOpen && (
          <>
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setIsOpen(false)}
              className="lg:hidden fixed inset-0 bg-black/50 z-40"
            />
            <motion.aside
              initial={{ x: "-100%" }}
              animate={{ x: 0 }}
              exit={{ x: "-100%" }}
              transition={{ type: "spring", damping: 25 }}
              className="lg:hidden fixed left-0 top-0 h-screen w-64 bg-primary z-50 overflow-y-auto shadow-xl"
            >
              <div className="p-6 pt-20">
                <h2 className="font-serif text-2xl font-bold text-secondary mb-8 text-center">Navigasi</h2>
                <nav className="space-y-2">
                  {navItems.map((item) => {
                    const Icon = item.icon
                    const isActive = activeSection === item.id

                    return (
                      <button
                        key={item.id}
                        onClick={() => scrollToSection(item.id)}
                        className={cn(
                          "w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-300 text-left",
                          isActive
                            ? "bg-secondary text-secondary-foreground shadow-md"
                            : "text-primary-foreground hover:bg-primary-foreground/10",
                        )}
                      >
                        <Icon className={cn("h-5 w-5", isActive ? "text-secondary-foreground" : "text-secondary")} />
                        <span className="font-medium">{item.label}</span>
                      </button>
                    )
                  })}
                </nav>
              </div>
            </motion.aside>
          </>
        )}
      </AnimatePresence>
    </>
  )
}
