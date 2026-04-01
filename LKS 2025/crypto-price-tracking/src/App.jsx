import { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { CryptoProvider } from './context/CryptoContext';
import Header from './components/Header';
import CoinList from './components/CoinList';
import CoinDetail from './components/CoinDetail';
import PageLoader from './components/PageLoader';
import './App.css';

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
        <div className="app">
          <Header />
          <main className="main-content">
            <div className="container">
              <Routes>
                <Route path="/" element={<CoinList />} />
                <Route path="/coin/:coinId" element={<CoinDetail />} />
              </Routes>
            </div>
          </main>
        </div>
      </Router>
    </CryptoProvider>
  );
}

export default App;
