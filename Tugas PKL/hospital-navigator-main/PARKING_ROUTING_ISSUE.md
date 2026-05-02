# Masalah Parking Routing - Random Start Point

## Deskripsi Masalah

Saat melakukan routing dari Hospital Lantai 1 ke Lahan Parkir Lantai 1, start point di area parkir menjadi **random/tidak konsisten**. Masalah ini terjadi baik saat menggunakan:
1. **Location via Dropdown** (start point berdasarkan room center)
2. **Location via QR Code** (start point berdasarkan QR anchor coordinates)

### Perilaku yang Diharapkan
- Routing dari Hospital L1 → Parking L1 harus **SELALU** dimulai dari node `Check_Point_Keluar_dari_Lahan_Parkir` di koordinat (1181.4375, 751.0)
- Routing harus berakhir di QR anchor `QR-PK-N01` yang memiliki `routeNodeId: "Belok_ke_Area_Parkir_Sepeda_Motor_Khusus_Tenaga_Medis"` di koordinat (929.478, 417.228)

### Perilaku Aktual
Console log menunjukkan:
```
🅿️ Hospital → Parking L1: Using fixed start point: Check_Point_Keluar_dari_Lahan_Parkir
🅿️ QR anchor: QR-PK-N01 at (929.478, 417.228)
🅿️ Route node ID: Belok_ke_Area_Parkir_Sepeda_Motor_Khusus_Tenaga_Medis
🅿️ Start node "Check_Point_Keluar_dari_Lahan_Parkir" exists: true
🅿️ End node "Belok_ke_Area_Parkir_Sepeda_Motor_Khusus_Tenaga_Medis" exists: true
🅿️ ✓ Parking segment created successfully
🅿️   Checkpoints: 50 nodes
🅿️   First: n_98_63  ❌ SALAH! Seharusnya Check_Point_Keluar_dari_Lahan_Parkir
🅿️   Last: n_77_35   ❌ SALAH! Seharusnya Belok_ke_Area_Parkir_Sepeda_Motor_Khusus_Tenaga_Medis
```

## Analisis Root Cause

### 1. Node Exists tapi Tidak Terhubung di Graph
Kedua node (`Check_Point_Keluar_dari_Lahan_Parkir` dan `Belok_ke_Area_Parkir_Sepeda_Motor_Khusus_Tenaga_Medis`) **ada di SVG** tapi tidak memiliki edges di graph routing.

### 2. Fungsi `resolveRouteEndpoint` Mencari Node Terdekat
Di file `src/data/hospitalRouteGraph.ts`, fungsi ini melakukan fallback:

```typescript
const resolveRouteEndpoint = (
  preferredAnchorNodeId: string | null,
  nodes: Record<string, GraphNode>,
  graph: Graph,
  fallbackPoint: { x: number; y: number },
): RouteEndpointResolution | null => {
  // Jika node ada DAN memiliki edges, gunakan node tersebut
  if (
    preferredAnchorNodeId &&
    nodes[preferredAnchorNodeId] &&
    (graph[preferredAnchorNodeId]?.length || 0) > 0  // ⚠️ MASALAH DI SINI
  ) {
    return {
      anchorNodeId: preferredAnchorNodeId,
      graphNodeId: preferredAnchorNodeId,
    };
  }

  // Jika node ada tapi TIDAK memiliki edges, cari node terdekat
  if (preferredAnchorNodeId && nodes[preferredAnchorNodeId]) {
    const nearestGraphNodeId = getNearestNodeId(
      nodes,
      graph,
      nodes[preferredAnchorNodeId],
    );
    if (nearestGraphNodeId) {
      return {
        anchorNodeId: preferredAnchorNodeId,
        graphNodeId: nearestGraphNodeId,  // ⚠️ MENGGUNAKAN NODE TERDEKAT!
      };
    }
  }

  // Fallback ke nearest node
  const nearestFallbackNodeId = getNearestNodeId(nodes, graph, fallbackPoint);
  if (!nearestFallbackNodeId) return null;

  return {
    anchorNodeId: nearestFallbackNodeId,
    graphNodeId: nearestFallbackNodeId,
  };
};
```

Karena `Check_Point_Keluar_dari_Lahan_Parkir` tidak memiliki edges, sistem mencari node terdekat yang terhubung → `n_98_63`

### 3. Koordinat Node
```
Check_Point_Keluar_dari_Lahan_Parkir: (1181.4375, 751.0)
Belok_ke_Area_Parkir_Sepeda_Motor_Khusus_Tenaga_Medis: (929.478, 417.228)

n_98_63: (98 * 12, 63 * 12) = (1176, 756) ← Dekat dengan checkpoint!
n_77_35: (77 * 12, 35 * 12) = (924, 420) ← Dekat dengan QR anchor!
```

## Solusi yang Diterapkan

Menggunakan `buildRouteFromPoint` dengan mode `point_to_point` untuk memastikan routing menggunakan koordinat eksak, bukan node terdekat.

---

## Kode yang Relevan

### 1. QR Anchor Registry (src/data/hospitalRouteGraph.ts)
```typescript
export const QR_ANCHOR_REGISTRY: Record<string, QrAnchor> = {
  // ... other anchors ...
  
  // Lahan Parkir — floor: 0 (peta parkir terpisah, bukan lantai RS)
  "QR-PK-N01": {
    qrId: "QR-PK-N01",
    roomId: "Parking_Lantai_1",
    svgX: 929.478,
    svgY: 417.228,
    label: "Belok ke Area Parkir Khusus Tenaga Medis",
    floor: 0,
    routeNodeId: "Belok_ke_Area_Parkir_Sepeda_Motor_Khusus_Tenaga_Medis",
  },
};
```

### 2. Parking Connection Definition (src/components/hospital/MapViewer.tsx)
```typescript
// Physical connection points between hospital floor-1 SVG and parking floor-1 SVG.
// Hospital SVG: Check_Point_Lahan_Parkir  cx=463.429  cy=133.196
// Parking SVG:  Check_Point_Keluar_dari_Lahan_Parkir  cx=1181.4375  cy=751.0
const PARKING_CONN = useMemo(() => ({
  hospitalNodeId: "Check_Point_Lahan_Parkir",
  hospitalX: 463.429,
  hospitalY: 133.196,
  parkingNodeId: "Check_Point_Keluar_dari_Lahan_Parkir",
  parkingX: 1181.4375,
  parkingY: 751.0,
}), []);
```

### 3. Routing Logic - Hospital → Parking (src/components/hospital/MapViewer.tsx)
```typescript
// Hospital → Parking: hospitalRoom → connector, connector → parkingRoom
else {
  // For Hospital → Parking L1, ALWAYS route from Check_Point_Keluar_dari_Lahan_Parkir
  // to the specific QR anchor location using point-to-point routing
  console.log(`🅿️ Hospital → Parking L1: Using fixed start point: ${conn.parkingNodeId}`);
  
  const parkAnchor = Object.values(QR_ANCHOR_REGISTRY).find(
    (a) => a.roomId === parkingRoomId && a.floor === 0
  );

  if (!parkAnchor) {
    console.log(`🅿️ ⚠️ No QR anchor found for ${parkingRoomId}`);
    return null;
  }

  console.log(`🅿️ QR anchor: ${parkAnchor.qrId} at (${parkAnchor.svgX}, ${parkAnchor.svgY})`);
  console.log(`🅿️ Route node ID: ${parkAnchor.routeNodeId || 'none'}`);

  // Use the routeNodeId if available, otherwise use parkingRoomId
  const endpointNodeId = parkAnchor.routeNodeId || parkingRoomId;
  
  // Inject both start and end nodes to ensure they exist
  injectVirtualAnchorNode(parkDoc, conn.parkingNodeId, conn.parkingX, conn.parkingY);
  injectVirtualAnchorNode(parkDoc, endpointNodeId, parkAnchor.svgX, parkAnchor.svgY);
  
  console.log(`🅿️ Routing point-to-point: ${conn.parkingNodeId} (${conn.parkingX}, ${conn.parkingY}) → ${endpointNodeId} (${parkAnchor.svgX}, ${parkAnchor.svgY})`);

  // Use buildRouteFromPoint with point_to_point mode to ensure exact coordinates are used
  parkingSegment = buildRouteFromPoint(
    conn.parkingNodeId,
    conn.parkingX,
    conn.parkingY,
    endpointNodeId,
    parkDoc,
    "point_to_point",
  );
  
  if (parkingSegment) {
    console.log(`🅿️ ✓ Parking segment created successfully`);
    console.log(`🅿️   Checkpoints: ${parkingSegment.checkpointIds.length} nodes`);
    console.log(`🅿️   First: ${parkingSegment.checkpointIds[0]}`);
    console.log(`🅿️   Last: ${parkingSegment.checkpointIds[parkingSegment.checkpointIds.length - 1]}`);
    console.log(`🅿️   Distance: ${parkingSegment.totalDistance.toFixed(2)} units`);
  } else {
    console.log(`🅿️ ⚠️ Failed to create parking segment`);
  }
}
```

### 4. Routing Logic - Parking → Hospital (src/components/hospital/MapViewer.tsx)
```typescript
if (isParkingStart) {
  // Parking → Hospital: Start from QR anchor, route to Check_Point_Keluar_dari_Lahan_Parkir
  console.log(`🅿️ Parking → Hospital: Start from parking QR anchor`);
  
  // Find QR anchor for parking
  const parkAnchor = Object.values(QR_ANCHOR_REGISTRY).find(
    (a) => a.roomId === parkingRoomId && a.floor === 0
  );
  
  if (!parkAnchor) {
    console.log(`🅿️ ⚠️ No QR anchor found for ${parkingRoomId}`);
    return null;
  }
  
  console.log(`🅿️ QR anchor: ${parkAnchor.qrId} at (${parkAnchor.svgX}, ${parkAnchor.svgY})`);
  console.log(`🅿️ Route node ID: ${parkAnchor.routeNodeId || 'none'}`);
  
  // Use the routeNodeId if available, otherwise use parkingRoomId
  const startNodeId = parkAnchor.routeNodeId || parkingRoomId;
  
  // Inject both start and end nodes
  injectVirtualAnchorNode(parkDoc, startNodeId, parkAnchor.svgX, parkAnchor.svgY);
  injectVirtualAnchorNode(parkDoc, conn.parkingNodeId, conn.parkingX, conn.parkingY);
  
  console.log(`🅿️ Routing point-to-point: ${startNodeId} (${parkAnchor.svgX}, ${parkAnchor.svgY}) → ${conn.parkingNodeId} (${conn.parkingX}, ${conn.parkingY})`);
  
  // Use buildRouteFromPoint with point_to_point mode
  parkingSegment = buildRouteFromPoint(
    startNodeId,
    parkAnchor.svgX,
    parkAnchor.svgY,
    conn.parkingNodeId,
    parkDoc,
    "point_to_point",
  );
  
  if (parkingSegment) {
    console.log(`🅿️ ✓ Parking segment: ${startNodeId} → ${conn.parkingNodeId}`);
    console.log(`🅿️   Checkpoints: ${parkingSegment.checkpointIds.length} nodes`);
    console.log(`🅿️   First: ${parkingSegment.checkpointIds[0]}`);
    console.log(`🅿️   Last: ${parkingSegment.checkpointIds[parkingSegment.checkpointIds.length - 1]}`);
  } else {
    console.log(`🅿️ ⚠️ Failed to create parking segment`);
  }
  
  hospitalSegment = buildRouteFromPoint(
    conn.hospitalNodeId, conn.hospitalX, conn.hospitalY,
    hospitalRoomId, hospitalDoc,
    "point_to_room",
  );
}
```

### 5. buildRouteFromPoint Function (src/data/hospitalRouteGraph.ts)
```typescript
/**
 * Build a route from a raw SVG coordinate point to a room, or vice-versa,
 * or from point to point within a single SVG document.
 * The coordinate is injected as a virtual node so the normal buildRouteForRooms machinery can resolve it.
 */
export const buildRouteFromPoint = (
  pointRoomId: string,
  pointX: number,
  pointY: number,
  otherRoomId: string,
  svgDoc: Document,
  direction: "point_to_room" | "room_to_point" | "point_to_point" = "point_to_room",
): RoomRouteResult | null => {
  // Inject the virtual node so getRoomCenter can find it
  injectVirtualAnchorNode(svgDoc, pointRoomId, pointX, pointY);

  if (direction === "point_to_point") {
    // Both start and end are points (coordinates)
    // otherRoomId is treated as a node ID, and we need to get its coordinates
    const otherNode = svgDoc.getElementById(otherRoomId);
    if (!otherNode) {
      console.warn(`[buildRouteFromPoint] point_to_point: otherRoomId "${otherRoomId}" not found in SVG`);
      return null;
    }
    
    // Get coordinates of the other node
    let otherX: number, otherY: number;
    if (otherNode.tagName.toLowerCase() === "circle" || otherNode.tagName.toLowerCase() === "ellipse") {
      otherX = Number(otherNode.getAttribute("cx") || "NaN");
      otherY = Number(otherNode.getAttribute("cy") || "NaN");
    } else {
      // Try to get center from room
      const otherCenter = getRoomCenter(svgDoc, otherRoomId);
      if (!otherCenter) {
        console.warn(`[buildRouteFromPoint] point_to_point: Cannot get center for "${otherRoomId}"`);
        return null;
      }
      otherX = otherCenter.x;
      otherY = otherCenter.y;
    }
    
    if (!Number.isFinite(otherX) || !Number.isFinite(otherY)) {
      console.warn(`[buildRouteFromPoint] point_to_point: Invalid coordinates for "${otherRoomId}"`);
      return null;
    }
    
    // Inject virtual node for the other point as well
    injectVirtualAnchorNode(svgDoc, otherRoomId, otherX, otherY);
    
    // Route from first point to second point, using exact coordinates for both
    return buildRouteForRooms(pointRoomId, otherRoomId, svgDoc, {
      startPoint: { x: pointX, y: pointY },
      endPoint: { x: otherX, y: otherY },
      useExactStartPoint: true,
    });
  }

  const startId = direction === "point_to_room" ? pointRoomId : otherRoomId;
  const endId = direction === "point_to_room" ? otherRoomId : pointRoomId;

  return buildRouteForRooms(startId, endId, svgDoc, {
    startPoint: direction === "point_to_room" ? { x: pointX, y: pointY } : undefined,
    endPoint: direction === "room_to_point" ? { x: pointX, y: pointY } : undefined,
    useExactStartPoint: direction === "point_to_room",
  });
};
```

---

## Pertanyaan untuk Claude AI

1. Apakah pendekatan menggunakan `buildRouteFromPoint` dengan mode `point_to_point` sudah benar?
2. Mengapa `Check_Point_Keluar_dari_Lahan_Parkir` tidak memiliki edges di graph padahal node tersebut ada di SVG?
3. Apakah ada cara yang lebih baik untuk memastikan routing selalu menggunakan node checkpoint yang spesifik?
4. Bagaimana cara memastikan node-node penting seperti checkpoint terhubung dengan baik di graph routing?

## Console Log Output Saat Ini
```
🅿️ Hospital → Parking L1: Using fixed start point: Check_Point_Keluar_dari_Lahan_Parkir
🅿️ QR anchor: QR-PK-N01 at (929.478, 417.228)
🅿️ Route node ID: Belok_ke_Area_Parkir_Sepeda_Motor_Khusus_Tenaga_Medis
🅿️ Start node "Check_Point_Keluar_dari_Lahan_Parkir" exists: true
🅿️ End node "Belok_ke_Area_Parkir_Sepeda_Motor_Khusus_Tenaga_Medis" exists: true
🅿️ ✓ Parking segment created successfully
🅿️   Checkpoints: 50 nodes
🅿️   First: n_98_63
🅿️   Last: n_77_35
🅿️   Distance: 439.72 units
```

## Harapan Setelah Fix
```
🅿️ Hospital → Parking L1: Using fixed start point: Check_Point_Keluar_dari_Lahan_Parkir
🅿️ QR anchor: QR-PK-N01 at (929.478, 417.228)
🅿️ Route node ID: Belok_ke_Area_Parkir_Sepeda_Motor_Khusus_Tenaga_Medis
🅿️ Routing point-to-point: Check_Point_Keluar_dari_Lahan_Parkir (1181.4375, 751) → Belok_ke_Area_Parkir_Sepeda_Motor_Khusus_Tenaga_Medis (929.478, 417.228)
🅿️ ✓ Parking segment created successfully
🅿️   Checkpoints: XX nodes
🅿️   First: Check_Point_Keluar_dari_Lahan_Parkir ✓
🅿️   Last: Belok_ke_Area_Parkir_Sepeda_Motor_Khusus_Tenaga_Medis ✓
```
