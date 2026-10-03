import { formatDelay } from "../../utils/formatters";

export function PositionDetailsPanel({ live, eta }) {
  const delay = eta?.currentDelayMinutes ?? live?.delayMinutes ?? 0;

  return (
    <div className="info-panel">
      <div className="panel-title">Current Position Details</div>

      <div className="position-grid">
        <div>
          <span>Current Station</span>
          <strong>{live?.currentStation || "--"}</strong>
        </div>

        <div>
          <span>Next Station</span>
          <strong>{live?.nextStation || "--"}</strong>
        </div>

        <div>
          <span>Speed</span>
          <strong>{live?.speed ?? "--"} km/h</strong>
        </div>

        <div>
          <span>Delay</span>
          <strong className="red">{formatDelay(delay)}</strong>
        </div>
      </div>

      <div className="position-note">
        Live data changes as the train moves.
      </div>
    </div>
  );
}
