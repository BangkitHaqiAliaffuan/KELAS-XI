'use client';

import { useState } from 'react';
import CameraCapture from './components/CameraCapture';
import ResultDisplay from './components/ResultDisplay';

export default function Home() {
  const [result, setResult] = useState<string>('');
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [error, setError] = useState<string>('');

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

        {/* Camera Section */}
        <CameraCapture onCapture={handleCapture} isAnalyzing={isAnalyzing} />

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
          <h3 className="font-bold text-blue-900 mb-3">How to use:</h3>
          <ol className="list-decimal list-inside space-y-2 text-blue-800">
            <li>Click "Start Camera" to activate your camera</li>
            <li>Point the camera at your ingredients</li>
            <li>Click "Capture & Analyze" to get AI suggestions</li>
            <li>Click "Speak" to hear the results read aloud</li>
          </ol>
        </div>
      </main>
    </div>
  );
}
