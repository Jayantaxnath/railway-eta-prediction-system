export function Breadcrumb({ trainNumber, trainName, onNavigateHome }) {
  return (
    <nav className="breadcrumb" aria-label="Breadcrumb">
      <button type="button" className="breadcrumb-link" onClick={onNavigateHome}>
        Home
      </button>
      <b aria-hidden="true">›</b>
      <span>Train Search</span>
      <b aria-hidden="true">›</b>
      <strong aria-current="page">
        {trainNumber} - {trainName}
      </strong>
    </nav>
  );
}
