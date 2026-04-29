# Hospital Navigator - Backend Implementation

Backend API untuk Hospital Navigator telah berhasil diimplementasikan sesuai dengan rekomendasi PRD.

## 📁 Struktur Backend

```
server/
├── src/
│   ├── config/
│   │   └── index.js              # Konfigurasi aplikasi
│   ├── data/
│   │   ├── hospitalRooms.js      # Data & operasi ruangan
│   │   └── qrAnchors.js          # Data & operasi QR anchors
│   ├── middleware/
│   │   └── errorHandler.js       # Error handling middleware
│   ├── routes/
│   │   ├── index.js              # Main router
│   │   ├── rooms.js              # Room endpoints
│   │   └── qrAnchors.js          # QR anchor endpoints
│   └── index.js                  # Entry point
├── .env.example                  # Template environment variables
├── .gitignore
├── package.json
├── README.md                     # Dokumentasi lengkap
├── API_EXAMPLES.md               # Contoh penggunaan API
├── DEPLOYMENT.md                 # Panduan deployment
└── INTEGRATION.md                # Panduan integrasi frontend
```

## 🚀 Quick Start

### 1. Install Dependencies

```bash
cd server
npm install
```

### 2. Setup Environment

```bash
cp .env.example .env
```

Edit `.env`:
```env
PORT=3001
NODE_ENV=development
CORS_ORIGIN=http://localhost:5173
API_PREFIX=/api/v1
```

### 3. Start Server

```bash
# Development (with hot-reload)
npm run dev

# Production
npm start
```

Server akan berjalan di: `http://localhost:3001`

## 📚 API Endpoints

### Base URL
```
http://localhost:3001/api/v1
```

### Health Check
```
GET /health
```

### Rooms API
```
GET    /rooms                    # Get all rooms
GET    /rooms/:id                # Get room by ID
GET    /rooms?category=Emergency # Filter by category
GET    /rooms?floor=1            # Filter by floor
GET    /rooms?search=igd         # Search rooms
GET    /rooms/categories         # Get all categories
POST   /rooms                    # Create/update room
PUT    /rooms/:id                # Update room
DELETE /rooms/:id                # Delete room
```

### QR Anchors API
```
GET    /qr-anchors               # Get all QR anchors
GET    /qr-anchors/:qrId         # Get QR anchor by ID
GET    /qr-anchors?roomId=IGD    # Filter by room
GET    /qr-anchors?floor=1       # Filter by floor
GET    /qr-anchors/stats         # Get statistics
POST   /qr-anchors/resolve       # Resolve QR code
POST   /qr-anchors               # Create/update anchor
PUT    /qr-anchors/:qrId         # Update anchor
DELETE /qr-anchors/:qrId         # Delete anchor
```

## 💡 Contoh Penggunaan

### Get All Rooms
```bash
curl http://localhost:3001/api/v1/rooms
```

### Search Rooms
```bash
curl "http://localhost:3001/api/v1/rooms?search=igd"
```

### Resolve QR Code
```bash
curl -X POST http://localhost:3001/api/v1/qr-anchors/resolve \
  -H "Content-Type: application/json" \
  -d '{"qrCode":"QR-F1-N01"}'
```

### Get QR Anchors by Floor
```bash
curl "http://localhost:3001/api/v1/qr-anchors?floor=1"
```

## 🔧 Tech Stack

Sesuai rekomendasi PRD:

- **Framework**: Express.js (Node.js)
- **CORS**: Mendukung cross-origin requests
- **Security**: Helmet untuk security headers
- **Logging**: Morgan untuk request logging
- **Compression**: Response compression
- **Environment**: dotenv untuk konfigurasi

## 📊 Fitur

✅ **Room Management**
- CRUD operations untuk data ruangan
- Filter berdasarkan kategori dan lantai
- Pencarian berdasarkan nama/deskripsi
- Mendukung multi-floor (Lantai 1, 2, Parking L1, L2)

✅ **QR Anchor Registry**
- Manajemen QR code dengan koordinat SVG
- Resolve QR code ke anchor data
- Filter berdasarkan room dan floor
- Statistik QR anchors

✅ **RESTful API**
- Consistent response format
- Proper HTTP status codes
- Error handling yang baik

✅ **Developer Friendly**
- Dokumentasi lengkap
- Contoh penggunaan
- Easy integration dengan frontend

## 🔗 Integrasi dengan Frontend

### Install Dependencies (Frontend)
```bash
npm install axios
# atau dengan React Query
npm install @tanstack/react-query axios
```

### Setup API Client
```typescript
// src/lib/api.ts
import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:3001/api/v1',
});

export default apiClient;
```

### Environment Variables (Frontend)
```env
# .env
VITE_API_URL=http://localhost:3001/api/v1
```

### Usage Example
```typescript
import apiClient from '@/lib/api';

// Get all rooms
const response = await apiClient.get('/rooms');
const rooms = response.data.data;

// Resolve QR code
const qrResponse = await apiClient.post('/qr-anchors/resolve', {
  qrCode: 'QR-F1-N01'
});
const anchor = qrResponse.data.data;
```

Lihat `server/INTEGRATION.md` untuk panduan lengkap integrasi dengan React.

## 📖 Dokumentasi

- **README.md**: Dokumentasi utama backend
- **API_EXAMPLES.md**: Contoh penggunaan API dengan curl dan JavaScript
- **DEPLOYMENT.md**: Panduan deployment ke production
- **INTEGRATION.md**: Panduan integrasi dengan frontend React

## 🚀 Deployment

### Development
```bash
npm run dev
```

### Production dengan PM2
```bash
npm install -g pm2
pm2 start src/index.js --name hospital-api
pm2 save
```

### Docker
```bash
docker build -t hospital-api .
docker run -d -p 3001:3001 hospital-api
```

Lihat `server/DEPLOYMENT.md` untuk panduan lengkap deployment.

## 🔐 Security

- ✅ Helmet untuk security headers
- ✅ CORS configuration
- ✅ Input validation
- ✅ Error handling yang aman
- 🔄 Authentication (future enhancement)
- 🔄 Rate limiting (future enhancement)

## 📝 Response Format

### Success Response
```json
{
  "success": true,
  "data": { ... },
  "count": 10,
  "message": "Optional message"
}
```

### Error Response
```json
{
  "success": false,
  "error": "Error message"
}
```

## 🧪 Testing

```bash
# Test health endpoint
curl http://localhost:3001/api/v1/health

# Test rooms endpoint
curl http://localhost:3001/api/v1/rooms

# Test QR anchors endpoint
curl http://localhost:3001/api/v1/qr-anchors
```

## 🔄 Future Enhancements

- [ ] Database integration (MongoDB/PostgreSQL)
- [ ] Authentication & Authorization (JWT)
- [ ] Rate limiting
- [ ] API versioning
- [ ] Swagger/OpenAPI documentation
- [ ] Unit & integration tests
- [ ] WebSocket support untuk real-time updates
- [ ] Caching layer (Redis)
- [ ] File upload untuk SVG maps
- [ ] Admin dashboard

## 📊 Data Storage

Saat ini menggunakan **in-memory storage** untuk development:
- Data disimpan dalam JavaScript objects
- Data akan hilang saat server restart
- Cocok untuk development dan testing

Untuk production, disarankan migrasi ke database:
- MongoDB untuk flexibility
- PostgreSQL untuk relational data
- Redis untuk caching

## 🆘 Troubleshooting

### Port sudah digunakan
```bash
# Ganti port di .env
PORT=3002
```

### CORS error
```bash
# Update CORS_ORIGIN di .env
CORS_ORIGIN=http://localhost:5173
```

### Module not found
```bash
# Reinstall dependencies
rm -rf node_modules package-lock.json
npm install
```

## 📞 Support

Untuk pertanyaan atau issues:
1. Cek dokumentasi di folder `server/`
2. Review API examples di `API_EXAMPLES.md`
3. Cek logs: `pm2 logs hospital-api` (jika menggunakan PM2)

## 📄 License

ISC

---

## ✅ Checklist Implementasi

Sesuai dengan PRD (Product Requirements Document):

- ✅ Backend menggunakan Node.js dengan Express
- ✅ Menyimpan data lokasi ruangan
- ✅ Menyimpan koordinat rute (QR anchors dengan koordinat SVG)
- ✅ RESTful API endpoints
- ✅ CORS support untuk frontend
- ✅ Error handling yang proper
- ✅ Logging untuk monitoring
- ✅ Environment configuration
- ✅ Dokumentasi lengkap
- ✅ Contoh penggunaan
- ✅ Panduan deployment
- ✅ Panduan integrasi frontend

## 🎯 Sesuai PRD Section 4.2 Tech Stack

> **Backend**: Node.js (Express) atau Python (FastAPI) untuk menyimpan data lokasi dan koordinat rute.

✅ **Implemented**: Node.js dengan Express untuk backend API yang mengelola:
- Data lokasi ruangan (hospitalRooms.js)
- Koordinat rute QR anchors (qrAnchors.js)
- RESTful API endpoints
- CRUD operations
- Search & filter functionality

Backend siap digunakan dan dapat diintegrasikan dengan frontend React/Vite yang sudah ada.
