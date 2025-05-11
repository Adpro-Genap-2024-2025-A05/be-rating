package id.ac.ui.cs.advprog.berating.exception;

public class ConsultationHistoryNotFoundException extends RuntimeException {
    public ConsultationHistoryNotFoundException(String id) {
        super("Consultation history not found with id: " + id);
    }
} 