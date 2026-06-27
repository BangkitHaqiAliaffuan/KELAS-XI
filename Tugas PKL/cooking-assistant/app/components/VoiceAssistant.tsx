'use client';

import { useEffect, useRef, useState } from 'react';

interface VoiceAssistantProps {
  onCommand: (command: string) => void;
  wakePhrase?: string;
}

export default function VoiceAssistant({ 
  onCommand, 
  wakePhrase = 'hey chef' 
}: VoiceAssistantProps) {
  const [isListening, setIsListening] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [status, setStatus] = useState('Idle');
  const [lastCommand, setLastCommand] = useState('');
  const [supported, setSupported] = useState(true);
  const [liveTranscript, setLiveTranscript] = useState('');
  const [transcriptHistory, setTranscriptHistory] = useState<string[]>([]);
  
  const recognitionRef = useRef<any>(null);
  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const audioChunksRef = useRef<Blob[]>([]);
  const lastWakeRef = useRef<number>(0);

  // Initialize Web Speech API for wake word detection
  useEffect(() => {
    if (typeof window === 'undefined') return;
    
    const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
    
    if (!SpeechRecognition) {
      setSupported(false);
      return;
    }

    const recognition = new SpeechRecognition();
    recognition.continuous = true;
    recognition.interimResults = true;
    recognition.lang = 'en-US';

    recognition.onstart = () => {
      setStatus('Listening for wake word...');
      setIsListening(true);
    };

    recognition.onend = () => {
      setStatus('Stopped');
      setIsListening(false);
    };

    recognition.onerror = (e: any) => {
      console.error('Speech recognition error:', e);
      setStatus(`Error: ${e?.error || 'unknown'}`);
    };

    recognition.onresult = (event: any) => {
      let transcript = '';
      for (let i = event.resultIndex; i < event.results.length; ++i) {
        transcript += event.results[i][0].transcript;
      }

      // Update live transcript
      setLiveTranscript(transcript);
      
      // Log to console
      console.log('🎤 Heard:', transcript);

      const normalized = transcript.toLowerCase().trim();
      
      // Wake word detected
      if (normalized.includes(wakePhrase.toLowerCase())) {
        const now = Date.now();
        if (now - lastWakeRef.current > 3000) {
          lastWakeRef.current = now;
          setStatus('Wake word detected! Listening for command...');
          
          // Add to history
          setTranscriptHistory(prev => [...prev.slice(-4), `[Wake] ${transcript}`]);
          
          console.log('✅ Wake word detected!');
          startCommandRecording();
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
    };
  }, [wakePhrase]);

  // Start recording for high-accuracy command recognition
  const startCommandRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      
      audioChunksRef.current = [];
      const mediaRecorder = new MediaRecorder(stream, {
        mimeType: 'audio/webm',
      });

      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          audioChunksRef.current.push(event.data);
        }
      };

      mediaRecorder.onstop = async () => {
        const audioBlob = new Blob(audioChunksRef.current, { type: 'audio/webm' });
        await transcribeWithGroq(audioBlob);
        
        // Stop all tracks
        stream.getTracks().forEach(track => track.stop());
      };

      mediaRecorderRef.current = mediaRecorder;
      mediaRecorder.start();

      // Record for 5 seconds
      setTimeout(() => {
        if (mediaRecorder.state === 'recording') {
          mediaRecorder.stop();
        }
      }, 5000);

    } catch (error) {
      console.error('Failed to start recording:', error);
      setStatus('Microphone access denied');
    }
  };

  // Transcribe using Groq Whisper API
  const transcribeWithGroq = async (audioBlob: Blob) => {
    setIsProcessing(true);
    setStatus('Processing command...');

    try {
      const formData = new FormData();
      formData.append('audio', audioBlob, 'command.webm');

      console.log('📤 Sending audio to Groq Whisper...');

      const response = await fetch('/api/voice/transcribe', {
        method: 'POST',
        body: formData,
      });

      if (!response.ok) {
        throw new Error('Transcription failed');
      }

      const data = await response.json();
      const command = data.text.trim();
      
      console.log('📥 Groq transcription:', command);
      console.log('🎯 Confidence:', data.confidence || 'N/A');
      
      setLastCommand(command);
      setStatus(`Command: "${command}"`);
      
      // Add to history
      setTranscriptHistory(prev => [...prev.slice(-4), `[Command] ${command}`]);
      
      // Execute command
      onCommand(command);

    } catch (error) {
      console.error('❌ Transcription error:', error);
      setStatus('Failed to process command');
    } finally {
      setIsProcessing(false);
    }
  };

  const startListening = async () => {
    if (!recognitionRef.current) return;
    
    try {
      // Request microphone permission
      await navigator.mediaDevices.getUserMedia({ audio: true });
      recognitionRef.current.start();
    } catch (error) {
      console.error('Failed to start listening:', error);
      setStatus('Microphone permission denied');
    }
  };

  const stopListening = () => {
    if (recognitionRef.current) {
      try {
        recognitionRef.current.stop();
      } catch (e) {
        // ignore
      }
    }
    setIsListening(false);
    setStatus('Stopped');
  };

  if (!supported) {
    return (
      <div className="w-full max-w-2xl mx-auto mt-4 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
        <p className="text-yellow-800 text-sm">
          ⚠️ Voice recognition not supported in this browser. Please use Chrome or Edge.
        </p>
      </div>
    );
  }

  return (
    <div className="w-full max-w-2xl mx-auto mt-6">
      <div className="bg-white rounded-lg shadow-lg p-6 border border-gray-200">
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-bold text-gray-900">🎤 Voice Assistant</h3>
          <div className="flex items-center gap-2">
            {isListening && (
              <span className="flex items-center gap-2 text-sm text-green-600">
                <span className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
                Active
              </span>
            )}
            {isProcessing && (
              <span className="flex items-center gap-2 text-sm text-blue-600">
                <svg className="animate-spin h-4 w-4" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Processing
              </span>
            )}
          </div>
        </div>

        {/* Live Transcript */}
        {isListening && liveTranscript && (
          <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-lg">
            <p className="text-xs text-green-700 font-medium mb-1">🎤 Live (Web Speech API):</p>
            <p className="text-sm text-green-900 font-mono">{liveTranscript}</p>
          </div>
        )}

        {/* Status */}
        <div className="mb-4 p-3 bg-gray-50 rounded-lg">
          <p className="text-sm text-gray-700">
            <span className="font-medium">Status:</span> {status}
          </p>
          {lastCommand && (
            <p className="text-sm text-gray-600 mt-1">
              <span className="font-medium">Last command:</span> "{lastCommand}"
            </p>
          )}
        </div>

        {/* Transcript History */}
        {transcriptHistory.length > 0 && (
          <div className="mb-4 p-3 bg-blue-50 border border-blue-200 rounded-lg max-h-32 overflow-y-auto">
            <p className="text-xs text-blue-700 font-medium mb-2">📝 Transcript History:</p>
            <div className="space-y-1">
              {transcriptHistory.map((item, index) => (
                <p key={index} className="text-xs text-blue-900 font-mono">
                  {item}
                </p>
              ))}
            </div>
          </div>
        )}

        {/* Controls */}
        <div className="flex gap-3">
          {!isListening ? (
            <button
              onClick={startListening}
              disabled={isProcessing}
              className="flex-1 px-6 py-3 bg-green-600 text-white rounded-lg font-medium hover:bg-green-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path d="M10 12a2 2 0 100-4 2 2 0 000 4z"/>
                <path fillRule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clipRule="evenodd"/>
              </svg>
              Start Voice Assistant
            </button>
          ) : (
            <button
              onClick={stopListening}
              disabled={isProcessing}
              className="flex-1 px-6 py-3 bg-red-600 text-white rounded-lg font-medium hover:bg-red-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8 7a1 1 0 00-1 1v4a1 1 0 002 0V8a1 1 0 00-1-1zm4 0a1 1 0 00-1 1v4a1 1 0 002 0V8a1 1 0 00-1-1z" clipRule="evenodd"/>
              </svg>
              Stop Listening
            </button>
          )}
        </div>

        {/* Instructions */}
        <div className="mt-4 p-3 bg-blue-50 border border-blue-200 rounded-lg">
          <p className="text-sm text-blue-800">
            <span className="font-medium">💡 How to use:</span>
          </p>
          <ol className="text-sm text-blue-700 mt-2 space-y-1 ml-4 list-decimal">
            <li>Click "Start Voice Assistant"</li>
            <li>Say "<strong>{wakePhrase}</strong>" to activate</li>
            <li>Speak your command clearly (e.g., "what's the next step?")</li>
            <li>Wait for high-accuracy processing (~1 second)</li>
          </ol>
          <p className="text-xs text-blue-600 mt-2">
            💡 Tip: Open browser console (F12) for detailed logs
          </p>
        </div>
      </div>
    </div>
  );
}
