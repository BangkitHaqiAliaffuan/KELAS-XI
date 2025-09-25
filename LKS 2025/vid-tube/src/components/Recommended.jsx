import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import moment from 'moment';
import { fetchRelatedVideos, formatViewCount } from '../data/api';
import './Recommended.css';

const Recommended = ({ videoId }) => {
  const [videos, setVideos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchVideos = async () => {
      setLoading(true);
      try {
        const relatedVideos = await fetchRelatedVideos(videoId);
        setVideos(relatedVideos.slice(0, 15)); // Limit to 15 videos
      } catch (error) {
        console.error('Error fetching related videos:', error);
      } finally {
        setLoading(false);
      }
    };

    if (videoId) {
      fetchVideos();
    }
  }, [videoId]);

  if (loading) {
    return (
      <div className="recommended">
        <h3>Recommended</h3>
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Loading recommendations...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="recommended">
      <h3>Recommended</h3>
      
      <div className="recommended-list">
        {videos.map((video, index) => {
          const videoId = typeof video.id === 'string' ? video.id : video.id?.videoId;
          const thumbnailUrl = video.snippet?.thumbnails?.medium?.url || video.snippet?.thumbnails?.default?.url;
          const title = video.snippet?.title || 'Untitled Video';
          const channelTitle = video.snippet?.channelTitle || 'Unknown Channel';
          const publishTime = video.snippet?.publishedAt;
          const viewCount = video.statistics?.viewCount || '0';

          return (
            <Link key={videoId || index} to={`/video/${videoId}`} className="recommended-video-link">
              <div className="recommended-video">
                <div className="video-thumbnail">
                  <img src={thumbnailUrl} alt={title} />
                  <span className="video-duration">10:30</span>
                </div>
                <div className="video-details">
                  <h4>{title}</h4>
                  <p className="channel-name">{channelTitle}</p>
                  <p className="video-stats">
                    {formatViewCount(viewCount)} views • {moment(publishTime).fromNow()}
                  </p>
                </div>
              </div>
            </Link>
          );
        })}
      </div>
    </div>
  );
};

export default Recommended;