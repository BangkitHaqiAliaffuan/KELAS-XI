package com.kelasxi.sqlitedatabase;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DebugPreferencesActivity extends AppCompatActivity {
    
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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Debug log - App lifecycle
        Log.d(TAG, "onCreate() called - App starting");
        
        setContentView(R.layout.activity_debug_preferences);
        
        // Initialize components
        load();
        
        // Debug log - Initialization complete
        Log.d(TAG, "onCreate() completed - Components initialized");
    }
    
    /**
     * Initialization method - Link EditTexts and initialize SharedPreferences
     */
    private void load() {
        try {
            // Debug log - Start initialization
            Log.d(TAG, "load() method started");
            
            // Link EditTexts to their XML IDs using findViewById
            etNamaBarang = findViewById(R.id.etNamaBarang);
            etStokBarang = findViewById(R.id.etStokBarang);
            tvDisplay = findViewById(R.id.tvDisplay);
            
            // Debug log - Views found
            Log.d(TAG, "Views successfully found - etNamaBarang, etStokBarang, tvDisplay");
            
            // Initialize sharedPreferences with getSharedPreferences
            sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            
            // Debug log - SharedPreferences initialized
            Log.d(TAG, "SharedPreferences initialized with name: " + PREF_NAME);
            
            // Check if there's existing data
            checkExistingData();
            
        } catch (Exception e) {
            // Debug log - Error handling
            Log.e(TAG, "Error in load() method", e);
            Toast.makeText(this, "Error initializing components: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Check for existing data in SharedPreferences
     */
    private void checkExistingData() {
        String existingNama = sharedPreferences.getString(KEY_NAMA_BARANG, "");
        float existingStok = sharedPreferences.getFloat(KEY_STOK_BARANG, 0.0f);
        
        if (!existingNama.isEmpty()) {
            Log.d(TAG, "Existing data found - Nama: " + existingNama + ", Stok: " + existingStok);
            updateDisplay("Data tersimpan ditemukan: " + existingNama + " (Stok: " + existingStok + ")");
        } else {
            Log.d(TAG, "No existing data found in SharedPreferences");
            updateDisplay("Tidak ada data tersimpan");
        }
    }
    
    /**
     * Save function - public void simpanData(View v)
     * This method demonstrates debugging with breakpoints
     */
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
            
            if (stokText.isEmpty()) {
                Log.w(TAG, "Validation failed - Stok barang is empty");
                Toast.makeText(this, "Stok barang tidak boleh kosong!", Toast.LENGTH_SHORT).show();
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
            Log.d(TAG, "SharedPreferences.Editor obtained");
            
            // Use editor.putString and editor.putFloat to save data
            editor.putString(KEY_NAMA_BARANG, valueNamaBarang);
            editor.putFloat(KEY_STOK_BARANG, valueStokBarang);
            editor.putLong(KEY_TIMESTAMP, System.currentTimeMillis());
            
            Log.d(TAG, "Data added to editor - Nama: " + valueNamaBarang + ", Stok: " + valueStokBarang);
            
            // Call editor.apply() to save
            editor.apply();
            Log.d(TAG, "editor.apply() called - Data saved to SharedPreferences");
            
            // Success feedback
            Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show();
            updateDisplay("Data disimpan: " + valueNamaBarang + " (Stok: " + valueStokBarang + ")");
            
            // Clear EditTexts after saving (optional)
            etNamaBarang.setText("");
            etStokBarang.setText("");
            Log.d(TAG, "EditTexts cleared after saving");
            
        } catch (Exception e) {
            // Debug log - Comprehensive error handling
            Log.e(TAG, "Unexpected error in simpanData()", e);
            Toast.makeText(this, "Error saving data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        
        Log.d(TAG, "simpanData() completed");
    }
    
    /**
     * Display function - public void tampilData(View v)
     * This method demonstrates data retrieval with debugging
     */
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
                updateDisplay("Tidak ada data tersimpan");
                return;
            }
            
            // Format timestamp
            String timeString = "";
            if (timestamp > 0) {
                timeString = "\nDisimpan: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(timestamp));
            }
            
            // Display retrieved data in Toast and TextView
            String displayMessage = "Nama: " + retrievedNama + "\nStok: " + retrievedStok + timeString;
            Toast.makeText(this, displayMessage, Toast.LENGTH_LONG).show();
            updateDisplay(displayMessage);
            
            Log.d(TAG, "Data displayed successfully");
            
        } catch (Exception e) {
            // Debug log - Error handling
            Log.e(TAG, "Error in tampilData()", e);
            Toast.makeText(this, "Error retrieving data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        
        Log.d(TAG, "tampilData() completed");
    }
    
    /**
     * Debug logging function - demonstrates extensive logging
     */
    public void debugLog(View v) {
        Log.d(TAG, "=== DEBUG LOG SESSION START ===");
        
        try {
            // Log all SharedPreferences data
            String nama = sharedPreferences.getString(KEY_NAMA_BARANG, "NOT_FOUND");
            float stok = sharedPreferences.getFloat(KEY_STOK_BARANG, -1.0f);
            long timestamp = sharedPreferences.getLong(KEY_TIMESTAMP, -1);
            
            Log.d(TAG, "Current SharedPreferences data:");
            Log.d(TAG, "  - KEY_NAMA_BARANG: '" + nama + "'");
            Log.d(TAG, "  - KEY_STOK_BARANG: " + stok);
            Log.d(TAG, "  - KEY_TIMESTAMP: " + timestamp);
            
            // Log current EditText values
            String currentNama = etNamaBarang.getText().toString();
            String currentStok = etStokBarang.getText().toString();
            
            Log.d(TAG, "Current EditText values:");
            Log.d(TAG, "  - etNamaBarang: '" + currentNama + "'");
            Log.d(TAG, "  - etStokBarang: '" + currentStok + "'");
            
            // Log memory and system info
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            
            Log.d(TAG, "Memory Information:");
            Log.d(TAG, "  - Max Memory: " + (maxMemory / 1024 / 1024) + " MB");
            Log.d(TAG, "  - Total Memory: " + (totalMemory / 1024 / 1024) + " MB");
            Log.d(TAG, "  - Free Memory: " + (freeMemory / 1024 / 1024) + " MB");
            
            Toast.makeText(this, "Debug information logged. Check Logcat with filter: " + TAG, Toast.LENGTH_LONG).show();
            updateDisplay("Debug log completed. Check Logcat for details.");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in debugLog()", e);
        }
        
        Log.d(TAG, "=== DEBUG LOG SESSION END ===");
    }
    
    /**
     * Clear all SharedPreferences data
     */
    public void clearData(View v) {
        Log.d(TAG, "clearData() called - Clearing all SharedPreferences");
        
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            
            etNamaBarang.setText("");
            etStokBarang.setText("");
            updateDisplay("Semua data telah dihapus");
            
            Log.d(TAG, "All SharedPreferences data cleared successfully");
            Toast.makeText(this, "Semua data telah dihapus!", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error clearing data", e);
            Toast.makeText(this, "Error clearing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Test debugging with intentional scenarios
     */
    public void testDebug(View v) {
        Log.d(TAG, "testDebug() called - Testing various debug scenarios");
        
        // *** SET BREAKPOINT HERE *** to step through test scenarios
        
        // Test 1: Variable inspection
        String testString = "Debug Test String";
        int testInt = 42;
        float testFloat = 3.14f;
        boolean testBoolean = true;
        
        Log.d(TAG, "Test variables created - String: " + testString + ", Int: " + testInt + ", Float: " + testFloat + ", Boolean: " + testBoolean);
        
        // Test 2: Loop debugging
        for (int i = 0; i < 5; i++) {
            Log.d(TAG, "Loop iteration: " + i);
            // *** SET BREAKPOINT HERE *** to see loop variable values
        }
        
        // Test 3: Conditional debugging
        if (testInt > 40) {
            Log.d(TAG, "Condition met: testInt > 40");
        } else {
            Log.d(TAG, "Condition not met: testInt <= 40");
        }
        
        // Test 4: Method call stack
        helperMethod("Test parameter");
        
        Toast.makeText(this, "Debug test completed. Check Logcat and use breakpoints!", Toast.LENGTH_LONG).show();
        updateDisplay("Debug test selesai. Periksa Logcat untuk detail.");
    }
    
    /**
     * Helper method for testing call stack debugging
     */
    private void helperMethod(String parameter) {
        Log.d(TAG, "helperMethod() called with parameter: " + parameter);
        
        // *** SET BREAKPOINT HERE *** to inspect call stack
        String result = parameter.toUpperCase();
        Log.d(TAG, "helperMethod() result: " + result);
    }
    
    /**
     * Update display TextView
     */
    private void updateDisplay(String message) {
        tvDisplay.setText(message);
        Log.d(TAG, "Display updated: " + message);
    }
    
    // Activity lifecycle methods with debugging
    
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called - App ending");
    }
}
