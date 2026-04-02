import { useEffect, useState } from 'react';
import { BrowserRouter as Router, NavLink, Route, Routes, useLocation } from 'react-router-dom';
import { FaCoins, FaStar } from 'react-icons/fa';
import { CryptoProvider } from './context/CryptoContext';
import Header from './components/Header';
import CoinList from './components/CoinList';
import CoinDetail from './components/CoinDetail';
import PageLoader from './components/PageLoader';
import './App.css';

const DashboardShell = () => {
  const location = useLocation();
  const isDetailPage = location.pathname.startsWith('/coin/');

  return (
    <div className="app-shell">
      <aside className="side-nav">
        <div className="brand-block">
          <h1>Obsidian Ledger</h1>
          <p>Premium Crypto Intelligence</p>
        </div>

        <nav className="menu-list">
          <NavLink to="/" className="menu-item">
            <FaCoins />
            Markets
          </NavLink>
          <NavLink to="/watchlist" className="menu-item">
            <FaStar />
            Watchlist
          </NavLink>
        </nav>

        <div className="side-note">
          <p className="side-note-title">Live Data</p>
          <p>Powered by CoinGecko API</p>
          {isDetailPage ? <span>Viewing coin analytics</span> : <span>Tracking market movement</span>}
        </div>
      </aside>

      <main className="workspace">
        <Header />
        <section className="workspace-content">
          <Routes>
            <Route path="/" element={<CoinList />} />
            <Route path="/watchlist" element={<CoinList showWatchlistOnly />} />
            <Route path="/coin/:coinId" element={<CoinDetail />} />
          </Routes>
        </section>
      </main>
    </div>
  );
};

function App() {
  const [isPageLoading, setIsPageLoading] = useState(true);

  useEffect(() => {
    const timerId = setTimeout(() => {
      setIsPageLoading(false);
    }, 1600);

    return () => {
      clearTimeout(timerId);
    };
  }, []);

  if (isPageLoading) {
    return <PageLoader />;
  }

  return (
    <CryptoProvider>
      <Router>
        <DashboardShell />
      </Router>
    </CryptoProvider>
  );
}

export default App;
