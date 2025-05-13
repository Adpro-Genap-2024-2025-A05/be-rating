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
import org.mockito.ArgumentCaptor;

import id.ac.ui.cs.advprog.berating.dto.ReviewRequest;
import id.ac.ui.cs.advprog.berating.exception.ReviewNotFoundException;
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
    private UUID parentId;
    private ReviewRequest reviewRequest;
    private Review review;
    private Review updatedReview;

    @BeforeEach
    void setUp() {
        consultationId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        reviewId = UUID.randomUUID();
        parentId = reviewId;

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
                .parentId(parentId)
                .isCurrentVersion(true)
                .build();

        updatedReview = Review.builder()
                .id(UUID.randomUUID())
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(4)
                .comment("Updated review")
                .version(2)
                .parentId(parentId)
                .isCurrentVersion(true)
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

    @Test
    void testCreateReviewWithVersioning() {
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.createReview(consultationId, reviewRequest);

        assertNotNull(result);
        assertEquals(reviewId, result.getId());
        assertEquals(1, result.getVersion());
        assertTrue(result.getIsCurrentVersion());
        assertEquals(parentId, result.getParentId());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void testUpdateReviewWithVersioning() {
        review = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("Great service!")
                .version(1)
                .parentId(parentId)
                .isCurrentVersion(true)
                .build();

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);

        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review savedReview = invocation.getArgument(0);
            return Review.builder()
                    .id(savedReview.getId() == null ? UUID.randomUUID() : savedReview.getId())
                    .doctorId(savedReview.getDoctorId())
                    .patientId(savedReview.getPatientId())
                    .consultationId(savedReview.getConsultationId())
                    .rating(savedReview.getRating())
                    .comment(savedReview.getComment())
                    .version(savedReview.getVersion())
                    .parentId(savedReview.getParentId())
                    .isCurrentVersion(savedReview.getIsCurrentVersion())
                    .build();
        });

        ReviewRequest updateRequest = new ReviewRequest();
        updateRequest.setDoctorId(doctorId);
        updateRequest.setPatientId(patientId);
        updateRequest.setRating(4);
        updateRequest.setComment("Updated review");

        Review result = reviewService.updateReview(reviewId, updateRequest);

        verify(reviewRepository, times(2)).save(reviewCaptor.capture());
        List<Review> savedReviews = reviewCaptor.getAllValues();

        assertNotNull(result);
        assertNotEquals(reviewId, result.getId());
        assertEquals(2, result.getVersion());
        assertTrue(result.getIsCurrentVersion());
        assertEquals(parentId, result.getParentId());
        assertEquals(4, result.getRating());
        assertEquals("Updated review", result.getComment());

        Review oldReview = savedReviews.get(0);
        Review newReview = savedReviews.get(1);

        assertEquals(reviewId, oldReview.getId());
        assertFalse(oldReview.getIsCurrentVersion());
        assertEquals(1, oldReview.getVersion());
        assertEquals(parentId, oldReview.getParentId());

        assertNotEquals(reviewId, newReview.getId());
        assertTrue(newReview.getIsCurrentVersion());
        assertEquals(2, newReview.getVersion());
        assertEquals(parentId, newReview.getParentId());
        assertEquals(4, newReview.getRating());
        assertEquals("Updated review", newReview.getComment());
    }

    @Test
    void testUpdateReviewNotFound() {
        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.empty());

        ReviewRequest updateRequest = new ReviewRequest();
        updateRequest.setDoctorId(doctorId);
        updateRequest.setPatientId(patientId);
        updateRequest.setRating(4);
        updateRequest.setComment("Updated review");

        assertThrows(ReviewNotFoundException.class, () -> 
            reviewService.updateReview(reviewId, updateRequest));
    }

    @Test
    void testUpdateReviewAlreadyNotCurrent() {
        review.setIsCurrentVersion(false);
        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.of(review));

        ReviewRequest updateRequest = new ReviewRequest();
        updateRequest.setDoctorId(doctorId);
        updateRequest.setPatientId(patientId);
        updateRequest.setRating(4);
        updateRequest.setComment("Updated review");

        assertThrows(IllegalStateException.class, () -> 
            reviewService.updateReview(reviewId, updateRequest));
    }

    @Test
    void testGetReviewHistory() {
        List<Review> reviewHistory = Arrays.asList(review, updatedReview);
        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.of(review));
        when(reviewRepository.findAllVersionsByParentId(parentId)).thenReturn(reviewHistory);

        List<Review> result = reviewService.getReviewHistory(reviewId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(review));
        assertTrue(result.contains(updatedReview));
        verify(reviewRepository).findAllVersionsByParentId(parentId);
    }

    @Test
    void testGetReviewHistoryNotFound() {
        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> 
            reviewService.getReviewHistory(reviewId));
    }

    @Test
    void testGetCurrentVersion() {
        review = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("Great service!")
                .version(1)
                .parentId(parentId)
                .isCurrentVersion(true)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.of(review));

        Review result = reviewService.getCurrentVersion(reviewId);

        assertNotNull(result);
        assertEquals(reviewId, result.getId());
        assertTrue(result.getIsCurrentVersion());
        
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, never()).findCurrentVersionByParentId(any());
    }

    @Test
    void testGetCurrentVersionNotFound() {
        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> 
            reviewService.getCurrentVersion(reviewId));
    }

    @Test
    void testGetCurrentVersionWhenNotCurrent() {
        // Setup the review with all required fields
        review = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("Great service!")
                .version(1)
                .parentId(parentId)
                .isCurrentVersion(false)
                .build();

        updatedReview = Review.builder()
                .id(UUID.randomUUID())
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(4)
                .comment("Updated review")
                .version(2)
                .parentId(parentId)
                .isCurrentVersion(true)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.of(review));
        when(reviewRepository.findCurrentVersionByParentId(parentId)).thenReturn(updatedReview);

        Review result = reviewService.getCurrentVersion(reviewId);

        assertNotNull(result);
        assertEquals(updatedReview.getId(), result.getId());
        assertTrue(result.getIsCurrentVersion());
        
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).findCurrentVersionByParentId(parentId);
    }

    @Test
    void testReviewNotFoundExceptionWithMessage() {
        String errorMessage = "Custom error message";
        ReviewNotFoundException exception = new ReviewNotFoundException(errorMessage);
        
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testReviewNotFoundExceptionWithMessageAndCause() {
        String errorMessage = "Custom error message";
        Throwable cause = new RuntimeException("Original error");
        ReviewNotFoundException exception = new ReviewNotFoundException(errorMessage, cause);
        
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testUpdateReviewThrowsReviewNotFoundExceptionWithMessage() {
        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.empty());

        ReviewRequest updateRequest = new ReviewRequest();
        updateRequest.setDoctorId(doctorId);
        updateRequest.setPatientId(patientId);
        updateRequest.setRating(4);
        updateRequest.setComment("Updated review");

        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> 
            reviewService.updateReview(reviewId, updateRequest));
        
        assertTrue(exception.getMessage().contains(reviewId.toString()));
    }

    @Test
    void testGetCurrentVersionThrowsReviewNotFoundExceptionWithMessage() {
        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.empty());

        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> 
            reviewService.getCurrentVersion(reviewId));
        
        assertTrue(exception.getMessage().contains(reviewId.toString()));
    }

    @Test
    void testGetReviewHistoryThrowsReviewNotFoundExceptionWithMessage() {
        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.empty());

        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> 
            reviewService.getReviewHistory(reviewId));
        
        assertTrue(exception.getMessage().contains(reviewId.toString()));
    }

    @Test
    void testDeleteReviewDeletesAllVersions() {
        // Setup initial review
        review = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("Great service!")
                .version(1)
                .parentId(parentId)
                .isCurrentVersion(true)
                .build();

        // Setup a second version
        Review secondVersion = Review.builder()
                .id(UUID.randomUUID())
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(4)
                .comment("Updated review")
                .version(2)
                .parentId(parentId)
                .isCurrentVersion(true)
                .build();

        // Setup a third version
        Review thirdVersion = Review.builder()
                .id(UUID.randomUUID())
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(3)
                .comment("Final review")
                .version(3)
                .parentId(parentId)
                .isCurrentVersion(true)
                .build();

        List<Review> allVersions = Arrays.asList(review, secondVersion, thirdVersion);

        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.of(review));
        when(reviewRepository.findAllVersionsByParentId(parentId)).thenReturn(allVersions);

        Review result = reviewService.deleteReview(reviewId);

        // Verify the result
        assertNotNull(result);
        assertEquals(reviewId, result.getId());
        assertEquals(parentId, result.getParentId());

        // Verify all versions were deleted
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).findAllVersionsByParentId(parentId);
        verify(reviewRepository, times(1)).delete(review);
        verify(reviewRepository, times(1)).delete(secondVersion);
        verify(reviewRepository, times(1)).delete(thirdVersion);
    }

    @Test
    void testDeleteReviewNotFound() {
        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.empty());

        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> 
            reviewService.deleteReview(reviewId));
        
        assertTrue(exception.getMessage().contains(reviewId.toString()));
        verify(reviewRepository, never()).findAllVersionsByParentId(any());
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void testDeleteReviewWithNoOtherVersions() {
        // Setup single version review
        review = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("Great service!")
                .version(1)
                .parentId(parentId)
                .isCurrentVersion(true)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.of(review));
        when(reviewRepository.findAllVersionsByParentId(parentId)).thenReturn(Collections.singletonList(review));

        Review result = reviewService.deleteReview(reviewId);

        // Verify the result
        assertNotNull(result);
        assertEquals(reviewId, result.getId());
        assertEquals(parentId, result.getParentId());

        // Verify only one version was deleted
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).findAllVersionsByParentId(parentId);
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void testGetReviewUser() {
        // Setup reviews for a patient
        Review review1 = Review.builder()
                .id(UUID.randomUUID())
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("First review")
                .version(1)
                .parentId(UUID.randomUUID())
                .isCurrentVersion(true)
                .build();

        Review review2 = Review.builder()
                .id(UUID.randomUUID())
                .doctorId(UUID.randomUUID())
                .patientId(patientId)
                .consultationId(UUID.randomUUID())
                .rating(4)
                .comment("Second review")
                .version(1)
                .parentId(UUID.randomUUID())
                .isCurrentVersion(true)
                .build();

        List<Review> expectedReviews = Arrays.asList(review1, review2);
        when(reviewRepository.findByPatientId(patientId)).thenReturn(expectedReviews);

        List<Review> result = reviewService.getReviewUser(patientId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(review1));
        assertTrue(result.contains(review2));
        verify(reviewRepository, times(1)).findByPatientId(patientId);
    }

    @Test
    void testGetReviewUserEmptyList() {
        when(reviewRepository.findByPatientId(patientId)).thenReturn(Collections.emptyList());

        List<Review> result = reviewService.getReviewUser(patientId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reviewRepository, times(1)).findByPatientId(patientId);
    }

    @Test
    void testDeleteReviewWithMultipleVersionsAndSomeNotCurrent() {
        // Setup initial review
        review = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("First version")
                .version(1)
                .parentId(parentId)
                .isCurrentVersion(false)
                .build();

        // Setup second version (not current)
        Review secondVersion = Review.builder()
                .id(UUID.randomUUID())
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(4)
                .comment("Second version")
                .version(2)
                .parentId(parentId)
                .isCurrentVersion(false)
                .build();

        // Setup third version (current)
        Review thirdVersion = Review.builder()
                .id(UUID.randomUUID())
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(3)
                .comment("Current version")
                .version(3)
                .parentId(parentId)
                .isCurrentVersion(true)
                .build();

        List<Review> allVersions = Arrays.asList(review, secondVersion, thirdVersion);

        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.of(review));
        when(reviewRepository.findAllVersionsByParentId(parentId)).thenReturn(allVersions);

        Review result = reviewService.deleteReview(reviewId);

        // Verify the result
        assertNotNull(result);
        assertEquals(reviewId, result.getId());
        assertEquals(parentId, result.getParentId());
        assertFalse(result.getIsCurrentVersion());

        // Verify all versions were deleted regardless of their current status
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).findAllVersionsByParentId(parentId);
        verify(reviewRepository, times(1)).delete(review);
        verify(reviewRepository, times(1)).delete(secondVersion);
        verify(reviewRepository, times(1)).delete(thirdVersion);
    }

    @Test
    void testDeleteReviewWithEmptyVersionList() {
        // Setup review
        review = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("Test review")
                .version(1)
                .parentId(parentId)
                .isCurrentVersion(true)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.of(review));
        when(reviewRepository.findAllVersionsByParentId(parentId)).thenReturn(Collections.emptyList());

        Review result = reviewService.deleteReview(reviewId);

        // Verify the result
        assertNotNull(result);
        assertEquals(reviewId, result.getId());
        assertEquals(parentId, result.getParentId());

        // Verify repository calls
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).findAllVersionsByParentId(parentId);
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void testDeleteReviewWithNullParentId() {
        // Setup review with null parentId
        review = Review.builder()
                .id(reviewId)
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("Test review")
                .version(1)
                .parentId(null)
                .isCurrentVersion(true)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.of(review));
        when(reviewRepository.findAllVersionsByParentId(null)).thenReturn(Collections.singletonList(review));

        Review result = reviewService.deleteReview(reviewId);

        // Verify the result
        assertNotNull(result);
        assertEquals(reviewId, result.getId());
        assertNull(result.getParentId());

        // Verify repository calls
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).findAllVersionsByParentId(null);
        verify(reviewRepository, times(1)).delete(review);
    }
}