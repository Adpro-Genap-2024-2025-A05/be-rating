package id.ac.ui.cs.advprog.berating.service;

import id.ac.ui.cs.advprog.berating.model.Review;
import id.ac.ui.cs.advprog.berating.model.User;
import id.ac.ui.cs.advprog.berating.model.ReviewStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewServiceImplTest {

    private User patient;
    private User doctor;
    private Review review;

    @BeforeEach
    void setUp() {
        patient = new User();
        patient.setId(UUID.randomUUID());
        patient.setName("Patient A");

        doctor = new User();
        doctor.setId(UUID.randomUUID());
        doctor.setName("Doctor B");

        review = new Review();
        review.setId(UUID.randomUUID());
        review.setPatient(patient);
        review.setDoctor(doctor);
        review.setRating(5);
        review.setComment("Sangat baik");
        review.setStatus(ReviewStatus.PENDING);
        review.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testReviewCreation() {
        assertEquals(patient, review.getPatient());
        assertEquals(doctor, review.getDoctor());
        assertEquals(5, review.getRating());
        assertEquals("Sangat baik", review.getComment());
        assertEquals(ReviewStatus.PENDING, review.getStatus());
        assertNotNull(review.getCreatedAt());
    }

    @Test
    void testUpdateReview() {
        review.setRating(3);
        review.setComment("Biasa aja");

        assertEquals(3, review.getRating());
        assertEquals("Biasa aja", review.getComment());
    }

    @Test
    void testStatusChangeToApproved() {
        review.setStatus(ReviewStatus.APPROVED);
        assertEquals(ReviewStatus.APPROVED, review.getStatus());
    }

    @Test
    void testDeleteReviewSimulation() {
        review.setComment(null);
        review.setRating(0);
        review.setStatus(null);

        assertNull(review.getComment());
        assertNull(review.getStatus());
        assertEquals(0, review.getRating());
    }
