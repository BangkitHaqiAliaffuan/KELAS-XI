import { API_KEY } from "../../data";

// export async function listGeminiModels() {
//   const apiKey = API_KEY;
//   const url = `https://generativelanguage.googleapis.com/v1beta/models/${MODEL_ID}:${GENERATE_CONTENT_API}?key=${GEMINI_API_KEY}`;
//   const res = await fetch(url);
//   const data = await res.json();
//   console.log(data);
//   return data;
// }

export async function fetchGemini(prompt) {
    const MODEL_ID = "gemini-2.5-flash"
    const GENERATE_CONTENT_API = "streamGenerateContent"
    const apiKey = API_KEY;
  const url = `https://generativelanguage.googleapis.com/v1beta/models/${MODEL_ID}:${GENERATE_CONTENT_API}?key=${API_KEY}`;

  const body = {
    contents: [{ parts: [{ text: prompt }] }],
  };

  const res = await fetch(url, {
    method:'POST',
    headers: {'Content-type': 'application/json'},
    body: JSON.stringify(body)
  })

  const data = await res.json()
  console.log(data[0].candidates?.[0]?.content?.parts?.[0]?.text)
  return data[0].candidates?.[0]?.content?.parts?.[0]?.text
}
