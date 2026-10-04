# Shared contracts

JSON schemas and a feature specification that the backend, the ML service and the frontend
agree on. They let each team build its part without waiting for the others. For how the
teams used them, see [communication.md](communication.md).

## Data flow

```
External data
   │
   ▼
NormalizedTrainState   (backend, internal)
   │  features built using eta_feature_spec.yaml
   ▼
ETAPredictionRequest   (backend ──► ML service, POST /predict-eta)
   │
   ▼
ETAPredictionResult    (ML service ──► backend)
   │
   ▼
TrainETAAPIResponse    (backend ──► frontend)
```

## Files

| Contract | Produced by | Used by |
|---|---|---|
| [normalized_train_state.schema.json](contracts/normalized_train_state.schema.json) | Backend | Backend feature building |
| [eta_prediction_request.schema.json](contracts/eta_prediction_request.schema.json) | Backend | ML service |
| [eta_prediction_result.schema.json](contracts/eta_prediction_result.schema.json) | ML service | Backend |
| [train_eta_api_response.schema.json](contracts/train_eta_api_response.schema.json) | Backend | Frontend |
| [eta_feature_spec.yaml](contracts/eta_feature_spec.yaml) | ML team | Backend and ML service |

In each schema, `properties` lists every allowed field and `required` lists the fields that
must be present.

## Rules

1. Timestamps are ISO 8601 in UTC, for example `2026-09-27T08:30:00Z`.
2. Distances are in kilometres, speeds in km/h and durations in minutes.
3. Do not rename a field or change its meaning without agreement from every team.
4. Add new fields as optional first.
5. Training and live prediction must build features in exactly the same way. The definitions
   live in `eta_feature_spec.yaml`.
