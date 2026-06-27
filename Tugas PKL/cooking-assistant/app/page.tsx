'use client';

import { useState } from 'react';
import CameraCapture from './components/CameraCapture';
import ResultDisplay from './components/ResultDisplay';
import VoiceAssistant from './components/VoiceAssistant';

export default function Home() {
  const [result, setResult] = useState<string>('');
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [error, setError] = useState<string>('');
  const [isSpeaking, setIsSpeaking] = useState(false);

  const handleCapture = async (imageData: string) => {
    setIsAnalyzing(true);
    setError('');
    setResult('');

    try {
      const response = await fetch('/api/analyze', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ image: imageData }),
      });

      const data = await response.json();

      if (!response.ok) {
        const errorMsg = data.details 
          ? `${data.error}: ${data.details}` 
          : data.error || 'Failed to analyze image';
        throw new Error(errorMsg);
      }

      setResult(data.result);
    } catch (err) {
      console.error('Analysis error:', err);
      const errorMessage = err instanceof Error ? err.message : 'Failed to analyze image';
      setError(errorMessage);
    } finally {
      setIsAnalyzing(false);
    }
  };

  const handleVoiceCommand = (command: string) => {
    console.log('Voice command received:', command);
    
    // Parse common cooking commands
    const lowerCommand = command.toLowerCase();
    
    // Speak response function
    const speakResponse = (text: string) => {
      setIsSpeaking(true);
      const utterance = new SpeechSynthesisUtterance(text);
      utterance.lang = 'en-US';
      utterance.rate = 0.9;
      utterance.pitch = 1;
      utterance.onend = () => setIsSpeaking(false);
      utterance.onerror = () => setIsSpeaking(false);
      window.speechSynthesis.speak(utterance);
    };
    
    if (lowerCommand.includes('take picture') || lowerCommand.includes('capture') || lowerCommand.includes('take photo')) {
      speakResponse('Taking a picture now. Please hold your ingredients steady.');
      setResult('📸 Taking picture...');
      // Trigger camera capture
      const captureBtn = document.querySelector('[data-capture-btn]') as HTMLButtonElement;
      if (captureBtn) {
        setTimeout(() => captureBtn.click(), 1000);
      }
    } else if (lowerCommand.includes('start camera') || lowerCommand.includes('open camera')) {
      speakResponse('Starting camera now.');
      setResult('📷 Starting camera...');
      const startBtn = document.querySelector('[data-start-camera]') as HTMLButtonElement;
      if (startBtn) {
        setTimeout(() => startBtn.click(), 500);
      }
    } else if (lowerCommand.includes('stop camera') || lowerCommand.includes('close camera')) {
      speakResponse('Stopping camera.');
      setResult('🛑 Stopping camera...');
      const stopBtn = document.querySelector('[data-stop-camera]') as HTMLButtonElement;
      if (stopBtn) {
        setTimeout(() => stopBtn.click(), 500);
      }
    } else if (lowerCommand.includes('next step') || lowerCommand.includes('what next')) {
      speakResponse('Point your camera at the ingredients, and I will guide you through the next step.');
      setResult('Voice command: Next step requested. Point camera at ingredients for guidance.');
    } else if (lowerCommand.includes('repeat') || lowerCommand.includes('say again')) {
      // Repeat last instruction
      if (result) {
        speakResponse(result);
      } else {
        speakResponse('There is nothing to repeat yet.');
      }
    } else if (lowerCommand.includes('ingredients') || lowerCommand.includes('what do i need')) {
      speakResponse('Let me identify your ingredients. Please show them to the camera.');
      setResult('Voice command: Show ingredients. Point camera at your ingredients to identify them.');
    } else if (lowerCommand.includes('help') || lowerCommand.includes('what can you do')) {
      const helpText = 'I can help you cook. Say take picture to capture ingredients, start camera to open the camera, or ask me about the next step.';
      speakResponse(helpText);
      setResult(helpText);
    } else {
      speakResponse(`I heard: ${command}. You can say take picture, start camera, or ask about the next step.`);
      setResult(`Voice command received: "${command}". Try: "take picture", "start camera", or "next step".`);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
      <main className="container mx-auto px-4 py-8">
        {/* Header */}
        <div className="text-center mb-12">
          <h1 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
            🍳 AI Cooking Assistant
          </h1>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            Point your camera at ingredients and get instant cooking suggestions powered by AI
          </p>
        </div>

        {/* Voice Assistant */}
        <VoiceAssistant onCommand={handleVoiceCommand} wakePhrase="hey chef" />

        {/* AI Speaking Indicator */}
        {isSpeaking && (
          <div className="w-full max-w-2xl mx-auto mt-4">
            <div className="bg-purple-50 border border-purple-200 rounded-lg p-4 flex items-center gap-3">
              <div className="flex gap-1">
                <span className="w-2 h-8 bg-purple-500 rounded animate-pulse" style={{ animationDelay: '0ms' }}></span>
                <span className="w-2 h-8 bg-purple-500 rounded animate-pulse" style={{ animationDelay: '150ms' }}></span>
                <span className="w-2 h-8 bg-purple-500 rounded animate-pulse" style={{ animationDelay: '300ms' }}></span>
              </div>
              <p className="text-purple-800 font-medium">🔊 AI is speaking...</p>
            </div>
          </div>
        )}

        {/* Camera Section */}
        <div className="mt-8">
          <CameraCapture onCapture={handleCapture} isAnalyzing={isAnalyzing} />
        </div>

        {/* Error Display */}
        {error && (
          <div className="w-full max-w-2xl mx-auto mt-6">
            <div className="bg-red-50 border border-red-200 rounded-lg p-4">
              <p className="text-red-800 font-medium">Error: {error}</p>
            </div>
          </div>
        )}

        {/* Result Display */}
        <ResultDisplay result={result} />

        {/* Instructions */}
        <div className="w-full max-w-2xl mx-auto mt-12 bg-blue-50 rounded-lg p-6 border border-blue-200">
          <h3 className="font-bold text-blue-900 mb-3">🎤 Voice Commands:</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-blue-800">
            <div>
              <p className="font-semibold mb-2">Camera Control:</p>
              <ul className="text-sm space-y-1 ml-4">
                <li>• "Hey Chef, <strong>start camera</strong>"</li>
                <li>• "Hey Chef, <strong>take picture</strong>"</li>
                <li>• "Hey Chef, <strong>stop camera</strong>"</li>
              </ul>
            </div>
            <div>
              <p className="font-semibold mb-2">Cooking Help:</p>
              <ul className="text-sm space-y-1 ml-4">
                <li>• "Hey Chef, <strong>next step</strong>"</li>
                <li>• "Hey Chef, <strong>what ingredients</strong>"</li>
                <li>• "Hey Chef, <strong>repeat</strong>"</li>
                <li>• "Hey Chef, <strong>help</strong>"</li>
              </ul>
            </div>
          </div>
          
          <div className="mt-4 pt-4 border-t border-blue-200">
            <h3 className="font-bold text-blue-900 mb-2">📱 Manual Control:</h3>
            <ol className="list-decimal list-inside space-y-1 text-sm text-blue-800">
              <li>Click "Start Voice Assistant" to enable hands-free control</li>
              <li>Or use buttons below to control camera manually</li>
              <li>Point camera at ingredients and click "Capture & Analyze"</li>
              <li>Click "Speak" button to hear results read aloud</li>
            </ol>
          </div>
        </div>
      </main>
    </div>
  );
}
