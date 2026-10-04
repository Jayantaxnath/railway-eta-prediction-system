# RailX frontend

React 19 app built with Vite 8. It has three views (Train Status, Station Board and Control
Room) plus the Privacy Policy and Terms pages, and it is designed to work on phones.

## Run

Requires Node 20.19+ or 22.12+, and the backend running on port 8080.

```bash
npm install
npm run dev      # http://localhost:5173
npm run build    # production build in dist/
npm run lint
```

The app calls the backend at `/api` on its own address, and the dev server forwards those
requests to `http://127.0.0.1:8080`. Inside Docker, `API_PROXY_TARGET` points the proxy at the
`backend` container instead. Set `VITE_API_BASE_URL` only if the backend runs on a different host
from the frontend.

`VITE_USE_POLLING=true` makes the dev server notice file changes inside Docker on Windows and
macOS. It costs some idle CPU, so it is only set in Docker Compose.

## Code layout

```
src/
  pages/         Dashboard, Result (train status), StationBoard, ControlRoom, Legal
  components/    Header, footer, search, route map, station timeline, fleet map, skeleton loaders
  hooks/         useTrainTracker (train state), useRecentSearches (local storage)
  services/      API calls (Axios)
  utils/         apiCache (session cache for Station Board / Control Room), formatters, delay helpers
  styles/        tokens.css holds colours and sizes; one file per area, skeleton.css for the loading shimmer
public/          favicon, app icons and web manifest
```

Maps use Leaflet with OpenStreetMap tiles. Icons come from `lucide-react`.

## Station Board & Control Room: loading, caching and data labels

Both pages show a skeleton screen (matching the real layout, not a generic spinner) while their
first fetch is in flight, instead of flashing sample data before the real result arrives.

Fetched results are kept in an in-memory cache (`src/utils/apiCache.js`) for the lifetime of the
page load, keyed per station for Station Board and as a single snapshot for Control Room.
Switching between the three tabs reuses that cache instead of re-calling the backend. Only a
**real** result is cached — a failed or empty response is never cached, so the next visit to that
station (or the Control Room's "Refresh" button) tries the live API again rather than getting
stuck on stale fallback data for the rest of the session.

The feed badge reflects what is actually on screen:

| Badge | Meaning |
|---|---|
| `LIVE` | Real data from the backend |
| `SCHEDULED DATA` | Live feed unavailable; showing the curated schedule for INDB/NDLS/CNB/BPL |
| `NO LIVE DATA` | Live feed unavailable and there is no curated schedule for this station |

Control Room's "Section congestion" panel is computed from live train positions (grouped into
~110 km geographic cells, ranked by train count) when the feed is live, and only shows the
hardcoded sample sections — tagged `Sample data` — when the live feed itself is unavailable.
