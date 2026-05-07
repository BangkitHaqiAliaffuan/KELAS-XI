# 🧪 Language Switcher Testing Guide

## Manual Testing Checklist

### Visual Testing
- [ ] **Bendera Indonesia** tampil dengan benar (merah di atas, putih di bawah)
- [ ] **Bendera UK** tampil dengan benar (Union Jack)
- [ ] **Border** bendera terlihat jelas
- [ ] **Shadow** bendera terlihat subtle
- [ ] **Size** bendera proporsional (h-4 w-6)
- [ ] **Rounded corners** bendera terlihat (rounded-sm)

### Functionality Testing
- [ ] Klik tombol → bahasa berubah
- [ ] Bendera berubah sesuai bahasa berikutnya
- [ ] Label "Indonesia" / "English" berubah
- [ ] Icon Globe tetap terlihat
- [ ] Hover effect bekerja
- [ ] Tooltip muncul saat hover

### Responsive Testing
- [ ] **Desktop** (>768px): Label text terlihat
- [ ] **Mobile** (<768px): Label text hidden, hanya icon + bendera
- [ ] Button tidak terlalu besar/kecil
- [ ] Spacing proporsional

### Accessibility Testing
- [ ] `aria-label` ada dan deskriptif
- [ ] `title` attribute ada
- [ ] Keyboard navigation works (Tab + Enter)
- [ ] Screen reader friendly

## Expected Behavior

### Saat Bahasa Indonesia Aktif
```
[Globe Icon] [🇬🇧 UK Flag] English
```
- Menampilkan bendera UK (untuk switch ke English)
- Label: "English"

### Saat Bahasa English Aktif
```
[Globe Icon] [🇮🇩 ID Flag] Indonesia
```
- Menampilkan bendera Indonesia (untuk switch ke Indonesia)
- Label: "Indonesia"

## Visual Comparison

### Before (CSS Flags)
```
❌ Bendera Indonesia: Kotak merah-putih sederhana
❌ Bendera UK: Garis biru-putih-merah tidak jelas
❌ Border tipis, kurang kontras
❌ Ukuran tidak konsisten
```

### After (SVG Flags)
```
✅ Bendera Indonesia: SVG proper dengan warna #FF0000 dan #FFFFFF
✅ Bendera UK: Union Jack lengkap dengan detail
✅ Border jelas dengan shadow-sm
✅ Ukuran konsisten h-4 w-6
✅ Rounded corners untuk polish look
```

## Screenshot Checklist

Take screenshots of:
1. Language switcher in Indonesian mode
2. Language switcher in English mode
3. Hover state
4. Mobile view
5. Desktop view

## Browser Compatibility

Test on:
- [ ] Chrome/Edge (Chromium)
- [ ] Firefox
- [ ] Safari (if available)
- [ ] Mobile Chrome
- [ ] Mobile Safari

## Performance Check

- [ ] No layout shift when switching
- [ ] Smooth transition
- [ ] No console errors
- [ ] SVG renders quickly

## Common Issues to Check

### Issue 1: Bendera tidak terlihat
**Solution**: Check if SVG viewBox is correct

### Issue 2: Border tidak terlihat
**Solution**: Verify `border-border` class exists in theme

### Issue 3: Label terpotong
**Solution**: Check `hidden sm:inline` class

### Issue 4: Spacing tidak pas
**Solution**: Adjust `gap-2` and `px-3 py-2`

## Automated Testing (Optional)

If you want to set up automated tests:

```typescript
// Example with Playwright
test('language switcher displays correct flag', async ({ page }) => {
  await page.goto('http://localhost:5173');
  
  // Check Indonesia flag is visible when English is active
  const flag = page.locator('svg[aria-label="Indonesia Flag"]');
  await expect(flag).toBeVisible();
  
  // Click to switch
  await page.click('button:has-text("Indonesia")');
  
  // Check UK flag is now visible
  const ukFlag = page.locator('svg[aria-label="UK Flag"]');
  await expect(ukFlag).toBeVisible();
});
```

## Success Criteria

All of these should be true:
- ✅ Bendera terlihat jelas dan proper
- ✅ Warna bendera akurat
- ✅ Border dan shadow terlihat
- ✅ Responsive di semua ukuran layar
- ✅ Accessibility compliant
- ✅ No console errors
- ✅ Smooth user experience

---

**Status**: Ready for manual testing
**Estimated Testing Time**: 5-10 minutes
**Priority**: High (UI/UX improvement)
