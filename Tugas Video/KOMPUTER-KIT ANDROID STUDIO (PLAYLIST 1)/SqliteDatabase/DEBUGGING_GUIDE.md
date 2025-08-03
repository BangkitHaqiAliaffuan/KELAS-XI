# SharedPreferences with Debugging Techniques - Complete Implementation Guide

## 📱 **Part 1: SharedPreferences Implementation**

### 1. Layout Implementation (activity_debug_preferences.xml)

#### Key UI Components:
```xml
<!-- Nama Barang EditText -->
<EditText
    android:id="@+id/etNamaBarang"
    android:hint="Masukkan Nama Barang"
    android:inputType="text" />

<!-- Stok Barang EditText -->
<EditText
    android:id="@+id/etStokBarang"
    android:hint="Masukkan Stok Barang"
    android:inputType="numberDecimal" />

<!-- Action Buttons -->
<Button android:id="@+id/btnSimpan" android:onClick="simpanData" />
<Button android:id="@+id/btnTampil" android:onClick="tampilData" />

<!-- Debug Buttons -->
<Button android:onClick="debugLog" />
<Button android:onClick="clearData" />
<Button android:onClick="testDebug" />
```

### 2. MainActivity Implementation (DebugPreferencesActivity.java)

#### Variable Declarations:
```java
// Debug Tag for Logcat filtering
private static final String TAG = "DebugPreferences";

// Declare EditText variables
EditText etNamaBarang, etStokBarang;
TextView tvDisplay;

// Declare SharedPreferences object
SharedPreferences sharedPreferences;

// SharedPreferences keys
private static final String PREF_NAME = "data_barang";
private static final String KEY_NAMA_BARANG = "nama_barang_key";
private static final String KEY_STOK_BARANG = "stok_barang_key";
private static final String KEY_TIMESTAMP = "timestamp_key";
```

#### Initialization Method:
```java
private void load() {
    try {
        // Link EditTexts to their XML IDs using findViewById
        etNamaBarang = findViewById(R.id.etNamaBarang);
        etStokBarang = findViewById(R.id.etStokBarang);
        tvDisplay = findViewById(R.id.tvDisplay);
        
        // Initialize sharedPreferences with getSharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        
        Log.d(TAG, "SharedPreferences initialized with name: " + PREF_NAME);
        
    } catch (Exception e) {
        Log.e(TAG, "Error in load() method", e);
    }
}
```

### 3. Save Function Implementation

```java
public void simpanData(View v) {
    // *** SET BREAKPOINT HERE *** (Click in gutter next to line number)
    Log.d(TAG, "simpanData() called - Starting save process");
    
    try {
        // Get String value from etNamaBarang
        String valueNamaBarang = etNamaBarang.getText().toString().trim();
        Log.d(TAG, "Retrieved nama barang: '" + valueNamaBarang + "'");
        
        // Get String value from etStokBarang and convert to float
        String stokText = etStokBarang.getText().toString().trim();
        Log.d(TAG, "Retrieved stok text: '" + stokText + "'");
        
        // Input validation with debugging
        if (valueNamaBarang.isEmpty()) {
            Log.w(TAG, "Validation failed - Nama barang is empty");
            Toast.makeText(this, "Nama barang tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Convert stok to float with error handling
        float valueStokBarang;
        try {
            valueStokBarang = Float.parseFloat(stokText);
            Log.d(TAG, "Stok converted to float: " + valueStokBarang);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error converting stok to float: " + stokText, e);
            Toast.makeText(this, "Stok harus berupa angka!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // *** SET BREAKPOINT HERE *** to inspect variables
        Log.d(TAG, "Data validation passed - Ready to save");
        
        // Get an Editor from sharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        // Use editor.putString and editor.putFloat to save data
        editor.putString(KEY_NAMA_BARANG, valueNamaBarang);
        editor.putFloat(KEY_STOK_BARANG, valueStokBarang);
        editor.putLong(KEY_TIMESTAMP, System.currentTimeMillis());
        
        // Call editor.apply() to save
        editor.apply();
        Log.d(TAG, "editor.apply() called - Data saved to SharedPreferences");
        
        // Clear EditTexts after saving (optional)
        etNamaBarang.setText("");
        etStokBarang.setText("");
        
    } catch (Exception e) {
        Log.e(TAG, "Unexpected error in simpanData()", e);
    }
}
```

### 4. Display Function Implementation

```java
public void tampilData(View v) {
    // *** SET BREAKPOINT HERE *** (Click in gutter next to line number)
    Log.d(TAG, "tampilData() called - Starting data retrieval");
    
    try {
        // Retrieve data using sharedPreferences.getString and sharedPreferences.getFloat
        String retrievedNama = sharedPreferences.getString(KEY_NAMA_BARANG, "Default Nama");
        float retrievedStok = sharedPreferences.getFloat(KEY_STOK_BARANG, 0.0f);
        long timestamp = sharedPreferences.getLong(KEY_TIMESTAMP, 0);
        
        // *** SET BREAKPOINT HERE *** to inspect retrieved values
        Log.d(TAG, "Data retrieved - Nama: '" + retrievedNama + "', Stok: " + retrievedStok + ", Timestamp: " + timestamp);
        
        // Check if data exists (not default values)
        if (retrievedNama.equals("Default Nama") && retrievedStok == 0.0f) {
            Log.w(TAG, "No data found in SharedPreferences - using default values");
            Toast.makeText(this, "Tidak ada data tersimpan!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Format and display data
        String timeString = "";
        if (timestamp > 0) {
            timeString = "\nDisimpan: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(timestamp));
        }
        
        String displayMessage = "Nama: " + retrievedNama + "\nStok: " + retrievedStok + timeString;
        Toast.makeText(this, displayMessage, Toast.LENGTH_LONG).show();
        
        Log.d(TAG, "Data displayed successfully");
        
    } catch (Exception e) {
        Log.e(TAG, "Error in tampilData()", e);
    }
}
```

## 🐛 **Part 2: Basic Debugging Techniques**

### 1. Setting Breakpoints

#### How to Set Breakpoints:
1. **Click in the gutter** (left margin) next to line number in MainActivity.java
2. **Red circle appears** indicating breakpoint is set
3. **Recommended breakpoint locations:**
   ```java
   // In simpanData() method:
   public void simpanData(View v) {
       // *** SET BREAKPOINT HERE *** 
       Log.d(TAG, "simpanData() called");
       
       // After data retrieval
       String valueNamaBarang = etNamaBarang.getText().toString().trim();
       // *** SET BREAKPOINT HERE *** to inspect valueNamaBarang
       
       // Before saving
       SharedPreferences.Editor editor = sharedPreferences.edit();
       // *** SET BREAKPOINT HERE *** to inspect editor
   }
   ```

#### Breakpoint Management:
- **Toggle breakpoint**: Click on red circle
- **Disable breakpoint**: Right-click → "Disable breakpoint"
- **Conditional breakpoint**: Right-click → "More" → Add condition

### 2. Running in Debug Mode

#### Steps to Debug:
1. **Click the green bug icon** 🐛 in Android Studio toolbar
2. **Select target device** (emulator or physical device)
3. **App launches in debug mode** - slower but allows inspection
4. **When breakpoint hits**: App pauses, debugger activates

#### Debug Toolbar Controls:
```
▶️  Resume Program (F9) - Continue execution
⏸️  Pause Program - Pause execution
🛑  Stop Debugging - End debug session
```

### 3. Stepping Through Code

#### Debugging Controls:
```
F8  - Step Over: Execute current line, don't enter method calls
F7  - Step Into: Enter method calls for detailed inspection
F9  - Resume: Continue execution until next breakpoint
```

#### Practical Example:
```java
public void simpanData(View v) {
    Log.d(TAG, "Starting save"); // <-- Breakpoint here, press F8
    String nama = etNamaBarang.getText().toString(); // <-- F8 executes this
    validateInput(nama); // <-- F7 enters this method, F8 skips it
    saveToPreferences(nama); // <-- Continue with F8
}
```

### 4. Inspecting Variables

#### Variables Window:
- **Location**: Bottom panel when debugging
- **Shows**: All variables in current scope
- **Features**: 
  - Expand objects to see properties
  - Right-click to "Set Value" for testing
  - Watch expressions for complex evaluations

#### Example Variable Inspection:
```java
// When stopped at breakpoint, Variables window shows:
valueNamaBarang = "Laptop Gaming"     // String value
valueStokBarang = 15.0               // float value
editor = android.content.SharedPreferences$EditorImpl@abc123  // Object reference
```

#### Watches Panel:
- **Add Watch**: Right-click variable → "Add to Watches"
- **Custom Expressions**: Type `valueNamaBarang.length()` to see string length
- **Complex Evaluations**: `etNamaBarang.getText().toString().trim().isEmpty()`

### 5. Using Logcat for Debugging

#### Logcat Setup:
1. **Open Logcat panel**: View → Tool Windows → Logcat
2. **Filter by package**: `com.kelasxi.sqlitedatabase`
3. **Filter by tag**: Search for `DebugPreferences`
4. **Filter by log level**: Debug, Info, Warning, Error

#### Log Levels and Usage:
```java
// Debug - Detailed development info
Log.d(TAG, "Detailed variable state: " + variable);

// Info - General information
Log.i(TAG, "User performed action: save data");

// Warning - Potential issues
Log.w(TAG, "Input validation warning: empty field");

// Error - Actual errors
Log.e(TAG, "Critical error occurred", exception);

// Verbose - Maximum detail (rarely used)
Log.v(TAG, "Very detailed trace information");
```

#### Advanced Logcat Filtering:
```
// Filter examples:
tag:DebugPreferences          // Show only our tag
package:mine level:debug      // Debug level from our app
tag:DebugPreferences level:error  // Only errors from our tag
```

### 6. Debug Functions in Our Implementation

#### debugLog() Method:
```java
public void debugLog(View v) {
    Log.d(TAG, "=== DEBUG LOG SESSION START ===");
    
    // Log all SharedPreferences data
    String nama = sharedPreferences.getString(KEY_NAMA_BARANG, "NOT_FOUND");
    float stok = sharedPreferences.getFloat(KEY_STOK_BARANG, -1.0f);
    
    Log.d(TAG, "Current SharedPreferences data:");
    Log.d(TAG, "  - KEY_NAMA_BARANG: '" + nama + "'");
    Log.d(TAG, "  - KEY_STOK_BARANG: " + stok);
    
    // Log current EditText values
    String currentNama = etNamaBarang.getText().toString();
    String currentStok = etStokBarang.getText().toString();
    
    Log.d(TAG, "Current EditText values:");
    Log.d(TAG, "  - etNamaBarang: '" + currentNama + "'");
    Log.d(TAG, "  - etStokBarang: '" + currentStok + "'");
    
    // System info logging
    Runtime runtime = Runtime.getRuntime();
    Log.d(TAG, "Memory - Max: " + (runtime.maxMemory() / 1024 / 1024) + " MB");
}
```

#### testDebug() Method:
```java
public void testDebug(View v) {
    // *** SET BREAKPOINT HERE *** to step through test scenarios
    
    // Test variable inspection
    String testString = "Debug Test String";
    int testInt = 42;
    float testFloat = 3.14f;
    
    // Loop debugging - set breakpoint inside loop
    for (int i = 0; i < 5; i++) {
        Log.d(TAG, "Loop iteration: " + i);
        // *** BREAKPOINT HERE *** to see loop variables
    }
    
    // Method call debugging
    helperMethod("Test parameter");
}
```

## 🔧 **Debugging Workflow Best Practices**

### 1. Systematic Debugging Approach:
```java
public void debuggingWorkflow() {
    // 1. Set breakpoints at key decision points
    // 2. Run in debug mode
    // 3. Inspect variables at each step
    // 4. Step through code line by line
    // 5. Check Logcat for detailed logs
    // 6. Verify SharedPreferences data
    // 7. Test edge cases (empty inputs, invalid data)
}
```

### 2. Common Debugging Scenarios:

#### Data Not Saving:
```java
// Set breakpoints at:
SharedPreferences.Editor editor = sharedPreferences.edit(); // ✓ Editor created?
editor.putString(KEY_NAMA_BARANG, valueNamaBarang);        // ✓ Data added?
editor.apply();                                            // ✓ Apply called?

// Check Logcat for:
Log.d(TAG, "Data to save: " + valueNamaBarang);
Log.d(TAG, "Editor apply completed");
```

#### Data Not Loading:
```java
// Set breakpoints at:
String retrieved = sharedPreferences.getString(KEY_NAMA_BARANG, "DEFAULT"); // ✓ Data retrieved?

// Check if using correct key:
Log.d(TAG, "Looking for key: " + KEY_NAMA_BARANG);
Log.d(TAG, "Retrieved value: " + retrieved);
```

#### App Crashes:
```java
// Use try-catch with logging:
try {
    float stok = Float.parseFloat(stokText);
} catch (NumberFormatException e) {
    Log.e(TAG, "Parse error for: " + stokText, e); // ✓ Log the exact error
}
```

### 3. Performance Debugging:
```java
public void performanceDebugging() {
    long startTime = System.currentTimeMillis();
    
    // Your code here
    sharedPreferences.edit().putString("key", "value").apply();
    
    long endTime = System.currentTimeMillis();
    Log.d(TAG, "Operation took: " + (endTime - startTime) + "ms");
}
```

## 📋 **Testing Checklist for Debugging:**

### ✅ **Breakpoint Testing:**
- [ ] Set breakpoint in `simpanData()` method
- [ ] Set breakpoint in `tampilData()` method  
- [ ] Step through code with F8 (Step Over)
- [ ] Step into methods with F7 (Step Into)
- [ ] Inspect variables in Variables panel
- [ ] Test conditional breakpoints

### ✅ **Logcat Testing:**
- [ ] Filter Logcat by package name
- [ ] Filter Logcat by TAG (`DebugPreferences`)
- [ ] Check Debug logs during save operation
- [ ] Check Error logs during invalid input
- [ ] Verify log levels (Debug, Info, Warning, Error)

### ✅ **SharedPreferences Testing:**
- [ ] Save valid data and verify in Logcat
- [ ] Try to save empty data (should show validation error)
- [ ] Save numeric data and verify type conversion
- [ ] Retrieve data and verify default values work
- [ ] Clear all data and verify it's actually cleared

### ✅ **Error Scenario Testing:**
- [ ] Input non-numeric data in stok field
- [ ] Leave fields empty and try to save
- [ ] Test app restart (data persistence)
- [ ] Test memory conditions with debugLog()

## 🚀 **How to Use This Implementation:**

1. **Run the main app** and click **"DEBUG PREFERENCES DEMO"** button
2. **Open DebugPreferencesActivity** with comprehensive debugging features
3. **Set breakpoints** in `simpanData()` and `tampilData()` methods
4. **Run in debug mode** and step through code
5. **Use debug buttons** to test various scenarios
6. **Monitor Logcat** with filter `DebugPreferences`
7. **Practice debugging techniques** with provided test methods

This implementation provides a complete learning environment for both SharedPreferences and Android debugging techniques!
