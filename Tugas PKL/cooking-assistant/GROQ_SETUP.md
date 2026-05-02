# Groq Vision Setup

This app uses Groq as the AI backend for low-latency image analysis.

## Model

Default model:

```env
GROQ_MODEL=llava-v1.5-7b-4096-preview
```

The camera sends a base64 image data URL to `app/api/analyze/route.ts`. The route sends that image to Groq Chat Completions as an `image_url` content part.

## Get a Groq API Key

1. Open https://console.groq.com/keys
2. Sign in or create a GroqCloud account.
3. Click **Create API Key**.
4. Copy the key.
5. Add it to `.env.local`:

```env
GROQ_API_KEY=your_groq_api_key_here
GROQ_MODEL=llava-v1.5-7b-4096-preview
```

Only team owners or users with the developer role can create/manage API keys.

Restart the dev server after editing `.env.local`.

## Install the Groq SDK

The official Groq TypeScript/JavaScript SDK package is named `groq-sdk`.

```bash
npm install groq-sdk
```

Import it like this:

```ts
import Groq from 'groq-sdk';
```

Note: `@groq/groq-sdk` is not the official package name shown in Groq's SDK repository and npm docs.

## Run

```bash
npm run dev
```

Then open the local Next.js URL and capture an ingredient image.
