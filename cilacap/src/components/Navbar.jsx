import { Anchor, Menu, X } from 'lucide-react';
import { useState } from 'react';
import './Navbar.css';

const Navbar = ({ currentPage, totalPages, onPageChange }) => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleDotClick = (pageIndex) => {
    onPageChange(pageIndex);
    setMobileMenuOpen(false);
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        {/* Logo */}
        <div className="navbar-logo">
          <Anchor size={32} />
          <span className="navbar-title">Kabupaten Cilacap</span>
        </div>

        {/* Desktop Navigation Dots */}
        <div className="navbar-dots desktop-only">
          {Array.from({ length: totalPages }).map((_, index) => (
            <button
              key={index}
              className={`nav-dot ${currentPage === index ? 'active' : ''}`}
              onClick={() => handleDotClick(index)}
              aria-label={`Halaman ${index + 1}`}
            />
          ))}
        </div>

        {/* Page Counter */}
        <div className="navbar-counter desktop-only">
          <span className="counter-current">{currentPage + 1}</span>
          <span className="counter-separator">/</span>
          <span className="counter-total">{totalPages}</span>
        </div>

        {/* Mobile Menu Button */}
        <button
          className="mobile-menu-button mobile-only"
          onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          aria-label="Toggle menu"
        >
          {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>

      {/* Mobile Menu */}
      {mobileMenuOpen && (
        <div className="mobile-menu">
          <div className="mobile-dots">
            {Array.from({ length: totalPages }).map((_, index) => (
              <button
                key={index}
                className={`mobile-dot ${currentPage === index ? 'active' : ''}`}
                onClick={() => handleDotClick(index)}
              >
                Halaman {index + 1}
              </button>
            ))}
          </div>
        </div>
      )}
    </nav>
  );
};

export default Navbar;
