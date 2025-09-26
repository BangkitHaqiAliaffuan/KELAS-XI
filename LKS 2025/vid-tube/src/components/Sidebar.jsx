import React from 'react';
import { sidebarCategories, sidebarNavItems } from '../data/staticData';
import './Sidebar.css';
import assets from '../assets/assets';

const Sidebar = ({ sidebar, category, setCategory }) => {
  return (
    <div className={`sidebar ${sidebar ? '' : 'small-sidebar'}`}>
      <div className="shortcut-links">
        {sidebarNavItems.map((item, index) => (
          <div key={index} className="side-link">
            <img src={item.icon} alt={item.name} />
            <p>{item.name}</p>
          </div>
        ))}
        <hr />
      </div>

      
      <div className="category-list">
        <h3>Explore</h3>
        {sidebarCategories.map((item) => (
          <div 
            key={item.id}
            className={`side-link ${category === item.id ? 'active' : ''}`}
            onClick={() => setCategory(item.id)}
          >
            <img src={item.icon} alt={item.name} />
            <p>{item.name}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Sidebar;