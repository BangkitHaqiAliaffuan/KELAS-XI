import React from "react";
import { assets } from "./Assets/assets";

const App = () => {
  return (
    <form className="main-container">
      <div className="title">
        SignUp
        <span></span>
      </div>

      <div className="input-container">
        <div className="input-wrapper">
          <img src={assets.person} />
          <input type="text" placeholder="Name" />
        </div>
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
        <div className="btn active">Sign Up</div>
        <div className="btn ">Login</div>
      </div>
    </form>
  );
};

export default App;
