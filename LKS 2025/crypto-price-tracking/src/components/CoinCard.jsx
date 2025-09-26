import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCrypto } from '../context/CryptoContext';
import { formatPrice, formatPercentage, formatNumber } from '../utils/api';
import './CoinCard.css';

const CoinCard = ({ coin }) => {
  const navigate = useNavigate();
  const { 
    currency, 
    currencySymbol, 
    watchlist, 
    addToWatchlist, 
    removeFromWatchlist 
  } = useCrypto();
  
  const [imageError, setImageError] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  // Handle image error
  const handleImageError = () => {
    setImageError(true);
  };

  // Handle card click to navigate to detail page
  const handleCardClick = () => {
    setIsLoading(true);
    navigate(`/coin/${coin.id}`);
  };

  // Handle watchlist toggle
  const handleWatchlistToggle = (e) => {
    e.stopPropagation(); // Prevent card click
    
    const isInWatchlist = watchlist.includes(coin.id);
    if (isInWatchlist) {
      removeFromWatchlist(coin.id);
    } else {
      addToWatchlist(coin.id);
    }
  };

  // Format price change
  const priceChange = formatPercentage(coin.price_change_percentage_24h);
  
  // Check if coin is in watchlist
  const isInWatchlist = watchlist.includes(coin.id);

  return (
    <div 
      className={`coin-card ${isLoading ? 'loading' : ''}`}
      onClick={handleCardClick}
      role="button"
      tabIndex={0}
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault();
          handleCardClick();
        }
      }}
    >
      {/* Watchlist Button */}
      <button
        className={`watchlist-btn ${isInWatchlist ? 'active' : ''}`}
        onClick={handleWatchlistToggle}
        aria-label={isInWatchlist ? 'Remove from watchlist' : 'Add to watchlist'}
        title={isInWatchlist ? 'Remove from watchlist' : 'Add to watchlist'}
      >
        <svg
          fill={isInWatchlist ? 'currentColor' : 'none'}
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z"
          />
        </svg>
      </button>

      {/* Coin Header */}
      <div className="coin-header">
        <div className="coin-image-container">
          {!imageError ? (
            <img
              src={coin.image}
              alt={coin.name}
              className="coin-image"
              onError={handleImageError}
              loading="lazy"
            />
          ) : (
            <div className="coin-image-fallback">
              {coin.symbol.charAt(0).toUpperCase()}
            </div>
          )}
        </div>
        <div className="coin-info">
          <h3 className="coin-name" title={coin.name}>
            {coin.name}
          </h3>
          <span className="coin-symbol">
            {coin.symbol.toUpperCase()}
          </span>
        </div>
        <div className="coin-rank">
          #{coin.market_cap_rank}
        </div>
      </div>

      {/* Price Section */}
      <div className="price-section">
        <div className="current-price">
          {formatPrice(coin.current_price, currency, currencySymbol)}
        </div>
        <div className={`price-change ${priceChange.colorClass}`}>
          <span className="change-indicator">
            {coin.price_change_percentage_24h >= 0 ? '▲' : '▼'}
          </span>
          {priceChange.value}
        </div>
      </div>

      {/* Market Data */}
      <div className="market-data">
        <div className="market-item">
          <span className="market-label">Market Cap</span>
          <span className="market-value">
            {currencySymbol}{formatNumber(coin.market_cap)}
          </span>
        </div>
        <div className="market-item">
          <span className="market-label">Volume (24h)</span>
          <span className="market-value">
            {currencySymbol}{formatNumber(coin.total_volume)}
          </span>
        </div>
        <div className="market-item">
          <span className="market-label">Circulating Supply</span>
          <span className="market-value">
            {formatNumber(coin.circulating_supply)} {coin.symbol.toUpperCase()}
          </span>
        </div>
      </div>

      {/* Price Range (24h) */}
      <div className="price-range">
        <div className="range-label">24h Range</div>
        <div className="range-bar">
          <div className="range-track">
            <div 
              className="range-fill"
              style={{
                width: `${((coin.current_price - coin.low_24h) / (coin.high_24h - coin.low_24h)) * 100}%`
              }}
            ></div>
          </div>
          <div className="range-values">
            <span className="range-low">
              {formatPrice(coin.low_24h, currency, currencySymbol)}
            </span>
            <span className="range-high">
              {formatPrice(coin.high_24h, currency, currencySymbol)}
            </span>
          </div>
        </div>
      </div>

      {/* Loading Overlay */}
      {isLoading && (
        <div className="loading-overlay">
          <div className="loading-spinner"></div>
        </div>
      )}
    </div>
  );
};

export default CoinCard;