import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

const API_BASE = process.env.REACT_APP_API_URL || "http://127.0.0.1:8000";

const JobVacan = () => {
    const [jobs, setJobs] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchJobs = async () => {
            setLoading(true);
            setError(null);
            try {
                const token = localStorage.getItem("token");
                const res = await fetch(`${API_BASE}/api/v1/jobvacan/`, {
                    headers: {
                        Accept: "application/json",
                        Authorization: token ? "Bearer " + token : "",
                    },
                });
                if (!res.ok) {
                    const txt = await res.text();
                    throw new Error(`Request failed: ${res.status} ${txt}`);
                }
                const data = await res.json();
                setJobs(data.data || []);
            } catch (err) {
                console.error("fetch jobs error", err);
                setError(err.message || "Fetch error");
            } finally {
                setLoading(false);
            }
        };

        fetchJobs();
    }, []);

    const renderPositions = (positions) => {
        if (!positions || !Array.isArray(positions)) return "-";
        return positions
            .map((p) => {
                const name = p.position || p.name || p.title || "position";
                const cap = p.capacity ?? p.available ?? p.qty ?? p.count ?? "";
                return cap ? `${name} (${cap})` : name;
            })
            .join(", ");
    };

    return (
        <>
            <nav className="navbar navbar-expand-md navbar-dark fixed-top bg-primary">
                <div className="container">
                    <a className="navbar-brand" href="#">
                        Job Seeker Platform
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
                        <span className="navbar-toggler-icon"></span>
                    </button>

                    <div className="collapse navbar-collapse" id="navbarsExampleDefault">
                        <ul className="navbar-nav ml-auto">
                            <li className="nav-item">
                                <a className="nav-link" href="#">
                                    {localStorage.getItem("token") ? "Profile" : "Guest"}
                                </a>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link" href="#">
                                    {localStorage.getItem("token") ? "Logout" : "Login"}
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>

            <main>
                <header className="jumbotron">
                    <div className="container">
                        <h1 className="display-4">Job Vacancies</h1>
                    </div>
                </header>

                <div className="container mb-5">
                    <div className="section-header mb-4">
                        <h4 className="section-title text-muted font-weight-normal">
                            List of Job Vacancies
                        </h4>
                    </div>

                    <div className="section-body">
                        {loading && <div>Loading...</div>}
                        {error && <div className="text-danger">{error}</div>}

                        {jobs.map((job) => (
                            <article className="spot" key={job.id}>
                                <div className="row">
                                    <div className="col-5">
                                        <h5 className="text-primary">{job.name || job.title || job.company_name || `Job #${job.id}`}</h5>
                                        <span className="text-muted">{job.address || job.location || job.city || "-"}</span>
                                    </div>
                                    <div className="col-4">
                                        <h5>Available Position (Capacity)</h5>
                                        <span className="text-muted">{renderPositions(job.positions)}</span>
                                    </div>
                                    <div className="col-3">
                                        <Link to={`/jobdetail/${job.id}`} className="btn btn-danger btn-lg btn-block">
                                            Detail / Apply
                                        </Link>
                                    </div>
                                </div>
                            </article>
                        ))}

                        {!loading && jobs.length === 0 && <div className="text-muted">No job vacancies found.</div>}
                    </div>
                </div>
            </main>

            <footer>
                <div className="container">
                    <div className="text-center py-4 text-muted">Copyright &copy; 2023 - Web Tech ID</div>
                </div>
            </footer>
        </>
    );
};

export default JobVacan;