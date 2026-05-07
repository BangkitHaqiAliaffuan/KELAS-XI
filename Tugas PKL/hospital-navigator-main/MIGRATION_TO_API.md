# Migration Plan: Static Files to API Backend

## Overview
Dokumen ini menjelaskan langkah-langkah untuk migrasi dari penggunaan file static ke API backend Express.js.

## Status Backend

### ✅ Data yang Sudah Lengkap di Backend
1. **Hospital Rooms** - `server/src/data/hospitalRooms.complete.js`
   - 80+ ruangan lengkap (sama dengan frontend)
   - Semua kategori: Emergency, Outpatient, Critical Care, Diagnostic, Facility, Service, Administration, Surgery, Ward, Treatment
   - Support multi-floor: -1 (Parkir L2), 0 (Parkir L1), 1 (Lantai 1), 2 (Lantai 2)

2. **QR Anchors** - `server/src/data/qrAnchors.js`
   - 10+ QR anchors dengan koordinat SVG
   - Support multi-floor
   - Resolusi QR code

### ❌ Data yang Belum Ada di Backend
1. **Hospital Route Graph** - Belum ada di backend
   - Graph nodes dan edges untuk pathfinding
   - Checkpoint connections
   - Multi-floor connectors

## Langkah Migrasi

### Phase 1: Persiapan Backend (SEKARANG)

#### 1.1 Update Backend Data
- [x] Buat `hospitalRooms.complete.js` dengan data lengkap
- [ ] Replace `hospitalRooms.js` dengan versi complete
- [ ] Update QR anchors jika ada yang kurang
- [ ] Buat endpoint baru untuk route graph (opsional untuk fase 1)

#### 1.2 Test Backend API
```bash
cd server
npm run dev
```

Test endpoints:
- GET http://localhost:3001/api/v1/rooms
- GET http://localhost:3001/api/v1/rooms/:id
- GET http://localhost:3001/api/v1/qr-anchors
- GET http://localhost:3001/api/v1/qr-anchors/:qrId

### Phase 2: Frontend API Service Layer

#### 2.1 Buat API Service
File: `src/services/api.ts`
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

export const roomsApi = {
  getAll: () => api.get('/rooms'),
  getById: (id: string) => api.get(`/rooms/${id}`),
  getByCategory: (category: string) => api.get(`/rooms/category/${category}`),
  getByFloor: (floor: number) => api.get(`/rooms/floor/${floor}`),
  search: (query: string) => api.get(`/rooms/search?q=${query}`),
};

export const qrAnchorsApi = {
  getAll: () => api.get('/qr-anchors'),
  getById: (qrId: string) => api.get(`/qr-anchors/${qrId}`),
  getByRoomId: (roomId: string) => api.get(`/qr-anchors/room/${roomId}`),
  getByFloor: (floor: number) => api.get(`/qr-anchors/floor/${floor}`),
  resolve: (qrCode: string) => api.get(`/qr-anchors/resolve?qr=${qrCode}`),
};

export default api;
```

#### 2.2 Buat React Query Hooks
File: `src/hooks/useHospitalData.ts`
```typescript
import { useQuery } from '@tanstack/react-query';
import { roomsApi, qrAnchorsApi } from '@/services/api';

export const useRooms = () => {
  return useQuery({
    queryKey: ['rooms'],
    queryFn: async () => {
      const response = await roomsApi.getAll();
      return response.data.data;
    },
  });
};

export const useRoomById = (id: string) => {
  return useQuery({
    queryKey: ['room', id],
    queryFn: async () => {
      const response = await roomsApi.getById(id);
      return response.data.data;
    },
    enabled: !!id,
  });
};

export const useQrAnchors = () => {
  return useQuery({
    queryKey: ['qrAnchors'],
    queryFn: async () => {
      const response = await qrAnchorsApi.getAll();
      return response.data.data;
    },
  });
};
```

### Phase 3: Update Components

#### 3.1 Update SearchBar Component
```typescript
// Before (static)
import { roomInfoBySvgId } from "@/data/hospitalRoomInfo";
const roomList = Object.values(roomInfoBySvgId);

// After (API)
import { useRooms } from "@/hooks/useHospitalData";
const { data: rooms, isLoading } = useRooms();
const roomList = rooms || [];
```

#### 3.2 Update MapViewer Component
```typescript
// Before (static)
import { QR_ANCHOR_REGISTRY } from "@/data/hospitalRouteGraph";

// After (API)
import { useQrAnchors } from "@/hooks/useHospitalData";
const { data: qrAnchors } = useQrAnchors();
```

#### 3.3 Update NavigationDialog Component
Similar pattern dengan SearchBar

### Phase 4: Environment Configuration

#### 4.1 Frontend .env
```env
VITE_API_URL=http://localhost:3001/api/v1
```

#### 4.2 Backend .env
```env
PORT=3001
NODE_ENV=development
CORS_ORIGIN=http://localhost:5173,http://localhost:3000
```

### Phase 5: Testing

#### 5.1 Backend Tests
- [ ] Test semua endpoints dengan Postman/Thunder Client
- [ ] Verify data consistency dengan frontend static
- [ ] Test CORS configuration

#### 5.2 Frontend Tests
- [ ] Test search functionality
- [ ] Test room selection
- [ ] Test QR code scanning
- [ ] Test navigation flow

### Phase 6: Deployment

#### 6.1 Backend Deployment
- Update production .env
- Deploy ke hosting (Heroku/Railway/Vercel)
- Update CORS untuk production domain

#### 6.2 Frontend Deployment
- Update VITE_API_URL untuk production
- Build dan deploy

## Rollback Plan

Jika terjadi masalah, rollback dengan:
1. Revert import statements ke static files
2. Comment out API service calls
3. Uncomment static imports

## Notes

### Keuntungan Migrasi ke API:
✅ Centralized data management
✅ Easier updates (tidak perlu rebuild frontend)
✅ Better scalability
✅ Support untuk admin panel di masa depan
✅ Real-time data updates

### Pertimbangan:
⚠️ Dependency ke backend server
⚠️ Network latency
⚠️ Perlu error handling yang baik
⚠️ Caching strategy untuk performance

### Route Graph Consideration:
Untuk fase 1, route graph bisa tetap di frontend karena:
- Data graph kompleks dan jarang berubah
- Pathfinding algorithm berjalan di client-side
- Menghindari overhead network untuk data besar

Bisa dimigrasikan ke backend di fase 2 jika diperlukan.

## Timeline Estimasi

- Phase 1: 1-2 jam (persiapan backend)
- Phase 2: 2-3 jam (frontend service layer)
- Phase 3: 3-4 jam (update components)
- Phase 4: 30 menit (configuration)
- Phase 5: 2-3 jam (testing)
- Phase 6: 1-2 jam (deployment)

**Total: 10-15 jam**

## Next Steps

1. ✅ Backup current code
2. ⏳ Replace hospitalRooms.js dengan complete version
3. ⏳ Test backend endpoints
4. ⏳ Create API service layer
5. ⏳ Update components one by one
6. ⏳ Test thoroughly
7. ⏳ Deploy

## Questions?

Apakah Anda ingin:
1. Mulai dengan Phase 1 (update backend)?
2. Lihat contoh implementasi API service?
3. Diskusi strategi caching?
4. Pertimbangan lain?
