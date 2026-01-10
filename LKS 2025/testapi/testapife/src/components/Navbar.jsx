import React from "react";
import "./Navbar.css";
const Navbar = () => {
  return (
    <div className="navbar">
      <div className="logo">
        <span className="logo-name">Logo</span>
      </div>
      <div className="items">
        <div className="item">Profil User</div>
        <div className="item">Daftar Pemain</div>
        <div className="item">Tambah Daftar Pemain</div>
        <div className="item">Edit Pemain</div>
        <div className="item">Hapus Data Pemain</div>
      </div>
    </div>
  );
};

export default Navbar;
