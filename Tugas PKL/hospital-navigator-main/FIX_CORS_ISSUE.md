# Fix CORS Issue - Quick Guide

## Problem
```
Access to XMLHttpRequest at 'http://localhost:3001/api/v1/qr-anchors' 
from origin 'http://127.0.0.1:8080' has been blocked by CORS policy
```

## Solution Applied

### 1. Updated Backend CORS Configuration
**File:** `server/src/index.js`
- Added more detailed logging
- Added explicit methods and headers
- Better error messages

### 2. Updated Allowed Origins
**File:** `server/.env`
Added more development origins:
- `http://localhost:5173`
- `http://localhost:3000`
- `http://localhost:8080`
- `http://127.0.0.1:8080`
- `http://127.0.0.1:5173`
- `http://127.0.0.1:3000`

## Steps to Fix

### Step 1: Stop Backend Server
If backend is running, stop it with `Ctrl+C`

### Step 2: Restart Backend
```bash
cd server
npm run dev
```

### Step 3: Verify CORS Configuration
You should see in console:
```
[CORS] Allowed origins: [
  'http://localhost:5173',
  'http://localhost:3000',
  'http://localhost:8080',
  'http://127.0.0.1:8080',
  'http://127.0.0.1:5173',
  'http://127.0.0.1:3000'
]
```

### Step 4: Test API Endpoint
Open browser console and test:
```javascript
fetch('http://localhost:3001/api/v1/qr-anchors')
  .then(r => r.json())
  .then(d => console.log(d))
```

### Step 5: Refresh Frontend
Refresh your frontend page at `http://127.0.0.1:8080`

## Verification

### Check Backend Logs
When frontend makes request, you should see:
```
[CORS] Request from origin: http://127.0.0.1:8080
[CORS] Origin allowed: http://127.0.0.1:8080
```

### Check Browser Console
- No CORS errors
- API requests succeed
- Data loads properly

## If Still Not Working

### Option 1: Add Your Specific Origin
Edit `server/.env` and add your frontend URL:
```env
CORS_ORIGIN=http://localhost:5173,...,http://YOUR_FRONTEND_URL
```

### Option 2: Allow All Origins (Development Only)
**⚠️ Only for development, never in production!**

Edit `server/src/index.js`:
```javascript
app.use(
  cors({
    origin: '*', // Allow all origins
    credentials: true,
  })
);
```

### Option 3: Check Frontend URL
Verify your frontend is actually running on `http://127.0.0.1:8080`:
```bash
# Check what port your frontend is using
npm run dev
# Look for: "Local: http://127.0.0.1:XXXX"
```

## Common Issues

### Issue 1: Backend Not Restarted
**Solution:** Always restart backend after changing `.env`

### Issue 2: Wrong Port
**Solution:** Check actual frontend port and add to CORS_ORIGIN

### Issue 3: HTTPS vs HTTP
**Solution:** Make sure both use same protocol (http or https)

### Issue 4: Trailing Slash
**Solution:** Don't add trailing slash in CORS_ORIGIN
- ✅ `http://localhost:8080`
- ❌ `http://localhost:8080/`

## Testing Commands

### Test from Command Line
```bash
# Should return data without CORS error
curl -H "Origin: http://127.0.0.1:8080" \
     -H "Access-Control-Request-Method: GET" \
     -H "Access-Control-Request-Headers: Content-Type" \
     -X OPTIONS \
     http://localhost:3001/api/v1/qr-anchors -v
```

### Test from Browser Console
```javascript
// Should log data, not CORS error
fetch('http://localhost:3001/api/v1/qr-anchors', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
  },
})
  .then(response => response.json())
  .then(data => console.log('Success:', data))
  .catch(error => console.error('Error:', error));
```

## Quick Fix Checklist

- [ ] Backend `.env` has correct CORS_ORIGIN
- [ ] Backend server restarted after `.env` change
- [ ] Frontend URL matches one in CORS_ORIGIN
- [ ] No typos in origins (check http vs https, localhost vs 127.0.0.1)
- [ ] Browser console shows no CORS errors
- [ ] Backend logs show "Origin allowed"

## Success Indicators

✅ Backend logs: `[CORS] Origin allowed: http://127.0.0.1:8080`  
✅ Browser console: No CORS errors  
✅ Network tab: Status 200 OK  
✅ Data loads in frontend  

## Need More Help?

If issue persists:
1. Check backend console for CORS logs
2. Check browser console for exact error
3. Verify frontend is on `http://127.0.0.1:8080`
4. Try Option 2 (allow all origins) temporarily
5. Check if backend is actually running on port 3001

## Production Note

For production deployment:
- Only add production domain to CORS_ORIGIN
- Never use `origin: '*'`
- Use environment-specific .env files
- Example: `CORS_ORIGIN=https://yourdomain.com`
