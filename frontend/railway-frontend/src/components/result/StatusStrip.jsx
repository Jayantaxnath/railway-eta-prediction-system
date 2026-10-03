import { formatDelay } from "../../utils/formatters";

export function StatusStrip({ live, eta }) {
  const delay = eta?.currentDelayMinutes ?? live?.delayMinutes ?? 0;
  const delayText = formatDelay(delay);
  const stationDisplay = live?.currentStationName 
    ? `${live.currentStationName} (${live.currentStation})`
    : live?.currentStation || "--";

  const rawStatus = (live?.status || "running").toLowerCase();
  const isArrived = rawStatus === "arrived" || rawStatus === "completed";
  const isUpcoming = rawStatus === "upcoming" || rawStatus === "yet_to_start";

  let statusText = "Train is currently running";
  let dotClass = "running-now";

  if (isArrived) {
    statusText = "Train reached destination";
    dotClass = "arrived-status";
  } else if (isUpcoming) {
    statusText = "Train yet to start from origin";
    dotClass = "upcoming-status";
  }

  return (
    <section className="status-strip" aria-live="polite">
      <div className="status-strip-location">
        {isArrived ? (
          <>Last recorded at <strong>{stationDisplay}</strong></>
        ) : isUpcoming ? (
          <>Scheduled from <strong>{stationDisplay}</strong></>
        ) : (
          <>Currently at <strong>{stationDisplay}</strong></>
        )}
        <span className={delay > 0 ? "delay-text" : "delay-text on-time"}>
          {delay > 0 ? ` · ${delayText} late` : " · On time"}
        </span>
      </div>

      <div className={dotClass}>
        <span></span>
        {statusText}
      </div>
    </section>
  );
}


