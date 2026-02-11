import React, { useEffect, useState } from "react";

const Dashboard = () => {
    const [validations, setValidations] = useState([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState(null)
    const [applications, setApplications] = useState([])
    const [appsLoading, setAppsLoading] = useState(false)
    const [appsError, setAppsError] = useState(null)

    const token = localStorage.getItem('token')
    const jobCategory = [
        'Computing and ICT',
        'Construction and building',
        'Animals, land and environment',
        'Design, arts and crafts',
    ]
    console.log(token)
    const getCategoryLabel = (id) => {
        const n = Number(id)
        console.log(id)
        if (!n) return '-'
        return jobCategory[n - 1] ?? '-'
    }
    useEffect(() => {
        const fetchData = async () => {
            if (!token) return
            setLoading(true)
            setError(null)
            try {
                const res = await fetch('http://127.0.0.1:8000/api/v1/validation', {
                    headers: {
                        'Authorization': 'Bearer ' + token,
                        'Accept': 'application/json',
                    }
                })
                if (!res.ok) {
                    const text = await res.text()
                    throw new Error(`Fetch failed: ${res.status} ${text}`)
                }
                const data = await res.json()
                setValidations(data.validation || [])
            } catch (err) {
                console.error('fetch validations error', err)
                setError(err.message || 'Fetch error')
            } finally {
                setLoading(false)
            }
        }

        fetchData()
    }, [token])

    useEffect(() => {
        const fetchApplications = async () => {
            if (!token) return
            setAppsLoading(true)
            setAppsError(null)
            try {
                const res = await fetch('http://127.0.0.1:8000/api/v1/jobvacan/get', {
                    headers: {
                        'Authorization': 'Bearer ' + token,
                        'Accept': 'application/json'
                    }
                })
                if (!res.ok) {
                    const txt = await res.text()
                    throw new Error(`Fetch failed: ${res.status} ${txt}`)
                }
                const data = await res.json()
                setApplications(data.data || [])
            } catch (err) {
                console.error('fetch applications error', err)
                setAppsError('Fetch error')
            } finally {
                setAppsLoading(false)
            }
        }

        fetchApplications()
    }, [token])

    console.log('appliucation', applications, { loading, error })
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
             <span class="navbar-toggler-icon"></span>
           </button>
 
                     <div className="collapse navbar-collapse" id="navbarsExampleDefault">
                         <ul className="navbar-nav ml-auto">
                             <li className="nav-item">
                                 <a className="nav-link" href="#">
                                     {token?"Logout":"Login"}
                                 </a>
                             </li>
                         </ul>
                     </div>
         </div>
       </nav>

    <header className="jumbotron">
        <div className="container">
            <h1 className="display-4">Dashboard</h1>
        </div>
    </header>

    <main>
        <div className="container">
            <section className="validation-section mb-5">
                <div className="section-header mb-3">
                    <h4 className="section-title text-muted">My Data Validation</h4>
                </div>
                <div className="row">

                    <div className="col-md-4">
                        <div className="card card-default">
                            <div className="card-header">
                                <h5 className="mb-0">Data Validation</h5>
                            </div>
                            <div className="card-body">
                                <a href="" className="btn btn-primary btn-block">+ Request validation</a>
                            </div>
                        </div>
                    </div>

                    <div className="col-md-4">
                        <div className="card card-default">
                            <div className="card-header border-0">
                                <h5 className="mb-0">Data Validation</h5>
                            </div>
                            <div className="card-body p-0">
                                <table className="table table-striped mb-0">
                                    <tbody>
                                        <tr>
                                            <th>Status</th>
                                            <td>
                                                <span className="badge badge-info">{validations[0]?.status ?? '-'}</span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Job Category</th>
                                            <td className="text-muted">
                                                { getCategoryLabel(validations[0]?.job_category_id) }
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Job Position</th>
                                            <td className="text-muted">{validations[0]?.job_position ?? '-'}</td>
                                        </tr>
                                        <tr>
                                            <th>Reason Accepted</th>
                                            <td className="text-muted">{validations[0]?.reason_accepted ?? '-'}</td>
                                        </tr>
                                        <tr>
                                            <th>Validator</th>
                                            <td className="text-muted">-</td>
                                        </tr>
                                        <tr>
                                            <th>Validator Notes</th>
                                            <td className="text-muted">-</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                    <div className="col-md-4">
                        <div className="card card-default">
                            <div className="card-header border-0">
                                <h5 className="mb-0">Data Validation</h5>
                            </div>
                            <div className="card-body p-0">
                                <table className="table table-striped mb-0">
                                    <tbody>
                                        <tr>
                                            <th>Status</th>
                                            <td><span className="badge badge-success">{validations[1]?.status ?? "-"}</span></td>
                                        </tr>
                                        <tr>
                                            <th>Job Category</th>
                                            <td className="text-muted">{ getCategoryLabel(validations[1]?.job_category_id) }</td>
                                        </tr>
                                        <tr>
                                            <th>Job Position</th>
                                            <td className="text-muted">{validations[1]?.job_position ?? '-'}</td>
                                        </tr>
                                        <tr>
                                            <th>Reason Accepted</th>
                                            <td className="text-muted">{validations[1]?.reason_accepted ?? '-'}</td>
                                        </tr>
                                        <tr>
                                            <th>Validator</th>
                                            <td className="text-muted">Usman M.Ti</td>
                                        </tr>
                                        <tr>
                                            <th>Validator Notes</th>
                                            <td className="text-muted">siap kerja</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                </div>
            </section>

            <section className="validation-section mb-5">
                <div className="section-header mb-3">
                    <div className="row">
                        <div className="col-md-8">
                            <h4 className="section-title text-muted">My Job Applications</h4>
                        </div>
                        <div className="col-md-4">
                            <a href="" className="btn btn-primary btn-lg btn-block">+ Add Job Applications</a>
                        </div>
                    </div>
                </div>
                <div className="section-body">
                    <div className="row mb-4">

                        <div className="col-md-12">
                            <div className="alert alert-warning">
                                Your validation must be approved by validator to applying job.
                            </div>
                        </div>

                        {appsLoading && (
                            <div className="col-12">Loading applications...</div>
                        )}

                        {appsError && (
                            <div className="col-12 text-danger">{appsError}</div>
                        )}

                        {!appsLoading && applications.length === 0 && (
                            <div className="col-12 text-muted">You have no job applications.</div>
                        )}

                        {applications.map((app) => (
                            <div className="col-md-6" key={app.apply_id}>
                                <div className="card card-default">
                                    <div className="card-header border-0">
                                        <h5 className="mb-0">{app.job_name ?? `Job #${app.job_id}`}</h5>
                                    </div>
                                    <div className="card-body p-0">
                                        <table className="table table-striped mb-0">
                                            <tbody>
                                                <tr>
                                                    <th>Address</th>
                                                    <td className="text-muted">{app.job_address ?? '-'}</td>
                                                </tr>
                                                <tr>
                                                    <th>Position</th>
                                                    <td className="text-muted">
                                                        <ul>
                                                            {app.positions && app.positions.length > 0 ? (
                                                                app.positions.map((p, i) => (
                                                                    <li key={i}>{p}</li>
                                                                ))
                                                            ) : (
                                                                <li>-</li>
                                                            )}
                                                        </ul>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th>Apply Date</th>
                                                    <td className="text-muted">{app.applied_at ?? '-'}</td>
                                                </tr>
                                                <tr>
                                                    <th>Notes</th>
                                                    <td className="text-muted">{app.notes ?? '-'}</td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        ))}

                    </div>
                </div>
            </section>
        </div>
    </main>

    <footer>
        <div className="container">
            <div className="text-center py-4 text-muted">
                Copyright &copy; 2023 - Web Tech ID
            </div>
        </div>
    </footer>
    </>
  )
}

export default Dashboard