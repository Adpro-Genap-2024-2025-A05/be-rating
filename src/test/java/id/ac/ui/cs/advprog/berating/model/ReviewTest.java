package id.ac.ui.cs.advprog.berating.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.UUID;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import id.ac.ui.cs.advprog.berating.enums.ReviewStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
public class ReviewTest {
    
    private UUID id;
    private UUID patientId;
    private UUID doctorId;
    private UUID consultationId;
    private Integer rating;
    private String comment;
    private ReviewStatus status;
    private Date createdAt;
    private Date updatedAt;
    private Validator validator;

    @Mock
    private Date mockDate;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        consultationId = UUID.randomUUID();
        rating = 5;
        comment = "Great service!";
        status = ReviewStatus.PENDING;
        createdAt = new Date();
        updatedAt = new Date();
        
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNoArgsConstructor() {
        Review review = new Review();
        assertNotNull(review);
        assertNull(review.getId());
        assertNull(review.getPatientId());
        assertNull(review.getDoctorId());
        assertNull(review.getConsultationId());
        assertNull(review.getRating());
        assertNull(review.getComment());
        assertNull(review.getStatus());
        assertNull(review.getCreatedAt());
        assertNull(review.getUpdatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        Review review = new Review(id, patientId, doctorId, consultationId, rating, comment, status, createdAt, updatedAt);
        
        assertEquals(id, review.getId());
        assertEquals(patientId, review.getPatientId());
        assertEquals(doctorId, review.getDoctorId());
        assertEquals(consultationId, review.getConsultationId());
        assertEquals(rating, review.getRating());
        assertEquals(comment, review.getComment());
        assertEquals(status, review.getStatus());
        assertEquals(createdAt, review.getCreatedAt());
        assertEquals(updatedAt, review.getUpdatedAt());
    }

    @Test
    void testBuilder() {
        Review review = Review.builder()
                .id(id)
                .patientId(patientId)
                .doctorId(doctorId)
                .consultationId(consultationId)
                .rating(rating)
                .comment(comment)
                .status(status)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertEquals(id, review.getId());
        assertEquals(patientId, review.getPatientId());
        assertEquals(doctorId, review.getDoctorId());
        assertEquals(consultationId, review.getConsultationId());
        assertEquals(rating, review.getRating());
        assertEquals(comment, review.getComment());
        assertEquals(status, review.getStatus());
        assertEquals(createdAt, review.getCreatedAt());
        assertEquals(updatedAt, review.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        Review review = new Review();
        
        review.setId(id);
        review.setPatientId(patientId);
        review.setDoctorId(doctorId);
        review.setConsultationId(consultationId);
        review.setRating(rating);
        review.setComment(comment);
        review.setStatus(status);
        review.setCreatedAt(createdAt);
        review.setUpdatedAt(updatedAt);

        assertEquals(id, review.getId());
        assertEquals(patientId, review.getPatientId());
        assertEquals(doctorId, review.getDoctorId());
        assertEquals(consultationId, review.getConsultationId());
        assertEquals(rating, review.getRating());
        assertEquals(comment, review.getComment());
        assertEquals(status, review.getStatus());
        assertEquals(createdAt, review.getCreatedAt());
        assertEquals(updatedAt, review.getUpdatedAt());
    }

    @Test
    void testPrePersist() {
        Review review = new Review();
        review.onCreate();
        
        assertNotNull(review.getId());
        assertTrue(review.getId() instanceof UUID);
    }

    @Test
    void testPrePersistWithExistingId() {
        Review review = new Review();
        review.setId(id);
        review.onCreate();
        
        assertEquals(id, review.getId());
    }

    @Test
    void testEqualsAndHashCode() {
        Review review1 = new Review(id, patientId, doctorId, consultationId, rating, comment, status, createdAt, updatedAt);
        Review review2 = new Review(id, patientId, doctorId, consultationId, rating, comment, status, createdAt, updatedAt);
        
        assertNotEquals(review1, review2);
        assertNotEquals(review1.hashCode(), review2.hashCode());
    }

    @Test
    void testToString() {
        Review review = new Review(id, patientId, doctorId, consultationId, rating, comment, status, createdAt, updatedAt);
        String toString = review.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("Review"));
    }

    @Test
    void testValidRating() {
        Review review = new Review();
        review.setRating(3);
        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidRatingTooLow() {
        Review review = new Review();
        review.setRating(0);
        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        assertFalse(violations.isEmpty());
        assertEquals("Rating must be at least 1", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidRatingTooHigh() {
        Review review = new Review();
        review.setRating(6);
        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        assertFalse(violations.isEmpty());
        assertEquals("Rating must be at most 5", violations.iterator().next().getMessage());
    }
}
