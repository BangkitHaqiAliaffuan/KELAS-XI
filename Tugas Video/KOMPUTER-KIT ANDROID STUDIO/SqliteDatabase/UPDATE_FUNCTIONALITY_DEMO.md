# SQLite UPDATE Functionality Implementation Demo

## Fitur SQL UPDATE yang telah diimplementasi:

### 1. SQL UPDATE Method (MainActivity.java)
```java
public void updateData(String id, String newBarang, String newStok, String newHarga) {
    try {
        // Input validation
        if (newBarang.trim().isEmpty() || newStok.trim().isEmpty() || newHarga.trim().isEmpty()) {
            pesan("Data tidak boleh kosong");
            return;
        }
        
        // Validate numeric inputs
        try {
            Integer.parseInt(newStok);
            Double.parseDouble(newHarga);
        } catch (NumberFormatException e) {
            pesan("Stok harus berupa angka bulat dan Harga harus berupa angka");
            return;
        }
        
        // Construct SQL UPDATE statement
        String sql = "UPDATE tblbarang SET barang = '" + newBarang + "', stok = " + newStok + ", harga = " + newHarga + " WHERE id = '" + id + "'";
        
        // Execute update query
        boolean result = db.runSQL(sql);
        
        // Check result and provide feedback
        if (result) {
            pesan("Data sudah diubah");
            selectData(); // Refresh data
        } else {
            pesan("Data tidak bisa diubah");
        }
        
    } catch (Exception e) {
        Log.e(TAG, "Error in updateData", e);
        pesan("Error updating data: " + e.getMessage());
    }
}
```

**SQL Query yang Dihasilkan:**
```sql
UPDATE tblbarang SET barang = 'Laptop Gaming', stok = 15, harga = 12000000 WHERE id = '1';
```

### 2. Dual Update Modes

#### A. Quick Edit (Dialog-based Update)
- **Trigger**: Click item menu → "Quick Edit"
- **UI**: AlertDialog with pre-filled EditText fields
- **Process**: Direct update via dialog without affecting main form

```java
// In BarangAdapter.java
private void showUpdateDialog(Barang barang) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle("Update Data Barang");
    
    // Create input fields with current data
    final EditText etUpdateBarang = new EditText(context);
    etUpdateBarang.setText(barang.getBarang());
    
    final EditText etUpdateStok = new EditText(context);
    etUpdateStok.setText(barang.getStock());
    
    final EditText etUpdateHarga = new EditText(context);
    etUpdateHarga.setText(barang.getHarga());
    
    // Set up dialog buttons
    builder.setPositiveButton("Update", (dialog, which) -> {
        String newBarang = etUpdateBarang.getText().toString().trim();
        String newStok = etUpdateStok.getText().toString().trim();
        String newHarga = etUpdateHarga.getText().toString().trim();
        
        ((MainActivity) context).updateData(barang.getIdbarang(), newBarang, newStok, newHarga);
    });
}
```

#### B. Form-based Update
- **Trigger**: Click item menu → "Edit in Form"
- **UI**: Populates main form with selected item data
- **Process**: Uses existing SIMPAN button in update mode

```java
// In MainActivity.java
public void populateFormForUpdate(String id, String barang, String stok, String harga) {
    updateId = id;  // Set update mode
    etBarang.setText(barang);
    etStok.setText(stok);
    etHarga.setText(harga);
    tvPilihan.setText("Update");  // Change label
}
```

### 3. Enhanced SIMPAN Button Logic
```java
public void simpan(View v) {
    String barang = etBarang.getText().toString().trim();
    String stok = etStok.getText().toString().trim();
    String harga = etHarga.getText().toString().trim();
    
    // Input validation...
    
    if (updateId != null) {
        // UPDATE MODE
        updateData(updateId, barang, stok, harga);
        updateId = null;  // Reset to insert mode
        tvPilihan.setText("Insert");
    } else {
        // INSERT MODE
        String sql = "INSERT INTO tblbarang (barang, stok, harga) VALUES ('" + barang + "', " + stok + ", " + harga + ")";
        boolean result = db.runSQL(sql);
        // Handle insert result...
    }
}
```

### 4. Form Management Methods

#### Clear Form Method
```java
public void clearForm() {
    updateId = null;           // Reset to insert mode
    etBarang.setText("");      // Clear all fields
    etStok.setText("");
    etHarga.setText("");
    tvPilihan.setText("Insert"); // Reset label
}

// Triggered by BATAL button
public void batalData(View v) {
    clearForm();
    pesan("Form cleared");
}
```

### 5. Updated PopupMenu Options

**Menu Items (menu_item.xml):**
```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:id="@+id/ubah" android:title="Quick Edit" />
    <item android:id="@+id/edit_form" android:title="Edit in Form" />
    <item android:id="@+id/hapus" android:title="HAPUS" />
</menu>
```

**Menu Handler (BarangAdapter.java):**
```java
int itemId = item.getItemId();
if (itemId == R.id.ubah) {
    showUpdateDialog(barang);        // Quick edit in dialog
} else if (itemId == R.id.edit_form) {
    ((MainActivity) context).populateFormForUpdate(...);  // Edit in main form
} else if (itemId == R.id.hapus) {
    // Delete confirmation dialog
}
```

### 6. UI Updates

#### Button Layout (activity_main.xml)
```xml
<!-- Row 1: SIMPAN and BACA buttons -->
<Button android:text="SIMPAN" android:onClick="simpan" />
<Button android:text="BACA" android:onClick="bacaData" />

<!-- Row 2: BATAL button -->
<Button android:text="BATAL" android:onClick="batalData" />
```

#### Dynamic Label Updates
- **Insert Mode**: `tvPilihan.setText("Insert")`
- **Update Mode**: `tvPilihan.setText("Update")`

### 7. Error Handling & Validation

#### Input Validation
```java
// Empty field validation
if (newBarang.trim().isEmpty() || newStok.trim().isEmpty() || newHarga.trim().isEmpty()) {
    pesan("Data tidak boleh kosong");
    return;
}

// Numeric validation
try {
    Integer.parseInt(newStok);
    Double.parseDouble(newHarga);
} catch (NumberFormatException e) {
    pesan("Stok harus berupa angka bulat dan Harga harus berupa angka");
    return;
}
```

#### Database Operation Feedback
```java
if (result) {
    pesan("Data sudah diubah");    // Success message
    selectData();                  // Refresh data display
} else {
    pesan("Data tidak bisa diubah"); // Failure message
}
```

#### Exception Handling
```java
try {
    // Update operations
} catch (Exception e) {
    Log.e(TAG, "Error in updateData", e);
    pesan("Error updating data: " + e.getMessage());
}
```

## Workflow Examples:

### Example 1: Quick Edit (Dialog)
1. User clicks menu icon (⋮) on any item
2. Select "Quick Edit" from PopupMenu
3. AlertDialog opens with pre-filled data
4. User modifies values and clicks "Update"
5. Data updated directly via `updateData()` method
6. RecyclerView refreshes automatically

### Example 2: Form Edit
1. User clicks menu icon (⋮) on any item
2. Select "Edit in Form" from PopupMenu
3. Main form populates with selected item data
4. `tvPilihan` changes to "Update" mode
5. User modifies values in main form
6. Click "SIMPAN" → triggers update instead of insert
7. Form resets to insert mode after successful update

### Example 3: Cancel Operation
1. User starts editing (either mode)
2. Clicks "BATAL" button
3. Form clears and resets to insert mode
4. User gets "Form cleared" confirmation

## SQL UPDATE Statement Structure:
```sql
UPDATE table_name 
SET column1 = value1, column2 = value2, ... 
WHERE condition;
```

**Practical Example:**
```sql
UPDATE tblbarang 
SET barang = 'Smartphone Galaxy', stok = 25, harga = 8500000 
WHERE id = '3';
```

## Key Features Implemented:
✅ **Complete UPDATE functionality** with proper SQL syntax
✅ **Dual update modes** (Quick Edit + Form Edit)
✅ **Input validation** for all fields
✅ **Dynamic form management** (Insert/Update mode switching)
✅ **Enhanced PopupMenu** with multiple options
✅ **Comprehensive error handling** with user feedback
✅ **Automatic data refresh** after updates
✅ **Clear form functionality** with BATAL button

## Testing Checklist:
✅ Quick Edit dialog functionality
✅ Form-based editing workflow  
✅ Input validation (empty fields, numeric validation)
✅ Success/failure feedback messages
✅ Automatic data refresh after updates
✅ Form clearing and mode switching
✅ Error handling for invalid operations
✅ UI responsiveness and user experience
