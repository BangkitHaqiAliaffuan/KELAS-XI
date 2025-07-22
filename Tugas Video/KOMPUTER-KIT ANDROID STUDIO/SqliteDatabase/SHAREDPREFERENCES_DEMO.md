# SharedPreferences Implementation Demo

## Fitur SharedPreferences yang telah diimplementasi:

### 1. SharedPreferences Declaration dan Initialization

#### Deklarasi (MainActivity.java):
```java
// SharedPreferences object for saving/retrieving app settings
SharedPreferences sharedPreferences;
```

#### Inisialisasi dalam initializeComponents():
```java
// Initialize SharedPreferences
Log.d(TAG, "Initializing SharedPreferences");
sharedPreferences = getSharedPreferences("barang", MODE_PRIVATE);
```

**Parameter:**
- **"barang"**: Nama file preferences (stored as `barang.xml`)
- **MODE_PRIVATE**: Hanya aplikasi ini yang dapat mengakses data

### 2. Data Saving Implementation (simpanPreferences method)

```java
public void simpanPreferences(View v) {
    try {
        // Retrieve text from EditText fields
        String barang = etBarang.getText().toString().trim();
        String stok = etStok.getText().toString().trim();
        String harga = etHarga.getText().toString().trim();
        
        // Input validation
        if (barang.isEmpty() || stok.isEmpty() || harga.isEmpty()) {
            pesan("Data Kosong - Tidak dapat menyimpan ke preferences");
            return;
        }
        
        // Validate numeric inputs
        try {
            Integer.parseInt(stok);
            Float.parseFloat(harga);
        } catch (NumberFormatException e) {
            pesan("Format data salah - Stok dan Harga harus berupa angka");
            return;
        }
        
        // Get Editor from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        // Use editor.putString to store data
        editor.putString("key_for_barang", barang);
        editor.putString("key_for_stok", stok);
        editor.putString("key_for_harga", harga);
        
        // Save additional metadata
        editor.putLong("last_saved_time", System.currentTimeMillis());
        editor.putString("last_saved_mode", tvPilihan.getText().toString());
        
        // Call editor.apply() to save the changes
        editor.apply();
        
        // Display "Data Sudah Disimpan" Toast message
        pesan("Data Sudah Disimpan ke Preferences");
        
        // Clear the input fields after saving
        clearForm();
        
    } catch (Exception e) {
        Log.e(TAG, "Error in simpanPreferences", e);
        pesan("Error saving to preferences: " + e.getMessage());
    }
}
```

**Key Features:**
- ✅ Input validation (empty fields + numeric validation)
- ✅ `SharedPreferences.Editor` untuk write operations
- ✅ Multiple data types: `putString()`, `putLong()`
- ✅ `editor.apply()` untuk async save (lebih efisien dari `commit()`)
- ✅ Metadata tracking (timestamp, mode)
- ✅ Auto clear form setelah save

### 3. Data Retrieval Implementation (tampilPreferences method)

```java
public void tampilPreferences(View v) {
    try {
        // Retrieve data using sharedPreferences.getString with default values
        String barang = sharedPreferences.getString("key_for_barang", "");
        String stok = sharedPreferences.getString("key_for_stok", "");
        String harga = sharedPreferences.getString("key_for_harga", "");
        long lastSavedTime = sharedPreferences.getLong("last_saved_time", 0);
        String lastSavedMode = sharedPreferences.getString("last_saved_mode", "Unknown");
        
        // Input validation: Check if retrieved data is empty or default
        if (barang.isEmpty() || stok.isEmpty() || harga.isEmpty()) {
            pesan("Data Kosong - Tidak ada data tersimpan di preferences");
            tvEmptyState.setVisibility(View.VISIBLE);
            rcvBarang.setVisibility(View.GONE);
            return;
        }
        
        // Populate form fields with retrieved data
        etBarang.setText(barang);
        etStok.setText(stok);
        etHarga.setText(harga);
        tvPilihan.setText("From Preferences");
        
        // Create timestamp string
        String timeString = "";
        if (lastSavedTime > 0) {
            timeString = " (Saved: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date(lastSavedTime)) + ")";
        }
        
        // Display the retrieved data with details
        String message = "Data berhasil dimuat dari Preferences:\n" +
                "Barang: " + barang + "\n" +
                "Stok: " + stok + "\n" +
                "Harga: " + harga + "\n" +
                "Mode: " + lastSavedMode + timeString;
        
        // Show AlertDialog with retrieved data details
        new AlertDialog.Builder(this)
                .setTitle("Data dari Preferences")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
        
    } catch (Exception e) {
        Log.e(TAG, "Error in tampilPreferences", e);
        pesan("Error retrieving from preferences: " + e.getMessage());
    }
}
```

**Key Features:**
- ✅ Default values untuk prevent null exceptions
- ✅ Data validation sebelum display
- ✅ Auto-populate form fields
- ✅ Detailed information dalam AlertDialog
- ✅ Timestamp formatting untuk user feedback
- ✅ Multiple data retrieval: `getString()`, `getLong()`

### 4. Clear Preferences Implementation (clearPreferences method)

```java
public void clearPreferences(View v) {
    try {
        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Yakin akan menghapus semua data preferences?")
                .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear all SharedPreferences data
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        
                        pesan("Data Preferences berhasil dihapus");
                        clearForm();
                    }
                })
                .setNegativeButton("TIDAK", null)
                .show();
                
    } catch (Exception e) {
        Log.e(TAG, "Error in clearPreferences", e);
        pesan("Error clearing preferences: " + e.getMessage());
    }
}
```

**Key Features:**
- ✅ Confirmation dialog untuk prevent accidental deletion
- ✅ `editor.clear()` untuk remove all data
- ✅ Form reset setelah clear
- ✅ User feedback dengan Toast message

### 5. Auto-load on Startup (loadLastPreferences method)

```java
private void loadLastPreferences() {
    try {
        // Check if there's saved data
        String barang = sharedPreferences.getString("key_for_barang", "");
        
        if (!barang.isEmpty()) {
            // Auto-load last saved preferences (optional)
            Log.d(TAG, "Previous preferences data found for: " + barang);
            pesan("Tip: Click 'Tampil Preferences' to load last saved data");
        } else {
            Log.d(TAG, "No previous preferences data found");
        }
        
    } catch (Exception e) {
        Log.e(TAG, "Error loading last preferences", e);
    }
}
```

**Called in onCreate()** untuk check existing data saat app startup.

## UI Implementation (activity_main.xml):

### Added Buttons Row:
```xml
<!-- Row 3: SharedPreferences buttons -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:layout_marginTop="8dp">

    <Button
        android:id="@+id/buttonSimpanPref"
        android:text="Simpan Pref"
        android:onClick="simpanPreferences"
        android:backgroundTint="@android:color/holo_purple" />

    <Button
        android:id="@+id/buttonTampilPref"
        android:text="Tampil Pref"
        android:onClick="tampilPreferences"
        android:backgroundTint="@android:color/holo_blue_bright" />

    <Button
        android:id="@+id/buttonClearPref"
        android:text="Clear Pref"
        android:onClick="clearPreferences"
        android:backgroundTint="@android:color/holo_red_light" />

</LinearLayout>
```

## SharedPreferences Data Structure:

### Keys dan Values yang disimpan:
```
File: barang.xml (di internal storage)
├── key_for_barang: "Laptop Gaming"
├── key_for_stok: "15"
├── key_for_harga: "12000000"
├── last_saved_time: 1642838400000 (timestamp)
└── last_saved_mode: "Insert"
```

### Storage Location:
```
/data/data/com.kelasxi.sqlitedatabase/shared_prefs/barang.xml
```

## SharedPreferences vs SQLite Database:

| Aspect | SharedPreferences | SQLite Database |
|--------|------------------|------------------|
| **Purpose** | Simple key-value settings | Complex relational data |
| **Data Type** | Primitives (String, int, boolean, etc.) | All data types with relationships |
| **Storage** | XML files | Database files (.db) |
| **Performance** | Fast for small data | Optimized for large datasets |
| **Query** | Key-based retrieval | SQL queries with WHERE, JOIN, etc. |
| **Use Case** | App settings, preferences, cache | User data, transactions, complex data |

## Practical Use Cases:

### 1. Form Auto-save/Restore:
```java
// Save current form state before app closes
@Override
protected void onPause() {
    super.onPause();
    if (!etBarang.getText().toString().trim().isEmpty()) {
        simpanPreferences(null); // Auto-save
    }
}
```

### 2. User Preferences:
```java
// Save user preferences
editor.putBoolean("dark_mode", true);
editor.putString("default_currency", "IDR");
editor.putInt("items_per_page", 20);
```

### 3. App State Management:
```java
// Save app state
editor.putString("last_search_query", searchQuery);
editor.putLong("last_login_time", System.currentTimeMillis());
editor.putBoolean("first_time_user", false);
```

## Error Handling:

### 1. Input Validation:
- ✅ Empty field checks
- ✅ Numeric format validation
- ✅ Null pointer prevention

### 2. Exception Handling:
- ✅ Try-catch blocks untuk all operations
- ✅ Specific error messages untuk debugging
- ✅ User-friendly error feedback

### 3. Data Integrity:
- ✅ Default values untuk prevent crashes
- ✅ Metadata tracking untuk audit trail
- ✅ Confirmation dialogs untuk destructive operations

## Testing Checklist:
✅ Save data to SharedPreferences
✅ Retrieve and display saved data
✅ Input validation (empty fields, numeric formats)
✅ Clear all preferences with confirmation
✅ Auto-load detection on app startup
✅ Error handling for all operations
✅ UI responsiveness dan user feedback
✅ Data persistence across app restarts
✅ Multiple data types storage (String, Long)
✅ Metadata tracking (timestamp, mode)
