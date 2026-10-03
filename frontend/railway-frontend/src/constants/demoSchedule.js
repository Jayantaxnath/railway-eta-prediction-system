/* =========================================================
   TRAIN SCHEDULE UTILS
   Dynamic helper utilities for RailRadar API schedules
========================================================= */

/**
 * Normalizes and formats raw schedule items from RailRadar API
 */
export function formatApiSchedule(apiScheduleList) {
  if (!Array.isArray(apiScheduleList)) return [];

  return apiScheduleList.map((st, idx) => ({
    station: st.stationName || st.station || st.stationCode || st.code || `STATION ${idx + 1}`,
    code: (st.stationCode || st.code || `STN${idx + 1}`).toUpperCase(),
    scheduledArrival: st.arrivalTime || st.scheduledArrival || "--",
    scheduledDeparture: st.departureTime || st.scheduledDeparture || "--",
    expectedArrival: st.expectedArrival || st.arrivalTime || st.scheduledArrival || "--",
    expectedDeparture: st.expectedDeparture || st.departureTime || st.scheduledDeparture || "--",
    platform: st.platform || "PF 1",
    distanceKm: st.distanceKm ?? (idx * 25),
    stopNumber: st.stopNumber ?? (idx + 1),
  }));
}

/**
 * Dynamic schedule fallback generator for arbitrary train searches
 */
export function getTrainSchedule(trainNumber, sourceName, destName) {
  const num = String(trainNumber || "").trim();
  const src = (sourceName || `ORIGIN (${num})`).toUpperCase();
  const dst = (destName || `DESTINATION (${num})`).toUpperCase();

  return [
    {
      station: src,
      code: src.substring(0, 4).toUpperCase(),
      scheduledArrival: "--",
      scheduledDeparture: "06:00",
      expectedArrival: "--",
      expectedDeparture: "06:00",
      platform: "PF 1",
      distanceKm: 0,
    },
    {
      station: "JUNCTION ALPHA",
      code: "JNAL",
      scheduledArrival: "08:15",
      scheduledDeparture: "08:20",
      expectedArrival: "08:30",
      expectedDeparture: "08:35",
      platform: "PF 2",
      distanceKm: 140,
    },
    {
      station: "CENTRAL HUB",
      code: "CHUB",
      scheduledArrival: "11:30",
      scheduledDeparture: "11:40",
      expectedArrival: "11:45",
      expectedDeparture: "11:55",
      platform: "PF 3",
      distanceKm: 320,
    },
    {
      station: "INTERMEDIATE JN",
      code: "INTM",
      scheduledArrival: "15:10",
      scheduledDeparture: "15:15",
      expectedArrival: "15:25",
      expectedDeparture: "15:30",
      platform: "PF 1",
      distanceKm: 580,
    },
    {
      station: dst,
      code: dst.substring(0, 4).toUpperCase(),
      scheduledArrival: "19:45",
      scheduledDeparture: "--",
      expectedArrival: "20:05",
      expectedDeparture: "--",
      platform: "PF 4",
      distanceKm: 850,
    },
  ];
}

export const stationSchedule = [];

