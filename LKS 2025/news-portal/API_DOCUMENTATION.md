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

#### 5. Get All Posts (Collection) - PUBLIC
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

#### 6. Get Single Post (Detail) - PUBLIC
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

#### 7. Create New Post
```
POST /api/posts
```

**Request Body:**
```json
{
    "title": "Judul Berita Baru",
    "news_content": "Isi konten berita yang lengkap dan informatif..."
}
```

**Response (201):**
```json
{
    "message": "Post created successfully",
    "data": {
        "id": 3,
        "title": "Judul Berita Baru",
        "news_content": "Isi konten berita yang lengkap dan informatif...",
        "created_at": "2024-01-15 14:30:00",
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

#### 8. Update Post (Only Post Owner)
```
PUT /api/posts/{id}
```

**Request Body:**
```json
{
    "title": "Judul Berita yang Diperbarui",
    "news_content": "Isi konten berita yang telah diperbarui..."
}
```

**Response (200):**
```json
{
    "message": "Post updated successfully",
    "data": {
        "id": 3,
        "title": "Judul Berita yang Diperbarui",
        "news_content": "Isi konten berita yang telah diperbarui...",
        "created_at": "2024-01-15 14:30:00",
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

#### 9. Delete Post (Only Post Owner)
```
DELETE /api/posts/{id}
```

**Response (200):**
```json
{
    "message": "Post deleted successfully"
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

### Forbidden Access - Not Post Owner (404)
```json
{
    "message": "Data not found"
}
```

### Post Not Found (404)
```json
{
    "message": "No query results for model [App\\Models\\Post] {id}"
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

### 4. CRUD Operations untuk Posts

#### Create New Post (Authenticated Users Only)
```bash
curl -X POST http://localhost:8000/api/posts \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Berita Teknologi Terbaru",
    "news_content": "Konten berita yang informatif dan menarik untuk dibaca oleh pembaca..."
  }'
```

#### Update Post (Only Post Owner)
```bash
curl -X PUT http://localhost:8000/api/posts/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Berita Teknologi Terbaru - Updated",
    "news_content": "Konten berita yang telah diperbarui dengan informasi terkini..."
  }'
```

#### Delete Post (Only Post Owner)
```bash
curl -X DELETE http://localhost:8000/api/posts/1 \
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
