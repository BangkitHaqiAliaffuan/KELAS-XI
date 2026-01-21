import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
const Login = () => {
  const [idnumber, setIdnumber] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log('handleSubmit called', { idnumber });

    try {
      const response = await fetch("http://127.0.0.1:8000/api/v1/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        body: JSON.stringify({
          idnumber: idnumber,
          password: password,
        }),
      });

      if (!response.ok) {
        const error = await response.json();
        alert(error.error || "Login gagal");
        return;
      }

      //   console.log(idnumber)

      const data = await response.json();
      console.log('login response', data);
      // backend returns `token` and `society`
      const token = data.token ?? (data.test && data.test.login_tokens);
      if (!token) {
        console.error('Token not found in response', data);
        alert('Token tidak ditemukan dalam response');
        return;
      }

      localStorage.setItem("token", token);
      navigate("/dashboard");
      console.log('stored token', token);
    } catch (error) {
      console.error('login error', error);
      alert('Terjadi kesalahan: ' + (error.message || error));
    }
  };

  return (
    <>
      <nav className="navbar navbar-expand-md navbar-dark fixed-top bg-primary">
        <div class="container">
          <a class="navbar-brand" href="#">
            Job Seekers Platform
          </a>
          <button
            className="navbar-toggler"
            type="button"
            data-toggle="collapse"
            data-target="#navbarsExampleDefault"
            aria-controls="navbarsExampleDefault"
            aria-expanded="false"
            aria-label="Toggle navigation"
          >
            <span class="navbar-toggler-icon"></span>
          </button>

          <div className="collapse navbar-collapse" id="navbarsExampleDefault">
            <ul className="navbar-nav ml-auto">
              <li className="nav-item">
                <a className="nav-link" href="#">
                  Login
                </a>
              </li>
            </ul>
          </div>
        </div>
      </nav>

      <main>
        <header className="jumbotron">
          <div className="container text-center m-5">
            <h1 className="display-4">Job Seekers Platform</h1>
          </div>
        </header>

        <div class="container">
          <div class="row justify-content-center">
            <div class="col-md-6">
              <form className="card card-default" onSubmit={handleSubmit}>
                <div className="card-header">
                  <h4 className="mb-0">Login</h4>
                </div>
                <div className="card-body">
                  <div className="form-group row align-items-center">
                    <div className="col-4 text-right">ID Card Number</div>
                    <div className="col-8">
                      <input
                        type="text"
                        className="form-control"
                        value={idnumber}
                        onChange={(e) => setIdnumber(e.target.value)}
                      />
                    </div>
                  </div>
                  <div className="form-group row align-items-center">
                    <div className="col-4 text-right">Password</div>
                    <div className="col-8">
                      <input
                        type="password"
                        className="form-control"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                      />
                    </div>
                  </div>
                  <div className="form-group row align-items-center mt-4">
                    <div className="col-4"></div>
                    <div className="col-8">
                      <button type="submit" className="btn btn-primary">Login</button>
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
