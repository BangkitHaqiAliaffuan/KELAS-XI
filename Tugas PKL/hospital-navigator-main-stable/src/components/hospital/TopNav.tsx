import { Globe, Menu, X } from "lucide-react";
import { Button } from "@/components/ui/button";
import logo from '../../../public/logo.png'

type Language = "id" | "en";

// Flag components with proper styling
const IndonesiaFlag = () => (
  <svg
    viewBox="0 0 24 16"
    className="h-4 w-6 rounded-sm border border-border shadow-sm"
    aria-label="Indonesia Flag"
  >
    <rect width="24" height="8" fill="#FF0000" />
    <rect y="8" width="24" height="8" fill="#FFFFFF" />
  </svg>
);

const UKFlag = () => (
  <svg
    viewBox="0 0 60 30"
    className="h-4 w-6 rounded-sm border border-border shadow-sm"
    aria-label="UK Flag"
  >
    {/* Blue background */}
    <rect width="60" height="30" fill="#012169" />
    
    {/* White diagonals */}
    <path d="M0,0 L60,30 M60,0 L0,30" stroke="#FFF" strokeWidth="6" />
    
    {/* Red diagonals */}
    <path d="M0,0 L60,30 M60,0 L0,30" stroke="#C8102E" strokeWidth="4" />
    
    {/* White cross */}
    <path d="M30,0 L30,30 M0,15 L60,15" stroke="#FFF" strokeWidth="10" />
    
    {/* Red cross */}
    <path d="M30,0 L30,30 M0,15 L60,15" stroke="#C8102E" strokeWidth="6" />
  </svg>
);

const tabs = [
  { id: "map", label: { id: "Peta", en: "Map" } },
  { id: "scanqrcode", label: { id: "Pindai QR Code", en: "Scan QR Code" } },
  { id: "emergency", label: { id: "Darurat", en: "Emergency" } },
];

interface TopNavProps {
  activeTab: string;
  onTabChange: (tab: string) => void;
  onStartNavigation?: () => void;
  isSidebarOpen?: boolean;
  onToggleSidebar?: () => void;
  language: Language;
  onToggleLanguage: () => void;
}

const TopNav = ({
  activeTab,
  onTabChange,
  onStartNavigation,
  isSidebarOpen,
  onToggleSidebar,
  language,
  onToggleLanguage,
}: TopNavProps) => {
  const title = language === "id" ? "Navigator Rumah Sakit" : "Hospital Navigator";

  const handleLanguageChange = (lang: Language) => {
    if (language !== lang) {
      onToggleLanguage();
    }
  };

  return (
    <header className="flex items-center justify-between px-6 py-3 bg-card border-b border-border">
      <div className="flex items-center gap-3">
        <Button
          variant="ghost"
          size="icon"
          className="lg:hidden text-muted-foreground hover:text-foreground"
          onClick={onToggleSidebar}
          aria-label={isSidebarOpen ? "Close sidebar" : "Open sidebar"}
        >
          {isSidebarOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
        </Button>
        <div className="h-9 w-9 rounded-lg bg-primary flex items-center justify-center">
          <img src={logo} alt="" />
        </div>
        <span className="font-bold text-foreground text-lg hidden sm:block">{title}</span>
      </div>

      <nav className="hidden md:flex items-center gap-1">
        {tabs.map((tab) => {
          const isActive = activeTab === tab.id;
          
          const handleClick = () => {
            if (tab.id === "scanqrcode" && onStartNavigation) {
              onStartNavigation();
            } else {
              onTabChange(tab.id);
            }
          };

          return (
            <button
              key={tab.id}
              onClick={handleClick}
              className={`px-4 py-2 text-sm font-medium rounded-md transition-colors
                ${isActive ? "text-primary" : "text-muted-foreground hover:text-foreground"}`}
            >
              {tab.label[language]}
            </button>
          );
        })}
      </nav>

      <div className="flex items-center gap-1">
        <Globe className="h-4 w-4 text-muted-foreground mr-1 hidden sm:block" />
        
        {/* Indonesian Button */}
        <Button
          variant="ghost"
          size="sm"
          className={`gap-2 px-2 sm:px-3 py-2 h-auto transition-all ${
            language === "id"
              ? "bg-primary/10 text-primary border border-primary/20 shadow-sm"
              : "text-muted-foreground hover:text-foreground hover:bg-accent"
          }`}
          onClick={() => handleLanguageChange("id")}
          title="Bahasa Indonesia"
          aria-label="Switch to Indonesian"
        >
          <IndonesiaFlag />
          <span className="text-xs font-semibold tracking-wide hidden sm:inline">
            ID
          </span>
        </Button>

        {/* English Button */}
        <Button
          variant="ghost"
          size="sm"
          className={`gap-2 px-2 sm:px-3 py-2 h-auto transition-all ${
            language === "en"
              ? "bg-primary/10 text-primary border border-primary/20 shadow-sm"
              : "text-muted-foreground hover:text-foreground hover:bg-accent"
          }`}
          onClick={() => handleLanguageChange("en")}
          title="English"
          aria-label="Switch to English"
        >
          <UKFlag />
          <span className="text-xs font-semibold tracking-wide hidden sm:inline">
            EN
          </span>
        </Button>
      </div>
    </header>
  );
};

export default TopNav;
