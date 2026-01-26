import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const [idCard, setIdCard] = useState("");
  const [password, setPass] = useState("");

  const usenavigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    console.log(idCard);
    try {
      const response = await fetch("http://127.0.0.1:8000/api/v1/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        body: JSON.stringify({
          idcard: idCard,
          password: password,
        }),
      });
      const data = await response.json();
      console.log(data.token)
      localStorage.setItem("token",data.token)
      localStorage.setItem("user",data.user)

      usenavigate("/dashboard")
    } catch (err) {}
  };

  return (
    <>
      <nav class="navbar navbar-expand-md navbar-dark fixed-top bg-primary">
        <div class="container">
          <a class="navbar-brand" href="#">
            Job Seekers Platform
          </a>
          <button
            class="navbar-toggler"
            type="button"
            data-toggle="collapse"
            data-target="#navbarsExampleDefault"
            aria-controls="navbarsExampleDefault"
            aria-expanded="false"
            aria-label="Toggle navigation"
          >
            <span class="navbar-toggler-icon"></span>
          </button>

          <div class="collapse navbar-collapse" id="navbarsExampleDefault">
            <ul class="navbar-nav ml-auto">
              <li class="nav-item">
                <a class="nav-link" href="#">
                  Login
                </a>
              </li>
            </ul>
          </div>
        </div>
      </nav>

      <main className="mt-5">
        <header class="jumbotron">
          <div class="container text-center">
            <h1 class="display-4">Job Seekers Platform</h1>
          </div>
        </header>

        <div class="container">
          <div class="row justify-content-center">
            <div class="col-md-6">
              <form class="card card-default" onSubmit={handleSubmit}>
                <div class="card-header">
                  <h4 class="mb-0">Login</h4>
                </div>
                <div class="card-body">
                  <div class="form-group row align-items-center">
                    <div class="col-4 text-right">ID Card Number</div>
                    <div class="col-8">
                      <input
                        type="text"
                        value={idCard}
                        onChange={(e) => setIdCard(e.target.value)}
                        class="form-control"
                      />
                    </div>
                  </div>
                  <div class="form-group row align-items-center">
                    <div class="col-4 text-right">Password</div>
                    <div class="col-8">
                      <input
                        type="password"
                        value={password}
                        onChange={(e) => setPass(e.target.value)}
                        class="form-control"
                      />
                    </div>
                  </div>
                  <div class="form-group row align-items-center mt-4">
                    <div class="col-4"></div>
                    <div class="col-8">
                      <button type="submit" class="btn btn-primary">
                        Login
                      </button>
                    </div>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
      </main>

      <footer>
        <div class="container">
          <div class="text-center py-4 text-muted">
            Copyright &copy; 2023 - Web Tech ID
          </div>
        </div>
      </footer>
    </>
  );
};

export default Login;
