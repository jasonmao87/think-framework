package com.think.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThinkNotSupportException extends Exception{

    public ThinkNotSupportException(String message) {
        super(message);
        log.error("ThinkNotSupportException INFO ",this);
    }

    public ThinkNotSupportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThinkNotSupportException() {
    }

    public ThinkNotSupportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        log.error("ThinkNotSupportException INFO ",this);
    }

    public ThinkNotSupportException(Throwable cause) {
        super(cause);
    }
}
