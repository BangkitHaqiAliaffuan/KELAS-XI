import { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import Hero from '../components/Hero';
import MovieRow from '../components/MovieRow';
import { requests, fetchMoviesByCategory } from '../utils/tmdb';
import './Home.css';

const Home = () => {
  const [featuredMovie, setFeaturedMovie] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchFeaturedMovie = async () => {
      try {
        const trendingMovies = await fetchMoviesByCategory(requests.fetchTrending);
        if (trendingMovies && trendingMovies.length > 0) {
          // Get a random movie from trending for featured movie
          const randomMovie = trendingMovies[Math.floor(Math.random() * trendingMovies.length)];
          setFeaturedMovie(randomMovie);
        }
      } catch (error) {
        console.error('Error fetching featured movie:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchFeaturedMovie();
  }, []);

  if (loading) {
    return (
      <div className="home-loading">
        <div className="loading-spinner"></div>
      </div>
    );
  }

  return (
    <div className="home">
      <Navbar />
      {featuredMovie && <Hero movie={featuredMovie} />}
      
      <div className="home-content">
        <MovieRow title="Trending Now" fetchUrl={requests.fetchTrending} />
        <MovieRow title="Netflix Originals" fetchUrl={requests.fetchNetflixOriginals} />
        <MovieRow title="Top Rated" fetchUrl={requests.fetchTopRated} />
        <MovieRow title="Action Movies" fetchUrl={requests.fetchActionMovies} />
        <MovieRow title="Comedy Movies" fetchUrl={requests.fetchComedyMovies} />
        <MovieRow title="Horror Movies" fetchUrl={requests.fetchHorrorMovies} />
        <MovieRow title="Romance Movies" fetchUrl={requests.fetchRomanceMovies} />
        <MovieRow title="Documentaries" fetchUrl={requests.fetchDocumentaries} />
      </div>
    </div>
  );
};

export default Home;