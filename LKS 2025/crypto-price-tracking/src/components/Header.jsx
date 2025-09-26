import { useState, useEffect } from 'react';
import { useCrypto } from '../context/CryptoContext';
import { debounce } from '../utils/api';
import './Header.css';

const Header = () => {
  const { 
    currency, 
    currencyOptions, 
    searchTerm, 
    setCurrency, 
    setSearchTerm 
  } = useCrypto();

  const [localSearchTerm, setLocalSearchTerm] = useState(searchTerm);

  // Debounced search function
  const debouncedSearch = debounce((term) => {
    setSearchTerm(term);
  }, 300);

  // Handle search input change
  const handleSearchChange = (e) => {
    const value = e.target.value;
    setLocalSearchTerm(value);
    debouncedSearch(value);
  };

  // Handle currency change
  const handleCurrencyChange = (e) => {
    setCurrency(e.target.value);
  };

  // Clear search
  const clearSearch = () => {
    setLocalSearchTerm('');
    setSearchTerm('');
  };

  return (
    <header className="header">
      <div className="container">
        <div className="header-content">
          {/* Logo Section */}
          <div className="logo-section">
            <div className="logo">
              <img 
                src="/src/assets/logo.png" 
                alt="CryptoTrack" 
                className="logo-image"
                onError={(e) => {
                  e.target.style.display = 'none';
                  e.target.nextSibling.style.display = 'block';
                }}
              />
              <div className="logo-text" style={{ display: 'none' }}>
                ₿ CryptoTrack
              </div>
            </div>
            <h1 className="app-title">CryptoTrack</h1>
          </div>

          {/* Search Section */}
          <div className="search-section">
            <div className="search-container">
              <div className="search-input-wrapper">
                <svg 
                  className="search-icon" 
                  fill="none" 
                  stroke="currentColor" 
                  viewBox="0 0 24 24"
                >
                  <path 
                    strokeLinecap="round" 
                    strokeLinejoin="round" 
                    strokeWidth={2} 
                    d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" 
                  />
                </svg>
                <input
                  type="text"
                  placeholder="Search cryptocurrency..."
                  value={localSearchTerm}
                  onChange={handleSearchChange}
                  className="search-input"
                />
                {localSearchTerm && (
                  <button 
                    onClick={clearSearch}
                    className="clear-search-btn"
                    aria-label="Clear search"
                  >
                    <svg 
                      fill="none" 
                      stroke="currentColor" 
                      viewBox="0 0 24 24"
                    >
                      <path 
                        strokeLinecap="round" 
                        strokeLinejoin="round" 
                        strokeWidth={2} 
                        d="M6 18L18 6M6 6l12 12" 
                      />
                    </svg>
                  </button>
                )}
              </div>
            </div>
          </div>

          {/* Currency Selector Section */}
          <div className="currency-section">
            <div className="currency-selector">
              <label htmlFor="currency-select" className="currency-label">
                Currency:
              </label>
              <select
                id="currency-select"
                value={currency}
                onChange={handleCurrencyChange}
                className="currency-select"
              >
                {Object.entries(currencyOptions).map(([code, { name, symbol }]) => (
                  <option key={code} value={code}>
                    {name} ({symbol.trim()})
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        {/* Market Summary Bar (Optional) */}
        <div className="market-summary">
          <div className="market-info">
            <span className="market-label">Global Market Cap:</span>
            <span className="market-value">Loading...</span>
          </div>
          <div className="market-info">
            <span className="market-label">24h Vol:</span>
            <span className="market-value">Loading...</span>
          </div>
          <div className="market-info">
            <span className="market-label">BTC Dominance:</span>
            <span className="market-value">Loading...</span>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;