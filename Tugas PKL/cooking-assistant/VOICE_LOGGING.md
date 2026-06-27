# 🎤 Voice Assistant Logging Guide

## 📊 Real-Time Monitoring

### Visual Logs (On Screen)

**1. Live Transcript (Green Box)**
- Shows real-time speech recognition
- Updates as you speak
- Uses Web Speech API
- Appears when listening is active

**2. Transcript History (Blue Box)**
- Shows last 5 transcriptions
- Format: `[Wake]` or `[Command]`
- Scrollable if more than 5 items
- Persists during session

**3. Status Bar**
- Current system status
- Last executed command
- Processing indicators

---

## 🖥️ Console Logs (Browser DevTools)

### How to Open Console

**Chrome/Edge:**
- Press `F12`
- Or `Ctrl+Shift+J` (Windows)
- Or `Cmd+Option+J` (Mac)

**Firefox:**
- Press `F12`
- Or `Ctrl+Shift+K` (Windows)
- Or `Cmd+Option+K` (Mac)

---

## 📝 Log Types

### Frontend Logs (Browser Console)

#### 1. Wake Word Detection
```
🎤 Heard: hey chef what's next
✅ Wake word detected!
```

#### 2. Audio Recording
```
📤 Sending audio to Groq Whisper...
```

#### 3. Transcription Result
```
📥 Groq transcription: what's the next step
🎯 Confidence: 0.95
```

#### 4. Errors
```
❌ Transcription error: [error details]
```

---

### Backend Logs (Terminal/Server)

#### 1. API Key Check
```
🔑 Groq API Key exists: true
```

#### 2. Audio File Received
```
📁 Audio file received: {
  name: 'command.webm',
  type: 'audio/webm',
  size: '45.23 KB'
}
```

#### 3. API Request
```
📤 Sending to Groq Whisper API...
```

#### 4. Response Time
```
⏱️ Groq API response time: 487ms
```

#### 5. Transcription Success
```
✅ Transcription successful: {
  text: 'what is the next step',
  length: 21,
  duration: '487ms'
}
```

#### 6. Errors
```
❌ Groq API error: [error details]
```

---

## 🔍 Debugging Workflow

### Step 1: Check Wake Word Detection

**Look for:**
```
🎤 Heard: [your speech]
```

**If not appearing:**
- Microphone not working
- Web Speech API not supported
- Permission denied

---

### Step 2: Check Wake Word Trigger

**Look for:**
```
✅ Wake word detected!
```

**If not appearing:**
- Wake phrase not in transcript
- Too fast (< 3 seconds between triggers)
- Check transcript contains "hey chef"

---

### Step 3: Check Audio Recording

**Look for:**
```
📤 Sending audio to Groq Whisper...
```

**If not appearing:**
- Recording failed to start
- Microphone permission issue
- MediaRecorder not supported

---

### Step 4: Check Server Processing

**Look for (in terminal):**
```
📁 Audio file received: {...}
📤 Sending to Groq Whisper API...
⏱️ Groq API response time: XXXms
✅ Transcription successful: {...}
```

**If errors:**
- Check Groq API key
- Check internet connection
- Check rate limits

---

### Step 5: Check Final Result

**Look for:**
```
📥 Groq transcription: [command text]
🎯 Confidence: 0.95
```

**Should also see:**
- Status update on screen
- Command in transcript history
- Action executed

---

## 🎯 Common Issues & Logs

### Issue: "Nothing happens when I speak"

**Check logs for:**
```
🎤 Heard: [empty or nothing]
```

**Solution:**
- Check microphone is working
- Check browser permissions
- Try speaking louder/closer

---

### Issue: "Wake word not detected"

**Check logs for:**
```
🎤 Heard: hey chef
```

**If you see this but no wake detection:**
- Check exact phrase (case-insensitive)
- Check 3-second cooldown
- Try saying "hey chef" more clearly

---

### Issue: "Command not processed"

**Check terminal logs for:**
```
❌ Groq API error: [details]
```

**Common causes:**
- API key invalid
- Rate limit exceeded
- Network error
- Audio format issue

---

### Issue: "Slow processing"

**Check logs for:**
```
⏱️ Groq API response time: XXXms
```

**Normal:** 300-800ms
**Slow:** >1000ms (check internet)
**Very slow:** >2000ms (network issue)

---

## 📊 Performance Monitoring

### Metrics to Watch

**1. Wake Word Latency**
- Time from speech to "✅ Wake word detected!"
- Target: <100ms
- Acceptable: <500ms

**2. Recording Duration**
- Fixed: 5 seconds
- Shown in audio file size

**3. API Response Time**
```
⏱️ Groq API response time: XXXms
```
- Target: <500ms
- Acceptable: <1000ms
- Slow: >1000ms

**4. Total Latency**
- Wake detection + Recording + Processing
- Target: <6 seconds
- Acceptable: <8 seconds

---

## 🔧 Advanced Debugging

### Enable Verbose Logging

Add to `VoiceAssistant.tsx`:
```typescript
// After recognition.onresult
console.log('Full event:', event);
console.log('Results:', event.results);
console.log('Confidence:', event.results[0][0].confidence);
```

### Check Audio Quality

Add to `startCommandRecording`:
```typescript
console.log('MediaRecorder state:', mediaRecorder.state);
console.log('Audio tracks:', stream.getAudioTracks());
console.log('Track settings:', stream.getAudioTracks()[0].getSettings());
```

### Monitor API Calls

Add to `transcribeWithGroq`:
```typescript
console.log('FormData entries:', Array.from(formData.entries()));
console.log('Blob size:', audioBlob.size);
console.log('Blob type:', audioBlob.type);
```

---

## 📈 Log Analysis Tips

### 1. Timeline Analysis

Track full flow:
```
[Time] 🎤 Heard: hey chef
[+0.05s] ✅ Wake word detected!
[+0.10s] 📤 Sending audio to Groq...
[+5.50s] ⏱️ Response time: 487ms
[+5.50s] 📥 Transcription: what's next
```

### 2. Error Pattern Detection

Look for repeated errors:
```
❌ Transcription error: Network timeout
❌ Transcription error: Network timeout
❌ Transcription error: Network timeout
```
→ Network issue, not code issue

### 3. Performance Degradation

Compare response times:
```
⏱️ Response: 450ms ✅
⏱️ Response: 480ms ✅
⏱️ Response: 1200ms ⚠️
⏱️ Response: 2500ms ❌
```
→ Network getting slower

---

## 🎓 Best Practices

### 1. Always Check Console First
- Most issues show clear error messages
- Logs show exact failure point

### 2. Compare Logs Across Layers
- Frontend log: What user said
- Backend log: What API received
- Result: What was understood

### 3. Use Timestamps
- Browser console shows timestamps
- Terminal shows timestamps
- Compare to find bottlenecks

### 4. Save Logs for Issues
- Copy console output
- Copy terminal output
- Include both when reporting bugs

---

## 🆘 Quick Troubleshooting

**No logs at all?**
→ Check console is open (F12)

**Logs stop after wake word?**
→ Check terminal for API errors

**API logs but no result?**
→ Check network tab (F12 > Network)

**Everything logs but no action?**
→ Check `handleVoiceCommand` in page.tsx

---

## ✅ Healthy Log Example

**Complete successful flow:**

```
// Browser Console
🎤 Heard: hey chef
✅ Wake word detected!
📤 Sending audio to Groq Whisper...
📥 Groq transcription: what's the next step
🎯 Confidence: 0.95

// Terminal
🔑 Groq API Key exists: true
📁 Audio file received: { name: 'command.webm', type: 'audio/webm', size: '42.15 KB' }
📤 Sending to Groq Whisper API...
⏱️ Groq API response time: 456ms
✅ Transcription successful: { text: 'what's the next step', length: 21, duration: '456ms' }
```

**This means everything is working perfectly!** ✨

---

## 📞 Need Help?

If logs show errors you don't understand:
1. Copy full console output
2. Copy full terminal output
3. Note what you said vs what was heard
4. Check TROUBLESHOOTING.md for solutions
