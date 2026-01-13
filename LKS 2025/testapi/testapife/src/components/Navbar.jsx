import React from "react";
import "./Navbar.css";
import Login from "./Login";
import { Link } from "react-router-dom";
const Navbar = () => {
  const token = localStorage.getItem("token");
  let isLoggedIn = false;
  if (token) {
    isLoggedIn = true;
    console.log("berhasil token")
  }
  return (
    <div className="navbar">
      <div className="logo">
        <span className="logo-name">Logo</span>
      </div>
      <div className="items">
        {isLoggedIn ? (
          <>
            <Link to="" className="item">Profil User</Link>
            <div className="item">Daftar Pemain</div>
            <div className="item">Tambah Daftar Pemain</div>
            <div className="item">Edit Pemain</div>
            <div className="item">Hapus Data Pemain</div>
          </>
        ) : (
          <Link className="login" to="/login">Login</Link>
        )}
      </div>
    </div>
  );
};

export default Navbar;
