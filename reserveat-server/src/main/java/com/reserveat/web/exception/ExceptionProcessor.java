package com.reserveat.web.exception;

import com.reserveat.domain.exception.LocationNotFoundException;
import com.reserveat.domain.exception.PhotoNotFoundException;
import com.reserveat.domain.exception.RestaurantNotFoundException;
import com.reserveat.web.model.ErrorDto;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionProcessor extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionProcessor.class);

    @ExceptionHandler({
        RestaurantNotFoundException.class,
        LocationNotFoundException.class,
        PhotoNotFoundException.class,
        ResourceNotFoundException.class
    })
    public ResponseEntity<ErrorDto> handleRestaurantNotFoundException(RuntimeException e) {
        ErrorDto errorDto = new ErrorDto()
            .code(HttpStatus.NOT_FOUND.name())
            .message(HttpStatus.NOT_FOUND.getReasonPhrase());
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
        ConstraintViolationException.class,
        ReservationValidationException.class
    })
    public ResponseEntity<ErrorDto> handleBadRequest() {
        ErrorDto errorDto = new ErrorDto()
            .code(HttpStatus.BAD_REQUEST.name())
            .message(HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReservationConflictException.class)
    public ResponseEntity<ErrorDto> handleReservationConflictException(RuntimeException e) {
        ErrorDto errorDto = new ErrorDto()
            .code(HttpStatus.CONFLICT.name())
            .message(HttpStatus.CONFLICT.getReasonPhrase());
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
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

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        @NonNull MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request
    ) {
        ErrorDto errorDto = new ErrorDto()
            .code(HttpStatus.BAD_REQUEST.name())
            .message(HttpStatus.BAD_REQUEST.getReasonPhrase());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }


}
