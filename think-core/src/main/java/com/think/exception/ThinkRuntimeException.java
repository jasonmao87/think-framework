package com.think.exception;

public class ThinkRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -1589927027417132202L;

    public ThinkRuntimeException() {
    }

    public ThinkRuntimeException(String message) {
        super(message);
    }

    public ThinkRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThinkRuntimeException(Throwable cause) {
        super(cause);
    }

    public ThinkRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
