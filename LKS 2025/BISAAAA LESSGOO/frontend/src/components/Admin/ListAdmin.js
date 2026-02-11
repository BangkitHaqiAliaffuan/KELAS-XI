import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

const ListAdmin = () => {
  const token = localStorage.getItem("admintoken");
  //   console.log(token);
  const [admins, setAdmins] = useState([]);
  const fetchData = async () => {
    const response = await fetch("http://localhost:8000/api/v1/admins", {
      headers: {
        accept: "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    const data = await response.json();
    setAdmins(data.data);
    console.log(data);
  };

  useEffect(() => {
    fetchData();
  }, [token]);

  console.log(admins);

  return (
    <>
      <nav class="navbar navbar-expand-lg sticky-top bg-primary navbar-dark">
        <div class="container">
          <a class="navbar-brand" href="index.html">
            Administrator Portal
          </a>
          <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
            <li>
              <Link to="/list-admin" class="nav-link px-2 text-white">List Admin</Link>
            </li>
            <li>
             <Link to="/list-user" class="nav-link px-2 text-white">List Users</Link>
            </li>
            <li class="nav-item">
              <a class="nav-link active bg-dark" href="#">
                Welcome, Administrator
              </a>
            </li>
            <li class="nav-item">
              <a href="../signin.html" class="btn bg-white text-primary ms-4">
                Sign Out
              </a>
            </li>
          </ul>
        </div>
      </nav>

      <main>
        <div class="list-form py-5">
          <div class="container">
            <h6 class="mb-3">List Admin Users</h6>

            <table class="table table-striped">
              <thead>
                <tr>
                  <th>Username</th>
                  <th>Created at</th>
                  <th>Last login</th>
                </tr>
              </thead>
              <tbody>
                {admins.length !== 0
                  ? admins.map((item, i) => (
                      <tr key={i}>
                        <td>{item.username}</td>
                        <td>{item.created_at}</td>
                        <td>{item.last_login_at}</td>
                      </tr>
                  ))
                  : "done"}
              </tbody>
            </table>
          </div>
        </div>
      </main>
    </>
  );
};

export default ListAdmin;
