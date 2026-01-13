import React from "react";
import "./Navbar.css";
import Login from "./Login";
import { Link, Navigate, useNavigate } from "react-router-dom";
import axios from "axios";
const Navbar = () => {
  const token = localStorage.getItem("token");
  let isLoggedIn = false;
  if (token) {
    isLoggedIn = true;
    // console.log("berhasil token", token)
  }

  const navigate = useNavigate()

  const handleLogout = async (e)=>{
    try{
      const token = localStorage.getItem("token")
      await axios.post("http://127.0.0.1:8000/api/logout", {}, {
        headers:{
          Authorization: `Bearer ${token}`
        }
      })

      console.log("sukses hapus token")

      localStorage.removeItem("token")
      localStorage.removeItem("user")

      isLoggedIn = false
      navigate("home")
    } catch (error){
      // console.log("Error", error.response?.data || error.message)
    }

  }

  return (
    <div className="navbar">
      <div className="logo">
        <span className="logo-name">Logo</span>
      </div>
      <div className="items">
        {isLoggedIn ? (
          <>
            <Link to="/profile" className="item">Profil User</Link>
            <div className="item">Daftar Pemain</div>
            <div className="item">Tambah Daftar Pemain</div>
            <div className="item">Edit Pemain</div>
            <div className="item">Hapus Data Pemain</div>
            <button className="item" onClick={handleLogout}>Logout</button>
          </>
        ) : (
          <Link className="login" to="/login">Login</Link>
        )}
      </div>
    </div>
  );
};

export default Navbar;
