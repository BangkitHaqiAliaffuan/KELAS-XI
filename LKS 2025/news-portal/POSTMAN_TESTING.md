# JSON Examples for Postman Testing

## 📝 CRUD Operations Testing

### 1. Create Post (POST /api/posts)
**Header:**
```
Authorization: Bearer YOUR_ACCESS_TOKEN
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "title": "Perkembangan Teknologi AI di Indonesia",
    "news_content": "Artificial Intelligence (AI) kini semakin berkembang pesat di Indonesia. Berbagai startup dan perusahaan teknologi mulai mengimplementasikan AI dalam berbagai aspek bisnis mereka. Pemerintah Indonesia juga mendukung penuh pengembangan teknologi AI melalui berbagai program dan inisiatif strategis. Diharapkan dengan adanya dukungan ini, Indonesia dapat menjadi salah satu pemain utama dalam industri AI di kawasan Asia Tenggara."
}
```

### 2. Update Post (PUT /api/posts/{id})
**Header:**
```
Authorization: Bearer YOUR_ACCESS_TOKEN
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "title": "Perkembangan Teknologi AI di Indonesia - Update Terbaru",
    "news_content": "Artificial Intelligence (AI) kini semakin berkembang pesat di Indonesia dengan berbagai terobosan baru. Berbagai startup dan perusahaan teknologi mulai mengimplementasikan AI dalam berbagai aspek bisnis mereka, termasuk e-commerce, fintech, dan healthtech. Pemerintah Indonesia juga mendukung penuh pengembangan teknologi AI melalui berbagai program dan inisiatif strategis seperti Making Indonesia 4.0. Diharapkan dengan adanya dukungan ini, Indonesia dapat menjadi salah satu pemain utama dalam industri AI di kawasan Asia Tenggara dalam 5 tahun ke depan."
}
```

### 3. Test Cases untuk Authorization

#### Test Case 1: Create Post dengan User yang Login
```json
{
    "title": "Berita Ekonomi Terkini",
    "news_content": "Pertumbuhan ekonomi Indonesia menunjukkan tren positif di kuartal ketiga tahun ini. Sektor manufaktur dan perdagangan menjadi pendorong utama dengan kontribusi signifikan terhadap GDP nasional."
}
```

#### Test Case 2: Create Post dengan Konten Panjang
```json
{
    "title": "Revolusi Industri 4.0 dan Dampaknya terhadap Ketenagakerjaan",
    "news_content": "Revolusi Industri 4.0 membawa perubahan fundamental dalam cara kerja dan jenis pekerjaan yang tersedia. Otomatisasi dan teknologi cerdas mulai menggantikan pekerjaan-pekerjaan yang bersifat repetitif, namun di sisi lain juga menciptakan peluang kerja baru yang membutuhkan keahlian digital. Para pekerja dituntut untuk terus meningkatkan kompetensi mereka agar dapat beradaptasi dengan perubahan ini. Pemerintah dan dunia pendidikan perlu berkolaborasi dalam menyiapkan sumber daya manusia yang siap menghadapi tantangan revolusi industri 4.0."
}
```

#### Test Case 3: Update Post yang Bukan Milik User (Harus Error 404)
Gunakan token user A untuk update post yang dibuat oleh user B - seharusnya menghasilkan error "Data not found"

#### Test Case 4: Delete Post yang Bukan Milik User (Harus Error 404)
Gunakan token user A untuk delete post yang dibuat oleh user B - seharusnya menghasilkan error "Data not found"

## 🔍 Testing Scenarios

### Scenario 1: Normal CRUD Flow
1. Register sebagai User A
2. Login sebagai User A → dapatkan token_A
3. Create post menggunakan token_A
4. Update post tersebut menggunakan token_A (success)
5. Delete post tersebut menggunakan token_A (success)

### Scenario 2: Authorization Testing
1. Register sebagai User A dan User B
2. Login sebagai User A → dapatkan token_A
3. Login sebagai User B → dapatkan token_B
4. Create post menggunakan token_A
5. Coba update post tersebut menggunakan token_B (should fail with 404)
6. Coba delete post tersebut menggunakan token_B (should fail with 404)

### Scenario 3: Validation Testing
1. Create post dengan title kosong (should fail with 422)
2. Create post dengan news_content kosong (should fail with 422)
3. Update post dengan data yang tidak valid (should fail with 422)

## 📋 Expected Responses

### Success Create (201):
```json
{
    "message": "Post created successfully",
    "data": {
        "id": 1,
        "title": "...",
        "news_content": "...",
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

### Success Update (200):
```json
{
    "message": "Post updated successfully",
    "data": { ... }
}
```

### Success Delete (200):
```json
{
    "message": "Post deleted successfully"
}
```

### Authorization Failed (404):
```json
{
    "message": "Data not found"
}
```

### Validation Failed (422):
```json
{
    "message": "Validation failed",
    "errors": {
        "title": ["The title field is required."],
        "news_content": ["The news content field is required."]
    }
}
```
