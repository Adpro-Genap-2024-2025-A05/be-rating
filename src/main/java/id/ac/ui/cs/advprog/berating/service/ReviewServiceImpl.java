package id.ac.ui.cs.advprog.berating.service;

import id.ac.ui.cs.advprog.berating.model.Review;
import id.ac.ui.cs.advprog.berating.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Review createReview(Review review) {
        if (review.getId() == null) {
            review.setId(UUID.randomUUID());
        }
        return reviewRepository.save(review);
    }

    @Override
    public Review getReviewById(UUID id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Review not found with id: " + id));
    }

    @Override
    public Review updateReview(UUID id, Review updatedReview) {
        Review existing = getReviewById(id);
        existing.setRating(updatedReview.getRating());
        existing.setComment(updatedReview.getComment());
        existing.setStatus(updatedReview.getStatus());
        return reviewRepository.save(existing);
    }

    @Override
    public void deleteReview(UUID id) {
        reviewRepository.deleteById(id);
    }
}
