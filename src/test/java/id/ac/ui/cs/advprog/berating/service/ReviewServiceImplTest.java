package id.ac.ui.cs.advprog.berating.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import id.ac.ui.cs.advprog.berating.dto.ReviewRequest;
import id.ac.ui.cs.advprog.berating.model.Review;
import id.ac.ui.cs.advprog.berating.repository.ReviewRepository;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserService userService;

    @Mock
    private ConsultationHistoryService consultationHistoryService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

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
                .build();
    }

    // Positive Cases
    @Test
    void testCreateReviewWithComment() {
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.createReview(consultationId, reviewRequest);

        assertNotNull(result);
        assertEquals(reviewId, result.getId());
        assertEquals(doctorId, result.getDoctorId());
        assertEquals(patientId, result.getPatientId());
        assertEquals(consultationId, result.getConsultationId());
        assertEquals(5, result.getRating());
        assertEquals("Great service!", result.getComment());

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void testCreateReviewWithoutComment() {
        reviewRequest.setComment(null);
        Review reviewWithoutComment = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .build();

        when(reviewRepository.save(any(Review.class))).thenReturn(reviewWithoutComment);

        Review result = reviewService.createReview(consultationId, reviewRequest);

        assertNotNull(result);
        assertEquals(reviewId, result.getId());
        assertEquals(doctorId, result.getDoctorId());
        assertEquals(patientId, result.getPatientId());
        assertEquals(consultationId, result.getConsultationId());
        assertEquals(5, result.getRating());
        assertNull(result.getComment());

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void testGetDoctorReviews() {
        List<Review> expectedReviews = Arrays.asList(review);
        when(reviewRepository.findByDoctorId(doctorId)).thenReturn(expectedReviews);

        List<Review> result = reviewService.getDoctorReviews(doctorId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(review, result.get(0));
        verify(reviewRepository).findByDoctorId(doctorId);
    }

    @Test
    void testGetDoctorReviewsEmptyList() {
        when(reviewRepository.findByDoctorId(doctorId)).thenReturn(Collections.emptyList());

        List<Review> result = reviewService.getDoctorReviews(doctorId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reviewRepository).findByDoctorId(doctorId);
    }

    @Test
    void testCreateReviewWithMinimumRating() {
        reviewRequest.setRating(1);
        Review reviewWithMinRating = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(1)
                .comment("Great service!")
                .build();

        when(reviewRepository.save(any(Review.class))).thenReturn(reviewWithMinRating);

        Review result = reviewService.createReview(consultationId, reviewRequest);

        assertNotNull(result);
        assertEquals(1, result.getRating());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void testCreateReviewWithMaximumRating() {
        reviewRequest.setRating(5);
        Review reviewWithMaxRating = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("Great service!")
                .build();

        when(reviewRepository.save(any(Review.class))).thenReturn(reviewWithMaxRating);

        Review result = reviewService.createReview(consultationId, reviewRequest);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        verify(reviewRepository).save(any(Review.class));
    }
}