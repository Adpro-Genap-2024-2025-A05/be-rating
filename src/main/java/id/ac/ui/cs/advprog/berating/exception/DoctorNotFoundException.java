package id.ac.ui.cs.advprog.berating.exception;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(String id) {
        super("Doctor not found with id: " + id);
    }
} 