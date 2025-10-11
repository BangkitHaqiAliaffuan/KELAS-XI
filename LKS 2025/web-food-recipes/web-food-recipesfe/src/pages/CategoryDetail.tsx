import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useSearchParams } from 'react-router-dom';
import type { Category, Recipe } from '../types';

const CategoryDetail: React.FC = () => {
  const { slug } = useParams<{ slug: string }>();
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  
  const [category, setCategory] = useState<Category | null>(null);
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [loading, setLoading] = useState(true);
  const [recipesLoading, setRecipesLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [searchQuery, setSearchQuery] = useState(searchParams.get('search') || '');
  const [selectedDifficulty, setSelectedDifficulty] = useState(searchParams.get('difficulty') || '');
  const [sortBy, setSortBy] = useState(searchParams.get('sort') || 'latest');

  const fetchCategoryAndRecipes = async (page: number = 1) => {
    if (!slug) return;

    setRecipesLoading(true);
    try {
      const params = new URLSearchParams();
      params.append('page', page.toString());
      params.append('limit', '12');
      
      if (searchQuery) params.append('search', searchQuery);
      if (selectedDifficulty) params.append('difficulty', selectedDifficulty);
      if (sortBy) params.append('sort', sortBy);

      const response = await fetch(`http://localhost:8000/api/categories/${slug}/recipes?${params}`, {
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
        setCategory(data.category);
        setRecipes(data.data || []);
        setCurrentPage(data.meta?.current_page || 1);
        setTotalPages(data.meta?.last_page || 1);
        setError(null);
      } else {
        throw new Error(data.message || 'Failed to fetch category data');
      }
    } catch (error) {
      console.error('Error fetching category recipes:', error);
      setError('Failed to load category recipes');
      setRecipes([]);
    } finally {
      setLoading(false);
      setRecipesLoading(false);
    }
  };

  useEffect(() => {
    fetchCategoryAndRecipes(currentPage);
  }, [slug, currentPage, searchQuery, selectedDifficulty, sortBy]);

  useEffect(() => {
    // Update URL params
    const params = new URLSearchParams();
    if (searchQuery) params.set('search', searchQuery);
    if (selectedDifficulty) params.set('difficulty', selectedDifficulty);
    if (sortBy !== 'latest') params.set('sort', sortBy);
    if (currentPage > 1) params.set('page', currentPage.toString());
    
    setSearchParams(params);
  }, [searchQuery, selectedDifficulty, sortBy, currentPage, setSearchParams]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setCurrentPage(1);
  };

  const clearFilters = () => {
    setSearchQuery('');
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

  if (loading) {
    return (
      <div style={{ 
        minHeight: '100vh', 
        display: 'flex', 
        alignItems: 'center', 
        justifyContent: 'center',
        backgroundColor: 'var(--color-gray-50)'
      }}>
        <div className="text-center">
          <div style={{
            display: 'inline-block',
            width: '3rem',
            height: '3rem',
            border: '4px solid var(--color-gray-300)',
            borderTop: '4px solid var(--color-primary)',
            borderRadius: '50%',
            animation: 'spin 1s linear infinite'
          }}></div>
          <p style={{ marginTop: '1rem', color: 'var(--color-gray-600)' }}>Loading category...</p>
        </div>
      </div>
    );
  }

  if (error || !category) {
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
        <h3 style={{ fontSize: '1.5rem', marginBottom: '0.5rem' }}>Category Not Found</h3>
        <p style={{ marginBottom: '2rem' }}>{error || 'The requested category could not be found.'}</p>
        <button
          onClick={() => navigate('/categories')}
          className="btn btn-primary"
        >
          Back to Categories
        </button>
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: 'var(--color-gray-50)' }}>
      {/* Category Header */}
      <section 
        style={{ 
          background: 'linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%)',
          color: 'white',
          padding: '3rem 0 2rem',
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
          {/* Breadcrumb */}
          <nav style={{ marginBottom: '2rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.875rem' }}>
              <button
                onClick={() => navigate('/')}
                style={{
                  background: 'none',
                  border: 'none',
                  color: 'rgba(255,255,255,0.8)',
                  cursor: 'pointer',
                  textDecoration: 'none'
                }}
              >
                Home
              </button>
              <span style={{ color: 'rgba(255,255,255,0.6)' }}>/</span>
              <button
                onClick={() => navigate('/categories')}
                style={{
                  background: 'none',
                  border: 'none',
                  color: 'rgba(255,255,255,0.8)',
                  cursor: 'pointer',
                  textDecoration: 'none'
                }}
              >
                Categories
              </button>
              <span style={{ color: 'rgba(255,255,255,0.6)' }}>/</span>
              <span style={{ color: 'white', fontWeight: '500' }}>{category.name}</span>
            </div>
          </nav>

          <div style={{ display: 'flex', alignItems: 'center', gap: '2rem', flexWrap: 'wrap' }}>
            <div style={{
              fontSize: '4rem',
              background: 'rgba(255,255,255,0.2)',
              borderRadius: '50%',
              width: '6rem',
              height: '6rem',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}>
              {getCategoryIcon(category.name)}
            </div>
            
            <div style={{ flex: 1 }}>
              <h1 style={{ 
                fontSize: '2.5rem', 
                fontWeight: 'bold', 
                marginBottom: '1rem',
                textShadow: '0 2px 4px rgba(0,0,0,0.1)'
              }}>
                {category.name}
              </h1>
              
              {category.description && (
                <p style={{ 
                  fontSize: '1.125rem', 
                  opacity: 0.9, 
                  marginBottom: '1rem',
                  lineHeight: '1.6'
                }}>
                  {category.description}
                </p>
              )}
              
              <div style={{ 
                display: 'flex', 
                gap: '2rem',
                alignItems: 'center',
                fontSize: '0.875rem',
                opacity: 0.9
              }}>
                <div>
                  <strong>{category.recipes_count || 0}</strong> recipes available
                </div>
              </div>
            </div>
          </div>
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
                  placeholder="Search recipes in this category..."
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
          {recipesLoading ? (
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
                Try adjusting your search criteria or browse other categories.
              </p>
              <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center', flexWrap: 'wrap' }}>
                <button
                  onClick={clearFilters}
                  className="btn btn-primary"
                >
                  Clear Filters
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
              <div style={{ marginBottom: '2rem' }}>
                <p style={{ color: 'var(--color-gray-600)' }}>
                  Showing {recipes.length} recipes in <strong>{category.name}</strong> (Page {currentPage} of {totalPages})
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

export default CategoryDetail;