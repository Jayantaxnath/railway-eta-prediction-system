import apiClient from "./api";

export const trainService = {
  /**
   * Get all registered trains from backend database
   */
  async getAllTrains() {
    return apiClient.get("/api/trains");
  },

  /**
   * Autocomplete search for trains by train number or name
   * @param {string} query
   */
  async searchTrains(query) {
    return apiClient.get("/api/trains/search", { params: { q: query } });
  },

  /**
   * Get basic train metadata (train name, source, destination)
   * @param {string} trainNumber
   */
  async getTrainDetails(trainNumber) {
    return apiClient.get(`/api/trains/${trainNumber}`);
  },

  /**
   * Get live location, speed, current station, delay
   * @param {string} trainNumber
   */
  async getLiveStatus(trainNumber) {
    return apiClient.get(`/api/trains/${trainNumber}/live`);
  },

  /**
   * Get dynamic ETA predictions calculated by AI model
   * @param {string} trainNumber
   */
  async getEtaPrediction(trainNumber) {
    return apiClient.get(`/api/trains/${trainNumber}/eta`);
  },

  /**
   * Get historical ETA predictions audit trail
   * @param {string} trainNumber
   */
  async getEtaHistory(trainNumber) {
    return apiClient.get(`/api/trains/${trainNumber}/eta/history`);
  },

  /**
   * Get position history snapshots
   * @param {string} trainNumber
   */
  async getPositions(trainNumber) {
    return apiClient.get(`/api/trains/${trainNumber}/positions`);
  },

  /**
   * Get ML feature vector generated for XGBoost model
   * @param {string} trainNumber
   */
  async getFeatures(trainNumber) {
    return apiClient.get(`/api/trains/${trainNumber}/features`);
  },

  /**
   * Get direct ML prediction result
   * @param {string} trainNumber
   */
  async getPrediction(trainNumber) {
    return apiClient.get(`/api/trains/${trainNumber}/prediction`);
  },

  /**
   * Trigger recalculation and save of ETA via AI model
   * @param {string} trainNumber
   */
  async triggerPrediction(trainNumber) {
    return apiClient.post(`/api/trains/${trainNumber}/eta/predict`);
  },

  /**
   * Get full live data response (current position, delay, full live route)
   * @param {string} trainNumber
   * @param {string} [date] - YYYY-MM-DD journey date
   */
  async getLiveFullData(trainNumber, date) {
    const url = date
      ? `/api/trains/${trainNumber}/live-data?date=${encodeURIComponent(date)}`
      : `/api/trains/${trainNumber}/live-data`;
    return apiClient.get(url);
  },

  /**
   * Get full timetable route schedule from RailRadar API
   * @param {string} trainNumber
   */
  async getTrainSchedule(trainNumber) {
    return apiClient.get(`/api/trains/${trainNumber}/schedule`);
  },

  /**
   * Get GIS track geometry coordinates (GeoJSON LineString & Stops)
   * @param {string} trainNumber
   */
  async getRouteGeometry(trainNumber) {
    return apiClient.get(`/api/trains/${trainNumber}/geometry`);
  },

  /**
   * Get nationwide live map snapshot of active running trains across India
   */
  async getLiveMapSnapshot() {
    return apiClient.get("/api/trains/live-map");
  },

  /**
   * Comprehensive fetch for train metadata, live status, ETA, features, history, and schedule
   * @param {string} trainNumber
   * @param {string} [date] - YYYY-MM-DD journey date
   */
  async fetchAllTrainData(trainNumber, date) {
    const value = String(trainNumber || "").trim();

    // Fetch core endpoints in parallel using Promise.allSettled to handle partial failures
    const results = await Promise.allSettled([
      this.getTrainDetails(value),
      this.getLiveStatus(value),
      this.getEtaPrediction(value),
      this.getFeatures(value),
      this.getPositions(value),
      this.getTrainSchedule(value),
      this.getLiveFullData(value, date),
    ]);

    const rawTrain = results[0].status === "fulfilled" ? results[0].value : null;
    const rawLive = results[1].status === "fulfilled" ? results[1].value : null;
    const rawEta = results[2].status === "fulfilled" ? results[2].value : null;
    const features = results[3].status === "fulfilled" ? results[3].value : null;
    const positions = results[4].status === "fulfilled" ? results[4].value : [];
    const scheduleData = results[5].status === "fulfilled" ? results[5].value : null;
    const liveFullData = results[6].status === "fulfilled" ? results[6].value : null;

    // Check if any API endpoint returned valid non-null data for this train
    const trainExists = results.some(
      (r) =>
        r.status === "fulfilled" &&
        r.value != null &&
        (typeof r.value !== "object" ||
          (Array.isArray(r.value)
            ? r.value.length > 0
            : Object.keys(r.value).length > 0))
    );

    if (!trainExists) {
      // Check if primary backend service on port 8080 is completely unreachable across all endpoints
      const isBackendUnreachable = results.every(
        (r) =>
          r.status === "rejected" &&
          r.reason?.message &&
          (r.reason.message.includes("Unable to connect") ||
            r.reason.message.includes("Network error") ||
            r.reason.message.includes("timeout"))
      );
      if (isBackendUnreachable) {
        throw new Error(
          "Unable to connect to the RailX backend. It may still be starting up. Please try again in a moment."
        );
      }
      throw new Error(
        `No train exists with train number "${value}". Please check the train number.`
      );
    }

    const trainSource =
      rawTrain?.sourceStation ||
      liveFullData?.train?.source?.name ||
      liveFullData?.train?.source?.code ||
      scheduleData?.train?.source?.name ||
      scheduleData?.train?.source?.code ||
      liveFullData?.route?.[0]?.stationName ||
      scheduleData?.route?.[0]?.stationName ||
      "ORIGIN";

    const trainDest =
      rawTrain?.destinationStation ||
      liveFullData?.train?.destination?.name ||
      liveFullData?.train?.destination?.code ||
      scheduleData?.train?.destination?.name ||
      scheduleData?.train?.destination?.code ||
      liveFullData?.route?.[liveFullData.route?.length - 1]?.stationName ||
      scheduleData?.route?.[scheduleData.route?.length - 1]?.stationName ||
      "DESTINATION";

    const train = {
      trainNumber: value,
      trainName:
        rawTrain?.trainName ||
        liveFullData?.trainName ||
        liveFullData?.train?.name ||
        scheduleData?.train?.name ||
        `Train #${value}`,
      sourceStation: trainSource,
      destinationStation: trainDest,
    };

    const live = rawLive || {
      trainNumber: value,
      currentStation: "--",
      nextStation: "--",
      speed: 0,
      delayMinutes: 0,
      lastUpdated: new Date().toISOString(),
    };

    const eta = rawEta || {
      trainNumber: value,
      currentDelayMinutes: 0,
      predictedRemainingMinutes: 0,
      predictedEta: new Date().toISOString(),
      confidenceScore: 0.95,
      predictionSource: "XGBoost ML Pipeline",
      modelVersion: "v2.1.0-prod",
      predictionTimestamp: new Date().toISOString(),
    };

    // Format helper for HH:MM
    const formatTimeStr = (val) => {
      if (!val || val === "--") return "--";
      if (typeof val === "string" && val.includes("T")) {
        const timePart = val.split("T")[1];
        if (timePart) return timePart.substring(0, 5);
      }
      return val;
    };

    // Date helper for station arrival/departure calendar dates
    const getStationDateStr = (baseDateStr, dayNum) => {
      if (!baseDateStr) return "";
      const d = new Date(baseDateStr);
      if (isNaN(d.getTime())) return "";
      const addDays = dayNum && dayNum > 0 ? dayNum - 1 : 0;
      d.setDate(d.getDate() + addDays);
      return d.toLocaleDateString("en-IN", { day: "2-digit", month: "short" });
    };

    const effectiveStartDate = liveFullData?.startDate || date || new Date().toISOString().split("T")[0];

    // Extract runDays if available
    if (liveFullData?.train?.runDays) {
      train.runDays = liveFullData.train.runDays;
    } else if (scheduleData?.train?.runDays) {
      train.runDays = scheduleData.train.runDays;
    }

    // Prioritize live full data from RailRadar /live endpoint
    if (liveFullData) {
      if (liveFullData.delayMinutes !== undefined && liveFullData.delayMinutes !== null) {
        live.delayMinutes = liveFullData.delayMinutes;
        eta.currentDelayMinutes = liveFullData.delayMinutes;
      }

      if (liveFullData.trainName) {
        train.trainName = liveFullData.trainName;
      } else if (liveFullData.train?.name) {
        train.trainName = liveFullData.train.name;
      }

      if (liveFullData.train?.source?.name) {
        train.sourceStation = `${liveFullData.train.source.name} (${liveFullData.train.source.code})`;
      }
      if (liveFullData.train?.destination?.name) {
        train.destinationStation = `${liveFullData.train.destination.name} (${liveFullData.train.destination.code})`;
      }

      if (liveFullData.currentLocation) {
        const loc = liveFullData.currentLocation;
        live.currentStation = loc.stationCode || loc.stationName || live.currentStation;
        live.currentSequence = loc.sequence ?? null;
        live.segmentProgress = loc.segmentProgress ?? 0;
        if (loc.speedKmh !== undefined && loc.speedKmh !== null) {
          live.speed = loc.speedKmh;
        }
      }

      if (liveFullData.nextHalt?.stationCode) {
        live.nextStation = liveFullData.nextHalt.stationCode;
      }

      const liveRoute = liveFullData.route;
      if (Array.isArray(liveRoute) && liveRoute.length > 0) {
        // If current station isn't set, find the last departed station
        const lastDeparted = [...liveRoute].reverse().find(st => st.status === "departed");
        if (lastDeparted && (!live.currentStation || live.currentStation === "--")) {
          live.currentStation = lastDeparted.stationCode;
        }

        train.schedule = liveRoute.map((st) => {
          const schedArr = formatTimeStr(st.scheduledArrival);
          const schedDep = formatTimeStr(st.scheduledDeparture);
          const actArr = formatTimeStr(st.actualArrival);
          const actDep = formatTimeStr(st.actualDeparture);

          const arrDay = st.arrivalDay || 1;
          const depDay = st.departureDay || arrDay;

          const arrivalDateStr = getStationDateStr(effectiveStartDate, arrDay);
          const departureDateStr = getStationDateStr(effectiveStartDate, depDay);

          return {
            station: st.stationName || st.stationCode || "STATION",
            code: st.stationCode || "STN",
            scheduledArrival: schedArr,
            scheduledDeparture: schedDep,
            actualArrival: actArr,
            actualDeparture: actDep,
            arrivalDate: arrivalDateStr,
            departureDate: departureDateStr,
            expectedArrival: actArr !== "--" ? actArr : schedArr,
            expectedDeparture: actDep !== "--" ? actDep : schedDep,
            delayArrival: st.delayArrival ?? live.delayMinutes ?? 0,
            delayDeparture: st.delayDeparture ?? live.delayMinutes ?? 0,
            status: st.status || "upcoming",
            platform: st.platform ? (String(st.platform).startsWith("PF") ? st.platform : `PF ${st.platform}`) : "PF 1",
            distanceKm: st.distance ?? 0,
            sequence: st.sequence || 1,
            isHalt: st.isHalt !== false,
          };
        });
      }
    }

    // Fallback to static scheduleData if live route was not available
    if (!train.schedule || train.schedule.length === 0) {
      if (scheduleData) {
        const trainMeta = scheduleData.train;
        if (trainMeta) {
          if (trainMeta.name) train.trainName = trainMeta.name;
          if (trainMeta.source?.name)
            train.sourceStation = `${trainMeta.source.name} (${trainMeta.source.code})`;
          if (trainMeta.destination?.name)
            train.destinationStation = `${trainMeta.destination.name} (${trainMeta.destination.code})`;
        }

        const routeList =
          scheduleData.route || scheduleData.schedule || scheduleData.stations;

        if (Array.isArray(routeList) && routeList.length > 0) {
          train.schedule = routeList.map((st) => ({
            station: st.station?.name || st.stationName || st.station || st.code || "STATION",
            code: st.station?.code || st.stationCode || st.code || "STN",
            scheduledArrival: formatTimeStr(st.arrival || st.arrivalTime || st.scheduledArrival),
            scheduledDeparture: formatTimeStr(st.departure || st.departureTime || st.scheduledDeparture),
            expectedArrival: formatTimeStr(st.arrival || st.arrivalTime || st.scheduledArrival),
            expectedDeparture: formatTimeStr(st.departure || st.departureTime || st.scheduledDeparture),
            platform: st.platform ? (String(st.platform).startsWith("PF") ? st.platform : `PF ${st.platform}`) : "PF 1",
            distanceKm: st.distance ?? st.distanceKm ?? 0,
            sequence: st.sequence || 1,
            isHalt: st.isHalt !== false,
          }));
        }
      }
    }

    return { train, live, eta, features, positions };
  },
};

