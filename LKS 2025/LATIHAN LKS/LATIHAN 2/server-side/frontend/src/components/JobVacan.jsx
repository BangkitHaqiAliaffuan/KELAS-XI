import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
const JobVacan = () => {
  const { id } = useParams();
  const [jobs, setJobs] = useState([]);
  const token = localStorage.getItem("token");
  const fetchData = async () => {
    const response = await fetch(
      `http://127.0.0.1:8000/api/v1/job_vacancies/${id}"`,
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `bearer ${token}`,
          Accept: "application/json",
        },
      },
    );
    const data = await response.json();
    setJobs(data.data);
    console.log(data.data);
  };
  useEffect(() => {
    fetchData();
  }, [token]);

  return (
    <>
      <nav class="navbar navbar-expand-md navbar-dark fixed-top bg-primary">
        <div class="container">
          <a class="navbar-brand" href="#">
            Job Seeker Platform
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
                  Marsito Kusmawati
                </a>
              </li>
              <li class="nav-item">
                <a class="nav-link" href="#">
                  Login
                </a>
              </li>
            </ul>
          </div>
        </div>
      </nav>

      <main>
        <header class="jumbotron">
          <div class="container">
            <h1 class="display-4">Job Vacancies</h1>
          </div>
        </header>

        <div class="container mb-5">
          <div class="section-header mb-4">
            <h4 class="section-title text-muted font-weight-normal">
              List of Job Vacancies
            </h4>
          </div>

          <div class="section-body">
            <article class="spot">
              {jobs.length === 0 ? (
                <>tidak ada data</>
              ) : (
                jobs.map((item,i) => (
                  <div key={i} class="row">
                    <div class="col-5">
                      <h5 class="text-primary">{item.company}</h5>
                      <span class="text-muted">
                        {item.address}
                      </span>
                    </div>
                    <div class="col-4">
                      <h5>Available Position (Capacity)</h5>
                      <span class="text-muted">
                        {Array.isArray(item.positions) && item.positions.length ? item.positions.map((p)=> p.position).join(", "):"-"}
                      </span>
                    </div>
                    <div class="col-3">
                      <Link to={`/jobdetail/:id`} class="btn btn-danger btn-lg btn-block">
                        Detail / Apply
                      </Link>
                    </div>
                  </div>
                ))
              )}
            </article>
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

export default JobVacan;
