import { useState, useRef, useEffect, useCallback } from "react";
import { 
  Dialog, 
  DialogContent, 
  DialogHeader, 
  DialogTitle, 
  DialogDescription,
  DialogFooter
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { 
  Select, 
  SelectContent, 
  SelectItem, 
  SelectTrigger, 
  SelectValue 
} from "@/components/ui/select";
import { Camera, RefreshCcw, MapPin, Loader2 } from "lucide-react";
import { roomInfoBySvgId } from "@/data/hospitalRoomInfo";
import jsQR from "jsqr";
import { resolveQrAnchor, resolveRoomIdFromQrCode } from "@/data/hospitalRouteGraph";

interface NavigationDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  defaultMode?: "manual" | "qr";
  onConfirmStart?: (payload: {
    roomId: string;
    source: "manual" | "qr";
    qrPayload?: string;
  }) => void;
}

const NavigationDialog = ({
  open,
  onOpenChange,
  defaultMode = "manual",
  onConfirmStart,
}: NavigationDialogProps) => {
  const [startLocation, setStartLocation] = useState<string>("");
  const [isCameraActive, setIsCameraActive] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [qrScanStatus, setQrScanStatus] = useState("");
  const videoRef = useRef<HTMLVideoElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const streamRef = useRef<MediaStream | null>(null);
  const scanRafRef = useRef<number | null>(null);
  const lastDetectedPayloadRef = useRef<string | null>(null);
  const isHandlingDetectionRef = useRef(false);

  const roomOptions = Object.values(roomInfoBySvgId).sort((a, b) =>
    a.name.localeCompare(b.name),
  );

  const stopCamera = useCallback(() => {
    if (scanRafRef.current !== null) {
      cancelAnimationFrame(scanRafRef.current);
      scanRafRef.current = null;
    }

    if (streamRef.current) {
      streamRef.current.getTracks().forEach(track => {
        track.stop();
        console.log("Camera track stopped:", track.label);
      });
      streamRef.current = null;
    }
    setIsCameraActive(false);
    setIsLoading(false);
    isHandlingDetectionRef.current = false;
  }, []);

  const startCamera = async () => {
    setIsLoading(true);
    try {
      console.log("Requesting camera access...");
      const stream = await navigator.mediaDevices.getUserMedia({ 
        video: { 
          facingMode: "environment",
          width: { ideal: 1280 },
          height: { ideal: 720 }
        } 
      });
      
      console.log("Camera access granted, stream acquired.");
      streamRef.current = stream;
      setIsCameraActive(true);
      setIsLoading(false);
      setQrScanStatus("Arahkan kamera ke QR code...");
    } catch (err) {
      console.error("Error accessing camera:", err);
      setIsLoading(false);
      setIsCameraActive(false);
      alert("Could not access camera. Please ensure you have given permission in your browser settings.");
    }
  };

  // Effect to attach stream to video element when it becomes available in the DOM
  useEffect(() => {
    if (isCameraActive && streamRef.current && videoRef.current) {
      console.log("Attaching stream to video element.");
      videoRef.current.srcObject = streamRef.current;
      
      // Some browsers require explicit play() call
      videoRef.current.play().catch(err => {
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
        setQrScanStatus(`QR tidak dikenali: ${normalizedPayload}`);
        return;
      }

      isHandlingDetectionRef.current = true;
      setStartLocation(resolvedRoomId);
      setQrScanStatus(`QR terdeteksi: ${normalizedPayload} → ${roomInfoBySvgId[resolvedRoomId]?.name || resolvedRoomId}`);

      onConfirmStart?.({
        roomId: resolvedRoomId,
        source: "qr",
        qrPayload: normalizedPayload,
      });

      onOpenChange(false);
      stopCamera();
    },
    [onConfirmStart, onOpenChange, stopCamera],
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

  // Clean up when dialog closes
  useEffect(() => {
    if (!open) {
      stopCamera();
      setQrScanStatus("");
      lastDetectedPayloadRef.current = null;
    }
  }, [open, stopCamera]);

  useEffect(() => {
    if (!open) return;
    if (defaultMode !== "qr") return;
    if (isCameraActive || isLoading) return;
    setQrScanStatus("Menyalakan kamera...");
    void startCamera();
  }, [open, defaultMode, isCameraActive, isLoading]);

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <MapPin className="h-5 w-5 text-primary" />
            Set Your Starting Point
          </DialogTitle>
          <DialogDescription>
            Scan a QR code nearby or select your current location manually to start navigation.
          </DialogDescription>
        </DialogHeader>

        <div className="grid gap-6 py-4">
          {/* Camera/QR Scanner Section */}
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
                  <p className="text-sm">{isLoading ? "Initializing camera..." : "Camera is inactive"}</p>
                  {!isLoading && (
                    <Button variant="outline" size="sm" onClick={startCamera}>
                      Enable Camera
                    </Button>
                  )}
                </div>
              )}
              
              {isCameraActive && !isLoading && (
                <div className="absolute inset-0 border-[30px] border-black/40 pointer-events-none flex items-center justify-center">
                   <div className="w-40 h-40 border-2 border-primary rounded-lg shadow-[0_0_0: 100vw_rgba(0,0,0,0.4)]" />
                </div>
              )}
            </div>
            
            {isCameraActive && (
              <Button variant="ghost" size="sm" onClick={stopCamera} className="gap-2">
                <RefreshCcw className="h-3.5 w-3.5" />
                Switch to Manual
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
              <span className="bg-background px-2 text-muted-foreground">Or select manually</span>
            </div>
          </div>

          {/* Manual Dropdown Section */}
          <div className="space-y-2">
            <label className="text-sm font-medium leading-none">
              Starting Location
            </label>
            <Select value={startLocation} onValueChange={setStartLocation}>
              <SelectTrigger>
                <SelectValue placeholder="Where are you now?" />
              </SelectTrigger>
              <SelectContent>
                {roomOptions.map((loc) => (
                  <SelectItem key={loc.id} value={loc.id}>
                    {loc.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div>

        <DialogFooter>
          <Button 
            disabled={!startLocation} 
            onClick={() => {
              console.log("Starting navigation from:", startLocation);
              onConfirmStart?.({
                roomId: startLocation,
                source: "manual",
              });
              onOpenChange(false);
            }}
            className="w-full"
          >
            Start Navigating
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default NavigationDialog;
