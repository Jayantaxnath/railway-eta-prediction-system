# RailX backend

Spring Boot 3.2 service (Java 21) that serves the RailX API on port 8080. It fetches live data
from RailRadar and OpenWeatherMap, stores train data in PostgreSQL, and requests arrival
predictions from the ML service.

## Run

```bash
export POSTGRES_USER=... POSTGRES_PASSWORD=... RAILWAY_API_KEY=... OPENWEATHER_API_KEY=...
mvn spring-boot:run
```

Configuration is read from environment variables; see the table in the root
[README](../README.md#configuration). The `dev` profile (`application-dev.properties`) points at
a local `traineta_db` database and turns on SQL and debug logging:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Health check: `GET /actuator/health`.

## Code layout

```
src/main/java/com/traineta/
  controller/   REST endpoints for trains, stations, ETA and features
  service/      Business logic (ETA, features, live data, weather, congestion)
  client/       HTTP clients for RailRadar, OpenWeatherMap and the ML service
  scheduler/    TrainDataScheduler, refreshes ACTIVE_TRAIN_NUMBERS every 5 minutes
  entity/       JPA entities: Train, TrainPosition, TrainSchedule, Station, EtaPrediction, WeatherData
  repository/   Spring Data repositories
  dto/          Request and response objects
  config/       WebClient, Jackson and scheduler setup
```

## Predictions

`POST /api/trains/{number}/eta/predict` builds the 10 input features, sends them to the ML
service and stores the result. If the ML service returns nothing or takes longer than 10
seconds, a built-in formula is used instead and the result is stored with `predictionSource`
set to `FALLBACK_MOCK`.

The 10-second limit is set in `MlApiClient`. The `ML_API_TIMEOUT` property exists in
`application.properties` but is not read yet.

## Notes

- RailRadar responses are not cached, and RailRadar rate-limits requests (HTTP 429). When that
  happens, the affected endpoints currently return HTTP 404.
- The RailRadar WebClient accepts responses up to 5 MB, because the nationwide live map is
  about 840 kB.

## Tests

```bash
mvn test
```

12 test classes. The pipeline integration test uses an in-memory H2 database, so no PostgreSQL
instance is needed.
