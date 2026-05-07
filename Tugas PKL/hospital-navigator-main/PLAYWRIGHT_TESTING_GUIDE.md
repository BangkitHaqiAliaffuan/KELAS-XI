# 🎭 Playwright MCP - Automated Testing Guide

## 📋 Overview

Panduan lengkap untuk automated testing Hospital Navigator menggunakan Playwright MCP yang sudah terkonfigurasi di Kiro.

## ✅ Prerequisites

### 1. Pastikan MCP Playwright Aktif
File `~/.kiro/settings/mcp.json` sudah dikonfigurasi:
```json
{
  "mcpServers": {
    "playwright": {
      "command": "npx",
      "args": ["@playwright/mcp@latest"],
      "disabled": false
    }
  }
}
```

### 2. Start Development Server
```bash
# Terminal 1 - Start dev server
npm run dev

# Server akan berjalan di http://localhost:5173
```

## 🚀 Automated Testing Scenarios

### Scenario 1: Test Homepage Load
**Tujuan**: Verify homepage loads correctly

**Steps**:
1. Navigate to homepage
2. Take screenshot
3. Verify title
4. Check main elements exist

**Kiro Commands**:
```
Test homepage - navigate to http://localhost:5173, take screenshot, and verify the page title contains "Hospital Navigator"
```

### Scenario 2: Test Language Switcher
**Tujuan**: Verify language switching works

**Steps**:
1. Navigate to homepage
2. Click language switcher button
3. Verify language changed
4. Take screenshot

**Kiro Commands**:
```
Test language switcher - navigate to http://localhost:5173, click the language button with Globe icon, take screenshot, and verify the page language changed
```

### Scenario 3: Test Map Interaction
**Tujuan**: Verify map can be zoomed and panned

**Steps**:
1. Navigate to homepage
2. Find map container
3. Click zoom in button
4. Click zoom out button
5. Take screenshot

**Kiro Commands**:
```
Test map zoom - navigate to http://localhost:5173, find and click the zoom in button (Plus icon), then click zoom out button (Minus icon), take screenshot
```

### Scenario 4: Test Room Search
**Tujuan**: Verify search functionality works

**Steps**:
1. Navigate to homepage
2. Find search input
3. Type "IGD"
4. Verify search results appear
5. Take screenshot

**Kiro Commands**:
```
Test room search - navigate to http://localhost:5173, find the search input, type "IGD", wait for results, and take screenshot
```

### Scenario 5: Test Navigation Dialog
**Tujuan**: Verify navigation dialog opens and works

**Steps**:
1. Navigate to homepage
2. Click "Scan QR Code" tab
3. Verify navigation dialog opens
4. Take screenshot

**Kiro Commands**:
```
Test navigation dialog - navigate to http://localhost:5173, click the "Pindai QR Code" button, verify dialog opens, take screenshot
```

### Scenario 6: Test Floor Switching
**Tujuan**: Verify floor switching works

**Steps**:
1. Navigate to homepage
2. Click "Lantai 2" button
3. Verify map changes
4. Take screenshot

**Kiro Commands**:
```
Test floor switch - navigate to http://localhost:5173, click "Lantai 2" button, wait for map to load, take screenshot
```

### Scenario 7: Test Mobile Responsive
**Tujuan**: Verify mobile layout works

**Steps**:
1. Resize browser to mobile size
2. Navigate to homepage
3. Verify mobile menu works
4. Take screenshot

**Kiro Commands**:
```
Test mobile view - resize browser to 375x667, navigate to http://localhost:5173, click hamburger menu, take screenshot
```

### Scenario 8: Test Room Selection
**Tujuan**: Verify room can be selected and info displayed

**Steps**:
1. Navigate to homepage
2. Click on a room in the map
3. Verify room info card appears
4. Take screenshot

**Kiro Commands**:
```
Test room selection - navigate to http://localhost:5173, wait for map to load, click on IGD room in the map, verify info card appears, take screenshot
```

## 🎯 Complete Test Suite

### Full Automated Test Flow
```
Run complete test suite:

1. Navigate to http://localhost:5173
2. Take screenshot of homepage
3. Click language switcher
4. Take screenshot after language change
5. Click search input and type "Lab"
6. Take screenshot of search results
7. Click "Lantai 2" button
8. Take screenshot of floor 2
9. Click zoom in button 3 times
10. Take screenshot of zoomed map
11. Click "Pindai QR Code" tab
12. Take screenshot of navigation dialog
```

## 📝 Example Test Scripts

### Test Script 1: Basic Functionality
```typescript
// You can ask Kiro to run this test:
"Test basic functionality:
1. Navigate to http://localhost:5173
2. Verify page title contains 'Navigator'
3. Click language button
4. Verify language changed to English
5. Take screenshot
6. Report results"
```

### Test Script 2: Navigation Flow
```typescript
// You can ask Kiro to run this test:
"Test navigation flow:
1. Navigate to http://localhost:5173
2. Click search input
3. Type 'IGD'
4. Click first search result
5. Verify room info appears
6. Take screenshot
7. Report results"
```

### Test Script 3: Performance Test
```typescript
// You can ask Kiro to run this test:
"Test performance:
1. Navigate to http://localhost:5173
2. Measure page load time
3. Click zoom in 5 times
4. Measure render time
5. Take screenshot
6. Report performance metrics"
```

## 🔧 Advanced Testing

### Custom Test with Playwright Code
You can ask Kiro to run custom Playwright code:

```javascript
"Run this Playwright test:
await page.goto('http://localhost:5173');
await page.waitForSelector('[data-testid=\"map-container\"]');
const mapElement = await page.$('[data-testid=\"map-container\"]');
expect(mapElement).toBeTruthy();
await page.screenshot({ path: 'test-map.png' });
"
```

### Network Monitoring
```
"Test with network monitoring:
1. Navigate to http://localhost:5173
2. Monitor network requests
3. Filter for API calls
4. Verify no failed requests
5. Report network stats"
```

### Console Error Detection
```
"Test for console errors:
1. Navigate to http://localhost:5173
2. Monitor console messages
3. Interact with map (zoom, pan)
4. Check for any errors
5. Report console logs"
```

## 📊 Test Reports

### Screenshot Naming Convention
- `homepage-{timestamp}.png` - Homepage screenshot
- `language-switch-{timestamp}.png` - After language switch
- `search-results-{timestamp}.png` - Search results
- `floor2-{timestamp}.png` - Floor 2 view
- `navigation-dialog-{timestamp}.png` - Navigation dialog
- `mobile-view-{timestamp}.png` - Mobile responsive view

### Test Results Format
```
✅ Test: Homepage Load
   - Status: PASSED
   - Load Time: 1.2s
   - Screenshot: homepage-2024.png

✅ Test: Language Switch
   - Status: PASSED
   - Switch Time: 0.3s
   - Screenshot: language-switch-2024.png

❌ Test: Room Search
   - Status: FAILED
   - Error: Search input not found
   - Screenshot: search-error-2024.png
```

## 🐛 Troubleshooting

### Issue: Browser won't open
**Solution**:
```bash
# Install Playwright browsers
npx playwright install
```

### Issue: Connection refused
**Solution**:
```bash
# Make sure dev server is running
npm run dev
```

### Issue: Elements not found
**Solution**:
- Add wait time: "wait 2 seconds before clicking"
- Use better selectors: "click button with text 'Pindai QR Code'"
- Check if element is visible: "verify button is visible first"

### Issue: Screenshots are blank
**Solution**:
- Wait for content: "wait for map to load before screenshot"
- Check viewport size: "resize browser to 1920x1080"

## 💡 Best Practices

### 1. Always Wait for Elements
```
❌ Bad: "click button"
✅ Good: "wait for button to be visible, then click"
```

### 2. Use Descriptive Selectors
```
❌ Bad: "click first button"
✅ Good: "click button with text 'Lantai 2'"
```

### 3. Take Screenshots at Key Points
```
✅ After navigation
✅ After user interaction
✅ Before and after state changes
✅ When errors occur
```

### 4. Verify State Changes
```
✅ Verify element appears
✅ Verify text changes
✅ Verify URL changes
✅ Verify no console errors
```

### 5. Clean Up After Tests
```
✅ Close dialogs
✅ Reset state
✅ Clear local storage if needed
```

## 🎓 Example Test Sessions

### Session 1: Quick Smoke Test (2 minutes)
```
1. "Navigate to http://localhost:5173 and take screenshot"
2. "Click language button and take screenshot"
3. "Click Lantai 2 and take screenshot"
4. "Report: All basic features working"
```

### Session 2: Comprehensive Test (10 minutes)
```
1. "Test homepage load and take screenshot"
2. "Test language switcher functionality"
3. "Test search with query 'IGD'"
4. "Test floor switching to Lantai 2"
5. "Test zoom in and out"
6. "Test navigation dialog"
7. "Test mobile responsive at 375px width"
8. "Generate test report with all screenshots"
```

### Session 3: Performance Test (5 minutes)
```
1. "Navigate to http://localhost:5173"
2. "Measure page load time"
3. "Click zoom in 10 times and measure performance"
4. "Switch floors 5 times and measure render time"
5. "Report performance metrics"
```

## 📚 Resources

### Playwright MCP Documentation
- [Playwright MCP GitHub](https://github.com/microsoft/playwright-mcp)
- [Playwright Documentation](https://playwright.dev)

### Useful Selectors
- Search input: `input[placeholder*="Cari"]`
- Language button: `button:has(svg.lucide-globe)`
- Zoom in: `button:has(svg.lucide-plus)`
- Zoom out: `button:has(svg.lucide-minus)`
- Floor 2 button: `button:has-text("Lantai 2")`
- Map container: `[data-testid="map-container"]` or `.map-viewer`

### Common Commands
```bash
# Take screenshot
"take screenshot and save as test.png"

# Click element
"click button with text 'Submit'"

# Type text
"type 'IGD' in search input"

# Wait
"wait 2 seconds"

# Verify
"verify element with text 'Success' is visible"

# Navigate
"navigate to http://localhost:5173"

# Resize
"resize browser to 1920x1080"
```

## 🚀 Quick Start

### Step 1: Start Server
```bash
npm run dev
```

### Step 2: Ask Kiro to Test
```
"Test Hospital Navigator website at http://localhost:5173:
1. Take screenshot of homepage
2. Test language switcher
3. Test map zoom
4. Test search functionality
5. Generate report with screenshots"
```

### Step 3: Review Results
- Check screenshots in current directory
- Review test report
- Fix any issues found

---

**Ready to test?** 🎭

Just say: "Test my Hospital Navigator website" and I'll run the automated tests!
