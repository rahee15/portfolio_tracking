package com.rahi.portfolio_tracking.exception;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends PortfolioTrackingException {
    public InvalidRequestException(HttpStatus status) {
        super(status);
    }

    public InvalidRequestException(HttpStatus status, String msg) {
        super(status, msg);
    }

    public InvalidRequestException(HttpStatus status, String msg, Object additionalData) {
        super(status, msg, additionalData);
    }

    public InvalidRequestException(HttpStatus status, Throwable ex) {
        super(status, ex);
    }

    public InvalidRequestException(HttpStatus status, String message, Throwable ex) {
        super(status, message, ex);
    }
}
