# 🎨 Filament Admin Panel - Theme Customization

## ✅ Yang Sudah Dikustomisasi

### 1. **Color Scheme** (Matching dengan Mobile App)
- **Primary**: Deep Blue (#1E40AF)
- **Success**: Emerald Green (#059669)  
- **Warning**: Amber Orange (#F59E0B)
- **Danger**: Red (#EF4444)
- **Info**: Blue (#3B82F6)

### 2. **Brand Identity**
- Brand Name: "SMKN 2 BUDURAN SIDOARJO"
- Font: Inter (matching dengan mobile app)
- Favicon support

### 3. **UI Components**
File: `resources/css/filament/admin/theme.css`
- ✅ Sidebar dengan gradient background
- ✅ Header dengan rounded bottom corners
- ✅ Card styling dengan hover effects
- ✅ Button gradient styling
- ✅ Table modern styling
- ✅ Form input dengan focus states
- ✅ Badge/Chip styling
- ✅ Stats widget cards
- ✅ Modal/Dialog rounded
- ✅ Notification styling
- ✅ Login page dengan gradient background
- ✅ Custom scrollbar
- ✅ Smooth animations

---

## 🚀 Cara Menjalankan Theme

### Step 1: Build Assets
```bash
cd AplikasiMonitoringKelasBe
npm install
npm run build
```

### Step 2: Clear Cache
```bash
php artisan filament:cache-components
php artisan view:clear
php artisan cache:clear
```

### Step 3: Akses Admin Panel
Buka browser: `http://localhost:8000/admin`

---

## 📁 File Structure

```
AplikasiMonitoringKelasBe/
├── app/
│   └── Providers/
│       └── Filament/
│           └── AdminPanelProvider.php  ← Config colors & settings
├── resources/
│   └── css/
│       ├── app.css                     ← Base CSS + Tailwind
│       └── filament/
│           └── admin/
│               └── theme.css           ← Custom theme styling
└── config/
    └── filament.php                    ← Filament config
```

---

## 🎨 Color Palette Reference

### Primary (Deep Blue)
```php
'primary' => [
    50 => '239, 246, 255',   // #eff6ff
    600 => '30, 64, 175',    // #1e40af - Main
    700 => '29, 78, 216',    // #1d4ed8
]
```

### Success (Emerald)
```php
'success' => [
    50 => '236, 253, 245',   // #ecfdf5
    600 => '5, 150, 105',    // #059669 - Main
    700 => '4, 120, 87',     // #047857
]
```

### Warning (Amber)
```php
'warning' => [
    50 => '255, 251, 235',   // #fffbeb
    500 => '245, 158, 11',   // #f59e0b - Main
    600 => '217, 119, 6',    // #d97706
]
```

---

## 🔧 Customization Tips

### Menambah Custom Widget
```php
// app/Filament/Widgets/StatsOverview.php
use Filament\Widgets\StatsOverviewWidget as BaseWidget;

class StatsOverview extends BaseWidget
{
    protected function getStats(): array
    {
        return [
            Card::make('Total Siswa', '1,234')
                ->description('32% increase')
                ->descriptionIcon('heroicon-m-arrow-trending-up')
                ->color('success'),
        ];
    }
}
```

### Mengubah Sidebar Navigation
```php
// AdminPanelProvider.php
->navigationGroups([
    'Manajemen Data',
    'Laporan',
    'Pengaturan',
])
```

### Custom Login Page
```php
// AdminPanelProvider.php
->login(\App\Filament\Pages\Auth\Login::class)
```

---

## 🎯 Next Steps (Optional)

### 1. Add Custom Logo
```bash
# Simpan logo di public/images/logo.png
# Update AdminPanelProvider.php
->brandLogo(asset('images/logo.png'))
->darkModeBrandLogo(asset('images/logo-dark.png'))
```

### 2. Add Custom Favicon
```bash
# Simpan favicon di public/favicon.ico
# Sudah di-config di AdminPanelProvider.php
```

### 3. Custom Dashboard Widgets
```bash
php artisan make:filament-widget TotalSiswaWidget --stats-overview
```

### 4. Enable Dark Mode
```php
// AdminPanelProvider.php
->darkMode(true)
```

---

## 📱 Consistency dengan Mobile App

| Element | Mobile App | Filament Admin |
|---------|-----------|----------------|
| Primary Color | Deep Blue #1E40AF | ✅ Same |
| Secondary Color | Emerald #059669 | ✅ Same |
| Accent Color | Amber #F59E0B | ✅ Same |
| Font | Inter | ✅ Same |
| Border Radius | 12-16dp | ✅ 0.75-1rem |
| Shadow | Elevation 2-8dp | ✅ Similar |
| Gradient | Yes | ✅ Yes |

---

## 🐛 Troubleshooting

### CSS Tidak Muncul
```bash
npm run build
php artisan view:clear
```

### Warna Tidak Berubah
```bash
php artisan filament:cache-components
php artisan cache:clear
```

### Error Vite
```bash
# Pastikan vite.config.js sudah benar
# Jalankan development mode
npm run dev
```

---

## 📞 Support

Jika ada masalah:
1. Cek console browser untuk error
2. Pastikan `npm run build` berhasil
3. Clear semua cache Laravel
4. Restart server PHP

---

**Theme berhasil dikustomisasi! 🎉**
Admin panel sekarang matching dengan design mobile app.
