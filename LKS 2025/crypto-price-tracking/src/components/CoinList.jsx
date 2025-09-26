import { useEffect, useState } from 'react';
import { useCrypto } from '../context/CryptoContext';
import { fetchCoinsMarket } from '../utils/api';
import CoinCard from './CoinCard';
import './CoinList.css';

const CoinList = () => {
  const { 
    currency, 
    filteredCoins, 
    loading, 
    setCoins, 
    setLoading, 
    setError,
    searchTerm
  } = useCrypto();

  const [refreshTimer, setRefreshTimer] = useState(null);
  const [lastRefresh, setLastRefresh] = useState(null);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);

  // Fetch coins data
  const fetchCoins = async (pageNum = 1, append = false) => {
    try {
      if (!append) {
        setLoading(true);
      } else {
        setLoadingMore(true);
      }
      
      setError(null);
      
      const data = await fetchCoinsMarket(currency, 50, pageNum);
      
      if (append) {
        setCoins(prevCoins => [...prevCoins, ...data]);
      } else {
        setCoins(data);
      }
      
      // Check if there are more pages
      setHasMore(data.length === 50);
      setLastRefresh(new Date());
      
    } catch (error) {
      console.error('Error fetching coins:', error);
      setError(error.message);
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  };

  // Initial fetch and currency change
  useEffect(() => {
    setPage(1);
    fetchCoins(1, false);
  }, [currency]);

  // Auto refresh every 30 seconds
  useEffect(() => {
    const timer = setInterval(() => {
      fetchCoins(1, false);
    }, 30000);

    setRefreshTimer(timer);

    return () => {
      if (timer) {
        clearInterval(timer);
      }
    };
  }, [currency]);

  // Cleanup timer on unmount
  useEffect(() => {
    return () => {
      if (refreshTimer) {
        clearInterval(refreshTimer);
      }
    };
  }, [refreshTimer]);

  // Handle manual refresh
  const handleRefresh = () => {
    setPage(1);
    fetchCoins(1, false);
  };

  // Handle load more
  const handleLoadMore = () => {
    const nextPage = page + 1;
    setPage(nextPage);
    fetchCoins(nextPage, true);
  };

  // Loading skeleton
  const LoadingSkeleton = () => (
    <div className="coins-grid">
      {Array.from({ length: 12 }).map((_, index) => (
        <div key={index} className="coin-card-skeleton">
          <div className="skeleton-header">
            <div className="skeleton-image"></div>
            <div className="skeleton-info">
              <div className="skeleton-name"></div>
              <div className="skeleton-symbol"></div>
            </div>
            <div className="skeleton-rank"></div>
          </div>
          <div className="skeleton-price">
            <div className="skeleton-current-price"></div>
            <div className="skeleton-change"></div>
          </div>
          <div className="skeleton-market">
            {Array.from({ length: 3 }).map((_, i) => (
              <div key={i} className="skeleton-market-item">
                <div className="skeleton-label"></div>
                <div className="skeleton-value"></div>
              </div>
            ))}
          </div>
          <div className="skeleton-range">
            <div className="skeleton-range-label"></div>
            <div className="skeleton-range-bar"></div>
          </div>
        </div>
      ))}
    </div>
  );

  // Error state
  const ErrorState = ({ error, onRetry }) => (
    <div className="error-state">
      <div className="error-icon">⚠️</div>
      <h3>Something went wrong</h3>
      <p>{error}</p>
      <button onClick={onRetry} className="retry-btn">
        Try Again
      </button>
    </div>
  );

  // Empty state
  const EmptyState = () => (
    <div className="empty-state">
      <div className="empty-icon">🔍</div>
      <h3>No cryptocurrencies found</h3>
      <p>
        {searchTerm 
          ? `No results found for "${searchTerm}". Try a different search term.`
          : 'Unable to load cryptocurrency data. Please try again.'
        }
      </p>
    </div>
  );

  // Main render
  if (loading && filteredCoins.length === 0) {
    return (
      <div className="coin-list-container">
        <LoadingSkeleton />
      </div>
    );
  }

  return (
    <div className="coin-list-container">
      {/* Header with refresh and stats */}
      <div className="list-header">
        <div className="list-info">
          <h2>
            {searchTerm 
              ? `Search Results for "${searchTerm}" (${filteredCoins.length})`
              : `Top Cryptocurrencies (${filteredCoins.length})`
            }
          </h2>
          {lastRefresh && (
            <p className="last-refresh">
              Last updated: {lastRefresh.toLocaleTimeString()}
            </p>
          )}
        </div>
        
        <div className="list-controls">
          <button 
            onClick={handleRefresh}
            className="refresh-btn"
            disabled={loading}
            aria-label="Refresh data"
          >
            <svg 
              className={`refresh-icon ${loading ? 'spinning' : ''}`}
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path 
                strokeLinecap="round" 
                strokeLinejoin="round" 
                strokeWidth={2} 
                d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" 
              />
            </svg>
            Refresh
          </button>
        </div>
      </div>

      {/* Content */}
      {filteredCoins.length === 0 ? (
        <EmptyState />
      ) : (
        <>
          <div className="coins-grid">
            {filteredCoins.map((coin) => (
              <CoinCard key={coin.id} coin={coin} />
            ))}
          </div>

          {/* Load More Button */}
          {!searchTerm && hasMore && (
            <div className="load-more-container">
              <button
                onClick={handleLoadMore}
                className="load-more-btn"
                disabled={loadingMore}
              >
                {loadingMore ? (
                  <>
                    <div className="loading-spinner small"></div>
                    Loading more...
                  </>
                ) : (
                  'Load More Cryptocurrencies'
                )}
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default CoinList;