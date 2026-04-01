import { useState, useEffect } from 'react';
import { FaBitcoin } from 'react-icons/fa';
import { useCrypto } from '../context/CryptoContext';
import { debounce, fetchGlobalData, formatNumber } from '../utils/api';
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
  const [globalSummary, setGlobalSummary] = useState(null);
  const [summaryLoading, setSummaryLoading] = useState(true);

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

  useEffect(() => {
    let isMounted = true;

    const loadGlobalSummary = async () => {
      try {
        setSummaryLoading(true);
        const data = await fetchGlobalData();
        if (isMounted) {
          setGlobalSummary(data);
        }
      } catch (error) {
        console.error('Error fetching global market summary:', error);
      } finally {
        if (isMounted) {
          setSummaryLoading(false);
        }
      }
    };

    loadGlobalSummary();

    const intervalId = setInterval(() => {
      loadGlobalSummary();
    }, 60000);

    return () => {
      isMounted = false;
      clearInterval(intervalId);
    };
  }, []);

  const currentMarketCap = globalSummary?.total_market_cap?.[currency];
  const currentVolume = globalSummary?.total_volume?.[currency];
  const btcDominance = globalSummary?.market_cap_percentage?.btc;

  const marketCapText = summaryLoading
    ? 'Loading...'
    : currentMarketCap !== null && currentMarketCap !== undefined
      ? formatNumber(currentMarketCap)
      : 'N/A';

  const volumeText = summaryLoading
    ? 'Loading...'
    : currentVolume !== null && currentVolume !== undefined
      ? formatNumber(currentVolume)
      : 'N/A';

  const dominanceText = summaryLoading
    ? 'Loading...'
    : btcDominance !== null && btcDominance !== undefined
      ? `${btcDominance.toFixed(2)}%`
      : 'N/A';

  return (
    <header className="header">
      <div className="container">
        <div className="header-content">
          {/* Logo Section */}
          <div className="logo-section">
            <div className="logo">
              <FaBitcoin className="logo-image" aria-hidden="true" />
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
            <span className="market-value">{marketCapText}</span>
          </div>
          <div className="market-info">
            <span className="market-label">24h Vol:</span>
            <span className="market-value">{volumeText}</span>
          </div>
          <div className="market-info">
            <span className="market-label">BTC Dominance:</span>
            <span className="market-value">{dominanceText}</span>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;