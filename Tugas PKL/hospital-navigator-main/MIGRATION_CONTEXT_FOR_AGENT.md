# Migration Context: Static Files to API Backend - Complete Guide for Agent

## Project Overview

**Project Name:** Hospital Navigator  
**Type:** React + TypeScript (Frontend) + Express.js (Backend)  
**Goal:** Migrate from static file imports to API-based data fetching

## Current Architecture

### Frontend (React + Vite + TypeScript)
- **Location:** `/src`
- **Data Files (Static):**
  - `src/data/hospitalRoomInfo.ts` - 80+ hospital rooms data
  - `src/data/hospitalRouteGraph.ts` - QR anchors and routing graph
  - `src/data/qrAnchors.ts` - QR code registry

- **Key Components:**
  - `src/components/hospital/SearchBar.tsx` - Uses `roomInfoBySvgId`
  - `src/components/hospital/MapViewer.tsx` - Uses `QR_ANCHOR_REGISTRY`
  - `src/components/hospital/NavigationDialog.tsx` - Uses both rooms and QR data
  - `src/components/hospital/LocationInfoCard.tsx` - Uses room info
  - `src/pages/Index.tsx` - Main page

- **Current Imports Pattern:**
```typescript
import { roomInfoBySvgId, type HospitalRoomInfo } from "@/data/hospitalRoomInfo";
import { QR_ANCHOR_REGISTRY, resolveQrAnchor } from "@/data/hospitalRouteGraph";
```

### Backend (Express.js)
- **Location:** `/server`
- **Data Files:**
  - `server/src/data/hospitalRooms.js` - OLD (incomplete, only ~15 rooms)
  - `server/src/data/hospitalRooms.complete.js` - NEW (complete, 80+ rooms) ✅
  - `server/src/data/qrAnchors.js` - Complete QR anchors ✅

- **API Endpoints (Already Available):**
  - `GET /api/v1/rooms` - Get all rooms
  - `GET /api/v1/rooms/:id` - Get room by ID
  - `GET /api/v1/rooms/category/:category` - Get rooms by category
  - `GET /api/v1/rooms/floor/:floor` - Get rooms by floor
  - `GET /api/v1/rooms/search?q=query` - Search rooms
  - `GET /api/v1/qr-anchors` - Get all QR anchors
  - `GET /api/v1/qr-anchors/:qrId` - Get QR anchor by ID
  - `GET /api/v1/qr-anchors/room/:roomId` - Get QR anchors by room
  - `GET /api/v1/qr-anchors/floor/:floor` - Get QR anchors by floor
  - `GET /api/v1/qr-anchors/resolve?qr=code` - Resolve QR code

- **Routes Location:** `server/src/routes/`

## Data Structure

### Hospital Room
```typescript
interface HospitalRoomInfo {
  id: string;           // e.g., "IGD", "Lab", "R._Korea"
  name: string;         // e.g., "IGD", "Laboratorium"
  category: string;     // Emergency, Outpatient, Critical Care, etc.
  locationHint: string; // e.g., "Sayap kiri bawah peta"
  description: string;  // Full description
  floor: number;        // -1 (Parkir L2), 0 (Parkir L1), 1 (Lantai 1), 2 (Lantai 2)
}
```

### QR Anchor
```typescript
interface QrAnchor {
  qrId: string;         // e.g., "QR-F1-N01"
  roomId: string;       // Foreign key to room
  svgX: number;         // SVG coordinate X
  svgY: number;         // SVG coordinate Y
  label: string;        // Description
  floor: number;        // Floor number
  routeNodeId?: string; // Optional routing node
}
```

## Migration Tasks

### PHASE 1: Backend Preparation

#### Task 1.1: Replace Old Backend Data
**File:** `server/src/data/hospitalRooms.js`
**Action:** Replace entire content with `hospitalRooms.complete.js`
**Why:** Current file only has ~15 rooms, need all 80+ rooms

**Steps:**
1. Backup current `hospitalRooms.js`
2. Copy content from `hospitalRooms.complete.js`
3. Paste into `hospitalRooms.js`
4. Delete `hospitalRooms.complete.js` (no longer needed)

#### Task 1.2: Verify Backend Routes
**Files to check:**
- `server/src/routes/rooms.js`
- `server/src/routes/qrAnchors.js`
- `server/src/routes/index.js`

**Verify:**
- All endpoints are properly exported
- Error handling is in place
- Response format is consistent

#### Task 1.3: Test Backend
**Command:** `cd server && npm run dev`
**Port:** 3001
**Test endpoints with curl or browser:**
```bash
curl http://localhost:3001/api/v1/rooms
curl http://localhost:3001/api/v1/qr-anchors
```

### PHASE 2: Frontend API Service Layer

#### Task 2.1: Create API Service
**File:** `src/services/api.ts` (NEW FILE)
**Content:**
```typescript
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3001/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export const roomsApi = {
  getAll: () => api.get('/rooms'),
  getById: (id: string) => api.get(`/rooms/${id}`),
  getByCategory: (category: string) => api.get(`/rooms/category/${category}`),
  getByFloor: (floor: number) => api.get(`/rooms/floor/${floor}`),
  search: (query: string) => api.get(`/rooms/search?q=${encodeURIComponent(query)}`),
};

export const qrAnchorsApi = {
  getAll: () => api.get('/qr-anchors'),
  getById: (qrId: string) => api.get(`/qr-anchors/${qrId}`),
  getByRoomId: (roomId: string) => api.get(`/qr-anchors/room/${roomId}`),
  getByFloor: (floor: number) => api.get(`/qr-anchors/floor/${floor}`),
  resolve: (qrCode: string) => api.get(`/qr-anchors/resolve?qr=${encodeURIComponent(qrCode)}`),
};

export default api;
```

#### Task 2.2: Create React Query Hooks
**File:** `src/hooks/useHospitalData.ts` (NEW FILE)
**Content:**
```typescript
import { useQuery } from '@tanstack/react-query';
import { roomsApi, qrAnchorsApi } from '@/services/api';
import type { HospitalRoomInfo } from '@/data/hospitalRoomInfo';
import type { QrAnchor } from '@/data/hospitalRouteGraph';

export const useRooms = () => {
  return useQuery<HospitalRoomInfo[]>({
    queryKey: ['rooms'],
    queryFn: async () => {
      const response = await roomsApi.getAll();
      return response.data.data;
    },
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

export const useRoomById = (id: string) => {
  return useQuery<HospitalRoomInfo>({
    queryKey: ['room', id],
    queryFn: async () => {
      const response = await roomsApi.getById(id);
      return response.data.data;
    },
    enabled: !!id,
    staleTime: 5 * 60 * 1000,
  });
};

export const useRoomsByCategory = (category: string) => {
  return useQuery<HospitalRoomInfo[]>({
    queryKey: ['rooms', 'category', category],
    queryFn: async () => {
      const response = await roomsApi.getByCategory(category);
      return response.data.data;
    },
    enabled: !!category,
    staleTime: 5 * 60 * 1000,
  });
};

export const useRoomsByFloor = (floor: number) => {
  return useQuery<HospitalRoomInfo[]>({
    queryKey: ['rooms', 'floor', floor],
    queryFn: async () => {
      const response = await roomsApi.getByFloor(floor);
      return response.data.data;
    },
    staleTime: 5 * 60 * 1000,
  });
};

export const useSearchRooms = (query: string) => {
  return useQuery<HospitalRoomInfo[]>({
    queryKey: ['rooms', 'search', query],
    queryFn: async () => {
      const response = await roomsApi.search(query);
      return response.data.data;
    },
    enabled: query.length > 0,
    staleTime: 1 * 60 * 1000, // 1 minute for search
  });
};

export const useQrAnchors = () => {
  return useQuery<QrAnchor[]>({
    queryKey: ['qrAnchors'],
    queryFn: async () => {
      const response = await qrAnchorsApi.getAll();
      return response.data.data;
    },
    staleTime: 5 * 60 * 1000,
  });
};

export const useQrAnchorById = (qrId: string) => {
  return useQuery<QrAnchor>({
    queryKey: ['qrAnchor', qrId],
    queryFn: async () => {
      const response = await qrAnchorsApi.getById(qrId);
      return response.data.data;
    },
    enabled: !!qrId,
    staleTime: 5 * 60 * 1000,
  });
};

export const useQrAnchorsByFloor = (floor: number) => {
  return useQuery<QrAnchor[]>({
    queryKey: ['qrAnchors', 'floor', floor],
    queryFn: async () => {
      const response = await qrAnchorsApi.getByFloor(floor);
      return response.data.data;
    },
    staleTime: 5 * 60 * 1000,
  });
};

export const useResolveQrCode = (qrCode: string) => {
  return useQuery<QrAnchor>({
    queryKey: ['qrAnchor', 'resolve', qrCode],
    queryFn: async () => {
      const response = await qrAnchorsApi.resolve(qrCode);
      return response.data.data;
    },
    enabled: !!qrCode,
    staleTime: 5 * 60 * 1000,
  });
};
```

#### Task 2.3: Create Helper to Convert API Data
**File:** `src/utils/apiHelpers.ts` (NEW FILE)
**Purpose:** Convert API array to object format (for backward compatibility)
```typescript
import type { HospitalRoomInfo } from '@/data/hospitalRoomInfo';
import type { QrAnchor } from '@/data/hospitalRouteGraph';

/**
 * Convert rooms array to object keyed by ID
 * For backward compatibility with existing code
 */
export const roomsArrayToObject = (rooms: HospitalRoomInfo[]): Record<string, HospitalRoomInfo> => {
  return rooms.reduce((acc, room) => {
    acc[room.id] = room;
    return acc;
  }, {} as Record<string, HospitalRoomInfo>);
};

/**
 * Convert QR anchors array to object keyed by qrId
 * For backward compatibility with existing code
 */
export const qrAnchorsArrayToObject = (anchors: QrAnchor[]): Record<string, QrAnchor> => {
  return anchors.reduce((acc, anchor) => {
    acc[anchor.qrId] = anchor;
    return acc;
  }, {} as Record<string, QrAnchor>);
};
```

### PHASE 3: Update Components

#### Task 3.1: Update SearchBar Component
**File:** `src/components/hospital/SearchBar.tsx`

**Current code pattern:**
```typescript
import { roomInfoBySvgId, type HospitalRoomInfo } from "@/data/hospitalRoomInfo";
const roomList = Object.values(roomInfoBySvgId).filter((room) => {
  return room.id !== "R._Tunggu" && 
         room.id !== "R._Tunggu_Keluarga_Pasien" && 
         room.id !== "Nurse_Station";
});
```

**New code pattern:**
```typescript
import { useRooms } from "@/hooks/useHospitalData";
import type { HospitalRoomInfo } from "@/data/hospitalRoomInfo";

const SearchBar = ({ onSelectLocation, language }: SearchBarProps) => {
  const { data: rooms, isLoading, error } = useRooms();
  
  const roomList = (rooms || []).filter((room) => {
    return room.id !== "R._Tunggu" && 
           room.id !== "R._Tunggu_Keluarga_Pasien" && 
           room.id !== "Nurse_Station";
  });
  
  // Add loading state
  if (isLoading) {
    return <div>Loading rooms...</div>;
  }
  
  // Add error state
  if (error) {
    return <div>Error loading rooms. Please try again.</div>;
  }
  
  // Rest of component remains the same
  // ...
}
```

#### Task 3.2: Update NavigationDialog Component
**File:** `src/components/hospital/NavigationDialog.tsx`

**Current code:**
```typescript
import { roomInfoBySvgId } from "@/data/hospitalRoomInfo";
import { resolveQrAnchor, resolveRoomIdFromQrCode, QR_ANCHOR_REGISTRY } from "@/data/hospitalRouteGraph";

const roomOptions = Object.values(roomInfoBySvgId)
  .filter((room) => {
    return room.id !== "R._Tunggu" && 
           room.id !== "R._Tunggu_Keluarga_Pasien" && 
           room.id !== "Nurse_Station";
  })
  .sort((a, b) => a.name.localeCompare(b.name));
```

**New code:**
```typescript
import { useRooms, useQrAnchors } from "@/hooks/useHospitalData";
import { roomsArrayToObject, qrAnchorsArrayToObject } from "@/utils/apiHelpers";

const NavigationDialog = ({ ... }: NavigationDialogProps) => {
  const { data: rooms, isLoading: roomsLoading } = useRooms();
  const { data: qrAnchors, isLoading: qrLoading } = useQrAnchors();
  
  // Convert to object format for backward compatibility
  const roomInfoBySvgId = roomsArrayToObject(rooms || []);
  const QR_ANCHOR_REGISTRY = qrAnchorsArrayToObject(qrAnchors || []);
  
  const roomOptions = (rooms || [])
    .filter((room) => {
      return room.id !== "R._Tunggu" && 
             room.id !== "R._Tunggu_Keluarga_Pasien" && 
             room.id !== "Nurse_Station";
    })
    .sort((a, b) => a.name.localeCompare(b.name));
  
  if (roomsLoading || qrLoading) {
    return <div>Loading...</div>;
  }
  
  // Rest of component remains the same
  // ...
}
```

#### Task 3.3: Update MapViewer Component
**File:** `src/components/hospital/MapViewer.tsx`

**Current code:**
```typescript
import {
  roomInfoBySvgId,
  roomLabelConfigBySvgId,
  type HospitalRoomInfo,
} from "@/data/hospitalRoomInfo";
import {
  buildRouteForRooms,
  buildRouteFromPoint,
  injectVirtualAnchorNode,
  QR_ANCHOR_REGISTRY,
  getRoutingRoomIds,
  resolveQrAnchor,
  resolveRoomIdFromQrCode,
  type QrAnchor,
  type RoomRouteResult,
} from "@/data/hospitalRouteGraph";
```

**New code:**
```typescript
import { useRooms, useQrAnchors } from "@/hooks/useHospitalData";
import { roomsArrayToObject, qrAnchorsArrayToObject } from "@/utils/apiHelpers";
import {
  roomLabelConfigBySvgId,
  type HospitalRoomInfo,
} from "@/data/hospitalRoomInfo";
import {
  buildRouteForRooms,
  buildRouteFromPoint,
  injectVirtualAnchorNode,
  getRoutingRoomIds,
  resolveQrAnchor,
  resolveRoomIdFromQrCode,
  type QrAnchor,
  type RoomRouteResult,
} from "@/data/hospitalRouteGraph";

const MapViewer = ({ ... }: MapViewerProps) => {
  const { data: rooms, isLoading: roomsLoading } = useRooms();
  const { data: qrAnchors, isLoading: qrLoading } = useQrAnchors();
  
  // Convert to object format
  const roomInfoBySvgId = roomsArrayToObject(rooms || []);
  const QR_ANCHOR_REGISTRY = qrAnchorsArrayToObject(qrAnchors || []);
  
  // Show loading state
  if (roomsLoading || qrLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div>Loading map data...</div>
      </div>
    );
  }
  
  // Rest of component remains the same
  // ...
}
```

#### Task 3.4: Update LocationInfoCard Component
**File:** `src/components/hospital/LocationInfoCard.tsx`

**Current code:**
```typescript
import { roomInfoBySvgId } from "@/data/hospitalRoomInfo";
```

**New code:**
```typescript
import { useRooms } from "@/hooks/useHospitalData";
import { roomsArrayToObject } from "@/utils/apiHelpers";

const LocationInfoCard = ({ ... }: LocationInfoCardProps) => {
  const { data: rooms } = useRooms();
  const roomInfoBySvgId = roomsArrayToObject(rooms || []);
  
  // Rest of component remains the same
  // ...
}
```

### PHASE 4: Environment Configuration

#### Task 4.1: Create Frontend .env
**File:** `.env` (root directory)
```env
VITE_API_URL=http://localhost:3001/api/v1
```

#### Task 4.2: Create Frontend .env.example
**File:** `.env.example`
```env
VITE_API_URL=http://localhost:3001/api/v1
```

#### Task 4.3: Update .gitignore
Ensure `.env` is in `.gitignore`:
```
.env
.env.local
.env.production
```

#### Task 4.4: Verify Backend .env
**File:** `server/.env`
```env
PORT=3001
NODE_ENV=development
CORS_ORIGIN=http://localhost:5173,http://localhost:3000
API_PREFIX=/api/v1
```

### PHASE 5: Testing

#### Task 5.1: Start Backend
```bash
cd server
npm install
npm run dev
```
Verify: Server running on http://localhost:3001

#### Task 5.2: Test Backend Endpoints
```bash
# Test rooms endpoint
curl http://localhost:3001/api/v1/rooms | jq

# Test QR anchors endpoint
curl http://localhost:3001/api/v1/qr-anchors | jq

# Test search
curl "http://localhost:3001/api/v1/rooms/search?q=IGD" | jq
```

#### Task 5.3: Start Frontend
```bash
npm install
npm run dev
```
Verify: Frontend running on http://localhost:5173

#### Task 5.4: Manual Testing Checklist
- [ ] Search bar loads and displays rooms
- [ ] Search functionality works
- [ ] Room selection shows info card
- [ ] Navigation dialog opens and loads rooms
- [ ] QR code scanning works
- [ ] Map displays correctly
- [ ] Floor switching works
- [ ] Navigation routing works
- [ ] No console errors
- [ ] Loading states display properly

### PHASE 6: Cleanup

#### Task 6.1: Keep Static Files (For Now)
**DO NOT DELETE** these files yet (needed for routing logic):
- `src/data/hospitalRoomInfo.ts` - Keep type definitions and label configs
- `src/data/hospitalRouteGraph.ts` - Keep routing functions

**Can be removed later:**
- `roomInfoBySvgId` export (now from API)
- `QR_ANCHOR_REGISTRY` export (now from API)

#### Task 6.2: Update Imports
Update all files to import types only:
```typescript
// Before
import { roomInfoBySvgId, type HospitalRoomInfo } from "@/data/hospitalRoomInfo";

// After
import type { HospitalRoomInfo } from "@/data/hospitalRoomInfo";
```

## Important Notes

### Data Consistency
- Backend data (`hospitalRooms.complete.js`) is synchronized with frontend
- All 80+ rooms are present
- Floor numbering: -1 (Parkir L2), 0 (Parkir L1), 1 (Lantai 1), 2 (Lantai 2)

### Routing Logic
- Keep routing functions in `hospitalRouteGraph.ts` (client-side pathfinding)
- Only migrate data (rooms, QR anchors) to API
- Routing algorithm stays in frontend for performance

### Error Handling
- Add loading states to all components
- Add error states with retry option
- Use React Query's built-in retry mechanism

### Performance
- React Query caching (5 minutes stale time)
- Minimize API calls with proper query keys
- Use `enabled` flag to prevent unnecessary requests

### Backward Compatibility
- Use helper functions to convert array to object
- Maintain same data structure for existing code
- Gradual migration approach

## Success Criteria

✅ Backend serves complete data (80+ rooms, 10+ QR anchors)  
✅ Frontend fetches data from API successfully  
✅ All components display data correctly  
✅ Search functionality works  
✅ Navigation flow works end-to-end  
✅ QR code scanning works  
✅ No console errors  
✅ Loading states display properly  
✅ Error handling works  

## Rollback Plan

If issues occur:
1. Revert component changes
2. Restore static imports
3. Comment out API service calls
4. Keep backend running for future attempts

## Files to Create

1. `src/services/api.ts`
2. `src/hooks/useHospitalData.ts`
3. `src/utils/apiHelpers.ts`
4. `.env`
5. `.env.example`

## Files to Modify

1. `server/src/data/hospitalRooms.js` - Replace with complete version
2. `src/components/hospital/SearchBar.tsx`
3. `src/components/hospital/NavigationDialog.tsx`
4. `src/components/hospital/MapViewer.tsx`
5. `src/components/hospital/LocationInfoCard.tsx`
6. `.gitignore` - Ensure .env is ignored

## Files to Keep (Don't Delete)

1. `src/data/hospitalRoomInfo.ts` - Keep for types and label configs
2. `src/data/hospitalRouteGraph.ts` - Keep for routing functions

## Estimated Time

- Phase 1: 30 minutes
- Phase 2: 1 hour
- Phase 3: 2 hours
- Phase 4: 15 minutes
- Phase 5: 1 hour
- Phase 6: 30 minutes

**Total: ~5 hours**
