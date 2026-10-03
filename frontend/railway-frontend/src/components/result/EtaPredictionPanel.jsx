import { formatTime, formatDelay } from "../../utils/formatters";

export function EtaPredictionPanel({ eta, live }) {
  const delay = eta?.currentDelayMinutes ?? live?.delayMinutes ?? 0;

  return (
    <div className="info-panel">
      <div className="panel-title">Dynamic ETA Prediction</div>

      <div className="eta-grid">
        <div>
          <span>PREDICTED ETA</span>
          <strong>{formatTime(eta?.predictedEta)}</strong>
        </div>

        <div>
          <span>CURRENT DELAY</span>
          <strong className="red">{formatDelay(delay)}</strong>
        </div>

        <div>
          <span>REMAINING TIME</span>
          <strong>{formatDelay(eta?.predictedRemainingMinutes)}</strong>
        </div>

        <div>
          <span>CONFIDENCE</span>
          <strong>
            {eta?.confidenceScore
              ? `${Math.round(eta.confidenceScore * 100)}%`
              : "--"}
          </strong>
        </div>
      </div>

      <div className="prediction-source">
        Prediction Source: <strong>{eta?.predictionSource || "--"}</strong>
        <span>•</span>
        Model: <strong>{eta?.modelVersion || "--"}</strong>
      </div>
    </div>
  );
}
