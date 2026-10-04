# How the teams communicate

RailX was built by three groups of two people. Each group owns one layer, and the groups
exchange data only through the contracts in [contracts/](contracts/).

## The three groups

| Group | Owns | Inputs | Outputs |
|---|---|---|---|
| 1. Data and ML | Model training and the ML service | Historical journeys, schedules and weather | `eta_feature_spec.yaml`, request and result schemas, trained models |
| 2. Backend | Live data, feature building, the public API | Live train data, the timetable database, the weather API, the feature spec | Feature vectors for the ML service, API responses for the frontend |
| 3. Frontend | Everything users see | Backend API responses | The RailX web app |

## What each group does

**Group 1, Data and ML**

1. Analyse historical data.
2. Decide which features the model needs.
3. Build the training dataset and train the models.
4. Define the model's input and output in the shared contracts.
5. Run the ML service.

`eta_feature_spec.yaml` describes every feature: its name, type, unit, source, whether it is
raw or derived, how it is calculated and what to do when it is missing.

**Group 2, Backend**

1. Fetch live train data and normalise it into `NormalizedTrainState`.
2. Calculate the features listed in `eta_feature_spec.yaml`.
3. Send them to the ML service as an `ETAPredictionRequest`.
4. Combine the prediction with the current train and schedule information.
5. Serve the result to the frontend.

**Group 3, Frontend**

Calls the backend (for example `GET /api/trains/{number}/eta`) and shows the train's current
position, speed and delay, the upcoming stations, scheduled and predicted times, the
prediction range and when the data was last updated.

## One prediction, end to end

```
External data sources
   │
   ▼
Backend normalises the data            normalized_train_state.schema.json
   │
   ▼
Backend builds the features            rules from eta_feature_spec.yaml
   │
   ▼
POST /predict-eta to the ML service    eta_prediction_request.schema.json
   │
   ▼
ML service predicts remaining minutes  eta_prediction_result.schema.json
   │
   ▼
Backend adds train and schedule data   train_eta_api_response.schema.json
   │
   ▼
Frontend displays it
```

The ML service never sends data to the frontend directly. The backend is always in between.

## Who owns which contract

| File | Defined by | Produced by | Used by |
|---|---|---|---|
| `eta_feature_spec.yaml` | Group 1 | Group 1 | Groups 1 and 2 |
| `normalized_train_state.schema.json` | Group 2 | Group 2 | Group 2 |
| `eta_prediction_request.schema.json` | Group 1 | Group 2 | ML service |
| `eta_prediction_result.schema.json` | Group 1 | ML service | Group 2 |
| `train_eta_api_response.schema.json` | Group 2 | Group 2 | Group 3 |
