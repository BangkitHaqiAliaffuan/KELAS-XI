import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { fetchMovieDetails, fetchMovieVideos, getBackdropUrl, getImageUrl, getYouTubeEmbedUrl } from '../utils/tmdb';
import { assets } from '../assets/assets';
import './MovieDetail.css';

const MovieDetail = () => {
  const { movieId } = useParams();
  const navigate = useNavigate();
  const [movie, setMovie] = useState(null);
  const [videos, setVideos] = useState([]);
  const [trailer, setTrailer] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showPlayer, setShowPlayer] = useState(false);

  useEffect(() => {
    const fetchMovieInfo = async () => {
      try {
        setLoading(true);
        
        // Fetch movie details and videos in parallel
        const [movieData, videosData] = await Promise.all([
          fetchMovieDetails(movieId),
          fetchMovieVideos(movieId)
        ]);

        setMovie(movieData);
        setVideos(videosData);
        
        // Find the best trailer
        const trailer = videosData.find(video => 
          video.type === 'Trailer' && video.site === 'YouTube'
        ) || videosData.find(video => 
          video.site === 'YouTube'
        );
        
        setTrailer(trailer);
      } catch (error) {
        console.error('Error fetching movie details:', error);
      } finally {
        setLoading(false);
      }
    };

    if (movieId) {
      fetchMovieInfo();
    }
  }, [movieId]);

  const handleBack = () => {
    navigate('/');
  };

  const handlePlayTrailer = () => {
    if (trailer) {
      setShowPlayer(true);
    }
  };

  const formatRuntime = (minutes) => {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours}h ${mins}m`;
  };

  const formatDate = (dateString) => {
    return new Date(dateString).getFullYear();
  };

  if (loading) {
    return (
      <div className="movie-detail-loading">
        <div className="loading-spinner"></div>
      </div>
    );
  }

  if (!movie) {
    return (
      <div className="movie-detail-error">
        <h2>Movie not found</h2>
        <button onClick={handleBack} className="btn btn-primary">
          Go Back
        </button>
      </div>
    );
  }

  return (
    <div className="movie-detail">
      {showPlayer && trailer ? (
        <div className="video-player">
          <div className="video-player-header">
            <button 
              className="back-button"
              onClick={() => setShowPlayer(false)}
            >
              <img src={assets.backArrowIcon} alt="Back" />
            </button>
            <h3>{movie.title || movie.name}</h3>
          </div>
          <div className="video-container">
            <iframe
              src={getYouTubeEmbedUrl(trailer.key)}
              title={trailer.name}
              frameBorder="0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
            ></iframe>
          </div>
        </div>
      ) : (
        <>
          <div className="movie-detail-header">
            <button className="back-button" onClick={handleBack}>
              <img src={assets.backArrowIcon} alt="Back" />
            </button>
          </div>

          <div 
            className="movie-detail-hero"
            style={{
              backgroundImage: `url(${getBackdropUrl(movie.backdrop_path)})`,
            }}
          >
            <div className="movie-detail-content">
              <div className="movie-info">
                <h1 className="movie-title">
                  {movie.title || movie.name}
                </h1>
                
                <div className="movie-meta">
                  <span className="movie-year">{formatDate(movie.release_date)}</span>
                  {movie.runtime && (
                    <>
                      <span className="movie-separator">•</span>
                      <span className="movie-runtime">{formatRuntime(movie.runtime)}</span>
                    </>
                  )}
                  <span className="movie-separator">•</span>
                  <span className="movie-rating">⭐ {movie.vote_average.toFixed(1)}</span>
                </div>

                <div className="movie-buttons">
                  {trailer && (
                    <button className="play-button btn btn-primary" onClick={handlePlayTrailer}>
                      <img src={assets.playIcon} alt="Play" />
                      <span>Play Trailer</span>
                    </button>
                  )}
                </div>

                <div className="movie-overview">
                  <p>{movie.overview}</p>
                </div>

                {movie.genres && movie.genres.length > 0 && (
                  <div className="movie-genres">
                    <strong>Genres: </strong>
                    {movie.genres.map(genre => genre.name).join(', ')}
                  </div>
                )}

                {movie.production_companies && movie.production_companies.length > 0 && (
                  <div className="movie-production">
                    <strong>Production: </strong>
                    {movie.production_companies.slice(0, 3).map(company => company.name).join(', ')}
                  </div>
                )}
              </div>

              {movie.poster_path && (
                <div className="movie-poster">
                  <img 
                    src={getImageUrl(movie.poster_path, 'w500')} 
                    alt={movie.title || movie.name}
                  />
                </div>
              )}
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default MovieDetail;