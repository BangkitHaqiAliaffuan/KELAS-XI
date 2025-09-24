import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getBackdropUrl, fetchMovieVideos } from '../utils/tmdb';
import { assets } from '../assets/assets';
import './Hero.css';

const Hero = ({ movie }) => {
  const [trailer, setTrailer] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (movie?.id) {
      fetchMovieVideos(movie.id)
        .then(videos => {
          const trailer = videos.find(video => 
            video.type === 'Trailer' && video.site === 'YouTube'
          );
          setTrailer(trailer);
        })
        .catch(error => console.error('Error fetching trailer:', error));
    }
  }, [movie]);

  const truncate = (str, n) => {
    return str?.length > n ? str.substring(0, n - 1) + '...' : str;
  };

  const handlePlayClick = () => {
    if (movie?.id) {
      navigate(`/movie/${movie.id}`);
    }
  };

  if (!movie) return null;

  return (
    <header 
      className="hero"
      style={{
        backgroundImage: `url(${getBackdropUrl(movie.backdrop_path)})`,
      }}
    >
      <div className="hero-content">
        <div className="hero-info">
          <h1 className="hero-title">
            {movie.title || movie.name || movie.original_name}
          </h1>
          
          <div className="hero-buttons">
            <button className="hero-button play-button" onClick={handlePlayClick}>
              <img src={assets.playIcon} alt="Play" />
              <span>Play</span>
            </button>
            <button className="hero-button info-button">
              <img src={assets.infoIcon} alt="More Info" />
              <span>More Info</span>
            </button>
          </div>
          
          <p className="hero-description">
            {truncate(movie.overview, 200)}
          </p>
        </div>
      </div>
      
      <div className="hero-fade-bottom"></div>
    </header>
  );
};

export default Hero;