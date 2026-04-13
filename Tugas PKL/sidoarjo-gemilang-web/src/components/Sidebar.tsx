import { Layers, MapPin, BarChart3, Globe, Users, Monitor, BarChart2 } from "lucide-react";

const navItems = [
  { icon: Layers, label: "PEMERINTAHAN" },
  { icon: MapPin, label: "WEB GIS" },
  { icon: BarChart3, label: "SATU DATA\nSIDOARJO" },
  { icon: Globe, label: "SMART CITY" },
  { icon: Users, label: "LAYANAN\nPUBLIK" },
  { icon: Monitor, label: "CCTV" },
  { icon: BarChart2, label: "TRANSPARANSI" },
];

const Sidebar = () => {
  return (
    <aside className="fixed left-0 top-0 z-40 flex h-screen w-24 flex-col items-center justify-between border-r border-border bg-background py-6">
      <div className="flex flex-col items-center gap-2">
        <img
          src="/images/sidoarjo-logo.png"
          alt="Logo Sidoarjo"
          width={56}
          height={56}
          className="mb-4"
        />
        <nav className="flex flex-col items-center gap-1">
          {navItems.map((item) => (
            <button
              key={item.label}
              className="group flex w-20 flex-col items-center gap-1 rounded-lg p-2 text-muted-foreground transition-colors hover:bg-accent hover:text-accent-foreground"
            >
              <item.icon className="h-5 w-5" />
              <span className="text-center text-[9px] font-semibold leading-tight whitespace-pre-line">
                {item.label}
              </span>
            </button>
          ))}
        </nav>
      </div>

      <div className="flex flex-col items-center gap-3 text-muted-foreground">
        <a href="#" className="text-xs hover:text-primary transition-colors">FB</a>
        <a href="#" className="text-xs hover:text-primary transition-colors">IG</a>
        <a href="#" className="text-xs hover:text-primary transition-colors">X</a>
        <a href="#" className="text-xs hover:text-primary transition-colors">YT</a>
      </div>
    </aside>
  );
};

export default Sidebar;
