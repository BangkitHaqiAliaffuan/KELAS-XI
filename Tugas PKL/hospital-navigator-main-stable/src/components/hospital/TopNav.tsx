import { Globe, Menu, User, X } from "lucide-react";
import { Button } from "@/components/ui/button";
import logo from '../../../public/logo.png'
const tabs = ["Map", "Scan QR Code", "Emergency"];

interface TopNavProps {
  activeTab: string;
  onTabChange: (tab: string) => void;
  onStartNavigation?: () => void;
  isSidebarOpen?: boolean;
  onToggleSidebar?: () => void;
}

const TopNav = ({ activeTab, onTabChange, onStartNavigation, isSidebarOpen, onToggleSidebar }: TopNavProps) => {
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
        <span className="font-bold text-foreground text-lg hidden sm:block">Hospital Navigator</span>
      </div>

      <nav className="hidden md:flex items-center gap-1">
        {tabs.map((tab) => {
          const key = tab.toLowerCase().replace(/\s/g, "");
          const isActive = activeTab === key;
          
          const handleClick = () => {
            if (tab === "Scan QR Code" && onStartNavigation) {
              onStartNavigation();
            } else {
              onTabChange(key);
            }
          };

          return (
            <button
              key={key}
              onClick={handleClick}
              className={`px-4 py-2 text-sm font-medium rounded-md transition-colors
                ${isActive ? "text-primary" : "text-muted-foreground hover:text-foreground"}`}
            >
              {tab}
            </button>
          );
        })}
      </nav>

      <div className="flex items-center gap-2">
        <Button variant="ghost" size="icon" className="text-muted-foreground hover:text-foreground">
          <Globe className="h-5 w-5" />
        </Button>
        <div className="h-9 w-9 rounded-full bg-primary/10 flex items-center justify-center">
          <User className="h-5 w-5 text-primary" />
        </div>
      </div>
    </header>
  );
};

export default TopNav;
