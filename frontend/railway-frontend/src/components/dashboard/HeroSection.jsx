import { useState, useEffect, useRef } from "react";
import { Search, X } from "lucide-react";
import { ErrorAlert } from "../common/ErrorAlert";
import { trainService } from "../../services/trainService";

export function HeroSection({
  trainNumber,
  setTrainNumber,
  onSearch,
  loading,
  error,
  clearError,
}) {
  const [suggestions, setSuggestions] = useState([]);
  const [showDropdown, setShowDropdown] = useState(false);
  const [activeIndex, setActiveIndex] = useState(-1);
  const dropdownRef = useRef(null);
  const inputRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  useEffect(() => {
    const query = (trainNumber || "").trim();
    if (query.length < 2) return; // dropdown is hidden via queryLongEnough

    const timer = setTimeout(async () => {
      try {
        const res = await trainService.searchTrains(query);
        const list = res?.data?.data || res?.data || [];
        if (Array.isArray(list) && list.length > 0) {
          setSuggestions(list.slice(0, 6));
          setShowDropdown(document.activeElement === inputRef.current);
          setActiveIndex(-1);
        } else {
          setSuggestions([]);
          setShowDropdown(false);
        }
      } catch {
        setSuggestions([]);
        setShowDropdown(false);
      }
    }, 250);

    return () => clearTimeout(timer);
  }, [trainNumber]);

  const queryLongEnough = (trainNumber || "").trim().length >= 2;
  const listboxOpen = showDropdown && queryLongEnough && suggestions.length > 0;

  const submit = (value) => {
    setShowDropdown(false);
    inputRef.current?.blur(); // close the mobile keyboard
    onSearch(value);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (listboxOpen && activeIndex >= 0 && suggestions[activeIndex]) {
      handleSelectSuggestion(suggestions[activeIndex]);
      return;
    }
    submit(trainNumber);
  };

  const handleKeyDown = (e) => {
    if (!listboxOpen) return;
    if (e.key === "ArrowDown") {
      e.preventDefault();
      setActiveIndex((i) => (i + 1) % suggestions.length);
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setActiveIndex((i) => (i <= 0 ? suggestions.length - 1 : i - 1));
    } else if (e.key === "Escape") {
      setShowDropdown(false);
    }
  };

  const handleSelectSuggestion = (item) => {
    const num = item.number || item.trainNumber || trainNumber;
    setTrainNumber(num);
    submit(num);
  };


  return (
    <section className="hero-section">
      <div className="hero-content">
        <h1 className="hero-title">Live train status</h1>

        <p className="hero-description">
          Enter a train number or name to see where it is, how late it is, and when it
          reaches each station.
        </p>

        <form
          className="search-container"
          ref={dropdownRef}
          onSubmit={handleSubmit}
          role="search"
        >
          <div className="search-input-wrapper">
            <Search className="search-icon" size={20} aria-hidden="true" />

            <label htmlFor="train-search" className="sr-only">
              Train number or name
            </label>
            <input
              id="train-search"
              ref={inputRef}
              type="search"
              inputMode="search"
              enterKeyHint="search"
              autoComplete="off"
              value={trainNumber}
              onChange={(e) => {
                setTrainNumber(e.target.value);
                if (clearError) clearError();
              }}
              onFocus={() => {
                if (suggestions.length > 0) setShowDropdown(true);
              }}
              onKeyDown={handleKeyDown}
              placeholder="Train number or name, e.g. 12919"
              role="combobox"
              aria-expanded={listboxOpen}
              aria-controls="train-suggestions"
              aria-activedescendant={
                listboxOpen && activeIndex >= 0 ? `train-suggestion-${activeIndex}` : undefined
              }
            />

            {trainNumber && (
              <button
                type="button"
                className="search-clear"
                onClick={() => {
                  setTrainNumber("");
                  inputRef.current?.focus();
                }}
                aria-label="Clear search"
              >
                <X size={18} aria-hidden="true" />
              </button>
            )}
          </div>

          <button type="submit" className="status-button" disabled={loading}>
            {loading ? "Checking…" : "Check status"}
          </button>

          {/* AUTOCOMPLETE DROPDOWN */}
          {listboxOpen && (
            <div className="suggestions" id="train-suggestions" role="listbox">
              <div className="suggestions-title">Matching trains</div>
              {suggestions.map((item, idx) => (
                <button
                  type="button"
                  key={`${item.number}-${idx}`}
                  id={`train-suggestion-${idx}`}
                  role="option"
                  aria-selected={idx === activeIndex}
                  className={`suggestion-item ${idx === activeIndex ? "active" : ""}`}
                  onMouseEnter={() => setActiveIndex(idx)}
                  onClick={() => handleSelectSuggestion(item)}
                >
                  <span className="suggestion-main">
                    <span className="suggestion-number">{item.number}</span>
                    <span className="suggestion-name">{item.name}</span>
                  </span>
                  {(item.source || item.destination) && (
                    <span className="suggestion-route">
                      {item.source} → {item.destination}
                    </span>
                  )}
                </button>
              ))}
            </div>
          )}
        </form>

        <ErrorAlert message={error} onDismiss={clearError} />
      </div>
    </section>
  );
}
