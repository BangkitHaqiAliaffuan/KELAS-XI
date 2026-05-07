# 🧪 Hospital Navigator - Automated Test Script

## 🎯 Quick Test Commands

Copy dan paste command ini ke Kiro untuk automated testing:

### Test 1: Homepage & Language Switcher ⚡
```
Test homepage and language switcher:
1. Navigate to http://localhost:5173
2. Wait for page to load
3. Take screenshot named "01-homepage.png"
4. Click the button with Globe icon (language switcher)
5. Wait 1 second
6. Take screenshot named "02-language-switched.png"
7. Report if language changed successfully
```

### Test 2: Map Zoom Controls ⚡
```
Test map zoom functionality:
1. Navigate to http://localhost:5173
2. Wait for map to load
3. Take screenshot named "03-map-default.png"
4. Click the Plus icon button 3 times (zoom in)
5. Wait 1 second
6. Take screenshot named "04-map-zoomed-in.png"
7. Click the Minus icon button 2 times (zoom out)
8. Wait 1 second
9. Take screenshot named "05-map-zoomed-out.png"
10. Report zoom functionality status
```

### Test 3: Search Functionality ⚡
```
Test room search:
1. Navigate to http://localhost:5173
2. Wait for page to load
3. Click the search input field
4. Type "IGD" in the search box
5. Wait 1 second for results
6. Take screenshot named "06-search-results.png"
7. Report if search results appeared
```

### Test 4: Floor Switching ⚡
```
Test floor switching:
1. Navigate to http://localhost:5173
2. Wait for map to load
3. Take screenshot named "07-floor1.png"
4. Click button with text "Lantai 2"
5. Wait 2 seconds for map to load
6. Take screenshot named "08-floor2.png"
7. Click button with text "Lantai 1"
8. Wait 2 seconds
9. Take screenshot named "09-back-to-floor1.png"
10. Report floor switching status
```

### Test 5: Navigation Dialog ⚡
```
Test navigation dialog:
1. Navigate to http://localhost:5173
2. Wait for page to load
3. Click button with text "Pindai QR Code"
4. Wait 1 second
5. Take screenshot named "10-navigation-dialog.png"
6. Report if dialog opened successfully
```

### Test 6: Mobile Responsive ⚡
```
Test mobile responsive design:
1. Resize browser to 375x667 (mobile size)
2. Navigate to http://localhost:5173
3. Wait for page to load
4. Take screenshot named "11-mobile-view.png"
5. Click the hamburger menu icon (Menu icon)
6. Wait 1 second
7. Take screenshot named "12-mobile-menu-open.png"
8. Resize browser back to 1920x1080
9. Report mobile responsiveness status
```

### Test 7: Complete Flow ⚡
```
Run complete test flow:
1. Navigate to http://localhost:5173
2. Take screenshot "test-01-homepage.png"
3. Click language switcher
4. Take screenshot "test-02-english.png"
5. Type "Lab" in search
6. Take screenshot "test-03-search.png"
7. Click "Lantai 2"
8. Take screenshot "test-04-floor2.png"
9. Click zoom in 3 times
10. Take screenshot "test-05-zoomed.png"
11. Click "Pindai QR Code"
12. Take screenshot "test-06-navigation.png"
13. Generate summary report
```

## 🎭 Advanced Tests

### Performance Test
```
Test performance metrics:
1. Navigate to http://localhost:5173
2. Get all network requests
3. Check console for errors
4. Measure page load time
5. Click zoom in 10 times and measure FPS
6. Report performance metrics
```

### Accessibility Test
```
Test accessibility:
1. Navigate to http://localhost:5173
2. Check for proper ARIA labels
3. Test keyboard navigation (Tab key)
4. Verify all buttons are accessible
5. Take screenshot
6. Report accessibility issues
```

### Cross-Browser Test
```
Test in different viewports:
1. Test at 1920x1080 (Desktop)
2. Test at 1366x768 (Laptop)
3. Test at 768x1024 (Tablet)
4. Test at 375x667 (Mobile)
5. Take screenshots for each
6. Report any layout issues
```

## 📋 Test Checklist

### Before Testing
- [ ] Dev server running (`npm run dev`)
- [ ] Server accessible at http://localhost:5173
- [ ] Playwright MCP enabled in Kiro
- [ ] Browser can open

### During Testing
- [ ] Homepage loads correctly
- [ ] Language switcher works
- [ ] Map zoom in/out works
- [ ] Search functionality works
- [ ] Floor switching works
- [ ] Navigation dialog opens
- [ ] Mobile menu works
- [ ] No console errors

### After Testing
- [ ] Review all screenshots
- [ ] Check test report
- [ ] Document any bugs found
- [ ] Fix critical issues
- [ ] Re-test fixed features

## 🐛 Common Issues & Solutions

### Issue: "Connection refused"
**Solution**: Start dev server first
```bash
npm run dev
```

### Issue: "Element not found"
**Solution**: Add wait time
```
Wait 2 seconds before clicking button
```

### Issue: "Screenshot is blank"
**Solution**: Wait for content to load
```
Wait for map to be visible before taking screenshot
```

### Issue: "Button not clickable"
**Solution**: Scroll element into view
```
Scroll to button, then click it
```

## 📊 Expected Results

### ✅ Passing Tests
- Homepage loads in < 3 seconds
- Language switches instantly
- Map zoom is smooth
- Search shows results in < 1 second
- Floor switch loads in < 2 seconds
- Navigation dialog opens instantly
- Mobile menu works correctly
- No console errors

### ❌ Failing Tests
If any test fails, check:
1. Is dev server running?
2. Are there console errors?
3. Did elements load properly?
4. Is network connection stable?

## 🚀 Quick Start

### Option 1: Run Single Test
```
Test language switcher on http://localhost:5173
```

### Option 2: Run Test Suite
```
Run all tests for Hospital Navigator at http://localhost:5173
```

### Option 3: Custom Test
```
Test [specific feature] on http://localhost:5173:
1. [Your step 1]
2. [Your step 2]
3. Take screenshot
4. Report results
```

## 💡 Pro Tips

1. **Always wait for elements**: Add "wait 1 second" between actions
2. **Use descriptive names**: Name screenshots clearly
3. **Test incrementally**: Test one feature at a time first
4. **Check console**: Monitor for JavaScript errors
5. **Take screenshots**: Capture before and after states
6. **Document bugs**: Note any issues found
7. **Re-test fixes**: Verify bugs are resolved

## 📝 Test Report Template

```
# Hospital Navigator Test Report
Date: [Date]
Tester: Kiro AI
Environment: http://localhost:5173

## Test Results

### 1. Homepage Load
- Status: ✅ PASSED / ❌ FAILED
- Load Time: [X]s
- Screenshot: 01-homepage.png
- Notes: [Any observations]

### 2. Language Switcher
- Status: ✅ PASSED / ❌ FAILED
- Response Time: [X]s
- Screenshot: 02-language-switched.png
- Notes: [Any observations]

### 3. Map Zoom
- Status: ✅ PASSED / ❌ FAILED
- Performance: [Smooth/Laggy]
- Screenshots: 03-05-map-zoom.png
- Notes: [Any observations]

### 4. Search Functionality
- Status: ✅ PASSED / ❌ FAILED
- Results Time: [X]s
- Screenshot: 06-search-results.png
- Notes: [Any observations]

### 5. Floor Switching
- Status: ✅ PASSED / ❌ FAILED
- Load Time: [X]s
- Screenshots: 07-09-floor-switch.png
- Notes: [Any observations]

### 6. Navigation Dialog
- Status: ✅ PASSED / ❌ FAILED
- Open Time: [X]s
- Screenshot: 10-navigation-dialog.png
- Notes: [Any observations]

### 7. Mobile Responsive
- Status: ✅ PASSED / ❌ FAILED
- Layout: [Good/Issues]
- Screenshots: 11-12-mobile.png
- Notes: [Any observations]

## Summary
- Total Tests: 7
- Passed: [X]
- Failed: [X]
- Success Rate: [X]%

## Issues Found
1. [Issue description]
2. [Issue description]

## Recommendations
1. [Recommendation]
2. [Recommendation]
```

---

**Ready to start testing?** 🎭

Just copy any test command above and paste it to Kiro!

**Example**:
```
Test homepage and language switcher on http://localhost:5173
```
