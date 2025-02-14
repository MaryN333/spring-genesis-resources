package cz.wz.marysidy.spring_genesis_resources.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException ex, WebRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode("VALIDATION_ERROR")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode("NOT_FOUND")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();
    }

    @ExceptionHandler(PartialSuccessException.class)
    @ResponseStatus(HttpStatus.MULTI_STATUS)
    public ErrorResponse handlePartialSuccessException(PartialSuccessException ex, WebRequest request) {
        log.warn("Partial success: {}", ex.getMessage());
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode("PARTIAL_SUCCESS")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred", ex);
        return ErrorResponse.builder()
                .message("An unexpected error occurred. Please try again later.")
                .errorCode("INTERNAL_ERROR")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .details(Map.of("exception", ex.getClass().getSimpleName()))
                .build();
    }
}
