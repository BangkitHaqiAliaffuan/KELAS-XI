import React, { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import Feed from '../components/Feed';
import './Home.css';

const Home = ({ sidebar, setSidebar, searchQuery, setSearchQuery }) => {
  const [category, setCategory] = useState('0'); // '0' represents 'All' category

  // Reset category when searching
  useEffect(() => {
    if (searchQuery) {
      setCategory('0');
    }
  }, [searchQuery]);

  const handleCategoryChange = (newCategory) => {
    setCategory(newCategory);
    if (searchQuery) {
      setSearchQuery(''); // Clear search when changing category
    }
  };

  return (
    <div className="home">
      <Sidebar 
        sidebar={sidebar} 
        category={category} 
        setCategory={handleCategoryChange}
      />
      <div className={`container ${sidebar ? '' : 'large-container'}`}>
        <Feed 
          category={category} 
          searchQuery={searchQuery}
        />
      </div>
    </div>
  );
};

export default Home;