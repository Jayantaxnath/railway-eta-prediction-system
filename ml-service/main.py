from contextlib import asynccontextmanager
from pathlib import Path
import uvicorn
from fastapi import FastAPI

from model_service import ETAModelService
from schemas import ETAPredictionRequest, ETAPredictionResult


@asynccontextmanager
async def lifespan(app: FastAPI):
    app.state.model_service = ETAModelService(model_dir=Path(__file__).parent / "models")
    yield


app = FastAPI(
    title="Railway ETA Prediction Service",
    version="1.0.0",
    lifespan=lifespan,
)


@app.get("/")
def home():
    return {"home": "ready to serve", 'goto':'base_url/docs'}


@app.get("/health")
def health():
    return {
        "status": "healthy",
        "model_version": ETAModelService.MODEL_VERSION,
    }


@app.post(
    "/predict-eta",
    response_model=ETAPredictionResult,
)
def predict_eta(request: ETAPredictionRequest):
    request_data = request.model_dump(mode="json")
    service = app.state.model_service

    prediction = service.predict(request_data)
    return prediction

if __name__ == "__main__":
    print("\n[Open][local] in Browser: http://127.0.0.1:8000 or http://localhost:8000")
    uvicorn.run(
        "main:app",  # python file name
        host="0.0.0.0", # local
        port=8000, # fasapi port
        reload=True,)