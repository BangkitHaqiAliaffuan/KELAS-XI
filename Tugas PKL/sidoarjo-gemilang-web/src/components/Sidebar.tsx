import { Layers, MapPin, BarChart3, Globe, Users, Monitor, BarChart2, ChevronLeft, ChevronRight } from "lucide-react";
import { FaFacebookF, FaInstagram, FaYoutube } from "react-icons/fa";
import { FaXTwitter } from "react-icons/fa6";
import { useLanguage } from "@/i18n";
import { TypewriterText } from "@/components/ui/typewriter-text";

type SidebarProps = {
  isOpen: boolean;
  onToggle: () => void;
};

const Sidebar = ({ isOpen, onToggle }: SidebarProps) => {
  const { t } = useLanguage();

  const navItems = [
    { icon: Layers, label: t('sidebar.government') },
    { icon: MapPin, label: t('sidebar.webgis') },
    { icon: BarChart3, label: t('sidebar.oneData') },
    { icon: Globe, label: t('sidebar.smartCity') },
    { icon: Users, label: t('sidebar.publicServices') },
    { icon: Monitor, label: t('sidebar.cctv') },
    { icon: BarChart2, label: t('sidebar.transparency') },
  ];

  const socialItems = [
    { icon: FaFacebookF, label: "FB" },
    { icon: FaInstagram, label: "IG" },
    { icon: FaXTwitter, label: "X" },
    { icon: FaYoutube, label: "YT" },
  ];

  return (
    <aside 
      className={`fixed left-0 top-0 z-40 flex h-screen flex-col items-center justify-between border-r border-emerald-100 bg-gradient-to-b from-emerald-50 via-white to-green-50/70 py-6 shadow-[6px_0_24px_-18px_rgba(22,163,74,0.45)] transition-all duration-300 ${
        isOpen ? "w-56" : "w-16"
      }`}
    >
      {/* Toggle Button */}
      <button
        onClick={onToggle}
        className="absolute -right-4 top-6 z-50 flex h-8 w-8 items-center justify-center rounded-full border border-emerald-200 bg-white shadow-md transition-all hover:bg-emerald-50 hover:shadow-lg"
        aria-label={isOpen ? "Tutup sidebar" : "Buka sidebar"}
      >
        {isOpen ? (
          <ChevronLeft className="h-4 w-4 text-emerald-700" />
        ) : (
          <ChevronRight className="h-4 w-4 text-emerald-700" />
        )}
      </button>

      <div className="flex flex-col items-center gap-3">
        <img
          src="/images/sidoarjo-logo.png"
          alt="Logo Sidoarjo"
          width={56}
          height={56}
          className="mb-2 rounded-xl bg-emerald-100/60 p-1 ring-1 ring-emerald-200/80"
        />
        
        {isOpen && (
          <nav className="flex w-full flex-col items-center gap-2 px-3">
            {navItems.map((item) => (
              <button
                key={item.label}
                className="group flex h-24 w-full flex-col items-center justify-center gap-1 rounded-xl border border-emerald-200/80 bg-white/60 px-2 py-1 text-emerald-700/80 transition-all duration-200 hover:border-emerald-300 hover:bg-emerald-100/80 hover:text-emerald-800 hover:shadow-sm"
              >
                <item.icon className="h-8 w-8 shrink-0 drop-shadow-[0_1px_2px_rgba(22,163,74,0.2)]" />
                <span className="flex min-h-[2.25rem] w-full items-center justify-center text-center text-[12px] font-semibold leading-tight whitespace-pre-line">
                  {item.label}
                </span>
              </button>
            ))}
          </nav>
        )}

        {!isOpen && (
          <nav className="flex w-full flex-col items-center gap-2 px-2">
            {navItems.map((item) => (
              <button
                key={item.label}
                className="group flex h-12 w-full items-center justify-center rounded-xl border border-emerald-200/80 bg-white/60 p-2 text-emerald-700/80 transition-all duration-200 hover:border-emerald-300 hover:bg-emerald-100/80 hover:text-emerald-800 hover:shadow-sm"
                title={item.label.replace('\n', ' ')}
              >
                <item.icon className="h-6 w-6 shrink-0 drop-shadow-[0_1px_2px_rgba(22,163,74,0.2)]" />
              </button>
            ))}
          </nav>
        )}
      </div>

      {isOpen && (
        <div className="flex w-full flex-wrap items-center justify-center gap-2 px-3 text-emerald-700/80">
          {socialItems.map((item) => (
            <a
              key={item.label}
              href="#"
              className="flex min-w-[3rem] items-center justify-center gap-1.5 rounded-lg border border-transparent px-2 py-1 text-xs font-medium transition-all hover:border-emerald-200 hover:bg-emerald-100/70 hover:text-emerald-900"
            >
              <item.icon className="h-3.5 w-3.5" />
              <span>{item.label}</span>
            </a>
          ))}
        </div>
      )}
    </aside>
  );
};

export default Sidebar;
