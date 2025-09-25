import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import assets from '../assets/assets';
import './Navbar.css';

const Navbar = ({ setSidebar, onSearch }) => {
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      onSearch(searchQuery.trim());
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch(e);
    }
  };

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <img 
          className="menu-icon" 
          onClick={() => setSidebar(prev => !prev)}
          src={assets.menu} 
          alt="Menu" 
        />
        <Link to="/">
          <img className="logo" src={assets.logo} alt="YouTube" />
        </Link>
      </div>

      <div className="navbar-middle">
        <div className="search-box">
          <input
            type="text"
            placeholder="Search"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyPress={handleKeyPress}
          />
          <img 
            src={assets.search} 
            alt="Search"
            onClick={handleSearch}
            className="search-icon"
          />
        </div>
        <img className="voice-icon" src={assets.voice_search} alt="Voice search" />
      </div>

      <div className="navbar-right">
        <img src={assets.upload} alt="Upload" />
        <img src={assets.more} alt="More" />
        <img src={assets.notification} alt="Notifications" />
        <img className="user-icon" src={assets.user_profile} alt="Profile" />
      </div>
    </nav>
  );
};

export default Navbar;