import { useEffect, useRef } from "react";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

const TILE_URL = "https://tile.openstreetmap.org/{z}/{x}/{y}.png";
const TILE_ATTRIBUTION =
  '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors';

// Mainland India, used when there are no trains to fit
const INDIA_BOUNDS = [
  [7.5, 68.0],
  [35.5, 97.5],
];

const dotColor = (delay) =>
  delay == null ? "#2563eb" : delay > 15 ? "#dc2626" : "#16a34a";

/**
 * Real map of every train in `trains` ({ trainNumber, trainName, lat, lng, delayMinutes }).
 * Canvas rendering keeps a few thousand markers smooth.
 */
export function FleetMap({ trains, onSelectTrain }) {
  const elRef = useRef(null);
  const mapRef = useRef(null);
  const layerRef = useRef(null);
  const fittedRef = useRef(false);
  const onSelectRef = useRef(onSelectTrain);

  useEffect(() => {
    onSelectRef.current = onSelectTrain;
  }, [onSelectTrain]);

  useEffect(() => {
    const map = L.map(elRef.current, {
      preferCanvas: true,
      scrollWheelZoom: false, // don't hijack page scrolling
      zoomSnap: 0.25,
      minZoom: 4,
    });
    L.tileLayer(TILE_URL, { attribution: TILE_ATTRIBUTION, maxZoom: 18 }).addTo(map);
    map.fitBounds(INDIA_BOUNDS);
    map.on("focus", () => map.scrollWheelZoom.enable());
    map.on("blur", () => map.scrollWheelZoom.disable());

    layerRef.current = L.layerGroup().addTo(map);
    mapRef.current = map;

    return () => {
      map.remove();
      mapRef.current = null;
      layerRef.current = null;
      fittedRef.current = false;
    };
  }, []);

  useEffect(() => {
    const map = mapRef.current;
    const layer = layerRef.current;
    if (!map || !layer) return;

    layer.clearLayers();
    const placed = trains.filter((t) => Number.isFinite(t.lat) && Number.isFinite(t.lng));

    placed.forEach((t) => {
      L.circleMarker([t.lat, t.lng], {
        // small dots: the live feed has ~2000 trains
        radius: trains.length > 200 ? 3.5 : 6,
        color: "#ffffff",
        weight: trains.length > 200 ? 0.8 : 1.5,
        fillColor: dotColor(t.delayMinutes),
        fillOpacity: 0.85,
      })
        .bindTooltip(`${t.trainNumber} ${t.trainName || ""}`.trim(), { direction: "top", offset: [0, -4] })
        .on("click", () => onSelectRef.current?.(t.trainNumber))
        .addTo(layer);
    });

    // Fit once to the trains, then leave the view to the user
    if (!fittedRef.current && placed.length > 0) {
      map.fitBounds(
        L.latLngBounds(placed.map((t) => [t.lat, t.lng])),
        { padding: [24, 24], maxZoom: 7 }
      );
      fittedRef.current = true;
    }
  }, [trains]);

  return <div ref={elRef} className="fleet-map" role="region" aria-label="Map of running trains" />;
}
