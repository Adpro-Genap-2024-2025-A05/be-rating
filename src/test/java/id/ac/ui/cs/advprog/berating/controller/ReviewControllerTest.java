package id.ac.ui.cs.advprog.berating.controller;

import id.ac.ui.cs.advprog.berating.dto.BaseResponseDTO;
import id.ac.ui.cs.advprog.berating.dto.ReviewRequest;
import id.ac.ui.cs.advprog.berating.interfaces.ReviewService;
import id.ac.ui.cs.advprog.berating.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private UUID doctorId;
    private UUID patientId;
    private UUID consultationId;
    private UUID reviewId;
    private Review review;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        doctorId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        consultationId = UUID.randomUUID();
        reviewId = UUID.randomUUID();

        // Setup Review
        review = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("Pelayanan bagus")
                .createdAt(new Date())
                .build();

        // Setup ReviewRequest
        reviewRequest = new ReviewRequest();
        reviewRequest.setDoctorId(doctorId);
        reviewRequest.setPatientId(patientId);
        reviewRequest.setRating(5);
        reviewRequest.setComment("Pelayanan bagus");
    }

    @Test
    void testGetDoctorReviews_Success() {
        List<Review> reviews = Arrays.asList(review);
        when(reviewService.getDoctorReviews(doctorId)).thenReturn(reviews);

        ResponseEntity<BaseResponseDTO<List<Review>>> response = reviewController.getDoctorReviews(doctorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(200, response.getBody().getStatus());
        assertEquals("Success to retrieve doctor reviews.", response.getBody().getMessage());
        assertEquals(reviews, response.getBody().getData());
        verify(reviewService).getDoctorReviews(doctorId);
    }

    @Test
    void testCreateReview_Success() {
        when(reviewService.createReview(eq(consultationId), any(ReviewRequest.class))).thenReturn(review);

        ResponseEntity<?> response = reviewController.createReview(consultationId, reviewRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        BaseResponseDTO<Review> responseBody = (BaseResponseDTO<Review>) response.getBody();
        assertEquals(201, responseBody.getStatus());
        assertEquals("Success to retrieve doctor reviews.", responseBody.getMessage());
        assertEquals(review, responseBody.getData());
        verify(reviewService).createReview(eq(consultationId), any(ReviewRequest.class));
    }

    @Test
    void testCreateReview_InvalidRating() {
        reviewRequest.setRating(6); // Invalid rating (should be 1-5)

        ResponseEntity<?> response = reviewController.createReview(consultationId, reviewRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Rating must be between 1 and 5", response.getBody());
        verify(reviewService, never()).createReview(any(), any());
    }

    @Test
    void testCreateReview_MissingRequiredField() {
        reviewRequest.setRating(null); // Required field

        ResponseEntity<?> response = reviewController.createReview(consultationId, reviewRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Rating is required", response.getBody());
        verify(reviewService, never()).createReview(any(), any());
    }

    @Test
    void testGetDoctorReviews_EmptyList() {
        when(reviewService.getDoctorReviews(doctorId)).thenReturn(Collections.emptyList());

        ResponseEntity<BaseResponseDTO<List<Review>>> response = reviewController.getDoctorReviews(doctorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(200, response.getBody().getStatus());
        assertEquals("Success to retrieve doctor reviews.", response.getBody().getMessage());
        assertEquals(Collections.emptyList(), response.getBody().getData());
        verify(reviewService).getDoctorReviews(doctorId);
    }

    @SuppressWarnings("unchecked")
@Test
    void testCreateReview_WithoutComment() {
        reviewRequest.setComment(null); // Comment is optional
        
        Review reviewWithoutComment = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .createdAt(new Date())
                .build();

        when(reviewService.createReview(eq(consultationId), any(ReviewRequest.class))).thenReturn(reviewWithoutComment);

        ResponseEntity<?> response = reviewController.createReview(consultationId, reviewRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        BaseResponseDTO<Review> responseBody = (BaseResponseDTO<Review>) response.getBody();
        assertEquals(201, responseBody.getStatus());
        assertEquals("Success to retrieve doctor reviews.", responseBody.getMessage());
        assertEquals(reviewWithoutComment, responseBody.getData());
        verify(reviewService).createReview(eq(consultationId), any(ReviewRequest.class));
    }

    @Test
    void testCreateReview_MinimumRating() {
        reviewRequest.setRating(1); // Minimum valid rating
        
        Review reviewWithMinRating = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(1)
                .comment("Pelayanan bagus")
                .createdAt(new Date())
                .build();

        when(reviewService.createReview(eq(consultationId), any(ReviewRequest.class))).thenReturn(reviewWithMinRating);

        ResponseEntity<?> response = reviewController.createReview(consultationId, reviewRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        BaseResponseDTO<Review> responseBody = (BaseResponseDTO<Review>) response.getBody();
        assertEquals(201, responseBody.getStatus());
        assertEquals("Success to retrieve doctor reviews.", responseBody.getMessage());
        assertEquals(reviewWithMinRating, responseBody.getData());
        verify(reviewService).createReview(eq(consultationId), any(ReviewRequest.class));
    }

    @Test
    void testCreateReview_ServiceThrowsIllegalArgumentException() {
        String errorMessage = "Invalid consultation ID";
        when(reviewService.createReview(eq(consultationId), any(ReviewRequest.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<?> response = reviewController.createReview(consultationId, reviewRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(reviewService).createReview(eq(consultationId), any(ReviewRequest.class));
    }
}