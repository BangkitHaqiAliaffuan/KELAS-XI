# 🎤 Voice Commands Guide

## ✨ Hands-Free Cooking Experience

AI sekarang bisa **menjawab dengan suara** dan **mengontrol kamera** berdasarkan perintah Anda!

---

## 🎯 Cara Menggunakan

### 1. Aktifkan Voice Assistant
```
Klik "Start Voice Assistant"
```

### 2. Katakan Wake Phrase
```
"Hey Chef"
```

### 3. Berikan Perintah
```
"take picture"
```

### 4. AI Akan:
- ✅ Menjawab dengan suara
- ✅ Mengeksekusi perintah
- ✅ Memberikan feedback visual

---

## 📋 Daftar Perintah

### 🎥 Camera Control

| Perintah | AI Response | Action |
|----------|-------------|--------|
| **"start camera"** | "Starting camera now." | Membuka kamera |
| **"take picture"** | "Taking a picture now. Please hold your ingredients steady." | Capture foto & analyze |
| **"stop camera"** | "Stopping camera." | Menutup kamera |

**Contoh:**
```
You: "Hey Chef, start camera"
AI: 🔊 "Starting camera now."
→ Kamera terbuka otomatis
```

---

### 🍳 Cooking Assistance

| Perintah | AI Response | Action |
|----------|-------------|--------|
| **"next step"** | "Point your camera at the ingredients, and I will guide you through the next step." | Meminta guidance |
| **"what ingredients"** | "Let me identify your ingredients. Please show them to the camera." | Identifikasi bahan |
| **"repeat"** | [Mengulang instruksi terakhir] | Repeat last result |

**Contoh:**
```
You: "Hey Chef, what ingredients"
AI: 🔊 "Let me identify your ingredients. Please show them to the camera."
→ Menampilkan instruksi di layar
```

---

### ℹ️ Information

| Perintah | AI Response |
|----------|-------------|
| **"help"** | "I can help you cook. Say take picture to capture ingredients, start camera to open the camera, or ask me about the next step." |

**Contoh:**
```
You: "Hey Chef, help"
AI: 🔊 "I can help you cook. Say take picture..."
```

---

## 🎬 Complete Workflow Example

### Scenario: Memasak dengan Hands-Free

**Step 1: Aktifkan Voice**
```
You: [Click "Start Voice Assistant"]
AI: Status: "Listening for wake word..."
```

**Step 2: Buka Kamera**
```
You: "Hey Chef, start camera"
AI: 🔊 "Starting camera now."
→ Kamera terbuka
```

**Step 3: Posisikan Ingredients**
```
[Letakkan bahan di depan kamera]
```

**Step 4: Capture & Analyze**
```
You: "Hey Chef, take picture"
AI: 🔊 "Taking a picture now. Please hold your ingredients steady."
→ Foto diambil
→ AI menganalisis
→ Hasil muncul di layar
```

**Step 5: Dengar Hasil**
```
AI: 🔊 [Membacakan hasil analisis]
```

**Step 6: Lanjut Memasak**
```
You: "Hey Chef, next step"
AI: 🔊 "Point your camera at the ingredients..."
```

---

## 🎨 Visual Feedback

### 1. AI Speaking Indicator
Saat AI berbicara, muncul:
```
🔊 AI is speaking...
[Animated sound bars]
```

### 2. Status Updates
```
📸 Taking picture...
📷 Starting camera...
🛑 Stopping camera...
```

### 3. Live Transcript
```
🎤 Live: hey chef take picture
```

### 4. Command History
```
📝 [Wake] hey chef
📝 [Command] take picture
```

---

## 🔊 Voice Response Examples

### Camera Commands
```
"Starting camera now."
"Taking a picture now. Please hold your ingredients steady."
"Stopping camera."
```

### Cooking Help
```
"Point your camera at the ingredients, and I will guide you through the next step."
"Let me identify your ingredients. Please show them to the camera."
```

### Errors
```
"There is nothing to repeat yet."
"I heard: [command]. You can say take picture, start camera, or ask about the next step."
```

---

## 💡 Tips untuk Pengalaman Terbaik

### 1. Speak Clearly
- Jeda setelah "Hey Chef" (0.5 detik)
- Ucapkan perintah dengan jelas
- Tidak perlu berteriak

### 2. Wait for Response
- AI akan menjawab dengan suara
- Tunggu hingga AI selesai berbicara
- Lihat visual feedback

### 3. Position Ingredients
- Letakkan bahan di area terang
- Jarak ideal: 30-50cm dari kamera
- Pastikan semua bahan terlihat

### 4. Use Natural Language
```
✅ "Hey Chef, take picture"
✅ "Hey Chef, take a photo"
✅ "Hey Chef, capture"
→ Semua akan dipahami!
```

---

## 🎯 Advanced Usage

### Chain Commands
```
1. "Hey Chef, start camera"
   [Wait for camera to start]

2. "Hey Chef, take picture"
   [Wait for analysis]

3. "Hey Chef, repeat"
   [Hear results again]
```

### Quick Capture
```
"Hey Chef, take picture"
→ Jika kamera belum aktif, akan otomatis:
   1. Start camera
   2. Wait 1 second
   3. Capture & analyze
```

---

## 🔧 Troubleshooting

### "AI tidak menjawab dengan suara"

**Check:**
1. Volume tidak mute
2. Browser support speech synthesis
3. Lihat console untuk errors

**Solution:**
```javascript
// Test di console:
speechSynthesis.speak(new SpeechSynthesisUtterance('test'))
```

---

### "Perintah tidak dieksekusi"

**Check:**
1. Wake word terdeteksi? (lihat transcript)
2. Command recognized? (lihat console)
3. Button ada? (inspect element)

**Solution:**
- Coba perintah lain
- Restart voice assistant
- Check browser console

---

### "Kamera tidak terbuka"

**Check:**
1. Permission granted?
2. Camera available?
3. Button clicked?

**Solution:**
- Grant camera permission
- Try manual button click
- Check console errors

---

## 📊 Performance

### Response Times

**Wake Word Detection:**
- Target: <100ms
- Typical: 50-150ms

**Voice Response:**
- Start: Immediate
- Duration: 2-5 seconds (depends on text length)

**Camera Control:**
- Start camera: 500-1000ms
- Take picture: Immediate
- Stop camera: Immediate

---

## 🎓 Best Practices

### 1. One Command at a Time
```
❌ "Hey Chef, start camera and take picture"
✅ "Hey Chef, start camera"
   [Wait]
   "Hey Chef, take picture"
```

### 2. Wait for Completion
```
Say command → Wait for voice response → Next command
```

### 3. Use Visual Feedback
```
Watch for:
- 🔊 AI is speaking
- 📸 Taking picture
- Status updates
```

### 4. Check Transcript
```
Verify command was heard correctly:
🎤 Live: [your speech]
📝 History: [Command] [recognized text]
```

---

## 🆕 Custom Commands (Coming Soon)

Want to add your own commands? Edit `handleVoiceCommand` in `app/page.tsx`:

```typescript
else if (lowerCommand.includes('your command')) {
  speakResponse('Your response');
  setResult('Your action');
  // Your code here
}
```

---

## ✅ Quick Reference

**Most Used Commands:**
```
"Hey Chef, start camera"    → Open camera
"Hey Chef, take picture"    → Capture & analyze
"Hey Chef, next step"       → Get guidance
"Hey Chef, repeat"          → Hear again
"Hey Chef, help"            → Show all commands
```

**Remember:**
1. Say "Hey Chef" first
2. Wait for AI response
3. Watch visual feedback
4. Enjoy hands-free cooking! 🍳

---

## 🎉 You're Ready!

Sistem voice control dengan AI response sudah siap digunakan!

**Test sekarang:**
```bash
npm run dev
```

Lalu coba:
```
"Hey Chef, help"
```

Dan lihat AI menjawab dengan suara! 🔊✨
