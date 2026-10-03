import { TrainFront, RefreshCw, Search, Zap } from "lucide-react";
import { formatDateTime } from "../../utils/formatters";
import { getDelayStatus } from "../../utils/delayUtils";

export function TrainHeader({
  train,
  live,
  eta,
  onRefresh,
  onTriggerPrediction,
  predicting,
  onNewSearch,
}) {
  const currentDelay = eta?.currentDelayMinutes ?? live?.delayMinutes ?? 0;
  const statusMeta = getDelayStatus(currentDelay);

  return (
    <section className="train-summary">
      <div className="train-summary-main">
        <div className="train-icon-large" aria-hidden="true">
          <TrainFront size={30} />
        </div>

        <div className="train-summary-info">
          <div className="train-number">{train?.trainNumber}</div>
          <h1>{train?.trainName}</h1>
          <div className="route-name">
            {train?.sourceStation}
            <span aria-hidden="true"> → </span>
            <span className="sr-only"> to </span>
            {train?.destinationStation}
          </div>
        </div>
      </div>

      <div className="train-actions">
        <div className="train-status-row">
          <div className={`running-badge ${statusMeta.isLate ? "late" : ""}`}>
            <span aria-hidden="true"></span>
            {statusMeta.label}
          </div>

          <div className="last-updated">
            Updated {formatDateTime(live?.lastUpdated)}
          </div>
        </div>

        <div className="action-buttons">
          <button
            type="button"
            className="btn-primary"
            onClick={onTriggerPrediction}
            disabled={predicting}
            title="Recalculate the ETA with the machine-learning model"
          >
            <Zap size={16} aria-hidden="true" />
            {predicting ? "Predicting…" : "Predict ETA"}
          </button>

          <button type="button" onClick={onRefresh}>
            <RefreshCw size={16} aria-hidden="true" />
            Refresh
          </button>

          <button type="button" onClick={onNewSearch}>
            <Search size={16} aria-hidden="true" />
            New search
          </button>
        </div>
      </div>
    </section>
  );
}
