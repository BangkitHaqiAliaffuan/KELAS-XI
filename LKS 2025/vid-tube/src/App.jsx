import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Video from './pages/Video';
import './App.css';

function App() {
  const [sidebar, setSidebar] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearch = (query) => {
    setSearchQuery(query);
  };

  return (
    <Router>
      <div className="App">
        <Navbar setSidebar={setSidebar} onSearch={handleSearch} />
        <Routes>
          <Route 
            path="/" 
            element={
              <Home 
                sidebar={sidebar} 
                setSidebar={setSidebar}
                searchQuery={searchQuery}
                setSearchQuery={setSearchQuery}
              />
            } 
          />
          <Route path="/video/:videoId" element={<Video />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
