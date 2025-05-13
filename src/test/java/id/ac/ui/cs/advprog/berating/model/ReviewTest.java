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
    private Date createdAt;
    private Date updatedAt;
    private Integer version;
    private UUID parentId;
    private Boolean isCurrentVersion;
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
        createdAt = new Date();
        updatedAt = new Date();
        version = 1;
        parentId = id; // For first version, parentId is same as id
        isCurrentVersion = true;
        
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
        assertNull(review.getCreatedAt());
        assertNull(review.getUpdatedAt());
        assertNull(review.getVersion());
        assertNull(review.getParentId());
        assertNull(review.getIsCurrentVersion());
    }

    @Test
    void testAllArgsConstructor() {
        Review review = new Review(id, patientId, doctorId, consultationId, version, parentId, 
            isCurrentVersion, rating, comment, createdAt, updatedAt);
        
        assertEquals(id, review.getId());
        assertEquals(patientId, review.getPatientId());
        assertEquals(doctorId, review.getDoctorId());
        assertEquals(consultationId, review.getConsultationId());
        assertEquals(version, review.getVersion());
        assertEquals(parentId, review.getParentId());
        assertEquals(isCurrentVersion, review.getIsCurrentVersion());
        assertEquals(rating, review.getRating());
        assertEquals(comment, review.getComment());
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
                .version(version)
                .parentId(parentId)
                .isCurrentVersion(isCurrentVersion)
                .rating(rating)
                .comment(comment)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertEquals(id, review.getId());
        assertEquals(patientId, review.getPatientId());
        assertEquals(doctorId, review.getDoctorId());
        assertEquals(consultationId, review.getConsultationId());
        assertEquals(version, review.getVersion());
        assertEquals(parentId, review.getParentId());
        assertEquals(isCurrentVersion, review.getIsCurrentVersion());
        assertEquals(rating, review.getRating());
        assertEquals(comment, review.getComment());
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
        review.setVersion(version);
        review.setParentId(parentId);
        review.setIsCurrentVersion(isCurrentVersion);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(createdAt);
        review.setUpdatedAt(updatedAt);

        assertEquals(id, review.getId());
        assertEquals(patientId, review.getPatientId());
        assertEquals(doctorId, review.getDoctorId());
        assertEquals(consultationId, review.getConsultationId());
        assertEquals(version, review.getVersion());
        assertEquals(parentId, review.getParentId());
        assertEquals(isCurrentVersion, review.getIsCurrentVersion());
        assertEquals(rating, review.getRating());
        assertEquals(comment, review.getComment());
        assertEquals(createdAt, review.getCreatedAt());
        assertEquals(updatedAt, review.getUpdatedAt());
    }

    @Test
    void testPrePersist() {
        Review review = new Review();
        review.onCreate();
        
        assertNotNull(review.getId());
        assertTrue(review.getId() instanceof UUID);
        assertEquals(1, review.getVersion());
        assertTrue(review.getIsCurrentVersion());
        assertEquals(review.getId(), review.getParentId()); // parentId should be same as id for first version
    }

    @Test
    void testPrePersistWithExistingId() {
        Review review = new Review();
        review.setId(id);
        review.setVersion(2);
        review.setIsCurrentVersion(false);
        review.onCreate();
        
        assertEquals(id, review.getId());
        assertEquals(2, review.getVersion());
        assertFalse(review.getIsCurrentVersion());
        assertEquals(id, review.getParentId()); // parentId should be set to id if not set
    }

    @Test
    void testPrePersistWithExistingParentId() {
        UUID existingParentId = UUID.randomUUID();
        Review review = new Review();
        review.setId(id);
        review.setParentId(existingParentId);
        review.onCreate();
        
        assertEquals(id, review.getId());
        assertEquals(existingParentId, review.getParentId()); // parentId should not change if already set
    }

    @Test
    void testValidVersion() {
        Review review = new Review();
        review.setVersion(1);
        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidVersionTooLow() {
        Review review = new Review();
        review.setVersion(0);
        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        assertFalse(violations.isEmpty());
        assertEquals("version must be at least 1", violations.iterator().next().getMessage());
    }

    @Test
    void testVersionHistory() {
        UUID firstVersionId = UUID.randomUUID();
        UUID secondVersionId = UUID.randomUUID();
        
        Review firstVersion = Review.builder()
            .id(firstVersionId)
            .version(1)
            .parentId(firstVersionId)
            .isCurrentVersion(false)
            .build();
            
        Review secondVersion = Review.builder()
            .id(secondVersionId)
            .version(2)
            .parentId(firstVersionId)
            .isCurrentVersion(true)
            .build();
            
        assertEquals(1, firstVersion.getVersion());
        assertFalse(firstVersion.getIsCurrentVersion());
        assertEquals(firstVersionId, firstVersion.getParentId());
        
        assertEquals(2, secondVersion.getVersion());
        assertTrue(secondVersion.getIsCurrentVersion());
        assertEquals(firstVersionId, secondVersion.getParentId());
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
