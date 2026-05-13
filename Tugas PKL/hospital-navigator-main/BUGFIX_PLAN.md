# Bugfix & Enhancement Plan
## Hospital Navigator — Navigation Instruction Fixes

---

## 📋 Daftar Perubahan

| # | Scope | File | Status |
|---|-------|------|--------|
| 1 | Bug 1 & 3 — Konteks "setelah keluar dari lift/tangga" | `src/components/hospital/MapViewer.tsx` | ✅ |
| 2 | Bug 2 — Deteksi persimpangan koridor vertikal | `src/components/hospital/MapViewer.tsx` | ✅ |
| 3 | Arah mata angin pada step orientasi exit | `src/components/hospital/MapViewer.tsx` | ✅ |
| 4 | Kompas overlay di pojok kanan atas peta | `src/components/hospital/MapViewer.tsx` | ✅ |

---

## 🐛 Bug 1 & Bug 3 — Konteks "setelah keluar dari lift/tangga" Salah Posisi

### Root Cause

Ada **dua fungsi** yang keduanya mencoba menambahkan konteks keluar lift/tangga, dan keduanya punya logika yang kurang tepat:

#### A. `improvePostConnectorSteps` (baris ~3859)

```
Masalah:
- Mencari firstTurnIndex = belokan pertama di seluruh route
- Guard: firstTurnIndex < 0 || firstTurnIndex > 2 → return
- Tapi tidak ada cek jarak SPASIAL antara belokan itu dengan titik awal route
- Akibat: belokan di index 1 atau 2 bisa saja jauh secara fisik dari exit lift/tangga
```

#### B. `enhanceInstructionContext` → `needsExitOrientationStep` (baris ~4022)

```
Masalah:
- Membuat step orientasi baru jika step[0] adalah "straight" dan distanceToNext >= 100
- Step orientasi dibuat berdasarkan arah VEKTOR step lurus (dx/dy)
- Ini salah: arah lurus ≠ arah belok setelah keluar lift/tangga
- Tidak ada cek apakah step lurus pertama itu memang PENDEK (tepat di area exit)
- Jika step lurus pertama panjang (>80px), user langsung jalan jauh → tidak perlu step orientasi
```

#### C. Bug 3 Khusus — Parkir L2 → jalur QR-F1-N11→N12

```
Masalah:
- Route dari Parkir L2 → Lantai 1 melewati jembatan → Lantai 2 → turun ke Lantai 1
- Di Lantai 1: visibleFloorIndex > 0 dan isVerticalFloorTransition = true
- Tapi jalur QR-F1-N11→N12 adalah koridor vertikal lurus, bukan belokan keluar lift/tangga
- Guard isQrF1N11ToN12Corridor di normalizeFloor1StairExitSteps sudah ada,
  tapi enhanceInstructionContext masih bisa menambahkan exitOrientationStep
  karena guard itu hanya berlaku di normalizeFloor1StairExitSteps
```

### Perbaikan

#### Fix A — `improvePostConnectorSteps`

**Tambah cek jarak spasial:**

```typescript
// SEBELUM
const firstTurnIndex = steps.findIndex((step) => step.type !== "straight" && step.type !== "arrive");
if (firstTurnIndex < 0 || firstTurnIndex > 2) return steps;

// SESUDAH
const routeOrigin = steps[0].fromPoint;
const firstTurnIndex = steps.findIndex(
  (step) =>
    step.type !== "straight" &&
    step.type !== "arrive" &&
    Math.hypot(step.pivotPoint.x - routeOrigin.x, step.pivotPoint.y - routeOrigin.y) <= 120
);
if (firstTurnIndex < 0) return steps;
```

**Logika:** Belokan hanya diberi konteks jika `pivotPoint`-nya berada dalam radius **120px** dari titik awal route. Belokan yang lebih jauh dianggap belokan normal, bukan belokan keluar lift/tangga.

#### Fix B — `enhanceInstructionContext` → `needsExitOrientationStep`

**Tambah syarat bahwa step lurus pertama harus PENDEK:**

```typescript
// SEBELUM
const needsExitOrientationStep =
  visibleFloorIndex > 0 &&
  isVerticalFloorTransition &&
  exitContext &&
  firstStep?.type === "straight" &&
  firstStep.distanceToNext >= 100 &&
  !firstStep.label.includes("setelah keluar");

// SESUDAH
const needsExitOrientationStep =
  visibleFloorIndex > 0 &&
  isVerticalFloorTransition &&
  exitContext &&
  firstStep?.type === "straight" &&
  firstStep.distanceToNext >= 100 &&
  firstStep.distanceToNext <= 200 &&   // ← BARU: step lurus harus pendek (tepat di area exit)
  !firstStep.label.includes("setelah keluar") &&
  !isQrF1N11ToN12Corridor(firstStep);  // ← BARU: jangan paksa jalur N11→N12
```

**Tambah helper `isQrF1N11ToN12Corridor` di dalam `enhanceInstructionContext`:**

```typescript
const isQrF1N11ToN12Corridor = (step: NavigationStep) => {
  const isNearX652 = Math.abs(step.fromPoint.x - 652) <= 70;
  const isVerticalDown = step.toPoint.y > step.fromPoint.y;
  const isLongEnough = Math.abs(step.toPoint.y - step.fromPoint.y) >= 120;
  const isNarrowX = Math.abs(step.toPoint.x - step.fromPoint.x) <= 45;
  return isNearX652 && isVerticalDown && isLongEnough && isNarrowX;
};
```

#### Fix C — `shouldAddExitContext` di `enhanceInstructionContext`

**Tambah guard `isQrF1N11ToN12Corridor` ke `isNearRouteStart`:**

Masalah Fix C lama (90→80px) hanya menggeser threshold 10px tanpa menyelesaikan root cause.
Root cause sebenarnya: `shouldAddExitContext` tidak punya guard untuk jalur N11→N12,
sehingga belokan pertama setelah koridor N11→N12 tetap mendapat label "(setelah keluar dari ...)".

```typescript
// SEBELUM
const isNearRouteStart = (step: NavigationStep) =>
  distanceBetween(routeStartPoint, step.pivotPoint) <= 90;

// SESUDAH — tambah guard N11→N12, radius tetap 90px
const isQrF1N11ToN12Corridor = (step: NavigationStep) => {
  const isNearX652 = Math.abs(step.fromPoint.x - 652) <= 70;
  const isVerticalDown = step.toPoint.y > step.fromPoint.y;
  const isLongEnough = Math.abs(step.toPoint.y - step.fromPoint.y) >= 120;
  const isNarrowX = Math.abs(step.toPoint.x - step.fromPoint.x) <= 45;
  return isNearX652 && isVerticalDown && isLongEnough && isNarrowX;
};

const isNearRouteStart = (step: NavigationStep) =>
  distanceBetween(routeStartPoint, step.pivotPoint) <= 90 &&
  !isQrF1N11ToN12Corridor(step);  // ← BARU: jangan label belokan setelah N11→N12
```

**Logika:** `isQrF1N11ToN12Corridor` sudah didefinisikan untuk Fix B di `needsExitOrientationStep`.
Guard yang sama diterapkan di `isNearRouteStart` agar `shouldAddExitContext` juga tidak aktif
untuk belokan yang pivotPoint-nya berada di area koridor N11→N12.

---

## 🐛 Bug 2 — Koridor Vertikal Tidak Menghitung Persimpangan

### Root Cause

`getQrAnchorsAlongStep` menggunakan `distancePointToSegment` dengan threshold **45px**.

Untuk koridor vertikal di x≈464 (QR-F2-N07→N06, QR-F1-N07→N10):
- QR anchor di persimpangan horizontal yang memotong koridor ini berada di x yang berbeda jauh
- Contoh: QR-F1-N11 (x=651) dan QR-F1-N12 (x=652) memotong koridor vertikal di x≈464
  → jarak horizontal = 651 - 464 = **187px** → jauh melebihi threshold 45px
- Akibat: `getQrAnchorsAlongStep` tidak mendeteksi persimpangan ini → label "Jalan lurus" tanpa patokan

### Perbaikan di `getStraightLandmarkLabel`

**Tambah deteksi khusus untuk step vertikal:**

```typescript
// Deteksi apakah step ini vertikal
const isVerticalStep = (step: NavigationStep) => {
  const dx = Math.abs(step.toPoint.x - step.fromPoint.x);
  const dy = Math.abs(step.toPoint.y - step.fromPoint.y);
  return dy > dx * 2; // vertikal jika dy lebih dari 2x dx
};

// Untuk step vertikal: hitung persimpangan dengan cara berbeda
const getVerticalCrossings = (step: NavigationStep) => {
  const minY = Math.min(step.fromPoint.y, step.toPoint.y);
  const maxY = Math.max(step.fromPoint.y, step.toPoint.y);
  const stepX = (step.fromPoint.x + step.toPoint.x) / 2;

  return routeQrAnchors.filter((anchor) => {
    // Anchor harus berada di antara y-range step (dengan margin ±40px)
    const inYRange = anchor.svgY >= minY - 40 && anchor.svgY <= maxY + 40;
    // Anchor tidak boleh terlalu jauh secara horizontal (max 300px — satu lebar koridor)
    const notTooFarX = Math.abs(anchor.svgX - stepX) <= 300;
    // Anchor tidak boleh di ujung step (bukan titik awal/akhir)
    const notAtEndpoints =
      Math.abs(anchor.svgY - step.fromPoint.y) > 40 &&
      Math.abs(anchor.svgY - step.toPoint.y) > 40;
    return inYRange && notTooFarX && notAtEndpoints;
  }).sort((a, b) => a.svgY - b.svgY);
};
```

**Integrasi ke `getStraightLandmarkLabel`:**

```typescript
const getStraightLandmarkLabel = (step: NavigationStep): string | null => {
  if (step.type !== "straight" || step.distanceToNext < 110) return null;

  // ... landmark check (existing) ...

  // Cek QR anchors — gunakan metode berbeda untuk koridor vertikal
  if (isVerticalStep(step)) {
    const crossings = getVerticalCrossings(step);
    if (crossings.length >= 2) {
      return `Lurus melewati ${crossings.length} persimpangan`;
    }
    if (crossings.length === 1) {
      return `Lurus hingga ${crossings[0].qrId}`;
    }
  } else {
    // Metode existing untuk koridor horizontal
    const qrAnchors = getQrAnchorsAlongStep(step);
    if (qrAnchors.length >= 3) {
      return `Lurus melewati ${qrAnchors.length} persimpangan`;
    }
    if (qrAnchors.length >= 1) {
      return `Lurus hingga ${qrAnchors[qrAnchors.length - 1].anchor.qrId}`;
    }
  }

  return null;
};
```

---

## 🧭 Arah Mata Angin pada Step Orientasi Exit

### Referensi Arah

```
Atas SVG  = Utara  (dy < 0)
Bawah SVG = Selatan (dy > 0)
Kanan SVG = Timur  (dx > 0)
Kiri SVG  = Barat  (dx < 0)
```

### Implementasi

Ganti label `exitOrientationStep` dari "Belok kanan/kiri" menjadi arah mata angin:

```typescript
// Helper: vektor → arah mata angin
const getCardinalDirection = (dx: number, dy: number): string => {
  const absDx = Math.abs(dx);
  const absDy = Math.abs(dy);
  if (absDx >= absDy) {
    return dx >= 0 ? "Timur" : "Barat";
  }
  return dy >= 0 ? "Selatan" : "Utara";
};

// Gunakan di exitOrientationStep
const dx = firstStep.toPoint.x - firstStep.fromPoint.x;
const dy = firstStep.toPoint.y - firstStep.fromPoint.y;
const cardinalDir = getCardinalDirection(dx, dy);

const exitOrientationStep: NavigationStep = {
  ...firstStep,
  type: exitTurnType,
  label: `Menuju ke arah ${cardinalDir} (setelah keluar dari ${exitContext})`,
  // ...
};
```

**Contoh output:**
- "Menuju ke arah Timur (setelah keluar dari lift)"
- "Menuju ke arah Selatan (setelah keluar dari tangga)"

---

## 🗺️ Kompas Overlay di Pojok Kanan Atas Peta

### Asset

File: `public/images/arah mata angin.png`

### Posisi

Pojok kanan atas peta SVG, **di atas** semua elemen lain (z-index tinggi), **tidak ikut zoom/pan** (posisi absolute di container, bukan di mapRef).

### Implementasi

Tambahkan di dalam `<div className="relative flex-1 min-h-0 overflow-hidden ...">`, setelah zoom controls:

```tsx
{/* Compass overlay */}
<div className="absolute top-4 right-4 z-20 pointer-events-none">
  <img
    src="/images/arah%20mata%20angin.png"
    alt="Kompas arah mata angin"
    className="w-16 h-16 opacity-85 drop-shadow-md select-none"
    draggable={false}
  />
</div>
```

**Catatan:**
- `pointer-events-none` agar tidak mengganggu interaksi peta
- `z-20` agar berada di atas peta tapi di bawah debug panel (z-30)
- Ukuran 64×64px (w-16 h-16) — cukup terlihat tanpa menutupi peta
- Tidak ikut transform mapRef → selalu di pojok kanan atas meski peta di-zoom/pan

---

## ⛔ Rules — Tidak Boleh Dilanggar

### R-01 — Jangan Ubah Logika Tangga Evakuasi
> Tangga evakuasi sudah benar. Jangan sentuh kondisi yang melibatkan `"evakuasi"` dalam string `exitContext`.

### R-02 — Jangan Ubah Guard `isQrF1N11ToN12Corridor` yang Ada
> Guard di `normalizeFloor1StairExitSteps` sudah benar untuk kasus step lurus N11→N12. Jangan hapus atau lemahkan kondisi ini. Hanya tambahkan guard yang sama di `enhanceInstructionContext`.

### R-03 — Radius Spasial Tidak Boleh Lebih dari 150px
> Threshold jarak untuk menentukan "belokan keluar lift/tangga" tidak boleh melebihi 150px dari titik awal route. Nilai yang terlalu besar akan kembali menyebabkan bug yang sama.

### R-04 — Jangan Hapus `mergeConsecutiveStraightSteps`
> Fungsi merge step lurus berurutan harus tetap dipanggil setelah semua transformasi. Menghapusnya akan menyebabkan step duplikat.

### R-05 — Deteksi Vertikal Hanya untuk Step dengan dy > 2×dx
> Jangan gunakan metode deteksi vertikal untuk step yang sebenarnya diagonal atau horizontal. Threshold `dy > dx * 2` harus dipertahankan.

### R-06 — Kompas Tidak Boleh Ikut Transform mapRef
> Kompas harus berada di luar `mapRef` (div yang punya `transform: translate/scale`). Jika diletakkan di dalam mapRef, kompas akan ikut zoom dan pan.

### R-07 — Jangan Ubah `improvePostConnectorSteps` untuk Kasus Tangga Evakuasi
> `improvePostConnectorSteps` dipanggil dari `normalizeFloor1StairExitSteps`. Perubahan di sini hanya boleh memperketat kondisi, bukan memperlonggar.

### R-08 — `getVerticalCrossings` Hanya Berlaku untuk Floor yang Sama
> Filter `routeQrAnchors` sudah dibatasi per `visibleFloor`. Jangan ubah filter ini — anchor dari lantai lain tidak boleh dihitung sebagai persimpangan.

### R-09 — Label "Jalan lurus" Tidak Boleh Diubah Menjadi Kosong
> Jika `getStraightLandmarkLabel` mengembalikan `null`, label tetap "Jalan lurus". Jangan biarkan label menjadi string kosong.

### R-10 — Arah Mata Angin Hanya untuk `exitOrientationStep`
> Penggunaan label Utara/Selatan/Timur/Barat hanya untuk step orientasi setelah keluar lift/tangga. Instruksi navigasi normal (Belok kiri/kanan, Jalan lurus) tidak diubah.

---

## 🔍 Test Cases Wajib Setelah Implementasi

| Test | Route | Expected |
|------|-------|----------|
| T-01 | Lantai 1 → Lobby Lantai 2 (via lift) | Step 1: "Menuju ke arah [X] (setelah keluar dari lift)", Step 2: "Jalan lurus..." |
| T-02 | Lantai 1 → Lobby Lantai 2 (via tangga utama) | Step 1: "Menuju ke arah [X] (setelah keluar dari tangga)", Step 2: "Jalan lurus..." |
| T-03 | Lahan Parkir L2 → ruangan di Lantai 1 (via N11→N12) | Step N11→N12 tetap "Jalan lurus", TIDAK ada "(setelah keluar dari ...)" |
| T-04 | QR-F2-N07 → QR-F2-N06 (koridor vertikal L2) | "Lurus melewati X persimpangan" |
| T-05 | QR-F1-N07 → QR-F1-N10 (koridor vertikal L1) | "Lurus melewati X persimpangan" |
| T-06 | Tangga evakuasi (semua route) | Tidak ada perubahan dari kondisi sekarang |
| T-07 | Kompas terlihat di semua tampilan peta | Kompas di pojok kanan atas, tidak bergerak saat zoom/pan |
