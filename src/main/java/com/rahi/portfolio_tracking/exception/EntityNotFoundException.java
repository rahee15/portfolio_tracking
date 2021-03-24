package com.rahi.portfolio_tracking.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class EntityNotFoundException extends PortfolioTrackingException {

    public EntityNotFoundException(HttpStatus status) {
        super(status);
    }

    public EntityNotFoundException(HttpStatus status, String msg) {
        super(status, msg);
    }

    public EntityNotFoundException(HttpStatus status, String msg, Object additionalData) {
        super(status, msg, additionalData);
    }

    public EntityNotFoundException(HttpStatus status, Throwable ex) {
        super(status, ex);
    }

    public EntityNotFoundException(HttpStatus status, String message, Throwable ex) {
        super(status, message, ex);
    }

}
