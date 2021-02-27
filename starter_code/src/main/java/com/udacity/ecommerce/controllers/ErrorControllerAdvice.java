package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.exceptions.*;
import org.slf4j.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class ErrorControllerAdvice {
    private final Logger logger = LoggerFactory.getLogger("splunk.logger");

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Object> handleUserNotFoundException() {
        this.logger.warn("userEvent:userNotFound");
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InvalidPasswordException.class)
    protected ResponseEntity<Object> handleInvalidPasswordException() {
        this.logger.warn("userEvent:invalidPassword");
        return ResponseEntity.badRequest().build();
    }
}
