import React, { useState, useEffect } from 'react';
import VideoCard from './VideoCard';
import { fetchPopularVideos, searchVideos } from '../data/api';
import './Feed.css';

const Feed = ({ category, searchQuery }) => {
  const [videos, setVideos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchVideos = async () => {
      setLoading(true);
      setError(null);
      
      try {
        let videoData = [];
        
        if (searchQuery) {
          // If there's a search query, search for videos
          videoData = await searchVideos(searchQuery);
        } else {
          // Otherwise, fetch popular videos by category
          videoData = await fetchPopularVideos(category);
        }
        
        setVideos(videoData);
      } catch (err) {
        console.error('Error fetching videos:', err);
        setError('Failed to load videos. Please check your API key or try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchVideos();
  }, [category, searchQuery]);

  if (loading) {
    return (
      <div className="feed">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Loading videos...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="feed">
        <div className="error-container">
          <h3>⚠️ YouTube API Error</h3>
          <p>{error}</p>
          <div className="error-hint">
            <h4>To fix this:</h4>
            <ol>
              <li>Get a YouTube Data API key from <a href="https://console.developers.google.com/" target="_blank" rel="noopener noreferrer">Google Cloud Console</a></li>
              <li>Enable YouTube Data API v3 for your project</li>
              <li>Copy your API key to the .env file:</li>
              <code>VITE_YOUTUBE_API_KEY=your_api_key_here</code>
              <li>Restart the development server</li>
            </ol>
            <p><strong>The app structure and UI are fully functional - just needs a valid API key for video data!</strong></p>
          </div>
        </div>
      </div>
    );
  }

  if (videos.length === 0) {
    return (
      <div className="feed">
        <div className="no-results">
          <h3>No videos found</h3>
          <p>Try searching for something else or check your internet connection.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="feed">
      <div className="video-grid">
        {videos.map((video, index) => (
          <VideoCard key={video.id || index} video={video} />
        ))}
      </div>
    </div>
  );
};

export default Feed;