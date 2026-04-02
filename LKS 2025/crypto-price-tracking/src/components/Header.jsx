import { useState, useEffect } from 'react';
import { FaBitcoin, FaChartPie, FaCoins, FaSearch, FaSignal, FaWallet } from 'react-icons/fa';
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
  const activeCoins = globalSummary?.active_cryptocurrencies;

  const formatCurrencySummary = (value) => (value !== null && value !== undefined ? formatNumber(value) : 'N/A');

  const summaryCards = [
    {
      title: 'Global Cap',
      icon: <FaChartPie />,
      value: summaryLoading ? 'Loading...' : formatCurrencySummary(currentMarketCap),
      accent: '+ live',
    },
    {
      title: '24h Volume',
      icon: <FaWallet />,
      value: summaryLoading ? 'Loading...' : formatCurrencySummary(currentVolume),
      accent: 'market flow',
    },
    {
      title: 'BTC Dominance',
      icon: <FaBitcoin />,
      value: summaryLoading ? 'Loading...' : (btcDominance !== null && btcDominance !== undefined ? `${btcDominance.toFixed(2)}%` : 'N/A'),
      accent: 'market share',
    },
    {
      title: 'Active Coins',
      icon: <FaCoins />,
      value: summaryLoading ? 'Loading...' : formatCurrencySummary(activeCoins),
      accent: 'tracked assets',
    },
  ];

  return (
    <header className="header">
      <div className="header-shell">
        <div className="header-content">
          <div className="logo-section">
            <div className="logo">
              <FaSignal className="logo-image" aria-hidden="true" />
            </div>
            <div>
              <h2 className="app-title">Cryptocurrency Markets</h2>
              <p className="app-subtitle">Real-time valuation powered by CoinGecko API</p>
            </div>
          </div>

          <div className="header-actions">
            <div className="search-container">
              <div className="search-input-wrapper">
                <FaSearch className="search-icon" aria-hidden="true" />
                <input
                  type="text"
                  placeholder="Search assets..."
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

            <div className="currency-selector">
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

            <button type="button" className="export-btn">Export CSV</button>
          </div>
        </div>

        <div className="market-summary-grid">
          {summaryCards.map((card) => (
            <article className="summary-card" key={card.title}>
              <div className="summary-head">
                <span className="summary-icon">{card.icon}</span>
                <span className="summary-title">{card.title}</span>
              </div>
              <p className="summary-value">{card.value}</p>
              <span className="summary-accent">{card.accent}</span>
            </article>
          ))}
        </div>
      </div>
    </header>
  );
};

export default Header;