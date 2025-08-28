import React from "react";
import "./Navbar.css";
import assets from "../../assets";
import { Link } from "react-router-dom";
const Navbar = ({ setSidebar }) => {
  return (
    <nav className="flex-div">
      <div className="nav-left flex-div">
        <img
          className="menu-icon"
          onClick={() => setSidebar((prev) => (prev === false ? true : false))}
          src={assets.menu}
        />
        <Link to="/">
        <  img className="logo" src={assets.logo} />
        </Link>
      </div>

      <div className="nav-middle flex-div">
        <div className="search-box flex-div">
          <input type="text" placeholder="search" />
          <img src={assets.search} />
        </div>
      </div>

      <div className="nav-right flex-div">
        <img src={assets.upload} />
        <img src={assets.more} />
        <img src={assets.notification} />
        <img src={assets.user_profile} className="user-icon" />
      </div>
    </nav>
  );
};

export default Navbar;
