import { useState } from "react";

export function GroundRealitySimulator({ onApplyImpact, predicting }) {
  const [congestion, setCongestion] = useState(30);
  const [weather, setWeather] = useState("clear");
  const [signalHalt, setSignalHalt] = useState(false);
  const [speedRestriction, setSpeedRestriction] = useState(false);

  const handleApply = () => {
    let rainfallMm = 0;
    if (weather === "light_rain") rainfallMm = 8;
    if (weather === "heavy_rain") rainfallMm = 35;
    if (weather === "dense_fog") rainfallMm = 0;

    const simulationPayload = {
      congestionScore: congestion / 100,
      rainfallMm,
      signalHalt,
      speedRestriction,
      weatherCondition: weather,
    };

    if (onApplyImpact) {
      onApplyImpact(simulationPayload);
    }
  };

  return (
    <div
      className="info-panel"
      style={{
        marginTop: "16px",
        border: "1px solid #1677ff",
        boxShadow: "0 4px 12px rgba(22, 119, 255, 0.1)",
      }}
    >
      <div
        className="panel-title"
        style={{
          background: "linear-gradient(90deg, #07356f, #1677ff)",
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <span style={{ display: "flex", alignItems: "center", gap: "8px" }}>
          ⚡ <strong>Real-Time Ground Reality & Environmental Simulator</strong>
        </span>
        <small style={{ fontSize: "11px", color: "#d6e7ff" }}>
          Simulate weather, track congestion & signal halts live
        </small>
      </div>

      <div
        style={{
          padding: "16px 20px",
          background: "#f8fbfe",
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
          gap: "18px",
          alignItems: "center",
        }}
      >
        {/* TRACK CONGESTION SLIDER */}
        <div>
          <label
            style={{
              fontSize: "12px",
              fontWeight: 700,
              color: "#0a376c",
              display: "flex",
              justifyContent: "space-between",
              marginBottom: "6px",
            }}
          >
            <span>Downstream Track Congestion</span>
            <span style={{ color: congestion > 60 ? "#d92d20" : "#1268e8" }}>
              {congestion}% {congestion > 60 ? "(Heavy Traffic)" : "(Normal)"}
            </span>
          </label>
          <input
            type="range"
            min="0"
            max="100"
            value={congestion}
            onChange={(e) => setCongestion(Number(e.target.value))}
            style={{ width: "100%", accentColor: "#1677ff", cursor: "pointer" }}
          />
        </div>

        {/* WEATHER CONDITIONS */}
        <div>
          <label
            style={{
              fontSize: "12px",
              fontWeight: 700,
              color: "#0a376c",
              display: "block",
              marginBottom: "6px",
            }}
          >
            Weather & Visibility Condition
          </label>
          <select
            value={weather}
            onChange={(e) => setWeather(e.target.value)}
            style={{
              width: "100%",
              padding: "7px 10px",
              borderRadius: "6px",
              border: "1px solid #c9dff3",
              background: "white",
              fontSize: "13px",
              color: "#101828",
              fontWeight: 500,
            }}
          >
            <option value="clear">☀️ Clear / Dry Weather</option>
            <option value="light_rain">🌧 Light Monsoon (8mm)</option>
            <option value="heavy_rain">⛈ Heavy Rain / Flooding (35mm+)</option>
            <option value="dense_fog">🌫 Dense Fog / Low Visibility</option>
          </select>
        </div>

        {/* SIGNAL HALTS & SPEED RESTRICTIONS */}
        <div style={{ display: "flex", flexDirection: "column", gap: "8px" }}>
          <label
            style={{
              fontSize: "12px",
              fontWeight: 700,
              color: "#0a376c",
              display: "flex",
              alignItems: "center",
              gap: "8px",
              cursor: "pointer",
            }}
          >
            <input
              type="checkbox"
              checked={signalHalt}
              onChange={(e) => setSignalHalt(e.target.checked)}
              style={{ width: "16px", height: "16px", accentColor: "#d92d20" }}
            />
            🚦 Unscheduled Signal Hold / Block
          </label>

          <label
            style={{
              fontSize: "12px",
              fontWeight: 700,
              color: "#0a376c",
              display: "flex",
              alignItems: "center",
              gap: "8px",
              cursor: "pointer",
            }}
          >
            <input
              type="checkbox"
              checked={speedRestriction}
              onChange={(e) => setSpeedRestriction(e.target.checked)}
              style={{ width: "16px", height: "16px", accentColor: "#1677ff" }}
            />
            ⚠️ Temporary Speed Restriction (PSR 30 km/h)
          </label>
        </div>

        {/* RECALCULATE ACTION BUTTON */}
        <div style={{ display: "flex", alignItems: "flex-end" }}>
          <button
            onClick={handleApply}
            disabled={predicting}
            style={{
              width: "100%",
              height: "42px",
              border: "none",
              borderRadius: "8px",
              background: "linear-gradient(135deg, #1677ff, #094da9)",
              color: "white",
              fontSize: "14px",
              fontWeight: 700,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              gap: "8px",
              boxShadow: "0 3px 8px rgba(22, 119, 255, 0.25)",
              cursor: predicting ? "wait" : "pointer",
            }}
          >
            {predicting ? "🤖 Computing ML Recalculation..." : "⚡ Recalculate AI ETA Impact"}
          </button>
        </div>
      </div>
    </div>
  );
}
