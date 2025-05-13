package id.ac.ui.cs.advprog.berating.exception;

import java.util.UUID;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(UUID reviewId) {
        super(String.format("Review with ID %s not found. This could be because the review doesn't exist or you don't have access to it.", reviewId));
    }

    public ReviewNotFoundException(String message) {
        super(message);
    }

    public ReviewNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 