package id.ac.ui.cs.advprog.berating.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ReviewRequestTest {
    
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testNoArgsConstructor() {
        ReviewRequest request = new ReviewRequest();
        assertNotNull(request);
        assertNull(request.getPatientId());
        assertNull(request.getDoctorId());
        assertNull(request.getRating());
        assertNull(request.getComment());
    }

    @Test
    void testAllArgsConstructor() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest(patientId, doctorId, 5, "Great service!");

        assertEquals(patientId, request.getPatientId());
        assertEquals(doctorId, request.getDoctorId());
        assertEquals(5, request.getRating());
        assertEquals("Great service!", request.getComment());
    }

    @Test
    void testSettersAndGetters() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest();
        
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setRating(5);
        request.setComment("Great service!");

        assertEquals(patientId, request.getPatientId());
        assertEquals(doctorId, request.getDoctorId());
        assertEquals(5, request.getRating());
        assertEquals("Great service!", request.getComment());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        ReviewRequest request1 = new ReviewRequest(patientId, doctorId, 5, "Great service!");
        ReviewRequest request2 = new ReviewRequest(patientId, doctorId, 5, "Great service!");
        ReviewRequest request3 = new ReviewRequest(UUID.randomUUID(), UUID.randomUUID(), 4, "Good service");

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void testToString() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest(patientId, doctorId, 5, "Great service!");
        
        String toString = request.toString();
        assertTrue(toString.contains(patientId.toString()));
        assertTrue(toString.contains(doctorId.toString()));
        assertTrue(toString.contains("5"));
        assertTrue(toString.contains("Great service!"));
    }

    @Test
    void testValidRequest() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest(patientId, doctorId, 5, "Great service!");

        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullPatientId() {
        UUID doctorId = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest(null, doctorId, 5, "Great service!");

        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Patient ID cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    void testNullDoctorId() {
        UUID patientId = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest(patientId, null, 5, "Great service!");

        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Doctor ID cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    void testNullRating() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest(patientId, doctorId, null, "Great service!");

        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Rating cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidRatingTooLow() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest(patientId, doctorId, 0, "Great service!");

        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testInvalidRatingTooHigh() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest(patientId, doctorId, 6, "Great service!");

        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testCommentTooLong() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        String longComment = "a".repeat(501);
        ReviewRequest request = new ReviewRequest(patientId, doctorId, 5, longComment);

        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Comment cannot be more than 500 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testNullComment() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest(patientId, doctorId, 5, null);

        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
