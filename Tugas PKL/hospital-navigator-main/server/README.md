# Hospital Navigator Backend API

Backend API untuk Hospital Navigator - sistem navigasi rumah sakit berbasis web yang mengelola data ruangan dan QR anchor registry.

## 📋 Fitur

- **Room Management**: CRUD operations untuk data ruangan rumah sakit
- **QR Anchor Registry**: Manajemen QR code dengan koordinat SVG untuk navigasi
- **Search & Filter**: Pencarian dan filter berdasarkan kategori, lantai, dll
- **RESTful API**: API yang mengikuti standar REST
- **CORS Support**: Mendukung cross-origin requests
- **Error Handling**: Error handling yang konsisten
- **Logging**: Request logging dengan Morgan

## 🚀 Quick Start

### Prerequisites

- Node.js >= 16.x
- npm atau yarn

### Installation

1. Navigate ke direktori server:
```bash
cd server
```

2. Install dependencies:
```bash
npm install
```

3. Copy file environment:
```bash
cp .env.example .env
```

4. Edit `.env` sesuai kebutuhan:
```env
PORT=3001
NODE_ENV=development
CORS_ORIGIN=http://localhost:5173
API_PREFIX=/api/v1
```

5. Start development server:
```bash
npm run dev
```

Server akan berjalan di `http://localhost:3001`

### Production

```bash
npm start
```

## 📚 API Documentation

Base URL: `http://localhost:3001/api/v1`

### Health Check

**GET** `/health`

Response:
```json
{
  "success": true,
  "message": "Hospital Navigator API is running",
  "timestamp": "2024-01-01T00:00:00.000Z"
}
```

---

### Rooms API

#### Get All Rooms

**GET** `/rooms`

Query Parameters:
- `category` (optional): Filter by category
- `floor` (optional): Filter by floor number
- `search` (optional): Search by name or description

Response:
```json
{
  "success": true,
  "data": [
    {
      "id": "IGD",
      "name": "IGD",
      "category": "Emergency",
      "locationHint": "Sayap kiri bawah peta",
      "description": "Instalasi Gawat Darurat...",
      "floor": 1
    }
  ],
  "count": 1
}
```

#### Get Room by ID

**GET** `/rooms/:id`

Response:
```json
{
  "success": true,
  "data": {
    "id": "IGD",
    "name": "IGD",
    "category": "Emergency",
    "locationHint": "Sayap kiri bawah peta",
    "description": "Instalasi Gawat Darurat...",
    "floor": 1
  }
}
```

#### Get Categories

**GET** `/rooms/categories`

Response:
```json
{
  "success": true,
  "data": ["Emergency", "Outpatient", "Critical Care", "Diagnostic", "Facility"]
}
```

#### Create/Update Room

**POST** `/rooms`

Request Body:
```json
{
  "id": "NEW_ROOM",
  "name": "New Room",
  "category": "Facility",
  "locationHint": "Location hint",
  "description": "Room description",
  "floor": 1
}
```

Response:
```json
{
  "success": true,
  "data": { ... },
  "message": "Room created/updated successfully"
}
```

#### Update Room

**PUT** `/rooms/:id`

Request Body: Same as POST

#### Delete Room

**DELETE** `/rooms/:id`

Response:
```json
{
  "success": true,
  "message": "Room deleted successfully"
}
```

---

### QR Anchors API

#### Get All QR Anchors

**GET** `/qr-anchors`

Query Parameters:
- `roomId` (optional): Filter by room ID
- `floor` (optional): Filter by floor number
- `search` (optional): Search by label or QR ID

Response:
```json
{
  "success": true,
  "data": [
    {
      "qrId": "QR-F1-N01",
      "roomId": "IGD",
      "svgX": 632.95538,
      "svgY": 753.07831,
      "label": "Persimpangan Area Pelayanan IGD",
      "floor": 1
    }
  ],
  "count": 1
}
```

#### Get QR Anchor by ID

**GET** `/qr-anchors/:qrId`

Response:
```json
{
  "success": true,
  "data": {
    "qrId": "QR-F1-N01",
    "roomId": "IGD",
    "svgX": 632.95538,
    "svgY": 753.07831,
    "label": "Persimpangan Area Pelayanan IGD",
    "floor": 1
  }
}
```

#### Get QR Anchor Statistics

**GET** `/qr-anchors/stats`

Response:
```json
{
  "success": true,
  "data": {
    "total": 10,
    "byFloor": {
      "0": 1,
      "1": 7,
      "2": 2
    },
    "rooms": 8
  }
}
```

#### Resolve QR Code

**POST** `/qr-anchors/resolve`

Request Body:
```json
{
  "qrCode": "QR-F1-N01"
}
```

Response:
```json
{
  "success": true,
  "data": {
    "qrId": "QR-F1-N01",
    "roomId": "IGD",
    "svgX": 632.95538,
    "svgY": 753.07831,
    "label": "Persimpangan Area Pelayanan IGD",
    "floor": 1
  }
}
```

#### Create/Update QR Anchor

**POST** `/qr-anchors`

Request Body:
```json
{
  "qrId": "QR-F1-N99",
  "roomId": "IGD",
  "svgX": 100.5,
  "svgY": 200.5,
  "label": "New QR Anchor",
  "floor": 1,
  "routeNodeId": "Optional_Route_Node"
}
```

Response:
```json
{
  "success": true,
  "data": { ... },
  "message": "QR anchor created/updated successfully"
}
```

#### Update QR Anchor

**PUT** `/qr-anchors/:qrId`

Request Body: Same as POST

#### Delete QR Anchor

**DELETE** `/qr-anchors/:qrId`

Response:
```json
{
  "success": true,
  "message": "QR anchor deleted successfully"
}
```

---

## 🏗️ Project Structure

```
server/
├── src/
│   ├── config/
│   │   └── index.js          # Configuration
│   ├── data/
│   │   ├── hospitalRooms.js  # Room data & operations
│   │   └── qrAnchors.js      # QR anchor data & operations
│   ├── middleware/
│   │   └── errorHandler.js   # Error handling middleware
│   ├── routes/
│   │   ├── index.js          # Main router
│   │   ├── rooms.js          # Room routes
│   │   └── qrAnchors.js      # QR anchor routes
│   └── index.js              # App entry point
├── .env.example              # Environment variables template
├── .gitignore
├── package.json
└── README.md
```

## 🔧 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Server port | `3001` |
| `NODE_ENV` | Environment (development/production) | `development` |
| `CORS_ORIGIN` | Allowed CORS origin | `http://localhost:5173` |
| `API_PREFIX` | API route prefix | `/api/v1` |

## 📝 Response Format

### Success Response
```json
{
  "success": true,
  "data": { ... },
  "message": "Optional message"
}
```

### Error Response
```json
{
  "success": false,
  "error": "Error message",
  "stack": "Stack trace (development only)"
}
```

## 🧪 Testing

Gunakan tools seperti Postman, Insomnia, atau curl untuk testing API:

```bash
# Health check
curl http://localhost:3001/api/v1/health

# Get all rooms
curl http://localhost:3001/api/v1/rooms

# Get room by ID
curl http://localhost:3001/api/v1/rooms/IGD

# Search rooms
curl "http://localhost:3001/api/v1/rooms?search=igd"

# Get QR anchors by floor
curl "http://localhost:3001/api/v1/qr-anchors?floor=1"

# Resolve QR code
curl -X POST http://localhost:3001/api/v1/qr-anchors/resolve \
  -H "Content-Type: application/json" \
  -d '{"qrCode":"QR-F1-N01"}'
```

## 🔐 Security Features

- **Helmet**: Security headers
- **CORS**: Cross-origin resource sharing
- **Input Validation**: Request validation
- **Error Handling**: Secure error messages

## 📦 Dependencies

- **express**: Web framework
- **cors**: CORS middleware
- **helmet**: Security middleware
- **morgan**: HTTP request logger
- **compression**: Response compression
- **dotenv**: Environment variables

## 🚧 Future Enhancements

- [ ] Database integration (MongoDB/PostgreSQL)
- [ ] Authentication & Authorization
- [ ] Rate limiting
- [ ] API versioning
- [ ] Swagger/OpenAPI documentation
- [ ] Unit & integration tests
- [ ] Docker support
- [ ] CI/CD pipeline

## 📄 License

ISC

## 👥 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
