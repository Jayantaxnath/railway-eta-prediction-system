# Contributors

RailX began as a team project, with a team of six split into three groups. The team did not
finish it. **Jayanta Nath picked the project up from where it was left incomplete and completed
it alone**, so most of the credit for the working system goes to Jayanta.

## Primary author

### Jayanta Nath, project owner, architect and lead developer

- Took over the incomplete team project and carried it through to a finished, working system
- Completed the remaining work across all three layers: ML, backend and frontend
- Designed how the layers communicate and wrote the shared contracts in `shared-contracts/`
  (including `communication.md`)
- Defined `eta_feature_spec.yaml`: feature names, types, sources, formulas and missing-value rules
- Chose the two-model XGBoost approach (delay classifier and delay regressor)
- Designed the ETA formula that blends scheduled, historical and speed-based estimates
- Defined the prediction output, including bounds and a confidence score
- Built the ML service integration (request/response schemas, model loading, dual inference,
  fallback to global defaults for missing features)

See [PERFORMANCE.md](PERFORMANCE.md) for the current evaluation of the models.

## Original team (initial groundwork)

The original six-person team split the work into three groups that agreed on shared data
contracts so they could work in parallel. Their contributions below are the early groundwork
that the project was later completed from.

```
Group 1 (ML)       eta_feature_spec.yaml            ──►  Group 2 (Backend)
Group 2            POST /predict-eta                ──►  Group 1 ML service
Group 1            prediction result                ──►  Group 2
Group 2            train_eta_api_response.schema    ──►  Group 3 (Frontend)
```

The ML service never talks to the frontend directly. The backend sits between them.

### Group 1: Data and ML

- **Member 2, ML engineer**: prepared the 1.5 million journey Indian Railways dataset from
  Kaggle, wrote `ml-service/train_full_model.py`, produced the initial model files and built
  `train_history_lookup.parquet` with per-train on-time statistics.

### Group 2: Backend and live data

- **Member 3, backend lead**: started the Spring Boot (Java 21) backend, database entities and
  PostgreSQL schema, `TrainDataScheduler`, `FeatureService` and `MlApiClient`.
- **Member 4, backend engineer**: started the OpenWeatherMap integration, `EtaService`, the
  train and station endpoints, CORS, the health check and environment-based configuration.

### Group 3: Frontend

- **Member 5, frontend lead**: set up the React and Vite project, the API layer, the
  `useTrainTracker` hook, and early versions of the home and train status pages.
- **Member 6, frontend engineer**: started the Station Board and Control Room pages, the
  Ground Reality Simulator panel and the route map.

## Shared contract ownership

| Contract | Defined by | Produced by | Used by |
|---|---|---|---|
| `eta_feature_spec.yaml` | Jayanta Nath | Jayanta Nath | ML and backend |
| `normalized_train_state.schema.json` | Jayanta Nath | Backend | Backend |
| `eta_prediction_request.schema.json` | Jayanta Nath | Backend | ML service |
| `eta_prediction_result.schema.json` | Jayanta Nath | ML service | Backend |
| `train_eta_api_response.schema.json` | Jayanta Nath | Backend | Frontend |
