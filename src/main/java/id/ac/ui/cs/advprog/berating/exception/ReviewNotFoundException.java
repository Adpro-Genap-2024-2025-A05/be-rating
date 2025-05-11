package id.ac.ui.cs.advprog.berating.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(String id) {
        super("Review not found with id: " + id);
    }
} 