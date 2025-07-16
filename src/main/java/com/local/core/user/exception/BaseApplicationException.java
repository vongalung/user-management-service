package com.local.core.user.exception;

public abstract class BaseApplicationException extends RuntimeException {
    public BaseApplicationException() {
    }

    public BaseApplicationException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
