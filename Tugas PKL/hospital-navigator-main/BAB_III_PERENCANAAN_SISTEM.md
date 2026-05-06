# BAB III – PERENCANAAN SISTEM (DSD)

## 3.1 Arsitektur Sistem (React + Express.js)

Hospital Map Viewer dibangun menggunakan arsitektur **Client-Server** dengan pemisahan yang jelas antara frontend dan backend:

### Frontend (Client)
- **Framework**: React 18 dengan TypeScript
- **Build Tool**: Vite
- **State Management**: React Hooks (useState, useEffect, useContext)
- **Routing**: React Router DOM v6
- **UI Components**: Radix UI + Tailwind CSS
- **HTTP Client**: Axios dengan React Query untuk data fetching

### Backend (Server)
- **Framework**: Express.js
- **Architecture**: RESTful API
- **Data Storage**: In-memory data structures (JavaScript objects)
- **Middleware**: CORS, Helmet, Morgan, Compression

### Arsitektur Komunikasi

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT (Browser)                      │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  React Application (TypeScript + Vite)                 │ │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │ │
│  │  │  Components  │  │    Hooks     │  │    Utils     │ │ │
│  │  │  - MapViewer │  │  - useState  │  │  - Pathfind  │ │ │
│  │  │  - SearchBar │  │  - useEffect │  │  - QR Parser │ │ │
│  │  │  - Sidebar   │  │  - useQuery  │  │  - SVG Utils │ │ │
│  │  └──────────────┘  └──────────────┘  └──────────────┘ │ │
│  │                                                          │ │
│  │  ┌──────────────────────────────────────────────────┐  │ │
│  │  │         Data Layer (Axios + React Query)         │  │ │
│  │  └──────────────────────────────────────────────────┘  │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP/REST API
                              │ (JSON)
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    SERVER (Node.js + Express)                │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Express.js Application                                │ │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │ │
│  │  │   Routes     │  │  Middleware  │  │     Data     │ │ │
│  │  │  - /rooms    │  │  - CORS      │  │  - Rooms     │ │ │
│  │  │  - /qr-      │  │  - Helmet    │  │  - QR        │ │ │
│  │  │    anchors   │  │  - Morgan    │  │    Anchors   │ │ │
│  │  │  - /health   │  │  - Error     │  │  - Routes    │ │ │
│  │  └──────────────┘  └──────────────┘  └──────────────┘ │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Alur Request-Response

1. **User Interaction** → User berinteraksi dengan UI (klik, scan QR, search)
2. **Component Event** → React component menangkap event dan memanggil API
3. **HTTP Request** → Axios mengirim request ke Express.js server
4. **Route Handler** → Express router menerima dan memproses request
5. **Data Processing** → Server mengakses data dan melakukan operasi
6. **HTTP Response** → Server mengirim response JSON ke client
7. **State Update** → React Query update cache dan trigger re-render
8. **UI Update** → Component menampilkan data terbaru ke user

---

## 3.2 Desain Database

Sistem Hospital Map Viewer menggunakan **in-memory data structures** yang disimpan dalam file JavaScript. Data dikelola dalam tiga struktur utama:

### Tabel: hospitalRooms

Menyimpan informasi detail tentang ruangan/lokasi di rumah sakit.

| Field | Tipe Data | Keterangan |
|-------|-----------|------------|
| id | String (PK) | Identifier unik ruangan (contoh: "IGD", "Poli_Umum") |
| name | String | Nama ruangan yang ditampilkan |
| category | String | Kategori ruangan (Emergency, Outpatient, Critical Care, dll) |
| locationHint | String | Petunjuk lokasi dalam bahasa natural |
| description | Text | Deskripsi lengkap ruangan dan layanan |
| floor | Number | Nomor lantai (0=Parkir L1, 1=Lantai 1, 2=Lantai 2) |

**Contoh Data:**
```javascript
{
  id: "IGD",
  name: "IGD",
  category: "Emergency",
  locationHint: "Sayap kiri bawah peta",
  description: "Instalasi Gawat Darurat 24 jam...",
  floor: 1
}
```

### Tabel: qrAnchors

Menyimpan registry QR code dengan koordinat SVG untuk navigasi.

| Field | Tipe Data | Keterangan |
|-------|-----------|------------|
| qrId | String (PK) | ID QR code (contoh: "QR-F1-N01") |
| roomId | String (FK) | Foreign key ke hospitalRooms.id |
| svgX | Number | Koordinat X pada SVG map |
| svgY | Number | Koordinat Y pada SVG map |
| label | String | Label deskriptif lokasi QR |
| floor | Number | Nomor lantai tempat QR berada |
| routeNodeId | String (Optional) | ID node untuk pathfinding |

**Contoh Data:**
```javascript
{
  qrId: "QR-F1-N01",
  roomId: "IGD",
  svgX: 632.95538,
  svgY: 753.07831,
  label: "Persimpangan Area Pelayanan IGD",
  floor: 1,
  routeNodeId: "IGD"
}
```

### Tabel: hospitalRouteGraph

Menyimpan graph nodes dan edges untuk algoritma pathfinding.

| Field | Tipe Data | Keterangan |
|-------|-----------|------------|
| id | String (PK) | ID node (sama dengan roomId atau checkpoint) |
| x | Number | Koordinat X SVG |
| y | Number | Koordinat Y SVG |
| floor | Number | Nomor lantai |
| neighbors | Array<String> | Array ID node tetangga yang terhubung |
| isCheckpoint | Boolean | Apakah node adalah checkpoint |
| checkpointType | String | Tipe checkpoint (stairs, elevator, parking) |

**Contoh Data:**
```javascript
{
  id: "IGD",
  x: 632.95538,
  y: 753.07831,
  floor: 1,
  neighbors: ["Poli_Umum", "Farmasi", "Check_Point_Tangga_Utama"],
  isCheckpoint: false
}
```

---

## 3.3 Entity Relationship Diagram (ERD)

```
┌─────────────────────────────────────────────────────────────┐
│                      hospitalRooms                           │
├─────────────────────────────────────────────────────────────┤
│ PK  id              : String                                 │
│     name            : String                                 │
│     category        : String                                 │
│     locationHint    : String                                 │
│     description     : Text                                   │
│     floor           : Number                                 │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 1
                              │
                              │ has many
                              │
                              │ *
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        qrAnchors                             │
├─────────────────────────────────────────────────────────────┤
│ PK  qrId            : String                                 │
│ FK  roomId          : String  ───────────────────────┐       │
│     svgX            : Number                         │       │
│     svgY            : Number                         │       │
│     label           : String                         │       │
│     floor           : Number                         │       │
│     routeNodeId     : String (Optional)              │       │
└─────────────────────────────────────────────────────────────┘
                              │                        │
                              │ references             │
                              │                        │
                              ▼                        │
┌─────────────────────────────────────────────────────────────┐
│                   hospitalRouteGraph                         │
├─────────────────────────────────────────────────────────────┤
│ PK  id              : String  ◄──────────────────────┘       │
│     x               : Number                                 │
│     y               : Number                                 │
│     floor           : Number                                 │
│     neighbors       : Array<String>                          │
│     isCheckpoint    : Boolean                                │
│     checkpointType  : String                                 │
└─────────────────────────────────────────────────────────────┘
```

**Relasi:**
- **hospitalRooms** (1) → (*) **qrAnchors**: Satu ruangan dapat memiliki banyak QR anchor
- **qrAnchors** → **hospitalRouteGraph**: QR anchor dapat reference ke route node via routeNodeId
- **hospitalRouteGraph** self-reference: Node terhubung dengan node lain via neighbors array

---

## 3.4 Alur Sistem (Request → Controller → Data → Response)

### Contoh Alur: User Scan QR Code untuk Navigasi

```
┌──────────────────────────────────────────────────────────────┐
│ 1. USER ACTION                                               │
│    User scan QR code "QR-F1-N01" menggunakan kamera         │
└──────────────────────────────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│ 2. FRONTEND - QR Scanner Component                          │
│    - Decode QR code menggunakan jsQR library                │
│    - Extract qrId dari QR data                              │
│    - Call API: POST /api/v1/qr-anchors/resolve              │
└──────────────────────────────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│ 3. HTTP REQUEST                                              │
│    POST http://localhost:3001/api/v1/qr-anchors/resolve     │
│    Body: { "qrCode": "QR-F1-N01" }                          │
└──────────────────────────────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│ 4. BACKEND - Express Router                                  │
│    - Route: POST /qr-anchors/resolve                        │
│    - Handler: qrAnchorsController.resolve()                 │
└──────────────────────────────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│ 5. DATA LAYER - qrAnchors.js                                │
│    - Search qrAnchors array by qrId                         │
│    - Return anchor data if found                            │
│    - Throw error if not found                               │
└──────────────────────────────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│ 6. HTTP RESPONSE                                             │
│    Status: 200 OK                                            │
│    Body: {                                                   │
│      "success": true,                                        │
│      "data": {                                               │
│        "qrId": "QR-F1-N01",                                 │
│        "roomId": "IGD",                                     │
│        "svgX": 632.95538,                                   │
│        "svgY": 753.07831,                                   │
│        "floor": 1                                           │
│      }                                                       │
│    }                                                         │
└──────────────────────────────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│ 7. FRONTEND - React Query Cache Update                      │
│    - Store anchor data in cache                             │
│    - Trigger component re-render                            │
└──────────────────────────────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│ 8. UI UPDATE - MapViewer Component                          │
│    - Set user position to (svgX, svgY)                      │
│    - Display user arrow marker on map                       │
│    - Switch to correct floor view                           │
│    - Enable navigation mode                                 │
└──────────────────────────────────────────────────────────────┘
```

### Alur Pathfinding (Navigasi dari A ke B)

```
1. User memilih destination room dari SearchBar
2. Frontend call calculatePath(startNode, endNode, startFloor, endFloor)
3. Pathfinding algorithm (Dijkstra/A*):
   - Build graph dari hospitalRouteGraph
   - Handle multi-floor dengan checkpoint nodes
   - Calculate shortest path
4. Return array of coordinates: [{x, y, floor}, ...]
5. MapViewer render path sebagai polyline SVG
6. Display step-by-step directions di NavigationDialog
```

---

## 3.5 Flowchart User (Donatur/Pengunjung)

```
                    ┌─────────────────┐
                    │   Start/Open    │
                    │   Application   │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │  Halaman Home   │
                    │  (Welcome Page) │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │ Pilih Aksi:     │
                    │ 1. Scan QR      │
                    │ 2. Search Room  │
                    │ 3. Browse Map   │
                    └────────┬────────┘
                             │
                ┌────────────┼────────────┐
                │            │            │
                ▼            ▼            ▼
        ┌──────────┐  ┌──────────┐  ┌──────────┐
        │ Scan QR  │  │  Search  │  │  Browse  │
        │   Code   │  │   Room   │  │   Map    │
        └────┬─────┘  └────┬─────┘  └────┬─────┘
             │             │             │
             │             │             │
             ▼             ▼             ▼
        ┌──────────────────────────────────┐
        │   Decode QR → Get Coordinates    │
        │   Search → Get Room Info         │
        │   Browse → Select Floor          │
        └────────────┬─────────────────────┘
                     │
                     ▼
        ┌──────────────────────────────────┐
        │    Display Map with Position     │
        │    - Show user location arrow    │
        │    - Highlight destination       │
        └────────────┬─────────────────────┘
                     │
                     ▼
        ┌──────────────────────────────────┐
        │   User Pilih Destination?        │
        └────────────┬─────────────────────┘
                     │
            ┌────────┴────────┐
            │                 │
           Ya                Tidak
            │                 │
            ▼                 ▼
   ┌─────────────────┐  ┌──────────────┐
   │ Calculate Path  │  │ Explore Map  │
   │ (Pathfinding)   │  │ Freely       │
   └────────┬────────┘  └──────────────┘
            │
            ▼
   ┌─────────────────┐
   │  Display Route  │
   │  - Polyline     │
   │  - Directions   │
   │  - Distance     │
   └────────┬────────┘
            │
            ▼
   ┌─────────────────┐
   │ Follow Route    │
   │ to Destination  │
   └────────┬────────┘
            │
            ▼
   ┌─────────────────┐
   │    Arrived!     │
   │  Show Success   │
   └────────┬────────┘
            │
            ▼
   ┌─────────────────┐
   │  End / Return   │
   │   to Home       │
   └─────────────────┘
```

---

## 3.6 Flowchart Admin (Pengelola Sistem)

```
                    ┌─────────────────┐
                    │  Admin Login    │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │ Verifikasi      │
                    │ Credentials     │
                    └────────┬────────┘
                             │
                    ┌────────┴────────┐
                    │                 │
                 Valid            Invalid
                    │                 │
                    ▼                 ▼
        ┌──────────────────┐   ┌──────────────┐
        │ Admin Dashboard  │   │ Show Error   │
        └────────┬─────────┘   │ Return Login │
                 │              └──────────────┘
                 ▼
        ┌──────────────────┐
        │ Pilih Menu:      │
        │ 1. Kelola Room   │
        │ 2. Kelola QR     │
        │ 3. Kelola Route  │
        │ 4. View Stats    │
        └────────┬─────────┘
                 │
    ┌────────────┼────────────┬────────────┐
    │            │            │            │
    ▼            ▼            ▼            ▼
┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐
│ Rooms  │  │   QR   │  │ Routes │  │ Stats  │
│  CRUD  │  │  CRUD  │  │  CRUD  │  │  View  │
└───┬────┘  └───┬────┘  └───┬────┘  └────────┘
    │           │           │
    ▼           ▼           ▼
┌─────────────────────────────────┐
│      Pilih Aksi CRUD:           │
│      - Create (Tambah)          │
│      - Read (Lihat)             │
│      - Update (Edit)            │
│      - Delete (Hapus)           │
└────────────┬────────────────────┘
             │
    ┌────────┴────────┐
    │                 │
 Create          Read/Update/Delete
    │                 │
    ▼                 ▼
┌─────────┐      ┌─────────┐
│  Form   │      │  List   │
│  Input  │      │  Data   │
└────┬────┘      └────┬────┘
     │                │
     ▼                ▼
┌─────────┐      ┌─────────┐
│Validate │      │ Select  │
│  Data   │      │  Item   │
└────┬────┘      └────┬────┘
     │                │
     ▼                ▼
┌─────────┐      ┌─────────┐
│  Save   │      │  Edit   │
│   to    │      │   or    │
│  Data   │      │ Delete  │
└────┬────┘      └────┬────┘
     │                │
     └────────┬───────┘
              │
              ▼
     ┌─────────────────┐
     │  Show Success   │
     │    Message      │
     └────────┬────────┘
              │
              ▼
     ┌─────────────────┐
     │ Return to Menu  │
     │   or Logout     │
     └─────────────────┘
```

---

## 3.7 Desain SVG Map dan Koordinat

### Struktur File SVG

Sistem menggunakan **3 file SVG** untuk representasi visual peta rumah sakit:

1. **hospital-map.svg** - Peta Lantai 1 (Ground Floor)
2. **hospital-map-lantai-2.svg** - Peta Lantai 2
3. **Lahan Parkir Lantai 1.svg** - Peta Area Parkir

### Spesifikasi SVG

```xml
<svg 
  width="2000" 
  height="1500" 
  viewBox="0 0 2000 1500"
  xmlns="http://www.w3.org/2000/svg">
  
  <!-- Background -->
  <rect width="2000" height="1500" fill="#f5f5f5"/>
  
  <!-- Rooms/Areas -->
  <g id="rooms">
    <rect id="IGD" x="500" y="700" width="200" height="150" 
          fill="#ff6b6b" stroke="#000" stroke-width="2"/>
    <text x="600" y="775" text-anchor="middle">IGD</text>
  </g>
  
  <!-- Pathways/Corridors -->
  <g id="corridors">
    <path d="M 600 850 L 600 1000" 
          stroke="#999" stroke-width="10" fill="none"/>
  </g>
  
  <!-- QR Anchor Points -->
  <g id="qr-anchors">
    <circle cx="632.95538" cy="753.07831" r="10" 
            fill="#4CAF50" opacity="0.7"/>
  </g>
  
  <!-- Route Nodes (Hidden) -->
  <g id="route-nodes" display="none">
    <circle cx="632.95538" cy="753.07831" r="5" fill="red"/>
  </g>
</svg>
```

### Sistem Koordinat

- **Origin (0,0)**: Top-left corner
- **X-axis**: Horizontal, kanan positif
- **Y-axis**: Vertical, bawah positif
- **Unit**: Pixels (px)

### Mapping Koordinat ke Data

```typescript
// QR Anchor dengan koordinat SVG
const qrAnchor = {
  qrId: "QR-F1-N01",
  svgX: 632.95538,  // Koordinat X pada SVG
  svgY: 753.07831,  // Koordinat Y pada SVG
  floor: 1
};

// Route Node dengan koordinat yang sama
const routeNode = {
  id: "IGD",
  x: 632.95538,
  y: 753.07831,
  floor: 1,
  neighbors: ["Poli_Umum", "Farmasi"]
};
```

### Proses Pembuatan SVG Map

1. **Design Phase**
   - Buat layout rumah sakit di software design (Inkscape/Illustrator)
   - Tentukan skala dan proporsi
   - Tandai lokasi ruangan, koridor, tangga, elevator

2. **Export SVG**
   - Export sebagai SVG dengan koordinat absolut
   - Pastikan viewBox konsisten
   - Simpan layer terpisah untuk rooms, corridors, markers

3. **Extract Coordinates**
   - Buka SVG di text editor
   - Extract koordinat (x, y) dari setiap elemen penting
   - Catat koordinat untuk QR anchor placement

4. **Data Entry**
   - Input koordinat ke `qrAnchors.js`
   - Input koordinat ke `hospitalRouteGraph.ts`
   - Verifikasi koordinat dengan visual testing

5. **Testing**
   - Test QR scan positioning
   - Test pathfinding visualization
   - Adjust koordinat jika diperlukan

### Tools untuk Generate QR Code

Script `generate-qrcodes.ts` digunakan untuk generate QR code images:

```bash
# Generate QR untuk semua anchors
npm run qr:generate:anchors

# Generate QR untuk lantai tertentu
npm run qr:generate:floor2

# Generate QR untuk parking
npm run qr:generate:parking
```

Output: PNG files di `public/images/qr/` dengan naming `anchor_QR-F1-N01.png`

---

## 3.8 Algoritma Pathfinding

Sistem menggunakan **Dijkstra's Algorithm** dengan modifikasi untuk multi-floor navigation.

### Pseudocode

```
function calculatePath(startNodeId, endNodeId, startFloor, endFloor):
  // Initialize
  graph = buildGraphFromRouteData()
  distances = {}
  previous = {}
  unvisited = new PriorityQueue()
  
  // Set all distances to infinity
  for each node in graph:
    distances[node] = Infinity
    previous[node] = null
  
  distances[startNodeId] = 0
  unvisited.enqueue(startNodeId, 0)
  
  // Dijkstra's algorithm
  while unvisited is not empty:
    currentNode = unvisited.dequeue()
    
    if currentNode == endNodeId:
      break
    
    for each neighbor in currentNode.neighbors:
      // Calculate distance
      distance = distances[currentNode] + 
                 euclideanDistance(currentNode, neighbor)
      
      // Add penalty for floor changes
      if currentNode.floor != neighbor.floor:
        distance += FLOOR_CHANGE_PENALTY
      
      if distance < distances[neighbor]:
        distances[neighbor] = distance
        previous[neighbor] = currentNode
        unvisited.enqueue(neighbor, distance)
  
  // Reconstruct path
  path = []
  current = endNodeId
  while current != null:
    path.unshift(current)
    current = previous[current]
  
  // Convert node IDs to coordinates
  coordinatePath = path.map(nodeId => {
    node = graph[nodeId]
    return { x: node.x, y: node.y, floor: node.floor }
  })
  
  return coordinatePath
```

### Handling Multi-Floor Navigation

```typescript
// Checkpoint nodes untuk perpindahan lantai
const checkpoints = {
  "Check_Point_Tangga_Utama": {
    floor: 1,
    connectedTo: "Check_Point_Tangga_Utama_L2" // Lantai 2
  },
  "Check_Point_Tangga_Pengunjung_Parkir_Lantai_2": {
    floor: 2,
    connectedTo: "Tangga_Pengunjung_di_Lahan_Parkir_lantai_1" // Parkir
  }
};

// Routing logic
if (startFloor !== endFloor) {
  // Find appropriate checkpoint
  checkpoint = findNearestCheckpoint(startNode, endFloor);
  
  // Split path into segments
  pathSegment1 = calculatePath(startNode, checkpoint, startFloor);
  pathSegment2 = calculatePath(checkpoint, endNode, endFloor);
  
  // Combine paths
  fullPath = [...pathSegment1, ...pathSegment2];
}
```

---

## 3.9 API Endpoints Design

### Base URL
```
Development: http://localhost:3001/api/v1
Production: https://api.hospital-navigator.com/api/v1
```

### Endpoint Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Health check |
| GET | `/rooms` | Get all rooms |
| GET | `/rooms/:id` | Get room by ID |
| GET | `/rooms/categories` | Get room categories |
| POST | `/rooms` | Create/update room |
| PUT | `/rooms/:id` | Update room |
| DELETE | `/rooms/:id` | Delete room |
| GET | `/qr-anchors` | Get all QR anchors |
| GET | `/qr-anchors/:qrId` | Get QR anchor by ID |
| POST | `/qr-anchors/resolve` | Resolve QR code |
| GET | `/qr-anchors/stats` | Get QR statistics |
| POST | `/qr-anchors` | Create/update QR anchor |
| PUT | `/qr-anchors/:qrId` | Update QR anchor |
| DELETE | `/qr-anchors/:qrId` | Delete QR anchor |

### Request/Response Format

**Standard Success Response:**
```json
{
  "success": true,
  "data": { ... },
  "message": "Optional message",
  "count": 10  // For list endpoints
}
```

**Standard Error Response:**
```json
{
  "success": false,
  "error": "Error message",
  "stack": "Stack trace (dev only)"
}
```

---

Dokumen ini menjelaskan perencanaan sistem Hospital Map Viewer secara lengkap, dari arsitektur hingga detail implementasi teknis.
