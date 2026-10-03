import { useState, useMemo } from "react";
import { ChevronDown, ChevronUp, TrainFront } from "lucide-react";
import { formatDelay } from "../../utils/formatters";

/**
 * Helper to add delay minutes to HH:MM time string
 */
function addMinutesToTime(timeStr, minutesToAdd) {
  if (!timeStr || timeStr === "--") return "--";
  const [h, m] = timeStr.split(":").map(Number);
  if (Number.isNaN(h) || Number.isNaN(m)) return timeStr;

  const totalMins = (h * 60 + m + Math.round(minutesToAdd)) % 1440;
  const newH = Math.floor(((totalMins + 1440) % 1440) / 60);
  const newM = ((totalMins + 1440) % 1440) % 60;
  return `${String(newH).padStart(2, "0")}:${String(newM).padStart(2, "0")}`;
}

export function StationTimelineTable({ live, train, eta }) {
  const [showPassed, setShowPassed] = useState(false);
  const [stopsOnly, setStopsOnly] = useState(true);

  const realDelay = eta?.currentDelayMinutes ?? live?.delayMinutes ?? 0;
  const currentStationCode = live?.currentStation || "";
  const nextStationCode = live?.nextStation || "";

  // Build dynamic schedule array using live API schedule or fallback route schedule
  const dynamicSchedule = useMemo(() => {
    let list =
      Array.isArray(train?.schedule) && train.schedule.length > 0
        ? [...train.schedule]
        : [];

    // Check if source station matches train source
    if (
      train?.sourceStation &&
      !list.some(
        (s) =>
          s.code === train.sourceStation ||
          s.station.includes(train.sourceStation)
      )
    ) {
      list[0] = {
        ...list[0],
        station: train.sourceStation,
        code: train.sourceStation.substring(0, 4).toUpperCase(),
      };
    }

    // Determine current station index in real schedule
    let activeCurrentIndex = list.findIndex(
      (s) => s.code.toUpperCase() === currentStationCode.toUpperCase()
    );

    // If current station code is not in list, find by next station code fallback
    if (activeCurrentIndex === -1 && nextStationCode) {
      const nextIdx = list.findIndex(
        (s) => s.code.toUpperCase() === nextStationCode.toUpperCase()
      );
      if (nextIdx > 0) {
        activeCurrentIndex = nextIdx - 1;
      }
    }

    if (activeCurrentIndex === -1) {
      activeCurrentIndex = 0;
    }

    return list.map((st, idx) => {
      const isPassed = idx < activeCurrentIndex;

      if (!isPassed && realDelay > 0) {
        const expectedArr =
          st.scheduledArrival !== "--"
            ? addMinutesToTime(st.scheduledArrival, realDelay)
            : st.expectedArrival;

        const expectedDep =
          st.scheduledDeparture !== "--"
            ? addMinutesToTime(st.scheduledDeparture, realDelay)
            : st.expectedDeparture;

        // Prediction uncertainty interval bounds
        const lowerBoundArr = addMinutesToTime(st.scheduledArrival, Math.max(0, realDelay - 4));
        const upperBoundArr = addMinutesToTime(st.scheduledArrival, realDelay + 6);

        return {
          ...st,
          expectedArrival: expectedArr,
          expectedDeparture: expectedDep,
          lowerBound: lowerBoundArr,
          upperBound: upperBoundArr,
          dynamicDelay: realDelay,
        };
      }
      return st;
    });
  }, [currentStationCode, nextStationCode, realDelay, train]);

  const currentIdx = dynamicSchedule.findIndex(
    (s) => s.code.toUpperCase() === currentStationCode.toUpperCase()
  );

  // Pass-through stations can be hidden; the current station and both ends always stay
  const hasPassThrough = dynamicSchedule.some((s) => s.isHalt === false);
  const lastIdx = dynamicSchedule.length - 1;
  const isListed = (station, index) =>
    !stopsOnly ||
    !hasPassThrough ||
    station.isHalt !== false ||
    index === currentIdx ||
    index === 0 ||
    index === lastIdx;

  // Stations before the current one are collapsed by default
  const passedCount = dynamicSchedule.filter(
    (st, i) => currentIdx > 0 && i < currentIdx && isListed(st, i)
  ).length;
  const firstVisibleIdx = showPassed || currentIdx < 0 ? 0 : currentIdx;

  const delayBadge = (delay) =>
    delay > 0 ? (
      <span className="delay-badge">{formatDelay(delay)}</span>
    ) : (
      <span className="delay-badge on-time-badge">On Time</span>
    );

  return (
    <section className="station-card" aria-label="Station timeline">
      <div className="station-card-toolbar">
        <h2 className="station-card-title">Route timeline</h2>

        {hasPassThrough && (
          <div className="segmented" role="group" aria-label="Stations to show">
            <button
              type="button"
              className={stopsOnly ? "active" : ""}
              aria-pressed={stopsOnly}
              onClick={() => setStopsOnly(true)}
            >
              Stops only
            </button>
            <button
              type="button"
              className={!stopsOnly ? "active" : ""}
              aria-pressed={!stopsOnly}
              onClick={() => setStopsOnly(false)}
            >
              All stations
            </button>
          </div>
        )}
      </div>

      <div className="station-table-header" aria-hidden="true">
        <div>Station</div>
        <div>Scheduled Arrival</div>
        <div>Expected Arrival</div>
        <div>Scheduled Departure</div>
        <div>Expected Departure</div>
      </div>

      <div className="station-table-body">
        {passedCount > 0 && (
          <button
            type="button"
            className="passed-toggle"
            onClick={() => setShowPassed((v) => !v)}
            aria-expanded={showPassed}
          >
            {showPassed ? (
              <ChevronUp size={16} aria-hidden="true" />
            ) : (
              <ChevronDown size={16} aria-hidden="true" />
            )}
            {showPassed ? "Hide" : "Show"} {passedCount} passed station{passedCount > 1 ? "s" : ""}
          </button>
        )}

        {dynamicSchedule.map((station, index) => {
          if (index < firstVisibleIdx || !isListed(station, index)) return null;
          const isCurrent =
            station.code.toUpperCase() === currentStationCode.toUpperCase() ||
            (currentIdx === -1 && index === 0);
          const isPassed = currentIdx !== -1 ? index < currentIdx : false;
          const isHalt = station.isHalt !== false;

          const arrivalDelay =
            station.dynamicDelay ?? (isCurrent || index > currentIdx ? realDelay : 0);
          const departureDelay =
            station.dynamicDelay ?? (isCurrent || index > currentIdx ? realDelay : 0);

          return (
            <div
              className={`station-row ${isCurrent ? "current-station" : ""} ${
                isPassed ? "passed-station" : ""
              } ${isHalt ? "" : "no-halt"}`}
              key={`${station.code}-${index}`}
              aria-current={isCurrent ? "location" : undefined}
            >
              {/* STATION DETAILS */}
              <div className="station-name-cell">
                <div className="timeline" aria-hidden="true">
                  <div
                    className={`timeline-line ${
                      isPassed || isCurrent ? "active" : ""
                    }`}
                  ></div>

                  <div
                    className={`timeline-dot ${
                      isCurrent ? "current" : isPassed ? "passed" : ""
                    }`}
                  ></div>

                  {isCurrent && (
                    <div className="train-pointer">
                      <TrainFront size={18} />
                    </div>
                  )}
                </div>

                <div className="station-details">
                  <strong>{station.station}</strong>
                  <span className="station-code">{station.code}</span>

                  <div className="station-tags">
                    {isCurrent && <span className="here-tag">Train is here</span>}
                    {isHalt ? (
                      <span className="platform-tag">{station.platform}</span>
                    ) : (
                      <span className="no-halt-tag">No halt</span>
                    )}
                    {station.distanceKm !== undefined && (
                      <span className="distance-tag">{station.distanceKm} km</span>
                    )}
                  </div>
                </div>
              </div>

              {/* SCHEDULED ARRIVAL */}
              <div className="time-cell sched-cell">
                <strong>{station.scheduledArrival}</strong>
                {station.scheduledArrival !== "--" && <small>{station.arrivalDate || ""}</small>}
              </div>

              {/* EXPECTED ARRIVAL / DYNAMIC ETA */}
              <div className="time-cell" data-label="Arrival">
                <strong className={arrivalDelay > 0 ? "late-time" : "on-time"}>
                  {station.expectedArrival}
                </strong>

                {station.expectedArrival !== "--" && (
                  <>
                    <small className="sched-inline">Sch {station.scheduledArrival}</small>
                    <small>{station.arrivalDate || ""}</small>

                    {/* Lower & Upper Confidence Interval Bounds */}
                    {station.lowerBound && station.upperBound && (
                      <span className="eta-range">
                        Range: {station.lowerBound} - {station.upperBound}
                      </span>
                    )}

                    {delayBadge(arrivalDelay)}
                  </>
                )}
              </div>

              {/* SCHEDULED DEPARTURE */}
              <div className="time-cell sched-cell">
                <strong>{station.scheduledDeparture}</strong>
                {station.scheduledDeparture !== "--" && <small>{station.departureDate || ""}</small>}
              </div>

              {/* EXPECTED DEPARTURE */}
              <div className="time-cell" data-label="Departure">
                <strong className={departureDelay > 0 ? "late-time" : "on-time"}>
                  {station.expectedDeparture}
                </strong>

                {station.expectedDeparture !== "--" && (
                  <>
                    <small className="sched-inline">Sch {station.scheduledDeparture}</small>
                    <small>{station.departureDate || ""}</small>
                    {delayBadge(departureDelay)}
                  </>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
}
