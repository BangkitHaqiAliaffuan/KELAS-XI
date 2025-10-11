import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import type { Recipe } from '../types';

const Search: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [searchQuery, setSearchQuery] = useState(searchParams.get('q') || '');
  const [totalResults, setTotalResults] = useState(0);

  const fetchSearchResults = async (query: string, page: number = 1) => {
    if (!query.trim()) {
      setRecipes([]);
      setTotalResults(0);
      setTotalPages(1);
      return;
    }

    setLoading(true);
    try {
      const params = new URLSearchParams();
      params.append('q', query);
      params.append('page', page.toString());
      params.append('per_page', '12');

      const response = await fetch(`http://localhost:8000/api/search?${params}`, {
        headers: {
          'Accept': 'application/json',
          'X-API-Key': 'food_recipes_secure_api_key_2025'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      if (data.success) {
        setRecipes(data.data || []);
        setCurrentPage(data.meta?.current_page || 1);
        setTotalPages(data.meta?.last_page || 1);
        setTotalResults(data.meta?.total || 0);
      } else {
        throw new Error(data.message || 'Failed to search recipes');
      }
    } catch (error) {
      console.error('Error searching recipes:', error);
      setRecipes([]);
      setTotalResults(0);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const query = searchParams.get('q') || '';
    setSearchQuery(query);
    if (query) {
      fetchSearchResults(query, currentPage);
    }
  }, [searchParams, currentPage]);

  useEffect(() => {
    // Update URL params
    const params = new URLSearchParams();
    if (searchQuery) params.set('q', searchQuery);
    if (currentPage > 1) params.set('page', currentPage.toString());
    setSearchParams(params);
  }, [searchQuery, currentPage, setSearchParams]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setCurrentPage(1);
    if (searchQuery.trim()) {
      fetchSearchResults(searchQuery.trim(), 1);
    }
  };

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case 'easy': return '#10b981';
      case 'medium': return '#f59e0b';
      case 'hard': return '#ef4444';
      default: return '#6b7280';
    }
  };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: 'var(--color-gray-50)' }}>
      {/* Search Header */}
      <section 
        style={{ 
          backgroundColor: 'white',
          padding: '2rem 0',
          borderBottom: '1px solid var(--color-gray-200)'
        }}
      >
        <div className="container">
          <div style={{ maxWidth: '600px', margin: '0 auto', textAlign: 'center' }}>
            <h1 style={{ 
              fontSize: '2rem', 
              fontWeight: 'bold', 
              marginBottom: '1rem',
              color: 'var(--color-gray-900)'
            }}>
              Search Recipes
            </h1>
            <p style={{ 
              color: 'var(--color-gray-600)', 
              marginBottom: '2rem' 
            }}>
              Find your perfect recipe from our collection
            </p>
            
            <form onSubmit={handleSearch}>
              <div style={{ 
                display: 'flex', 
                gap: '0.5rem',
                maxWidth: '500px',
                margin: '0 auto'
              }}>
                <input
                  type="text"
                  placeholder="Search for recipes, ingredients, or categories..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  style={{
                    flex: 1,
                    padding: '1rem',
                    border: '2px solid var(--color-gray-300)',
                    borderRadius: 'var(--radius-lg)',
                    fontSize: '1rem',
                    outline: 'none',
                    transition: 'border-color 0.2s ease'
                  }}
                  onFocus={(e) => {
                    e.target.style.borderColor = 'var(--color-primary)';
                  }}
                  onBlur={(e) => {
                    e.target.style.borderColor = 'var(--color-gray-300)';
                  }}
                />
                <button
                  type="submit"
                  className="btn btn-primary"
                  style={{ 
                    padding: '1rem 2rem',
                    fontSize: '1rem',
                    whiteSpace: 'nowrap'
                  }}
                >
                  Search
                </button>
              </div>
            </form>
          </div>
        </div>
      </section>

      {/* Search Results */}
      <section style={{ padding: '3rem 0' }}>
        <div className="container">
          {searchQuery && (
            <div style={{ marginBottom: '2rem' }}>
              <h2 style={{ 
                fontSize: '1.5rem', 
                fontWeight: '600', 
                marginBottom: '0.5rem',
                color: 'var(--color-gray-900)'
              }}>
                Search Results for "{searchQuery}"
              </h2>
              <p style={{ color: 'var(--color-gray-600)' }}>
                {loading ? 'Searching...' : `${totalResults} recipes found`}
              </p>
            </div>
          )}

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
              <p style={{ marginTop: '1rem', color: 'var(--color-gray-600)' }}>Searching recipes...</p>
            </div>
          ) : !searchQuery ? (
            <div className="text-center" style={{ padding: '4rem 0' }}>
              <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>🔍</div>
              <h3 style={{ fontSize: '1.5rem', marginBottom: '1rem', color: 'var(--color-gray-800)' }}>
                Start Your Recipe Search
              </h3>
              <p style={{ color: 'var(--color-gray-600)', marginBottom: '2rem' }}>
                Enter keywords to search for recipes, ingredients, or categories
              </p>
            </div>
          ) : recipes.length === 0 ? (
            <div className="text-center" style={{ padding: '4rem 0' }}>
              <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>😔</div>
              <h3 style={{ fontSize: '1.5rem', marginBottom: '1rem', color: 'var(--color-gray-800)' }}>
                No recipes found
              </h3>
              <p style={{ color: 'var(--color-gray-600)', marginBottom: '2rem' }}>
                Try different keywords or browse our categories
              </p>
              <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center', flexWrap: 'wrap' }}>
                <button
                  onClick={() => navigate('/recipes')}
                  className="btn btn-primary"
                >
                  Browse All Recipes
                </button>
                <button
                  onClick={() => navigate('/categories')}
                  className="btn"
                  style={{
                    backgroundColor: 'var(--color-gray-100)',
                    color: 'var(--color-gray-700)',
                    border: 'none'
                  }}
                >
                  Browse Categories
                </button>
              </div>
            </div>
          ) : (
            <>
              <div className="grid sm-grid-cols-2 md-grid-cols-3 lg-grid-cols-4" style={{ gap: '1.5rem' }}>
                {recipes.map((recipe: Recipe) => (
                  <div 
                    key={recipe.id} 
                    className="card"
                    onClick={() => navigate(`/recipes/${recipe.slug}`)}
                    style={{ 
                      overflow: 'hidden', 
                      cursor: 'pointer',
                      transition: 'transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out'
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.transform = 'translateY(-4px)';
                      e.currentTarget.style.boxShadow = '0 10px 25px rgba(0,0,0,0.1)';
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.transform = 'translateY(0)';
                      e.currentTarget.style.boxShadow = '0 2px 8px rgba(0,0,0,0.1)';
                    }}
                  >
                    <div 
                      style={{ 
                        height: '12rem', 
                        backgroundColor: 'var(--color-gray-200)',
                        position: 'relative',
                        overflow: 'hidden'
                      }}
                    >
                      {recipe.recipe_photos?.[0]?.photo_url || recipe.thumbnail ? (
                        <img 
                          src={recipe.recipe_photos?.[0]?.photo_url || recipe.thumbnail} 
                          alt={recipe.name}
                          style={{ 
                            width: '100%', 
                            height: '100%', 
                            objectFit: 'cover'
                          }}
                        />
                      ) : (
                        <div style={{ 
                          display: 'flex', 
                          alignItems: 'center', 
                          justifyContent: 'center', 
                          height: '100%',
                          fontSize: '3rem'
                        }}>
                          🍳
                        </div>
                      )}
                      
                      {/* Category Badge */}
                      {recipe.category && (
                        <div style={{
                          position: 'absolute',
                          top: '0.75rem',
                          left: '0.75rem',
                          backgroundColor: 'var(--color-primary)',
                          color: 'white',
                          padding: '0.25rem 0.75rem',
                          borderRadius: 'var(--radius)',
                          fontSize: '0.75rem',
                          fontWeight: '600'
                        }}>
                          {recipe.category.name}
                        </div>
                      )}

                      {/* Difficulty Badge */}
                      <div style={{
                        position: 'absolute',
                        top: '0.75rem',
                        right: '0.75rem',
                        backgroundColor: getDifficultyColor(recipe.difficulty || 'medium'),
                        color: 'white',
                        padding: '0.25rem 0.75rem',
                        borderRadius: 'var(--radius)',
                        fontSize: '0.75rem',
                        fontWeight: '600',
                        textTransform: 'capitalize'
                      }}>
                        {recipe.difficulty || 'Medium'}
                      </div>
                    </div>
                    
                    <div style={{ padding: '1.5rem' }}>
                      <h3 style={{ 
                        fontSize: '1.125rem', 
                        fontWeight: '600', 
                        marginBottom: '0.5rem',
                        color: 'var(--color-gray-900)',
                        lineHeight: '1.4'
                      }}>
                        {recipe.name}
                      </h3>
                      
                      <p style={{ 
                        color: 'var(--color-gray-600)', 
                        fontSize: '0.875rem', 
                        marginBottom: '1rem',
                        lineHeight: '1.5',
                        display: '-webkit-box',
                        WebkitLineClamp: 2,
                        WebkitBoxOrient: 'vertical',
                        overflow: 'hidden'
                      }}>
                        {recipe.description}
                      </p>
                      
                      <div style={{ 
                        display: 'flex', 
                        justifyContent: 'space-between', 
                        alignItems: 'center',
                        fontSize: '0.75rem',
                        color: 'var(--color-gray-500)',
                        marginBottom: '1rem'
                      }}>
                        <span>⏱️ {recipe.cooking_time || 'N/A'} min</span>
                        <span>👥 {recipe.servings || 'N/A'} servings</span>
                      </div>

                      {/* Author */}
                      {recipe.recipe_author && (
                        <div style={{ 
                          display: 'flex', 
                          alignItems: 'center', 
                          gap: '0.5rem',
                          paddingTop: '1rem',
                          borderTop: '1px solid var(--color-gray-200)'
                        }}>
                          <img 
                            src={recipe.recipe_author.photo || '/default-avatar.jpg'} 
                            alt={recipe.recipe_author.name}
                            style={{
                              width: '2rem',
                              height: '2rem',
                              borderRadius: '50%',
                              objectFit: 'cover'
                            }}
                          />
                          <span style={{ 
                            fontSize: '0.875rem', 
                            color: 'var(--color-gray-700)',
                            fontWeight: '500'
                          }}>
                            {recipe.recipe_author.name}
                          </span>
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div style={{ 
                  display: 'flex', 
                  justifyContent: 'center', 
                  alignItems: 'center',
                  gap: '0.5rem',
                  marginTop: '3rem'
                }}>
                  <button
                    onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                    disabled={currentPage === 1}
                    style={{
                      padding: '0.5rem 1rem',
                      backgroundColor: currentPage === 1 ? 'var(--color-gray-200)' : 'white',
                      color: currentPage === 1 ? 'var(--color-gray-400)' : 'var(--color-gray-700)',
                      border: '1px solid var(--color-gray-300)',
                      borderRadius: 'var(--radius)',
                      cursor: currentPage === 1 ? 'not-allowed' : 'pointer'
                    }}
                  >
                    Previous
                  </button>

                  {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                    let pageNum;
                    if (totalPages <= 5) {
                      pageNum = i + 1;
                    } else if (currentPage <= 3) {
                      pageNum = i + 1;
                    } else if (currentPage >= totalPages - 2) {
                      pageNum = totalPages - 4 + i;
                    } else {
                      pageNum = currentPage - 2 + i;
                    }

                    return (
                      <button
                        key={pageNum}
                        onClick={() => setCurrentPage(pageNum)}
                        style={{
                          padding: '0.5rem 0.75rem',
                          backgroundColor: currentPage === pageNum ? 'var(--color-primary)' : 'white',
                          color: currentPage === pageNum ? 'white' : 'var(--color-gray-700)',
                          border: '1px solid var(--color-gray-300)',
                          borderRadius: 'var(--radius)',
                          cursor: 'pointer',
                          fontWeight: currentPage === pageNum ? '600' : 'normal'
                        }}
                      >
                        {pageNum}
                      </button>
                    );
                  })}

                  <button
                    onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
                    disabled={currentPage === totalPages}
                    style={{
                      padding: '0.5rem 1rem',
                      backgroundColor: currentPage === totalPages ? 'var(--color-gray-200)' : 'white',
                      color: currentPage === totalPages ? 'var(--color-gray-400)' : 'var(--color-gray-700)',
                      border: '1px solid var(--color-gray-300)',
                      borderRadius: 'var(--radius)',
                      cursor: currentPage === totalPages ? 'not-allowed' : 'pointer'
                    }}
                  >
                    Next
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      </section>
    </div>
  );
};

export default Search;