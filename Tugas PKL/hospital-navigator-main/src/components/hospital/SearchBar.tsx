import { Search } from "lucide-react";
import { useState, useRef, useEffect } from "react";
import { roomInfoBySvgId, type HospitalRoomInfo } from "@/data/hospitalRoomInfo";

interface SearchBarProps {
  onSelectLocation: (location: HospitalRoomInfo) => void;
  language: "id" | "en";
}

const SearchBar = ({ onSelectLocation, language }: SearchBarProps) => {
  const copy = language === "id"
    ? {
        placeholder: "Cari ruangan rumah sakit (e.g., IGD, Lab, Farmasi)...",
        suggestions: "Saran",
        noResultsTitle: (value: string) => `Tidak ada hasil untuk "${value}"`,
        noResultsHint: "Coba cari berdasarkan nama ruangan atau kategori.",
      }
    : {
        placeholder: "Search hospital rooms (e.g., ER, Lab, Pharmacy)...",
        suggestions: "Suggestions",
        noResultsTitle: (value: string) => `No results found for "${value}"`,
        noResultsHint: "Try searching by room name or category.",
      };
  const [query, setQuery] = useState("");
  const [open, setOpen] = useState(false);
  const [highlightedIndex, setHighlightedIndex] = useState(-1);
  const ref = useRef<HTMLDivElement>(null);
  const listRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const roomList = Object.values(roomInfoBySvgId);

  const filtered = query.length > 0
    ? roomList.filter(
        (l) => {
          const lowerQuery = query.toLowerCase();
          
          return (
            l.name.toLowerCase().includes(lowerQuery) ||
            l.category.toLowerCase().includes(lowerQuery) ||
            l.locationHint.toLowerCase().includes(lowerQuery) ||
            l.description.toLowerCase().includes(lowerQuery)
          );
        }
      )
    : [];

  // Reset highlighted index when filtered results change
  useEffect(() => {
    setHighlightedIndex(-1);
  }, [query]);

  // Close dropdown on outside click
  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false);
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  const selectLocation = (loc: HospitalRoomInfo) => {
    onSelectLocation(loc);
    setQuery(loc.name);
    setOpen(false);
    inputRef.current?.blur();
  };

  // Scroll highlighted item into view
  useEffect(() => {
    if (highlightedIndex < 0 || !listRef.current) return;
    const items = listRef.current.querySelectorAll('[data-search-item]');
    if (items[highlightedIndex]) {
      items[highlightedIndex].scrollIntoView({ block: 'nearest' });
    }
  }, [highlightedIndex]);

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (!open || filtered.length === 0) {
      if (e.key === 'Escape') {
        setQuery('');
        setOpen(false);
        inputRef.current?.blur();
      }
      return;
    }

    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        setHighlightedIndex((prev) =>
          prev < filtered.length - 1 ? prev + 1 : 0
        );
        break;
      case 'ArrowUp':
        e.preventDefault();
        setHighlightedIndex((prev) =>
          prev > 0 ? prev - 1 : filtered.length - 1
        );
        break;
      case 'Enter':
        e.preventDefault();
        if (highlightedIndex >= 0 && highlightedIndex < filtered.length) {
          selectLocation(filtered[highlightedIndex]);
        }
        break;
      case 'Escape':
        e.preventDefault();
        setOpen(false);
        setHighlightedIndex(-1);
        inputRef.current?.blur();
        break;
    }
  };

  return (
    <div ref={ref} className="relative w-full max-w-xl mx-auto">
      <div className="flex items-center bg-card rounded-xl border border-border shadow-sm px-4 py-2.5 gap-3 focus-within:ring-2 focus-within:ring-primary/20 focus-within:border-primary transition-all">
        <Search className="h-5 w-5 text-muted-foreground shrink-0" />
        <input
          ref={inputRef}
          type="text"
          placeholder={copy.placeholder}
          value={query}
          onChange={(e) => { setQuery(e.target.value); setOpen(true); }}
          onFocus={() => setOpen(true)}
          onKeyDown={handleKeyDown}
          className="flex-1 bg-transparent outline-none text-sm text-foreground placeholder:text-muted-foreground"
        />
        {query && (
          <button 
            onClick={() => { setQuery(""); setOpen(false); setHighlightedIndex(-1); }}
            className="text-muted-foreground hover:text-foreground p-1"
          >
            <span className="text-xs font-bold">✕</span>
          </button>
        )}
      </div>

      {open && filtered.length > 0 && (
        <div ref={listRef} className="absolute top-full left-0 right-0 mt-2 bg-card border border-border rounded-xl shadow-xl z-50 max-h-80 overflow-y-auto animate-in fade-in slide-in-from-top-2 duration-200">
          <div className="p-2 border-b border-border bg-muted/30">
            <p className="text-[10px] font-bold text-muted-foreground uppercase tracking-wider px-2">{copy.suggestions}</p>
          </div>
          {filtered.map((loc, index) => (
            <button
              key={loc.id}
              data-search-item
              onClick={() => selectLocation(loc)}
              onMouseEnter={() => setHighlightedIndex(index)}
              className={`w-full text-left px-4 py-3 transition-colors flex items-center justify-between text-sm group ${
                index === highlightedIndex
                  ? 'bg-accent'
                  : 'hover:bg-accent'
              }`}
            >
              <div className="flex flex-col">
                <p className={`font-semibold flex items-center gap-2 ${
                  index === highlightedIndex
                    ? 'text-accent-foreground'
                    : 'text-foreground group-hover:text-accent-foreground'
                }`}>
                  {loc.name}
                </p>
                <p className={`text-xs ${
                  index === highlightedIndex
                    ? 'text-accent-foreground/80'
                    : 'text-muted-foreground group-hover:text-accent-foreground/80'
                }`}>📍 {loc.locationHint}</p>
              </div>
              <div className="flex items-center gap-2">
                <span className={`text-[10px] px-2 py-0.5 rounded-full border font-medium ${
                  index === highlightedIndex
                    ? 'bg-primary/20 text-primary border-primary/30'
                    : 'bg-primary/10 text-primary border-primary/20'
                }`}>
                  {loc.category}
                </span>
                <span className={`transition-transform ${
                  index === highlightedIndex
                    ? 'text-accent-foreground translate-x-1'
                    : 'text-muted-foreground group-hover:translate-x-1'
                }`}>
                  <Search className="h-3 w-3" />
                </span>
              </div>
            </button>
          ))}
        </div>
      )}
      {open && query.length > 0 && filtered.length === 0 && (
        <div className="absolute top-full left-0 right-0 mt-2 bg-card border border-border rounded-xl shadow-xl z-50 p-6 text-center animate-in fade-in slide-in-from-top-2 duration-200">
          <div className="bg-muted rounded-full w-12 h-12 flex items-center justify-center mx-auto mb-3">
            <Search className="h-6 w-6 text-muted-foreground" />
          </div>
          <p className="text-sm font-medium text-foreground">{copy.noResultsTitle(query)}</p>
          <p className="text-xs text-muted-foreground mt-1">{copy.noResultsHint}</p>
        </div>
      )}
    </div>
  );
};

export default SearchBar;
