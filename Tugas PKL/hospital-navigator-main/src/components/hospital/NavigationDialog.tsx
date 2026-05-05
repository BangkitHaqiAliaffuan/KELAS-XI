import { useState, useRef, useEffect, useCallback, useMemo } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Camera, RefreshCcw, MapPin, Loader2, Search } from "lucide-react";
import { roomInfoBySvgId } from "@/data/hospitalRoomInfo";
import jsQR from "jsqr";
import { resolveQrAnchor, resolveRoomIdFromQrCode, QR_ANCHOR_REGISTRY } from "@/data/hospitalRouteGraph";

interface NavigationDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  defaultMode?: "manual" | "qr";
  defaultDestinationRoomId?: string | null;
  language: "id" | "en";
  onConfirmNavigation?: (payload: {
    roomId: string;
    destinationRoomId: string;
    source: "manual" | "qr";
    qrPayload?: string;
  }) => void;
}

const NavigationDialog = ({
  open,
  onOpenChange,
  defaultMode = "manual",
  defaultDestinationRoomId = null,
  language,
  onConfirmNavigation,
}: NavigationDialogProps) => {
  const copy = useMemo(
    () =>
      language === "id"
        ? {
            startTitle: "Atur Titik Awal",
            destinationTitle: "Atur Tujuan",
            startDescription: "Pindai QR code terdekat atau pilih lokasi Anda secara manual.",
            destinationDescription: "Pilih ruangan tujuan untuk mulai menampilkan rute navigasi.",
            initializingCamera: "Menyiapkan kamera...",
            cameraInactive: "Kamera tidak aktif",
            enableCamera: "Aktifkan Kamera",
            switchManual: "Pilih Manual",
            orSelect: "Atau pilih manual",
            startingLocation: "Lokasi Awal",
            destinationLocation: "Lokasi Tujuan",
            whereNow: "Di mana posisi Anda?",
            whereTo: "Ke mana tujuan Anda?",
            searchDestination: "Cari tujuan, contoh: IGD, Lab, Farmasi...",
            suggestions: "Saran",
            noResults: "Tidak ada tujuan yang cocok.",
            next: "Lanjut",
            back: "Kembali",
            startNavigating: "Mulai Navigasi",
            scanHint: "Arahkan kamera ke QR code...",
            startingCamera: "Menyalakan kamera...",
            qrUnknown: (payload: string) => `QR tidak dikenali: ${payload}`,
            qrDetected: (payload: string, roomName: string) => `QR terdeteksi: ${payload} -> ${roomName}`,
            cameraError: "Tidak dapat mengakses kamera. Pastikan izin kamera sudah diberikan di pengaturan browser.",
            sameLocation: "Tujuan harus berbeda dari titik awal.",
          }
        : {
            startTitle: "Set Your Starting Point",
            destinationTitle: "Set Your Destination",
            startDescription: "Scan a QR code nearby or select your current location manually.",
            destinationDescription: "Choose your destination room to start showing the route.",
            initializingCamera: "Initializing camera...",
            cameraInactive: "Camera is inactive",
            enableCamera: "Enable Camera",
            switchManual: "Switch to Manual",
            orSelect: "Or select manually",
            startingLocation: "Starting Location",
            destinationLocation: "Destination",
            whereNow: "Where are you now?",
            whereTo: "Where do you want to go?",
            searchDestination: "Search destination, e.g. ER, Lab, Pharmacy...",
            suggestions: "Suggestions",
            noResults: "No matching destination found.",
            next: "Next",
            back: "Back",
            startNavigating: "Start Navigating",
            scanHint: "Point the camera at a QR code...",
            startingCamera: "Starting camera...",
            qrUnknown: (payload: string) => `QR not recognized: ${payload}`,
            qrDetected: (payload: string, roomName: string) => `QR detected: ${payload} -> ${roomName}`,
            cameraError: "Could not access camera. Please ensure you have given permission in your browser settings.",
            sameLocation: "Destination must be different from the starting point.",
          },
    [language],
  );

  const [step, setStep] = useState<"start" | "destination">("start");
  const [startLocation, setStartLocation] = useState("");
  const [destinationLocation, setDestinationLocation] = useState(defaultDestinationRoomId || "");
  const [destinationQuery, setDestinationQuery] = useState("");
  const [startQuery, setStartQuery] = useState("");
  const [isDestinationSearchOpen, setIsDestinationSearchOpen] = useState(false);
  const [isStartSearchOpen, setIsStartSearchOpen] = useState(false);
  const [highlightedDestinationIndex, setHighlightedDestinationIndex] = useState(-1);
  const [highlightedStartIndex, setHighlightedStartIndex] = useState(-1);
  const [startSource, setStartSource] = useState<"manual" | "qr">("manual");
  const [detectedQrPayload, setDetectedQrPayload] = useState<string | undefined>();
  const [isCameraActive, setIsCameraActive] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [qrScanStatus, setQrScanStatus] = useState("");
  const videoRef = useRef<HTMLVideoElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const streamRef = useRef<MediaStream | null>(null);
  const destinationSearchRef = useRef<HTMLDivElement>(null);
  const startSearchRef = useRef<HTMLDivElement>(null);
  const scanRafRef = useRef<number | null>(null);
  const lastDetectedPayloadRef = useRef<string | null>(null);
  const isHandlingDetectionRef = useRef(false);

  const roomOptions = Object.values(roomInfoBySvgId).sort((a, b) => a.name.localeCompare(b.name));
  
  const getFloorLabel = useCallback((roomId: string): string => {
    const anchor = Object.values(QR_ANCHOR_REGISTRY).find((a) => a.roomId === roomId);
    if (anchor) {
      if (anchor.floor === 0) return ' (Parkir L1)';
      if (anchor.floor === -1) return ' (Parkir L2)';
      if (anchor.floor === 2) return ' (Lantai 2)';
      if (anchor.floor === 1) return ' (Lantai 1)';
    }
    // Fallback: detect from room ID
    if (roomId.includes('Parking_Lantai_1')) return ' (Parkir L1)';
    if (roomId.includes('Parking_Lantai_2')) return ' (Parkir L2)';
    if (roomId.includes('Lantai_2') || roomId.startsWith('R._')) return ' (Lantai 2)';
    return ' (Lantai 1)';
  }, []);
  
  const destinationSearchOptions = roomOptions.filter((room) => {
    if (room.id === startLocation) return false;
    const query = destinationQuery.trim().toLowerCase();
    if (!query) return true;

    return (
      room.name.toLowerCase().includes(query) ||
      room.category.toLowerCase().includes(query) ||
      room.locationHint.toLowerCase().includes(query) ||
      room.description.toLowerCase().includes(query)
    );
  });

  const startSearchOptions = roomOptions.filter((room) => {
    const query = startQuery.trim().toLowerCase();
    if (!query) return true;

    return (
      room.name.toLowerCase().includes(query) ||
      room.category.toLowerCase().includes(query) ||
      room.locationHint.toLowerCase().includes(query) ||
      room.description.toLowerCase().includes(query)
    );
  });

  const selectStart = useCallback((roomId: string) => {
    const room = roomInfoBySvgId[roomId];
    setStartLocation(roomId);
    setStartQuery(room?.name || "");
    setIsStartSearchOpen(false);
    setHighlightedStartIndex(-1);
    setStartSource("manual");
    setDetectedQrPayload(undefined);
  }, []);

  const selectDestination = useCallback((roomId: string) => {
    const room = roomInfoBySvgId[roomId];
    setDestinationLocation(roomId);
    setDestinationQuery(room?.name || "");
    setIsDestinationSearchOpen(false);
    setHighlightedDestinationIndex(-1);
  }, []);

  const stopCamera = useCallback(() => {
    if (scanRafRef.current !== null) {
      cancelAnimationFrame(scanRafRef.current);
      scanRafRef.current = null;
    }

    if (streamRef.current) {
      streamRef.current.getTracks().forEach((track) => track.stop());
      streamRef.current = null;
    }
    setIsCameraActive(false);
    setIsLoading(false);
    isHandlingDetectionRef.current = false;
  }, []);

  const startCamera = async () => {
    setIsLoading(true);
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: {
          facingMode: "environment",
          width: { ideal: 1280 },
          height: { ideal: 720 },
        },
      });

      streamRef.current = stream;
      setIsCameraActive(true);
      setIsLoading(false);
      setQrScanStatus(copy.scanHint);
    } catch (err) {
      console.error("Error accessing camera:", err);
      setIsLoading(false);
      setIsCameraActive(false);
      alert(copy.cameraError);
    }
  };

  useEffect(() => {
    if (isCameraActive && streamRef.current && videoRef.current) {
      videoRef.current.srcObject = streamRef.current;
      videoRef.current.play().catch((err) => {
        console.error("Error playing video:", err);
      });
    }
  }, [isCameraActive]);

  const handleDetectedQrPayload = useCallback(
    (rawPayload: string) => {
      const normalizedPayload = rawPayload.trim();
      if (!normalizedPayload) return;
      if (isHandlingDetectionRef.current) return;
      if (lastDetectedPayloadRef.current === normalizedPayload) return;

      lastDetectedPayloadRef.current = normalizedPayload;

      const resolvedRoomId =
        resolveRoomIdFromQrCode(normalizedPayload) ||
        resolveQrAnchor(normalizedPayload)?.roomId ||
        null;

      if (!resolvedRoomId) {
        setQrScanStatus(copy.qrUnknown(normalizedPayload));
        return;
      }

      isHandlingDetectionRef.current = true;
      setStartLocation(resolvedRoomId);
      setStartSource("qr");
      setDetectedQrPayload(normalizedPayload);
      setQrScanStatus(copy.qrDetected(normalizedPayload, roomInfoBySvgId[resolvedRoomId]?.name || resolvedRoomId));
      stopCamera();
      setStep("destination");
    },
    [copy, stopCamera],
  );

  useEffect(() => {
    if (!isCameraActive) return;
    if (!videoRef.current || !canvasRef.current) return;

    const video = videoRef.current;
    const canvas = canvasRef.current;
    const ctx = canvas.getContext("2d", { willReadFrequently: true });
    if (!ctx) return;

    const scan = () => {
      if (!isCameraActive) return;

      if (video.readyState >= HTMLMediaElement.HAVE_CURRENT_DATA) {
        const width = video.videoWidth;
        const height = video.videoHeight;

        if (width > 0 && height > 0) {
          canvas.width = width;
          canvas.height = height;
          ctx.drawImage(video, 0, 0, width, height);

          const imageData = ctx.getImageData(0, 0, width, height);
          const result = jsQR(imageData.data, width, height, {
            inversionAttempts: "attemptBoth",
          });

          if (result?.data) {
            handleDetectedQrPayload(result.data);
          }
        }
      }

      scanRafRef.current = requestAnimationFrame(scan);
    };

    scanRafRef.current = requestAnimationFrame(scan);

    return () => {
      if (scanRafRef.current !== null) {
        cancelAnimationFrame(scanRafRef.current);
        scanRafRef.current = null;
      }
    };
  }, [isCameraActive, handleDetectedQrPayload]);

  useEffect(() => {
    if (!open) {
      stopCamera();
      setQrScanStatus("");
      lastDetectedPayloadRef.current = null;
      return;
    }

    setStep("start");
    setStartLocation("");
    setStartQuery("");
    setDestinationLocation(defaultDestinationRoomId || "");
    setDestinationQuery(defaultDestinationRoomId ? roomInfoBySvgId[defaultDestinationRoomId]?.name || "" : "");
    setIsDestinationSearchOpen(false);
    setIsStartSearchOpen(false);
    setHighlightedDestinationIndex(-1);
    setHighlightedStartIndex(-1);
    setStartSource("manual");
    setDetectedQrPayload(undefined);
  }, [open, defaultDestinationRoomId, stopCamera]);

  useEffect(() => {
    if (!destinationLocation) return;
    setDestinationQuery(roomInfoBySvgId[destinationLocation]?.name || "");
  }, [destinationLocation]);

  useEffect(() => {
    setHighlightedDestinationIndex(destinationSearchOptions.length ? 0 : -1);
  }, [destinationQuery, startLocation]);

  useEffect(() => {
    const handler = (event: MouseEvent) => {
      if (!destinationSearchRef.current?.contains(event.target as Node)) {
        setIsDestinationSearchOpen(false);
      }
      if (!startSearchRef.current?.contains(event.target as Node)) {
        setIsStartSearchOpen(false);
      }
    };

    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  useEffect(() => {
    setHighlightedStartIndex(startSearchOptions.length ? 0 : -1);
  }, [startQuery]);

  useEffect(() => {
    if (!startLocation) return;
    setStartQuery(roomInfoBySvgId[startLocation]?.name || "");
  }, [startLocation]);

  useEffect(() => {
    if (!open) return;
    if (step !== "start") return;
    if (defaultMode !== "qr") return;
    if (isCameraActive || isLoading) return;
    setQrScanStatus(copy.startingCamera);
    void startCamera();
  }, [copy, open, defaultMode, step, isCameraActive, isLoading]);

  const canStartNavigation =
    Boolean(startLocation) &&
    Boolean(destinationLocation) &&
    startLocation !== destinationLocation;

  const handleDestinationSearchKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "Escape") {
      event.preventDefault();
      setIsDestinationSearchOpen(false);
      return;
    }

    if (!isDestinationSearchOpen || destinationSearchOptions.length === 0) return;

    if (event.key === "ArrowDown") {
      event.preventDefault();
      setHighlightedDestinationIndex((current) =>
        current < destinationSearchOptions.length - 1 ? current + 1 : 0,
      );
      return;
    }

    if (event.key === "ArrowUp") {
      event.preventDefault();
      setHighlightedDestinationIndex((current) =>
        current > 0 ? current - 1 : destinationSearchOptions.length - 1,
      );
      return;
    }

    if (event.key === "Enter") {
      event.preventDefault();
      const selectedRoom = destinationSearchOptions[Math.max(0, highlightedDestinationIndex)];
      if (selectedRoom) {
        selectDestination(selectedRoom.id);
      }
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <MapPin className="h-5 w-5 text-primary" />
            {step === "start" ? copy.startTitle : copy.destinationTitle}
          </DialogTitle>
          <DialogDescription>
            {step === "start" ? copy.startDescription : copy.destinationDescription}
          </DialogDescription>
        </DialogHeader>

        {step === "start" ? (
          <div className="grid gap-6 py-4">
            <div className="flex flex-col items-center gap-3">
              <div className="relative w-full aspect-video bg-muted rounded-lg overflow-hidden border-2 border-dashed border-muted-foreground/20 flex items-center justify-center">
                {isCameraActive ? (
                  <video
                    ref={videoRef}
                    autoPlay
                    playsInline
                    muted
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <div className="flex flex-col items-center gap-2 text-muted-foreground text-center p-4">
                    {isLoading ? (
                      <Loader2 className="h-10 w-10 animate-spin opacity-40" />
                    ) : (
                      <Camera className="h-10 w-10 opacity-20" />
                    )}
                    <p className="text-sm">{isLoading ? copy.initializingCamera : copy.cameraInactive}</p>
                    {!isLoading && (
                      <Button variant="outline" size="sm" onClick={startCamera}>
                        {copy.enableCamera}
                      </Button>
                    )}
                  </div>
                )}

                {isCameraActive && !isLoading && (
                  <div className="absolute inset-0 border-[30px] border-black/40 pointer-events-none flex items-center justify-center">
                    <div className="w-40 h-40 border-2 border-primary rounded-lg shadow-[0_0_0_100vw_rgba(0,0,0,0.4)]" />
                  </div>
                )}
              </div>

              {isCameraActive && (
                <Button variant="ghost" size="sm" onClick={stopCamera} className="gap-2">
                  <RefreshCcw className="h-3.5 w-3.5" />
                  {copy.switchManual}
                </Button>
              )}

              {qrScanStatus && (
                <p className="text-[11px] text-muted-foreground text-center">
                  {qrScanStatus}
                </p>
              )}

              <canvas ref={canvasRef} className="hidden" aria-hidden="true" />
            </div>

            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <span className="w-full border-t" />
              </div>
              <div className="relative flex justify-center text-xs uppercase">
                <span className="bg-background px-2 text-muted-foreground">{copy.orSelect}</span>
              </div>
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium leading-none">
                {copy.startingLocation}
              </label>
              <div ref={startSearchRef} className="relative">
                <div className="flex items-center gap-2 rounded-md border border-input bg-background px-3 py-2 focus-within:ring-2 focus-within:ring-ring focus-within:ring-offset-2">
                  <Search className="h-4 w-4 shrink-0 text-muted-foreground" />
                  <input
                    value={startQuery}
                    placeholder={copy.whereNow}
                    onChange={(event) => {
                      setStartQuery(event.target.value);
                      setStartLocation("");
                      setIsStartSearchOpen(true);
                    }}
                    onFocus={() => setIsStartSearchOpen(true)}
                    onKeyDown={(event) => {
                      if (event.key === "ArrowDown") {
                        event.preventDefault();
                        setHighlightedStartIndex((prev) =>
                          prev < startSearchOptions.length - 1 ? prev + 1 : prev
                        );
                      } else if (event.key === "ArrowUp") {
                        event.preventDefault();
                        setHighlightedStartIndex((prev) => (prev > 0 ? prev - 1 : -1));
                      } else if (event.key === "Enter") {
                        event.preventDefault();
                        if (highlightedStartIndex >= 0 && startSearchOptions[highlightedStartIndex]) {
                          selectStart(startSearchOptions[highlightedStartIndex].id);
                        }
                      } else if (event.key === "Escape") {
                        setIsStartSearchOpen(false);
                      }
                    }}
                    className="h-5 flex-1 bg-transparent text-sm outline-none placeholder:text-muted-foreground"
                  />
                </div>

                {isStartSearchOpen && startSearchOptions.length > 0 && (
                  <div className="absolute left-0 right-0 top-full z-50 mt-2 max-h-64 overflow-y-auto rounded-md border border-border bg-popover text-popover-foreground shadow-lg">
                    <div className="border-b border-border bg-muted/30 px-3 py-2">
                      <p className="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">
                        {copy.suggestions}
                      </p>
                    </div>
                    {startSearchOptions.map((loc, index) => (
                      <button
                        key={loc.id}
                        type="button"
                        onClick={() => selectStart(loc.id)}
                        onMouseEnter={() => setHighlightedStartIndex(index)}
                        className={`flex w-full items-center justify-between gap-3 px-3 py-2.5 text-left text-sm transition-colors ${
                          index === highlightedStartIndex ? "bg-accent text-accent-foreground" : "hover:bg-accent"
                        }`}
                      >
                        <span className="min-w-0">
                          <span className="block truncate font-semibold">{loc.name}{getFloorLabel(loc.id)}</span>
                          <span className="block truncate text-xs text-muted-foreground">{loc.locationHint}</span>
                        </span>
                        <span className="shrink-0 rounded-full border border-primary/20 bg-primary/10 px-2 py-0.5 text-[10px] font-medium text-primary">
                          {loc.category}
                        </span>
                      </button>
                    ))}
                  </div>
                )}

                {isStartSearchOpen && startQuery.trim() && startSearchOptions.length === 0 && (
                  <div className="absolute left-0 right-0 top-full z-50 mt-2 rounded-md border border-border bg-popover p-4 text-center text-sm text-muted-foreground shadow-lg">
                    {copy.noResults}
                  </div>
                )}
              </div>
            </div>
          </div>
        ) : (
          <div className="grid gap-4 py-4">
            <div className="space-y-2">
              <label className="text-sm font-medium leading-none">
                {copy.destinationLocation}
              </label>
              <div ref={destinationSearchRef} className="relative">
                <div className="flex items-center gap-2 rounded-md border border-input bg-background px-3 py-2 focus-within:ring-2 focus-within:ring-ring focus-within:ring-offset-2">
                  <Search className="h-4 w-4 shrink-0 text-muted-foreground" />
                  <input
                    value={destinationQuery}
                    placeholder={copy.searchDestination}
                    onChange={(event) => {
                      setDestinationQuery(event.target.value);
                      setDestinationLocation("");
                      setIsDestinationSearchOpen(true);
                    }}
                    onFocus={() => setIsDestinationSearchOpen(true)}
                    onKeyDown={handleDestinationSearchKeyDown}
                    className="h-5 flex-1 bg-transparent text-sm outline-none placeholder:text-muted-foreground"
                  />
                </div>

                {isDestinationSearchOpen && destinationSearchOptions.length > 0 && (
                  <div className="absolute left-0 right-0 top-full z-50 mt-2 max-h-64 overflow-y-auto rounded-md border border-border bg-popover text-popover-foreground shadow-lg">
                    <div className="border-b border-border bg-muted/30 px-3 py-2">
                      <p className="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">
                        {copy.suggestions}
                      </p>
                    </div>
                    {destinationSearchOptions.map((loc, index) => (
                      <button
                        key={loc.id}
                        type="button"
                        onClick={() => selectDestination(loc.id)}
                        onMouseEnter={() => setHighlightedDestinationIndex(index)}
                        className={`flex w-full items-center justify-between gap-3 px-3 py-2.5 text-left text-sm transition-colors ${
                          index === highlightedDestinationIndex ? "bg-accent text-accent-foreground" : "hover:bg-accent"
                        }`}
                      >
                        <span className="min-w-0">
                          <span className="block truncate font-semibold">{loc.name}{getFloorLabel(loc.id)}</span>
                          <span className="block truncate text-xs text-muted-foreground">{loc.locationHint}</span>
                        </span>
                        <span className="shrink-0 rounded-full border border-primary/20 bg-primary/10 px-2 py-0.5 text-[10px] font-medium text-primary">
                          {loc.category}
                        </span>
                      </button>
                    ))}
                  </div>
                )}

                {isDestinationSearchOpen && destinationQuery.trim() && destinationSearchOptions.length === 0 && (
                  <div className="absolute left-0 right-0 top-full z-50 mt-2 rounded-md border border-border bg-popover p-4 text-center text-sm text-muted-foreground shadow-lg">
                    {copy.noResults}
                  </div>
                )}
              </div>
              {startLocation && destinationLocation === startLocation && (
                <p className="text-xs text-destructive">{copy.sameLocation}</p>
              )}
            </div>
          </div>
        )}

        <DialogFooter className="gap-2 sm:gap-2">
          {step === "destination" && (
            <Button
              variant="outline"
              onClick={() => {
                setStep("start");
                setDestinationLocation(defaultDestinationRoomId || "");
              }}
            >
              {copy.back}
            </Button>
          )}
          {step === "start" ? (
            <Button
              disabled={!startLocation}
              onClick={() => {
                stopCamera();
                setStep("destination");
              }}
              className="w-full"
            >
              {copy.next}
            </Button>
          ) : (
            <Button
              disabled={!canStartNavigation}
              onClick={() => {
                onConfirmNavigation?.({
                  roomId: startLocation,
                  destinationRoomId: destinationLocation,
                  source: startSource,
                  qrPayload: detectedQrPayload,
                });
                onOpenChange(false);
              }}
              className="w-full"
            >
              {copy.startNavigating}
            </Button>
          )}
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default NavigationDialog;
