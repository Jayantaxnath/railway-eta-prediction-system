"""Diagnostic evaluation of the deployed XGBoost models on the 500-row labelled sample.

Features are built exactly as in train_full_model.py. The sample rows may also have
been part of the training data, so every number here is in-sample.

Run from anywhere:  python ml-service/evaluate_sample.py
See PERFORMANCE.md (section 2) for how to read the output.
"""
import json
from pathlib import Path

import numpy as np
import pandas as pd
import xgboost as xgb
from sklearn.metrics import accuracy_score, mean_absolute_error, mean_squared_error, roc_auc_score

# The model files were saved with XGBoost 3.4.1; older versions misread them without an error
_major, _minor = (int(x) for x in xgb.__version__.split(".")[:2])
if (_major, _minor) < (3, 2):
    raise SystemExit(f"XGBoost {xgb.__version__} found; 3.2 or newer is required to load these models correctly.")

HERE = Path(__file__).resolve().parent
df = pd.read_csv(HERE / "data" / "ir_train_sample.csv")

feat = pd.DataFrame()
feat["distance_to_target_km"] = df["distance_km"].astype(float)
feat["scheduled_time_to_target_minutes"] = (df["scheduled_travel_hours"] * 60.0).astype(float)
feat["current_delay_minutes"] = df["delay_minutes"].astype(float)
feat["current_speed_kmph"] = np.where(
    df["scheduled_travel_hours"] > 0,
    np.maximum(20.0, df["distance_km"] / df["scheduled_travel_hours"]),
    55.0,
).astype(float)
feat["average_speed_last_5_minutes"] = feat["current_speed_kmph"]
feat["rainfall_mm"] = np.where(df["season"] == "Monsoon", 12.5, 0.0).astype(float)
feat["congestion_score"] = df["zone_congestion_index"].astype(float)
feat["route_historical_ontime_pct"] = df["route_historical_ontime_pct"].astype(float)
feat["departure_hour"] = df["departure_hour"].astype(float)
feat["day_of_week"] = df["day_of_week"].astype(float)

y_cls = df["is_delayed"]
y_reg = df["delay_minutes"].astype(float)

cls = xgb.XGBClassifier()
cls.load_model(str(HERE / "models" / "delay_risk_xgboost.json"))
reg = xgb.XGBRegressor()
reg.load_model(str(HERE / "models" / "eta_xgboost.json"))


def evaluate(x):
    proba = cls.predict_proba(x)[:, 1]
    pred = reg.predict(x)
    return {
        "roc_auc": round(float(roc_auc_score(y_cls, proba)), 4),
        "accuracy": round(float(accuracy_score(y_cls, proba >= 0.5)), 4),
        "mae_minutes": round(float(mean_absolute_error(y_reg, pred)), 2),
        "rmse_minutes": round(float(np.sqrt(mean_squared_error(y_reg, pred))), 2),
    }


leak_hidden = feat.copy()
leak_hidden["current_delay_minutes"] = 0.0

mean_pred = np.full(len(y_reg), y_reg.mean())
result = {
    "rows": int(len(df)),
    "delayed_share": round(float(y_cls.mean()), 4),
    "delay_minutes_mean": round(float(y_reg.mean()), 2),
    "threshold_check": {
        "max_delay_when_not_delayed": int(df.loc[y_cls == 0, "delay_minutes"].max()),
        "min_delay_when_delayed": int(df.loc[y_cls == 1, "delay_minutes"].min()),
    },
    "as_trained_with_current_delay": evaluate(feat),
    "current_delay_hidden": evaluate(leak_hidden),
    "baseline_predict_mean": {
        "mae_minutes": round(float(mean_absolute_error(y_reg, mean_pred)), 2),
        "rmse_minutes": round(float(np.sqrt(mean_squared_error(y_reg, mean_pred))), 2),
    },
    "baseline_majority_class_accuracy": round(float(max(y_cls.mean(), 1 - y_cls.mean())), 4),
}
print(json.dumps(result, indent=2))
