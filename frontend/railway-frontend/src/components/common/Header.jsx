import { TrainFront, Monitor, Gauge } from "lucide-react";

const NAV_ITEMS = [
  { view: "passenger", Icon: TrainFront, label: "Train Status", shortLabel: "Train" },
  { view: "stationboard", Icon: Monitor, label: "Station Board", shortLabel: "Station" },
  { view: "controlroom", Icon: Gauge, label: "Control Room", shortLabel: "Control" },
];

export function Header({
  onNavigateHome,
  onNavigateView,
  activeView = "passenger",
}) {
  const handleNav = (view) => {
    if (view === "passenger" && onNavigateHome) {
      onNavigateHome();
    } else if (onNavigateView) {
      onNavigateView(view);
    }
  };

  return (
    <header className="main-header">
      <div className="header-inner">
        <button
          type="button"
          className="brand"
          onClick={() => handleNav("passenger")}
          aria-label="RailX home"
        >
          <span className="brand-name">
            Rail<span>X</span>
          </span>
          <span className="brand-tagline">
            Smart Railway Tracking System
          </span>
        </button>

        {/* Segmented tabs on desktop; fixed bottom tab bar on mobile */}
        <nav className="navigation" aria-label="Main">
          {NAV_ITEMS.map((item) => (
            <button
              key={item.view}
              type="button"
              className={`nav-tab-btn ${activeView === item.view ? "active" : ""}`}
              aria-current={activeView === item.view ? "page" : undefined}
              onClick={() => handleNav(item.view)}
            >
              <item.Icon className="nav-icon" size={18} strokeWidth={2} aria-hidden="true" />
              <span className="nav-label">{item.label}</span>
              <span className="nav-label-short">{item.shortLabel}</span>
            </button>
          ))}
        </nav>
      </div>
    </header>
  );
}
