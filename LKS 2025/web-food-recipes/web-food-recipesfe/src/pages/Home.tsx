import React from 'react';
import { useCategories, useFeaturedRecipes } from '../hooks/useApi';
import type { Recipe, Category } from '../types';

const Home: React.FC = () => {
  const { categories, loading: categoriesLoading } = useCategories();
  const { recipes: featuredRecipes, loading: recipesLoading } = useFeaturedRecipes();

  return (
    <div>
      {/* Hero Section */}
      <section 
        className="bg-primary" 
        style={{ 
          background: 'linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%)',
          color: 'white',
          padding: '4rem 0'
        }}
      >
        <div className="container text-center">
          <h1 
            className="text-3xl font-bold mb-4" 
            style={{ fontSize: '3rem', marginBottom: '1.5rem' }}
          >
            Discover Amazing Recipes
          </h1>
          <p 
            className="text-lg mb-8" 
            style={{ fontSize: '1.25rem', marginBottom: '2rem', opacity: 0.9 }}
          >
            Cook delicious meals with step-by-step instructions from expert chefs
          </p>
          <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem' }}>
            <a 
              href="/recipes" 
              className="btn"
              style={{ 
                backgroundColor: 'white',
                color: 'var(--color-primary)',
                padding: '0.75rem 2rem',
                fontSize: '1.125rem',
                fontWeight: '600',
                textDecoration: 'none',
                borderRadius: 'var(--radius-lg)'
              }}
            >
              Browse Recipes
            </a>
            <a 
              href="/categories" 
              className="btn"
              style={{ 
                backgroundColor: 'transparent',
                color: 'white',
                border: '2px solid white',
                padding: '0.75rem 2rem',
                fontSize: '1.125rem',
                fontWeight: '600',
                textDecoration: 'none',
                borderRadius: 'var(--radius-lg)'
              }}
            >
              View Categories
            </a>
          </div>
        </div>
      </section>

      {/* Categories Section */}
      <section style={{ padding: '4rem 0' }}>
        <div className="container">
          <h2 
            className="text-2xl font-bold text-center mb-8"
            style={{ fontSize: '2rem', marginBottom: '2rem' }}
          >
            Browse by Category
          </h2>
          
          {categoriesLoading ? (
            <div className="text-center" style={{ padding: '2rem' }}>
              <div style={{ 
                display: 'inline-block',
                width: '2rem',
                height: '2rem',
                border: '3px solid var(--color-gray-300)',
                borderTop: '3px solid var(--color-primary)',
                borderRadius: '50%',
                animation: 'spin 1s linear infinite'
              }}></div>
              <p style={{ marginTop: '1rem', color: 'var(--color-gray-600)' }}>Loading categories...</p>
            </div>
          ) : (
            <div className="grid sm-grid-cols-2 md-grid-cols-3" style={{ gap: '1.5rem' }}>
              {categories.slice(0, 6).map((category: Category) => (
                <div key={category.id} className="card" style={{ padding: '1.5rem', textAlign: 'center' }}>
                  <div 
                    style={{ 
                      width: '4rem', 
                      height: '4rem', 
                      backgroundColor: 'var(--color-primary)', 
                      borderRadius: '50%',
                      margin: '0 auto 1rem',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '1.5rem'
                    }}
                  >
                    🍽️
                  </div>
                  <h3 className="text-lg font-semibold mb-2">{category.name}</h3>
                  <p className="text-gray-600 text-sm mb-4">{category.description}</p>
                  <p className="text-sm text-gray-500 mb-4">
                    {category.recipes_count || 0} recipes
                  </p>
                  <a 
                    href={`/categories/${category.slug}`}
                    className="btn btn-primary"
                    style={{ textDecoration: 'none' }}
                  >
                    View Recipes
                  </a>
                </div>
              ))}
            </div>
          )}
        </div>
      </section>

      {/* Featured Recipes Section */}
      <section style={{ padding: '4rem 0', backgroundColor: 'white' }}>
        <div className="container">
          <h2 
            className="text-2xl font-bold text-center mb-8"
            style={{ fontSize: '2rem', marginBottom: '2rem' }}
          >
            Featured Recipes
          </h2>
          
          {recipesLoading ? (
            <div className="text-center" style={{ padding: '2rem' }}>
              <div style={{ 
                display: 'inline-block',
                width: '2rem',
                height: '2rem',
                border: '3px solid var(--color-gray-300)',
                borderTop: '3px solid var(--color-primary)',
                borderRadius: '50%',
                animation: 'spin 1s linear infinite'
              }}></div>
              <p style={{ marginTop: '1rem', color: 'var(--color-gray-600)' }}>Loading recipes...</p>
            </div>
          ) : (
            <div className="grid sm-grid-cols-2 md-grid-cols-3" style={{ gap: '1.5rem' }}>
              {featuredRecipes.slice(0, 6).map((recipe: Recipe) => (
                <div key={recipe.id} className="card" style={{ overflow: 'hidden' }}>
                  <div 
                    style={{ 
                      height: '12rem', 
                      backgroundColor: 'var(--color-gray-200)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '3rem'
                    }}
                  >
                    {recipe.thumbnail ? (
                      <img 
                        src={recipe.thumbnail} 
                        alt={recipe.name}
                        style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                      />
                    ) : (
                      '🍳'
                    )}
                  </div>
                  <div style={{ padding: '1.5rem' }}>
                    <h3 className="text-lg font-semibold mb-2">{recipe.name}</h3>
                    <p className="text-gray-600 text-sm mb-4">
                      {recipe.description?.slice(0, 100)}...
                    </p>
                    <div className="flex justify-between items-center" style={{ marginBottom: '1rem' }}>
                      <span className="text-sm text-gray-500">
                        ⏱️ {recipe.cooking_time || 'N/A'} min
                      </span>
                      <span className="text-sm text-gray-500">
                        👥 {recipe.servings || 'N/A'} servings
                      </span>
                      <span 
                        className="text-xs"
                        style={{ 
                          padding: '0.25rem 0.5rem',
                          backgroundColor: recipe.difficulty === 'easy' ? '#10b981' : 
                                          recipe.difficulty === 'medium' ? '#f59e0b' : '#ef4444',
                          color: 'white',
                          borderRadius: 'var(--radius)',
                          textTransform: 'capitalize'
                        }}
                      >
                        {recipe.difficulty}
                      </span>
                    </div>
                    <a 
                      href={`/recipes/${recipe.slug}`}
                      className="btn btn-primary"
                      style={{ textDecoration: 'none', width: '100%' }}
                    >
                      View Recipe
                    </a>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </section>
    </div>
  );
};

export default Home;