package com.think.exception;

public class ThinkException extends Exception {
    static final long serialVersionUID = -6387116903124220947L;

    public ThinkException() {
    }

    public ThinkException(String message) {
        super(message);
    }

    public ThinkException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThinkException(Throwable cause) {
        super(cause);
    }

    public ThinkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}