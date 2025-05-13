package id.ac.ui.cs.advprog.berating.interfaces;

import java.util.List;
import java.util.UUID;

import id.ac.ui.cs.advprog.berating.dto.ReviewRequest;
import id.ac.ui.cs.advprog.berating.model.Review;

public interface ReviewService {
    /**
     * Creates a new review for a consultation
     * @param consultationId The ID of the consultation
     * @param reviewRequest The review data
     * @return The created review
     */
    Review createReview(UUID consultationId, ReviewRequest reviewRequest);

    /**
     * Gets all reviews for a specific doctor
     * @param doctorId The ID of the doctor
     * @return List of reviews for the doctor
     */
    List<Review> getDoctorReviews(UUID doctorId);

    /**
     * Updates an existing review by creating a new version
     * @param reviewId The ID of the review to update
     * @param reviewRequest The updated review data
     * @return The new version of the review
     * @throws ReviewNotFoundException if the review is not found
     * @throws IllegalStateException if the review is not the current version
     */
    Review updateReview(UUID reviewId, ReviewRequest reviewRequest);

    /**
     * Gets the complete version history of a review
     * @param reviewId The ID of any version of the review
     * @return List of all versions of the review, ordered by version number
     * @throws ReviewNotFoundException if the review is not found
     */
    List<Review> getReviewHistory(UUID reviewId);

    /**
     * Gets the current version of a review
     * @param reviewId The ID of any version of the review
     * @return The current version of the review
     * @throws ReviewNotFoundException if the review is not found
     */
    Review getCurrentVersion(UUID reviewId);
}
