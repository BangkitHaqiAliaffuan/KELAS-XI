import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaArrowRight, FaFileExport, FaSyncAlt } from 'react-icons/fa';
import { useCrypto } from '../context/CryptoContext';
import { fetchCoinsMarket, formatNumber, formatPercentage, formatPrice } from '../utils/api';
import './CoinList.css';

const CoinList = ({ showWatchlistOnly = false }) => {
  const navigate = useNavigate();
  const { 
    currency, 
    currencySymbol,
    coins,
    filteredCoins, 
    watchlist,
    loading, 
    error,
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
  const displayedCoins = showWatchlistOnly
    ? filteredCoins.filter((coin) => watchlist.includes(coin.id))
    : filteredCoins;

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
      
      const mergedData = append ? [...coins, ...data] : data;
      setCoins(mergedData);
      
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

  const handleRowClick = (coinId) => {
    navigate(`/coin/${coinId}`);
  };

  const LoadingSkeleton = () => (
    <div className="market-table-wrapper">
      <div className="market-table">
        {Array.from({ length: 6 }).map((_, index) => (
          <div key={index} className="market-row skeleton-row">
            {Array.from({ length: 6 }).map((__, i) => (
              <div key={i} className="skeleton-cell" />
            ))}
          </div>
        ))}
      </div>
    </div>
  );

  const renderPriceChange = (coin) => {
    const percentage = formatPercentage(coin.price_change_percentage_24h);
    return (
      <span className={`market-change ${percentage.colorClass}`}>
        {percentage.value}
      </span>
    );
  };

  const topMovers = [...filteredCoins]
    .sort((a, b) => (b.price_change_percentage_24h || 0) - (a.price_change_percentage_24h || 0))
    .slice(0, 3)
    .map((coin) => ({
      id: coin.id,
      symbol: coin.symbol.toUpperCase(),
      change: formatPercentage(coin.price_change_percentage_24h).value,
      isUp: (coin.price_change_percentage_24h || 0) >= 0,
    }));

  const totalMarketCapSnapshot = filteredCoins.reduce((sum, coin) => sum + (coin.market_cap || 0), 0);

  const ErrorState = ({ message, onRetry }) => (
    <div className="list-state error">
      <h3>Failed to load markets</h3>
      <p>{message}</p>
      <button onClick={onRetry} className="state-btn">Try again</button>
    </div>
  );

  const EmptyState = () => (
    <div className="list-state empty">
      <h3>{showWatchlistOnly ? 'Watchlist is empty' : 'No assets found'}</h3>
      <p>
        {showWatchlistOnly
          ? 'Add coins from Markets to see them here.'
          : 'Try a different keyword in the search box.'}
      </p>
    </div>
  );

  if (loading && displayedCoins.length === 0) {
    return <LoadingSkeleton />;
  }

  if (!loading && displayedCoins.length === 0) {
    return <EmptyState />;
  }

  if (!loading && error && displayedCoins.length === 0) {
    return <ErrorState message={error} onRetry={handleRefresh} />;
  }

  return (
    <section className="coin-list-container">
      <div className="market-headline">
        <div>
          <h2>Cryptocurrency Markets</h2>
          <p>
            {showWatchlistOnly
              ? 'Your selected assets from watchlist.'
              : 'Track top assets by market capitalization and live volume.'}
          </p>
        </div>
        <div className="market-actions">
          <button type="button" className="ghost-btn">Favorites</button>
          <button type="button" className="solid-btn">
            <FaFileExport />
            Export CSV
          </button>
        </div>
      </div>

      <div className="market-overview">
        <article>
          <span>Assets shown</span>
          <strong>{displayedCoins.length}</strong>
        </article>
        <article>
          <span>Market cap snapshot</span>
          <strong>{currencySymbol}{formatNumber(showWatchlistOnly ? displayedCoins.reduce((sum, coin) => sum + (coin.market_cap || 0), 0) : totalMarketCapSnapshot)}</strong>
        </article>
        <article>
          <span>Top movers</span>
          <div className="movers-wrap">
            {(showWatchlistOnly
              ? [...displayedCoins]
                  .sort((a, b) => (b.price_change_percentage_24h || 0) - (a.price_change_percentage_24h || 0))
                  .slice(0, 3)
                  .map((coin) => ({
                    id: coin.id,
                    symbol: coin.symbol.toUpperCase(),
                    change: formatPercentage(coin.price_change_percentage_24h).value,
                    isUp: (coin.price_change_percentage_24h || 0) >= 0,
                  }))
              : topMovers).map((mover) => (
              <span key={mover.id} className={mover.isUp ? 'mover up' : 'mover down'}>
                {mover.symbol} {mover.change}
              </span>
            ))}
          </div>
        </article>
        <article>
          <span>Last sync</span>
          <strong>{lastRefresh ? lastRefresh.toLocaleTimeString() : '-'}</strong>
        </article>
      </div>

      <div className="table-toolbar">
        <button 
          onClick={handleRefresh}
          className="refresh-btn"
          disabled={loading}
          aria-label="Refresh data"
        >
          <FaSyncAlt className={loading ? 'spinning' : ''} />
          Refresh
        </button>
      </div>

      <div className="market-table-wrapper">
        <div className="market-table header-row">
          <span># Rank</span>
          <span>Name</span>
          <span>Price</span>
          <span>24h Change</span>
          <span>Market Cap</span>
          <span>Volume (24h)</span>
        </div>

        {displayedCoins.map((coin) => (
          <button key={coin.id} className="market-row" onClick={() => handleRowClick(coin.id)} type="button">
            <span className="rank-cell">{String(coin.market_cap_rank).padStart(2, '0')}</span>
            <span className="name-cell">
              <img src={coin.image} alt={coin.name} loading="lazy" />
              <span>
                <strong>{coin.name}</strong>
                <small>{coin.symbol.toUpperCase()}</small>
              </span>
            </span>
            <span className="price-cell">{formatPrice(coin.current_price, currency, currencySymbol)}</span>
            <span>{renderPriceChange(coin)}</span>
            <span>{currencySymbol}{formatNumber(coin.market_cap)}</span>
            <span>{currencySymbol}{formatNumber(coin.total_volume)}</span>
          </button>
        ))}
      </div>

      {!searchTerm && hasMore && !showWatchlistOnly && (
        <div className="load-more-container">
          <button
            onClick={handleLoadMore}
            className="load-more-btn"
            disabled={loadingMore}
          >
            {loadingMore ? 'Loading...' : 'View More Assets'}
            <FaArrowRight />
          </button>
        </div>
      )}
    </section>
  );
};

export default CoinList;