# Indian Railways: Predict Train Delay (Kaggle)

The dataset used to train the RailX models. It comes from the Kaggle competition
[Indian Railways: Predict Train Delay](https://www.kaggle.com/competitions/indian-railways-predict-train-delay/data).
The task is to predict whether a journey arrives more than 15 minutes late.

## Files

| File | Rows | Columns | Contents |
|---|---|---|---|
| `ir_train.csv` | 1,500,000 | 45 | Journeys with features and the target `is_delayed` |
| `ir_test.csv` | 375,000 | 42 | Journeys to predict, without the target |
| `ir_sample_submission.csv` | | 2 | Submission format: `journey_id`, `is_delayed` |
| `ir_data_dictionary.csv` | | | Description of each column |

The full files are not in this repository because of their size. Small samples of each are in
`ml-service/data/`.

## Columns

The test file has 42 columns. The training file has the same 42 plus `primary_delay_cause`,
`delay_minutes` and `is_delayed`.

| Group | Columns |
|---|---|
| Identity | `journey_id`, `train_number`, `train_type` |
| Time | `departure_date`, `year`, `month`, `day_of_week`, `departure_hour`, `is_weekend`, `is_night_departure`, `is_peak_hour`, `is_festival_season`, `season` |
| Geography | `zone`, `zone_abbr`, `source_station_category`, `destination_station_category`, `zone_fog_index`, `zone_congestion_index` |
| Route | `distance_km`, `num_scheduled_stops`, `scheduled_travel_hours`, `track_doubled`, `is_hdn_route`, `traction_type`, `is_electrified`, `psr_count`, `is_circular_route` |
| Weather | `is_monsoon_season`, `is_fog_risk`, `fog_risk_score`, `season_severity_score` |
| Rolling stock | `loco_age_years`, `coach_age_years`, `has_lhb_coaches`, `is_rake_shared`, `maintenance_score` |
| Operations | `seat_utilisation_pct`, `is_overloaded`, `late_incoming_rake`, `is_special_train`, `route_historical_ontime_pct` |
| Training file only | `primary_delay_cause`, `delay_minutes`, `is_delayed` |

## Target

`is_delayed` is 1 when the train arrives more than 15 minutes late.

## Data leakage

`primary_delay_cause` and `delay_minutes` describe the outcome and are not in the test file,
so they must not be used as model inputs. The current RailX training script does use
`delay_minutes` as an input; [PERFORMANCE.md](../PERFORMANCE.md) shows the effect.

## Origin

The dataset page does not say whether the data is real or synthetic.
