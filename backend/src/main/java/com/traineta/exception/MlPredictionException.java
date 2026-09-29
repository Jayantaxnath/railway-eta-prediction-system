package com.traineta.exception;

public class MlPredictionException extends RuntimeException {

    public MlPredictionException(String message) {
        super(message);
    }

    public MlPredictionException(String message, Throwable cause) {
        super(message, cause);
    }
}
