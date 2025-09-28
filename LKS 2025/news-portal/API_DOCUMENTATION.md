# Laravel News Portal API Documentation

## Authentication System dengan Laravel Sanctum

API ini menggunakan Laravel Sanctum untuk authentication dengan token-based system.

## Endpoints

### 🔓 Public Endpoints (Tidak memerlukan authentication)

#### 1. Register User
```
POST /api/register
```

**Request Body:**
```json
{
    "first_name": "John",
    "last_name": "Doe",
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123",
    "password_confirmation": "password123"
}
```

**Response (201):**
```json
{
    "message": "User registered successfully",
    "user": {
        "id": 1,
        "first_name": "John",
        "last_name": "Doe",
        "username": "johndoe",
        "email": "john@example.com",
        "created_at": "2024-01-15 10:30:00"
    },
    "access_token": "1|abc123def456...",
    "token_type": "Bearer"
}
```

#### 2. Login User
```
POST /api/login
```

**Request Body:**
```json
{
    "email": "john@example.com",
    "password": "password123"
}
```

**Response (200):**
```json
{
    "message": "Login successful",
    "user": {
        "id": 1,
        "first_name": "John",
        "last_name": "Doe",
        "username": "johndoe",
        "email": "john@example.com",
        "created_at": "2024-01-15 10:30:00"
    },
    "access_token": "2|xyz789abc456...",
    "token_type": "Bearer"
}
```

### 🔒 Protected Endpoints (Memerlukan Bearer Token)

**Header untuk semua protected endpoints:**
```
Authorization: Bearer YOUR_ACCESS_TOKEN
```

#### 3. Get User Profile
```
GET /api/profile
```

**Response (200):**
```json
{
    "user": {
        "id": 1,
        "first_name": "John",
        "last_name": "Doe",
        "username": "johndoe",
        "email": "john@example.com",
        "created_at": "2024-01-15 10:30:00"
    }
}
```

#### 4. Logout User
```
POST /api/logout
```

**Response (200):**
```json
{
    "message": "Logged out successfully"
}
```

#### 5. Get All Posts (Collection)
```
GET /api/posts
```

**Response (200):**
```json
{
    "data": [
        {
            "id": 1,
            "title": "Teknologi AI Terbaru Mengubah Industri Pendidikan",
            "news_content": "Perkembangan teknologi Artificial Intelligence...",
            "created_at": "2024-01-15 10:30:00"
        },
        {
            "id": 2,
            "title": "Ekonomi Indonesia Menunjukkan Tren Positif",
            "news_content": "Berdasarkan data terbaru dari Badan Pusat Statistik...",
            "created_at": "2024-01-15 11:45:00"
        }
    ]
}
```

#### 6. Get Single Post (Detail)
```
GET /api/posts/{id}
```

**Response (200):**
```json
{
    "data": {
        "id": 1,
        "title": "Teknologi AI Terbaru Mengubah Industri Pendidikan",
        "news_content": "Perkembangan teknologi Artificial Intelligence...",
        "created_at": "2024-01-15 10:30:00",
        "writer": {
            "id": 1,
            "first_name": "John",
            "last_name": "Doe",
            "username": "johndoe",
            "email": "john@example.com",
            "created_at": "2024-01-15 09:00:00"
        }
    }
}
```

## Error Responses

### Validation Error (422)
```json
{
    "message": "Validation failed",
    "errors": {
        "email": ["The email field is required."],
        "password": ["The password field is required."]
    }
}
```

### Authentication Error (401)
```json
{
    "message": "Invalid login credentials"
}
```

### Unauthorized Access (401)
```json
{
    "message": "Unauthenticated."
}
```

## Cara Penggunaan

### 1. Register/Login untuk mendapatkan token
```bash
# Register
curl -X POST http://localhost:8000/api/register \
  -H "Content-Type: application/json" \
  -d '{
    "first_name": "John",
    "last_name": "Doe", 
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123",
    "password_confirmation": "password123"
  }'

# Login
curl -X POST http://localhost:8000/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 2. Gunakan token untuk mengakses protected endpoints
```bash
# Get Posts
curl -X GET http://localhost:8000/api/posts \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

# Get User Profile  
curl -X GET http://localhost:8000/api/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 3. Logout untuk revoke token
```bash
curl -X POST http://localhost:8000/api/logout \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## Setup Database

Pastikan MySQL server berjalan dan database `news-portal` telah dibuat, kemudian jalankan:

```bash
# Run migrations
php artisan migrate

# Seed dummy data
php artisan db:seed

# Start server
php artisan serve
```

API akan tersedia di `http://localhost:8000/api/`
