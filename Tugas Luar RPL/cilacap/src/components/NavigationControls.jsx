import { ChevronLeft, ChevronRight } from 'lucide-react';
import './NavigationControls.css';

const NavigationControls = ({ currentPage, totalPages, onNavigate }) => {
  const canGoPrev = currentPage > 0;
  const canGoNext = currentPage < totalPages - 1;

  return (
    <div className="navigation-controls">
      <button
        className={`nav-button nav-prev ${!canGoPrev ? 'disabled' : ''}`}
        onClick={() => canGoPrev && onNavigate(currentPage - 1)}
        disabled={!canGoPrev}
        aria-label="Halaman sebelumnya"
      >
        <ChevronLeft size={32} />
      </button>

      <button
        className={`nav-button nav-next ${!canGoNext ? 'disabled' : ''}`}
        onClick={() => canGoNext && onNavigate(currentPage + 1)}
        disabled={!canGoNext}
        aria-label="Halaman berikutnya"
      >
        <ChevronRight size={32} />
      </button>
    </div>
  );
};

export default NavigationControls;
