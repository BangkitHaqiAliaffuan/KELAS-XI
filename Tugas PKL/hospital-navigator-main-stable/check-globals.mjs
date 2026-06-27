import fs from 'fs';

const code = fs.readFileSync('src/data/hospitalRouteGraph.ts', 'utf8');
const lines = code.split('\n');
const globalVars = [];

let braces = 0;
for (let i = 0; i < lines.length; i++) {
  const line = lines[i];
  if (line.match(/^(export\s+)?(const|let|var)\s/)) {
      if (braces === 0) {
          globalVars.push(i + 1 + ": " + line);
      }
  }
  
  if (line.includes('{')) braces++;
  if (line.includes('}')) braces--;
}

console.log("Globals containing map/set or mutable-looking structures:");
globalVars.forEach(v => {
    if (v.includes('new Set') || v.includes('new Map') || v.includes('={}')) {
        console.log(v);
    }
});
