# Project summary and common questions

Notes for explaining the ML side of RailX. Details are in [MODEL_PIPELINE.md](MODEL_PIPELINE.md).

## In 30 seconds

RailX predicts how long a running train needs to reach a station. The backend collects the
train's live state (speed, delay, distance left, schedule) plus weather and congestion, and
sends it to a separate ML service. The ML service blends the scheduled time, a historical
estimate and a speed-based estimate, adds a delay predicted by an XGBoost model and some
weather and congestion adjustments, and returns the remaining minutes with a range and a
confidence score.

## In 2 minutes

Training and prediction are separate. Training happens offline: `train_full_model.py` reads
1.5 million historical journeys, builds a per-train history table and trains two XGBoost
models, which are saved as files.

The ML service loads those files once at startup. For each request it validates the input
with Pydantic, looks up the train's history, computes a base ETA from the timetable, history
and current speed, and adds a delay predicted by the XGBoost regressor. Rain and congestion
add a few more minutes. The range around the prediction gets wider when the train is often
late or the line is congested.

The backend calls the ML service over HTTP. If the service is down or slow, the backend uses
a simpler built-in formula, so the app keeps working.

We tested the API with 10 scripted cases, including a stopped train, a train at its station,
missing optional data and three invalid requests, which correctly return HTTP 422.

## Questions

**Why machine learning instead of just the timetable?**
Delays depend on several factors at once, such as current delay, speed and conditions. A
model can learn how those combine, where a fixed timetable offset cannot.

**Why XGBoost?**
It works well on tabular data, trains quickly on large datasets, and predicts in
milliseconds, which suits a live API.

**Why are there two models?**
The plan was one model for how much delay to expect and one for the chance of being late. In
the current code only the delay regressor is used. The "delay risk" comes from each train's
historical delay rate instead of the classifier.

**Are the models retrained for every prediction?**
No. They are trained once offline and loaded when the service starts.

**What happens when optional data is missing?**
Rainfall counts as 0, the model uses a congestion score of 0.2, and the historical section
time falls back to the scheduled time. A train missing from the history table uses averages
over all trains.

**How is bad input kept away from the model?**
The request schema rejects negative speeds or distances, congestion outside 0 to 1, missing
required fields and unknown fields. The API answers HTTP 422 with the reason.

**What if the ML service goes down?**
The backend waits up to 10 seconds, then uses its fallback formula and marks the prediction
`FALLBACK_MOCK`.

**How accurate is it?**
Not yet measured properly. The training script gave the model the answer as one of its
inputs and scored it on its own training data, so the saved scores look perfect but mean
nothing. [PERFORMANCE.md](../PERFORMANCE.md) shows the evidence and the steps to a real
evaluation. Say this plainly if asked. It is better than quoting the saved numbers.
