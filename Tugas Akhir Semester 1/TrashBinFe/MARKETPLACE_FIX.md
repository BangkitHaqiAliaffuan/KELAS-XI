# Perbaikan Error Marketplace - NumberFormatException

## Masalah
Error `java.lang.NumberFormatException: Expected an int but was 3.50 at line 1 column ...` muncul saat mencoba load data marketplace dari API.

## Penyebab
Data JSON dari server mengirimkan field `quantity` sebagai **decimal/double** (contoh: `"3.50"`), tetapi model Android `MarketplaceListing` mendefinisikannya sebagai **Int**, sehingga Gson gagal parsing.

Selain itu, ada beberapa ketidaksesuaian field antara response API dan model Android:
1. Field `quantity` di API adalah decimal, di model adalah Int
2. Missing field `unit`, `total_price`, `status`, `expires_at`
3. Field `waste_category` di response, tapi di model hanya `category`
4. Field `lat` dan `lng` di API adalah string, di model adalah Double
5. Struktur `seller` berbeda (API mengirim `SellerInfo` yang lebih sederhana)

## Perubahan yang Dilakukan

### 1. MarketplaceListing.kt
**Sebelum:**
```kotlin
data class MarketplaceListing(
    @SerializedName("id") val id: Int,
    @SerializedName("seller_id") val sellerId: Int,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("quantity") val quantity: Int,  // ❌ Int
    @SerializedName("price_per_unit") val pricePerUnit: Double,
    // ... missing fields
)
```

**Sesudah:**
```kotlin
data class MarketplaceListing(
    @SerializedName("id") val id: Int,
    @SerializedName("seller_id") val sellerId: Int,
    @SerializedName("waste_category_id") val categoryId: Int,
    @SerializedName("quantity") val quantity: Double,  // ✅ Double
    @SerializedName("unit") val unit: String,  // ✅ Added
    @SerializedName("price_per_unit") val pricePerUnit: Double,
    @SerializedName("total_price") val totalPrice: Double,  // ✅ Added
    @SerializedName("condition") val condition: String,
    @SerializedName("location") val location: String,
    @SerializedName("lat") val lat: String,  // ✅ Changed to String
    @SerializedName("lng") val lng: String,  // ✅ Changed to String
    @SerializedName("status") val status: String,  // ✅ Added
    @SerializedName("photos") val photos: List<String>,
    @SerializedName("views_count") val views: Int,
    @SerializedName("expires_at") val expiresAt: String?,  // ✅ Added
    @SerializedName("waste_category") val category: WasteCategory,  // ✅ Fixed mapping
    @SerializedName("seller") val seller: SellerInfo,  // ✅ Changed type
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

// ✅ Added new data class
data class SellerInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("points") val points: Int
)
```

### 2. MarketplaceAdapter.kt
**Perubahan:**
- Menggunakan `listing.totalPrice` dari API instead of calculating
- Menampilkan unit: `"${listing.quantity} ${listing.unit}"`
- Memperbaiki mapping condition: `"clean"`, `"needs_cleaning"`, `"mixed"`

```kotlin
// Before
tvPrice.text = CurrencyHelper.formatRupiah(listing.pricePerUnit * listing.quantity)
tvQuantity.text = "Tersedia: ${listing.quantity}"

// After
tvPrice.text = CurrencyHelper.formatRupiah(listing.totalPrice)
tvQuantity.text = "Tersedia: ${listing.quantity} ${listing.unit}"
```

### 3. ListingDetailActivity.kt
**Perubahan:**
- Update binding data untuk menggunakan field baru
- Ganti `listing.seller.rating` dengan `listing.seller.points`
- Update condition mapping
- Update status mapping menggunakan field `status`
- Fix quantity comparison untuk order dialog

```kotlin
// Before
tvPrice.text = CurrencyHelper.formatRupiah(listing.quantity * listing.pricePerUnit)
tvQuantity.text = "${listing.quantity.toInt()}"
if (quantity != null && quantity > 0 && quantity <= listing.quantity.toInt())

// After
tvPrice.text = CurrencyHelper.formatRupiah(listing.totalPrice)
tvQuantity.text = "${listing.quantity} ${listing.unit}"
if (quantity != null && quantity > 0 && quantity <= listing.quantity)
```

### 4. MarketplaceAdapter.kt (di ui folder)
Update yang sama seperti di adapters folder

## Mapping Condition Values
- `"clean"` → "Bersih"
- `"needs_cleaning"` → "Perlu Dibersihkan"
- `"mixed"` → "Campur"

## Mapping Status Values
- `"available"` → "Tersedia"
- `"sold"` → "Terjual"
- `"expired"` → "Kadaluarsa"

## Testing
Setelah perubahan ini:
1. ✅ Data marketplace dapat di-load tanpa error
2. ✅ Quantity dengan decimal dapat ditampilkan dengan benar
3. ✅ Total price menggunakan nilai dari API
4. ✅ Unit ditampilkan bersama quantity
5. ✅ Semua field dari API dapat di-mapping dengan benar

## Response API Example
```json
{
  "data": {
    "data": [
      {
        "id": 1,
        "seller_id": 1,
        "waste_category_id": 1,
        "title": "Kain Perca Bekas Jumlah Banyak",
        "description": "Kain perca bekas dengan berbagai motif dan warna.",
        "quantity": "3.50",
        "unit": "kg",
        "price_per_unit": "3200.00",
        "total_price": "11200.00",
        "condition": "clean",
        "location": "Jl. Kerajinan No. 15, Jakarta",
        "lat": "-6.21360000",
        "lng": "106.84410000",
        "status": "available",
        "photos": ["https://example.com/photos/kain-perca-1.jpg"],
        "views_count": 4,
        "expires_at": "2025-11-08T15:42:14.000000Z",
        "created_at": "2025-10-14T15:42:14.000000Z",
        "updated_at": "2025-10-20T08:09:40.000000Z",
        "waste_category": {
          "id": 1,
          "name": "Kain Perca",
          "slug": "kain-perca",
          "unit": "kg",
          "base_price_per_unit": "3000.00"
        },
        "seller": {
          "id": 1,
          "name": "Gembus",
          "avatar": null,
          "points": 0
        }
      }
    ]
  }
}
```

## Catatan
- Error deprecation untuk `getColor()` masih ada tapi tidak mempengaruhi fungsionalitas
- Untuk production, sebaiknya ganti dengan `ContextCompat.getColor()`
