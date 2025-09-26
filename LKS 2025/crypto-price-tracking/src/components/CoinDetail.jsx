import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useCrypto } from '../context/CryptoContext';
import { fetchCoinDetail, formatPrice, formatPercentage, formatNumber } from '../utils/api';
import './CoinDetail.css';

const CoinDetail = () => {
  const { coinId } = useParams();
  const navigate = useNavigate();
  const { 
    currency, 
    currencySymbol, 
    watchlist, 
    addToWatchlist, 
    removeFromWatchlist 
  } = useCrypto();

  const [coin, setCoin] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [imageError, setImageError] = useState(false);

  // Fetch coin detail
  useEffect(() => {
    const fetchDetail = async () => {
      try {
        setLoading(true);
        setError(null);
        
        const data = await fetchCoinDetail(coinId);
        setCoin(data);
      } catch (error) {
        console.error('Error fetching coin detail:', error);
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    if (coinId) {
      fetchDetail();
    }
  }, [coinId, currency]);

  // Handle back navigation
  const handleBack = () => {
    navigate(-1);
  };

  // Handle watchlist toggle
  const handleWatchlistToggle = () => {
    const isInWatchlist = watchlist.includes(coinId);
    if (isInWatchlist) {
      removeFromWatchlist(coinId);
    } else {
      addToWatchlist(coinId);
    }
  };

  // Handle image error
  const handleImageError = () => {
    setImageError(true);
  };

  // Loading state
  if (loading) {
    return (
      <div className="coin-detail-container">
        <div className="detail-loading">
          <div className="loading-spinner large"></div>
          <p>Loading cryptocurrency details...</p>
        </div>
      </div>
    );
  }

  // Error state
  if (error) {
    return (
      <div className="coin-detail-container">
        <div className="detail-error">
          <div className="error-icon">⚠️</div>
          <h2>Failed to load details</h2>
          <p>{error}</p>
          <div className="error-actions">
            <button onClick={handleBack} className="back-btn">
              Go Back
            </button>
            <button onClick={() => window.location.reload()} className="retry-btn">
              Try Again
            </button>
          </div>
        </div>
      </div>
    );
  }

  // No coin data
  if (!coin) {
    return (
      <div className="coin-detail-container">
        <div className="detail-error">
          <div className="error-icon">❓</div>
          <h2>Cryptocurrency not found</h2>
          <p>The requested cryptocurrency could not be found.</p>
          <button onClick={handleBack} className="back-btn">
            Go Back
          </button>
        </div>
      </div>
    );
  }

  const isInWatchlist = watchlist.includes(coinId);
  const currentPrice = coin.market_data?.current_price?.[currency];
  const priceChange24h = coin.market_data?.price_change_percentage_24h;
  const marketCap = coin.market_data?.market_cap?.[currency];
  const totalVolume = coin.market_data?.total_volume?.[currency];
  const circulatingSupply = coin.market_data?.circulating_supply;
  const totalSupply = coin.market_data?.total_supply;
  const maxSupply = coin.market_data?.max_supply;

  const priceChangeFormatted = formatPercentage(priceChange24h);

  return (
    <div className="coin-detail-container">
      {/* Header */}
      <div className="detail-header">
        <button onClick={handleBack} className="back-btn">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
          Back
        </button>

        <button
          onClick={handleWatchlistToggle}
          className={`watchlist-btn ${isInWatchlist ? 'active' : ''}`}
          aria-label={isInWatchlist ? 'Remove from watchlist' : 'Add to watchlist'}
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
          {isInWatchlist ? 'Remove from Watchlist' : 'Add to Watchlist'}
        </button>
      </div>

      {/* Main Content */}
      <div className="detail-content">
        {/* Coin Info Section */}
        <div className="coin-info-section">
          <div className="coin-header">
            <div className="coin-image-container">
              {!imageError ? (
                <img
                  src={coin.image?.large}
                  alt={coin.name}
                  className="coin-image-large"
                  onError={handleImageError}
                />
              ) : (
                <div className="coin-image-fallback-large">
                  {coin.symbol?.charAt(0).toUpperCase()}
                </div>
              )}
            </div>
            <div className="coin-title">
              <h1>{coin.name}</h1>
              <span className="coin-symbol-large">
                {coin.symbol?.toUpperCase()}
              </span>
              {coin.market_cap_rank && (
                <span className="coin-rank-large">
                  Rank #{coin.market_cap_rank}
                </span>
              )}
            </div>
          </div>

          {/* Price Section */}
          <div className="price-section-detail">
            <div className="current-price-large">
              {formatPrice(currentPrice, currency, currencySymbol)}
            </div>
            {priceChange24h !== null && (
              <div className={`price-change-large ${priceChangeFormatted.colorClass}`}>
                <span className="change-indicator">
                  {priceChange24h >= 0 ? '▲' : '▼'}
                </span>
                {priceChangeFormatted.value}
                <span className="time-period">(24h)</span>
              </div>
            )}
          </div>
        </div>

        {/* Statistics Grid */}
        <div className="stats-grid">
          <div className="stat-card">
            <h3>Market Cap</h3>
            <p>{currencySymbol}{formatNumber(marketCap)}</p>
          </div>
          <div className="stat-card">
            <h3>24h Volume</h3>
            <p>{currencySymbol}{formatNumber(totalVolume)}</p>
          </div>
          <div className="stat-card">
            <h3>Circulating Supply</h3>
            <p>{formatNumber(circulatingSupply)} {coin.symbol?.toUpperCase()}</p>
          </div>
          <div className="stat-card">
            <h3>Total Supply</h3>
            <p>{totalSupply ? `${formatNumber(totalSupply)} ${coin.symbol?.toUpperCase()}` : 'N/A'}</p>
          </div>
          <div className="stat-card">
            <h3>Max Supply</h3>
            <p>{maxSupply ? `${formatNumber(maxSupply)} ${coin.symbol?.toUpperCase()}` : '∞'}</p>
          </div>
          <div className="stat-card">
            <h3>All-Time High</h3>
            <p>{formatPrice(coin.market_data?.ath?.[currency], currency, currencySymbol)}</p>
          </div>
        </div>

        {/* Description Section */}
        {coin.description?.en && (
          <div className="description-section">
            <h2>About {coin.name}</h2>
            <div 
              className="description-content"
              dangerouslySetInnerHTML={{ 
                __html: coin.description.en.replace(/<a/g, '<a target="_blank" rel="noopener noreferrer"')
              }}
            />
          </div>
        )}

        {/* Links Section */}
        {(coin.links?.homepage?.[0] || coin.links?.blockchain_site?.length > 0) && (
          <div className="links-section">
            <h2>Links</h2>
            <div className="links-grid">
              {coin.links.homepage?.[0] && (
                <a 
                  href={coin.links.homepage[0]} 
                  target="_blank" 
                  rel="noopener noreferrer"
                  className="link-btn"
                >
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9v-9m0 9c-1.657 0-3-4.03-3-9s1.343-9 3-9m0 18c1.657 0 3-4.03 3-9s-1.343-9-3-9m-9 9a9 9 0 019-9" />
                  </svg>
                  Official Website
                </a>
              )}
              {coin.links.blockchain_site?.slice(0, 3).map((site, index) => {
                if (!site) return null;
                const siteName = new URL(site).hostname.replace('www.', '');
                return (
                  <a 
                    key={index}
                    href={site} 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="link-btn"
                  >
                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1" />
                    </svg>
                    {siteName}
                  </a>
                );
              })}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default CoinDetail;