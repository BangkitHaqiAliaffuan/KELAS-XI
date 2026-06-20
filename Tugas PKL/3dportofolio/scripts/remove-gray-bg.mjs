import sharp from 'sharp';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const inputPath = path.resolve(__dirname, '../src/assets/spaceship-window.png');
const outputPath = path.resolve(__dirname, '../src/assets/spaceship-window-processed.png');

const TARGET = { r: 130, g: 130, b: 129 };
const TOLERANCE = 20;

const image = sharp(inputPath).ensureAlpha();
const { data, info } = await image.raw().toBuffer({ resolveWithObject: true });

const pixels = Buffer.alloc(data.length);

let removed = 0;
let partial = 0;

for (let i = 0; i < data.length; i += 4) {
  const r = data[i];
  const g = data[i + 1];
  const b = data[i + 2];
  const origA = data[i + 3];

  const dist = Math.sqrt(
    (r - TARGET.r) ** 2 +
    (g - TARGET.g) ** 2 +
    (b - TARGET.b) ** 2
  );
  const maxDist = TOLERANCE * Math.sqrt(3);
  const factor = Math.max(0, Math.min(1, dist / maxDist));

  pixels[i] = r;
  pixels[i + 1] = g;
  pixels[i + 2] = b;
  pixels[i + 3] = Math.round(origA * factor);

  if (factor === 0) removed++;
  else if (factor < 1) partial++;
}

await sharp(pixels, {
  raw: { width: info.width, height: info.height, channels: 4 },
}).png().toFile(outputPath);

const total = data.length / 4;
console.log(`✅  ${inputPath}`);
console.log(`   → ${outputPath}`);
console.log(`   fully removed: ${removed.toLocaleString()} px (${(removed/total*100).toFixed(1)}%)`);
console.log(`   partially blended: ${partial.toLocaleString()} px (${(partial/total*100).toFixed(1)}%)`);
