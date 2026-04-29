/**
 * Admin Dashboard Page
 * Monitor dan manage backend data
 */

import { BackendStatus } from '@/components/BackendStatus';
import { DataMonitor } from '@/components/admin/DataMonitor';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';

export default function Admin() {
  return (
    <div className="container mx-auto p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Admin Dashboard</h1>
          <p className="text-muted-foreground">
            Monitor dan kelola data Hospital Navigator
          </p>
        </div>
      </div>

      <Tabs defaultValue="monitor" className="w-full">
        <TabsList>
          <TabsTrigger value="monitor">Data Monitor</TabsTrigger>
          <TabsTrigger value="backend">Backend Status</TabsTrigger>
        </TabsList>

        <TabsContent value="monitor" className="space-y-4">
          <DataMonitor />
        </TabsContent>

        <TabsContent value="backend" className="space-y-4">
          <div className="max-w-2xl">
            <BackendStatus />
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
}
