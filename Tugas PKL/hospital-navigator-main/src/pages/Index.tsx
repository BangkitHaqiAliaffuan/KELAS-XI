import { useState, useCallback } from "react";
import TopNav from "@/components/hospital/TopNav";
import Sidebar from "@/components/hospital/Sidebar";
import SearchBar from "@/components/hospital/SearchBar";
import MapViewer from "@/components/hospital/MapViewer";
import NavigationDialog from "@/components/hospital/NavigationDialog";
import type { HospitalRoomInfo } from "@/data/hospitalRoomInfo";

type RoomHighlightCategory = "departments" | "facilities" | "emergency" | null;

const Index = () => {
  const [activeTab, setActiveTab] = useState("map");
  const [selectedLocation, setSelectedLocation] = useState<HospitalRoomInfo | null>(null);
  const [isNavDialogOpen, setIsNavDialogOpen] = useState(false);

  const highlightCategory: RoomHighlightCategory =
    activeTab === "departments" || activeTab === "facilities" || activeTab === "emergency"
      ? activeTab
      : null;

  const handleStartNavigation = () => {
    setIsNavDialogOpen(true);
  };

  const handleClearSelection = useCallback(() => {
    setSelectedLocation(null);
  }, []);

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
              <MapViewer 
                selectedLocation={selectedLocation} 
                onClearSelection={handleClearSelection}
                highlightCategory={highlightCategory}
              />
            </div>
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
