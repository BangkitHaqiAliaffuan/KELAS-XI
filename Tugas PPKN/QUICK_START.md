# 🚀 Quick Start Guide

## Prerequisites
- Node.js 18+ 
- npm atau yarn atau pnpm

## Installation Steps

1. **Install Dependencies**
   ```bash
   npm install --legacy-peer-deps
   ```

2. **Run Development Server**
   ```bash
   npm run dev
   ```

3. **Open Browser**
   ```
   http://localhost:3000
   ```

## Key Features to Test

### ✅ Navigation
- Click arrow buttons (left/right) to navigate
- Use keyboard: `→` next, `←` prev, `ESC` home
- Click dots at bottom to jump to specific page
- Try hamburger menu on mobile view

### ✅ Animations  
- Watch smooth page transitions
- Hover over cards to see effects
- Check floating particles on Hero page
- See confetti on Thank You page

### ✅ Responsive
- Resize browser to test responsive design
- Check mobile menu (< 1024px width)
- Test on different devices

## Troubleshooting

### Port Already in Use
```bash
npm run dev -- -p 3001
```

### Module Not Found
```bash
rm -rf node_modules package-lock.json
npm install --legacy-peer-deps
```

### Build Errors
```bash
npm run build
```
Check console for specific errors.

## Development Tips

### Hot Reload
File changes will auto-reload the page.

### Component Structure
- Each section is a separate component
- Navigation is handled in `app/page.tsx`
- Styles use Tailwind + custom CSS

### Adding New Pages
1. Create component in `components/sections/`
2. Add to `sections` array in `app/page.tsx`
3. Import the component

## Browser Support
- Chrome 90+ ✅
- Firefox 88+ ✅
- Safari 14+ ✅
- Edge 90+ ✅

## Performance Notes
- Framer Motion animations are GPU-accelerated
- Images lazy load automatically
- Glassmorphism uses CSS backdrop-filter

---

🎉 **Selamat mencoba!** Jika ada pertanyaan, check README.md untuk dokumentasi lengkap.
