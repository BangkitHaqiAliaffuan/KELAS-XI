import React from "react";

const Login = () => {
  return (
    <div>
      <div className="judul">Login</div>
      <div className="form">
        <form>
          <div className="input">
            <div>
              <label>Email</label>
              <input type="email"></input>
            </div>
            <div>
              <label>Password</label>
              <input type="password"></input>
            </div>
            <input type="submit"></input>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login;
