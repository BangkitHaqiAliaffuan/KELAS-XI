import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

const ListUser = () => {
  const token = localStorage.getItem("admintoken");
  //   console.log(token);
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const fetchData = async () => {
    const response = await fetch("http://localhost:8000/api/v1/users", {
      headers: {
        accept: "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    const data = await response.json();
    setUsers(data.data);
    console.log(data);
  };

  const DeleteData = async (id) => {
    if (!window.confirm("Are you sure you want to delete this user?")) {
      return;
    }
    const response = await fetch(`http://localhost:8000/api/v1/users/${id}`, {
      method: "DELETE",
      headers: {
        accept: "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.ok) {
      // Update state langsung tanpa fetch ulang
      setUsers(users.filter((user) => user.id !== id));
    }

    const data = await response.json();
    // setUsers(data.data);
    // console.log(data);
    fetchData();
    // navigate("/list-user");
  };

  useEffect(() => {
    if (token) {
      fetchData();
    } else {
      navigate("/login");
    }
  }, []);

  return (
    <>
      <nav class="navbar navbar-expand-lg sticky-top bg-primary navbar-dark">
        <div class="container">
          <a class="navbar-brand" href="index.html">
            Administrator Portal
          </a>
          <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
            <li>
              <Link to="/list-admin" class="nav-link px-2 text-white">
                List Admin
              </Link>
            </li>
            <li>
              <Link to="/list-user" class="nav-link px-2 text-white">
                List Users
              </Link>
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
        <div class="hero py-5 bg-light">
          <div class="container">
            <Link to="/add-user" class="btn btn-primary">
              Add User
            </Link>
          </div>
        </div>

        <div class="list-form py-5">
          <div class="container">
            <h6 class="mb-3">List Users</h6>

            <table class="table table-striped">
              <thead>
                <tr>
                  <th>Username</th>
                  <th>Created at</th>
                  <th>Last login</th>
                  <th>Role</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {users.length !== 0
                  ? users.map((item, i) => (
                      <tr key={i}>
                        <td>
                          <a
                            href="../Gaming Portal/profile.html"
                            target="_blank"
                          >
                            {item.username}
                          </a>
                        </td>
                        <td>{item.created_at}</td>
                        <td>{item.last_login_at ? item.last_login_at : "-"}</td>
                        <td>{item.role}</td>
                        <td>
                          {item.status === "active" ? (
                            <span class="bg-success text-white p-1 d-inline-block">Active</span>
                          ) : (
                            <span class="bg-danger text-white p-1 d-inline-block">Blocked</span>
                          )}
                        </td>
                        <td>
                          <div class="btn-group" role="group">
                            <button
                              type="button"
                              class="btn btn-primary btn-sm dropdown-toggle"
                              data-bs-toggle="dropdown"
                              aria-expanded="false"
                            >
                              Lock
                            </button>
                            <ul class="dropdown-menu">
                              <li>
                                <button
                                  type="submit"
                                  class="dropdown-item"
                                  name="reason"
                                  value="spamming"
                                >
                                  Spamming
                                </button>
                              </li>
                              <li>
                                <button
                                  type="submit"
                                  class="dropdown-item"
                                  name="reason"
                                  value="cheating"
                                >
                                  Cheating
                                </button>
                              </li>
                              <li>
                                <button
                                  type="submit"
                                  class="dropdown-item"
                                  name="reason"
                                  value="other"
                                >
                                  Other
                                </button>
                              </li>
                            </ul>
                          </div>
                          <Link
                            to={`/update-users/${item.id}`}
                            class="btn btn-sm btn-secondary"
                          >
                            Update
                          </Link>
                          <div
                            onClick={() => DeleteData(item.id)}
                            class="btn btn-sm btn-danger"
                          >
                            Delete
                          </div>
                        </td>
                      </tr>
                    ))
                  : "tidak ada data"}
              </tbody>
            </table>
          </div>
        </div>
      </main>
    </>
  );
};

export default ListUser;
