import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

const API_BASE = process.env.REACT_APP_API_URL || "http://127.0.0.1:8000";

const JobDetail = () => {
    const { id } = useParams();
    const [job, setJob] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [selectedPositions, setSelectedPositions] = useState([]);
    const [notes, setNotes] = useState("");
    const [applyResult, setApplyResult] = useState(null);

    useEffect(() => {
        const fetchDetail = async () => {
            setLoading(true);
            setError(null);
            try {
                const token = localStorage.getItem("token");
                const res = await fetch(`${API_BASE}/api/v1/jobvacan/detail/${id}`, {
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
                setJob(data.data || null);
            } catch (err) {
                console.error("fetch job detail error", err);
                setError(err.message || "Fetch error");
            } finally {
                setLoading(false);
            }
        };

        if (id) fetchDetail();
    }, [id]);

    const togglePosition = (posName) => {
        setSelectedPositions((prev) => {
            if (prev.includes(posName)) return prev.filter((p) => p !== posName);
            return [...prev, posName];
        });
    };

    const handleApply = async (e) => {
        e.preventDefault();
        setApplyResult(null);
        try {
            const token = localStorage.getItem("token");
            const res = await fetch(`${API_BASE}/api/v1/jobvacan/create`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Accept: "application/json",
                    Authorization: token ? "Bearer " + token : "",
                },
                body: JSON.stringify({
                    vacancy_id: id,
                    positions: selectedPositions,
                    note: notes,
                }),
            });
            const data = await res.json();
            if (!res.ok) {
                throw new Error(data.message || JSON.stringify(data));
            }
            setApplyResult({ success: true, message: data.message || "Applied" });
        } catch (err) {
            console.error("apply error", err);
            setApplyResult({ success: false, message: err.message || "Error" });
        }
    };

    return (
        <>
            <nav className="navbar navbar-expand-md navbar-dark fixed-top bg-primary">
                <div className="container">
                    <a className="navbar-brand" href="#">
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
                        <span className="navbar-toggler-icon"></span>
                    </button>

                    <div className="collapse navbar-collapse" id="navbarsExampleDefault">
                        <ul className="navbar-nav ml-auto">
                            <li className="nav-item">
                                <a className="nav-link" href="#">
                                    {localStorage.getItem("token") ? "Profile" : "Login"}
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>

            <main>
                <header className="jumbotron">
                    <div className="container text-center">
                        <div>
                            <h1 className="display-4">{job?.name || job?.title || `Job #${id}`}</h1>
                            <span className="text-muted">{job?.address || job?.location || "-"}</span>
                        </div>
                    </div>
                </header>

                <div className="container">
                    {loading && <div>Loading...</div>}
                    {error && <div className="text-danger">{error}</div>}

                    {job && (
                        <form onSubmit={handleApply}>
                            <div className="row mb-3">
                                <div className="col-md-12">
                                    <div className="form-group">
                                        <h3>Description</h3>
                                        <div>{job.description || job.desc || "-"}</div>
                                    </div>
                                </div>
                            </div>

                            <div className="row mb-3">
                                <div className="col-md-12">
                                    <div className="form-group">
                                        <h3>Select position</h3>
                                        <table className="table table-bordered table-hover table-striped">
                                            <thead>
                                                <tr>
                                                    <th width="1">#</th>
                                                    <th>Position</th>
                                                    <th>Capacity</th>
                                                    <th>Application / Max</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {(job.positions || []).map((p, idx) => {
                                                    const name = p.position || p.name || p.title || `pos-${idx}`;
                                                    const cap = p.capacity ?? p.available ?? p.qty ?? "-";
                                                    const applications = p.applications ?? "-";
                                                    const disabled = p.disabled ?? false;
                                                    return (
                                                        <tr key={idx} className={disabled ? "table-warning" : ""}>
                                                            <td>
                                                                <input
                                                                    type="checkbox"
                                                                    checked={selectedPositions.includes(name)}
                                                                    disabled={disabled}
                                                                    onChange={() => togglePosition(name)}
                                                                />
                                                            </td>
                                                            <td>{name}</td>
                                                            <td>{cap}</td>
                                                            <td>{applications}</td>
                                                        </tr>
                                                    );
                                                })}
                                            </tbody>
                                        </table>
                                        <div className="text-right">
                                            <button type="submit" className="btn btn-primary btn-lg">
                                                Apply for this job
                                            </button>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-md-12">
                                    <div className="form-group">
                                        <div className="d-flex align-items-center mb-3">
                                            <label className="mr-3 mb-0">Notes for Company</label>
                                        </div>
                                        <textarea
                                            value={notes}
                                            onChange={(e) => setNotes(e.target.value)}
                                            className="form-control"
                                            cols="30"
                                            rows="6"
                                            placeholder="Explain why you should be accepted"
                                        ></textarea>
                                    </div>
                                </div>
                            </div>

                            {applyResult && (
                                <div className={`alert ${applyResult.success ? "alert-success" : "alert-danger"}`}>
                                    {applyResult.message}
                                </div>
                            )}
                        </form>
                    )}
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

export default JobDetail;