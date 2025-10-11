import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useCategories } from '../hooks/useApi';
import type { Category } from '../types';

const Categories: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const { categories, loading, error } = useCategories();
  
  const [searchQuery, setSearchQuery] = useState(searchParams.get('search') || '');
  const [sortBy, setSortBy] = useState(searchParams.get('sort') || 'name_asc');
  const [filteredCategories, setFilteredCategories] = useState<Category[]>([]);

  useEffect(() => {
    let filtered = categories;

    // Filter by search query
    if (searchQuery) {
      filtered = filtered.filter(category =>
        category.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (category.description && category.description.toLowerCase().includes(searchQuery.toLowerCase()))
      );
    }

    // Sort categories
    switch (sortBy) {
      case 'name_asc':
        filtered.sort((a, b) => a.name.localeCompare(b.name));
        break;
      case 'name_desc':
        filtered.sort((a, b) => b.name.localeCompare(a.name));
        break;
      case 'recipes_asc':
        filtered.sort((a, b) => (a.recipes_count || 0) - (b.recipes_count || 0));
        break;
      case 'recipes_desc':
        filtered.sort((a, b) => (b.recipes_count || 0) - (a.recipes_count || 0));
        break;
      default:
        filtered.sort((a, b) => a.name.localeCompare(b.name));
    }

    setFilteredCategories(filtered);
  }, [categories, searchQuery, sortBy]);

  useEffect(() => {
    // Update URL params
    const params = new URLSearchParams();
    if (searchQuery) params.set('search', searchQuery);
    if (sortBy !== 'name_asc') params.set('sort', sortBy);
    setSearchParams(params);
  }, [searchQuery, sortBy, setSearchParams]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
  };

  const clearSearch = () => {
    setSearchQuery('');
    setSortBy('name_asc');
  };

  const getCategoryIcon = (name: string) => {
    const iconMap: Record<string, string> = {
      'appetizer': '🥗',
      'main course': '🍽️',
      'main-course': '🍽️',
      'dessert': '🍰',
      'breakfast': '🍳',
      'lunch': '🥙',
      'dinner': '🍖',
      'snack': '🍪',
      'drink': '🥤',
      'beverage': '🥤',
      'soup': '🍲',
      'salad': '🥗',
      'pasta': '🍝',
      'pizza': '🍕',
      'burger': '🍔',
      'seafood': '🐟',
      'meat': '🥩',
      'vegetarian': '🥬',
      'vegan': '🌱',
      'healthy': '🥗',
      'traditional': '🏠',
      'international': '🌍',
      'asian': '🥢',
      'italian': '🇮🇹',
      'chinese': '🥡',
      'japanese': '🍣',
      'mexican': '🌮',
      'indian': '🍛',
    };

    const key = name.toLowerCase().replace(/\s+/g, '-');
    return iconMap[key] || iconMap[name.toLowerCase()] || '🍽️';
  };

  const getCategoryGradient = (index: number) => {
    const gradients = [
      'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
      'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
      'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
      'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
      'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
      'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)',
      'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)',
      'linear-gradient(135deg, #ff8177 0%, #ff867a 0%, #ff8c7f 21%, #f99185 52%, #cf556c 78%, #b12a5b 100%)',
      'linear-gradient(135deg, #3bb2b8 0%, #42e695 100%)',
    ];
    return gradients[index % gradients.length];
  };

  if (error) {
    return (
      <div style={{ 
        minHeight: '50vh', 
        display: 'flex', 
        alignItems: 'center', 
        justifyContent: 'center',
        flexDirection: 'column',
        color: 'var(--color-gray-600)'
      }}>
        <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>❌</div>
        <h3 style={{ fontSize: '1.5rem', marginBottom: '0.5rem' }}>Error Loading Categories</h3>
        <p>{error}</p>
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: 'var(--color-gray-50)' }}>
      {/* Page Header */}
      <section 
        style={{ 
          background: 'linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%)',
          color: 'white',
          padding: '4rem 0 3rem',
          position: 'relative',
          overflow: 'hidden'
        }}
      >
        {/* Background Pattern */}
        <div style={{
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          opacity: 0.1,
          backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Ccircle cx='7' cy='7' r='7'/%3E%3Ccircle cx='53' cy='7' r='7'/%3E%3Ccircle cx='7' cy='53' r='7'/%3E%3Ccircle cx='53' cy='53' r='7'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
        }} />
        
        <div className="container" style={{ position: 'relative', zIndex: 1 }}>
          <div className="text-center">
            <h1 style={{ 
              fontSize: '3rem', 
              fontWeight: 'bold', 
              marginBottom: '1rem',
              textShadow: '0 2px 4px rgba(0,0,0,0.1)'
            }}>
              Recipe Categories
            </h1>
            <p style={{ 
              fontSize: '1.25rem', 
              opacity: 0.9, 
              marginBottom: '2rem',
              maxWidth: '600px',
              margin: '0 auto 2rem'
            }}>
              Explore our diverse collection of recipe categories and discover your next favorite dish
            </p>
            
            {/* Stats */}
            <div style={{ 
              display: 'flex', 
              justifyContent: 'center', 
              gap: '2rem',
              flexWrap: 'wrap'
            }}>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '2rem', fontWeight: 'bold' }}>{categories.length}</div>
                <div style={{ fontSize: '0.875rem', opacity: 0.9 }}>Categories</div>
              </div>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '2rem', fontWeight: 'bold' }}>
                  {categories.reduce((total, cat) => total + (cat.recipes_count || 0), 0)}
                </div>
                <div style={{ fontSize: '0.875rem', opacity: 0.9 }}>Total Recipes</div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Search and Filter Section */}
      <section style={{ 
        backgroundColor: 'white', 
        padding: '2rem 0', 
        borderBottom: '1px solid var(--color-gray-200)',
        boxShadow: '0 2px 4px rgba(0,0,0,0.05)'
      }}>
        <div className="container">
          <form onSubmit={handleSearch} style={{ marginBottom: '1.5rem' }}>
            <div style={{ 
              display: 'flex', 
              gap: '1rem', 
              alignItems: 'center', 
              flexWrap: 'wrap',
              justifyContent: 'center'
            }}>
              <div style={{ flex: 1, minWidth: '300px', maxWidth: '500px' }}>
                <input
                  type="text"
                  placeholder="Search categories..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '0.875rem 1rem',
                    border: '2px solid var(--color-gray-200)',
                    borderRadius: 'var(--radius-lg)',
                    fontSize: '1rem',
                    transition: 'border-color 0.2s ease',
                    outline: 'none'
                  }}
                  onFocus={(e) => {
                    e.target.style.borderColor = 'var(--color-primary)';
                  }}
                  onBlur={(e) => {
                    e.target.style.borderColor = 'var(--color-gray-200)';
                  }}
                />
              </div>
              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
                style={{
                  padding: '0.875rem 1rem',
                  border: '2px solid var(--color-gray-200)',
                  borderRadius: 'var(--radius-lg)',
                  backgroundColor: 'white',
                  fontSize: '1rem',
                  minWidth: '150px'
                }}
              >
                <option value="name_asc">Name A-Z</option>
                <option value="name_desc">Name Z-A</option>
                <option value="recipes_desc">Most Recipes</option>
                <option value="recipes_asc">Least Recipes</option>
              </select>
              {(searchQuery || sortBy !== 'name_asc') && (
                <button
                  type="button"
                  onClick={clearSearch}
                  style={{
                    padding: '0.875rem 1.5rem',
                    backgroundColor: 'var(--color-gray-100)',
                    color: 'var(--color-gray-600)',
                    border: 'none',
                    borderRadius: 'var(--radius-lg)',
                    cursor: 'pointer',
                    fontSize: '1rem',
                    transition: 'background-color 0.2s ease'
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.backgroundColor = 'var(--color-gray-200)';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.backgroundColor = 'var(--color-gray-100)';
                  }}
                >
                  Clear
                </button>
              )}
            </div>
          </form>

          {/* Results count */}
          <div style={{ textAlign: 'center' }}>
            <p style={{ color: 'var(--color-gray-600)' }}>
              {filteredCategories.length} {filteredCategories.length === 1 ? 'category' : 'categories'} found
            </p>
          </div>
        </div>
      </section>

      {/* Categories Grid */}
      <section style={{ padding: '3rem 0' }}>
        <div className="container">
          {loading ? (
            <div className="text-center" style={{ padding: '4rem 0' }}>
              <div style={{
                display: 'inline-block',
                width: '3rem',
                height: '3rem',
                border: '4px solid var(--color-gray-300)',
                borderTop: '4px solid var(--color-primary)',
                borderRadius: '50%',
                animation: 'spin 1s linear infinite'
              }}></div>
              <p style={{ marginTop: '1rem', color: 'var(--color-gray-600)' }}>Loading categories...</p>
            </div>
          ) : filteredCategories.length === 0 ? (
            <div className="text-center" style={{ padding: '4rem 0' }}>
              <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>🔍</div>
              <h3 style={{ fontSize: '1.5rem', marginBottom: '1rem', color: 'var(--color-gray-800)' }}>
                No categories found
              </h3>
              <p style={{ color: 'var(--color-gray-600)', marginBottom: '2rem' }}>
                Try adjusting your search criteria or browse all categories.
              </p>
              <button
                onClick={clearSearch}
                className="btn btn-primary"
              >
                Show All Categories
              </button>
            </div>
          ) : (
            <div className="grid sm-grid-cols-2 md-grid-cols-3 lg-grid-cols-4" style={{ gap: '2rem' }}>
              {filteredCategories.map((category, index) => (
                <div 
                  key={category.id} 
                  className="card"
                  onClick={() => navigate(`/categories/${category.slug}`)}
                  style={{ 
                    cursor: 'pointer',
                    overflow: 'hidden',
                    position: 'relative',
                    transition: 'all 0.3s ease',
                    border: 'none',
                    borderRadius: 'var(--radius-xl)'
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.transform = 'translateY(-8px) scale(1.02)';
                    e.currentTarget.style.boxShadow = '0 20px 40px rgba(0,0,0,0.15)';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.transform = 'translateY(0) scale(1)';
                    e.currentTarget.style.boxShadow = '0 4px 20px rgba(0,0,0,0.08)';
                  }}
                >
                  {/* Category Header with Gradient */}
                  <div 
                    style={{ 
                      background: getCategoryGradient(index),
                      height: '8rem',
                      display: 'flex',
                      flexDirection: 'column',
                      alignItems: 'center',
                      justifyContent: 'center',
                      color: 'white',
                      position: 'relative',
                      overflow: 'hidden'
                    }}
                  >
                    {/* Background Pattern */}
                    <div style={{
                      position: 'absolute',
                      top: 0,
                      left: 0,
                      right: 0,
                      bottom: 0,
                      opacity: 0.2,
                      backgroundImage: `url("data:image/svg+xml,%3Csvg width='40' height='40' viewBox='0 0 40 40' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Cpath d='m0 40l40-40h-40v40zm40 0v-40h-40l40 40z'/%3E%3C/g%3E%3C/svg%3E")`,
                    }} />
                    
                    <div style={{ 
                      fontSize: '2.5rem', 
                      marginBottom: '0.5rem',
                      position: 'relative',
                      zIndex: 1
                    }}>
                      {getCategoryIcon(category.name)}
                    </div>
                    
                    {/* Recipe count badge */}
                    <div style={{
                      position: 'absolute',
                      top: '1rem',
                      right: '1rem',
                      backgroundColor: 'rgba(255,255,255,0.2)',
                      backdropFilter: 'blur(10px)',
                      color: 'white',
                      padding: '0.25rem 0.75rem',
                      borderRadius: 'var(--radius-full)',
                      fontSize: '0.75rem',
                      fontWeight: '600'
                    }}>
                      {category.recipes_count || 0} recipes
                    </div>
                  </div>
                  
                  {/* Category Content */}
                  <div style={{ padding: '1.5rem' }}>
                    <h3 style={{ 
                      fontSize: '1.25rem', 
                      fontWeight: '700', 
                      marginBottom: '0.75rem',
                      color: 'var(--color-gray-900)',
                      textAlign: 'center'
                    }}>
                      {category.name}
                    </h3>
                    
                    {category.description && (
                      <p style={{ 
                        color: 'var(--color-gray-600)', 
                        fontSize: '0.875rem', 
                        lineHeight: '1.5',
                        textAlign: 'center',
                        marginBottom: '1rem',
                        display: '-webkit-box',
                        WebkitLineClamp: 3,
                        WebkitBoxOrient: 'vertical',
                        overflow: 'hidden'
                      }}>
                        {category.description}
                      </p>
                    )}
                    
                    {/* Action Button */}
                    <div style={{ textAlign: 'center', marginTop: 'auto' }}>
                      <button
                        className="btn btn-primary"
                        style={{
                          width: '100%',
                          padding: '0.75rem',
                          fontSize: '0.875rem',
                          fontWeight: '600',
                          border: 'none',
                          borderRadius: 'var(--radius-lg)',
                          transition: 'all 0.2s ease'
                        }}
                        onClick={(e) => {
                          e.stopPropagation();
                          navigate(`/categories/${category.slug}`);
                        }}
                      >
                        Explore Recipes
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </section>

      {/* Popular Categories Section */}
      {!loading && filteredCategories.length > 0 && !searchQuery && (
        <section style={{ 
          backgroundColor: 'white', 
          padding: '3rem 0',
          borderTop: '1px solid var(--color-gray-200)'
        }}>
          <div className="container">
            <h2 style={{ 
              fontSize: '2rem', 
              fontWeight: 'bold', 
              textAlign: 'center', 
              marginBottom: '2rem',
              color: 'var(--color-gray-900)'
            }}>
              Popular Categories
            </h2>
            
            <div className="grid sm-grid-cols-2 md-grid-cols-4" style={{ gap: '1.5rem' }}>
              {filteredCategories
                .sort((a, b) => (b.recipes_count || 0) - (a.recipes_count || 0))
                .slice(0, 4)
                .map((category) => (
                  <div 
                    key={`popular-${category.id}`}
                    onClick={() => navigate(`/categories/${category.slug}`)}
                    style={{
                      padding: '1.5rem',
                      backgroundColor: 'var(--color-gray-50)',
                      borderRadius: 'var(--radius-lg)',
                      textAlign: 'center',
                      cursor: 'pointer',
                      transition: 'all 0.2s ease',
                      border: '2px solid transparent'
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.backgroundColor = 'var(--color-primary-light)';
                      e.currentTarget.style.borderColor = 'var(--color-primary)';
                      e.currentTarget.style.transform = 'translateY(-2px)';
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.backgroundColor = 'var(--color-gray-50)';
                      e.currentTarget.style.borderColor = 'transparent';
                      e.currentTarget.style.transform = 'translateY(0)';
                    }}
                  >
                    <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>
                      {getCategoryIcon(category.name)}
                    </div>
                    <h4 style={{ 
                      fontSize: '1rem', 
                      fontWeight: '600', 
                      marginBottom: '0.25rem',
                      color: 'var(--color-gray-900)'
                    }}>
                      {category.name}
                    </h4>
                    <p style={{ 
                      fontSize: '0.75rem', 
                      color: 'var(--color-gray-600)' 
                    }}>
                      {category.recipes_count || 0} recipes
                    </p>
                  </div>
                ))}
            </div>
          </div>
        </section>
      )}
    </div>
  );
};

export default Categories;