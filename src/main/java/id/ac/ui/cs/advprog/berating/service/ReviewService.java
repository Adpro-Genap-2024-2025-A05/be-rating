package id.ac.ui.cs.advprog.berating.service;

import id.ac.ui.cs.advprog.berating.model.Review;

import java.util.UUID;

public interface ReviewService {
    Review createReview(Review review);
    Review getReviewById(UUID id);
    Review updateReview(UUID id, Review updatedReview);
    void deleteReview(UUID id);
}
