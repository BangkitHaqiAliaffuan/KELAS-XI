import { Share2, Navigation } from "lucide-react";
import { Button } from "@/components/ui/button";
import type { HospitalRoomInfo } from "@/data/hospitalRoomInfo";

interface LocationInfoCardProps {
  location: HospitalRoomInfo | null;
}

const categoryIcons: Record<string, string> = {
  Emergency: "🚑",
  Outpatient: "🏥",
  "Critical Care": "❤️‍🩹",
  Diagnostic: "🔬",
  Facility: "🔧",
  Service: "ℹ️",
  Administration: "📋",
  Surgery: "🔪",
  Room: "🚪",
};

const LocationInfoCard = ({ location }: LocationInfoCardProps) => {
  if (!location) return null;

  return (
    <div className="absolute bottom-4 left-4 right-20 z-10">
      <div className="bg-card rounded-2xl shadow-lg border border-border p-4 flex items-center gap-4 max-w-2xl mx-auto">
        <div className="h-12 w-12 rounded-xl bg-hospital-gold-light flex items-center justify-center text-2xl shrink-0">
          {categoryIcons[location.category] || "📍"}
        </div>

        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-0.5">
            <span className="text-xs font-bold uppercase tracking-wide px-2 py-0.5 rounded bg-hospital-gold text-accent-foreground">
              {location.category}
            </span>
          </div>
          <h3 className="font-bold text-foreground text-lg leading-tight truncate">{location.name}</h3>
          <p className="text-sm text-muted-foreground">
            📍 {location.locationHint}
          </p>
          <p className="text-xs text-muted-foreground mt-0.5 line-clamp-2">{location.description}</p>
        </div>

        <div className="flex items-center gap-2 shrink-0">
          <Button variant="outline" size="sm" className="gap-1.5 border-border text-foreground">
            <Share2 className="h-4 w-4" />
            Share
          </Button>
          <Button size="sm" className="gap-1.5 bg-primary text-primary-foreground hover:bg-primary/90">
            <Navigation className="h-4 w-4" />
            Navigate Here
          </Button>
        </div>
      </div>
    </div>
  );
};

export default LocationInfoCard;
