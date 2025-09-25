import React from 'react';
import { useParams } from 'react-router-dom';
import PlayVideo from '../components/PlayVideo';
import Recommended from '../components/Recommended';
import './Video.css';

const Video = () => {
  const { videoId } = useParams();

  return (
    <div className="video-page">
      <div className="video-container">
        <PlayVideo />
        <Recommended videoId={videoId} />
      </div>
    </div>
  );
};

export default Video;