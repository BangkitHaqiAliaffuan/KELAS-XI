import { FaBitcoin } from 'react-icons/fa';
import './PageLoader.css';

const PageLoader = () => {
  return (
    <div className="page-loader" role="status" aria-live="polite" aria-label="Loading page">
      <div className="loader-core single-icon-loader">
        <FaBitcoin className="single-loader-icon" />
      </div>
      <p className="loader-title">Preparing market dashboard...</p>
    </div>
  );
};

export default PageLoader;
