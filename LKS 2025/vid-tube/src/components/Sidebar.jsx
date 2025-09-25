import React from 'react';
import { sidebarCategories, sidebarNavItems } from '../data/staticData';
import './Sidebar.css';

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

      <div className="subscribed-list">
        <h3>Subscribed</h3>
        <div className="side-link">
          <img src="https://yt3.ggpht.com/yti/ANjgQV_wVdKF-Y6_UcFfXxMB-MgGq-E7KG4WcFUdgZhc=s88-c-k-c0x00ffffff-no-rj" alt="PewDiePie" />
          <p>PewDiePie</p>
        </div>
        <div className="side-link">
          <img src="https://yt3.ggpht.com/yti/ANjgQV_aVF8FMX6RVEQB6UxL-mJE4MZZ5aDMhpjUlVZB=s88-c-k-c0x00ffffff-no-rj" alt="MrBeast" />
          <p>MrBeast</p>
        </div>
        <div className="side-link">
          <img src="https://yt3.ggpht.com/yti/ANjgQV-NVQeUUzI8yOC8PNkRr8vgQx5YCg1oKrZVjWaF=s88-c-k-c0x00ffffff-no-rj" alt="Justin Bieber" />
          <p>Justin Bieber</p>
        </div>
        <div className="side-link">
          <img src="https://yt3.ggpht.com/yti/ANjgQV8GcpzY9cX2mLjT1QoL1B9oF5J5QqNr5NRgKCE7=s88-c-k-c0x00ffffff-no-rj" alt="5-Minute Crafts" />
          <p>5-Minute Crafts</p>
        </div>
        <div className="side-link">
          <img src="https://yt3.ggpht.com/yti/ANjgQV9vAZE8Z5qJb4d2VlV8pzY3RzC8KV4L7e6DgXhZ=s88-c-k-c0x00ffffff-no-rj" alt="Nas Daily" />
          <p>Nas Daily</p>
        </div>
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