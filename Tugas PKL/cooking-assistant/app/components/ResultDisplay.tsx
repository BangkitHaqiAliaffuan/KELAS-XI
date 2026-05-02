'use client';

import { useEffect, useState } from 'react';

interface ResultDisplayProps {
  result: string;
}

export default function ResultDisplay({ result }: ResultDisplayProps) {
  const [isSpeaking, setIsSpeaking] = useState(false);
  const [isMounted, setIsMounted] = useState(false);

  useEffect(() => {
    setIsMounted(true);
  }, []);

  const speechSupported = isMounted && typeof window !== 'undefined' && 'speechSynthesis' in window;

  const speakText = (text: string) => {
    if (!speechSupported) {
      alert('Speech synthesis not supported in your browser');
      return;
    }

    // Cancel any ongoing speech
    window.speechSynthesis.cancel();

    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = 'en-US';
    utterance.rate = 0.9;
    utterance.pitch = 1;

    utterance.onstart = () => setIsSpeaking(true);
    utterance.onend = () => setIsSpeaking(false);
    utterance.onerror = () => setIsSpeaking(false);

    window.speechSynthesis.speak(utterance);
  };

  const stopSpeaking = () => {
    window.speechSynthesis.cancel();
    setIsSpeaking(false);
  };

  if (!result) return null;

  return (
    <div className="w-full max-w-2xl mx-auto mt-8">
      <div className="bg-white rounded-lg shadow-xl p-6 border border-gray-200">
        <div className="flex items-start justify-between mb-4">
          <h2 className="text-xl font-bold text-gray-900">Analysis Result</h2>
          {speechSupported && (
            <button
              onClick={() => (isSpeaking ? stopSpeaking() : speakText(result))}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                isSpeaking
                  ? 'bg-red-600 hover:bg-red-700 text-white'
                  : 'bg-purple-600 hover:bg-purple-700 text-white'
              }`}
            >
              {isSpeaking ? (
                <span className="flex items-center gap-2">
                  <svg className="w-5 h-5 animate-pulse" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M10 18a8 8 0 100-16 8 8 0 000 16zM8 7a1 1 0 00-1 1v4a1 1 0 002 0V8a1 1 0 00-1-1zm4 0a1 1 0 00-1 1v4a1 1 0 002 0V8a1 1 0 00-1-1z" />
                  </svg>
                  Stop
                </span>
              ) : (
                <span className="flex items-center gap-2">
                  <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M18 3a1 1 0 00-1.196-.98l-10 2A1 1 0 006 5v9.114A4.369 4.369 0 005 14c-1.657 0-3 .895-3 2s1.343 2 3 2 3-.895 3-2V7.82l8-1.6v5.894A4.37 4.37 0 0015 12c-1.657 0-3 .895-3 2s1.343 2 3 2 3-.895 3-2V3z" />
                  </svg>
                  Speak
                </span>
              )}
            </button>
          )}
        </div>

        <div className="prose prose-sm max-w-none">
          <div className="whitespace-pre-wrap text-gray-700 leading-relaxed">
            {result}
          </div>
        </div>
      </div>
    </div>
  );
}
