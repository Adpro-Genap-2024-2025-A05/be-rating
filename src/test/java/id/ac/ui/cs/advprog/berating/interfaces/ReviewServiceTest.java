package id.ac.ui.cs.advprog.berating.interfaces;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.berating.dto.ReviewRequest;
import id.ac.ui.cs.advprog.berating.exception.ReviewNotFoundException;
import id.ac.ui.cs.advprog.berating.model.Review;

public class ReviewServiceTest {

    private ReviewService service;
    private UUID consultationId;
    private UUID doctorId;
    private UUID patientId;
    private UUID reviewId;
    private ReviewRequest reviewRequest;
    private Review review;

    @BeforeEach
    void setUp() {
        consultationId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        reviewId = UUID.randomUUID();

        reviewRequest = new ReviewRequest();
        reviewRequest.setDoctorId(doctorId);
        reviewRequest.setPatientId(patientId);
        reviewRequest.setRating(5);
        reviewRequest.setComment("Great service!");

        review = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("Great service!")
                .version(1)
                .parentId(reviewId)
                .isCurrentVersion(true)
                .build();

        // Create a mock implementation of ReviewService
        service = new ReviewService() {
            private final List<Review> reviews = new ArrayList<>();

            @Override
            public Review createReview(UUID consultationId, ReviewRequest reviewRequest) {
                Review newReview = Review.builder()
                        .id(UUID.randomUUID())
                        .doctorId(reviewRequest.getDoctorId())
                        .patientId(reviewRequest.getPatientId())
                        .consultationId(consultationId)
                        .rating(reviewRequest.getRating())
                        .comment(reviewRequest.getComment())
                        .version(1)
                        .parentId(UUID.randomUUID())
                        .isCurrentVersion(true)
                        .build();
                reviews.add(newReview);
                return newReview;
            }

            @Override
            public List<Review> getDoctorReviews(UUID doctorId) {
                return reviews.stream()
                        .filter(r -> r.getDoctorId().equals(doctorId))
                        .toList();
            }

            @Override
            public Review updateReview(UUID reviewId, ReviewRequest reviewRequest) {
                Review oldReview = reviews.stream()
                        .filter(r -> r.getId().equals(reviewId))
                        .findFirst()
                        .orElseThrow(() -> new ReviewNotFoundException(reviewId));

                if (!oldReview.getIsCurrentVersion()) {
                    throw new IllegalStateException("Cannot update a review that is not the current version");
                }

                oldReview.setIsCurrentVersion(false);
                Review newReview = Review.builder()
                        .id(UUID.randomUUID())
                        .doctorId(reviewRequest.getDoctorId())
                        .patientId(reviewRequest.getPatientId())
                        .consultationId(oldReview.getConsultationId())
                        .rating(reviewRequest.getRating())
                        .comment(reviewRequest.getComment())
                        .version(oldReview.getVersion() + 1)
                        .parentId(oldReview.getParentId())
                        .isCurrentVersion(true)
                        .build();
                reviews.add(newReview);
                return newReview;
            }

            @Override
            public List<Review> getReviewHistory(UUID reviewId) {
                Review review = reviews.stream()
                        .filter(r -> r.getId().equals(reviewId))
                        .findFirst()
                        .orElseThrow(() -> new ReviewNotFoundException(reviewId));

                return reviews.stream()
                        .filter(r -> r.getParentId().equals(review.getParentId()))
                        .sorted((r1, r2) -> r1.getVersion().compareTo(r2.getVersion()))
                        .toList();
            }

            @Override
            public Review deleteReview(UUID reviewId) {
                Review review = reviews.stream()
                        .filter(r -> r.getId().equals(reviewId))
                        .findFirst()
                        .orElseThrow(() -> new ReviewNotFoundException(reviewId));

                List<Review> versionsToDelete = reviews.stream()
                        .filter(r -> r.getParentId().equals(review.getParentId()))
                        .toList();

                reviews.removeAll(versionsToDelete);
                return review;
            }

            @Override
            public List<Review> getReviewUser(UUID patientId) {
                return reviews.stream()
                        .filter(r -> r.getPatientId().equals(patientId))
                        .toList();
            }
        };
    }

    @Test
    void testCreateReview() {
        Review result = service.createReview(consultationId, reviewRequest);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(doctorId, result.getDoctorId());
        assertEquals(patientId, result.getPatientId());
        assertEquals(consultationId, result.getConsultationId());
        assertEquals(5, result.getRating());
        assertEquals("Great service!", result.getComment());
        assertEquals(1, result.getVersion());
        assertTrue(result.getIsCurrentVersion());
        assertNotNull(result.getParentId());
    }

    @Test
    void testGetDoctorReviews() {
        service.createReview(consultationId, reviewRequest);
        List<Review> reviews = service.getDoctorReviews(doctorId);

        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        assertEquals(doctorId, reviews.get(0).getDoctorId());
    }

    @Test
    void testUpdateReview() {
        Review createdReview = service.createReview(consultationId, reviewRequest);
        
        ReviewRequest updateRequest = new ReviewRequest();
        updateRequest.setDoctorId(doctorId);
        updateRequest.setPatientId(patientId);
        updateRequest.setRating(4);
        updateRequest.setComment("Updated review");

        Review updatedReview = service.updateReview(createdReview.getId(), updateRequest);

        assertNotNull(updatedReview);
        assertNotEquals(createdReview.getId(), updatedReview.getId());
        assertEquals(2, updatedReview.getVersion());
        assertTrue(updatedReview.getIsCurrentVersion());
        assertEquals(createdReview.getParentId(), updatedReview.getParentId());
        assertEquals(4, updatedReview.getRating());
        assertEquals("Updated review", updatedReview.getComment());

        List<Review> history = service.getReviewHistory(createdReview.getId());
        assertEquals(2, history.size());
        assertFalse(history.get(0).getIsCurrentVersion());
        assertTrue(history.get(1).getIsCurrentVersion());
    }

    @Test
    void testUpdateReviewNotFound() {
        ReviewRequest updateRequest = new ReviewRequest();
        updateRequest.setDoctorId(doctorId);
        updateRequest.setPatientId(patientId);
        updateRequest.setRating(4);
        updateRequest.setComment("Updated review");

        assertThrows(ReviewNotFoundException.class, () -> 
            service.updateReview(UUID.randomUUID(), updateRequest));
    }

    @Test
    void testGetReviewHistory() {
        Review createdReview = service.createReview(consultationId, reviewRequest);
        
        ReviewRequest updateRequest = new ReviewRequest();
        updateRequest.setDoctorId(doctorId);
        updateRequest.setPatientId(patientId);
        updateRequest.setRating(4);
        updateRequest.setComment("Updated review");

        service.updateReview(createdReview.getId(), updateRequest);
        List<Review> history = service.getReviewHistory(createdReview.getId());

        assertNotNull(history);
        assertEquals(2, history.size());
        assertEquals(1, history.get(0).getVersion());
        assertEquals(2, history.get(1).getVersion());
        assertFalse(history.get(0).getIsCurrentVersion());
        assertTrue(history.get(1).getIsCurrentVersion());
    }

    @Test
    void testGetReviewHistoryNotFound() {
        assertThrows(ReviewNotFoundException.class, () -> 
            service.getReviewHistory(UUID.randomUUID()));
    }

    @Test
    void testGetReviewUser() {
        // Create multiple reviews for the same patient
        Review review1 = service.createReview(consultationId, reviewRequest);
        
        ReviewRequest reviewRequest2 = new ReviewRequest();
        reviewRequest2.setDoctorId(UUID.randomUUID());
        reviewRequest2.setPatientId(patientId);
        reviewRequest2.setRating(4);
        reviewRequest2.setComment("Second review");
        Review review2 = service.createReview(UUID.randomUUID(), reviewRequest2);

        // Create a review for a different patient
        ReviewRequest reviewRequest3 = new ReviewRequest();
        reviewRequest3.setDoctorId(doctorId);
        reviewRequest3.setPatientId(UUID.randomUUID());
        reviewRequest3.setRating(5);
        reviewRequest3.setComment("Different patient review");
        service.createReview(consultationId, reviewRequest3);

        List<Review> userReviews = service.getReviewUser(patientId);

        assertNotNull(userReviews);
        assertEquals(2, userReviews.size());
        assertTrue(userReviews.stream().allMatch(r -> r.getPatientId().equals(patientId)));
        assertTrue(userReviews.contains(review1));
        assertTrue(userReviews.contains(review2));
    }

    @Test
    void testGetReviewUserEmptyList() {
        List<Review> userReviews = service.getReviewUser(patientId);
        
        assertNotNull(userReviews);
        assertTrue(userReviews.isEmpty());
    }

    @Test
    void testGetReviewUserWithUpdatedReviews() {
        // Create initial review
        Review initialReview = service.createReview(consultationId, reviewRequest);
        
        // Update the review
        ReviewRequest updateRequest = new ReviewRequest();
        updateRequest.setDoctorId(doctorId);
        updateRequest.setPatientId(patientId);
        updateRequest.setRating(4);
        updateRequest.setComment("Updated review");
        Review updatedReview = service.updateReview(initialReview.getId(), updateRequest);

        List<Review> userReviews = service.getReviewUser(patientId);

        assertNotNull(userReviews);
        assertEquals(2, userReviews.size()); // Should include both versions
        assertTrue(userReviews.stream().allMatch(r -> r.getPatientId().equals(patientId)));
        assertTrue(userReviews.contains(initialReview));
        assertTrue(userReviews.contains(updatedReview));
    }
}
