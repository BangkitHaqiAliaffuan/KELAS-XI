import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Home from './pages/Home';

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/categories" element={<div style={{ padding: '2rem' }}>Categories Page (Coming Soon)</div>} />
          <Route path="/recipes" element={<div style={{ padding: '2rem' }}>Recipes Page (Coming Soon)</div>} />
          <Route path="/categories/:slug" element={<div style={{ padding: '2rem' }}>Category Detail (Coming Soon)</div>} />
          <Route path="/recipes/:slug" element={<div style={{ padding: '2rem' }}>Recipe Detail (Coming Soon)</div>} />
        </Routes>
      </Layout>
    </Router>
  );
}

export default App
