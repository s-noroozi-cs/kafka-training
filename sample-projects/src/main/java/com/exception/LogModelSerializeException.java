package com.exception;

public class LogModelSerializeException extends RuntimeException{
    public LogModelSerializeException() {
        super();
    }

    public LogModelSerializeException(String message) {
        super(message);
    }

    public LogModelSerializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogModelSerializeException(Throwable cause) {
        super(cause);
    }

    protected LogModelSerializeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
