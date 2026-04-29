# Hospital Navigator API - Contoh Penggunaan

Dokumen ini berisi contoh-contoh penggunaan API Hospital Navigator.

## Base URL

```
http://localhost:3001/api/v1
```

## 1. Health Check

### Request
```bash
curl http://localhost:3001/api/v1/health
```

### Response
```json
{
  "success": true,
  "message": "Hospital Navigator API is running",
  "timestamp": "2024-01-01T00:00:00.000Z"
}
```

---

## 2. Rooms API

### 2.1 Get All Rooms

```bash
curl http://localhost:3001/api/v1/rooms
```

### 2.2 Get Rooms by Category

```bash
# Get Emergency rooms
curl "http://localhost:3001/api/v1/rooms?category=Emergency"

# Get Facility rooms
curl "http://localhost:3001/api/v1/rooms?category=Facility"
```

### 2.3 Get Rooms by Floor

```bash
# Get Floor 1 rooms
curl "http://localhost:3001/api/v1/rooms?floor=1"

# Get Floor 2 rooms
curl "http://localhost:3001/api/v1/rooms?floor=2"

# Get Parking (Floor 0)
curl "http://localhost:3001/api/v1/rooms?floor=0"
```

### 2.4 Search Rooms

```bash
# Search by name
curl "http://localhost:3001/api/v1/rooms?search=igd"

# Search by description
curl "http://localhost:3001/api/v1/rooms?search=darurat"
```

### 2.5 Get Room by ID

```bash
curl http://localhost:3001/api/v1/rooms/IGD
curl http://localhost:3001/api/v1/rooms/Parking_Lantai_1
```

### 2.6 Get All Categories

```bash
curl http://localhost:3001/api/v1/rooms/categories
```

### 2.7 Create New Room

```bash
curl -X POST http://localhost:3001/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "id": "R._Baru",
    "name": "Ruang Baru",
    "category": "Facility",
    "locationHint": "Lantai 1, sayap kanan",
    "description": "Ruangan baru untuk keperluan administrasi",
    "floor": 1
  }'
```

### 2.8 Update Room

```bash
curl -X PUT http://localhost:3001/api/v1/rooms/R._Baru \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ruang Baru (Updated)",
    "category": "Administration",
    "locationHint": "Lantai 1, sayap kanan (updated)",
    "description": "Ruangan untuk administrasi umum",
    "floor": 1
  }'
```

### 2.9 Delete Room

```bash
curl -X DELETE http://localhost:3001/api/v1/rooms/R._Baru
```

---

## 3. QR Anchors API

### 3.1 Get All QR Anchors

```bash
curl http://localhost:3001/api/v1/qr-anchors
```

### 3.2 Get QR Anchors by Room ID

```bash
# Get QR anchors for IGD
curl "http://localhost:3001/api/v1/qr-anchors?roomId=IGD"

# Get QR anchors for Parking
curl "http://localhost:3001/api/v1/qr-anchors?roomId=Parking_Lantai_1"
```

### 3.3 Get QR Anchors by Floor

```bash
# Floor 1 QR anchors
curl "http://localhost:3001/api/v1/qr-anchors?floor=1"

# Floor 2 QR anchors
curl "http://localhost:3001/api/v1/qr-anchors?floor=2"

# Parking L1 (Floor 0)
curl "http://localhost:3001/api/v1/qr-anchors?floor=0"

# Parking L2 (Floor -1)
curl "http://localhost:3001/api/v1/qr-anchors?floor=-1"
```

### 3.4 Search QR Anchors

```bash
# Search by label
curl "http://localhost:3001/api/v1/qr-anchors?search=persimpangan"

# Search by QR ID
curl "http://localhost:3001/api/v1/qr-anchors?search=QR-F1"
```

### 3.5 Get QR Anchor by ID

```bash
curl http://localhost:3001/api/v1/qr-anchors/QR-F1-N01
curl http://localhost:3001/api/v1/qr-anchors/QR-PK-N01
```

### 3.6 Get QR Anchor Statistics

```bash
curl http://localhost:3001/api/v1/qr-anchors/stats
```

Response:
```json
{
  "success": true,
  "data": {
    "total": 10,
    "byFloor": {
      "-1": 1,
      "0": 1,
      "1": 7,
      "2": 1
    },
    "rooms": 8
  }
}
```

### 3.7 Resolve QR Code

```bash
# Resolve exact QR code
curl -X POST http://localhost:3001/api/v1/qr-anchors/resolve \
  -H "Content-Type: application/json" \
  -d '{"qrCode":"QR-F1-N01"}'

# Resolve with fuzzy matching
curl -X POST http://localhost:3001/api/v1/qr-anchors/resolve \
  -H "Content-Type: application/json" \
  -d '{"qrCode":"QR-PK-N01"}'
```

### 3.8 Create New QR Anchor

```bash
curl -X POST http://localhost:3001/api/v1/qr-anchors \
  -H "Content-Type: application/json" \
  -d '{
    "qrId": "QR-F1-N99",
    "roomId": "IGD",
    "svgX": 650.5,
    "svgY": 750.5,
    "label": "QR Anchor Baru di IGD",
    "floor": 1,
    "routeNodeId": "Node_IGD_Baru"
  }'
```

### 3.9 Update QR Anchor

```bash
curl -X PUT http://localhost:3001/api/v1/qr-anchors/QR-F1-N99 \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "IGD",
    "svgX": 655.0,
    "svgY": 755.0,
    "label": "QR Anchor Updated",
    "floor": 1
  }'
```

### 3.10 Delete QR Anchor

```bash
curl -X DELETE http://localhost:3001/api/v1/qr-anchors/QR-F1-N99
```

---

## 4. Use Cases

### Use Case 1: Get All Emergency Rooms on Floor 1

```bash
# Step 1: Get all rooms on floor 1
curl "http://localhost:3001/api/v1/rooms?floor=1" > floor1_rooms.json

# Step 2: Filter by category (can be done in one request)
curl "http://localhost:3001/api/v1/rooms?category=Emergency" | jq '.data[] | select(.floor == 1)'
```

### Use Case 2: Find QR Anchors for a Specific Room

```bash
# Get all QR anchors for IGD
curl "http://localhost:3001/api/v1/qr-anchors?roomId=IGD"
```

### Use Case 3: Navigation Flow

```bash
# 1. User scans QR code
QR_CODE="QR-F1-N01"

# 2. Resolve QR code to get anchor data
curl -X POST http://localhost:3001/api/v1/qr-anchors/resolve \
  -H "Content-Type: application/json" \
  -d "{\"qrCode\":\"$QR_CODE\"}"

# 3. Get room information
ROOM_ID="IGD"
curl "http://localhost:3001/api/v1/rooms/$ROOM_ID"
```

### Use Case 4: Get All Parking Information

```bash
# Get parking rooms
curl "http://localhost:3001/api/v1/rooms?search=parking"

# Get parking QR anchors
curl "http://localhost:3001/api/v1/qr-anchors?floor=0"
curl "http://localhost:3001/api/v1/qr-anchors?floor=-1"
```

### Use Case 5: Admin Dashboard Data

```bash
# Get statistics
curl http://localhost:3001/api/v1/qr-anchors/stats

# Get all categories
curl http://localhost:3001/api/v1/rooms/categories

# Get total rooms count
curl http://localhost:3001/api/v1/rooms | jq '.count'
```

---

## 5. Error Handling Examples

### 404 - Room Not Found

```bash
curl http://localhost:3001/api/v1/rooms/NONEXISTENT
```

Response:
```json
{
  "success": false,
  "error": "Room not found"
}
```

### 404 - QR Anchor Not Found

```bash
curl -X POST http://localhost:3001/api/v1/qr-anchors/resolve \
  -H "Content-Type: application/json" \
  -d '{"qrCode":"INVALID-QR"}'
```

Response:
```json
{
  "success": false,
  "error": "QR code not found"
}
```

### 400 - Invalid Request

```bash
curl -X POST http://localhost:3001/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Room Without ID"
  }'
```

Response:
```json
{
  "success": false,
  "error": "Room must have id, name, and category"
}
```

---

## 6. Using with JavaScript/Frontend

### Fetch API Example

```javascript
// Get all rooms
async function getAllRooms() {
  const response = await fetch('http://localhost:3001/api/v1/rooms');
  const data = await response.json();
  return data.data;
}

// Resolve QR code
async function resolveQrCode(qrCode) {
  const response = await fetch('http://localhost:3001/api/v1/qr-anchors/resolve', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ qrCode }),
  });
  const data = await response.json();
  return data.data;
}

// Search rooms
async function searchRooms(query) {
  const response = await fetch(`http://localhost:3001/api/v1/rooms?search=${encodeURIComponent(query)}`);
  const data = await response.json();
  return data.data;
}
```

### Axios Example

```javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:3001/api/v1';

// Get room by ID
async function getRoomById(roomId) {
  const response = await axios.get(`${API_BASE_URL}/rooms/${roomId}`);
  return response.data.data;
}

// Create new room
async function createRoom(roomData) {
  const response = await axios.post(`${API_BASE_URL}/rooms`, roomData);
  return response.data.data;
}

// Get QR anchors by floor
async function getQrAnchorsByFloor(floor) {
  const response = await axios.get(`${API_BASE_URL}/qr-anchors`, {
    params: { floor },
  });
  return response.data.data;
}
```

---

## 7. Testing with Postman

Import this collection to Postman:

1. Create new collection: "Hospital Navigator API"
2. Add requests for each endpoint
3. Set base URL as variable: `{{baseUrl}}` = `http://localhost:3001/api/v1`
4. Create environment with variables

Example Postman Collection structure:
```
Hospital Navigator API/
├── Health Check
├── Rooms/
│   ├── Get All Rooms
│   ├── Get Room by ID
│   ├── Search Rooms
│   ├── Get Categories
│   ├── Create Room
│   ├── Update Room
│   └── Delete Room
└── QR Anchors/
    ├── Get All QR Anchors
    ├── Get QR Anchor by ID
    ├── Get Statistics
    ├── Resolve QR Code
    ├── Create QR Anchor
    ├── Update QR Anchor
    └── Delete QR Anchor
```

---

## 8. Tips & Best Practices

1. **Always check the response status**:
   ```javascript
   if (response.data.success) {
     // Handle success
   } else {
     // Handle error
   }
   ```

2. **Use query parameters for filtering**:
   - Prefer `?category=Emergency` over fetching all and filtering client-side

3. **Handle errors gracefully**:
   ```javascript
   try {
     const data = await fetchData();
   } catch (error) {
     console.error('API Error:', error.message);
   }
   ```

4. **Cache frequently accessed data**:
   - Categories list
   - Room information
   - QR anchor registry

5. **Use environment variables**:
   ```javascript
   const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:3001/api/v1';
   ```
