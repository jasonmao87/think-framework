package com.think.exception;

public class ThinkNotSupportException extends Exception{

    public ThinkNotSupportException(String message) {
        super(message);
    }

    public ThinkNotSupportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThinkNotSupportException() {
    }

    public ThinkNotSupportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ThinkNotSupportException(Throwable cause) {
        super(cause);
    }
}
