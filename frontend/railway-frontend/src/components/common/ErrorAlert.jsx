export function ErrorAlert({ message, onDismiss }) {
  if (!message) return null;

  return (
    <div className="error-message">
      <span>{message}</span>
      {onDismiss && (
        <button
          onClick={onDismiss}
          style={{
            background: "transparent",
            border: "none",
            marginLeft: "12px",
            color: "inherit",
            cursor: "pointer",
            fontWeight: "bold",
          }}
          aria-label="Dismiss error"
        >
          ×
        </button>
      )}
    </div>
  );
}
