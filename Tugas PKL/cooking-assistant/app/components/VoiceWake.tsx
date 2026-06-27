'use client';

import { useEffect, useRef, useState } from 'react';

interface VoiceWakeProps {
  onWake: () => void;
  wakePhrase?: string;
  autoStart?: boolean;
  onToggleAutoStart?: (v: boolean) => void;
}

export default function VoiceWake({ 
  onWake, 
  wakePhrase = 'hey chef', 
  autoStart = true, 
  onToggleAutoStart 
}: VoiceWakeProps) {
  const [listening, setListening] = useState(false);
  const [supported, setSupported] = useState(true);
  const [statusText, setStatusText] = useState('Idle');
  const [micPermission, setMicPermission] = useState<'unknown' | 'granted' | 'denied'>('unknown');
  const [transcriptPreview, setTranscriptPreview] = useState('');
  const recognitionRef = useRef<any>(null);
  const lastTriggeredRef = useRef<number>(0);

  useEffect(() => {
    const win = typeof window !== 'undefined' ? window : undefined;
    const SpeechRecognition = win && (win.SpeechRecognition || (win as any).webkitSpeechRecognition);
    
    if (!SpeechRecognition) {
      setSupported(false);
      return;
    }

    const recognition = new SpeechRecognition();
    recognition.continuous = true;
    recognition.interimResults = true;
    recognition.lang = 'en-US';

    recognition.onstart = () => {
      setStatusText('Listening');
      setListening(true);
    };

    recognition.onend = () => {
      setStatusText('Stopped');
      setListening(false);
    };

    recognition.onerror = (e: any) => {
      console.error('Speech recognition error', e);
      setStatusText(`Error: ${e?.error || 'unknown'}`);
      setListening(false);
    };

    recognition.onresult = (event: any) => {
      let latest = '';
      for (let i = event.resultIndex; i < event.results.length; ++i) {
        latest += event.results[i][0].transcript;
      }

      setTranscriptPreview(latest);

      const normalized = latest.toLowerCase().replace(/[.,!?]/g, '').trim();
      if (normalized.includes(wakePhrase.toLowerCase())) {
        const now = Date.now();
        if (now - lastTriggeredRef.current > 2000) {
          lastTriggeredRef.current = now;
          try {
            onWake();
            setStatusText('Wake word detected');
          } catch (err) {
            console.error('onWake handler error', err);
          }
        }
      }
    };

    recognitionRef.current = recognition;

    return () => {
      try {
        recognition.stop();
      } catch (e) {
        // ignore
      }
      recognitionRef.current = null;
    };
  }, [onWake, wakePhrase]);

  const requestMic = async () => {
    try {
      await navigator.mediaDevices.getUserMedia({ audio: true });
      setMicPermission('granted');
      setStatusText('Microphone permission granted');
    } catch (e) {
      setMicPermission('denied');
      setStatusText('Microphone permission denied');
    }
  };

  const startListening = async () => {
    if (!recognitionRef.current) return;
    try {
      // Pre-warm permission prompt if needed
      if (micPermission !== 'granted') {
        try {
          await navigator.mediaDevices.getUserMedia({ audio: true });
          setMicPermission('granted');
        } catch (e) {
          setMicPermission('denied');
        }
      }
      recognitionRef.current.start();
    } catch (err) {
      console.error('Start listening failed', err);
      setStatusText('Failed to start');
      setListening(false);
    }
  };

  const stopListening = () => {
    if (!recognitionRef.current) return;
    try {
      recognitionRef.current.stop();
    } catch (err) {
      // ignore
    }
    setListening(false);
    setStatusText('Stopped');
  };

  if (!supported) {
    return (
      <div className="w-full max-w-2xl mx-auto mt-4 text-sm text-yellow-700 bg-yellow-50 border border-yellow-200 p-3 rounded">
        Voice wake-word not supported in this browser. Try Chrome or Edge.
      </div>
    );
  }

  return (
    <div className="w-full max-w-2xl mx-auto mt-4 flex flex-col items-center gap-3">
      <div className="flex items-center gap-3">
        <button
          onClick={() => (listening ? stopListening() : startListening())}
          className={`px-4 py-2 rounded-lg font-medium transition-colors flex items-center gap-3 ${
            listening ? 'bg-red-600 text-white hover:bg-red-700' : 'bg-green-600 text-white hover:bg-green-700'
          }`}
        >
          <span className={`w-3 h-3 rounded-full ${listening ? 'bg-red-300 animate-pulse' : 'bg-white/60'}`} />
          {listening ? 'Stop Listening' : `Enable "${wakePhrase}"`}
        </button>

        <button
          onClick={requestMic}
          className="px-3 py-2 rounded-lg bg-blue-50 border border-blue-200 text-blue-700 text-sm"
        >
          Request Microphone
        </button>

        <div className="text-sm text-gray-700">{statusText}</div>
      </div>

      <div className="w-full max-w-md text-xs text-gray-600 bg-white/60 border border-gray-100 rounded p-2 flex items-center justify-between">
        <div>
          <div><strong>Mic:</strong> {micPermission}</div>
          <div className="truncate"><strong>Heard:</strong> {transcriptPreview || '—'}</div>
        </div>

        {onToggleAutoStart && (
          <div className="flex flex-col items-end">
            <label className="text-xs text-gray-500">Auto-start camera on wake</label>
            <button
              onClick={() => onToggleAutoStart(!autoStart)}
              className={`mt-1 px-3 py-1 rounded font-medium text-sm ${
                autoStart ? 'bg-indigo-600 text-white' : 'bg-gray-200 text-gray-700'
              }`}
            >
              {autoStart ? 'On' : 'Off'}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
