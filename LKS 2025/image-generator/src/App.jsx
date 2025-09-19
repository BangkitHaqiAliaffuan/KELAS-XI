import React, { useState } from "react";
import "./App.css";
import defaultImg from "./assets/default_image.svg";
import { GoogleGenAI } from "@google/genai";
const App = () => {
  const [inputText, setInputText] = useState("");
  const [generatedImage, setGeneratedImage] = useState(defaultImg);
  const [isLoading, setIsLoading] = useState(false);

  return (
    <div className="container">
      <div className="title">
        AI Image <span>Generator</span>
      </div>

      <div className="image-result">
        <img src={defaultImg} alt="" />
      </div>

      <div className="input-wrapper">
        <input
          value={inputText}
          onChange={(e) => {
            setInputText(e.target.value);
          }}
          type="text"
          placeholder="Describe What You Want To See"
        />
        <div className="btn">Generate</div>
      </div>
    </div>
  );
};

export default App;
