import { useState, useEffect, useMemo } from "react";
import { Gauge, RefreshCw, TrainFront, Search } from "lucide-react";
import { FleetMap } from "../components/controlroom/FleetMap";
import { Header } from "../components/common/Header";
import { Footer } from "../components/common/Footer";
import { SkeletonBar } from "../components/common/Skeleton";
import { trainService } from "../services/trainService";
import { getCached, setCached } from "../utils/apiCache";

// The live feed has 2000+ trains; the list shows them in pages
const PAGE_SIZE = 50;
const CACHE_KEY = "controlRoomSnapshot";

function ControlRoomSkeleton() {
  return (
    <>
      <section
        className="train-summary cr-banner"
        aria-hidden="true"
        style={{
          background: "#1d4ed8",
          border: "1px solid #3b82f6",
          borderRadius: "14px",
          marginBottom: "16px",
          padding: "24px 32px",
          display: "flex",
          justifyContent: "space-between",
          flexWrap: "wrap",
          gap: "16px",
        }}
      >
        <div>
          <div style={{ display: "flex", gap: 10 }}>
            <SkeletonBar dark width={100} height={12} />
            <SkeletonBar dark width={70} height={16} radius={6} />
          </div>
          <div style={{ marginTop: 10 }}>
            <SkeletonBar dark width={260} height={26} />
          </div>
          <div style={{ marginTop: 10 }}>
            <SkeletonBar dark width={320} height={14} />
          </div>
        </div>
        <SkeletonBar dark width={100} height={40} radius={8} />
      </section>

      <section className="metrics-grid">
        {Array.from({ length: 5 }).map((_, i) => (
          <div key={i} className="info-panel" style={{ padding: "16px", textAlign: "center" }}>
            <SkeletonBar width={110} height={11} style={{ margin: "0 auto" }} />
            <div style={{ marginTop: 8 }}>
              <SkeletonBar width={70} height={26} style={{ margin: "0 auto" }} />
            </div>
          </div>
        ))}
      </section>

      <section className="map-card fleet-card" aria-label="Loading train positions" aria-busy="true">
        <div className="fleet-card-head">
          <div>
            <SkeletonBar width={160} height={18} />
            <div style={{ marginTop: 6 }}>
              <SkeletonBar width={280} height={13} />
            </div>
          </div>
          <SkeletonBar width={80} height={28} radius={8} />
        </div>
        <div className="fleet-map skeleton" />
      </section>

      <section className="info-panel" style={{ marginBottom: "18px" }}>
        <div className="panel-title" style={{ background: "#075da8" }}>
          <SkeletonBar dark width={160} height={16} />
        </div>
        <div
          style={{
            padding: "16px",
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(260px, 1fr))",
            gap: "12px",
            background: "#f8fbfe",
          }}
        >
          {Array.from({ length: 5 }).map((_, i) => (
            <div key={i} style={{ background: "white", padding: "14px", borderRadius: "8px", border: "1px solid #dcdfe6" }}>
              <SkeletonBar width="70%" height={14} />
              <div style={{ marginTop: 10 }}>
                <SkeletonBar width="100%" height={8} radius={4} />
              </div>
              <div style={{ marginTop: 8, display: "flex", justifyContent: "space-between" }}>
                <SkeletonBar width={90} height={11} />
                <SkeletonBar width={90} height={11} />
              </div>
            </div>
          ))}
        </div>
      </section>

      <section className="station-card">
        <div className="fleet-toolbar">
          <SkeletonBar dark width={160} height={16} />
          <SkeletonBar dark width={260} height={38} radius={8} />
        </div>
        <ControlRoomTableSkeleton />
      </section>
    </>
  );
}

function ControlRoomTableSkeleton({ rows = 6 }) {
  return Array.from({ length: rows }).map((_, i) => (
    <div className="skeleton-row" key={i}>
      <SkeletonBar width={130} height={16} />
      <SkeletonBar width={110} height={14} />
      <SkeletonBar width={160} height={14} style={{ flex: 1 }} />
      <SkeletonBar width={90} height={24} radius={6} />
    </div>
  ));
}

export function ControlRoomPage({ onSelectTrain, onNavigateHome, onNavigateView }) {
  const [filter, setFilter] = useState("all");
  const [query, setQuery] = useState("");
  const [visibleCount, setVisibleCount] = useState(PAGE_SIZE);
  const [loadingMap, setLoadingMap] = useState(true);
  const [initialLoading, setInitialLoading] = useState(true);
  const [isRealFeed, setIsRealFeed] = useState(false);
  const [liveMapSnapshot, setLiveMapSnapshot] = useState([]);

  const presetSections = [
    { name: "Delhi - Kanpur Corridor (NCR)", congestion: 78, speedLimit: 110, status: "CONGESTED" },
    { name: "Kanpur - Prayagraj Section (NCR)", congestion: 85, speedLimit: 90, status: "CRITICAL" },
    { name: "Prayagraj - DDU Section (ECR)", congestion: 42, speedLimit: 130, status: "NORMAL" },
    { name: "Howrah Main Line (ER)", congestion: 35, speedLimit: 130, status: "NORMAL" },
    { name: "Mumbai - Pune Ghat Section (CR)", congestion: 64, speedLimit: 80, status: "CONGESTED" },
  ];

  const presetActiveTrainsList = [
    {
      trainNumber: "12919",
      trainName: "Malwa SF Express",
      route: "Indore (INDB) → New Delhi (NDLS)",
      currentSection: "Kanpur Central (CNB)",
      speed: 85,
      delayMinutes: 15,
      predictedEta: "18:45 IST",
      risk: "LOW",
      lat: 26.4499,
      lng: 80.3319,
    },
    {
      trainNumber: "15654",
      trainName: "Amarnath Express",
      route: "Jammu Tawi (JAT) → Guwahati (GHY)",
      currentSection: "Aligarh Junction (ALJN)",
      speed: 42,
      delayMinutes: 38,
      predictedEta: "22:15 IST",
      risk: "MEDIUM",
      lat: 27.8974,
      lng: 78.088,
    },
    {
      trainNumber: "15657",
      trainName: "Kamakhya Express",
      route: "Amritsar (ASR) → Kamakhya (KYQ)",
      currentSection: "Tundla Junction (TDL)",
      speed: 28,
      delayMinutes: 65,
      predictedEta: "01:30 IST (+1d)",
      risk: "HIGH",
      lat: 27.2069,
      lng: 78.242,
    },
    {
      trainNumber: "12301",
      trainName: "Howrah Rajdhani Express",
      route: "Howrah (HWH) → New Delhi (NDLS)",
      currentSection: "DDU Junction (DDU)",
      speed: 120,
      delayMinutes: 0,
      predictedEta: "10:05 IST",
      risk: "LOW",
      lat: 25.2818,
      lng: 83.1152,
    },
    {
      trainNumber: "22917",
      trainName: "Vande Bharat Express",
      route: "Ahmedabad (ADI) → Mumbai (MMCT)",
      currentSection: "Surat (ST)",
      speed: 130,
      delayMinutes: 2,
      predictedEta: "13:25 IST",
      risk: "LOW",
      lat: 21.1702,
      lng: 72.8311,
    },
  ];

  const [activeTrains, setActiveTrains] = useState([]);

  const applySnapshot = (data) => {
    setIsRealFeed(data.isRealFeed);
    setActiveTrains(data.activeTrains);
    setLiveMapSnapshot(data.liveMapSnapshot);
  };

  const fetchLiveSnapshot = async (force = false) => {
    // Already fetched this session — reuse it instead of hitting the API again,
    // unless the user explicitly asked to refresh.
    if (!force) {
      const cached = getCached(CACHE_KEY);
      if (cached) {
        applySnapshot(cached);
        setLoadingMap(false);
        setInitialLoading(false);
        return;
      }
    }

    setLoadingMap(true);
    try {
      const res = await trainService.getLiveMapSnapshot();
      const rawList = res?.data?.data || res?.data;

      let result;
      if (Array.isArray(rawList) && rawList.length > 0) {
        // The live map feed has positions only: no delay, speed or ETA, so those stay unknown.
        // It does carry train type, distance covered and time since departure, used for the network stats below.
        const mapped = rawList.map((t) => {
          return {
            trainNumber: t.train_number || t.trainNumber || "00100",
            trainName: t.train_name || t.trainName || "Rapid Transit Train",
            route: t.type || "--",
            currentSection: `${t.current_station_name || t.current_station} → ${t.next_station_name || t.next_station}`,
            speed: null,
            delayMinutes: null,
            predictedEta: "--",
            risk: null,
            lat: t.current_lat ?? null,
            lng: t.current_lng ?? null,
            type: t.type || null,
            minsSinceDep: Number.isFinite(t.mins_since_dep) ? t.mins_since_dep : null,
            currDistance: Number.isFinite(t.curr_distance) ? t.curr_distance : null,
            currentStationCode: t.current_station || null,
            currentStationName: t.current_station_name || t.current_station || null,
          };
        });

        result = { isRealFeed: true, activeTrains: mapped, liveMapSnapshot: rawList };
      } else {
        result = { isRealFeed: false, activeTrains: presetActiveTrainsList, liveMapSnapshot: [] };
      }

      // Only cache a real feed — a fallback means the API failed or was briefly empty,
      // and that shouldn't be remembered as "the answer" for the rest of the session.
      if (result.isRealFeed) setCached(CACHE_KEY, result);
      applySnapshot(result);
    } catch (err) {
      console.warn("Live map snapshot feed offline, using preset fleet status.");
      const result = { isRealFeed: false, activeTrains: presetActiveTrainsList, liveMapSnapshot: [] };
      applySnapshot(result);
    } finally {
      setLoadingMap(false);
      setInitialLoading(false);
    }
  };

  useEffect(() => {
    fetchLiveSnapshot();
  }, []);

  // Every figure is calculated from the trains listed below; unknown values show as "--"
  const metrics = useMemo(() => {
    const withDelay = activeTrains.filter((t) => Number.isFinite(t.delayMinutes));
    const withSpeed = activeTrains.filter((t) => Number.isFinite(t.speed));
    const withType = activeTrains.filter((t) => t.type);
    const withDistance = activeTrains.filter((t) => Number.isFinite(t.currDistance));
    const withRunTime = activeTrains.filter((t) => Number.isFinite(t.minsSinceDep));
    const stationsInUse = new Set(
      activeTrains.filter((t) => t.currentStationCode).map((t) => t.currentStationCode)
    ).size;

    return {
      trainsShown: activeTrains.length,
      onTimePct: withDelay.length
        ? Math.round((withDelay.filter((t) => t.delayMinutes <= 15).length / withDelay.length) * 100)
        : null,
      delayedTrains: withDelay.length ? withDelay.filter((t) => t.delayMinutes > 15).length : null,
      avgSpeed: withSpeed.length
        ? Math.round(withSpeed.reduce((sum, t) => sum + t.speed, 0) / withSpeed.length)
        : null,
      trainCategories: withType.length ? new Set(withType.map((t) => t.type)).size : null,
      avgDistanceCovered: withDistance.length
        ? Math.round(withDistance.reduce((sum, t) => sum + t.currDistance, 0) / withDistance.length)
        : null,
      avgRunHours: withRunTime.length
        ? (withRunTime.reduce((sum, t) => sum + t.minsSinceDep, 0) / withRunTime.length / 60).toFixed(1)
        : null,
      stationsInUse: stationsInUse || null,
    };
  }, [activeTrains]);

  // Busiest ~110km geographic cells (1° lat/lng) by current train count — a real,
  // live-derived stand-in for "section congestion" since the feed has no track-section data.
  const busyRegions = useMemo(() => {
    if (!isRealFeed) return [];

    const buckets = new Map();
    for (const t of activeTrains) {
      if (!Number.isFinite(t.lat) || !Number.isFinite(t.lng)) continue;
      const key = `${Math.round(t.lat)},${Math.round(t.lng)}`;
      if (!buckets.has(key)) buckets.set(key, { count: 0, stationCounts: new Map() });
      const bucket = buckets.get(key);
      bucket.count++;
      const name = t.currentStationName || "Unknown";
      bucket.stationCounts.set(name, (bucket.stationCounts.get(name) || 0) + 1);
    }

    const regions = Array.from(buckets.values()).map((bucket) => {
      let label = "Unknown";
      let topCount = 0;
      for (const [name, count] of bucket.stationCounts) {
        if (count > topCount) {
          topCount = count;
          label = name;
        }
      }
      return { name: `Near ${label}`, count: bucket.count };
    });

    regions.sort((a, b) => b.count - a.count);
    const top = regions.slice(0, 5);
    const maxCount = top[0]?.count || 1;

    return top.map((r) => {
      const ratio = r.count / maxCount;
      const status = ratio >= 0.75 ? "CRITICAL" : ratio >= 0.4 ? "CONGESTED" : "NORMAL";
      return { ...r, congestion: Math.round(ratio * 100), status };
    });
  }, [activeTrains, isRealFeed]);

  const show = (value, suffix = "") => (value == null ? "--" : `${value}${suffix}`);

  // Delay-based filters only make sense when the data has delays (the live feed does not)
  const hasDelayData = activeTrains.some((t) => Number.isFinite(t.delayMinutes));
  const q = query.trim().toLowerCase();
  const filteredTrains = activeTrains.filter((item) => {
    if (hasDelayData && filter === "delayed" && !(item.delayMinutes > 15)) return false;
    if (!q) return true;
    return `${item.trainNumber} ${item.trainName} ${item.currentSection}`.toLowerCase().includes(q);
  });
  const shownTrains = filteredTrains.slice(0, visibleCount);

  const filterButtonStyle = (active, activeBg, activeColor) => ({
    minHeight: "36px",
    padding: "4px 14px",
    borderRadius: "8px",
    border: "none",
    background: active ? activeBg : "rgba(255,255,255,0.2)",
    color: active ? activeColor : "white",
    fontSize: "12px",
    fontWeight: 700,
    cursor: "pointer",
  });

  return (
    <div className="result-page" style={{ background: "#fdfbf7", color: "#0f172a", minHeight: "100vh" }}>
      <Header
        onNavigateHome={onNavigateHome}
        onNavigateView={onNavigateView}
        activeView="controlroom"
      />

      <main className="result-content page-wide">
        {initialLoading ? (
          <ControlRoomSkeleton />
        ) : (
          <>
        {/* CONTROL ROOM HEADER BANNER */}
        <section
          className="train-summary cr-banner"
          style={{
            background: "#1d4ed8",
            color: "white",
            border: "1px solid #3b82f6",
            borderRadius: "14px",
            marginBottom: "16px",
            boxShadow: "0 10px 30px -5px rgba(29, 78, 216, 0.25)",
            padding: "24px 32px",
          }}
        >
          <div className="train-icon-large" style={{ background: "rgba(255, 255, 255, 0.18)", color: "white" }}>
            <Gauge size={30} aria-hidden="true" />
          </div>

          <div className="train-summary-info">
            <div style={{ display: "flex", alignItems: "center", flexWrap: "wrap", gap: "6px 10px" }}>
              <span className="train-number" style={{ color: "#93c5fd", fontWeight: 700, fontSize: "13px" }}>
                CONTROL ROOM
              </span>
              <span
                style={{
                  background: isRealFeed ? "#22c55e" : "#f59e0b",
                  color: "#ffffff",
                  fontSize: "10px",
                  fontWeight: 800,
                  padding: "2px 8px",
                  borderRadius: "12px",
                  letterSpacing: "0.5px",
                }}
              >
                {isRealFeed ? "LIVE" : "SAMPLE DATA"}
              </span>
            </div>

            <h1 style={{ color: "white", fontSize: "clamp(20px, 5vw, 28px)", lineHeight: 1.25, margin: "4px 0" }}>
              Network overview
            </h1>

            <div className="route-name" style={{ color: "#e0f2fe" }}>
              Running trains, delays and busy sections in one place.
            </div>
          </div>

          <div className="train-actions">
            <button
              type="button"
              onClick={() => fetchLiveSnapshot(true)}
              disabled={loadingMap}
              style={{
                display: "inline-flex",
                alignItems: "center",
                justifyContent: "center",
                gap: "6px",
                minHeight: "40px",
                padding: "8px 16px",
                borderRadius: "8px",
                border: "none",
                background: "#ffffff",
                color: "#1d4ed8",
                fontWeight: 800,
                fontSize: "13px",
                cursor: "pointer",
                boxShadow: "0 2px 8px rgba(0,0,0,0.1)",
              }}
            >
              <RefreshCw size={16} aria-hidden="true" />
              {loadingMap ? "Refreshing…" : "Refresh"}
            </button>
          </div>
        </section>

        {/* METRICS SUMMARY GRID: only figures we actually know */}
        <section className="metrics-grid">
          {[
            { label: isRealFeed ? "RUNNING NOW" : "TRAINS SHOWN", value: metrics.trainsShown, color: "#0a376c" },
            { label: "ON TIME (UP TO 15 MIN LATE)", value: metrics.onTimePct, suffix: "%", color: "#0b8b49" },
            { label: "MORE THAN 15 MIN LATE", value: metrics.delayedTrains, color: "#d92d20" },
            { label: "AVERAGE SPEED", value: metrics.avgSpeed, suffix: " km/h", color: "#1677ff" },
            { label: "TRAIN CATEGORIES", value: metrics.trainCategories, color: "#7c3aed" },
            { label: "AVG DISTANCE COVERED", value: metrics.avgDistanceCovered, suffix: " km", color: "#0a376c" },
            { label: "AVG TIME RUNNING", value: metrics.avgRunHours, suffix: " hrs", color: "#0b8b49" },
            { label: "STATIONS IN USE", value: metrics.stationsInUse, color: "#d97706" },
          ]
            .filter((m) => m.value != null)
            .map((m) => (
              <div key={m.label} className="info-panel" style={{ padding: "16px", textAlign: "center" }}>
                <span style={{ fontSize: "11px", color: "#66809e", fontWeight: 700 }}>{m.label}</span>
                <strong style={{ fontSize: "26px", color: m.color, display: "block", marginTop: "4px" }}>
                  {m.value}
                  {m.suffix || ""}
                </strong>
              </div>
            ))}
        </section>

        {/* TRAIN POSITIONS */}
        <section className="map-card fleet-card" aria-label="Train positions">
          <div className="fleet-card-head">
            <div>
              <h2 className="fleet-card-title">Train positions</h2>
              <p className="fleet-card-sub">
                {isRealFeed
                  ? "Live positions of running trains. Delay and speed are not part of this feed."
                  : "Sample trains. Green is on time, red is more than 15 min late."}{" "}
                Tap a dot to open the train.
              </p>
            </div>
            <span className="fleet-count">{activeTrains.length} trains</span>
          </div>

          <FleetMap trains={activeTrains} onSelectTrain={onSelectTrain} />
        </section>

        {/* DOWNSTREAM TRACK CONGESTION GRID */}
        <section className="info-panel" style={{ marginBottom: "18px" }}>
          <div
            className="panel-title"
            style={{
              background: "#075da8",
              display: "flex",
              justifyContent: "space-between",
            }}
          >
            <span>{isRealFeed ? "Busiest areas right now" : "Section congestion"}</span>
            <small style={{ fontSize: "11px", color: "#d6e7ff" }}>{isRealFeed ? "Live" : "Sample data"}</small>
          </div>

          <div
            style={{
              padding: "16px",
              display: "grid",
              gridTemplateColumns: "repeat(auto-fit, minmax(260px, 1fr))",
              gap: "12px",
              background: "#f8fbfe",
            }}
          >
            {(isRealFeed ? busyRegions : presetSections).length === 0 ? (
              <div style={{ color: "#66809e", fontSize: "13px" }}>No position data available right now.</div>
            ) : (
              (isRealFeed ? busyRegions : presetSections).map((sec, idx) => (
                <div
                  key={`${sec.name}-${idx}`}
                  style={{
                    background: "white",
                    padding: "14px",
                    borderRadius: "8px",
                    border: "1px solid #dcdfe6",
                  }}
                >
                  <div style={{ display: "flex", justifyContent: "space-between", fontSize: "13px", fontWeight: 700 }}>
                    <span>{sec.name}</span>
                    <span
                      style={{
                        color: sec.status === "CRITICAL" ? "#d92d20" : sec.status === "CONGESTED" ? "#e6a23c" : "#67c23a",
                      }}
                    >
                      {sec.status}
                    </span>
                  </div>

                  <div
                    style={{
                      height: "8px",
                      background: "#e4e7ed",
                      borderRadius: "4px",
                      marginTop: "10px",
                      overflow: "hidden",
                    }}
                  >
                    <div
                      style={{
                        height: "100%",
                        width: `${sec.congestion}%`,
                        background:
                          sec.congestion > 80
                            ? "#d92d20"
                            : sec.congestion > 60
                            ? "#e6a23c"
                            : "#67c23a",
                      }}
                    ></div>
                  </div>

                  <div
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      fontSize: "11px",
                      color: "#606266",
                      marginTop: "8px",
                    }}
                  >
                    <span>Congestion: {sec.congestion}%</span>
                    <span>{sec.speedLimit != null ? `Speed Limit: ${sec.speedLimit} km/h` : `${sec.count} trains nearby`}</span>
                  </div>
                </div>
              ))
            )}
          </div>
        </section>

        {/* RUNNING TRAINS LIST */}
        <section className="station-card">
          <div className="fleet-toolbar">
            <strong className="fleet-toolbar-title">
              <TrainFront size={18} aria-hidden="true" />
              Running trains ({filteredTrains.length})
            </strong>

            <div className="fleet-toolbar-controls">
              <label className="fleet-search">
                <Search size={16} aria-hidden="true" />
                <span className="sr-only">Search trains</span>
                <input
                  type="search"
                  value={query}
                  onChange={(e) => {
                    setQuery(e.target.value);
                    setVisibleCount(PAGE_SIZE);
                  }}
                  placeholder="Train number, name or station"
                  enterKeyHint="search"
                />
              </label>

              {hasDelayData && (
                <div className="chip-row on-dark" role="group" aria-label="Filter trains">
                  <button
                    type="button"
                    aria-pressed={filter === "all"}
                    onClick={() => {
                      setFilter("all");
                      setVisibleCount(PAGE_SIZE);
                    }}
                    style={filterButtonStyle(filter === "all", "white", "#075aa5")}
                  >
                    All
                  </button>
                  <button
                    type="button"
                    aria-pressed={filter === "delayed"}
                    onClick={() => {
                      setFilter("delayed");
                      setVisibleCount(PAGE_SIZE);
                    }}
                    style={filterButtonStyle(filter === "delayed", "#d92d20", "white")}
                  >
                    Delayed only
                  </button>
                </div>
              )}
            </div>
          </div>

          {filteredTrains.length === 0 ? (
            <div className="empty-state">No trains match your search.</div>
          ) : (
            <table className="responsive-table fleet-table">
              <thead>
                <tr>
                  <th>Train</th>
                  <th>{hasDelayData ? "Route" : "Type"}</th>
                  <th>Current position</th>
                  {hasDelayData && (
                    <>
                      <th>Speed</th>
                      <th>Delay</th>
                      <th>Predicted ETA</th>
                      <th>Risk</th>
                    </>
                  )}
                  <th>
                    <span className="sr-only">Action</span>
                  </th>
                </tr>
              </thead>
              <tbody>
                {shownTrains.map((item, idx) => (
                  <tr key={item.trainNumber + idx}>
                    <td className="cell-train" style={{ fontWeight: 700 }}>
                      <span style={{ color: "#1677ff", display: "block" }}>{item.trainNumber}</span>
                      <span style={{ color: "#101828" }}>{item.trainName}</span>
                    </td>

                    <td data-label={hasDelayData ? "Route" : "Type"} style={{ color: "#475467" }}>
                      {item.route}
                    </td>

                    <td data-label="Position" style={{ fontWeight: 600, color: "#0a376c" }}>
                      {item.currentSection}
                    </td>

                    {hasDelayData && (
                      <>
                        <td data-label="Speed">{show(item.speed, " km/h")}</td>

                        <td data-label="Delay">
                          {item.delayMinutes == null ? (
                            "--"
                          ) : (
                            <span
                              style={{
                                padding: "3px 8px",
                                borderRadius: "4px",
                                background: item.delayMinutes > 15 ? "#fef3f2" : "#edfcf2",
                                color: item.delayMinutes > 15 ? "#b42318" : "#087443",
                                fontWeight: 700,
                              }}
                            >
                              {item.delayMinutes > 0 ? `${item.delayMinutes} mins` : "On Time"}
                            </span>
                          )}
                        </td>

                        <td data-label="Predicted ETA" style={{ fontWeight: 700, color: "#0a376c" }}>
                          {item.predictedEta}
                        </td>

                        <td data-label="Risk">
                          {item.risk == null ? (
                            "--"
                          ) : (
                            <span
                              style={{
                                fontSize: "11px",
                                fontWeight: 700,
                                padding: "2px 8px",
                                borderRadius: "6px",
                                background:
                                  item.risk === "HIGH" ? "#fee4e2" : item.risk === "MEDIUM" ? "#fef0c7" : "#e0f2fe",
                                color:
                                  item.risk === "HIGH" ? "#912018" : item.risk === "MEDIUM" ? "#93370d" : "#035388",
                              }}
                            >
                              {item.risk} RISK
                            </span>
                          )}
                        </td>
                      </>
                    )}

                    <td className="cell-action">
                      <button
                        type="button"
                        className="btn-track outline"
                        onClick={() => onSelectTrain(item.trainNumber)}
                      >
                        Track train
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          {filteredTrains.length > visibleCount && (
            <button
              type="button"
              className="show-more-btn"
              onClick={() => setVisibleCount((c) => c + PAGE_SIZE)}
            >
              Show {Math.min(PAGE_SIZE, filteredTrains.length - visibleCount)} more
              {" "}({filteredTrains.length - visibleCount} left)
            </button>
          )}
        </section>
          </>
        )}
      </main>

      <Footer />
    </div>
  );
}
