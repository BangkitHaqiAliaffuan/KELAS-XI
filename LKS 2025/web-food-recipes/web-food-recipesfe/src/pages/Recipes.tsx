import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useCategories } from '../hooks/useApi';
import type { Recipe } from '../types';

const Recipes: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const { categories } = useCategories();
  
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [searchQuery, setSearchQuery] = useState(searchParams.get('search') || '');
  const [selectedCategory, setSelectedCategory] = useState(searchParams.get('category') || '');
  const [selectedDifficulty, setSelectedDifficulty] = useState(searchParams.get('difficulty') || '');
  const [sortBy, setSortBy] = useState(searchParams.get('sort') || 'latest');

  const fetchRecipes = async (page: number = 1) => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      params.append('page', page.toString());
      params.append('limit', '12');
      
      if (searchQuery) params.append('search', searchQuery);
      if (selectedCategory) params.append('category', selectedCategory);
      if (selectedDifficulty) params.append('difficulty', selectedDifficulty);
      if (sortBy) params.append('sort', sortBy);

      const response = await fetch(`http://localhost:8000/api/recipes?${params}`, {
        headers: {
          'Accept': 'application/json',
          'X-API-Key': 'food_recipes_secure_api_key_2025'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      setRecipes(data.data || []);
      setCurrentPage(data.current_page || 1);
      setTotalPages(data.last_page || 1);
    } catch (error) {
      console.error('Error fetching recipes:', error);
      setRecipes([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRecipes(currentPage);
  }, [currentPage, searchQuery, selectedCategory, selectedDifficulty, sortBy]);

  useEffect(() => {
    // Update URL params
    const params = new URLSearchParams();
    if (searchQuery) params.set('search', searchQuery);
    if (selectedCategory) params.set('category', selectedCategory);
    if (selectedDifficulty) params.set('difficulty', selectedDifficulty);
    if (sortBy !== 'latest') params.set('sort', sortBy);
    if (currentPage > 1) params.set('page', currentPage.toString());
    
    setSearchParams(params);
  }, [searchQuery, selectedCategory, selectedDifficulty, sortBy, currentPage, setSearchParams]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setCurrentPage(1);
    fetchRecipes(1);
  };

  const clearFilters = () => {
    setSearchQuery('');
    setSelectedCategory('');
    setSelectedDifficulty('');
    setSortBy('latest');
    setCurrentPage(1);
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
      {/* Page Header */}
      <section 
        style={{ 
          backgroundColor: 'var(--color-primary)',
          color: 'white',
          padding: '3rem 0 2rem'
        }}
      >
        <div className="container">
          <h1 style={{ fontSize: '2.5rem', fontWeight: 'bold', marginBottom: '0.5rem' }}>
            All Recipes
          </h1>
          <p style={{ fontSize: '1.125rem', opacity: 0.9 }}>
            Discover delicious recipes from our collection
          </p>
        </div>
      </section>

      {/* Filters Section */}
      <section style={{ backgroundColor: 'white', padding: '2rem 0', borderBottom: '1px solid var(--color-gray-200)' }}>
        <div className="container">
          <form onSubmit={handleSearch} style={{ marginBottom: '1.5rem' }}>
            <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
              <div style={{ flex: 1, minWidth: '250px' }}>
                <input
                  type="text"
                  placeholder="Search recipes..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '1px solid var(--color-gray-300)',
                    borderRadius: 'var(--radius)',
                    fontSize: '1rem'
                  }}
                />
              </div>
              <button
                type="submit"
                className="btn btn-primary"
                style={{ padding: '0.75rem 1.5rem' }}
              >
                Search
              </button>
            </div>
          </form>

          <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              style={{
                padding: '0.5rem',
                border: '1px solid var(--color-gray-300)',
                borderRadius: 'var(--radius)',
                backgroundColor: 'white'
              }}
            >
              <option value="">All Categories</option>
              {categories.map(category => (
                <option key={category.id} value={category.slug}>
                  {category.name}
                </option>
              ))}
            </select>

            <select
              value={selectedDifficulty}
              onChange={(e) => setSelectedDifficulty(e.target.value)}
              style={{
                padding: '0.5rem',
                border: '1px solid var(--color-gray-300)',
                borderRadius: 'var(--radius)',
                backgroundColor: 'white'
              }}
            >
              <option value="">All Difficulties</option>
              <option value="easy">Easy</option>
              <option value="medium">Medium</option>
              <option value="hard">Hard</option>
            </select>

            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              style={{
                padding: '0.5rem',
                border: '1px solid var(--color-gray-300)',
                borderRadius: 'var(--radius)',
                backgroundColor: 'white'
              }}
            >
              <option value="latest">Latest</option>
              <option value="oldest">Oldest</option>
              <option value="name_asc">Name A-Z</option>
              <option value="name_desc">Name Z-A</option>
              <option value="difficulty_asc">Difficulty Low-High</option>
              <option value="difficulty_desc">Difficulty High-Low</option>
            </select>

            <button
              onClick={clearFilters}
              style={{
                padding: '0.5rem 1rem',
                backgroundColor: 'transparent',
                color: 'var(--color-gray-600)',
                border: '1px solid var(--color-gray-300)',
                borderRadius: 'var(--radius)',
                cursor: 'pointer'
              }}
            >
              Clear Filters
            </button>
          </div>
        </div>
      </section>

      {/* Recipes Grid */}
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
              <p style={{ marginTop: '1rem', color: 'var(--color-gray-600)' }}>Loading recipes...</p>
            </div>
          ) : recipes.length === 0 ? (
            <div className="text-center" style={{ padding: '4rem 0' }}>
              <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>🍳</div>
              <h3 style={{ fontSize: '1.5rem', marginBottom: '1rem', color: 'var(--color-gray-800)' }}>
                No recipes found
              </h3>
              <p style={{ color: 'var(--color-gray-600)', marginBottom: '2rem' }}>
                Try adjusting your search criteria or browse all recipes.
              </p>
              <button
                onClick={clearFilters}
                className="btn btn-primary"
              >
                Show All Recipes
              </button>
            </div>
          ) : (
            <>
              <div style={{ marginBottom: '2rem' }}>
                <p style={{ color: 'var(--color-gray-600)' }}>
                  Showing {recipes.length} recipes (Page {currentPage} of {totalPages})
                </p>
              </div>

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
                            objectFit: 'cover',
                            transition: 'transform 0.3s ease-in-out'
                          }}
                          onMouseEnter={(e) => {
                            e.currentTarget.style.transform = 'scale(1.05)';
                          }}
                          onMouseLeave={(e) => {
                            e.currentTarget.style.transform = 'scale(1)';
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

export default Recipes;