import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
// useState
const AddUser = () => {
  const [name, setName] = useState("");
  const [pass, setPass] = useState("");
  const token = localStorage.getItem("admintoken");
  const navigate = useNavigate()
  const handleSubmit = async (e) => {
    e.preventDefault()
    const response = await fetch("http://localhost:8000/api/v1/users", {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        username: name,
        password: pass,
      }),
    });
    navigate('/list-user')
  };
  return (
    <>
      <main>
        <div class="hero py-5 bg-light">
          <div class="container text-center">
            <h2 class="mb-3">Manage User - Administrator Portal</h2>
            <div class="text-muted">
              Lorem ipsum, dolor sit amet consectetur adipisicing elit.
            </div>
          </div>
        </div>

        <div class="py-5">
          <div class="container">
            <div class="row justify-content-center ">
              <div class="col-lg-5 col-md-6">
                <form onSubmit={handleSubmit}>
                  <div class="form-item card card-default my-4">
                    <div class="card-body">
                      <div class="form-group">
                        <label for="username" class="mb-1 text-muted">
                          Username <span class="text-danger">*</span>
                        </label>
                        <input
                          value={name}
                          onChange={(e) => setName(e.target.value)}
                          id="username"
                          type="text"
                          placeholder="Username"
                          class="form-control"
                          name="username"
                        />
                      </div>
                    </div>
                  </div>
                  <div class="form-item card card-default my-4">
                    <div class="card-body">
                      <div class="form-group">
                        <label for="password" class="mb-1 text-muted">
                          Password <span class="text-danger">*</span>
                        </label>
                        <input
                          value={pass}
                          onChange={(e) => setPass(e.target.value)}
                          id="password"
                          type="password"
                          placeholder="Password"
                          class="form-control"
                          name="userpasswordname"
                        />
                      </div>
                    </div>
                  </div>

                  <div class="mt-4 row">
                    <div class="col">
                      <button class="btn btn-primary w-100">Submit</button>
                    </div>
                    <div class="col">
                      <a href="users.html" class="btn btn-danger w-100">
                        Back
                      </a>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </main>
    </>
  );
};

export default AddUser;
