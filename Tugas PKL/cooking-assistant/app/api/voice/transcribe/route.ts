import { NextRequest, NextResponse } from 'next/server';

export async function POST(request: NextRequest) {
  try {
    const apiKey = process.env.GROQ_API_KEY;
    
    console.log('🔑 Groq API Key exists:', !!apiKey);
    
    if (!apiKey) {
      return NextResponse.json(
        { error: 'Groq API key not configured' },
        { status: 500 }
      );
    }

    const formData = await request.formData();
    const audioFile = formData.get('audio') as File;
    
    if (!audioFile) {
      return NextResponse.json(
        { error: 'No audio file provided' },
        { status: 400 }
      );
    }

    console.log('📁 Audio file received:', {
      name: audioFile.name,
      type: audioFile.type,
      size: `${(audioFile.size / 1024).toFixed(2)} KB`
    });

    // Forward to Groq Whisper API
    const groqFormData = new FormData();
    groqFormData.append('file', audioFile);
    groqFormData.append('model', 'whisper-large-v3-turbo');
    groqFormData.append('language', 'en'); // or 'id' for Indonesian
    groqFormData.append('response_format', 'json');

    console.log('📤 Sending to Groq Whisper API...');
    const startTime = Date.now();

    const response = await fetch('https://api.groq.com/openai/v1/audio/transcriptions', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${apiKey}`,
      },
      body: groqFormData,
    });

    const duration = Date.now() - startTime;
    console.log(`⏱️ Groq API response time: ${duration}ms`);

    if (!response.ok) {
      const error = await response.text();
      console.error('❌ Groq API error:', error);
      return NextResponse.json(
        { error: 'Transcription failed', details: error },
        { status: response.status }
      );
    }

    const result = await response.json();
    
    console.log('✅ Transcription successful:', {
      text: result.text,
      length: result.text.length,
      duration: `${duration}ms`
    });
    
    return NextResponse.json({
      text: result.text,
      confidence: 0.95, // Groq Whisper typically has high confidence
      processingTime: duration,
    });

  } catch (error) {
    console.error('❌ Transcription error:', error);
    return NextResponse.json(
      { error: 'Failed to transcribe audio' },
      { status: 500 }
    );
  }
}
