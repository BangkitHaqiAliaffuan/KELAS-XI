import { useState } from "react";
import TopNav from "@/components/hospital/TopNav";
import Sidebar from "@/components/hospital/Sidebar";
import SearchBar from "@/components/hospital/SearchBar";
import MapViewer from "@/components/hospital/MapViewer";
import LocationInfoCard from "@/components/hospital/LocationInfoCard";
import NavigationDialog from "@/components/hospital/NavigationDialog";
import type { HospitalLocation } from "@/data/hospitalLocations";

const Index = () => {
  const [activeTab, setActiveTab] = useState("map");
  const [selectedLocation, setSelectedLocation] = useState<HospitalLocation | null>(null);
  const [isNavDialogOpen, setIsNavDialogOpen] = useState(false);

  const handleStartNavigation = () => {
    setIsNavDialogOpen(true);
  };

  return (
    <div className="flex flex-col h-screen overflow-hidden bg-background">
      <TopNav 
        activeTab={activeTab} 
        onTabChange={setActiveTab} 
        onStartNavigation={handleStartNavigation}
      />

      <div className="flex flex-1 overflow-hidden">
        <Sidebar 
          activeTab={activeTab} 
          onTabChange={setActiveTab} 
          onStartNavigation={handleStartNavigation}
        />

        <main className="flex-1 flex flex-col overflow-hidden relative">
          {/* Search */}
          <div className="px-4 pt-4 pb-2 z-10">
            <SearchBar onSelectLocation={setSelectedLocation} />
          </div>

          {/* Map area */}
          <div className="flex-1 relative px-4 pb-4">
            <div className="h-full flex gap-3">
              <MapViewer selectedLocation={selectedLocation} />
            </div>

            <LocationInfoCard location={selectedLocation} />
          </div>
        </main>
      </div>

      <NavigationDialog 
        open={isNavDialogOpen} 
        onOpenChange={setIsNavDialogOpen} 
      />
    </div>
  );
};

export default Index;
