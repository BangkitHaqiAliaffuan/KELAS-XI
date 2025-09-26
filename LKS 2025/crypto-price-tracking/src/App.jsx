import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { CryptoProvider } from './context/CryptoContext';
import Header from './components/Header';
import CoinList from './components/CoinList';
import CoinDetail from './components/CoinDetail';
import './App.css';

function App() {
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
