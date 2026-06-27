'use client';

import { useRef, useState, useEffect } from 'react';

interface CameraCaptureProps {
  onCapture: (imageData: string) => void;
  isAnalyzing: boolean;
  triggerCaptureSignal?: number;
  autoStartOnTrigger?: boolean;
  lastWakeAt?: number;
}

export default function CameraCapture({ onCapture, isAnalyzing, triggerCaptureSignal, autoStartOnTrigger = true, lastWakeAt }: CameraCaptureProps) {
  const videoRef = useRef<HTMLVideoElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [isCameraActive, setIsCameraActive] = useState(false);
  const [error, setError] = useState<string>('');
  const streamRef = useRef<MediaStream | null>(null);
  const [showWakePulse, setShowWakePulse] = useState(false);

  const startCamera = async () => {
    try {
      setError('');
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: 'environment', width: 1280, height: 720 },
        audio: false,
      });

      if (videoRef.current) {
        videoRef.current.srcObject = stream;
        streamRef.current = stream;
        setIsCameraActive(true);
      }
    } catch (err) {
      console.error('Camera error:', err);
      setError('Unable to access camera. Please grant camera permissions.');
    }
  };

  const stopCamera = () => {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach((track) => track.stop());
      streamRef.current = null;
    }
    if (videoRef.current) {
      videoRef.current.srcObject = null;
    }
    setIsCameraActive(false);
  };

  const captureImage = () => {
    if (!videoRef.current || !canvasRef.current) return;

    const video = videoRef.current;
    const canvas = canvasRef.current;
    const context = canvas.getContext('2d');

    if (!context) return;

    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    context.drawImage(video, 0, 0);

    const imageData = canvas.toDataURL('image/jpeg', 0.8);
    onCapture(imageData);
  };

  // If parent toggles `triggerCaptureSignal`, capture an image.
  useEffect(() => {
    if (typeof triggerCaptureSignal === 'undefined') return;

    let mounted = true;

    const doCapture = async () => {
      try {
        if (!isCameraActive) {
          if (autoStartOnTrigger) {
            await startCamera();
            // wait a short moment for the camera to initialize
            await new Promise((res) => setTimeout(res, 600));
            if (!mounted) return;
            captureImage();
          }
        } else {
          captureImage();
        }
      } catch (err) {
        console.error('Trigger capture error:', err);
      }
    };

    doCapture();

    return () => {
      mounted = false;
    };
  }, [triggerCaptureSignal]);

  // Pulse when lastWakeAt updates
  useEffect(() => {
    if (typeof (arguments as any) === 'undefined') return;
  }, []);


  useEffect(() => {
    return () => {
      stopCamera();
    };
  }, []);

  // Show a short pulse UI when a wake event occurs (lastWakeAt updated)
  useEffect(() => {
    if (!lastWakeAt) return;
    setShowWakePulse(true);
    const t = setTimeout(() => setShowWakePulse(false), 1200);
    return () => clearTimeout(t);
  }, [lastWakeAt]);

  return (
    <div className="w-full max-w-2xl mx-auto">
      <div className="relative bg-gray-900 rounded-lg overflow-hidden shadow-xl">
        <video
          ref={videoRef}
          autoPlay
          playsInline
          muted
          className="w-full h-auto"
          style={{ display: isCameraActive ? 'block' : 'none' }}
        />
        
        {!isCameraActive && (
          <div className="aspect-video flex items-center justify-center bg-gray-800">
            <div className="text-center p-8">
              <svg
                className="w-16 h-16 mx-auto mb-4 text-gray-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"
                />
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M15 13a3 3 0 11-6 0 3 3 0 016 0z"
                />
              </svg>
              <p className="text-gray-400">Camera not active</p>
            </div>
          </div>
        )}

        <canvas ref={canvasRef} className="hidden" />

        {showWakePulse && (
          <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
            <div className="w-40 h-40 rounded-full bg-white/30 animate-pulse-shadow" />
            <style>{`@keyframes pulse-shadow {0%{transform:scale(0.6);opacity:0.9}70%{transform:scale(1);opacity:0.15}100%{transform:scale(1.2);opacity:0}} .animate-pulse-shadow{animation:pulse-shadow 1.1s ease-out;}`}</style>
          </div>
        )}
      </div>

      {error && (
        <div className="mt-4 p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-red-800 text-sm">{error}</p>
          <p className="text-red-600 text-xs mt-2">
            Make sure you're using HTTPS or localhost and have granted camera permissions.
          </p>
        </div>
      )}

      <div className="mt-6 flex gap-4 justify-center">
        {!isCameraActive ? (
          <button
            onClick={startCamera}
            data-start-camera
            className="px-6 py-3 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-colors shadow-lg"
          >
            Start Camera
          </button>
        ) : (
          <>
            <button
              onClick={captureImage}
              disabled={isAnalyzing}
              data-capture-btn
              className="px-6 py-3 bg-green-600 text-white rounded-lg font-medium hover:bg-green-700 transition-colors shadow-lg disabled:bg-gray-400 disabled:cursor-not-allowed"
            >
              {isAnalyzing ? 'Analyzing...' : 'Capture & Analyze'}
            </button>
            <button
              onClick={stopCamera}
              disabled={isAnalyzing}
              data-stop-camera
              className="px-6 py-3 bg-red-600 text-white rounded-lg font-medium hover:bg-red-700 transition-colors shadow-lg disabled:bg-gray-400 disabled:cursor-not-allowed"
            >
              Stop Camera
            </button>
          </>
        )}
      </div>
    </div>
  );
}
