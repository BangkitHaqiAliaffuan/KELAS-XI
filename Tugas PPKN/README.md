# 🏛️ Website Keberagaman Kota Palembang - Multi-Page Presentation

Sebuah website presentasi interaktif yang menampilkan keberagaman budaya Kota Palembang dengan gaya presentasi multi-page yang modern dan elegant.

## ✨ Fitur Utama

### 🎨 **Multi-Page Presentation System**
- **9 Halaman Full-Screen**: Setiap section menjadi halaman penuh yang terpisah
- **Smooth Page Transitions**: Transisi antar halaman dengan slide/fade effect menggunakan Framer Motion
- **Keyboard Navigation**: 
  - `→` atau `Space`: Halaman berikutnya
  - `←`: Halaman sebelumnya  
  - `ESC`: Kembali ke halaman awal
- **Swipe Gestures** (Mobile): Geser untuk berpindah halaman

### 🧭 **Fixed Navigation Bar**
- **Glassmorphism Design**: Backdrop blur dengan background maroon/gold
- **Page Indicator**: Menampilkan halaman aktif dari total halaman
- **Navigation Dots**: 9 dots untuk quick jump ke halaman tertentu
- **Mobile Menu**: Hamburger menu dengan list semua sections
- **Auto-hide on Scroll**: Navbar hilang saat scroll down, muncul saat scroll up

### 🎯 **Page Navigation Controls**
- **Floating Arrow Buttons**: Kiri-kanan untuk navigasi (tersembunyi di halaman pertama/terakhir)
- **Dot Navigation (Bottom Center)**: 9 dots dengan tooltip nama section
- **Page Counter (Bottom Right)**: Tampilan "1 / 9" yang elegant
- **Hover Effects**: Glow dan scale animation

### 💎 **Enhanced Sections dengan Glassmorphism**

#### 1. **Hero Page** 
- Animated gradient background (maroon to gold)
- Floating particles effect
- Text gradient gold-yellow
- Bounce button animation
- Scroll indicator

#### 2. **Makanan Page**
- Grid cards dengan glassmorphism effect
- Badge "Kuliner Khas"  
- Hover: lift up dengan shadow glow gold
- Image zoom on hover
- Play button overlay untuk video
- Decorative rotating elements

#### 3. **Pakaian Adat Page** *(Coming Soon)*
- Split screen layout (Aesan Gede vs Aesan Pak Sangkong)
- Frame ornamen emas
- Songket pattern background

#### 4. **Kesenian Page** *(Coming Soon)*
- Featured carousel
- Auto-play dengan pause on hover
- Progress bar

#### 5. **Pekerjaan Page** *(Coming Soon)*
- Animated icons dengan hexagon layout
- Line connections antar icons
- City silhouette background

#### 6. **Adat Unik Page** *(Coming Soon)*
- Vertical timeline dengan alternating layout
- Numbered badges dengan gold border
- Stagger animation entrance

#### 7. **Nilai-Nilai Page** *(Coming Soon)*
- 2x2 grid symmetric layout
- Large animated icons
- Center ornament

#### 8. **Kesimpulan Page** *(Coming Soon)*
- Center-aligned quote box
- Decorative quotation marks
- Elegant frame khas Palembang

#### 9. **Thank You Page** ✅
- Confetti animation on load
- Quote box dengan border ornament
- Credits (Kelas XI RPL)
- "Kembali ke Awal" button
- Floating ornamental elements

## 🎨 **Design System**

### Color Palette
```css
Maroon: #800000, #a52a2a, #5c0000
Gold: #FFD700, #ffed4e, #d4af37  
Cream: #FFF8DC
Bronze: #CD7F32
```

### Glassmorphism Style
```css
background: rgba(255, 255, 255, 0.1);
backdrop-filter: blur(10px);
border: 1px solid rgba(255, 215, 0, 0.2);
box-shadow: 0 8px 32px 0 rgba(128, 0, 0, 0.2);
```

### Animations
- Fade In/Out
- Slide transitions (left/right)
- Bounce effects
- Glow effects
- Rotate animations
- Scale transformations

## 🚀 **Tech Stack**

- **Framework**: Next.js 16.0.0 (React 19)
- **Styling**: Tailwind CSS 4.1.9
- **Animations**: Framer Motion
- **Icons**: Lucide React
- **Confetti**: canvas-confetti
- **UI Components**: Radix UI + shadcn/ui

## 📦 **Installation**

```bash
# Clone repository
git clone <repository-url>
cd "Tugas PPKN"

# Install dependencies
npm install --legacy-peer-deps

# Run development server
npm run dev
```

Buka [http://localhost:3000](http://localhost:3000) di browser.

## 🏗️ **Project Structure**

```
app/
├── globals.css          # Global styles + glassmorphism
├── layout.tsx           # Root layout
└── page.tsx            # Main page dengan multi-page logic

components/
├── navbar.tsx           # Fixed navigation bar
├── page-navigation.tsx  # Navigation controls (arrows, dots)
├── loading-screen.tsx   # Loading screen with animation
└── sections/
    ├── hero-section.tsx           ✅ Enhanced
    ├── makanan-section.tsx        ✅ Enhanced  
    ├── pakaian-section.tsx        🚧 Todo
    ├── kesenian-section.tsx       🚧 Todo
    ├── pekerjaan-section.tsx      🚧 Todo
    ├── adat-unik-section.tsx      🚧 Todo
    ├── nilai-section.tsx          🚧 Todo
    ├── kesimpulan-section.tsx     🚧 Todo
    └── thank-you-section.tsx      ✅ Complete
```

## 🎯 **Next Steps (TODO)**

- [ ] Enhance remaining sections (Pakaian, Kesenian, dll)
- [ ] Add swipe gesture support for mobile
- [ ] Implement page transition sound effects (optional)
- [ ] Add more batik patterns as background
- [ ] Optimize images (WebP format)
- [ ] Add loading states for images
- [ ] Implement page progress bar
- [ ] Add share functionality
- [ ] SEO optimization

## 📱 **Responsive Design**

- **Desktop**: Full multi-page experience dengan floating arrows
- **Tablet**: Adjusted layout, still paginated
- **Mobile**: 
  - Simplified navigation
  - Swipe gestures (planned)
  - Bottom navigation bar
  - Hamburger menu

## 🎓 **Credits**

**Dibuat oleh**: Kelas XI RPL  
**Mata Pelajaran**: PPKn  
**Tema**: Keberagaman Budaya Indonesia - Kota Palembang

## 📄 **License**

This project is for educational purposes (Tugas PPKn).

---

**Catatan**: Project ini masih dalam pengembangan. Beberapa section masih menggunakan design lama dan akan di-enhance secara bertahap dengan glassmorphism effect dan animasi yang lebih baik.

🔥 **WOW Factor**: Smooth transitions, glassmorphism effects, elegant animations, dan professional presentation style!
