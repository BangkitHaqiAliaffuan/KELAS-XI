import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/search?q=${encodeURIComponent(searchQuery.trim())}`);
      setSearchQuery('');
    }
  };

  return (
    <div style={{ minHeight: '100vh' }} className="bg-gray-50">
      {/* Header/Navbar */}
      <header className="bg-white" style={{ boxShadow: 'var(--shadow)', borderBottom: '1px solid var(--color-gray-200)' }}>
        <div className="container">
          <div className="flex justify-between items-center" style={{ height: '4rem' }}>
            {/* Logo */}
            <div>
              <h1 className="text-2xl font-bold text-primary">
                Food Recipes
              </h1>
            </div>

            {/* Navigation */}
            <nav className="flex" style={{ gap: '2rem' }}>
              <button
                onClick={() => navigate('/')}
                className="text-gray-700"
                style={{ 
                  padding: '0.5rem 0.75rem', 
                  background: 'none',
                  border: 'none',
                  cursor: 'pointer',
                  fontSize: '0.875rem',
                  fontWeight: '500',
                  transition: 'color 0.2s ease-in-out'
                }}
                onMouseEnter={(e) => e.currentTarget.style.color = 'var(--color-primary)'}
                onMouseLeave={(e) => e.currentTarget.style.color = 'var(--color-gray-700)'}
              >
                Home
              </button>
              <button
                onClick={() => navigate('/categories')}
                className="text-gray-700"
                style={{ 
                  padding: '0.5rem 0.75rem', 
                  background: 'none',
                  border: 'none',
                  cursor: 'pointer',
                  fontSize: '0.875rem',
                  fontWeight: '500',
                  transition: 'color 0.2s ease-in-out'
                }}
                onMouseEnter={(e) => e.currentTarget.style.color = 'var(--color-primary)'}
                onMouseLeave={(e) => e.currentTarget.style.color = 'var(--color-gray-700)'}
              >
                Categories
              </button>
              <button
                onClick={() => navigate('/recipes')}
                className="text-gray-700"
                style={{ 
                  padding: '0.5rem 0.75rem', 
                  background: 'none',
                  border: 'none',
                  cursor: 'pointer',
                  fontSize: '0.875rem',
                  fontWeight: '500',
                  transition: 'color 0.2s ease-in-out'
                }}
                onMouseEnter={(e) => e.currentTarget.style.color = 'var(--color-primary)'}
                onMouseLeave={(e) => e.currentTarget.style.color = 'var(--color-gray-700)'}
              >
                Recipes
              </button>
            </nav>

            {/* Search */}
            <div className="flex items-center">
              <form onSubmit={handleSearchSubmit} style={{ position: 'relative' }}>
                <input
                  type="text"
                  placeholder="Search recipes..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="input"
                  style={{ width: '16rem', paddingRight: '3rem' }}
                />
                <button
                  type="submit"
                  style={{ 
                    position: 'absolute', 
                    top: '50%', 
                    right: '0.75rem', 
                    transform: 'translateY(-50%)',
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    padding: '0.25rem'
                  }}
                >
                  <svg
                    style={{ width: '1.25rem', height: '1.25rem', color: 'var(--color-gray-400)' }}
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                    />
                  </svg>
                </button>
              </form>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main>{children}</main>

      {/* Footer */}
      <footer className="bg-gray-800 text-white" style={{ marginTop: '3rem' }}>
        <div className="container" style={{ padding: '3rem 1rem' }}>
          <div className="grid md-grid-cols-4" style={{ gap: '2rem' }}>
            <div style={{ gridColumn: 'span 2' }}>
              <h3 className="text-xl font-bold mb-4">Food Recipes</h3>
              <p style={{ color: '#cbd5e1', marginBottom: '1rem', lineHeight: '1.6' }}>
                Discover amazing recipes from around the world. Cook delicious meals 
                with step-by-step instructions and helpful tips from our expert chefs.
              </p>
            </div>
            <div>
              <h4 className="text-lg font-semibold mb-4">Quick Links</h4>
              <ul style={{ listStyle: 'none', padding: 0 }}>
                <li style={{ marginBottom: '0.5rem' }}>
                  <a 
                    href="/" 
                    style={{ 
                      color: '#cbd5e1', 
                      textDecoration: 'none',
                      transition: 'color 0.2s ease-in-out'
                    }}
                    onMouseEnter={(e) => e.currentTarget.style.color = 'white'}
                    onMouseLeave={(e) => e.currentTarget.style.color = '#cbd5e1'}
                  >
                    Home
                  </a>
                </li>
                <li style={{ marginBottom: '0.5rem' }}>
                  <a 
                    href="/categories" 
                    style={{ 
                      color: '#cbd5e1', 
                      textDecoration: 'none',
                      transition: 'color 0.2s ease-in-out'
                    }}
                    onMouseEnter={(e) => e.currentTarget.style.color = 'white'}
                    onMouseLeave={(e) => e.currentTarget.style.color = '#cbd5e1'}
                  >
                    Categories
                  </a>
                </li>
                <li style={{ marginBottom: '0.5rem' }}>
                  <a 
                    href="/recipes" 
                    style={{ 
                      color: '#cbd5e1', 
                      textDecoration: 'none',
                      transition: 'color 0.2s ease-in-out'
                    }}
                    onMouseEnter={(e) => e.currentTarget.style.color = 'white'}
                    onMouseLeave={(e) => e.currentTarget.style.color = '#cbd5e1'}
                  >
                    All Recipes
                  </a>
                </li>
              </ul>
            </div>
            <div>
              <h4 className="text-lg font-semibold mb-4">Categories</h4>
              <ul style={{ listStyle: 'none', padding: 0 }}>
                <li style={{ marginBottom: '0.5rem' }}>
                  <a 
                    href="/categories/main-course" 
                    style={{ 
                      color: '#cbd5e1', 
                      textDecoration: 'none',
                      transition: 'color 0.2s ease-in-out'
                    }}
                    onMouseEnter={(e) => e.currentTarget.style.color = 'white'}
                    onMouseLeave={(e) => e.currentTarget.style.color = '#cbd5e1'}
                  >
                    Main Course
                  </a>
                </li>
                <li style={{ marginBottom: '0.5rem' }}>
                  <a 
                    href="/categories/desserts" 
                    style={{ 
                      color: '#cbd5e1', 
                      textDecoration: 'none',
                      transition: 'color 0.2s ease-in-out'
                    }}
                    onMouseEnter={(e) => e.currentTarget.style.color = 'white'}
                    onMouseLeave={(e) => e.currentTarget.style.color = '#cbd5e1'}
                  >
                    Desserts
                  </a>
                </li>
                <li style={{ marginBottom: '0.5rem' }}>
                  <a 
                    href="/categories/appetizers" 
                    style={{ 
                      color: '#cbd5e1', 
                      textDecoration: 'none',
                      transition: 'color 0.2s ease-in-out'
                    }}
                    onMouseEnter={(e) => e.currentTarget.style.color = 'white'}
                    onMouseLeave={(e) => e.currentTarget.style.color = '#cbd5e1'}
                  >
                    Appetizers
                  </a>
                </li>
              </ul>
            </div>
          </div>
          <div 
            style={{ 
              borderTop: '1px solid #475569', 
              marginTop: '2rem', 
              paddingTop: '2rem', 
              textAlign: 'center',
              color: '#cbd5e1'
            }}
          >
            <p>&copy; 2025 Food Recipes. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Layout;