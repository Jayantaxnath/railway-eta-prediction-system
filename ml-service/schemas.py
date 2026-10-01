from datetime import datetime
from typing import Optional

from pydantic import BaseModel, ConfigDict, Field


class ETAPredictionRequest(BaseModel):
    model_config = ConfigDict(extra="forbid")

    train_id: str
    target_station_id: str
    observed_at: datetime

    current_speed_kmph: float = Field(ge=0)
    average_speed_last_5_minutes: float = Field(ge=0)
    current_delay_minutes: float

    distance_to_target_km: float = Field(ge=0)
    scheduled_time_to_target_minutes: float = Field(ge=0)

    rainfall_mm: Optional[float] = Field(default=None, ge=0)
    congestion_score: Optional[float] = Field(default=None, ge=0, le=1)
    historical_section_average_minutes: Optional[float] = Field(
        default=None,
        ge=0,
    )


class ETAPredictionResult(BaseModel):
    train_id: str
    target_station_id: str

    predicted_remaining_minutes: float = Field(ge=0)
    lower_bound_minutes: Optional[float] = Field(default=None, ge=0)
    upper_bound_minutes: Optional[float] = Field(default=None, ge=0)
    confidence_score: Optional[float] = Field(default=0.92, ge=0, le=1)

    model_version: str
