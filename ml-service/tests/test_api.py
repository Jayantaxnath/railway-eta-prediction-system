import json
from datetime import datetime
from pathlib import Path

import requests

# Change this if your prediction endpoint has a different path
BASE_URL = "http://127.0.0.1:8000"
PREDICTION_ENDPOINT = f"{BASE_URL}/predict-eta"

RESULTS_FILE = Path(__file__).parent / "test_results.json"


test_cases = [
    {
        "name": "1_normal_running_train",
        "expected_status": 200,
        "payload": {
            "train_id": "12345",
            "target_station_id": "GHY",
            "observed_at": "2026-09-04T19:30:00+05:30",
            "current_speed_kmph": 65.0,
            "average_speed_last_5_minutes": 60.0,
            "current_delay_minutes": 15.0,
            "distance_to_target_km": 120.0,
            "scheduled_time_to_target_minutes": 110.0,
            "rainfall_mm": 0.0,
            "congestion_score": 0.3,
            "historical_section_average_minutes": 125.0,
        },
    },
    {
        "name": "2_almost_at_station",
        "expected_status": 200,
        "payload": {
            "train_id": "12345",
            "target_station_id": "GHY",
            "observed_at": "2026-09-04T19:35:00+05:30",
            "current_speed_kmph": 12.0,
            "average_speed_last_5_minutes": 18.0,
            "current_delay_minutes": 5.0,
            "distance_to_target_km": 0.2,
            "scheduled_time_to_target_minutes": 3.0,
            "rainfall_mm": 0.0,
            "congestion_score": 0.2,
            "historical_section_average_minutes": 5.0,
        },
    },
    {
        "name": "3_train_stopped",
        "expected_status": 200,
        "payload": {
            "train_id": "12345",
            "target_station_id": "GHY",
            "observed_at": "2026-09-04T19:40:00+05:30",
            "current_speed_kmph": 0.0,
            "average_speed_last_5_minutes": 0.0,
            "current_delay_minutes": 45.0,
            "distance_to_target_km": 50.0,
            "scheduled_time_to_target_minutes": 40.0,
            "rainfall_mm": 5.0,
            "congestion_score": 0.9,
            "historical_section_average_minutes": 65.0,
        },
    },
    {
        "name": "4_optional_fields_null",
        "expected_status": 200,
        "payload": {
            "train_id": "12345",
            "target_station_id": "GHY",
            "observed_at": "2026-09-04T19:45:00+05:30",
            "current_speed_kmph": 50.0,
            "average_speed_last_5_minutes": 48.0,
            "current_delay_minutes": 10.0,
            "distance_to_target_km": 80.0,
            "scheduled_time_to_target_minutes": 90.0,
            "rainfall_mm": None,
            "congestion_score": None,
            "historical_section_average_minutes": None,
        },
    },
    {
        "name": "5_extreme_conditions",
        "expected_status": 200,
        "payload": {
            "train_id": "99999",
            "target_station_id": "GHY",
            "observed_at": "2026-09-04T20:00:00+05:30",
            "current_speed_kmph": 8.0,
            "average_speed_last_5_minutes": 12.0,
            "current_delay_minutes": 180.0,
            "distance_to_target_km": 150.0,
            "scheduled_time_to_target_minutes": 120.0,
            "rainfall_mm": 120.0,
            "congestion_score": 1.0,
            "historical_section_average_minutes": 240.0,
        },
    },
    {
        "name": "6_ahead_of_schedule",
        "expected_status": 200,
        "payload": {
            "train_id": "54321",
            "target_station_id": "GHY",
            "observed_at": "2026-09-04T20:10:00+05:30",
            "current_speed_kmph": 80.0,
            "average_speed_last_5_minutes": 78.0,
            "current_delay_minutes": -10.0,
            "distance_to_target_km": 100.0,
            "scheduled_time_to_target_minutes": 90.0,
            "rainfall_mm": 0.0,
            "congestion_score": 0.1,
            "historical_section_average_minutes": 95.0,
        },
    },
    {
        "name": "7_invalid_negative_speed",
        "expected_status": 422,
        "payload": {
            "train_id": "12345",
            "target_station_id": "GHY",
            "observed_at": "2026-09-04T20:15:00+05:30",
            "current_speed_kmph": -20.0,
            "average_speed_last_5_minutes": 40.0,
            "current_delay_minutes": 10.0,
            "distance_to_target_km": 50.0,
            "scheduled_time_to_target_minutes": 60.0,
        },
    },
    {
        "name": "8_invalid_congestion_over_one",
        "expected_status": 422,
        "payload": {
            "train_id": "12345",
            "target_station_id": "GHY",
            "observed_at": "2026-09-04T20:20:00+05:30",
            "current_speed_kmph": 50.0,
            "average_speed_last_5_minutes": 45.0,
            "current_delay_minutes": 10.0,
            "distance_to_target_km": 50.0,
            "scheduled_time_to_target_minutes": 60.0,
            "rainfall_mm": 0.0,
            "congestion_score": 1.5,
            "historical_section_average_minutes": 70.0,
        },
    },
    {
        "name": "9_missing_required_field",
        "expected_status": 422,
        "payload": {
            "train_id": "12345",
            "target_station_id": "GHY",
            "observed_at": "2026-09-04T20:25:00+05:30",
            "current_speed_kmph": 50.0,
            "average_speed_last_5_minutes": 45.0,
            "current_delay_minutes": 10.0,
            "scheduled_time_to_target_minutes": 60.0,
        },
    },
    {
        "name": "10_zero_distance",
        "expected_status": 200,
        "payload": {
            "train_id": "12345",
            "target_station_id": "GHY",
            "observed_at": "2026-09-04T20:30:00+05:30",
            "current_speed_kmph": 0.0,
            "average_speed_last_5_minutes": 10.0,
            "current_delay_minutes": 5.0,
            "distance_to_target_km": 0.0,
            "scheduled_time_to_target_minutes": 0.0,
            "rainfall_mm": 0.0,
            "congestion_score": 0.0,
            "historical_section_average_minutes": 0.0,
        },
    },
]


results = []

print("=" * 70)
print("STARTING API TESTS")
print("=" * 70)

for test in test_cases:
    print(f"\nRunning: {test['name']}")

    started_at = datetime.now().isoformat()

    try:
        response = requests.post(
            PREDICTION_ENDPOINT,
            json=test["payload"],
            timeout=30,
        )

        try:
            response_body = response.json()
        except ValueError:
            response_body = response.text

        passed = response.status_code == test["expected_status"]

        result = {
            "test_name": test["name"],
            "timestamp": started_at,
            "expected_status": test["expected_status"],
            "actual_status": response.status_code,
            "passed": passed,
            "response": response_body,
        }

        if passed:
            print(f"PASS | Status: {response.status_code}")
        else:
            print(
                f"FAIL | Expected: {test['expected_status']} "
                f"| Got: {response.status_code}"
            )

        print("Response:")
        print(json.dumps(response_body, indent=2, default=str))

    except requests.exceptions.RequestException as error:
        result = {
            "test_name": test["name"],
            "timestamp": started_at,
            "expected_status": test["expected_status"],
            "actual_status": None,
            "passed": False,
            "response": None,
            "error": str(error),
        }

        print(f"ERROR | {error}")

    results.append(result)


# Save detailed results
with open(RESULTS_FILE, "w", encoding="utf-8") as file:
    json.dump(results, file, indent=2, default=str)


# Summary
total = len(results)
passed = sum(result["passed"] for result in results)
failed = total - passed

print("\n" + "=" * 70)
print("TEST SUMMARY")
print("=" * 70)
print(f"Total:  {total}")
print(f"Passed: {passed}")
print(f"Failed: {failed}")
print(f"\nDetailed results saved to: {RESULTS_FILE}")

if failed > 0:
    raise SystemExit(1)