import { assets } from '../assets/assets';
import './LoadingSpinner.css';

const LoadingSpinner = () => {
  return (
    <div className="loading-container">
      <img src={assets.netflixSpinner} alt="Loading..." className="netflix-spinner" />
    </div>
  );
};

export default LoadingSpinner;