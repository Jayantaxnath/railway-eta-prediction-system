# RailX Performance Metrics

Measured on 27 September 2026 against the Docker Compose stack in this repository.

| | |
|---|---|
| Runtime | Docker 28.5.1, all four services running locally |
| ML libraries | XGBoost 3.2.0 (CPU build), scikit-learn 1.9.1, Python 3.11 |

Every number below was measured or read from files in this repository.

## 1. Summary

| Area | Metric | Value |
|---|---|---|
| ML service | `POST /predict-eta` latency | p50 14.6 ms, p95 34.4 ms |
| Backend | `GET /api/trains/{n}/eta` latency | p50 17.6 ms, p95 47.2 ms |
| Frontend | JavaScript bundle | 465.3 kB (144.2 kB gzipped) |
| Frontend | CSS bundle | 47.7 kB (13.6 kB gzipped) |
| Backend | Startup time | 28.6 s |
| Memory (idle) | Backend / ML / Postgres | 477 MiB / 208 MiB / 52 MiB |

## 2. Service latency

Sequential requests from the host to the local containers. The first request of each
endpoint is reported separately; percentiles are over the requests that follow it.

| Endpoint | Requests | First request | p50 | p95 | Max | Response size |
|---|---|---|---|---|---|---|
| ML `POST /predict-eta` | 300 | 38.7 ms | 14.6 ms | 34.4 ms | 39.1 ms | 0.2 kB |
| ML `GET /health` | 300 | 26.2 ms | 7.2 ms | 25.6 ms | 38.4 ms | 0.1 kB |
| Backend `GET /api/trains/12919/eta` | 100 | 33.1 ms | 17.6 ms | 47.2 ms | 148.2 ms | 0.3 kB |

All requests returned HTTP 200.

### Endpoints that depend on RailRadar

Train status, schedules, search, route geometry, station boards and the live map fetch data
from the RailRadar API on every request. Their response time is RailRadar's response time
plus a few milliseconds, so they are not benchmarked here. While measuring, RailRadar began
rejecting requests with **HTTP 429 (Too Many Requests)** after roughly 20 calls in a short
period. See 4.1.

## 3. Frontend

Production build (`npm run build`, Vite 8), built in 3.5 s:

| File | Size | Gzipped |
|---|---|---|
| `index.html` | 0.9 kB | 0.5 kB |
| JavaScript bundle | 465.3 kB | 144.2 kB |
| CSS bundle | 47.7 kB | 13.6 kB |

The JavaScript bundle includes React, Leaflet, Axios and the Lucide icons used by the app.
Map tiles are loaded separately from OpenStreetMap as the user pans and zooms.

## 4. Resources

| Service | Docker image | Memory at idle |
|---|---|---|
| Backend (Spring Boot, Java 21) | 565 MB | 477 MiB |
| ML service (FastAPI, XGBoost) | 989 MB | 208 MiB |
| PostgreSQL 16 | 419 MB | 52 MiB |
| Frontend dev server (Node 20) | 193 MB | 428 MiB |

The backend takes 28.6 s to start. The frontend dev server uses 20 to 23% CPU while idle
because file polling is enabled (see 4.3).

### 4.1 Findings that affect performance

1. **RailRadar rate limit.** The backend does not cache RailRadar responses. Opening one train
   page sends seven backend requests in parallel plus one for the route map, and at least
   three of them (live data, schedule and route geometry) call RailRadar. A few page loads in
   quick succession can hit the rate limit. Caching responses for 30 to 60 seconds would remove
   most repeated calls.
2. **Rate-limit errors are reported as "not found".** When RailRadar returns HTTP 429, the
   backend returns HTTP 404, so the app shows a missing-train message instead of "try again
   shortly".
3. **Live map payload.** `/api/trains/live-map` returns about 690 kB for roughly 2,000 trains.
   RailRadar's own response is about 840 kB, which is why the backend's WebClient buffer
   limit was raised from 256 kB to 5 MB.
4. **Dev-server polling.** `VITE_USE_POLLING=true` makes file changes visible inside Docker on
   Windows but keeps the dev server at 20 to 23% CPU. Raising the polling interval (for
   example `watch.interval: 1000` in `vite.config.js`) would reduce it. This does not apply to
   a production build.

## 5. Reproducing these numbers

```bash
# Latency (start the stack first with docker compose up -d)
python scripts/benchmark_api.py

# Frontend bundle sizes
cd frontend/railway-frontend && npm run build

# Memory and CPU
docker stats --no-stream
```

Latency depends on the machine and on other load at the time, so expect some variation
between runs.
