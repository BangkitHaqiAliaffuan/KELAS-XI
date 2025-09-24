import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchMoviesByCategory, getImageUrl } from '../utils/tmdb';
import './MovieRow.css';

const MovieRow = ({ title, fetchUrl, isLargeRow = false }) => {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchMovies = async () => {
      try {
        const data = await fetchMoviesByCategory(fetchUrl);
        setMovies(data);
      } catch (error) {
        console.error(`Error fetching ${title}:`, error);
      } finally {
        setLoading(false);
      }
    };

    fetchMovies();
  }, [fetchUrl, title]);

  const handleMovieClick = (movie) => {
    navigate(`/movie/${movie.id}`);
  };

  if (loading) {
    return (
      <div className="movie-row">
        <h2>{title}</h2>
        <div className="movie-row-loading">
          <div className="loading-spinner"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="movie-row">
      <h2>{title}</h2>
      <div className="movie-row-posters">
        {movies.map((movie) => (
          <div
            key={movie.id}
            className={`movie-poster-container ${isLargeRow && 'movie-poster-container-large'}`}
            onClick={() => handleMovieClick(movie)}
          >
            <img
              className={`movie-row-poster ${isLargeRow && 'movie-row-poster-large'}`}
              src={getImageUrl(
                isLargeRow ? movie.poster_path : movie.backdrop_path,
                isLargeRow ? 'w300' : 'w500'
              )}
              alt={movie.title || movie.name}
              loading="lazy"
            />
            <div className="movie-poster-overlay">
              <h3 className="movie-poster-title">{movie.title || movie.name}</h3>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default MovieRow;