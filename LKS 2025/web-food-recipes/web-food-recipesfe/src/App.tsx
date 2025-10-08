import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Home from './pages/Home';
import RecipeDetail from './pages/RecipeDetail';

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/categories" element={<div style={{ padding: '2rem' }}>Categories Page (Coming Soon)</div>} />
          <Route path="/recipes" element={<div style={{ padding: '2rem' }}>Recipes Page (Coming Soon)</div>} />
          <Route path="/categories/:slug" element={<div style={{ padding: '2rem' }}>Category Detail (Coming Soon)</div>} />
          <Route path="/recipes/:slug" element={<RecipeDetail />} />
        </Routes>
      </Layout>
    </Router>
  );
}

export default App
