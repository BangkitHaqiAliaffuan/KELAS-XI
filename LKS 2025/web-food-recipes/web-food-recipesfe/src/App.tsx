import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Home from './pages/Home';
import Categories from './pages/Categories';
import CategoryDetail from './pages/CategoryDetail';
import Recipes from './pages/Recipes';
import Search from './pages/Search';
import RecipeDetail from './pages/RecipeDetail';

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/categories" element={<Categories />} />
          <Route path="/recipes" element={<Recipes />} />
          <Route path="/search" element={<Search />} />
          <Route path="/categories/:slug" element={<CategoryDetail />} />
          <Route path="/recipes/:slug" element={<RecipeDetail />} />
        </Routes>
      </Layout>
    </Router>
  );
}

export default App
