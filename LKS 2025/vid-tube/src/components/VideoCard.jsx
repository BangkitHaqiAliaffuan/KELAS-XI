import React from 'react';
import { Link } from 'react-router-dom';
import moment from 'moment';
import { formatViewCount } from '../data/api';
import './VideoCard.css';

const VideoCard = ({ video }) => {
  if (!video) return null;

  const {
    id,
    snippet,
    statistics
  } = video;

  const videoId = typeof id === 'string' ? id : id?.videoId;
  const thumbnailUrl = snippet?.thumbnails?.medium?.url
  const title = snippet?.title || 'Untitled Video';
  const channelTitle = snippet?.channelTitle || 'Unknown Channel';
  const publishTime = snippet?.publishedAt;
  const viewCount = statistics?.viewCount || '0';

  // Generate a simple avatar placeholder based on channel name
  const getChannelAvatar = (channelName) => {
    const colors = ['#ff4444', '#44ff44', '#4444ff', '#ffff44', '#ff44ff', '#44ffff'];
    const colorIndex = channelName.length % colors.length;
    const initial = channelName.charAt(0).toUpperCase();
    
    return {
      backgroundColor: colors[colorIndex],
      initial: initial
    };
  };

  // Format publish time
  const getTimeAgo = (publishTime) => {
    if (!publishTime) return '';
    return moment(publishTime).fromNow();
  };

  // Handle image loading error
  const handleImageError = (e) => {
    e.target.style.backgroundColor = '#f1f1f1';
    e.target.alt = 'Video thumbnail not available';
  };

  const avatarStyle = getChannelAvatar(channelTitle);

  return (
    <Link to={`video/${videoId}`} className="feed">
      <div className="card">
        <div className="thumbnail-container">
          <img 
            src={thumbnailUrl} 
            alt={title}
            onError={handleImageError}
          />
        </div>
        <div className="card-info">
          <h2>{title}</h2>
          <h3>{channelTitle}</h3>
          <p>{formatViewCount(viewCount)} views • {getTimeAgo(publishTime)}</p>
        </div>
      </div>
    </Link>
  );
};

export default VideoCard;