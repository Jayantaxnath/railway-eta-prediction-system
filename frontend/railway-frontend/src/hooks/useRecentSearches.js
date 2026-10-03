import { useState } from "react";
import { DEFAULT_RECENT_SEARCHES, RECENT_SEARCHES_KEY } from "../constants/recentSearches";

export function useRecentSearches() {
  const [recentSearches, setRecentSearches] = useState(() => {
    try {
      const saved = localStorage.getItem(RECENT_SEARCHES_KEY);
      if (saved) {
        return JSON.parse(saved);
      }
    } catch (err) {
      console.warn("Could not load recent searches from localStorage:", err);
    }
    return DEFAULT_RECENT_SEARCHES;
  });

  const saveRecentSearch = (item) => {
    setRecentSearches((prev) => {
      const updated = [
        item,
        ...prev.filter((x) => x.trainNumber !== item.trainNumber),
      ].slice(0, 3);

      try {
        localStorage.setItem(RECENT_SEARCHES_KEY, JSON.stringify(updated));
      } catch (err) {
        console.warn("Could not save recent search to localStorage:", err);
      }

      return updated;
    });
  };

  const removeRecentSearch = (trainNumber) => {
    setRecentSearches((prev) => {
      const updated = prev.filter((x) => x.trainNumber !== trainNumber);
      try {
        localStorage.setItem(RECENT_SEARCHES_KEY, JSON.stringify(updated));
      } catch (err) {
        console.warn("Could not update recent searches in localStorage:", err);
      }
      return updated;
    });
  };

  const clearAllRecentSearches = () => {
    setRecentSearches([]);
    try {
      localStorage.removeItem(RECENT_SEARCHES_KEY);
    } catch (err) {
      console.warn("Could not clear recent searches in localStorage:", err);
    }
  };

  return {
    recentSearches,
    saveRecentSearch,
    removeRecentSearch,
    clearAllRecentSearches,
  };
}
