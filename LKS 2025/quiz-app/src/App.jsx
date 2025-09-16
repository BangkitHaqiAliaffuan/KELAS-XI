import React from "react";
import "./App.css";

const App = () => {
  return (
    <div className="container">
      <div className="title">
        <h1>Quiz App</h1>
      </div>

      <div className="break-line"></div>

      <div className="questions">
        <div className="option"></div>
        <div className="option"></div>
        <div className="option"></div>
        <div className="option"></div>
      </div>

      <div className="btn">Next</div>

      <div className="status"></div>
    </div>
  );
};

export default App;
