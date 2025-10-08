import React from 'react';
import { useParams } from 'react-router-dom';
import { useRecipe } from '../hooks/useApi';
import type { Recipe, RecipeIngredient, RecipeTutorial } from '../types';

const RecipeDetail: React.FC = () => {
  const { slug } = useParams<{ slug: string }>();
  const { recipe, loading, error } = useRecipe(slug);

  if (loading) {
    return (
      <div className="container" style={{ padding: '4rem 1rem', textAlign: 'center' }}>
        <div style={{ 
          display: 'inline-block',
          width: '3rem',
          height: '3rem',
          border: '4px solid var(--color-gray-300)',
          borderTop: '4px solid var(--color-primary)',
          borderRadius: '50%',
          animation: 'spin 1s linear infinite'
        }}></div>
        <p style={{ marginTop: '1rem', color: 'var(--color-gray-600)', fontSize: '1.125rem' }}>
          Loading recipe...
        </p>
      </div>
    );
  }

  if (error || !recipe) {
    return (
      <div className="container" style={{ padding: '4rem 1rem', textAlign: 'center' }}>
        <div style={{ 
          backgroundColor: '#fee2e2', 
          color: '#dc2626', 
          padding: '1rem', 
          borderRadius: 'var(--radius)',
          marginBottom: '2rem' 
        }}>
          <h2 style={{ marginBottom: '0.5rem' }}>Recipe Not Found</h2>
          <p>{error || 'The recipe you are looking for does not exist.'}</p>
        </div>
        <a 
          href="/recipes" 
          className="btn btn-primary"
          style={{ textDecoration: 'none' }}
        >
          Back to Recipes
        </a>
      </div>
    );
  }

  const handleDownloadPDF = () => {
    if (recipe.file_url) {
      const link = document.createElement('a');
      link.href = recipe.file_url;
      link.download = `${recipe.name.replace(/\s+/g, '_')}_recipe.pdf`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  };

  const getYouTubeEmbedUrl = (videoUrl: string) => {
    if (!videoUrl) return null;
    
    // Extract YouTube video ID from various URL formats
    const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|&v=)([^#&?]*).*/;
    const match = videoUrl.match(regExp);
    
    if (match && match[2].length === 11) {
      return `https://www.youtube.com/embed/${match[2]}`;
    }
    
    // If it's already an embed URL or video ID
    if (videoUrl.length === 11) {
      return `https://www.youtube.com/embed/${videoUrl}`;
    }
    
    return null;
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
    <div>
      {/* Recipe Header */}
      <section style={{ backgroundColor: 'white', padding: '2rem 0', borderBottom: '1px solid var(--color-gray-200)' }}>
        <div className="container">
          <div className="grid md-grid-cols-2" style={{ gap: '3rem', alignItems: 'center' }}>
            {/* Recipe Image */}
            <div>
              <div 
                className="card"
                style={{ 
                  height: '20rem', 
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '4rem',
                  backgroundColor: 'var(--color-gray-100)'
                }}
              >
                {recipe.thumbnail ? (
                  <img 
                    src={recipe.thumbnail} 
                    alt={recipe.name}
                    style={{ 
                      width: '100%', 
                      height: '100%', 
                      objectFit: 'cover',
                      borderRadius: 'var(--radius-lg)'
                    }}
                  />
                ) : (
                  '🍳'
                )}
              </div>
            </div>

            {/* Recipe Info */}
            <div>
              {/* Breadcrumb */}
              <nav style={{ marginBottom: '1rem' }}>
                <ol style={{ 
                  display: 'flex', 
                  listStyle: 'none', 
                  padding: 0, 
                  fontSize: '0.875rem',
                  color: 'var(--color-gray-600)'
                }}>
                  <li><a href="/" style={{ color: 'var(--color-primary)', textDecoration: 'none' }}>Home</a></li>
                  <li style={{ margin: '0 0.5rem' }}>/</li>
                  <li><a href="/recipes" style={{ color: 'var(--color-primary)', textDecoration: 'none' }}>Recipes</a></li>
                  <li style={{ margin: '0 0.5rem' }}>/</li>
                  {recipe.category && (
                    <>
                      <li>
                        <a 
                          href={`/categories/${recipe.category.slug}`} 
                          style={{ color: 'var(--color-primary)', textDecoration: 'none' }}
                        >
                          {recipe.category.name}
                        </a>
                      </li>
                      <li style={{ margin: '0 0.5rem' }}>/</li>
                    </>
                  )}
                  <li>{recipe.name}</li>
                </ol>
              </nav>

              <h1 className="text-3xl font-bold mb-4" style={{ marginBottom: '1rem' }}>
                {recipe.name}
              </h1>
              
              {recipe.description && (
                <p className="text-lg text-gray-600 mb-6" style={{ marginBottom: '1.5rem', lineHeight: '1.6' }}>
                  {recipe.description}
                </p>
              )}

              {/* Recipe Meta */}
              <div className="grid grid-cols-2 sm-grid-cols-4" style={{ gap: '1rem', marginBottom: '1.5rem' }}>
                <div className="text-center">
                  <div style={{ fontSize: '1.5rem', marginBottom: '0.5rem' }}>⏱️</div>
                  <div className="text-sm text-gray-600">Cook Time</div>
                  <div className="font-semibold">{recipe.cooking_time || 'N/A'} min</div>
                </div>
                <div className="text-center">
                  <div style={{ fontSize: '1.5rem', marginBottom: '0.5rem' }}>👥</div>
                  <div className="text-sm text-gray-600">Servings</div>
                  <div className="font-semibold">{recipe.servings || 'N/A'}</div>
                </div>
                <div className="text-center">
                  <div style={{ fontSize: '1.5rem', marginBottom: '0.5rem' }}>📊</div>
                  <div className="text-sm text-gray-600">Difficulty</div>
                  <div 
                    className="font-semibold"
                    style={{ 
                      color: getDifficultyColor(recipe.difficulty),
                      textTransform: 'capitalize'
                    }}
                  >
                    {recipe.difficulty}
                  </div>
                </div>
                <div className="text-center">
                  <div style={{ fontSize: '1.5rem', marginBottom: '0.5rem' }}>👨‍🍳</div>
                  <div className="text-sm text-gray-600">Chef</div>
                  <div className="font-semibold text-sm">
                    {recipe.recipe_author?.name || 'Unknown'}
                  </div>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex" style={{ gap: '1rem', flexWrap: 'wrap' }}>
                {recipe.file_url && (
                  <button 
                    onClick={handleDownloadPDF}
                    className="btn btn-primary"
                    style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
                  >
                    <svg style={{ width: '1rem', height: '1rem' }} fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M3 17a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm3.293-7.707a1 1 0 011.414 0L9 10.586V3a1 1 0 112 0v7.586l1.293-1.293a1 1 0 111.414 1.414l-3 3a1 1 0 01-1.414 0l-3-3a1 1 0 010-1.414z" clipRule="evenodd" />
                    </svg>
                    Download PDF
                  </button>
                )}
                <button 
                  className="btn btn-secondary"
                  style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
                  onClick={() => {
                    if (navigator.share) {
                      navigator.share({
                        title: recipe.name,
                        text: recipe.description,
                        url: window.location.href,
                      });
                    } else {
                      navigator.clipboard.writeText(window.location.href);
                      alert('Recipe link copied to clipboard!');
                    }
                  }}
                >
                  <svg style={{ width: '1rem', height: '1rem' }} fill="currentColor" viewBox="0 0 20 20">
                    <path d="M15 8a3 3 0 10-2.977-2.63l-4.94 2.47a3 3 0 100 4.319l4.94 2.47a3 3 0 10.895-1.789l-4.94-2.47a3.027 3.027 0 000-.74l4.94-2.47C13.456 7.68 14.19 8 15 8z" />
                  </svg>
                  Share Recipe
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Video Tutorial (if available) */}
      {recipe.video_url && (
        <section style={{ padding: '3rem 0', backgroundColor: 'var(--color-gray-50)' }}>
          <div className="container">
            <h2 className="text-2xl font-bold mb-6 text-center">Video Tutorial</h2>
            <div style={{ maxWidth: '800px', margin: '0 auto' }}>
              <div style={{ 
                position: 'relative', 
                paddingBottom: '56.25%', 
                height: 0,
                borderRadius: 'var(--radius-lg)',
                overflow: 'hidden',
                boxShadow: 'var(--shadow-lg)'
              }}>
                <iframe
                  src={getYouTubeEmbedUrl(recipe.video_url) || ''}
                  title={`${recipe.name} - Video Tutorial`}
                  style={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    width: '100%',
                    height: '100%',
                    border: 'none'
                  }}
                  allowFullScreen
                />
              </div>
            </div>
          </div>
        </section>
      )}

      {/* Recipe Content */}
      <section style={{ padding: '3rem 0' }}>
        <div className="container">
          <div className="grid md-grid-cols-3" style={{ gap: '3rem' }}>
            {/* Ingredients */}
            <div className="card" style={{ padding: '2rem', height: 'fit-content' }}>
              <h3 className="text-xl font-bold mb-4" style={{ 
                display: 'flex', 
                alignItems: 'center', 
                gap: '0.5rem',
                borderBottom: '2px solid var(--color-primary)',
                paddingBottom: '0.5rem'
              }}>
                🛒 Ingredients
              </h3>
              
              {recipe.ingredients && recipe.ingredients.length > 0 ? (
                <ul style={{ listStyle: 'none', padding: 0 }}>
                  {recipe.ingredients.map((recipeIngredient: RecipeIngredient) => (
                    <li 
                      key={recipeIngredient.id} 
                      style={{ 
                        padding: '0.75rem 0', 
                        borderBottom: '1px solid var(--color-gray-200)',
                        display: 'flex',
                        alignItems: 'flex-start',
                        gap: '0.75rem'
                      }}
                    >
                      <span style={{ 
                        backgroundColor: 'var(--color-primary)', 
                        color: 'white',
                        width: '1.5rem',
                        height: '1.5rem',
                        borderRadius: '50%',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: '0.75rem',
                        fontWeight: 'bold',
                        flexShrink: 0,
                        marginTop: '0.125rem'
                      }}>
                        ✓
                      </span>
                      <div style={{ flex: 1 }}>
                        <div className="flex justify-between" style={{ marginBottom: '0.25rem' }}>
                          <span className="font-medium">{recipeIngredient.ingredient?.name}</span>
                          <span className="text-sm text-primary font-semibold">
                            {recipeIngredient.quantity}
                          </span>
                        </div>
                        {recipeIngredient.notes && (
                          <p className="text-sm text-gray-600">{recipeIngredient.notes}</p>
                        )}
                      </div>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="text-gray-600">No ingredients listed for this recipe.</p>
              )}
            </div>

            {/* Instructions */}
            <div style={{ gridColumn: 'span 2' }}>
              <div className="card" style={{ padding: '2rem' }}>
                <h3 className="text-xl font-bold mb-6" style={{ 
                  display: 'flex', 
                  alignItems: 'center', 
                  gap: '0.5rem',
                  borderBottom: '2px solid var(--color-primary)',
                  paddingBottom: '0.5rem'
                }}>
                  👨‍🍳 Instructions
                </h3>
                
                {recipe.tutorials && recipe.tutorials.length > 0 ? (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
                    {recipe.tutorials.map((tutorial: RecipeTutorial, index: number) => (
                      <div key={tutorial.id} style={{ display: 'flex', gap: '1.5rem' }}>
                        {/* Step Number */}
                        <div style={{ 
                          backgroundColor: 'var(--color-primary)', 
                          color: 'white',
                          width: '2.5rem',
                          height: '2.5rem',
                          borderRadius: '50%',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          fontSize: '1.125rem',
                          fontWeight: 'bold',
                          flexShrink: 0
                        }}>
                          {tutorial.step_number}
                        </div>
                        
                        {/* Step Content */}
                        <div style={{ flex: 1 }}>
                          <h4 className="text-lg font-semibold mb-2">{tutorial.title}</h4>
                          <p className="text-gray-700 mb-3" style={{ lineHeight: '1.6' }}>
                            {tutorial.instruction}
                          </p>
                          
                          {/* Step Image */}
                          {tutorial.image && (
                            <div style={{ 
                              width: '100%', 
                              maxWidth: '300px',
                              height: '200px',
                              backgroundColor: 'var(--color-gray-100)',
                              borderRadius: 'var(--radius)',
                              overflow: 'hidden',
                              marginTop: '1rem'
                            }}>
                              <img 
                                src={tutorial.image} 
                                alt={`Step ${tutorial.step_number}: ${tutorial.title}`}
                                style={{ 
                                  width: '100%', 
                                  height: '100%', 
                                  objectFit: 'cover'
                                }}
                              />
                            </div>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-gray-600">No instructions available for this recipe.</p>
                )}
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Additional Photos Gallery */}
      {recipe.photos && recipe.photos.length > 0 && (
        <section style={{ padding: '3rem 0', backgroundColor: 'var(--color-gray-50)' }}>
          <div className="container">
            <h2 className="text-2xl font-bold mb-6 text-center">Recipe Gallery</h2>
            <div className="grid sm-grid-cols-2 md-grid-cols-3" style={{ gap: '1rem' }}>
              {recipe.photos.map((photo) => (
                <div 
                  key={photo.id} 
                  className="card"
                  style={{ 
                    height: '200px',
                    overflow: 'hidden',
                    cursor: 'pointer',
                    transition: 'transform 0.2s ease-in-out'
                  }}
                  onMouseEnter={(e) => e.currentTarget.style.transform = 'scale(1.05)'}
                  onMouseLeave={(e) => e.currentTarget.style.transform = 'scale(1)'}
                  onClick={() => {
                    // Simple lightbox functionality
                    const overlay = document.createElement('div');
                    overlay.style.cssText = `
                      position: fixed;
                      top: 0;
                      left: 0;
                      width: 100%;
                      height: 100%;
                      background: rgba(0,0,0,0.8);
                      display: flex;
                      align-items: center;
                      justify-content: center;
                      z-index: 1000;
                      cursor: pointer;
                    `;
                    
                    const img = document.createElement('img');
                    img.src = photo.photo_path;
                    img.alt = photo.alt_text || `Recipe photo ${photo.order}`;
                    img.style.cssText = 'max-width: 90%; max-height: 90%; object-fit: contain; border-radius: 8px;';
                    
                    overlay.appendChild(img);
                    document.body.appendChild(overlay);
                    
                    overlay.onclick = () => document.body.removeChild(overlay);
                  }}
                >
                  <img 
                    src={photo.photo_path} 
                    alt={photo.alt_text || `Recipe photo ${photo.order}`}
                    style={{ 
                      width: '100%', 
                      height: '100%', 
                      objectFit: 'cover'
                    }}
                  />
                </div>
              ))}
            </div>
          </div>
        </section>
      )}

      {/* Recipe Author Info */}
      {recipe.recipe_author && (
        <section style={{ padding: '3rem 0', backgroundColor: 'white' }}>
          <div className="container">
            <div className="card" style={{ padding: '2rem', maxWidth: '600px', margin: '0 auto', textAlign: 'center' }}>
              <h3 className="text-xl font-bold mb-4">About the Chef</h3>
              <div style={{ 
                width: '80px', 
                height: '80px', 
                backgroundColor: 'var(--color-gray-300)',
                borderRadius: '50%',
                margin: '0 auto 1rem',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '2rem'
              }}>
                {recipe.recipe_author.photo ? (
                  <img 
                    src={recipe.recipe_author.photo} 
                    alt={recipe.recipe_author.name}
                    style={{ 
                      width: '100%', 
                      height: '100%', 
                      borderRadius: '50%',
                      objectFit: 'cover'
                    }}
                  />
                ) : (
                  '👨‍🍳'
                )}
              </div>
              <h4 className="text-lg font-semibold mb-2">{recipe.recipe_author.name}</h4>
              {recipe.recipe_author.bio && (
                <p className="text-gray-600 mb-3">{recipe.recipe_author.bio}</p>
              )}
              {recipe.recipe_author.email && (
                <p className="text-sm text-gray-500">
                  Contact: {recipe.recipe_author.email}
                </p>
              )}
            </div>
          </div>
        </section>
      )}
    </div>
  );
};

export default RecipeDetail;