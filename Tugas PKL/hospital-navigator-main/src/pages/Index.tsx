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
  const [isMobileSidebarOpen, setIsMobileSidebarOpen] = useState(false);
  const [selectedLocation, setSelectedLocation] = useState<HospitalRoomInfo | null>(null);
  const [isNavDialogOpen, setIsNavDialogOpen] = useState(false);
  const [navDialogMode, setNavDialogMode] = useState<"manual" | "qr">("manual");
  const [navigationStartRequest, setNavigationStartRequest] = useState<{
    requestId: number;
    roomId: string;
    source: "manual" | "qr";
    qrPayload?: string;
  } | null>(null);
  const [navigationStartCounter, setNavigationStartCounter] = useState(0);

  const highlightCategory: RoomHighlightCategory =
    activeTab === "departments" || activeTab === "facilities" || activeTab === "emergency"
      ? activeTab
      : null;

  const handleStartNavigation = (options?: { mode?: "manual" | "qr" }) => {
    setNavDialogMode(options?.mode ?? "manual");
    setIsNavDialogOpen(true);
  };

  const handleConfirmNavigationStart = useCallback((payload: {
    roomId: string;
    source: "manual" | "qr";
    qrPayload?: string;
  }) => {
    const nextId = navigationStartCounter + 1;
    setNavigationStartCounter(nextId);
    setNavigationStartRequest({
      requestId: nextId,
      roomId: payload.roomId,
      source: payload.source,
      qrPayload: payload.qrPayload,
    });
    setIsNavDialogOpen(false);
  }, [navigationStartCounter]);

  const handleNavigationStartRequestHandled = useCallback((requestId: number) => {
    setNavigationStartRequest((current) => {
      if (!current) return current;
      return current.requestId === requestId ? null : current;
    });
  }, []);

  const handleClearSelection = useCallback(() => {
    setSelectedLocation(null);
  }, []);

  return (
    <div className="flex flex-col h-screen overflow-hidden bg-background">
      <TopNav 
        activeTab={activeTab} 
        onTabChange={setActiveTab} 
        onStartNavigation={handleStartNavigation}
        isSidebarOpen={isMobileSidebarOpen}
        onToggleSidebar={() => setIsMobileSidebarOpen((prev) => !prev)}
      />

      <div className="flex flex-1 overflow-hidden">
        <Sidebar 
          activeTab={activeTab} 
          onTabChange={setActiveTab} 
          onStartNavigation={handleStartNavigation}
          mobileOpen={isMobileSidebarOpen}
          onMobileOpenChange={setIsMobileSidebarOpen}
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
                onStartNavigation={handleStartNavigation}
                navigationStartRequest={navigationStartRequest}
                onNavigationStartRequestHandled={handleNavigationStartRequestHandled}
              />
            </div>
          </div>
        </main>
      </div>

      <NavigationDialog 
        open={isNavDialogOpen} 
        onOpenChange={setIsNavDialogOpen} 
        defaultMode={navDialogMode}
        onConfirmStart={handleConfirmNavigationStart}
      />
    </div>
  );
};

export default Index;
