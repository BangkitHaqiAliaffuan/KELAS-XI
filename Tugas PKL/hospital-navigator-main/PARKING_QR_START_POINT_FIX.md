# Parking QR Start Point Fix

## Problem
Saat menggunakan **Lokasi via QR** untuk routing **FROM** Parking Lantai 1 ke Hospital, start point di parking menjadi random dan berbeda tiap QR code yang di-scan.

### Behavior:
- ✅ **Lokasi via Dropdown** (room to room): Start point konsisten
- ❌ **Lokasi via QR**: Start point random, berbeda tiap QR code

## Root Cause

Kode routing logic hanya menghandle kasus **TO** parking (Hospital → Parking), tapi tidak menghandle kasus **FROM** parking (Parking → Hospital) dengan benar.

### Before Fix:

```typescript
// Line ~2755
if (endFloorExtended === 0 || endFloorExtended === -1) {
  // ✅ Destination is parking - use exact QR coordinates
  startPoint = liveSvgPointRef.current;
} else if (!preferRoomCenterStartRef.current && liveSvgPointRef.current) {
  // ❌ Start is parking - masuk ke sini, tapi useExactStartPoint = false
  startPoint = liveSvgPointRef.current;
}

// Line ~2786
const useExactStartPoint = (endFloorExtended === 0 || endFloorExtended === -1)
  ? true  // ✅ Only true if DESTINATION is parking
  : !preferRoomCenterStartRef.current && Boolean(startPoint);
  //  ❌ False if START is parking (because endFloorExtended !== 0)
```

### Masalah:
1. Ketika start di parking, `startPoint` di-set ke `liveSvgPointRef.current` ✅
2. Tapi `useExactStartPoint` di-set ke `false` ❌
3. Karena `useExactStartPoint = false`, routing logic tidak menggunakan koordinat QR yang tepat
4. Routing menggunakan nearest node dari room center, yang bisa berbeda tergantung QR code

## Solution

### After Fix:

```typescript
// Line ~2755 - Handle START is parking FIRST
if (startFloorExtended === 0 || startFloorExtended === -1) {
  // ✅ Start is parking - use exact QR coordinates
  startPoint = liveSvgPointRef.current;
} else if (endFloorExtended === 0 || endFloorExtended === -1) {
  // ✅ Destination is parking - use exact QR coordinates
  startPoint = liveSvgPointRef.current;
} else if (!preferRoomCenterStartRef.current && liveSvgPointRef.current) {
  // ✅ Normal QR scan (not parking)
  startPoint = liveSvgPointRef.current;
}

// Line ~2786 - Check BOTH start AND destination
const useExactStartPoint = (startFloorExtended === 0 || startFloorExtended === -1 || endFloorExtended === 0 || endFloorExtended === -1)
  ? true  // ✅ True if START OR DESTINATION is parking
  : !preferRoomCenterStartRef.current && Boolean(startPoint);
```

### Key Changes:
1. **Check start floor FIRST**: `if (startFloorExtended === 0 || startFloorExtended === -1)`
2. **Set useExactStartPoint for both cases**: `startFloorExtended === 0 || ... || endFloorExtended === 0`

## How It Works Now

### Scenario 1: Hospital → Parking (via QR)
```
1. Scan QR di IGD (Hospital L1)
2. liveSvgPointRef.current = { x: QR_X, y: QR_Y }
3. Select destination: Parking Lantai 1
4. startFloorExtended = 1, endFloorExtended = 0
5. Code enters: else if (endFloorExtended === 0) ✅
6. startPoint = liveSvgPointRef.current ✅
7. useExactStartPoint = true (because endFloorExtended === 0) ✅
8. Route uses EXACT QR coordinates ✅
```

### Scenario 2: Parking → Hospital (via QR) - FIXED
```
1. Scan QR di Parking L1 (e.g., QR-PK-N01)
2. liveSvgPointRef.current = { x: 929.478, y: 417.228 }
3. Select destination: Farmasi (Hospital L1)
4. startFloorExtended = 0, endFloorExtended = 1
5. Code enters: if (startFloorExtended === 0) ✅ NEW!
6. startPoint = liveSvgPointRef.current ✅
7. useExactStartPoint = true (because startFloorExtended === 0) ✅ NEW!
8. Route uses EXACT QR coordinates ✅
9. Start point KONSISTEN untuk semua QR code di parking ✅
```

### Scenario 3: Parking → Hospital (via Dropdown)
```
1. Select start: Parking Lantai 1 (dropdown)
2. preferRoomCenterStartRef.current = true
3. Select destination: Farmasi
4. startFloorExtended = 0, endFloorExtended = 1
5. Code enters: if (startFloorExtended === 0) ✅
6. startPoint = fallback to QR anchor from registry
7. useExactStartPoint = true ✅
8. Route uses room center or QR anchor ✅
```

## Testing

### Test Case 1: Scan different QR codes in Parking L1
```
1. Scan QR-PK-N01 (Belok ke Area Parkir Khusus Tenaga Medis)
   - Coordinates: (929.478, 417.228)
   - Select destination: Farmasi
   - Expected: Route starts from (929.478, 417.228)

2. Scan different QR in Parking L1 (if exists)
   - Coordinates: (different X, Y)
   - Select destination: Farmasi
   - Expected: Route starts from (different X, Y)
   
3. Both routes should be CONSISTENT from their respective QR coordinates
```

### Test Case 2: Verify console output
```
Expected console output:
🗺️ Route: Parking_Lantai_1 → Farmasi
📍 Nodes: virtual_qr_start_Parking_Lantai_1 → ... → Check_Point_Farmasi
📏 Total distance: XXX units
🏢 Floors involved: 1
🔄 Transition: Tangga Pengunjung Parkir

Note: First node should be virtual_qr_start_Parking_Lantai_1 (exact QR coordinates)
```

### Test Case 3: Compare Dropdown vs QR
```
1. Dropdown: Start = Parking Lantai 1, End = Farmasi
   - Should use room center or default QR anchor
   
2. QR: Scan QR-PK-N01, End = Farmasi
   - Should use EXACT QR coordinates (929.478, 417.228)
   
3. Routes should be DIFFERENT (QR is more precise)
```

## Expected Behavior After Fix

| Scenario | Start Method | Start Point | useExactStartPoint | Result |
|----------|--------------|-------------|-------------------|---------|
| Hospital → Parking | QR | QR coordinates | ✅ true | Consistent |
| Hospital → Parking | Dropdown | Room center | ❌ false | Consistent |
| Parking → Hospital | QR | QR coordinates | ✅ true | **FIXED** - Consistent |
| Parking → Hospital | Dropdown | Room center/QR anchor | ✅ true | Consistent |

## Files Modified
- `src/components/hospital/MapViewer.tsx` - Fixed start point logic for parking QR routing

## Related Issues
- This fix ensures QR-based routing FROM parking is as precise as QR-based routing TO parking
- Each QR code in parking will have its own unique start point (as intended)
- Routes will be consistent for the same QR code

## Prevention Tips
When adding new special areas (like parking):
1. Always check BOTH start and destination cases
2. Set `useExactStartPoint = true` for BOTH directions
3. Test with QR codes in both directions
4. Verify console logs show correct node IDs
