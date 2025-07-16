package com.local.core.user.exception;

public class InternalServerErrorException extends BaseApplicationException {
    public InternalServerErrorException() {
    }

    public InternalServerErrorException(Throwable cause) {
        super(cause);
    }
}
