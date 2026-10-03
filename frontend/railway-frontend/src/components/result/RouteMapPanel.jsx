import { useState, useEffect, useMemo, useRef } from "react";
import L from "leaflet";
import { LocateFixed, Maximize2, Map as MapIcon } from "lucide-react";
import "leaflet/dist/leaflet.css";
import { trainService } from "../../services/trainService";

// Lightweight, key-free basemap (OpenStreetMap standard tiles)
const TILE_URL = "https://tile.openstreetmap.org/{z}/{x}/{y}.png";
const TILE_ATTRIBUTION =
  '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors';

const FALLBACK_COORDS = [
  [22.72, 75.86], // INDB
  [23.17, 75.78], // UJN
  [23.25, 77.41], // BPL
  [24.15, 78.08], // BINA
  [25.44, 78.56], // VGLJ
  [26.21, 78.17], // GWL
  [27.18, 78.00], // AGC
  [28.61, 77.23], // NDLS
  [30.73, 76.77], // CDG
  [31.32, 75.57], // JUC
  [32.72, 74.85], // JAT
  [32.99, 74.93], // SVDK
];

const FALLBACK_STOPS = [
  { code: "INDB", name: "Indore Junction", lat: 22.72, lng: 75.86 },
  { code: "UJN", name: "Ujjain Junction", lat: 23.17, lng: 75.78 },
  { code: "BPL", name: "Bhopal Junction", lat: 23.25, lng: 77.41 },
  { code: "VGLJ", name: "VGL Jhansi", lat: 25.44, lng: 78.56 },
  { code: "AGC", name: "Agra Cantt", lat: 27.18, lng: 78.00 },
  { code: "NDLS", name: "New Delhi", lat: 28.61, lng: 77.23 },
  { code: "SVDK", name: "SMVD Katra", lat: 32.99, lng: 74.93 },
];

// Leaflet markers take raw HTML, so the train icon is inlined as SVG (lucide "train-front")
const TRAIN_MARKER_SVG =
  '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#1d4ed8" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M8 3.1V7a4 4 0 0 0 8 0V3.1"/><path d="m9 15-1-1"/><path d="m15 15 1-1"/><path d="M9 19c-2.8 0-5-2.2-5-5v-4a8 8 0 0 1 16 0v4c0 2.8-2.2 5-5 5Z"/><path d="m8 19-2 3"/><path d="m16 19 2 3"/></svg>';

const hasCoords = (st) => Number.isFinite(st?.lat) && Number.isFinite(st?.lng);

/**
 * Estimate the train's [lat, lng] from its live route position:
 * the current stop, moved `segmentProgress` of the way toward the next stop.
 */
function estimateTrainPosition(stops, liveData) {
  if (!liveData || stops.length === 0) return null;

  let idx = -1;
  if (liveData.currentSequence != null) {
    idx = stops.findIndex((s) => s.sequence === liveData.currentSequence);
  }
  if (idx === -1 && liveData.currentStation) {
    const code = String(liveData.currentStation).toUpperCase();
    idx = stops.findIndex((s) => String(s.code || "").toUpperCase() === code);
  }
  if (idx === -1 || !hasCoords(stops[idx])) return null;

  const cur = stops[idx];
  const next = stops.slice(idx + 1).find(hasCoords);
  const progress = Math.min(Math.max(Number(liveData.segmentProgress) || 0, 0), 1);
  if (!next || progress === 0) return [cur.lat, cur.lng];

  return [
    cur.lat + (next.lat - cur.lat) * progress,
    cur.lng + (next.lng - cur.lng) * progress,
  ];
}

function nearestCoordIndex(coords, [lat, lng]) {
  let best = 0;
  let bestDist = Infinity;
  coords.forEach(([cLat, cLng], i) => {
    const d = (cLat - lat) ** 2 + (cLng - lng) ** 2;
    if (d < bestDist) {
      bestDist = d;
      best = i;
    }
  });
  return best;
}

export function RouteMapPanel({ trainNumber, liveData, schedule }) {
  const [stopsList, setStopsList] = useState([]);
  const [coordsList, setCoordsList] = useState([]); // [[lat, lng], ...]
  const [loading, setLoading] = useState(false);
  const [isRealGis, setIsRealGis] = useState(false);

  const mapElRef = useRef(null);
  const mapRef = useRef(null);
  const overlayRef = useRef(null);
  const fittedKeyRef = useRef(null);

  useEffect(() => {
    if (!trainNumber) return;
    let cancelled = false;

    const applyFallback = () => {
      setIsRealGis(false);
      setCoordsList(FALLBACK_COORDS);
      setStopsList(FALLBACK_STOPS);
    };

    (async () => {
      setLoading(true);
      try {
        const res = await trainService.getRouteGeometry(trainNumber);
        if (cancelled) return;
        const dataObj = res?.data?.data || res?.data;

        if (dataObj?.geojson?.geometry) {
          const rawCoords = dataObj.geojson.geometry.coordinates || [];
          // Convert GeoJSON [lng, lat] -> [lat, lng]
          setCoordsList(rawCoords.map((c) => [c[1], c[0]]));
          setStopsList(dataObj.stops || []);
          setIsRealGis(true);
        } else {
          applyFallback();
        }
      } catch {
        if (cancelled) return;
        console.warn("GIS geometry endpoint fallback for train:", trainNumber);
        applyFallback();
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [trainNumber]);

  // Only halting stations get markers; the full route has hundreds of pass-through stations
  const haltStops = useMemo(() => {
    const haltCodes = new Set(
      (schedule || []).filter((s) => s.isHalt).map((s) => String(s.code).toUpperCase())
    );
    const withCoords = stopsList.filter(hasCoords);
    if (haltCodes.size === 0) return withCoords;
    return withCoords.filter(
      (s, i) =>
        i === 0 ||
        i === withCoords.length - 1 ||
        haltCodes.has(String(s.code || "").toUpperCase())
    );
  }, [stopsList, schedule]);

  const trainPosition = useMemo(
    () => estimateTrainPosition(stopsList, liveData),
    [stopsList, liveData]
  );

  // Create the map once
  useEffect(() => {
    if (loading || !mapElRef.current || mapRef.current) return;

    const map = L.map(mapElRef.current, {
      scrollWheelZoom: false, // don't hijack page scrolling
      preferCanvas: true, // faster for thousands of track points
      zoomSnap: 0.25, // fit long routes tightly instead of jumping a whole zoom level
    });
    L.tileLayer(TILE_URL, {
      attribution: TILE_ATTRIBUTION,
      maxZoom: 19,
    }).addTo(map);
    map.on("focus", () => map.scrollWheelZoom.enable());
    map.on("blur", () => map.scrollWheelZoom.disable());

    overlayRef.current = L.layerGroup().addTo(map);
    mapRef.current = map;

    return () => {
      map.remove();
      mapRef.current = null;
      overlayRef.current = null;
      fittedKeyRef.current = null;
    };
  }, [loading]);

  // Draw route, stops and train
  useEffect(() => {
    const map = mapRef.current;
    const overlay = overlayRef.current;
    if (!map || !overlay || coordsList.length === 0) return;

    overlay.clearLayers();

    const splitIdx = trainPosition ? nearestCoordIndex(coordsList, trainPosition) : 0;
    const travelled = coordsList.slice(0, splitIdx + 1);
    const remaining = coordsList.slice(splitIdx);

    if (travelled.length > 1) {
      L.polyline(travelled, { color: "#94a3b8", weight: 4, opacity: 0.9 }).addTo(overlay);
    }
    L.polyline(remaining, { color: "#2563eb", weight: 4, opacity: 0.9 }).addTo(overlay);

    haltStops.forEach((st, i) => {
      const isEnd = i === 0 || i === haltStops.length - 1;
      const marker = L.circleMarker([st.lat, st.lng], {
        radius: isEnd ? 6 : 4,
        color: isEnd ? "#1e3a8a" : "#2563eb",
        weight: 2,
        fillColor: "#ffffff",
        fillOpacity: 1,
      }).addTo(overlay);
      const label = st.name ? `${st.name} (${st.code})` : st.code;
      marker.bindTooltip(label, {
        direction: "top",
        offset: [0, -6],
        permanent: isEnd,
        className: "route-map-label",
      });
    });

    if (trainPosition) {
      L.marker(trainPosition, {
        icon: L.divIcon({
          className: "route-map-train",
          html: TRAIN_MARKER_SVG,
          iconSize: [30, 30],
          iconAnchor: [15, 15],
        }),
        zIndexOffset: 1000,
      })
        .bindTooltip(`Currently near ${liveData?.currentStation || "--"}`, {
          direction: "top",
          offset: [0, -14],
        })
        .addTo(overlay);
    }

    // Fit the whole route once per route (not on every live refresh)
    const routeKey = `${trainNumber}:${coordsList.length}`;
    if (fittedKeyRef.current !== routeKey) {
      map.fitBounds(L.latLngBounds(coordsList), { padding: [24, 24] });
      fittedKeyRef.current = routeKey;
    }
  }, [coordsList, haltStops, trainPosition, trainNumber, liveData?.currentStation, loading]);

  const recenterOnTrain = () => {
    if (mapRef.current && trainPosition) {
      mapRef.current.setView(trainPosition, 10);
    }
  };

  const showWholeRoute = () => {
    if (mapRef.current && coordsList.length > 0) {
      mapRef.current.fitBounds(L.latLngBounds(coordsList), { padding: [24, 24] });
    }
  };

  const mapButtonStyle = {
    fontSize: "12px",
    fontWeight: 700,
    color: "#1d4ed8",
    background: "#eff6ff",
    border: "1px solid #bfdbfe",
    borderRadius: "8px",
    padding: "6px 12px",
    minHeight: "36px",
    cursor: "pointer",
    display: "inline-flex",
    alignItems: "center",
    gap: "6px",
  };

  return (
    <section className="map-card" aria-label="Live route map">
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          flexWrap: "wrap",
          gap: "12px",
          marginBottom: "16px",
        }}
      >
        <div>
          <div style={{ display: "flex", alignItems: "center", flexWrap: "wrap", gap: "8px 10px" }}>
            <h2 style={{ margin: 0, fontSize: "17px", color: "#0f172a", fontWeight: 800, display: "flex", alignItems: "center", gap: "8px" }}>
              <MapIcon size={20} color="#1d4ed8" aria-hidden="true" />
              Route map
            </h2>
            {!isRealGis && (
              <span
                style={{
                  background: "#fef3c7",
                  color: "#92400e",
                  fontSize: "11px",
                  fontWeight: 700,
                  padding: "2px 8px",
                  borderRadius: "6px",
                }}
              >
                Approximate route
              </span>
            )}
          </div>
          <p style={{ margin: "4px 0 0", fontSize: "13px", color: "#64748b" }}>
            Tap a station to see its name.
          </p>
        </div>

        <div style={{ display: "flex", gap: "8px", alignItems: "center", flexWrap: "wrap" }}>
          <button type="button" style={mapButtonStyle} onClick={recenterOnTrain} disabled={!trainPosition}>
            <LocateFixed size={16} aria-hidden="true" />
            Locate train
          </button>
          <button type="button" style={mapButtonStyle} onClick={showWholeRoute}>
            <Maximize2 size={16} aria-hidden="true" />
            Whole route
          </button>
        </div>
      </div>

      {loading ? (
        <div style={{ height: "200px", display: "flex", alignItems: "center", justifyContent: "center", color: "#64748b", fontWeight: 600 }}>
          Loading route map…
        </div>
      ) : (
        <>
          <div ref={mapElRef} className="route-map-canvas" />

          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              flexWrap: "wrap",
              gap: "8px",
              marginTop: "12px",
              color: "#64748b",
              fontSize: "12px",
            }}
          >
            <span style={{ display: "flex", gap: "16px", flexWrap: "wrap" }}>
              <span><span style={{ display: "inline-block", width: "18px", height: "4px", background: "#94a3b8", verticalAlign: "middle", marginRight: "6px" }} />Travelled</span>
              <span><span style={{ display: "inline-block", width: "18px", height: "4px", background: "#2563eb", verticalAlign: "middle", marginRight: "6px" }} />Remaining</span>
              <span>
                <span style={{ display: "inline-block", width: "10px", height: "10px", borderRadius: "50%", border: "2px solid #2563eb", background: "#ffffff", verticalAlign: "middle", marginRight: "6px" }} />
                Stop
              </span>
            </span>
          </div>
        </>
      )}
    </section>
  );
}
