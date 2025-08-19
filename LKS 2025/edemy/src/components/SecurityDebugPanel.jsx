import React, { useState } from 'react';
import { useUser } from '@clerk/clerk-react';
import { useUserRole } from '../hooks/useUserRole.js';

/**
 * Debug Security Status Component
 * Menampilkan status keamanan user untuk debugging
 * Hanya muncul di development mode
 */
const SecurityDebugPanel = () => {
  const [isOpen, setIsOpen] = useState(false);
  const { user, isLoaded, isSignedIn } = useUser();
  const { 
    isEducator, 
    isStudent, 
    organizationLoaded, 
    organizationData, 
    currentRole,
    organizationRole,
    roleCalculationLoaded
  } = useUserRole();

  // Only show in development
  if (import.meta.env.PROD) return null;

  return (
    <div className="fixed bottom-4 right-4 z-50">
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="bg-red-500 text-white px-3 py-2 rounded-full text-xs font-mono hover:bg-red-600 transition-colors"
      >
        🔍 DEBUG
      </button>
      
      {isOpen && (
        <div className="absolute bottom-12 right-0 bg-black text-green-400 p-4 rounded-lg shadow-xl max-w-sm text-xs font-mono">
          <h3 className="text-yellow-400 font-bold mb-2">SECURITY STATUS</h3>
          
          <div className="space-y-1">
            <div>Email: {user?.emailAddresses[0]?.emailAddress || 'None'}</div>
            <div>Signed In: {isSignedIn ? '✅' : '❌'}</div>
            <div>User Loaded: {isLoaded ? '✅' : '⏳'}</div>
            <div>Org Loaded: {organizationLoaded ? '✅' : '⏳'}</div>
            <div>Role Calc: {roleCalculationLoaded ? '✅' : '⏳'}</div>
            
            <div className="border-t border-gray-600 pt-2 mt-2">
              <div>Current Role: <span className="text-yellow-400">{currentRole || 'None'}</span></div>
              <div>Is Educator: {isEducator ? '✅' : '❌'} {!organizationLoaded ? '(Loading...)' : ''}</div>
              <div>Is Student: {isStudent ? '✅' : '❌'} {!organizationLoaded ? '(Loading...)' : ''}</div>
              <div>Org Role: <span className="text-cyan-400">{organizationRole || 'None'}</span></div>
              <div className="text-xs text-gray-400">
                Logic: {organizationData?.length === 0 ? 'No orgs = Student only' : 'Has orgs, checking roles'}
              </div>
            </div>
            
            <div className="border-t border-gray-600 pt-2 mt-2">
              <div>Organizations: {organizationData?.length || 0}</div>
              {organizationData?.map((membership, index) => (
                <div key={index} className="ml-2 text-cyan-300">
                  • {membership.organization.name}: {membership.role}
                </div>
              ))}
            </div>
            
            <div className="border-t border-gray-600 pt-2 mt-2">
              <div>Path: {window.location.pathname}</div>
              <div>Expected Access: {
                window.location.pathname.startsWith('/educator') 
                  ? (isEducator ? '✅ ALLOWED' : '❌ BLOCKED') 
                  : '✅ PUBLIC'
              }</div>
            </div>
          </div>
          
          <button
            onClick={() => setIsOpen(false)}
            className="mt-2 bg-gray-700 text-white px-2 py-1 rounded text-xs hover:bg-gray-600"
          >
            Close
          </button>
        </div>
      )}
    </div>
  );
};

export default SecurityDebugPanel;
