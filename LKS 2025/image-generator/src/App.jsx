import React, { useState, useCallback } from "react";
import "./App.css";
import defaultImg from "./assets/default_image.svg";
import { GoogleGenAI } from "@google/genai";
import { API_KEY } from "../data";

const App = () => {
  const [inputText, setInputText] = useState("");
  const [generatedImage, setGeneratedImage] = useState(defaultImg);
  const [isLoading, setIsLoading] = useState(false);

  const generateImage = useCallback(async () => {
    // Prevent multiple calls
    if (isLoading) {
      console.log("Already generating, ignoring request");
      return;
    }

    if (!inputText.trim()) {
      alert("Please Enter Prompt");
      return;
    }

    console.log("Starting image generation...");
    setIsLoading(true);

    try {
      const ai = new GoogleGenAI({
        apiKey: API_KEY,
      });

      const config = {
        responseModalities: ["IMAGE", "TEXT"],
      };

      const model = "gemini-2.5-flash-image-preview";
      const contents = [
        {
          role: "user",
          parts: [
            {
              text: inputText,
            },
          ],
        },
      ];

      const response = await ai.models.generateContentStream({
        model,
        config,
        contents,
      });

      let imageFound = false;
      let chunkCount = 0;
      const maxChunks = 50;

      for await (const chunk of response) {
        chunkCount++;

        if (chunkCount > maxChunks) {
          console.warn("Maximum chunks reached, breaking loop");
          break;
        }

        if (!chunk.candidates || !chunk.candidates[0]?.content?.parts) {
          continue;
        }

        if (chunk.candidates[0].content.parts[0]?.inlineData) {
          const inlineData = chunk.candidates[0].content.parts[0].inlineData;
          const base64Data = inlineData.data;
          const mimeType = inlineData.mimeType;

          if (base64Data && mimeType) {
            const dataUrl = `data:${mimeType};base64,${base64Data}`;
            setGeneratedImage(dataUrl);
            imageFound = true;
            break;
          }
        }
      }

      if (!imageFound) {
        alert("No image was generated. Please try again with a different prompt.");
      }

    } catch (error) {
      console.error("Error generating image:", error);
      
      if (error.message && error.message.includes("Rate limit")) {
        alert("Too many requests. Please wait a few minutes before trying again.");
      } else {
        alert("Failed to generate image. Please check your API key and try again.");
      }
    } finally {
      setIsLoading(false);
    }
  }, [isLoading, inputText]); // Dependencies for useCallback

  return (
    <div className="container">
      <div className="title">
        AI Image <span>Generator</span>
      </div>

      <div className="image-result">
        <img src={generatedImage} alt="" />
        {isLoading && <div className="loading">Generating image...</div>}
      </div>

      <div className="input-wrapper">
        <input
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          type="text"
          placeholder={isLoading ? "Generating..." : "Describe What You Want"}
          disabled={isLoading}
        />
        <div
          onClick={!isLoading ? generateImage : undefined}
          className={`btn ${isLoading ? "disabled" : ""}`}
          style={{
            opacity: isLoading ? 0.6 : 1,
            cursor: isLoading ? "not-allowed" : "pointer",
            pointerEvents: isLoading ? "none" : "auto"
          }}
        >
          {isLoading ? "Generating..." : "Generate"}
        </div>
      </div>
    </div>
  );
};

export default App;