package com.reserveat.web.exception;

import com.reserveat.domain.exception.LocationNotFoundException;
import com.reserveat.domain.exception.RestaurantNotFoundException;
import com.reserveat.web.model.ErrorDto;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionProcessor.class);

    @ExceptionHandler({
        RestaurantNotFoundException.class,
        LocationNotFoundException.class
    })
    public ResponseEntity<ErrorDto> handleRestaurantNotFoundException() {
        ErrorDto errorDto = new ErrorDto()
            .code(HttpStatus.NOT_FOUND.name())
            .message(HttpStatus.NOT_FOUND.getReasonPhrase());
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleConstraintViolationException(ConstraintViolationException ignored) {
        ErrorDto errorDto = new ErrorDto()
            .code(HttpStatus.BAD_REQUEST.name())
            .message(HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    //generic handler for all non-handled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(RestaurantNotFoundException e) {
        logger.error("Unhandled exception", e);
        ErrorDto errorDto = new ErrorDto()
            .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
            .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
