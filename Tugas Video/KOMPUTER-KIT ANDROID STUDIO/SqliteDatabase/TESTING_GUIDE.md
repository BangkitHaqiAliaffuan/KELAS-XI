# 🧪 Testing Guide for SharedPreferences with Debugging Techniques

## 📱 **Setup Instructions**

### 1. **Install and Run the Application**
- Connect your Android device or start an emulator
- In Android Studio, click the **green play button** ▶️ to run the app
- Or manually install the APK from: `app\build\outputs\apk\debug\app-debug.apk`

### 2. **Open Debugging Features**
- Launch the app on your device
- On the main screen, click **"DEBUG PREFERENCES DEMO"** button
- This opens `DebugPreferencesActivity` with comprehensive debugging features

---

## 🔧 **Step-by-Step Debugging Tests**

### **Test 1: Setting Up Breakpoints** 🔴

#### Instructions:
1. **Open Android Studio**
2. **Navigate to** `DebugPreferencesActivity.java`
3. **Find the `simpanData()` method** (around line 50)
4. **Click in the left gutter** next to line numbers to set breakpoints:
   ```java
   public void simpanData(View v) {
       // *** CLICK HERE *** - Set breakpoint on this line
       Log.d(TAG, "simpanData() called - Starting save process");
       
       // And here after data retrieval
       String valueNamaBarang = etNamaBarang.getText().toString().trim();
       // *** CLICK HERE *** - Set breakpoint to inspect variable
   ```

#### Expected Result:
- ✅ **Red circles appear** in the gutter indicating breakpoints are set
- ✅ **Breakpoint markers** show in the left margin

---

### **Test 2: Debug Mode Execution** 🐛

#### Instructions:
1. **Click the debug button** 🐛 in Android Studio (next to run button)
2. **Select your device/emulator**
3. **Wait for app to launch** in debug mode
4. **Navigate to Debug Preferences screen**
5. **Enter test data:**
   - **Nama Barang:** `Laptop Gaming`
   - **Stok Barang:** `25.5`
6. **Click "SIMPAN DATA" button**

#### Expected Result:
- ✅ **App pauses at first breakpoint** - execution stops
- ✅ **Debug panel opens** at bottom of Android Studio
- ✅ **Variables panel shows** current method variables
- ✅ **Code line is highlighted** where execution paused

---

### **Test 3: Stepping Through Code** ⏭️

#### Instructions:
1. **When stopped at breakpoint**, use these controls:
   - **F8 (Step Over)** - Execute current line, move to next
   - **F7 (Step Into)** - Enter method calls for detailed inspection
   - **F9 (Resume)** - Continue until next breakpoint

2. **Step through the save process:**
   ```java
   Log.d(TAG, "simpanData() called");        // <-- Start here, press F8
   String valueNamaBarang = etNamaBarang.getText().toString(); // <-- F8 executes this
   Log.d(TAG, "Retrieved nama: " + valueNamaBarang);           // <-- F8 continues
   ```

3. **Watch the Variables panel** change as you step through

#### Expected Result:
- ✅ **Execution advances line by line** with F8
- ✅ **Variables update** in the Variables panel
- ✅ **Console shows log messages** as they execute

---

### **Test 4: Variable Inspection** 🔍

#### Instructions:
1. **When stopped at breakpoint** after data retrieval:
   ```java
   String valueNamaBarang = etNamaBarang.getText().toString().trim();
   // *** BREAKPOINT HERE *** - Inspect variables
   ```

2. **In Variables panel**, look for:
   - `valueNamaBarang` = "Laptop Gaming"
   - `this` = DebugPreferencesActivity object
   - `v` = Button object that was clicked

3. **Right-click on `valueNamaBarang`** → **"Add to Watches"**
4. **In Watches panel**, try expressions:
   - `valueNamaBarang.length()` - Shows string length
   - `valueNamaBarang.isEmpty()` - Shows if empty
   - `valueNamaBarang.toUpperCase()` - Shows uppercase version

#### Expected Result:
- ✅ **Variables panel shows** all current variables and their values
- ✅ **Watches panel evaluates** custom expressions
- ✅ **Object properties expand** when clicked

---

### **Test 5: Logcat Monitoring** 📋

#### Instructions:
1. **Open Logcat panel** in Android Studio: View → Tool Windows → Logcat
2. **Set up filters:**
   - **Package filter:** `com.kelasxi.sqlitedatabase`
   - **Search filter:** `DebugPreferences`
   - **Log level:** Debug and above

3. **Continue debugging** (press F9) and watch logs appear:
   ```
   D/DebugPreferences: simpanData() called - Starting save process
   D/DebugPreferences: Retrieved nama barang: 'Laptop Gaming'
   D/DebugPreferences: Retrieved stok text: '25.5'
   D/DebugPreferences: Stok converted to float: 25.5
   D/DebugPreferences: Data validation passed - Ready to save
   D/DebugPreferences: editor.apply() called - Data saved to SharedPreferences
   ```

#### Expected Result:
- ✅ **Filtered logs appear** in real-time
- ✅ **Debug messages show** detailed execution flow
- ✅ **Variable values** are logged at each step

---

### **Test 6: Data Persistence Testing** 💾

#### Instructions:
1. **Complete the save operation** (let it finish with F9)
2. **Click "TAMPIL DATA" button** to retrieve saved data
3. **Set breakpoint in `tampilData()` method:**
   ```java
   public void tampilData(View v) {
       // *** SET BREAKPOINT HERE ***
       Log.d(TAG, "tampilData() called - Starting data retrieval");
   ```

4. **Step through data retrieval** and inspect:
   - `retrievedNama` variable
   - `retrievedStok` variable
   - `timestamp` variable

#### Expected Result:
- ✅ **Saved data is retrieved** correctly
- ✅ **Variables show** the exact values that were saved
- ✅ **Toast message displays** the retrieved data

---

### **Test 7: Error Scenario Testing** ❌

#### Instructions:
1. **Test invalid input:**
   - **Clear the form**
   - **Enter only spaces** in Nama Barang: `   `
   - **Enter text** in Stok Barang: `abc123`
   - **Click "SIMPAN DATA"**

2. **Step through error handling:**
   ```java
   // Validation check
   if (valueNamaBarang.isEmpty()) {
       Log.w(TAG, "Validation failed - Nama barang is empty");
       // *** BREAKPOINT HERE *** - Check error handling
       return;
   }
   
   // Number conversion
   try {
       valueStokBarang = Float.parseFloat(stokText);
   } catch (NumberFormatException e) {
       Log.e(TAG, "Error converting stok to float: " + stokText, e);
       // *** BREAKPOINT HERE *** - Check exception handling
       return;
   }
   ```

#### Expected Result:
- ✅ **Validation errors are caught** properly
- ✅ **Error logs appear** in Logcat with level "Warning" or "Error"
- ✅ **Toast messages** inform user of the error
- ✅ **Method exits early** without saving invalid data

---

### **Test 8: Advanced Debug Features** 🚀

#### Instructions:
1. **Click "DEBUG LOG"** button to test logging functionality
2. **Monitor Logcat** for comprehensive debug output:
   ```
   D/DebugPreferences: === DEBUG LOG SESSION START ===
   D/DebugPreferences: Current SharedPreferences data:
   D/DebugPreferences:   - KEY_NAMA_BARANG: 'Laptop Gaming'
   D/DebugPreferences:   - KEY_STOK_BARANG: 25.5
   D/DebugPreferences: Current EditText values:
   D/DebugPreferences:   - etNamaBarang: ''
   D/DebugPreferences:   - etStokBarang: ''
   D/DebugPreferences: Memory - Max: 512 MB
   ```

3. **Click "TEST DEBUG"** button to test breakpoint scenarios
4. **Set breakpoint in `testDebug()` method** and step through test scenarios

#### Expected Result:
- ✅ **Comprehensive system info** is logged
- ✅ **Current state** of all components is displayed
- ✅ **Memory usage** and system stats are shown
- ✅ **Test scenarios** help practice debugging techniques

---

## 🎯 **Debugging Skills Checklist**

After completing all tests, you should be able to:

### ✅ **Breakpoint Management:**
- [ ] Set breakpoints by clicking in gutter
- [ ] Remove breakpoints by clicking again
- [ ] Disable/enable breakpoints via right-click
- [ ] Set conditional breakpoints for specific scenarios

### ✅ **Code Navigation:**
- [ ] Use F8 (Step Over) to execute line by line
- [ ] Use F7 (Step Into) to enter method calls
- [ ] Use F9 (Resume) to continue execution
- [ ] Use Shift+F8 (Step Out) to exit current method

### ✅ **Variable Inspection:**
- [ ] Read variable values in Variables panel
- [ ] Expand object properties to see details
- [ ] Add variables to Watches for monitoring
- [ ] Evaluate custom expressions in Watches

### ✅ **Logcat Proficiency:**
- [ ] Filter logs by package name
- [ ] Filter logs by tag name
- [ ] Understand log levels (Debug, Info, Warning, Error)
- [ ] Search for specific text in log output

### ✅ **Error Diagnosis:**
- [ ] Identify validation errors through debugging
- [ ] Trace exception handling flow
- [ ] Understand error log output
- [ ] Connect error symptoms to code causes

### ✅ **Data Flow Understanding:**
- [ ] Trace data from UI input to SharedPreferences
- [ ] Verify data persistence across app restarts
- [ ] Understand SharedPreferences save/retrieve cycle
- [ ] Monitor data transformation (String to Float, etc.)

---

## 📚 **Next Steps for Advanced Debugging**

### **1. Performance Debugging:**
- Monitor memory usage with Android Profiler
- Identify slow operations with timing logs
- Analyze method call frequency

### **2. UI Debugging:**
- Use Layout Inspector to examine UI structure
- Debug touch events and user interactions
- Analyze View hierarchy problems

### **3. Database Debugging:**
- Debug SQL queries with log output
- Inspect database state with Database Inspector
- Monitor transaction performance

### **4. Network Debugging:**
- Debug API calls with network profiler
- Monitor request/response data
- Handle network errors gracefully

---

## 🏆 **Completion Confirmation**

When you've successfully completed all tests:

1. **Take screenshots** of:
   - Variables panel showing inspected data
   - Logcat with filtered debug messages
   - Breakpoints set in code editor
   - App running with saved data displayed

2. **Verify understanding** by:
   - Explaining the data flow from UI to SharedPreferences
   - Describing how breakpoints help identify issues
   - Demonstrating variable inspection techniques
   - Using Logcat effectively for debugging

**Congratulations! You now have comprehensive debugging skills for Android development! 🎉**
