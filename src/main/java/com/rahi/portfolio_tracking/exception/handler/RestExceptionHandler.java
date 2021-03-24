package com.rahi.portfolio_tracking.exception.handler;

import com.rahi.portfolio_tracking.exception.PortfolioTrackingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PortfolioTrackingException.class)
    protected ResponseEntity<Object> handleEWSException(
            PortfolioTrackingException ex) {
        log.error(
                String.join(
                        ",",
                        ex.getStatus().toString(),
                        ex.getMessage() != null ? ex.getMessage() : "-",
                        ex.getDebugMessage() != null ? ex.getDebugMessage() : "-"
                ),
                ex
        );
        return buildResponseEntity(ex);
    }

    private ResponseEntity<Object> buildResponseEntity(PortfolioTrackingException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
    }
}
