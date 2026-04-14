import { useState } from "react";
import Sidebar from "@/components/Sidebar";
import HeroSection from "@/components/HeroSection";
import WeatherCard from "@/components/WeatherCard";

const Index = () => {
  const [leftSidebarOpen, setLeftSidebarOpen] = useState(true);
  const [rightSidebarOpen, setRightSidebarOpen] = useState(true);

  return (
    <div className="flex min-h-screen">
      <Sidebar isOpen={leftSidebarOpen} onToggle={() => setLeftSidebarOpen(!leftSidebarOpen)} />
      <main
        className={`flex-1 transition-all duration-300 ${
          leftSidebarOpen ? "ml-56" : "ml-16"
        } ${
          rightSidebarOpen ? "mr-56" : "mr-0"
        }`}
      >
        <HeroSection />
      </main>
      
      {/* Right Sidebar - Cuaca */}
      {rightSidebarOpen && (
        <aside
          className="fixed right-0 top-0 z-40 h-screen w-56 border-l border-emerald-100/60 bg-white/60 backdrop-blur-xl transition-all duration-300"
        >
          <div className="relative h-full">
            <WeatherCard
              variant="sidebar"
              isOpen={rightSidebarOpen}
              onToggle={() => setRightSidebarOpen(false)}
            />
          </div>
        </aside>
      )}
      
      {/* Toggle Button - Muncul saat sidebar tertutup */}
      {!rightSidebarOpen && (
        <button
          onClick={() => setRightSidebarOpen(true)}
          className="fixed right-4 top-1/2 -translate-y-1/2 z-[60] flex h-12 w-12 items-center justify-center rounded-full border-2 border-emerald-300 bg-white shadow-xl transition-all hover:bg-emerald-50 hover:shadow-2xl hover:scale-110 group"
          aria-label="Buka cuaca"
          title="Buka cuaca"
        >
          <svg 
            className="h-6 w-6 text-emerald-600 transition-transform group-hover:scale-110" 
            fill="none" 
            viewBox="0 0 24 24" 
            stroke="currentColor" 
            strokeWidth={2.5}
          >
            <path strokeLinecap="round" strokeLinejoin="round" d="M3 15a4 4 0 004 4h9a5 5 0 10-.1-9.999 5.002 5.002 0 10-9.78 2.096A4.001 4.001 0 003 15z" />
          </svg>
        </button>
      )}
    </div>
  );
};

export default Index;
