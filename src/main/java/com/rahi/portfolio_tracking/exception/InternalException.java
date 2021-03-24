package com.rahi.portfolio_tracking.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class InternalException extends PortfolioTrackingException {

    public InternalException(HttpStatus status) {
        super(status);
    }

    public InternalException(HttpStatus status, String msg) {
        super(status, msg);
    }

    public InternalException(HttpStatus status, String msg, Object additionalData) {
        super(status, msg, additionalData);
    }

    public InternalException(HttpStatus status, Throwable ex) {
        super(status, ex);
    }

    public InternalException(HttpStatus status, String message, Throwable ex) {
        super(status, message, ex);
    }

}
