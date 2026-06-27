# Changelog - AI Cooking Assistant

## [Unreleased] - 2026-05-05

### Fixed
- ✅ **Hydration Error**: Fixed React hydration mismatch in `ResultDisplay.tsx` by using mount check instead of state-based `speechSupported`
- ✅ **Model 404 Error**: Changed Gemini model from `gemini-1.5-flash` to `gemini-pro-vision` for better compatibility
- ✅ **Build Error**: Fixed syntax error in `VoiceWake.tsx` - removed duplicate code and malformed export statement

### Added
- ✅ **VoiceWake Component**: Voice wake-word detection component with "Hey Chef" trigger
- ✅ **Enhanced Error Logging**: Added detailed logging in API route for better debugging
- ✅ **Model List Script**: Created `scripts/list-models.js` to check available Gemini models
- ✅ **PRD Generator**: Created `scripts/prd.js` to generate Product Requirements Document
- ✅ **Comprehensive Documentation**:
  - `TROUBLESHOOTING.md` - Complete troubleshooting guide
  - `MODEL_INFO.md` - Gemini model information and comparison
  - `FIXES.md` - Technical explanation of fixes applied
  - `LANGKAH_MANUAL.md` - Step-by-step manual setup (Indonesian)
  - `QUICK_START.md` - Quick reference guide
  - `SETUP_GUIDE.md` - Detailed setup guide (Indonesian)

### Changed
- ✅ **API Route**: Updated to use `gemini-pro-vision` model with fallback strategy
- ✅ **Error Messages**: More descriptive error messages with actionable details
- ✅ **Environment Setup**: Added `.env.local.example` template

### Technical Details

#### Hydration Fix
**Problem**: Server and client render mismatch for `speechSupported` state.

**Solution**: 
```typescript
// Before (❌)
const [speechSupported, setSpeechSupported] = useState(false);
useEffect(() => setSpeechSupported('speechSynthesis' in window), []);

// After (✅)
const [isMounted, setIsMounted] = useState(false);
useEffect(() => setIsMounted(true), []);
const speechSupported = isMounted && typeof window !== 'undefined' && 'speechSynthesis' in window;
```

#### Model Fix
**Problem**: `models/gemini-1.5-flash is not found for API version v1beta`

**Solution**: Changed to `gemini-pro-vision` which is:
- ✅ More stable for image analysis
- ✅ Available in all regions
- ✅ Supports generateContent with images

#### VoiceWake Fix
**Problem**: Duplicate code and malformed export inside function body

**Solution**: Cleaned up file structure, removed duplicates, proper component export

---

## Project Status

### ✅ Working Features
- Real-time camera access
- AI ingredient analysis (Gemini Pro Vision)
- Voice output (Web Speech API)
- Voice wake-word detection
- Responsive UI (Tailwind CSS)
- Error handling and logging

### 🚧 In Progress
- Recipe browser integration
- Contextual memory system
- Offline recipe caching

### 📋 Planned (v1.1)
- Improvisation mode (generate recipes from available ingredients)
- Freshness detection
- Cooking session logs
- Multi-language support

---

## Development Commands

```bash
# Install dependencies
npm install

# Run development server
npm run dev

# Build for production
npm run build

# Run production server
npm start

# Generate PRD document
node scripts/prd.js

# List available Gemini models
node scripts/list-models.js
```

---

## Environment Variables

Required in `.env.local`:
```env
GOOGLE_AI_API_KEY=your_api_key_here
```

Get your free API key: https://aistudio.google.com/app/apikey

---

## Browser Compatibility

| Feature | Chrome | Edge | Firefox | Safari |
|---------|--------|------|---------|--------|
| Camera | ✅ | ✅ | ✅ | ✅ |
| Speech Synthesis | ✅ | ✅ | ⚠️ Limited | ✅ |
| Speech Recognition | ✅ | ✅ | ❌ | ✅ |
| AI Analysis | ✅ | ✅ | ✅ | ✅ |

**Recommended**: Chrome or Edge for full feature support

---

## Known Issues

### Minor Issues
- Speech Recognition not supported in Firefox (fallback to manual mode)
- Camera may require HTTPS in production (localhost is OK for development)

### Workarounds
- For Firefox users: Use manual text input instead of voice commands
- For production: Deploy to Vercel (automatic HTTPS)

---

## Contributors

- Initial development: May 2026
- Framework: Next.js 16.2.4
- AI: Google Gemini Pro Vision
- Voice: Web Speech API

---

## License

MIT License - Free for personal and commercial use

---

**Last Updated**: May 5, 2026
