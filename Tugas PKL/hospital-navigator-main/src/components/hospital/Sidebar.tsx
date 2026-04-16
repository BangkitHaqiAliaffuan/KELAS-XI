import { Map, Building2, Wrench, AlertTriangle, Navigation } from "lucide-react";
import { Button } from "@/components/ui/button";

interface SidebarProps {
  activeTab: string;
  onTabChange: (tab: string) => void;
  onStartNavigation?: () => void;
}

const navItems = [
  { id: "map", label: "Map", icon: Map },
  { id: "departments", label: "Departments", icon: Building2 },
  { id: "facilities", label: "Facilities", icon: Wrench },
  { id: "emergency", label: "Emergency", icon: AlertTriangle },
];

const Sidebar = ({ activeTab, onTabChange, onStartNavigation }: SidebarProps) => {
  return (
    <aside className="hidden lg:flex flex-col w-[240px] bg-card border-r border-border h-full">
      <div className="p-5 border-b border-border">
        <h2 className="text-lg font-bold text-primary">Clinical Concierge</h2>
        <p className="text-sm text-muted-foreground">Hospital Wayfinding</p>
      </div>

      <nav className="flex-1 py-4">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => onTabChange(item.id)}
              className={`flex items-center gap-3 w-full px-5 py-3 text-sm font-medium transition-colors relative
                ${isActive
                  ? "text-primary bg-primary/5"
                  : "text-muted-foreground hover:text-foreground hover:bg-muted"
                }`}
            >
              {isActive && (
                <div className="absolute left-0 top-1 bottom-1 w-[3px] bg-primary rounded-r-full" />
              )}
              <Icon className="h-5 w-5" />
              {item.label}
            </button>
          );
        })}
      </nav>

      <div className="p-4 border-t border-border">
        <Button 
          onClick={onStartNavigation}
          className="w-full gap-2 bg-primary text-primary-foreground hover:bg-primary/90 shadow-md"
        >
          <Navigation className="h-4 w-4" />
          Start Navigation
        </Button>
      </div>
    </aside>
  );
};

export default Sidebar;
