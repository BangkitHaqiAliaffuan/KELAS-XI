import { Anchor, Menu, X } from 'lucide-react';
import { useState } from 'react';
import './Navbar.css';

const Navbar = ({ activeSection }) => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const sections = [
    { id: 'hero', label: 'Beranda' },
    { id: 'makanan', label: 'Makanan' },
    { id: 'pakaian', label: 'Pakaian' },
    { id: 'kesenian', label: 'Kesenian' },
    { id: 'pekerjaan', label: 'Pekerjaan' },
    { id: 'adat', label: 'Adat' },
    { id: 'nilai', label: 'Nilai' },
    { id: 'konflik', label: 'Konflik' },
    { id: 'solusi', label: 'Solusi' },
    { id: 'kesimpulan', label: 'Kesimpulan' },
    { id: 'refleksi', label: 'Refleksi' }
  ];

  const handleNavClick = (sectionId) => {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
      setMobileMenuOpen(false);
    }
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        {/* Logo */}
        <div className="navbar-logo">
          <Anchor size={32} />
          <span className="navbar-title">Kabupaten Cilacap</span>
        </div>

        {/* Desktop Navigation Links */}
        <div className="navbar-links desktop-only">
          {sections.map((section) => (
            <button
              key={section.id}
              className={`nav-link ${activeSection === section.id ? 'active' : ''}`}
              onClick={() => handleNavClick(section.id)}
            >
              {section.label}
            </button>
          ))}
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
            {sections.map((section) => (
              <button
                key={section.id}
                className={`mobile-dot ${activeSection === section.id ? 'active' : ''}`}
                onClick={() => handleNavClick(section.id)}
              >
                {section.label}
              </button>
            ))}
          </div>
        </div>
      )}
    </nav>
  );
};

export default Navbar;
