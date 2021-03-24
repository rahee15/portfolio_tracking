package com.rahi.portfolio_tracking.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class PortfolioTrackingException extends RuntimeException {

    private HttpStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    private String debugMessage;

    private Object additionalData;

    private PortfolioTrackingException() {
        timestamp = LocalDateTime.now();
    }

    PortfolioTrackingException(HttpStatus status) {
        this();
        this.status = status;
    }

    public PortfolioTrackingException(HttpStatus status, String msg) {
        super(msg);
        timestamp = LocalDateTime.now();
        this.status = status;
    }

    public PortfolioTrackingException(HttpStatus status, String msg, Object additionalData) {
        this(status, msg);
        this.additionalData = additionalData;
    }

    PortfolioTrackingException(HttpStatus status, Throwable ex) {
        this(status, "Unexpected error", ex);
    }

    PortfolioTrackingException(HttpStatus status, String message, Throwable ex) {
        this(status, message);
        this.debugMessage = ex.getLocalizedMessage();
    }
}
