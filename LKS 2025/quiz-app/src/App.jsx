import React from "react";
import "./App.css";

const App = () => {
  return (
    <div className="container">
      <div className="title">
        <h1>Quiz App</h1>
        <div className="break-line"></div>
      </div>

      <div className="question">
        1. What is the best framework from javascript
      </div>

      <div className="answers">
        <div className="option">
          <div> tes</div>
        </div>
        <div className="option">
          <div> tes</div>
        </div>
        <div className="option">
          <div> tes</div>
        </div>
        <div className="option">
          <div> tes</div>
        </div>
      </div>

      <div className="info-wrapper">
        <div className="btn">Next</div>
        <div className="status">3 of 5 questions</div>
      </div>
    </div>
  );
};

export default App;
