import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import moment from 'moment';
import { fetchVideoDetails, fetchChannelDetails, formatViewCount } from '../data/api';
import assets from '../assets/assets';
import './PlayVideo.css';

const PlayVideo = () => {
  const { videoId } = useParams();
  const [videoData, setVideoData] = useState(null);
  const [channelData, setChannelData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      
      try {
        // Fetch video details
        const video = await fetchVideoDetails(videoId);
        if (video) {
          setVideoData(video);
          
          // Fetch channel details
          const channel = await fetchChannelDetails(video.snippet.channelId);
          setChannelData(channel);
        } else {
          setError('Video not found');
        }
      } catch (err) {
        console.error('Error fetching video data:', err);
        setError('Failed to load video. Please check your API key or try again.');
      } finally {
        setLoading(false);
      }
    };

    if (videoId) {
      fetchData();
    }
  }, [videoId]);

  if (loading) {
    return (
      <div className="play-video">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Loading video...</p>
        </div>
      </div>
    );
  }

  if (error || !videoData) {
    return (
      <div className="play-video">
        <div className="error-container">
          <h3>⚠️ Error</h3>
          <p>{error || 'Video not found'}</p>
        </div>
      </div>
    );
  }

  const { snippet, statistics } = videoData;
  const publishTime = moment(snippet.publishedAt).fromNow();
  const viewCount = formatViewCount(statistics.viewCount);
  const likeCount = formatViewCount(statistics.likeCount);
  const subscriberCount = channelData ? formatViewCount(channelData.statistics.subscriberCount) : 'Unknown';

  return (
    <div className="play-video">
      {/* Video Player */}
      <div className="video-player">
        <iframe
          src={`https://www.youtube.com/embed/${videoId}?autoplay=1`}
          frameBorder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowFullScreen
          title={snippet.title}
        ></iframe>
      </div>

      {/* Video Info */}
      <div className="video-info">
        <h3>{snippet.title}</h3>
        <div className="video-stats">
          <p>{viewCount} views • {publishTime}</p>
          <div className="video-actions">
            <span>
              <img src={assets.like} alt="Like" />
              {likeCount}
            </span>
            <span>
              <img src={assets.dislike} alt="Dislike" />
            </span>
            <span>
              <img src={assets.share} alt="Share" />
              Share
            </span>
            <span>
              <img src={assets.save} alt="Save" />
              Save
            </span>
          </div>
        </div>
      </div>

      <hr />

      {/* Channel Info */}
      <div className="channel-info">
        <div className="channel-details">
          <img 
            src={channelData?.snippet?.thumbnails?.default?.url || assets.user_profile} 
            alt={snippet.channelTitle}
          />
          <div>
            <p className="channel-name">{snippet.channelTitle}</p>
            <span className="subscriber-count">{subscriberCount} subscribers</span>
          </div>
        </div>
        <button className="subscribe-btn">Subscribe</button>
      </div>

      {/* Video Description */}
      <div className="video-description">
        <p>{snippet.description}</p>
      </div>

      <hr />

      {/* Comments Section */}
      <div className="comments-section">
        <h4>{statistics.commentCount || '0'} Comments</h4>
        
        <div className="comment">
          <img src={assets.user_profile} alt="User" />
          <div>
            <h3>Jack Nicholson <span>1 day ago</span></h3>
            <p>A global computer network providing a variety of information and communication facilities.</p>
            <div className="comment-actions">
              <img src={assets.like} alt="Like" />
              <span>244</span>
              <img src={assets.dislike} alt="Dislike" />
            </div>
          </div>
        </div>

        <div className="comment">
          <img src={assets.user_profile} alt="User" />
          <div>
            <h3>Simon Baker <span>1 day ago</span></h3>
            <p>A global computer network providing a variety of information and communication facilities.</p>
            <div className="comment-actions">
              <img src={assets.like} alt="Like" />
              <span>244</span>
              <img src={assets.dislike} alt="Dislike" />
            </div>
          </div>
        </div>

        <div className="comment">
          <img src={assets.user_profile} alt="User" />
          <div>
            <h3>Tom Williams <span>1 day ago</span></h3>
            <p>A global computer network providing a variety of information and communication facilities.</p>
            <div className="comment-actions">
              <img src={assets.like} alt="Like" />
              <span>244</span>
              <img src={assets.dislike} alt="Dislike" />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PlayVideo;