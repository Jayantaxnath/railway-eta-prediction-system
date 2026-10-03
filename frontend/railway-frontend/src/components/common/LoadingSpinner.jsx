export function LoadingSpinner({ text = "Loading train details..." }) {
  return (
    <div className="loading-container" style={{ padding: "40px", textAlign: "center" }}>
      <div className="spinner" style={{ fontSize: "28px", marginBottom: "12px" }}>
        🚆 ⏳
      </div>
      <p style={{ color: "#475467", fontWeight: 500 }}>{text}</p>
    </div>
  );
}
