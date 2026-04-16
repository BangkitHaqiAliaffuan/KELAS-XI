import { Search, Mic } from "lucide-react";
import { useState, useRef, useEffect } from "react";
import { hospitalLocations, type HospitalLocation } from "@/data/hospitalLocations";

interface SearchBarProps {
  onSelectLocation: (location: HospitalLocation) => void;
}

const SearchBar = ({ onSelectLocation }: SearchBarProps) => {
  const [query, setQuery] = useState("");
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  const filtered = query.length > 0
    ? hospitalLocations.filter(
        (l) => {
          const lowerQuery = query.toLowerCase();
          const isNumber = !isNaN(Number(query));
          
          return (
            l.name.toLowerCase().includes(lowerQuery) ||
            l.location.toLowerCase().includes(lowerQuery) ||
            (isNumber && l.svgIndex?.toString() === query)
          );
        }
      )
    : [];

  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false);
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  return (
    <div ref={ref} className="relative w-full max-w-xl mx-auto">
      <div className="flex items-center bg-card rounded-xl border border-border shadow-sm px-4 py-2.5 gap-3 focus-within:ring-2 focus-within:ring-primary/20 focus-within:border-primary transition-all">
        <Search className="h-5 w-5 text-muted-foreground shrink-0" />
        <input
          type="text"
          placeholder="Search by district name or ID (e.g., Sidoarjo or 11)..."
          value={query}
          onChange={(e) => { setQuery(e.target.value); setOpen(true); }}
          onFocus={() => setOpen(true)}
          className="flex-1 bg-transparent outline-none text-sm text-foreground placeholder:text-muted-foreground"
        />
        {query && (
          <button 
            onClick={() => { setQuery(""); setOpen(false); }}
            className="text-muted-foreground hover:text-foreground p-1"
          >
            <span className="text-xs font-bold">✕</span>
          </button>
        )}
        <div className="h-4 w-[1px] bg-border mx-1" />
        <button className="text-muted-foreground hover:text-foreground transition-colors p-1 rounded-full hover:bg-muted">
          <Mic className="h-5 w-5" />
        </button>
      </div>

      {open && filtered.length > 0 && (
        <div className="absolute top-full left-0 right-0 mt-2 bg-card border border-border rounded-xl shadow-xl z-50 max-h-80 overflow-y-auto animate-in fade-in slide-in-from-top-2 duration-200">
          <div className="p-2 border-b border-border bg-muted/30">
            <p className="text-[10px] font-bold text-muted-foreground uppercase tracking-wider px-2">Suggestions</p>
          </div>
          {filtered.map((loc) => (
            <button
              key={loc.id}
              onClick={() => { onSelectLocation(loc); setQuery(loc.name); setOpen(false); }}
              className="w-full text-left px-4 py-3 hover:bg-accent transition-colors flex items-center justify-between text-sm group"
            >
              <div className="flex flex-col">
                <p className="font-semibold text-foreground group-hover:text-accent-foreground flex items-center gap-2">
                  {loc.name}
                  {loc.svgIndex && <span className="text-[10px] bg-muted px-1.5 py-0.5 rounded text-muted-foreground font-mono">ID: {loc.svgIndex}</span>}
                </p>
                <p className="text-xs text-muted-foreground group-hover:text-accent-foreground/80">{loc.building} • {loc.location}</p>
              </div>
              <div className="flex items-center gap-2">
                <span className="text-[10px] px-2 py-0.5 rounded-full bg-primary/10 text-primary border border-primary/20 font-medium">
                  {loc.category}
                </span>
                <span className="text-muted-foreground group-hover:translate-x-1 transition-transform">
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
          <p className="text-sm font-medium text-foreground">No results found for "{query}"</p>
          <p className="text-xs text-muted-foreground mt-1">Try searching by district name or ID number.</p>
        </div>
      )}
    </div>
  );
};

export default SearchBar;
