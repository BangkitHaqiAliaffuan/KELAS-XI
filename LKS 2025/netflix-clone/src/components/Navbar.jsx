import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { logout } from '../utils/firebase';
import { assets } from '../assets/assets';
import './Navbar.css';

const Navbar = () => {
  const [show, setShow] = useState(false);
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const handleScroll = () => {
      if (window.scrollY > 100) {
        setShow(true);
      } else {
        setShow(false);
      }
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleLogout = async () => {
    try {
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Error logging out:', error);
    }
  };

  return (
    <nav className={`navbar ${show && 'navbar-black'}`}>
      <div className="navbar-content">
        <div className="navbar-left">
          <img 
            src={assets.logo} 
            alt="Netflix" 
            className="navbar-logo"
            onClick={() => navigate('/')}
          />
          <ul className="navbar-links">
            <li><a href="/">Home</a></li>
            <li><a href="/">TV Shows</a></li>
            <li><a href="/">Movies</a></li>
            <li><a href="/">Latest</a></li>
            <li><a href="/">My List</a></li>
          </ul>
        </div>
        
        <div className="navbar-right">
          <div className="navbar-search">
            <img src={assets.searchIcon} alt="Search" />
          </div>
          <div className="navbar-notifications">
            <img src={assets.bellIcon} alt="Notifications" />
          </div>
          <div className="navbar-profile" onClick={() => setShowProfileMenu(!showProfileMenu)}>
            <img src={assets.profileImg} alt="Profile" />
            <img src={assets.caretIcon} alt="Menu" className="navbar-caret" />
            
            {showProfileMenu && (
              <div className="profile-dropdown">
                <div className="profile-option">
                  <img src={assets.profileImg} alt="Profile" />
                  <span>Manage Profiles</span>
                </div>
                <div className="profile-option">
                  <span>Account</span>
                </div>
                <div className="profile-option">
                  <span>Help Center</span>
                </div>
                <div className="profile-divider"></div>
                <div className="profile-option" onClick={handleLogout}>
                  <span>Sign out of Netflix</span>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;