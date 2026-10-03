import { useState, useEffect, useRef } from "react";
import { Header } from "../components/common/Header";
import { Footer } from "../components/common/Footer";
import { stationService } from "../services/stationService";
import { TrainFront } from "lucide-react";
import { SkeletonBar } from "../components/common/Skeleton";
import { getCached, setCached } from "../utils/apiCache";

function StationBoardTableSkeleton({ rows = 5 }) {
  return Array.from({ length: rows }).map((_, i) => (
    <div className="skeleton-row" key={i}>
      <SkeletonBar width={60} height={16} />
      <SkeletonBar width={170} height={16} style={{ flex: 1 }} />
      <SkeletonBar width={130} height={14} />
      <SkeletonBar width={56} height={26} radius={6} />
      <SkeletonBar width={72} height={16} />
      <SkeletonBar width={90} height={24} radius={6} />
    </div>
  ));
}

function StationBoardSkeleton() {
  return (
    <>
      <section className="hero-banner board-hero" aria-hidden="true">
        <div className="board-hero-info">
          <div className="banner-eyebrow">
            <SkeletonBar dark width={100} height={12} />
            <SkeletonBar dark width={70} height={18} radius={6} />
          </div>
          <div style={{ marginTop: 10 }}>
            <SkeletonBar dark width="65%" height={30} />
          </div>
          <div style={{ marginTop: 10 }}>
            <SkeletonBar dark width="35%" height={14} />
          </div>
          <div className="board-clock">
            <SkeletonBar dark width={150} height={32} />
            <SkeletonBar dark width={170} height={14} />
          </div>
        </div>

        <div className="board-search">
          <SkeletonBar dark width="100%" height={44} radius={8} />
          <div className="chip-row">
            {Array.from({ length: 5 }).map((_, i) => (
              <SkeletonBar key={i} dark width={56} height={34} radius={8} />
            ))}
          </div>
        </div>
      </section>

      <section className="data-card" aria-label="Loading station board" aria-busy="true">
        <div className="data-card-header dark">
          <SkeletonBar width={220} height={18} />
        </div>
        <StationBoardTableSkeleton />
      </section>
    </>
  );
}

export function StationBoardPage({ onSelectTrain, onNavigateHome, onNavigateView }) {
  const [stationCode, setStationCode] = useState("INDB");
  const [searchInput, setSearchInput] = useState("INDB");
  const [currentTime, setCurrentTime] = useState(new Date());
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);
  const [isRealApi, setIsRealApi] = useState(false);
  const [stationName, setStationName] = useState("");
  const [divisionText, setDivisionText] = useState("");
  const [trainsList, setTrainsList] = useState([]);

  // Live timer for clock
  useEffect(() => {
    const timer = setInterval(() => setCurrentTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  // Pre-configured fallback demo data
  const fallbackStationData = {
    INDB: {
      name: "INDORE JUNCTION (INDB)",
      division: "Western Railway (WR) • Ratlam Division",
      trains: [
        {
          trainNumber: "12919",
          trainName: "Malwa SF Express",
          route: "INDB → SVDK",
          platform: "PF 4",
          scheduled: "23:55 IST",
          expected: "23:55 IST",
          delayMinutes: 0,
          status: "AT PLATFORM",
        },
        {
          trainNumber: "19313",
          trainName: "Indore - Patna Express",
          route: "INDB → PNBE",
          platform: "PF 1",
          scheduled: "13:55 IST",
          expected: "14:10 IST",
          delayMinutes: 15,
          status: "DELAYED 15M",
        },
      ],
    },
    NDLS: {
      name: "NEW DELHI (NDLS)",
      division: "Northern Railway (NR) • Delhi Division",
      trains: [
        {
          trainNumber: "12919",
          trainName: "Malwa SF Express",
          route: "INDB → SVDK",
          platform: "PF 16",
          scheduled: "19:30 IST",
          expected: "19:45 IST",
          delayMinutes: 15,
          status: "DELAYED 15M",
        },
        {
          trainNumber: "12301",
          trainName: "Howrah Rajdhani",
          route: "HWH → NDLS",
          platform: "PF 1",
          scheduled: "10:05 IST",
          expected: "10:05 IST",
          delayMinutes: 0,
          status: "ARRIVING SOON",
        },
        {
          trainNumber: "12004",
          trainName: "Lucknow Shatabdi",
          route: "NDLS → LKO",
          platform: "PF 9",
          scheduled: "06:10 IST",
          expected: "06:10 IST",
          delayMinutes: 0,
          status: "ON TIME",
        },
        {
          trainNumber: "22436",
          trainName: "Vande Bharat Express",
          route: "NDLS → BSB",
          platform: "PF 11",
          scheduled: "06:00 IST",
          expected: "06:00 IST",
          delayMinutes: 0,
          status: "ON TIME",
        },
      ],
    },
    CNB: {
      name: "KANPUR CENTRAL (CNB)",
      division: "North Central Railway (NCR) • Kanpur Division",
      trains: [
        {
          trainNumber: "15654",
          trainName: "Amarnath Express",
          route: "JAT → GHY",
          platform: "PF 5",
          scheduled: "05:25 IST",
          expected: "06:03 IST",
          delayMinutes: 38,
          status: "DELAYED 38M",
        },
        {
          trainNumber: "12556",
          trainName: "Gorakhdham SF Express",
          route: "HSR → GKP",
          platform: "PF 3",
          scheduled: "03:15 IST",
          expected: "03:15 IST",
          delayMinutes: 0,
          status: "ON TIME",
        },
      ],
    },
    BPL: {
      name: "BHOPAL JUNCTION (BPL)",
      division: "West Central Railway (WCR) • Bhopal Division",
      trains: [
        {
          trainNumber: "12919",
          trainName: "Malwa SF Express",
          route: "INDB → SVDK",
          platform: "PF 2",
          scheduled: "17:35 IST",
          expected: "17:35 IST",
          delayMinutes: 0,
          status: "ON TIME",
        },
      ],
    },
  };

  const buildRealStationData = (dataObj, cleanCode) => {
    const mapped = dataObj.trains.map((item) => {
      const t = item.train || {};
      const s = item.stop || {};
      const l = item.live || {};

      const delay = l.delayMinutes || 0;
      let statusText = "ON TIME";
      if (delay > 0) {
        statusText = `DELAYED ${delay}M`;
      } else if (l.type === "at-station") {
        statusText = "AT PLATFORM";
      } else if (l.type === "upcoming") {
        statusText = "APPROACHING";
      } else if (l.type === "departed") {
        statusText = "DEPARTED";
      }

      let expTime = s.arrival || s.departure || "12:00";
      if (l.expectedDepartureTime) {
        try {
          const d = new Date(l.expectedDepartureTime);
          expTime = d.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit", hour12: false }) + " IST";
        } catch (e) {}
      } else if (l.expectedArrivalTime) {
        try {
          const d = new Date(l.expectedArrivalTime);
          expTime = d.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit", hour12: false }) + " IST";
        } catch (e) {}
      } else {
        expTime += " IST";
      }

      return {
        trainNumber: t.number || "N/A",
        trainName: t.name || "Express Train",
        route: `${t.source || 'SRC'} → ${t.destination || 'DEST'}`,
        platform: l.platform ? `PF ${l.platform}` : "PF --",
        scheduled: (s.arrival || s.departure || "--:--") + " IST",
        expected: expTime,
        delayMinutes: delay,
        status: statusText,
      };
    });

    return {
      isRealApi: true,
      stationName: `${dataObj.station?.name || cleanCode} (${cleanCode})`,
      divisionText: "Live arrivals and departures",
      trainsList: mapped,
    };
  };

  const buildFallbackData = (code) => {
    // Only INDB/NDLS/CNB/BPL have curated sample trains. Any other station
    // genuinely has no data right now — don't mislabel it as one of those.
    const fb = fallbackStationData[code];
    if (fb) {
      return {
        isRealApi: false,
        stationName: fb.name,
        divisionText: fb.division,
        trainsList: fb.trains,
      };
    }
    return {
      isRealApi: false,
      stationName: code,
      divisionText: "No live data available for this station",
      trainsList: [],
    };
  };

  const applyStationData = (data) => {
    setIsRealApi(data.isRealApi);
    setStationName(data.stationName);
    setDivisionText(data.divisionText);
    setTrainsList(data.trainsList);
  };

  const fetchStationBoardData = async (codeToFetch) => {
    const cleanCode = (codeToFetch || "INDB").trim().toUpperCase();
    const cacheKey = `stationBoard:${cleanCode}`;

    // Already fetched this station this session — reuse it instead of hitting the API again
    const cached = getCached(cacheKey);
    if (cached) {
      applyStationData(cached);
      setInitialLoading(false);
      return;
    }

    setLoading(true);
    try {
      const res = await stationService.getLiveStationBoard(cleanCode);
      const dataObj = res?.data?.data || res?.data;

      const result =
        dataObj && dataObj.trains && dataObj.trains.length > 0
          ? buildRealStationData(dataObj, cleanCode)
          : buildFallbackData(cleanCode);

      // Only cache real data — a fallback means the API failed or was briefly empty,
      // and that shouldn't be remembered as "the answer" for the rest of the session.
      if (result.isRealApi) setCached(cacheKey, result);
      applyStationData(result);
    } catch (error) {
      console.warn("Live station API offline/fallback, using preset schedule for code:", cleanCode);
      const result = buildFallbackData(cleanCode);
      applyStationData(result);
    } finally {
      setLoading(false);
      setInitialLoading(false);
    }
  };

  // Fetch live board data whenever stationCode changes
  useEffect(() => {
    fetchStationBoardData(stationCode);
  }, [stationCode]);

  const handleSearchSubmit = (e) => {
    if (e && e.preventDefault) e.preventDefault();
    if (searchInput.trim()) {
      setStationCode(searchInput.trim().toUpperCase());
      setShowStationDropdown(false);
    }
  };

  const [stationSuggestions, setStationSuggestions] = useState([]);
  const [showStationDropdown, setShowStationDropdown] = useState(false);
  const stationDropdownRef = useRef(null);

  // Close station dropdown on click outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (stationDropdownRef.current && !stationDropdownRef.current.contains(event.target)) {
        setShowStationDropdown(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Autocomplete search for stations
  useEffect(() => {
    const query = (searchInput || "").trim();
    if (query.length < 2) {
      setStationSuggestions([]);
      setShowStationDropdown(false);
      return;
    }

    const timer = setTimeout(async () => {
      try {
        const res = await stationService.searchStations(query);
        const list = res?.data?.data || res?.data || [];
        if (Array.isArray(list) && list.length > 0) {
          setStationSuggestions(list.slice(0, 6));
          // only pop the list open while the user is typing in the field
          setShowStationDropdown(document.activeElement?.id === "station-search");
        } else {
          setStationSuggestions([]);
          setShowStationDropdown(false);
        }
      } catch (err) {
        setStationSuggestions([]);
        setShowStationDropdown(false);
      }
    }, 250);

    return () => clearTimeout(timer);
  }, [searchInput]);

  const handleSelectStationSuggestion = (item) => {
    const code = item.code || searchInput;
    setSearchInput(code);
    setStationCode(code);
    setShowStationDropdown(false);
  };

  const statusTone = (delay) => (delay > 20 ? "bad" : delay > 0 ? "warn" : "good");

  // Only INDB/NDLS/CNB/BPL have curated sample trains to fall back to;
  // any other station with no live data has nothing to show at all.
  const hasFallbackSample = !isRealApi && trainsList.length > 0;

  return (
    <div className="result-page board-page">
      <Header
        onNavigateHome={onNavigateHome}
        onNavigateView={onNavigateView}
        activeView="stationboard"
      />

      <main className="result-content page-wide">
        {initialLoading ? (
          <StationBoardSkeleton />
        ) : (
          <>
        {/* PUBLIC FIDS DISPLAY HEADER */}
        <section className="hero-banner board-hero">
          <div className="board-hero-info">
            <div className="banner-eyebrow">
              <span>STATION BOARD</span>
              <span className={`feed-badge ${isRealApi ? "live" : ""}`}>
                {isRealApi ? "LIVE" : hasFallbackSample ? "SCHEDULED DATA" : "NO LIVE DATA"}
              </span>
            </div>

            <h1 className="banner-title">{stationName}</h1>
            <div className="banner-subtitle">{divisionText}</div>

            <div className="board-clock" aria-label="Current time">
              <span className="board-clock-time">{currentTime.toLocaleTimeString("en-IN")}</span>
              <span className="board-clock-date">
                {currentTime.toLocaleDateString("en-IN", {
                  weekday: "long",
                  day: "2-digit",
                  month: "short",
                  year: "numeric",
                })}
              </span>
            </div>
          </div>

          <div className="board-search">
            {/* LIVE SEARCH FORM WITH AUTOCOMPLETE DROPDOWN */}
            <div className="board-search-box" ref={stationDropdownRef}>
              <form onSubmit={handleSearchSubmit} className="board-search-form" role="search">
                <label htmlFor="station-search" className="sr-only">
                  Station code or name
                </label>
                <input
                  id="station-search"
                  type="search"
                  enterKeyHint="search"
                  autoComplete="off"
                  value={searchInput}
                  onChange={(e) => setSearchInput(e.target.value)}
                  onFocus={() => {
                    if (stationSuggestions.length > 0) setShowStationDropdown(true);
                  }}
                  placeholder="Station code or name, e.g. NDLS"
                />
                <button type="submit" disabled={loading}>
                  {loading ? "Loading…" : "Search"}
                </button>
              </form>

              {/* STATION AUTOCOMPLETE DROPDOWN */}
              {showStationDropdown && stationSuggestions.length > 0 && (
                <div className="suggestions board-suggestions" role="listbox">
                  <div className="suggestions-title">Matching stations</div>
                  {stationSuggestions.map((item, idx) => (
                    <button
                      type="button"
                      role="option"
                      aria-selected="false"
                      key={`${item.code}-${idx}`}
                      className="suggestion-item"
                      onClick={() => handleSelectStationSuggestion(item)}
                    >
                      <span className="suggestion-main">
                        <span className="suggestion-number">{item.code}</span>
                        <span className="suggestion-name">{item.name}</span>
                      </span>
                      {item.city && <span className="suggestion-route">{item.city}</span>}
                    </button>
                  ))}
                </div>
              )}
            </div>

            {/* PRESET QUICK SWITCHER */}
            <div className="chip-row" role="group" aria-label="Popular stations">
              {["INDB", "NDLS", "CNB", "BPL", "AGC"].map((code) => (
                <button
                  key={code}
                  type="button"
                  className={`chip ${stationCode === code ? "active" : ""}`}
                  aria-pressed={stationCode === code}
                  onClick={() => {
                    setSearchInput(code);
                    setStationCode(code);
                  }}
                >
                  {code}
                </button>
              ))}
            </div>
          </div>
        </section>

        {/* HIGH VISIBILITY PLATFORM BOARD */}
        <section className="data-card" aria-label="Live platform board">
          <div className="data-card-header dark">
            <h2 className="data-card-title">
              <TrainFront size={20} aria-hidden="true" />
              Arrivals and departures
            </h2>
            {!isRealApi && (
              <span>
                {hasFallbackSample
                  ? "Live feed unavailable, showing scheduled departures"
                  : "Live feed unavailable for this station"}
              </span>
            )}
          </div>

          {loading ? (
            <StationBoardTableSkeleton />
          ) : trainsList.length === 0 ? (
            <div className="empty-state">
              No active trains scheduled at {stationCode} in the next 4 hours.
            </div>
          ) : (
            <table className="responsive-table board-table">
              <thead>
                <tr>
                  <th>Train No.</th>
                  <th>Train Name</th>
                  <th>Route</th>
                  <th>Platform</th>
                  <th>Scheduled</th>
                  <th>Expected</th>
                  <th>Status</th>
                  <th>
                    <span className="sr-only">Action</span>
                  </th>
                </tr>
              </thead>
              <tbody>
                {trainsList.map((t, idx) => (
                  <tr key={t.trainNumber + idx}>
                    <td className="cell-train-number" data-label="Train No.">
                      {t.trainNumber}
                    </td>
                    <td className="cell-train-name" data-label="Train">
                      {t.trainName}
                    </td>
                    <td className="cell-muted" data-label="Route">
                      {t.route}
                    </td>
                    <td data-label="Platform">
                      <span className="platform-pill">{t.platform}</span>
                    </td>
                    <td data-label="Scheduled">{t.scheduled}</td>
                    <td
                      data-label="Expected"
                      className={`cell-strong ${t.delayMinutes > 0 ? "text-bad" : "text-good"}`}
                    >
                      {t.expected}
                    </td>
                    <td data-label="Status">
                      <span className={`status-pill ${statusTone(t.delayMinutes)}`}>{t.status}</span>
                    </td>
                    <td className="cell-action">
                      <button
                        type="button"
                        className="btn-track"
                        onClick={() => onSelectTrain(t.trainNumber)}
                      >
                        Track train →
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </section>
          </>
        )}
      </main>

      <Footer />
    </div>
  );
}
