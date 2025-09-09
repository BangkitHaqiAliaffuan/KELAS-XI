import React, { useEffect, useState } from "react";
import { assets } from "./Assets/assets";
import './App.css'
const App = () => {

  const [status, setStatus] = useState("Sign Up")
  
  return (
    <form className="main-container">
      <div className="title">
        {status}
        <span></span>
      </div>

      <div className="input-container">
        {status === "Sign Up"? 
        <div className="input-wrapper">
          <img src={assets.person} />
          <input type="text" placeholder="Name" />
        </div>:""
      }
        
        <div className="input-wrapper">
          <img src={assets.email} />
          <input type="email" required placeholder="Email" />
        </div>
        <div className="input-wrapper">
          <img src={assets.password} />
          <input type="password" placeholder="Password" />
        </div>
      </div>

      <div className="lost-password">
        <p>
          Lost Password? <span>Click Here!</span>
        </p>
      </div>

      <div className="btn-container">
        <div className={`btn ${status === "Sign Up" ? "active" : ""}`} onClick={()=>{setStatus("Sign Up")}}>Sign Up</div>
        <div className={`btn ${status === "Login" ? "active" : ""}`} onClick={()=>{setStatus("Login")}}>Login</div>
      </div>
    </form>
  );
};

export default App;
