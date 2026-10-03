import { History, X } from "lucide-react";

export function RecentSearches({
  recentSearches,
  onSelectSearch,
  onRemoveSearch,
  onClearAll,
}) {
  if (!recentSearches || recentSearches.length === 0) {
    return null;
  }

  return (
    <section className="recent-section" aria-labelledby="recent-title">
      <div className="recent-header">
        <h2 className="recent-title" id="recent-title">
          Recent searches
        </h2>

        <button type="button" className="clear-history-btn" onClick={onClearAll}>
          Clear all
        </button>
      </div>

      <ul className="recent-grid">
        {recentSearches.map((item) => (
          <li className="recent-card" key={item.trainNumber}>
            <button
              type="button"
              className="recent-card-main"
              onClick={() => onSelectSearch(item.trainNumber)}
            >
              <span className="recent-icon" aria-hidden="true">
                <History size={18} />
              </span>
              <span className="recent-info">
                <span className="recent-train-number">{item.trainNumber}</span>
                <span className="recent-train-name">{item.trainName}</span>
                <span className="recent-train-route">
                  {item.source} → {item.destination}
                </span>
              </span>
            </button>

            <button
              type="button"
              className="recent-remove-btn"
              onClick={() => onRemoveSearch(item.trainNumber)}
              aria-label={`Remove ${item.trainNumber} from recent searches`}
            >
              <X size={16} aria-hidden="true" />
            </button>
          </li>
        ))}
      </ul>
    </section>
  );
}
