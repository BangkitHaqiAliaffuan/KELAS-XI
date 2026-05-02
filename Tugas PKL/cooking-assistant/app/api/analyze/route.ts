import Groq from 'groq-sdk';
import { NextRequest, NextResponse } from 'next/server';

const DEFAULT_GROQ_MODEL = 'llava-v1.5-7b-4096-preview';

function getStatusCode(error: unknown) {
  if (typeof error === 'object' && error !== null && 'status' in error) {
    const status = (error as { status?: unknown }).status;
    return typeof status === 'number' ? status : undefined;
  }

  return undefined;
}

export async function POST(request: NextRequest) {
  try {
    const apiKey = process.env.GROQ_API_KEY;
    const modelName = process.env.GROQ_MODEL || DEFAULT_GROQ_MODEL;

    if (!apiKey) {
      console.error('Groq API key not found in environment variables');
      return NextResponse.json(
        { error: 'Groq API key not configured. Please check your .env.local file.' },
        { status: 500 }
      );
    }

    const { image } = await request.json();

    if (!image || typeof image !== 'string') {
      return NextResponse.json(
        { error: 'No image provided' },
        { status: 400 }
      );
    }

    const groq = new Groq({
      apiKey,
      maxRetries: 1,
      timeout: 20_000,
    });

    const completion = await groq.chat.completions.create({
      model: modelName,
      temperature: 0.2,
      max_tokens: 300,
      messages: [
        {
          role: 'user',
          content: [
            {
              type: 'text',
              text: `You are a helpful cooking assistant. Analyze this image and:
1. Identify all visible ingredients
2. Suggest ONE simple next step the user can take with these ingredients
3. Keep your response concise and actionable

Format your response as:
Ingredients: [list ingredients]
Next Step: [one clear action]`,
            },
            {
              type: 'image_url',
              image_url: {
                url: image,
              },
            },
          ],
        },
      ],
    });

    const text = completion.choices[0]?.message?.content;

    if (!text) {
      return NextResponse.json(
        { error: 'Groq returned an empty analysis result.' },
        { status: 502 }
      );
    }

    return NextResponse.json({ result: text });
  } catch (error) {
    console.error('Error analyzing image with Groq:', error);

    const errorMessage = error instanceof Error ? error.message : 'Unknown error';
    const statusCode = getStatusCode(error);

    return NextResponse.json(
      {
        error: 'Failed to analyze image with Groq',
        details: errorMessage,
      },
      { status: statusCode && statusCode >= 400 ? statusCode : 500 }
    );
  }
}
