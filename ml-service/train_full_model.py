import json
import logging
from pathlib import Path
import pandas as pd
import numpy as np
import xgboost as xgb
from sklearn.metrics import roc_auc_score, mean_absolute_error, mean_squared_error

logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")
logger = logging.getLogger(__name__)

DATA_DIR = Path("./data")
MODEL_DIR = Path("./models")
MODEL_DIR.mkdir(exist_ok=True)

TRAIN_PATH = DATA_DIR / "ir_train.csv"
TEST_PATH = DATA_DIR / "ir_test.csv"

logger.info(f"Loading training dataset from {TRAIN_PATH}...")
train_df = pd.read_csv(TRAIN_PATH, low_memory=False)
logger.info(f"Train shape: {train_df.shape}")

logger.info(f"Loading test dataset from {TEST_PATH}...")
test_df = pd.read_csv(TEST_PATH, low_memory=False)
logger.info(f"Test shape: {test_df.shape}")

# 1. Build train history lookup table for fast delay/ontime lookups
logger.info("Generating train_history_lookup.parquet...")
train_history = train_df.groupby("train_number").agg(
    total_journeys=("is_delayed", "count"),
    historical_delay_rate=("is_delayed", "mean"),
    historical_ontime_pct=("is_delayed", lambda x: float((1 - x.mean()) * 100)),
    historical_avg_delay_minutes=("delay_minutes", "mean") if "delay_minutes" in train_df.columns else ("is_delayed", lambda x: float(x.mean() * 15)),
    historical_avg_speed_kmph=("distance_km", lambda x: 55.0),
).reset_index()

train_history.to_parquet(MODEL_DIR / "train_history_lookup.parquet", index=False)
logger.info(f"Saved train_history_lookup.parquet ({len(train_history)} distinct train routes)")

# Save global defaults
global_defaults = {
    "historical_delay_rate": float(train_df["is_delayed"].mean()),
    "historical_ontime_pct": float((1 - train_df["is_delayed"].mean()) * 100),
    "historical_avg_delay_minutes": float(train_df["delay_minutes"].mean() if "delay_minutes" in train_df.columns else 12.0),
    "historical_avg_speed_kmph": 55.0,
    "default_ontime_pct": float((1 - train_df["is_delayed"].mean()) * 100),
    "default_delay_minutes": float(train_df["delay_minutes"].mean() if "delay_minutes" in train_df.columns else 12.0),
    "total_training_records": len(train_df),
}
with open(MODEL_DIR / "global_defaults.json", "w") as f:
    json.dump(global_defaults, f, indent=2)
logger.info("Saved global_defaults.json")

# 2. Prepare 10 Live Telemetry Features for 100% Inference Alignment
logger.info("Preparing 10 Live Telemetry Features for XGBoost training...")

train_df["distance_to_target_km"] = train_df["distance_km"].astype(float)
train_df["scheduled_time_to_target_minutes"] = (train_df["scheduled_travel_hours"] * 60.0).astype(float)
train_df["current_delay_minutes"] = (train_df["delay_minutes"] if "delay_minutes" in train_df.columns else 0.0).astype(float)
train_df["current_speed_kmph"] = np.where(
    train_df["scheduled_travel_hours"] > 0,
    np.maximum(20.0, train_df["distance_km"] / train_df["scheduled_travel_hours"]),
    55.0
).astype(float)
train_df["average_speed_last_5_minutes"] = train_df["current_speed_kmph"].astype(float)
train_df["rainfall_mm"] = np.where(train_df["season"] == "Monsoon", 12.5, 0.0).astype(float)
train_df["congestion_score"] = (train_df["zone_congestion_index"] if "zone_congestion_index" in train_df.columns else 0.2).astype(float)
train_df["route_historical_ontime_pct"] = (train_df["route_historical_ontime_pct"] if "route_historical_ontime_pct" in train_df.columns else 85.0).astype(float)
train_df["departure_hour"] = (train_df["departure_hour"] if "departure_hour" in train_df.columns else 12).astype(float)
train_df["day_of_week"] = (train_df["day_of_week"] if "day_of_week" in train_df.columns else 3).astype(float)

feature_cols = [
    "distance_to_target_km",
    "scheduled_time_to_target_minutes",
    "current_delay_minutes",
    "current_speed_kmph",
    "average_speed_last_5_minutes",
    "rainfall_mm",
    "congestion_score",
    "route_historical_ontime_pct",
    "departure_hour",
    "day_of_week",
]

X_train = train_df[feature_cols]
y_train_cls = train_df["is_delayed"]
y_train_reg = train_df["delay_minutes"] if "delay_minutes" in train_df.columns else train_df["is_delayed"] * 15.0

# --- Model 1: XGBClassifier for Delay Risk ---
logger.info(f"1. Training XGBoost Classifier (Delay Risk) on {len(X_train)} samples with {len(feature_cols)} features...")
cls_model = xgb.XGBClassifier(
    n_estimators=100,
    max_depth=6,
    learning_rate=0.1,
    tree_method="hist",
    n_jobs=-1,
    random_state=42,
    eval_metric="logloss"
)
cls_model.fit(X_train, y_train_cls)

train_preds_cls = cls_model.predict_proba(X_train)[:, 1]
auc = roc_auc_score(y_train_cls, train_preds_cls)
logger.info(f"XGBoost Classifier Training Completed! ROC-AUC Score: {auc:.4f}")

cls_out = MODEL_DIR / "delay_risk_xgboost.json"
cls_model.save_model(str(cls_out))
logger.info(f"Saved trained XGBoost classifier to {cls_out}")

cls_metadata = {
    "model_name": "XGBClassifier",
    "num_training_samples": len(X_train),
    "roc_auc_score": float(auc),
    "feature_names": feature_cols,
}
with open(MODEL_DIR / "delay_risk_metadata.json", "w") as f:
    json.dump(cls_metadata, f, indent=2)

# --- Model 2: XGBRegressor for ETA Delay Minutes ---
logger.info(f"2. Training XGBoost Regressor (ETA Delay Minutes) on {len(X_train)} samples with {len(feature_cols)} features...")
reg_model = xgb.XGBRegressor(
    n_estimators=100,
    max_depth=6,
    learning_rate=0.1,
    tree_method="hist",
    n_jobs=-1,
    random_state=42,
    eval_metric="rmse"
)
reg_model.fit(X_train, y_train_reg)

train_preds_reg = reg_model.predict(X_train)
mae = mean_absolute_error(y_train_reg, train_preds_reg)
rmse = np.sqrt(mean_squared_error(y_train_reg, train_preds_reg))
logger.info(f"XGBoost Regressor Training Completed! MAE: {mae:.2f} mins, RMSE: {rmse:.2f} mins")

reg_out = MODEL_DIR / "eta_xgboost.json"
reg_model.save_model(str(reg_out))
logger.info(f"Saved trained XGBoost regressor to {reg_out}")

reg_metadata = {
    "model_name": "XGBRegressor",
    "num_training_samples": len(X_train),
    "mae_minutes": float(mae),
    "rmse_minutes": float(rmse),
    "feature_names": feature_cols,
}
with open(MODEL_DIR / "eta_metadata.json", "w") as f:
    json.dump(reg_metadata, f, indent=2)

logger.info("🎉 All model artifacts (Classifier + Regressor) successfully generated!")
