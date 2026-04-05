package com.sagar.MovieBookingSystem.Exception;

/**
 * Custom exception for booking-related errors
 */
public class BookingException extends RuntimeException {

    private String errorCode;
    private int statusCode;

    public BookingException(String message) {
        super(message);
        this.statusCode = 400;
    }

    public BookingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = 400;
    }

    public BookingException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public BookingException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 400;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

