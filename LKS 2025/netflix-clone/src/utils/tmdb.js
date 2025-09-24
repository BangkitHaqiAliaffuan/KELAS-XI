import axios from 'axios';

const API_KEY = import.meta.env.VITE_TMDB_API_KEY;
const BASE_URL = import.meta.env.VITE_TMDB_BASE_URL || 'https://api.themoviedb.org/3';
const IMAGE_BASE_URL = 'https://image.tmdb.org/t/p';

// Create axios instance
const tmdbApi = axios.create({
  baseURL: BASE_URL,
  params: {
    api_key: API_KEY,
  },
});

// Movie categories endpoints
export const requests = {
  fetchTrending: `/trending/movie/week`,
  fetchNetflixOriginals: `/discover/movie?with_networks=213`,
  fetchTopRated: `/movie/top_rated`,
  fetchActionMovies: `/discover/movie?with_genres=28`,
  fetchComedyMovies: `/discover/movie?with_genres=35`,
  fetchHorrorMovies: `/discover/movie?with_genres=27`,
  fetchRomanceMovies: `/discover/movie?with_genres=10749`,
  fetchDocumentaries: `/discover/movie?with_genres=99`,
  fetchPopular: `/movie/popular`,
  fetchUpcoming: `/movie/upcoming`,
  fetchNowPlaying: `/movie/now_playing`,
};

// Fetch movies by category
export const fetchMoviesByCategory = async (endpoint) => {
  try {
    const response = await tmdbApi.get(endpoint);
    return response.data.results;
  } catch (error) {
    console.error('Error fetching movies:', error);
    throw error;
  }
};

// Fetch single movie details
export const fetchMovieDetails = async (movieId) => {
  try {
    const response = await tmdbApi.get(`/movie/${movieId}`, {
      params: {
        append_to_response: 'videos,credits',
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching movie details:', error);
    throw error;
  }
};

// Fetch movie videos (trailers)
export const fetchMovieVideos = async (movieId) => {
  try {
    const response = await tmdbApi.get(`/movie/${movieId}/videos`);
    return response.data.results;
  } catch (error) {
    console.error('Error fetching movie videos:', error);
    throw error;
  }
};

// Search movies
export const searchMovies = async (query) => {
  try {
    const response = await tmdbApi.get('/search/movie', {
      params: {
        query,
      },
    });
    return response.data.results;
  } catch (error) {
    console.error('Error searching movies:', error);
    throw error;
  }
};

// Get image URL
export const getImageUrl = (imagePath, size = 'w500') => {
  if (!imagePath) return null;
  return `${IMAGE_BASE_URL}/${size}${imagePath}`;
};

// Get backdrop URL (for hero banners)
export const getBackdropUrl = (imagePath, size = 'original') => {
  if (!imagePath) return null;
  return `${IMAGE_BASE_URL}/${size}${imagePath}`;
};

// Get YouTube trailer URL
export const getYouTubeUrl = (videoKey) => {
  if (!videoKey) return null;
  return `https://www.youtube.com/watch?v=${videoKey}`;
};

// Get YouTube embed URL
export const getYouTubeEmbedUrl = (videoKey) => {
  if (!videoKey) return null;
  return `https://www.youtube.com/embed/${videoKey}`;
};

export default tmdbApi;