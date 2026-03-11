import React from 'react';
import { useNavigate } from 'react-router-dom';

export default function Header({
  title,
  showBack = false,
  onBack,
  rightContent = null,
  transparent = false,
}) {
  const navigate = useNavigate();
  const handleBack = onBack ?? (() => navigate(-1));

  return (
    <header className={`flex items-center px-4 py-3 gap-3 z-40 sticky top-0
      ${transparent ? 'bg-transparent' : 'bg-green-primary shadow-sm'}`}>
      {showBack && (
        <button onClick={handleBack}
          className="w-8 h-8 flex items-center justify-center rounded-full
                     bg-white/20 text-white active:bg-white/30 transition-colors flex-shrink-0">
          ←
        </button>
      )}
      <h1 className={`flex-1 font-bold text-base truncate
        ${transparent ? 'text-text-primary' : 'text-white'}`}>
        {title}
      </h1>
      {rightContent && <div className="flex-shrink-0">{rightContent}</div>}
    </header>
  );
}
