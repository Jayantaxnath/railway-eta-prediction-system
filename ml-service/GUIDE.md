# Calling the ML service

How another program, such as the RailX backend, gets a prediction from the ML service. For
how the prediction is calculated, see [MODEL_PIPELINE.md](MODEL_PIPELINE.md).

## Start the service

```bash
pip install -r requirements.txt
uvicorn main:app --host 0.0.0.0 --port 8000
```

Check that it is up:

```bash
curl http://localhost:8000/health
# {"status":"healthy","model_version":"hybrid-eta-v2-xgb-dual"}
```

Interactive API documentation is at http://localhost:8000/docs.

## Request

`POST /predict-eta` with a JSON body:

```json
{
  "train_id": "12919",
  "target_station_id": "NDLS",
  "observed_at": "2026-09-27T08:30:00+05:30",
  "current_speed_kmph": 65.0,
  "average_speed_last_5_minutes": 60.0,
  "current_delay_minutes": 15.0,
  "distance_to_target_km": 120.0,
  "scheduled_time_to_target_minutes": 110.0,
  "rainfall_mm": null,
  "congestion_score": null,
  "historical_section_average_minutes": null
}
```

| Field | Required | Rule |
|---|---|---|
| `train_id` | yes | Train number as text |
| `target_station_id` | yes | Station code |
| `observed_at` | yes | ISO 8601 time of the reading |
| `current_speed_kmph` | yes | 0 or more |
| `average_speed_last_5_minutes` | yes | 0 or more |
| `current_delay_minutes` | yes | Any number; negative means ahead of schedule |
| `distance_to_target_km` | yes | 0 or more |
| `scheduled_time_to_target_minutes` | yes | 0 or more |
| `rainfall_mm` | no | 0 or more, or `null` |
| `congestion_score` | no | 0 to 1, or `null` |
| `historical_section_average_minutes` | no | 0 or more, or `null` |

Fields that are not in this list are rejected.

## Response

The response to the request above:

```json
{
  "train_id": "12919",
  "target_station_id": "NDLS",
  "predicted_remaining_minutes": 126.15,
  "lower_bound_minutes": 106.38,
  "upper_bound_minutes": 145.91,
  "confidence_score": 0.87,
  "model_version": "hybrid-eta-v2-xgb-dual"
}
```

## Status codes

| Code | Meaning |
|---|---|
| 200 | Prediction returned |
| 422 | The body failed validation, for example a negative speed, a missing required field or an unknown field. The response lists each problem. |
| 404 | Wrong path. The endpoint is `/predict-eta`, not `/predict`. |

## Example in Python

```python
import requests

response = requests.post(
    "http://localhost:8000/predict-eta",
    json=payload,
    timeout=10,
)
response.raise_for_status()
prediction = response.json()
```

Always set a timeout. The RailX backend waits up to 10 seconds and falls back to its own
formula if the service does not answer.
