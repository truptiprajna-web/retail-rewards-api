package com.retailer.rewards.exception;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Standard error body returned by the REST API.
 */
@Getter
public class ApiError {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    /**
     * Creates an API error response.
     *
     * @param status HTTP status number
     * @param error HTTP status description
     * @param message useful explanation of the failure
     * @param path requested API path
     */
    public ApiError(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
