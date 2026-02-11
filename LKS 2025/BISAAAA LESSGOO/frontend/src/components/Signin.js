import React, { useState } from "react";
import _default from "react-bootstrap/esm/Accordion";
import { Link, useNavigate } from "react-router-dom";

const Signin = () => {
  const [name, setName] = useState("");
  const [pass, setPass] = useState("");
  const navigate = useNavigate();
  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log(name);

    const response = await fetch("http://localhost:8000/api/v1/auth/signin", {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username: name,
        password: pass,
      }),
    });

    const data = await response.json();

    console.log(data);

    const role = data.role;

    console.log(data.role);

    switch (role) {
      case "admin":
        localStorage.setItem("admintoken", data.token);
        navigate("/admin");
        break;
      case "player":
      case "dev":
        localStorage.setItem("token", data.token);
        navigate("/");
        break;
    }
  };

  return (
    <main>
      <section class="login">
        <div class="container">
          <div class="row justify-content-center">
            <div class="col-lg-5 col-md-6">
              <h1 class="text-center mb-4">Gaming Portal</h1>
              <div class="card card-default">
                <div class="card-body">
                  <h3 class="mb-3">Sign In</h3>

                  <form
                    onSubmit={handleSubmit}
                    action="Administrator Portal/index.html"
                  >
                    <div class="form-group my-3">
                      <label for="username" class="mb-1 text-muted">
                        Username
                      </label>
                      <input
                        type="text"
                        id="username"
                        name="username"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        class="form-control"
                        autofocus
                      />
                    </div>

                    <div class="form-group my-3">
                      <label for="password" class="mb-1 text-muted">
                        Password
                      </label>
                      <input
                        value={pass}
                        onChange={(e) => setPass(e.target.value)}
                        type="password"
                        id="password"
                        name="password"
                        class="form-control"
                      />
                    </div>

                    <div class="mt-4 row">
                      <div class="col">
                        <button type="submit" class="btn btn-primary w-100">
                          Sign In
                        </button>
                      </div>
                      <div class="col">
                        <Link to="/signup" class="btn btn-danger w-100">
                          Sign up
                        </Link>
                      </div>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  );
};

export default Signin;
