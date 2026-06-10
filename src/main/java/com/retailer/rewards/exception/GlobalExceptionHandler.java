package com.retailer.rewards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * Converts application and validation exceptions into consistent API errors.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles requests for customer IDs that do not exist.
     *
     * @param exception customer lookup failure
     * @param request current HTTP request
     * @return HTTP 404 error response
     */
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiError> handleCustomerNotFound(
            CustomerNotFoundException exception, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    /**
     * Handles numeric validation failures such as a non-positive customer ID.
     *
     * @param exception validation failure
     * @param request current HTTP request
     * @return HTTP 400 error response
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request) {
        String message = exception.getConstraintViolations().iterator()
                .next().getMessage();
        return buildError(HttpStatus.BAD_REQUEST, message, request);
    }

    /**
     * Handles path values that cannot be converted to a customer ID.
     *
     * @param exception path conversion failure
     * @param request current HTTP request
     * @return HTTP 400 error response
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Customer id must be a number",
                request);
    }

    private ResponseEntity<ApiError> buildError(
            HttpStatus status, String message, HttpServletRequest request) {
        ApiError error = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }
}
