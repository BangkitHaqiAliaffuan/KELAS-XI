import { Globe, Menu, X } from "lucide-react";
import { Button } from "@/components/ui/button";
import logo from '../../../public/logo.png'
type Language = "id" | "en";

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
  const nextLanguage = language === "id" ? "en" : "id";
  const switchLabel = language === "id" ? "Switch to English" : "Ganti ke Bahasa Indonesia";
  const languageLabel = nextLanguage === "id" ? "Indonesia" : "English";

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

      <div className="flex items-center gap-2">
        <Button
          variant="ghost"
          className="text-muted-foreground hover:text-foreground gap-2 px-2"
          onClick={onToggleLanguage}
          title={switchLabel}
          aria-label={switchLabel}
        >
          <Globe className="h-5 w-5" />
          {nextLanguage === "id" ? (
            <span className="inline-flex h-3 w-4 overflow-hidden rounded-sm border border-border">
              <span className="h-1/2 w-full bg-red-500" />
              <span className="h-1/2 w-full bg-white" />
            </span>
          ) : (
            <span className="inline-flex h-3 w-4 overflow-hidden rounded-sm border border-border">
              <span className="h-1/3 w-full bg-blue-600" />
              <span className="h-1/3 w-full bg-white" />
              <span className="h-1/3 w-full bg-red-500" />
            </span>
          )}
          <span className="text-[11px] font-semibold tracking-wide text-foreground">
            {languageLabel}
          </span>
        </Button>
      </div>
    </header>
  );
};

export default TopNav;
