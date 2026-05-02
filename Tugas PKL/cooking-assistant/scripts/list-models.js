/**
 * List Groq models available to the configured API key.
 * Run: node scripts/list-models.js
 */

/* eslint-disable @typescript-eslint/no-require-imports */

const fs = require('fs');
const path = require('path');
const Groq = require('groq-sdk');

function loadEnvLocal() {
  const envPath = path.join(__dirname, '..', '.env.local');

  if (!fs.existsSync(envPath)) {
    return;
  }

  const envContent = fs.readFileSync(envPath, 'utf8');
  for (const line of envContent.split(/\r?\n/)) {
    const match = line.match(/^\s*([A-Z0-9_]+)\s*=\s*(.+?)\s*$/);
    if (match && !process.env[match[1]]) {
      process.env[match[1]] = match[2].replace(/^["']|["']$/g, '');
    }
  }
}

async function listModels() {
  loadEnvLocal();

  const apiKey = process.env.GROQ_API_KEY;

  if (!apiKey) {
    console.error('Error: GROQ_API_KEY not found.');
    console.log('Add GROQ_API_KEY=your_groq_api_key_here to .env.local, then try again.');
    process.exit(1);
  }

  console.log('Fetching available Groq models...\n');

  try {
    const groq = new Groq({ apiKey });
    const models = await groq.models.list();

    for (const model of models.data) {
      console.log(`- ${model.id}`);
      console.log(`  Owner: ${model.owned_by}`);
    }

    const recommended = models.data.find((model) =>
      model.id === 'llava-v1.5-7b-4096-preview'
    );

    if (recommended) {
      console.log(`\nConfigured vision model is available: ${recommended.id}`);
    } else {
      console.log('\nConfigured vision model was not returned by the models endpoint.');
      console.log('Check https://console.groq.com/docs/models for currently supported vision models.');
    }
  } catch (error) {
    console.error('Error fetching Groq models:', error.message);
    console.log('Check or create your key at https://console.groq.com/keys');
    process.exit(1);
  }
}

listModels();
