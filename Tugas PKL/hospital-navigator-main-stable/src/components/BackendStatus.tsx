/**
 * Backend Status Component
 * Shows whether backend API is available and allows manual refresh
 */

import { useState, useEffect } from 'react';
import { checkBackendHealth, resetBackendCheck } from '@/lib/api';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { RefreshCw, Server, Database } from 'lucide-react';

export function BackendStatus() {
  const [isChecking, setIsChecking] = useState(false);
  const [backendAvailable, setBackendAvailable] = useState<boolean | null>(null);
  const [lastChecked, setLastChecked] = useState<Date | null>(null);

  const checkStatus = async () => {
    setIsChecking(true);
    resetBackendCheck(); // Force recheck
    
    try {
      const available = await checkBackendHealth();
      setBackendAvailable(available);
      setLastChecked(new Date());
    } catch (error) {
      setBackendAvailable(false);
      setLastChecked(new Date());
    } finally {
      setIsChecking(false);
    }
  };

  useEffect(() => {
    checkStatus();
  }, []);

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Server className="h-5 w-5" />
          Backend Status
        </CardTitle>
        <CardDescription>
          Connection status to Hospital Navigator API
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Database className="h-4 w-4 text-muted-foreground" />
            <span className="text-sm font-medium">API Server</span>
          </div>
          {backendAvailable === null ? (
            <Badge variant="outline">Checking...</Badge>
          ) : backendAvailable ? (
            <Badge variant="default" className="bg-green-500">
              Connected
            </Badge>
          ) : (
            <Badge variant="destructive">Disconnected</Badge>
          )}
        </div>

        <div className="text-sm text-muted-foreground">
          {backendAvailable === null && (
            <p>Checking backend connection...</p>
          )}
          {backendAvailable === true && (
            <p>✅ Using API data from backend server</p>
          )}
          {backendAvailable === false && (
            <p>⚠️ Using static data (backend not available)</p>
          )}
        </div>

        {lastChecked && (
          <div className="text-xs text-muted-foreground">
            Last checked: {lastChecked.toLocaleTimeString()}
          </div>
        )}

        <Button
          onClick={checkStatus}
          disabled={isChecking}
          variant="outline"
          size="sm"
          className="w-full"
        >
          <RefreshCw className={`h-4 w-4 mr-2 ${isChecking ? 'animate-spin' : ''}`} />
          {isChecking ? 'Checking...' : 'Refresh Status'}
        </Button>

        <div className="pt-2 border-t text-xs text-muted-foreground space-y-1">
          <p><strong>Data Source:</strong></p>
          <ul className="list-disc list-inside space-y-1 ml-2">
            <li>
              {backendAvailable ? (
                <span>API: <code className="text-xs bg-muted px-1 py-0.5 rounded">http://localhost:3001/api/v1</code></span>
              ) : (
                <span>Static: <code className="text-xs bg-muted px-1 py-0.5 rounded">hospitalRoomInfo.ts</code></span>
              )}
            </li>
          </ul>
        </div>
      </CardContent>
    </Card>
  );
}

export default BackendStatus;
