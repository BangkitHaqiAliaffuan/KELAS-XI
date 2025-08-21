import React, { useState, useEffect } from 'react';
import { useUser } from '@clerk/clerk-react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, AlertTriangle } from 'lucide-react';
import PaymentDebugPanel from '../components/PaymentDebugPanel';

const PaymentTestPage = () => {
  const { user, isLoaded } = useUser();
  const navigate = useNavigate();
  const [showPanel, setShowPanel] = useState(true);

  // Environment check
  const isDevelopment = process.env.NODE_ENV === 'development';
  const hasDebugParam = new URLSearchParams(window.location.search).get('debug') === 'true';
  const hasDebugStorage = localStorage.getItem('paymentDebugMode') === 'true';
  
  const canAccessDebug = isDevelopment || hasDebugParam || hasDebugStorage;

  useEffect(() => {
    // Redirect if not authorized to access debug mode
    if (!canAccessDebug) {
      navigate('/');
    }
  }, [canAccessDebug, navigate]);

  // Show loading while checking Clerk auth
  if (!isLoaded) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading authentication...</p>
        </div>
      </div>
    );
  }

  // Show access denied if not authorized
  if (!canAccessDebug) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <div className="max-w-md mx-auto text-center">
          <AlertTriangle className="h-16 w-16 text-red-500 mx-auto mb-4" />
          <h1 className="text-2xl font-bold text-gray-900 mb-2">Access Denied</h1>
          <p className="text-gray-600 mb-6">
            Payment debug mode is only available in development environment or with debug parameters.
          </p>
          <button 
            onClick={() => navigate('/')}
            className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 transition-colors"
          >
            Go to Home
          </button>
        </div>
      </div>
    );
  }

  // Show login prompt if not authenticated
  if (!user) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <div className="max-w-md mx-auto text-center">
          <div className="bg-white rounded-lg shadow-md p-8">
            <h1 className="text-2xl font-bold text-gray-900 mb-4">Payment Debug Mode</h1>
            <p className="text-gray-600 mb-6">
              Please sign in to access the payment debugging tools.
            </p>
            <button 
              onClick={() => navigate('/auth')}
              className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 transition-colors"
            >
              Sign In
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center space-x-4">
              <button
                onClick={() => navigate(-1)}
                className="flex items-center space-x-2 text-gray-600 hover:text-gray-900 transition-colors"
              >
                <ArrowLeft className="h-5 w-5" />
                <span>Back</span>
              </button>
              <div className="h-6 w-px bg-gray-300"></div>
              <h1 className="text-xl font-semibold text-gray-900">
                🧪 Payment Debug Console
              </h1>
            </div>
            
            <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-500">
                Environment: {process.env.NODE_ENV}
              </span>
              <div className="flex items-center space-x-2">
                <div className="h-2 w-2 bg-green-500 rounded-full"></div>
                <span className="text-sm text-gray-600">Debug Mode Active</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Debug Console Access</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
            <div className="bg-blue-50 p-4 rounded-lg">
              <h3 className="font-medium text-blue-900 mb-2">Development Mode</h3>
              <p className="text-blue-700 text-sm">
                {isDevelopment ? '✅ Active' : '❌ Not active'}
              </p>
            </div>
            <div className="bg-green-50 p-4 rounded-lg">
              <h3 className="font-medium text-green-900 mb-2">URL Debug Parameter</h3>
              <p className="text-green-700 text-sm">
                {hasDebugParam ? '✅ ?debug=true detected' : '❌ Not detected'}
              </p>
            </div>
            <div className="bg-purple-50 p-4 rounded-lg">
              <h3 className="font-medium text-purple-900 mb-2">Local Storage</h3>
              <p className="text-purple-700 text-sm">
                {hasDebugStorage ? '✅ Debug mode enabled' : '❌ Not enabled'}
              </p>
            </div>
          </div>

          <div className="border-t pt-4">
            <h3 className="font-medium text-gray-900 mb-2">How to enable debug mode:</h3>
            <ul className="text-sm text-gray-600 space-y-1">
              <li>• Add <code className="bg-gray-100 px-1 rounded">?debug=true</code> to any course URL</li>
              <li>• Run in development environment</li>
              <li>• Set localStorage: <code className="bg-gray-100 px-1 rounded">localStorage.setItem('paymentDebugMode', 'true')</code></li>
            </ul>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">Payment Testing Tools</h2>
            <button
              onClick={() => setShowPanel(!showPanel)}
              className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors"
            >
              {showPanel ? 'Hide Tools' : 'Show Tools'}
            </button>
          </div>

          {showPanel && (
            <div className="relative">
              <PaymentDebugPanel 
                courseId={null} // Will be set in the panel
                onClose={() => setShowPanel(false)}
              />
            </div>
          )}
        </div>

        <div className="mt-6 bg-yellow-50 border border-yellow-200 rounded-lg p-4">
          <h3 className="font-medium text-yellow-900 mb-2">⚠️ Important Notes:</h3>
          <ul className="text-sm text-yellow-800 space-y-1">
            <li>• This is a development/testing tool only</li>
            <li>• All Stripe payments use test mode with test cards</li>
            <li>• Real user data from Clerk and MongoDB is used</li>
            <li>• Webhook events can be simulated or triggered via Stripe CLI</li>
            <li>• Always test in a safe environment</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default PaymentTestPage;
