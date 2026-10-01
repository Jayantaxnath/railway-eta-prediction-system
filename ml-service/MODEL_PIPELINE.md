# How the ML service predicts arrival time

The ML service is a FastAPI app (`main.py`) that estimates how many minutes a train needs to
reach a station, with a lower bound, an upper bound and a confidence score. The logic is in
`model_service.py`. For request and response formats, see [GUIDE.md](GUIDE.md).

## At startup

`ETAModelService` loads these files from `models/`:

| File | Used for |
|---|---|
| `train_history_lookup.parquet` | Per-train history for 6,293 trains: delay rate, on-time percentage, average delay |
| `global_defaults.json` | The same values averaged over all trains, used when a train is not in the lookup |
| `eta_xgboost.json`, `eta_metadata.json` | The XGBoost regressor that predicts delay minutes, and its input order |
| `delay_risk_xgboost.json`, `delay_risk_metadata.json` | The XGBoost classifier. It is loaded but **not used** in predictions. |

If XGBoost cannot be imported or a model file fails to load, the service keeps running and
uses the formula fallbacks described below.

## Steps for one prediction

1. **Look up history.** Find the train in the history table (or use the global defaults). Its
   `historical_delay_rate` is used as the delay risk, a number between 0 and 1.
2. **Blend three ETAs.**
   ```
   live speed  = max(0.40 × current speed + 0.60 × 5-minute average speed, 1)
   speed ETA   = distance to target ÷ live speed × 60
   base ETA    = 0.40 × scheduled time to target
               + 0.25 × historical section time   (scheduled time if not given)
               + 0.35 × speed ETA
   ```
3. **Predict the extra delay.** The regressor receives 10 inputs: distance to target, scheduled
   time to target, current delay, current speed, 5-minute average speed, rainfall (0 if not
   given), congestion (0.2 if not given), the route's historical on-time percentage, and the
   hour and weekday of `observed_at`. Negative outputs are raised to 0.

   Without the regressor, the delay adjustment is the current delay multiplied by
   `0.50 + 0.40 × delay risk`, or the current delay unchanged if the train is on time or early.
4. **Add adjustments.**

   | Rainfall | Minutes added |
   |---|---|
   | not given or under 2 mm | 0 |
   | 2 to under 10 mm | 2 |
   | 10 to under 30 mm | 5 |
   | 30 mm or more | 10 |

   Congestion adds `congestion score × 15` minutes.
5. **Total.** `predicted = max(0, base ETA + delay adjustment + rainfall + congestion)`.
6. **Bounds and confidence.**
   ```
   uncertainty = predicted × (0.08 + 0.12 × delay risk + 0.08 × congestion)
   lower bound = max(0, predicted − uncertainty)
   upper bound = predicted + uncertainty
   confidence  = 1 − 0.40 × (upper − lower) ÷ max(predicted, 1), limited to 0.50 to 0.98
   ```

## Training

`train_full_model.py` reads `data/ir_train.csv` (1.5 million journeys from the Kaggle
"Indian Railways: Predict Train Delay" dataset, not included in the repository because of
its size). It writes the history lookup, the global defaults and both XGBoost models (100
trees, depth 6, learning rate 0.1).

The Kaggle data has no live telemetry, so the script derives the 10 inputs from journey-level
columns. For example, current speed is distance divided by scheduled travel time, and
rainfall is 12.5 mm for monsoon journeys and 0 otherwise.

**Known problems.** Current delay is set to the journey's final delay, which is the value the
regressor predicts, and the metrics are computed on the training data. The saved scores
(ROC-AUC 1.0, MAE 0.11 min) are therefore not real accuracy. With the current delay hidden,
the model does no better than chance. [PERFORMANCE.md](../PERFORMANCE.md) has the full analysis
and the steps to fix it.

`train_xgb_models.ipynb` is a separate experiment that trains a classifier on the Kaggle
dataset's own columns (44 features). `models/feature_importance.csv` comes from that notebook and does not
describe the deployed models. `models/eta_pipeline_config.json` is documentation from an
earlier version and is not read by the service.

The model files were saved with XGBoost 3.4.1. Use XGBoost 3.2 or newer: 3.0 loads them without
an error but shifts every regressor prediction by about 98 minutes.

## Testing

With the service running:

```bash
python tests/test_api.py
```

This sends 10 requests covering a normal train, a train near its station, a stopped train,
missing optional fields, extreme conditions, a train ahead of schedule, a zero distance, and
three invalid requests that must return HTTP 422. Results are written to
`tests/test_results.json`.

To check the models against the labelled sample data:

```bash
python evaluate_sample.py
```
