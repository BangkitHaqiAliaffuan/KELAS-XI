# Image Upload Documentation

## Fitur Upload Image untuk Post

### Endpoint
**POST** `/api/posts`

### Headers
```
Accept: application/json
Authorization: Bearer {your_token}
Content-Type: multipart/form-data
```

### Request Body (Form Data)
- `title` (required, string): Judul post
- `news_content` (required, string): Konten berita
- `file` (optional, image): File gambar (jpeg, png, jpg, gif, max 2MB)

### Contoh Request dengan Postman

#### 1. Request dengan Image
**Method:** POST
**URL:** `http://localhost:8000/api/posts`
**Headers:**
```
Accept: application/json
Authorization: Bearer 1|abc123...
```

**Body (form-data):**
```
title: "Berita Terbaru dengan Gambar"
news_content: "Ini adalah konten berita yang memiliki gambar"
file: [pilih file image dari komputer]
```

#### 2. Request tanpa Image
**Method:** POST
**URL:** `http://localhost:8000/api/posts`
**Headers:**
```
Accept: application/json
Authorization: Bearer 1|abc123...
```

**Body (form-data):**
```
title: "Berita Tanpa Gambar"
news_content: "Ini adalah konten berita tanpa gambar"
```

### Response Success (201)

#### Dengan Image:
```json
{
    "message": "Post created successfully",
    "data": {
        "id": 1,
        "title": "Berita Terbaru dengan Gambar",
        "news_content": "Ini adalah konten berita yang memiliki gambar",
        "image": "abcdef123456789012345678901234.png",
        "created_at": "2024-01-15 10:30:00",
        "writer": {
            "id": 1,
            "name": "John Doe",
            "email": "john@example.com"
        },
        "comments": [],
        "comment_total": 0
    }
}
```

#### Tanpa Image:
```json
{
    "message": "Post created successfully",
    "data": {
        "id": 2,
        "title": "Berita Tanpa Gambar",
        "news_content": "Ini adalah konten berita tanpa gambar",
        "image": null,
        "created_at": "2024-01-15 10:35:00",
        "writer": {
            "id": 1,
            "name": "John Doe",
            "email": "john@example.com"
        },
        "comments": [],
        "comment_total": 0
    }
}
```

### Response Error (422)

#### Validation Error:
```json
{
    "message": "Validation failed",
    "errors": {
        "title": ["The title field is required."],
        "file": ["The file must be an image.", "The file may not be greater than 2048 kilobytes."]
    }
}
```

### File Storage Details

#### Lokasi File:
- **Path:** `storage/app/image/`
- **Nama File:** Random 30 karakter + ekstensi asli
- **Contoh:** `abcdef123456789012345678901234.png`

#### Validasi File:
- **Tipe:** image (jpeg, png, jpg, gif)
- **Ukuran Maksimal:** 2MB (2048 KB)
- **Required:** Tidak (nullable)

### Testing Steps

#### 1. Persiapan:
1. Login untuk mendapatkan token: `POST /api/login`
2. Siapkan file image untuk testing (kurang dari 2MB)

#### 2. Test Upload dengan Image:
1. Buka Postman
2. Set method ke POST
3. URL: `http://localhost:8000/api/posts`
4. Headers: Authorization Bearer token
5. Body: form-data dengan title, news_content, dan file
6. Send request
7. Verifikasi response dan file tersimpan di `storage/app/image/`

#### 3. Test Upload tanpa Image:
1. Gunakan setup yang sama
2. Body: form-data hanya dengan title dan news_content (tanpa file)
3. Send request
4. Verifikasi response memiliki `image: null`

#### 4. Test Validation:
1. Kirim request tanpa title (harus error)
2. Kirim request dengan file bukan image (harus error)
3. Kirim request dengan file > 2MB (harus error)

### Database Schema

#### Tabel posts:
```sql
CREATE TABLE posts (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    news_content TEXT NOT NULL,
    image VARCHAR(255) NULL,
    author BIGINT UNSIGNED NOT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    deleted_at TIMESTAMP NULL
);
```

### Fitur yang Diimplementasikan:
- ✅ Upload image dengan form-data
- ✅ Generate nama file acak 30 karakter
- ✅ Validasi tipe dan ukuran file
- ✅ Null handling (tidak error jika tanpa file)
- ✅ Simpan nama file ke database
- ✅ Storage menggunakan Laravel Storage facade
- ✅ Response API menyertakan field image

### Keamanan dan Best Practices:
- ✅ Validasi tipe file (hanya image)
- ✅ Batasan ukuran file (max 2MB)
- ✅ Nama file random untuk menghindari konflik
- ✅ File disimpan di storage/app (tidak public)
- ✅ Null handling untuk mencegah error
