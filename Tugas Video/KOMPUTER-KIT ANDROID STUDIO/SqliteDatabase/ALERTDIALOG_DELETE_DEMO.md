# AlertDialog for Delete Confirmation Implementation Demo

## Fitur AlertDialog Delete Confirmation yang telah diimplementasi:

### 1. Enhanced MainActivity.java - Delete Method with AlertDialog

#### A. Basic Delete Method (with ID only)
```java
public void deleteData(String id) {
    try {
        if (db == null) {
            Log.e(TAG, "Database is null");
            pesan("Database not initialized");
            return;
        }
        
        // Create AlertDialog.Builder instance for delete confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        // Set the dialog title
        builder.setTitle("PERINGATAN");
        
        // Set the dialog message
        builder.setMessage("Yakin akan menghapus data ini?");
        
        // Add Positive Button (YA - Yes)
        builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Move the existing SQL delete command and related logic inside this button's onClick listener
                try {
                    // Construct SQL DELETE statement
                    String sql = "DELETE FROM tblbarang WHERE id = '" + id + "'";
                    
                    Log.d(TAG, "Executing DELETE SQL: " + sql);
                    
                    // Call db.runSQL(sql) to execute the delete query
                    boolean result = db.runSQL(sql);
                    
                    // Display Toast message indicating success or failure of the deletion
                    if (result) {
                        pesan("Data berhasil dihapus");
                        Log.d(TAG, "Delete successful for ID: " + id);
                        
                        // Refresh data after successful deletion
                        selectData();
                    } else {
                        pesan("Gagal menghapus data");
                        Log.e(TAG, "Delete failed for ID: " + id);
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error in delete operation", e);
                    pesan("Error deleting data: " + e.getMessage());
                }
            }
        });
        
        // Add Negative Button (TIDAK - No)
        builder.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Simply dismiss the dialog
                dialog.cancel();
            }
        });
        
        // Show the dialog
        builder.show();
        
    } catch (Exception e) {
        Log.e(TAG, "Error in deleteData", e);
        pesan("Error showing delete confirmation: " + e.getMessage());
    }
}
```

#### B. Enhanced Delete Method (with ID and Item Name)
```java
public void deleteData(String id, String itemName) {
    try {
        if (db == null) {
            Log.e(TAG, "Database is null");
            pesan("Database not initialized");
            return;
        }
        
        // Create AlertDialog.Builder instance for delete confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        // Set the dialog title
        builder.setTitle("PERINGATAN");
        
        // Set the dialog message with item name
        builder.setMessage("Yakin akan menghapus item '" + itemName + "'?");
        
        // Add Positive Button (YA - Yes)
        builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // SQL delete operation with enhanced feedback
                try {
                    String sql = "DELETE FROM tblbarang WHERE id = '" + id + "'";
                    boolean result = db.runSQL(sql);
                    
                    if (result) {
                        pesan("Data '" + itemName + "' berhasil dihapus");
                        selectData(); // Auto refresh
                    } else {
                        pesan("Gagal menghapus data '" + itemName + "'");
                    }
                } catch (Exception e) {
                    pesan("Error deleting data: " + e.getMessage());
                }
            }
        });
        
        // Add Negative Button (TIDAK - No)
        builder.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Dismiss dialog
            }
        });
        
        // Show the dialog
        builder.show();
        
    } catch (Exception e) {
        Log.e(TAG, "Error in deleteData", e);
        pesan("Error showing delete confirmation: " + e.getMessage());
    }
}
```

### 2. Updated BarangAdapter.java Integration

#### Before (Old Implementation):
```java
// Old: Direct confirmation dialog in adapter
new AlertDialog.Builder(context)
    .setTitle("Konfirmasi Hapus")
    .setMessage("Apakah Anda yakin ingin menghapus item '" + namaBarang + "'?")
    .setPositiveButton("Ya", (dialog, which) -> {
        ((MainActivity) context).deleteData(idBarang);
        ((MainActivity) context).selectData(); // Duplicate refresh
    })
    .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
    .show();
```

#### After (New Implementation):
```java
// New: Centralized delete handling in MainActivity
String idBarang = barang.getIdbarang();
String namaBarang = barang.getBarang();

// Call MainActivity deleteData method with item name
((MainActivity) context).deleteData(idBarang, namaBarang);
```

### 3. Required Imports

**MainActivity.java:**
```java
import android.app.AlertDialog;
import android.content.DialogInterface;
```

**BarangAdapter.java:**
```java
// AlertDialog imports already exist from previous implementation
import android.app.AlertDialog;
import android.content.DialogInterface;
```

## AlertDialog Structure Components:

### 1. AlertDialog.Builder
```java
AlertDialog.Builder builder = new AlertDialog.Builder(this);
```
- **Purpose**: Creates a builder for constructing AlertDialog
- **Context**: Uses `this` (Activity context) for proper theming

### 2. Dialog Title
```java
builder.setTitle("PERINGATAN");
```
- **Purpose**: Sets the dialog header text
- **Best Practice**: Use descriptive titles like "PERINGATAN", "Konfirmasi", "Warning"

### 3. Dialog Message
```java
builder.setMessage("Yakin akan menghapus item '" + itemName + "'?");
```
- **Purpose**: Main question/information for user
- **Enhancement**: Include specific item name for clarity

### 4. Positive Button (Action Button)
```java
builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        // Execute delete operation
        // Show success/failure feedback
        // Refresh data display
    }
});
```
- **Purpose**: Confirms the action (delete)
- **Contains**: All delete logic moved from direct execution

### 5. Negative Button (Cancel Button)
```java
builder.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.cancel(); // or dialog.dismiss()
    }
});
```
- **Purpose**: Cancels the operation
- **Action**: Simply dismisses the dialog

### 6. Show Dialog
```java
builder.show();
```
- **Purpose**: Displays the constructed dialog to user

## User Experience Flow:

### 1. Delete Action Trigger
1. User clicks menu icon (⋮) on RecyclerView item
2. Selects "HAPUS" from PopupMenu
3. BarangAdapter calls `((MainActivity) context).deleteData(id, itemName)`

### 2. Confirmation Dialog Display
1. AlertDialog appears with:
   - **Title**: "PERINGATAN"
   - **Message**: "Yakin akan menghapus item '[ItemName]'?"
   - **Buttons**: "YA" and "TIDAK"

### 3. User Decision
#### If User Clicks "YA":
1. Delete SQL query executes
2. Success/failure Toast message shows
3. RecyclerView automatically refreshes
4. Dialog dismisses

#### If User Clicks "TIDAK":
1. Dialog dismisses immediately
2. No delete operation occurs
3. Data remains unchanged

## Key Benefits of Implementation:

### 1. **Centralized Delete Logic**
- ✅ Single point of delete handling in MainActivity
- ✅ Consistent confirmation across all delete operations
- ✅ Easier maintenance and debugging

### 2. **Enhanced User Feedback**
- ✅ Specific item name in confirmation message
- ✅ Clear success/failure notifications
- ✅ Automatic data refresh after deletion

### 3. **Error Prevention**
- ✅ Prevents accidental deletions
- ✅ Gives users chance to reconsider
- ✅ Clear action choices (YA/TIDAK)

### 4. **Better Code Organization**
- ✅ Separation of concerns (UI vs Logic)
- ✅ Reusable delete methods
- ✅ Reduced code duplication

## Example Dialog Messages:

### Generic Confirmation:
```
Title: PERINGATAN
Message: Yakin akan menghapus data ini?
Buttons: [YA] [TIDAK]
```

### Specific Item Confirmation:
```
Title: PERINGATAN  
Message: Yakin akan menghapus item 'Laptop Gaming'?
Buttons: [YA] [TIDAK]
```

### Success Feedback:
```
Toast: Data 'Laptop Gaming' berhasil dihapus
```

### Failure Feedback:
```
Toast: Gagal menghapus data 'Laptop Gaming'
```

## Testing Checklist:
✅ AlertDialog appears when delete is triggered
✅ Dialog shows correct item name in message
✅ "YA" button executes delete and shows feedback
✅ "TIDAK" button cancels operation safely
✅ RecyclerView refreshes automatically after delete
✅ Error handling for database failures
✅ Proper dialog dismissal in all scenarios
✅ No memory leaks from dialog instances
