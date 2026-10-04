# Indian Railways Passenger Train Delays (Kaggle)

A snapshot of delay and punctuality statistics for about 1,900 train and station pairs,
collected from [ETrain.info](https://etrain.info) with a web scraper in September 2025.
Each row describes one train's record at one station.

Dataset: [kaggle.com/datasets/naijilaji/indian-railways-passenger-train-delays-dataset](https://www.kaggle.com/datasets/naijilaji/indian-railways-passenger-train-delays-dataset)

## Columns

| Column | Meaning |
|---|---|
| `train_number`, `train_name` | Train |
| `station_code`, `station_name` | Station |
| `average_delay_minutes` | Average delay at the station |
| `pct_right_time` | Share of arrivals on time |
| `pct_slight_delay` | Share of arrivals 0 to 15 minutes late |
| `pct_significant_delay` | Share of arrivals more than 15 minutes late |
| `pct_cancelled_unknown` | Share of runs cancelled or with no data |
| `scraped_at` | When the row was collected |

This dataset is not used by the current training script.
