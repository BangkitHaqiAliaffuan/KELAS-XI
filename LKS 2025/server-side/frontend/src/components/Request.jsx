import React, { useState } from "react";

const Request = () => {
  const [jobCategory, setCategory] = useState(0);
  const [jobPosition, setPosition] = useState("");
  const [workExperience, setExperience] = useState("");
  const [reason, setReason] = useState("");
  const token = localStorage.getItem("token");

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log(token)
    const response = await fetch("http://127.0.0.1:8000/api/v1/validation", {
      method:"POST",
      headers: {
        "Content-type": "application/json",
        "Accept": "application/json",
        "Authorization": token? "Bearer " + token : ""
      },
      body: JSON.stringify({
        job_position: jobPosition,
        job_category_id: jobCategory,
        reason_accepted: reason,
        work_experience: workExperience,
      }),
    });

    const data = response.json()
    console.log(data)

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
                  {token ? "Logout" : "Login"}
                </a>
              </li>
            </ul>
          </div>
        </div>
      </nav>

      <main>
        <header class="jumbotron m-5">
          <div class="container">
            <h1 class="display-4">Request Data Validation</h1>
          </div>
        </header>

        <div class="container">
          <form action="" onSubmit={handleSubmit}>
            <div class="row mb-4">
              <div class="col-md-6">
                <div class="form-group">
                  <div class="d-flex align-items-center mb-3">
                    <label class=" mb-0">Job Category</label>
                    <select
                      value={jobCategory}
                      onChange={(e) => setCategory(e.target.value)}
                      class="form-control-sm m-1"
                    >
                      <option value="">Select Your Category</option>
                      <option value="1">Computing and ICT</option>
                      <option value="2">Construction and building</option>
                      <option value="3">Animals, land and environment</option>
                      <option value="4">Design, arts and crafts</option>
                      <option value="5">Education and training</option>
                    </select>
                  </div>
                  <textarea
                    value={jobPosition}
                    onChange={(e) => setPosition(e.target.value)}
                    class="form-control"
                    cols="30"
                    rows="5"
                    placeholder="Job position sparate with , (comma)"
                  ></textarea>
                </div>
              </div>

              <div class="col-md-6">
                <div class="form-group">
                  <div class="d-flex align-items-center mb-3">
                    <label class="mr-3 mb-0">Work Experiences ?</label>
                    <select class="m-1 form-control-sm">
                      <option value="yes">Yes, I have</option>
                      <option value="no">No</option>
                    </select>
                  </div>
                  <textarea
                    value={workExperience}
                    onChange={(e) => setExperience(e.target.value)}
                    class="form-control"
                    cols="30"
                    rows="5"
                    placeholder="Describe your work experiences"
                  ></textarea>
                </div>
              </div>

              <div class="col-md-12">
                <div class="form-group">
                  <div class="d-flex align-items-center mb-3">
                    <label class="mr-3 mb-0">Reason Accepted</label>
                  </div>
                  <textarea
                    value={reason}
                    onChange={(e) => setReason(e.target.value)}
                    class="form-control"
                    cols="30"
                    rows="6"
                    placeholder="Explain why you should be accepted"
                  ></textarea>
                </div>
              </div>
            </div>

            <button type="submit" class="btn btn-primary">Send Request</button>
          </form>
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

export default Request;
