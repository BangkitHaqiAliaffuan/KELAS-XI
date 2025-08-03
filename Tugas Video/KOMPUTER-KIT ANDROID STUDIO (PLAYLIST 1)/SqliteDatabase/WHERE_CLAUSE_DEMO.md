# SQLite WHERE Clause Implementation Demo

## Fitur SQL SELECT dengan WHERE clause yang telah diimplementasi:

### 1. Search by Name (LIKE operator)
```java
// Database.java
public Cursor searchByName(String name) {
    String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_BARANG + " LIKE ?";
    return database.rawQuery(query, new String[]{"%" + name + "%"});
}

// MainActivity.java
public void searchByItemName(String itemName) {
    Cursor cursor = db.searchByName(itemName);
    processSelectResults(cursor, "Search results for: " + itemName);
}
```

**Cara Penggunaan:**
- Ketik nama barang di field search
- Klik tombol "Search"
- Akan menampilkan semua barang yang mengandung kata yang dicari

### 2. Filter by Stock Range (Comparison operators)
```java
// Database.java
public Cursor filterByStockRange(int minStock, int maxStock) {
    String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STOK + " BETWEEN ? AND ?";
    return database.rawQuery(query, new String[]{String.valueOf(minStock), String.valueOf(maxStock)});
}

// MainActivity.java  
public void filterByStock(int minStock, int maxStock) {
    Cursor cursor = db.filterByStockRange(minStock, maxStock);
    processSelectResults(cursor, "Items with stock between " + minStock + " and " + maxStock);
}
```

**Cara Penggunaan:**
- Klik tombol "Low Stock" untuk melihat barang dengan stok ≤ 9
- Menggunakan operator BETWEEN untuk filter rentang stok

### 3. Filter by Price Range (BETWEEN operator)
```java
// Database.java
public Cursor filterByPriceRange(double minPrice, double maxPrice) {
    String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_HARGA + " BETWEEN ? AND ?";
    return database.rawQuery(query, new String[]{String.valueOf(minPrice), String.valueOf(maxPrice)});
}

// MainActivity.java
public void filterByPrice(double minPrice, double maxPrice) {
    Cursor cursor = db.filterByPriceRange(minPrice, maxPrice);
    processSelectResults(cursor, "Items with price between " + minPrice + " and " + maxPrice);
}
```

**Cara Penggunaan:**
- Klik tombol "Expensive" untuk melihat barang dengan harga > 50,000
- Menggunakan operator BETWEEN untuk filter rentang harga

### 4. Universal WHERE clause method
```java
// Database.java
public Cursor selectWhere(String whereClause, String[] whereArgs) {
    String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + whereClause;
    return database.rawQuery(query, whereArgs);
}

// MainActivity.java
public void selectDataWhere(String whereClause, String[] whereArgs) {
    Cursor cursor = db.selectWhere(whereClause, whereArgs);
    processSelectResults(cursor, "Custom filter results");
}
```

**Contoh Custom Queries:**
- `selectDataWhere("stok > ?", new String[]{"5"})`
- `selectDataWhere("harga < ? AND stok > ?", new String[]{"30000", "10"})`
- `selectDataWhere("barang LIKE ? OR harga > ?", new String[]{"%laptop%", "100000"})`

## WHERE Clause Operators yang Digunakan:

### 1. LIKE operator
- **Tujuan:** Pattern matching untuk pencarian teks
- **Contoh:** `barang LIKE '%laptop%'`
- **Wildcard:** `%` untuk 0 atau lebih karakter

### 2. BETWEEN operator  
- **Tujuan:** Filter data dalam rentang nilai
- **Contoh:** `harga BETWEEN 10000 AND 50000`
- **Keuntungan:** Lebih readable daripada `>= AND <=`

### 3. Comparison operators
- **`>`** : Lebih besar dari
- **`<`** : Lebih kecil dari  
- **`>=`** : Lebih besar atau sama dengan
- **`<=`** : Lebih kecil atau sama dengan
- **`=`** : Sama dengan
- **`!=`** : Tidak sama dengan

### 4. Logical operators
- **AND** : Kedua kondisi harus true
- **OR** : Salah satu kondisi harus true
- **NOT** : Negasi kondisi

## UI Controls:

### Search Section:
- **EditText etSearch:** Input untuk kata kunci pencarian
- **Button Search:** Trigger search by name menggunakan LIKE operator

### Filter Buttons:
- **Button "Low Stock":** Filter barang dengan stok ≤ 9
- **Button "Expensive":** Filter barang dengan harga > 50,000  
- **Button "Show All":** Reset filter, tampilkan semua data

## Error Handling:
- Try-catch blocks pada semua method
- Null checks pada Cursor objects
- Toast messages untuk user feedback
- Comprehensive logging untuk debugging

## Practical Examples:

### Example 1: Search Products
```java
// Mencari semua produk yang mengandung kata "laptop"
searchByItemName("laptop");
// SQL: SELECT * FROM barang WHERE barang LIKE '%laptop%'
```

### Example 2: Filter Low Stock Items
```java
// Menampilkan barang dengan stok rendah (0-9)
filterByStock(0, 9);
// SQL: SELECT * FROM barang WHERE stok BETWEEN 0 AND 9
```

### Example 3: Filter Expensive Items  
```java
// Menampilkan barang mahal (> 50000)
filterByPrice(50000, Integer.MAX_VALUE);
// SQL: SELECT * FROM barang WHERE harga BETWEEN 50000 AND 2147483647
```

### Example 4: Custom Complex Query
```java
// Barang dengan nama mengandung "phone" DAN harga < 20000
selectDataWhere("barang LIKE ? AND harga < ?", new String[]{"%phone%", "20000"});
// SQL: SELECT * FROM barang WHERE barang LIKE '%phone%' AND harga < 20000
```

## Performance Notes:
- Menggunakan prepared statements (parameterized queries) untuk mencegah SQL injection
- Index dapat ditambahkan pada kolom yang sering di-query untuk performa lebih baik
- LIKE operator dengan leading wildcard (%) dapat lambat pada dataset besar

## Testing Checklist:
✅ Search dengan kata kunci yang ada
✅ Search dengan kata kunci yang tidak ada
✅ Filter low stock items
✅ Filter expensive items  
✅ Show all items (reset filter)
✅ Empty search field (show all)
✅ Error handling untuk invalid input
✅ UI responsiveness dan user feedback
