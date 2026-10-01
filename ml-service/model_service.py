import json
import logging
from datetime import datetime
from pathlib import Path
import pandas as pd

logger = logging.getLogger(__name__)

# Safe import for XGBoost in case native C/C++ library dependencies (e.g. libomp) are missing on host system
try:
    import xgboost as xgb
    XGBOOST_AVAILABLE = True
except Exception as e:
    logger.warning(f"XGBoost native library not available ({e}). Falling back to parquet/heuristic pipeline.")
    XGBOOST_AVAILABLE = False


class ETAModelService:
    MODEL_VERSION = "hybrid-eta-v2-xgb-dual"

    def __init__(self, model_dir="models"):
        self.model_dir = Path(model_dir)

        # Load Parquet lookup table
        self.train_history = pd.read_parquet(self.model_dir / "train_history_lookup.parquet")
        with open(self.model_dir / "global_defaults.json") as f:
            self.global_defaults = json.load(f)

        self.train_history["train_number"] = self.train_history["train_number"].astype(str)
        self.train_history = self.train_history.set_index("train_number")

        # Attempt loading XGBoost trained classifier & regressor models
        self.xgb_model = None
        self.xgb_regressor = None
        self.xgb_metadata = None
        self.eta_metadata = None

        if XGBOOST_AVAILABLE:
            cls_path = self.model_dir / "delay_risk_xgboost.json"
            cls_meta_path = self.model_dir / "delay_risk_metadata.json"
            reg_path = self.model_dir / "eta_xgboost.json"
            reg_meta_path = self.model_dir / "eta_metadata.json"

            if cls_path.exists():
                try:
                    self.xgb_model = xgb.XGBClassifier()
                    self.xgb_model.load_model(str(cls_path))
                    logger.info("Successfully loaded XGBoost delay risk classifier: delay_risk_xgboost.json")
                except Exception as ex:
                    logger.warning(f"Failed to load XGBoost classifier model ({ex}). Using parquet lookups.")
                    self.xgb_model = None

            if cls_meta_path.exists():
                try:
                    with open(cls_meta_path) as mf:
                        self.xgb_metadata = json.load(mf)
                except Exception:
                    self.xgb_metadata = None

            if reg_path.exists():
                try:
                    self.xgb_regressor = xgb.XGBRegressor()
                    self.xgb_regressor.load_model(str(reg_path))
                    logger.info("Successfully loaded XGBoost ETA regressor: eta_xgboost.json")
                except Exception as ex:
                    logger.warning(f"Failed to load XGBoost regressor model ({ex}). Using hybrid fallback.")
                    self.xgb_regressor = None

            if reg_meta_path.exists():
                try:
                    with open(reg_meta_path) as rmf:
                        self.eta_metadata = json.load(rmf)
                except Exception:
                    self.eta_metadata = None

    def get_train_history(self, train_id: str) -> dict:
        train_id = str(train_id)

        if train_id in self.train_history.index:
            row = self.train_history.loc[train_id]
            return {
                "historical_delay_rate": self._safe_value(
                    row["historical_delay_rate"],
                    self.global_defaults["historical_delay_rate"],
                ),
                "historical_avg_delay_minutes": self._safe_value(
                    row["historical_avg_delay_minutes"],
                    self.global_defaults["historical_avg_delay_minutes"],
                ),
                "historical_avg_speed_kmph": self._safe_value(
                    row["historical_avg_speed_kmph"],
                    self.global_defaults["historical_avg_speed_kmph"],
                ),
                "historical_ontime_pct": self._safe_value(
                    row["historical_ontime_pct"],
                    self.global_defaults["historical_ontime_pct"],
                ),
            }

        return self.global_defaults.copy()

    @staticmethod
    def _safe_value(value, fallback):
        if pd.isna(value):
            return float(fallback)
        return float(value)

    @staticmethod
    def calculate_live_speed(current_speed_kmph, average_speed_last_5_minutes):
        speed = 0.40 * current_speed_kmph + 0.60 * average_speed_last_5_minutes
        return max(speed, 1.0)

    @staticmethod
    def calculate_speed_eta(distance_km, speed_kmph):
        return (distance_km / speed_kmph) * 60.0

    @staticmethod
    def calculate_rainfall_adjustment(rainfall_mm):
        if rainfall_mm is None:
            return 0.0
        if rainfall_mm < 2:
            return 0.0
        if rainfall_mm < 10:
            return 2.0
        if rainfall_mm < 30:
            return 5.0
        return 10.0

    @staticmethod
    def calculate_congestion_adjustment(congestion_score):
        if congestion_score is None:
            return 0.0
        return congestion_score * 15.0

    @staticmethod
    def calculate_delay_propagation(current_delay_minutes, delay_risk):
        if current_delay_minutes <= 0:
            return current_delay_minutes

        propagation_factor = 0.50 + 0.40 * delay_risk
        return current_delay_minutes * propagation_factor

    @staticmethod
    def calculate_prediction_interval(
        predicted_minutes,
        delay_risk,
        congestion_score,
    ):
        if congestion_score is None:
            congestion_score = 0.0

        uncertainty_factor = 0.08 + 0.12 * delay_risk + 0.08 * congestion_score
        uncertainty = predicted_minutes * uncertainty_factor

        lower_bound = max(0.0, predicted_minutes - uncertainty)
        upper_bound = predicted_minutes + uncertainty

        return lower_bound, upper_bound

    def predict(self, request: dict) -> dict:
        history = self.get_train_history(request["train_id"])
        delay_risk = history["historical_delay_rate"]

        scheduled_eta = request["scheduled_time_to_target_minutes"]
        historical_eta = request.get("historical_section_average_minutes")
        if historical_eta is None:
            historical_eta = scheduled_eta

        # Live speed weighted combination
        live_speed = self.calculate_live_speed(
            request["current_speed_kmph"],
            request["average_speed_last_5_minutes"],
        )

        speed_eta = self.calculate_speed_eta(
            request["distance_to_target_km"],
            live_speed,
        )

        base_eta = 0.40 * scheduled_eta + 0.25 * historical_eta + 0.35 * speed_eta

        # Extract departure hour and day of week from observed_at timestamp
        try:
            obs_raw = request.get("observed_at")
            if isinstance(obs_raw, str):
                obs_dt = datetime.fromisoformat(obs_raw.replace("Z", "+00:00"))
            else:
                obs_dt = datetime.now()
            dep_hour = float(obs_dt.hour)
            day_week = float(obs_dt.weekday())
        except Exception:
            dep_hour = 12.0
            day_week = 3.0

        # Determine delay adjustment: Use XGBRegressor with 10 Live Features if available
        if self.xgb_regressor is not None and self.eta_metadata is not None:
            try:
                feature_names = self.eta_metadata.get("feature_names", [
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
                ])

                feature_dict = {
                    "distance_to_target_km": float(request["distance_to_target_km"]),
                    "scheduled_time_to_target_minutes": float(request["scheduled_time_to_target_minutes"]),
                    "current_delay_minutes": float(request["current_delay_minutes"]),
                    "current_speed_kmph": float(request["current_speed_kmph"]),
                    "average_speed_last_5_minutes": float(request["average_speed_last_5_minutes"]),
                    "rainfall_mm": float(request.get("rainfall_mm") or 0.0),
                    "congestion_score": float(request.get("congestion_score") or 0.2),
                    "route_historical_ontime_pct": float(history.get("historical_ontime_pct", 85.0)),
                    "departure_hour": dep_hour,
                    "day_of_week": day_week,
                }

                input_df = pd.DataFrame([feature_dict])[feature_names]
                reg_prediction = float(self.xgb_regressor.predict(input_df)[0])
                delay_adjustment = max(0.0, reg_prediction)
            except Exception as ex:
                logger.warning(f"XGBRegressor prediction error ({ex}). Falling back to heuristic propagation.")
                delay_adjustment = self.calculate_delay_propagation(
                    request["current_delay_minutes"],
                    delay_risk,
                )
        else:
            delay_adjustment = self.calculate_delay_propagation(
                request["current_delay_minutes"],
                delay_risk,
            )

        rainfall_adjustment = self.calculate_rainfall_adjustment(request.get("rainfall_mm"))
        congestion_adjustment = self.calculate_congestion_adjustment(request.get("congestion_score"))

        predicted_minutes = max(
            0.0,
            base_eta + delay_adjustment + rainfall_adjustment + congestion_adjustment,
        )

        lower_bound, upper_bound = self.calculate_prediction_interval(
            predicted_minutes,
            delay_risk,
            request.get("congestion_score"),
        )

        uncertainty_span = upper_bound - lower_bound
        rel_uncertainty = uncertainty_span / max(predicted_minutes, 1.0)
        confidence_score = round(max(0.50, min(0.98, 1.0 - 0.40 * rel_uncertainty)), 2)

        return {
            "train_id": request["train_id"],
            "target_station_id": request["target_station_id"],
            "predicted_remaining_minutes": round(predicted_minutes, 2),
            "lower_bound_minutes": round(lower_bound, 2),
            "upper_bound_minutes": round(upper_bound, 2),
            "confidence_score": confidence_score,
            "model_version": self.MODEL_VERSION,
        }

