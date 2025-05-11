package id.ac.ui.cs.advprog.berating.service;

import id.ac.ui.cs.advprog.berating.dto.ReviewRequest;
import id.ac.ui.cs.advprog.berating.interfaces.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import id.ac.ui.cs.advprog.berating.model.Review;
import id.ac.ui.cs.advprog.berating.enums.ReviewStatus;
import id.ac.ui.cs.advprog.berating.repository.ReviewRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ConsultationHistoryService consultationHistoryService;

    @Override
    public Review createReview(UUID consultationId, ReviewRequest reviewRequest) {
        Review review = createReviewEntity(consultationId, reviewRequest);
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getDoctorReviews(UUID doctorId) {
        return reviewRepository.findByDoctorId(doctorId);
    }

    @Override
    public Review updateReviewStatus(UUID reviewId, ReviewStatus status) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        review.setStatus(status);
        return reviewRepository.save(review);
    }

    private Review createReviewEntity(UUID consultationId, ReviewRequest reviewRequest) {
        Review.ReviewBuilder builder = Review.builder()
                .consultationId(consultationId)
                .doctorId(reviewRequest.getDoctorId())
                .patientId(reviewRequest.getPatientId())
                .rating(reviewRequest.getRating())
                .status(ReviewStatus.PENDING);

        if (reviewRequest.getComment() != null) {
            builder.comment(reviewRequest.getComment());
        }

        return builder.build();
    }
}
