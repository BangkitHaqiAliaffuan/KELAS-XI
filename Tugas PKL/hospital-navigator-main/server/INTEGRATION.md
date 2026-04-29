# Frontend Integration Guide

Panduan integrasi Hospital Navigator Backend API dengan frontend React/Vite.

## 📋 Overview

Backend API menyediakan data ruangan dan QR anchor registry yang dapat digunakan oleh frontend untuk:
- Menampilkan daftar ruangan
- Navigasi berbasis QR code
- Pencarian dan filtering ruangan
- Manajemen data (admin)

## 🔧 Setup

### 1. Install Dependencies (Frontend)

```bash
cd hospital-navigator
npm install axios
# atau
npm install @tanstack/react-query axios
```

### 2. Create API Client

Buat file `src/lib/api.ts`:

```typescript
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3001/api/v1';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export default apiClient;
```

### 3. Environment Variables

Tambahkan ke `.env`:

```env
VITE_API_URL=http://localhost:3001/api/v1
```

Production `.env.production`:

```env
VITE_API_URL=https://api.yourdomain.com/api/v1
```

---

## 📡 API Services

### Room Service

Buat file `src/services/roomService.ts`:

```typescript
import apiClient from '@/lib/api';
import type { HospitalRoomInfo } from '@/data/hospitalRoomInfo';

interface ApiResponse<T> {
  success: boolean;
  data: T;
  count?: number;
  message?: string;
}

export const roomService = {
  // Get all rooms
  async getAllRooms(): Promise<HospitalRoomInfo[]> {
    const response = await apiClient.get<ApiResponse<HospitalRoomInfo[]>>('/rooms');
    return response.data.data;
  },

  // Get room by ID
  async getRoomById(roomId: string): Promise<HospitalRoomInfo> {
    const response = await apiClient.get<ApiResponse<HospitalRoomInfo>>(`/rooms/${roomId}`);
    return response.data.data;
  },

  // Get rooms by category
  async getRoomsByCategory(category: string): Promise<HospitalRoomInfo[]> {
    const response = await apiClient.get<ApiResponse<HospitalRoomInfo[]>>('/rooms', {
      params: { category },
    });
    return response.data.data;
  },

  // Get rooms by floor
  async getRoomsByFloor(floor: number): Promise<HospitalRoomInfo[]> {
    const response = await apiClient.get<ApiResponse<HospitalRoomInfo[]>>('/rooms', {
      params: { floor },
    });
    return response.data.data;
  },

  // Search rooms
  async searchRooms(query: string): Promise<HospitalRoomInfo[]> {
    const response = await apiClient.get<ApiResponse<HospitalRoomInfo[]>>('/rooms', {
      params: { search: query },
    });
    return response.data.data;
  },

  // Get categories
  async getCategories(): Promise<string[]> {
    const response = await apiClient.get<ApiResponse<string[]>>('/rooms/categories');
    return response.data.data;
  },

  // Create/Update room (admin)
  async upsertRoom(roomData: Partial<HospitalRoomInfo>): Promise<HospitalRoomInfo> {
    const response = await apiClient.post<ApiResponse<HospitalRoomInfo>>('/rooms', roomData);
    return response.data.data;
  },

  // Delete room (admin)
  async deleteRoom(roomId: string): Promise<void> {
    await apiClient.delete(`/rooms/${roomId}`);
  },
};
```

### QR Anchor Service

Buat file `src/services/qrAnchorService.ts`:

```typescript
import apiClient from '@/lib/api';
import type { QrAnchor } from '@/data/hospitalRouteGraph';

interface ApiResponse<T> {
  success: boolean;
  data: T;
  count?: number;
  message?: string;
}

export const qrAnchorService = {
  // Get all QR anchors
  async getAllQrAnchors(): Promise<QrAnchor[]> {
    const response = await apiClient.get<ApiResponse<QrAnchor[]>>('/qr-anchors');
    return response.data.data;
  },

  // Get QR anchor by ID
  async getQrAnchorById(qrId: string): Promise<QrAnchor> {
    const response = await apiClient.get<ApiResponse<QrAnchor>>(`/qr-anchors/${qrId}`);
    return response.data.data;
  },

  // Get QR anchors by room ID
  async getQrAnchorsByRoomId(roomId: string): Promise<QrAnchor[]> {
    const response = await apiClient.get<ApiResponse<QrAnchor[]>>('/qr-anchors', {
      params: { roomId },
    });
    return response.data.data;
  },

  // Get QR anchors by floor
  async getQrAnchorsByFloor(floor: number): Promise<QrAnchor[]> {
    const response = await apiClient.get<ApiResponse<QrAnchor[]>>('/qr-anchors', {
      params: { floor },
    });
    return response.data.data;
  },

  // Resolve QR code
  async resolveQrCode(qrCode: string): Promise<QrAnchor> {
    const response = await apiClient.post<ApiResponse<QrAnchor>>('/qr-anchors/resolve', {
      qrCode,
    });
    return response.data.data;
  },

  // Get statistics
  async getStats(): Promise<{
    total: number;
    byFloor: Record<string, number>;
    rooms: number;
  }> {
    const response = await apiClient.get('/qr-anchors/stats');
    return response.data.data;
  },

  // Create/Update QR anchor (admin)
  async upsertQrAnchor(anchorData: Partial<QrAnchor>): Promise<QrAnchor> {
    const response = await apiClient.post<ApiResponse<QrAnchor>>('/qr-anchors', anchorData);
    return response.data.data;
  },

  // Delete QR anchor (admin)
  async deleteQrAnchor(qrId: string): Promise<void> {
    await apiClient.delete(`/qr-anchors/${qrId}`);
  },
};
```

---

## 🎣 React Hooks (with React Query)

### Setup React Query

```typescript
// src/main.tsx
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, // 5 minutes
      cacheTime: 10 * 60 * 1000, // 10 minutes
      refetchOnWindowFocus: false,
    },
  },
});

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <App />
    </QueryClientProvider>
  </React.StrictMode>
);
```

### Custom Hooks

Buat file `src/hooks/useRooms.ts`:

```typescript
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { roomService } from '@/services/roomService';
import type { HospitalRoomInfo } from '@/data/hospitalRoomInfo';

export const useRooms = () => {
  return useQuery({
    queryKey: ['rooms'],
    queryFn: roomService.getAllRooms,
  });
};

export const useRoom = (roomId: string) => {
  return useQuery({
    queryKey: ['rooms', roomId],
    queryFn: () => roomService.getRoomById(roomId),
    enabled: !!roomId,
  });
};

export const useRoomsByCategory = (category: string) => {
  return useQuery({
    queryKey: ['rooms', 'category', category],
    queryFn: () => roomService.getRoomsByCategory(category),
    enabled: !!category,
  });
};

export const useRoomsByFloor = (floor: number) => {
  return useQuery({
    queryKey: ['rooms', 'floor', floor],
    queryFn: () => roomService.getRoomsByFloor(floor),
  });
};

export const useSearchRooms = (query: string) => {
  return useQuery({
    queryKey: ['rooms', 'search', query],
    queryFn: () => roomService.searchRooms(query),
    enabled: query.length > 0,
  });
};

export const useCategories = () => {
  return useQuery({
    queryKey: ['categories'],
    queryFn: roomService.getCategories,
  });
};

// Mutations for admin
export const useCreateRoom = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (roomData: Partial<HospitalRoomInfo>) => 
      roomService.upsertRoom(roomData),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rooms'] });
    },
  });
};

export const useDeleteRoom = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (roomId: string) => roomService.deleteRoom(roomId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rooms'] });
    },
  });
};
```

Buat file `src/hooks/useQrAnchors.ts`:

```typescript
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { qrAnchorService } from '@/services/qrAnchorService';
import type { QrAnchor } from '@/data/hospitalRouteGraph';

export const useQrAnchors = () => {
  return useQuery({
    queryKey: ['qr-anchors'],
    queryFn: qrAnchorService.getAllQrAnchors,
  });
};

export const useQrAnchor = (qrId: string) => {
  return useQuery({
    queryKey: ['qr-anchors', qrId],
    queryFn: () => qrAnchorService.getQrAnchorById(qrId),
    enabled: !!qrId,
  });
};

export const useQrAnchorsByFloor = (floor: number) => {
  return useQuery({
    queryKey: ['qr-anchors', 'floor', floor],
    queryFn: () => qrAnchorService.getQrAnchorsByFloor(floor),
  });
};

export const useResolveQrCode = () => {
  return useMutation({
    mutationFn: (qrCode: string) => qrAnchorService.resolveQrCode(qrCode),
  });
};

export const useQrAnchorStats = () => {
  return useQuery({
    queryKey: ['qr-anchors', 'stats'],
    queryFn: qrAnchorService.getStats,
  });
};
```

---

## 💡 Usage Examples

### Example 1: Room List Component

```typescript
import { useRooms } from '@/hooks/useRooms';

export function RoomList() {
  const { data: rooms, isLoading, error } = useRooms();

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error loading rooms</div>;

  return (
    <div>
      <h2>Hospital Rooms</h2>
      <ul>
        {rooms?.map((room) => (
          <li key={room.id}>
            <strong>{room.name}</strong> - {room.category}
            <p>{room.description}</p>
          </li>
        ))}
      </ul>
    </div>
  );
}
```

### Example 2: Search Component

```typescript
import { useState } from 'react';
import { useSearchRooms } from '@/hooks/useRooms';

export function RoomSearch() {
  const [query, setQuery] = useState('');
  const { data: results, isLoading } = useSearchRooms(query);

  return (
    <div>
      <input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search rooms..."
      />
      
      {isLoading && <div>Searching...</div>}
      
      {results && (
        <ul>
          {results.map((room) => (
            <li key={room.id}>{room.name}</li>
          ))}
        </ul>
      )}
    </div>
  );
}
```

### Example 3: QR Code Scanner Integration

```typescript
import { useResolveQrCode } from '@/hooks/useQrAnchors';

export function QrScanner() {
  const resolveQr = useResolveQrCode();

  const handleQrScan = async (qrCode: string) => {
    try {
      const anchor = await resolveQr.mutateAsync(qrCode);
      console.log('QR Anchor:', anchor);
      // Navigate to room or show location
    } catch (error) {
      console.error('QR code not found');
    }
  };

  return (
    <div>
      {/* QR Scanner component */}
      <button onClick={() => handleQrScan('QR-F1-N01')}>
        Test QR Scan
      </button>
    </div>
  );
}
```

### Example 4: Floor Selector

```typescript
import { useRoomsByFloor } from '@/hooks/useRooms';

export function FloorSelector() {
  const [floor, setFloor] = useState(1);
  const { data: rooms } = useRoomsByFloor(floor);

  return (
    <div>
      <select value={floor} onChange={(e) => setFloor(Number(e.target.value))}>
        <option value={0}>Parking L1</option>
        <option value={1}>Floor 1</option>
        <option value={2}>Floor 2</option>
        <option value={-1}>Parking L2</option>
      </select>

      <div>
        <h3>Rooms on Floor {floor}</h3>
        {rooms?.map((room) => (
          <div key={room.id}>{room.name}</div>
        ))}
      </div>
    </div>
  );
}
```

---

## 🔄 Migration Strategy

### Phase 1: Parallel Running

1. Keep existing static data
2. Add API integration alongside
3. Use feature flag to switch between sources

```typescript
const USE_API = import.meta.env.VITE_USE_API === 'true';

export function useRoomData() {
  if (USE_API) {
    return useRooms(); // From API
  } else {
    return { data: roomInfoBySvgId }; // Static data
  }
}
```

### Phase 2: Gradual Migration

1. Migrate read operations first
2. Test thoroughly
3. Add write operations (admin)
4. Remove static data

### Phase 3: Full API Integration

1. Remove static data files
2. Use API as single source of truth
3. Add caching strategy
4. Implement offline support (optional)

---

## 🎯 Best Practices

1. **Error Handling**
```typescript
try {
  const rooms = await roomService.getAllRooms();
} catch (error) {
  if (axios.isAxiosError(error)) {
    console.error('API Error:', error.response?.data);
  }
}
```

2. **Loading States**
```typescript
if (isLoading) return <Skeleton />;
if (error) return <ErrorMessage error={error} />;
if (!data) return null;
```

3. **Caching**
```typescript
// Use React Query's built-in caching
const { data } = useRooms(); // Cached for 5 minutes
```

4. **Optimistic Updates**
```typescript
const mutation = useMutation({
  mutationFn: updateRoom,
  onMutate: async (newRoom) => {
    // Optimistically update UI
    await queryClient.cancelQueries({ queryKey: ['rooms'] });
    const previous = queryClient.getQueryData(['rooms']);
    queryClient.setQueryData(['rooms'], (old) => [...old, newRoom]);
    return { previous };
  },
  onError: (err, newRoom, context) => {
    // Rollback on error
    queryClient.setQueryData(['rooms'], context.previous);
  },
});
```

---

## 🧪 Testing

```typescript
// Mock API for testing
import { rest } from 'msw';
import { setupServer } from 'msw/node';

const server = setupServer(
  rest.get('/api/v1/rooms', (req, res, ctx) => {
    return res(ctx.json({
      success: true,
      data: [/* mock rooms */],
    }));
  })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());
```

---

## 📝 Notes

- API menggunakan in-memory storage, data akan hilang saat restart
- Untuk production, pertimbangkan database persistence
- Implementasikan authentication untuk admin endpoints
- Add rate limiting untuk production
- Monitor API performance dan errors
