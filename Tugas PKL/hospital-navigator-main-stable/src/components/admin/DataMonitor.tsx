/**
 * Data Monitor Component
 * Monitor dan display data dari backend API
 */

import { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { ScrollArea } from '@/components/ui/scroll-area';
import { RefreshCw, Database, MapPin, QrCode, TrendingUp, Tag } from 'lucide-react';
import { roomService } from '@/services/roomService';
import { qrAnchorService } from '@/services/qrAnchorService';
import { categoryService } from '@/services/categoryService';
import type { HospitalRoomInfo } from '@/data/hospitalRoomInfo';
import type { QrAnchor } from '@/data/hospitalRouteGraph';
import type { Category } from '@/types/category';
import { CategoryBadge } from '@/components/hospital/CategoryBadge';

export function DataMonitor() {
  const [isLoading, setIsLoading] = useState(false);
  const [rooms, setRooms] = useState<HospitalRoomInfo[]>([]);
  const [qrAnchors, setQrAnchors] = useState<QrAnchor[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [stats, setStats] = useState<{
    total: number;
    byFloor: Record<string, number>;
    rooms: number;
  } | null>(null);
  const [lastUpdated, setLastUpdated] = useState<Date | null>(null);

  const loadData = async () => {
    setIsLoading(true);
    try {
      const [roomsData, anchorsData, statsData, categoriesData] = await Promise.all([
        roomService.getAllRooms(),
        qrAnchorService.getAllQrAnchors(),
        qrAnchorService.getQrAnchorStats(),
        categoryService.getAllCategories(),
      ]);

      setRooms(roomsData);
      setQrAnchors(anchorsData);
      setStats(statsData);
      setCategories(categoriesData);
      setLastUpdated(new Date());
    } catch (error) {
      console.error('Error loading data:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // Group rooms by category
  const roomsByCategory = rooms.reduce((acc, room) => {
    if (!acc[room.category]) {
      acc[room.category] = [];
    }
    acc[room.category].push(room);
    return acc;
  }, {} as Record<string, HospitalRoomInfo[]>);

  // Group QR anchors by floor
  const anchorsByFloor = qrAnchors.reduce((acc, anchor) => {
    const floor = String(anchor.floor);
    if (!acc[floor]) {
      acc[floor] = [];
    }
    acc[floor].push(anchor);
    return acc;
  }, {} as Record<string, QrAnchor[]>);

  return (
    <div className="space-y-4">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold">Data Monitor</h2>
          <p className="text-sm text-muted-foreground">
            Monitor data ruangan dan QR anchors dari backend
          </p>
        </div>
        <Button onClick={loadData} disabled={isLoading} size="sm">
          <RefreshCw className={`h-4 w-4 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
          {isLoading ? 'Loading...' : 'Refresh'}
        </Button>
      </div>

      {lastUpdated && (
        <p className="text-xs text-muted-foreground">
          Last updated: {lastUpdated.toLocaleString()}
        </p>
      )}

      {/* Statistics Cards */}
      <div className="grid gap-4 md:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Rooms</CardTitle>
            <Database className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{rooms.length}</div>
            <p className="text-xs text-muted-foreground">
              {Object.keys(roomsByCategory).length} categories
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Categories</CardTitle>
            <Tag className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{categories.length}</div>
            <p className="text-xs text-muted-foreground">
              From backend API
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">QR Anchors</CardTitle>
            <QrCode className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{qrAnchors.length}</div>
            <p className="text-xs text-muted-foreground">
              {stats?.rooms || 0} rooms covered
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Floors</CardTitle>
            <MapPin className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {stats ? Object.keys(stats.byFloor).length : 0}
            </div>
            <p className="text-xs text-muted-foreground">
              Including parking levels
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Data Tables */}
      <Tabs defaultValue="rooms" className="w-full">
        <TabsList className="grid w-full grid-cols-4">
          <TabsTrigger value="rooms">Rooms ({rooms.length})</TabsTrigger>
          <TabsTrigger value="categories">Categories ({categories.length})</TabsTrigger>
          <TabsTrigger value="qr-anchors">QR Anchors ({qrAnchors.length})</TabsTrigger>
          <TabsTrigger value="statistics">Statistics</TabsTrigger>
        </TabsList>

        {/* Rooms Tab */}
        <TabsContent value="rooms" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Hospital Rooms</CardTitle>
              <CardDescription>
                All rooms grouped by category
              </CardDescription>
            </CardHeader>
            <CardContent>
              <ScrollArea className="h-[500px] pr-4">
                {Object.entries(roomsByCategory).map(([category, categoryRooms]) => (
                  <div key={category} className="mb-6">
                    <div className="flex items-center gap-2 mb-3">
                      <CategoryBadge categoryName={category} />
                      <span className="text-sm text-muted-foreground">
                        ({categoryRooms.length} rooms)
                      </span>
                    </div>
                    <div className="space-y-2 ml-4">
                      {categoryRooms.map((room) => (
                        <div
                          key={room.id}
                          className="p-3 border rounded-lg hover:bg-accent transition-colors"
                        >
                          <div className="flex items-start justify-between">
                            <div className="flex-1">
                              <h4 className="font-medium">{room.name}</h4>
                              <p className="text-sm text-muted-foreground mt-1">
                                {room.description}
                              </p>
                              <p className="text-xs text-muted-foreground mt-1">
                                ID: <code className="bg-muted px-1 py-0.5 rounded">{room.id}</code>
                              </p>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </ScrollArea>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Categories Tab */}
        <TabsContent value="categories" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Categories</CardTitle>
              <CardDescription>
                All available categories from backend API
              </CardDescription>
            </CardHeader>
            <CardContent>
              <ScrollArea className="h-[500px] pr-4">
                <div className="space-y-3">
                  {categories.map((category) => {
                    const roomCount = rooms.filter(r => r.category === category.name).length;
                    return (
                      <div
                        key={category.name}
                        className="p-4 border rounded-lg hover:bg-accent transition-colors"
                      >
                        <div className="flex items-start justify-between mb-2">
                          <div className="flex items-center gap-2">
                            <Badge variant="default">{category.name}</Badge>
                            <span className="text-sm text-muted-foreground">
                              {roomCount} {roomCount === 1 ? 'room' : 'rooms'}
                            </span>
                          </div>
                        </div>
                        <p className="text-sm text-muted-foreground">
                          {category.description}
                        </p>
                      </div>
                    );
                  })}
                </div>
              </ScrollArea>
            </CardContent>
          </Card>
        </TabsContent>

        {/* QR Anchors Tab */}
        <TabsContent value="qr-anchors" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>QR Anchors</CardTitle>
              <CardDescription>
                QR code anchors grouped by floor
              </CardDescription>
            </CardHeader>
            <CardContent>
              <ScrollArea className="h-[500px] pr-4">
                {Object.entries(anchorsByFloor)
                  .sort(([a], [b]) => Number(b) - Number(a))
                  .map(([floor, floorAnchors]) => (
                    <div key={floor} className="mb-6">
                      <div className="flex items-center gap-2 mb-3">
                        <Badge variant="outline">
                          Floor {floor === '0' ? 'Parking L1' : floor === '-1' ? 'Parking L2' : floor}
                        </Badge>
                        <span className="text-sm text-muted-foreground">
                          ({floorAnchors.length} anchors)
                        </span>
                      </div>
                      <div className="space-y-2 ml-4">
                        {floorAnchors.map((anchor) => (
                          <div
                            key={anchor.qrId}
                            className="p-3 border rounded-lg hover:bg-accent transition-colors"
                          >
                            <div className="flex items-start justify-between">
                              <div className="flex-1">
                                <div className="flex items-center gap-2">
                                  <code className="text-sm font-mono bg-muted px-2 py-1 rounded">
                                    {anchor.qrId}
                                  </code>
                                  <Badge variant="secondary" className="text-xs">
                                    {anchor.roomId}
                                  </Badge>
                                </div>
                                <p className="text-sm text-muted-foreground mt-2">
                                  {anchor.label}
                                </p>
                                <div className="flex gap-4 mt-2 text-xs text-muted-foreground">
                                  <span>X: {anchor.svgX.toFixed(2)}</span>
                                  <span>Y: {anchor.svgY.toFixed(2)}</span>
                                  {anchor.routeNodeId && (
                                    <span>Node: {anchor.routeNodeId}</span>
                                  )}
                                </div>
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  ))}
              </ScrollArea>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Statistics Tab */}
        <TabsContent value="statistics" className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2">
            {/* Rooms by Category */}
            <Card>
              <CardHeader>
                <CardTitle>Rooms by Category</CardTitle>
                <CardDescription>Distribution of rooms</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {Object.entries(roomsByCategory)
                    .sort(([, a], [, b]) => b.length - a.length)
                    .map(([category, categoryRooms]) => (
                      <div key={category} className="flex items-center justify-between">
                        <span className="text-sm">{category}</span>
                        <div className="flex items-center gap-2">
                          <div className="w-32 h-2 bg-muted rounded-full overflow-hidden">
                            <div
                              className="h-full bg-primary"
                              style={{
                                width: `${(categoryRooms.length / rooms.length) * 100}%`,
                              }}
                            />
                          </div>
                          <span className="text-sm font-medium w-8 text-right">
                            {categoryRooms.length}
                          </span>
                        </div>
                      </div>
                    ))}
                </div>
              </CardContent>
            </Card>

            {/* QR Anchors by Floor */}
            <Card>
              <CardHeader>
                <CardTitle>QR Anchors by Floor</CardTitle>
                <CardDescription>Distribution by floor</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {stats &&
                    Object.entries(stats.byFloor)
                      .sort(([a], [b]) => Number(b) - Number(a))
                      .map(([floor, count]) => (
                        <div key={floor} className="flex items-center justify-between">
                          <span className="text-sm">
                            Floor {floor === '0' ? 'Parking L1' : floor === '-1' ? 'Parking L2' : floor}
                          </span>
                          <div className="flex items-center gap-2">
                            <div className="w-32 h-2 bg-muted rounded-full overflow-hidden">
                              <div
                                className="h-full bg-primary"
                                style={{
                                  width: `${(count / stats.total) * 100}%`,
                                }}
                              />
                            </div>
                            <span className="text-sm font-medium w-8 text-right">
                              {count}
                            </span>
                          </div>
                        </div>
                      ))}
                </div>
              </CardContent>
            </Card>

            {/* Summary Stats */}
            <Card className="md:col-span-2">
              <CardHeader>
                <CardTitle>Summary</CardTitle>
                <CardDescription>Overall statistics</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="grid gap-4 md:grid-cols-4">
                  <div className="space-y-1">
                    <p className="text-sm text-muted-foreground">Total Rooms</p>
                    <p className="text-2xl font-bold">{rooms.length}</p>
                  </div>
                  <div className="space-y-1">
                    <p className="text-sm text-muted-foreground">Categories</p>
                    <p className="text-2xl font-bold">{Object.keys(roomsByCategory).length}</p>
                  </div>
                  <div className="space-y-1">
                    <p className="text-sm text-muted-foreground">QR Anchors</p>
                    <p className="text-2xl font-bold">{qrAnchors.length}</p>
                  </div>
                  <div className="space-y-1">
                    <p className="text-sm text-muted-foreground">Floors</p>
                    <p className="text-2xl font-bold">
                      {stats ? Object.keys(stats.byFloor).length : 0}
                    </p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
}

export default DataMonitor;
