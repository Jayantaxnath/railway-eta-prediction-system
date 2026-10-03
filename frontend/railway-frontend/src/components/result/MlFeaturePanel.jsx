export function MlFeaturePanel({ features, live }) {
  const feat = features || {
    trainNumber: live?.trainNumber || "--",
    currentSpeed: live?.speed ?? 85,
    currentDelayMinutes: live?.delayMinutes ?? 15,
    distanceRemaining: 342.5,
    scheduledRemainingMinutes: 240,
    stopsRemaining: 5,
    latitude: live?.latitude ?? 28.6139,
    longitude: live?.longitude ?? 77.209,
    hourOfDay: new Date().getHours(),
    dayOfWeek: new Date().getDay(),
  };

  return (
    <div className="info-panel" style={{ marginTop: "12px" }}>
      <div
        className="panel-title"
        style={{
          background: "linear-gradient(90deg, #06366f, #094ca0)",
          display: "flex",
          justifyContent: "space-between",
        }}
      >
        <span>🤖 AI Model Input Feature Vector (/api/trains/features)</span>
        <small style={{ fontSize: "11px", fontWeight: "normal", opacity: 0.9 }}>
          Spring Boot → XGBoost Pipeline
        </small>
      </div>

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(130px, 1fr))",
          gap: "1px",
          background: "#d8e4ef",
        }}
      >
        <div style={{ background: "#fff", padding: "12px", textAlign: "center" }}>
          <span style={{ fontSize: "11px", color: "#66809e", display: "block" }}>
            SPEED (KM/H)
          </span>
          <strong style={{ fontSize: "16px", color: "#0a417c" }}>
            {feat.currentSpeed ?? "--"}
          </strong>
        </div>

        <div style={{ background: "#fff", padding: "12px", textAlign: "center" }}>
          <span style={{ fontSize: "11px", color: "#66809e", display: "block" }}>
            CURRENT DELAY
          </span>
          <strong style={{ fontSize: "16px", color: "#ef1818" }}>
            {feat.currentDelayMinutes ?? 0} m
          </strong>
        </div>

        <div style={{ background: "#fff", padding: "12px", textAlign: "center" }}>
          <span style={{ fontSize: "11px", color: "#66809e", display: "block" }}>
            DISTANCE REM.
          </span>
          <strong style={{ fontSize: "16px", color: "#0a417c" }}>
            {feat.distanceRemaining ? `${feat.distanceRemaining} km` : "--"}
          </strong>
        </div>

        <div style={{ background: "#fff", padding: "12px", textAlign: "center" }}>
          <span style={{ fontSize: "11px", color: "#66809e", display: "block" }}>
            SCHED. REM. MINS
          </span>
          <strong style={{ fontSize: "16px", color: "#0a417c" }}>
            {feat.scheduledRemainingMinutes ? `${feat.scheduledRemainingMinutes} m` : "--"}
          </strong>
        </div>

        <div style={{ background: "#fff", padding: "12px", textAlign: "center" }}>
          <span style={{ fontSize: "11px", color: "#66809e", display: "block" }}>
            STOPS REM.
          </span>
          <strong style={{ fontSize: "16px", color: "#0a417c" }}>
            {feat.stopsRemaining ?? "--"}
          </strong>
        </div>

        <div style={{ background: "#fff", padding: "12px", textAlign: "center" }}>
          <span style={{ fontSize: "11px", color: "#66809e", display: "block" }}>
            LAT / LNG
          </span>
          <strong style={{ fontSize: "13px", color: "#0a417c" }}>
            {feat.latitude && feat.longitude
              ? `${Number(feat.latitude).toFixed(2)}, ${Number(feat.longitude).toFixed(2)}`
              : "--"}
          </strong>
        </div>

        <div style={{ background: "#fff", padding: "12px", textAlign: "center" }}>
          <span style={{ fontSize: "11px", color: "#66809e", display: "block" }}>
            HOUR / DAY
          </span>
          <strong style={{ fontSize: "14px", color: "#0a417c" }}>
            Hr {feat.hourOfDay ?? "--"} / Day {feat.dayOfWeek ?? "--"}
          </strong>
        </div>
      </div>
    </div>
  );
}
