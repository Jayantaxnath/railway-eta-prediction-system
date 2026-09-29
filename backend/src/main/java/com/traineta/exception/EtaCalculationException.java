package com.traineta.exception;

public class EtaCalculationException extends RuntimeException {

    public EtaCalculationException(String message) {
        super(message);
    }

    public EtaCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
