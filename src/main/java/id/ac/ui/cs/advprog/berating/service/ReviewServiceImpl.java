package id.ac.ui.cs.advprog.berating.service;

import id.ac.ui.cs.advprog.berating.dto.ReviewRequest;
import id.ac.ui.cs.advprog.berating.exception.ReviewNotFoundException;
import id.ac.ui.cs.advprog.berating.interfaces.ReviewService;
import lombok.RequiredArgsConstructor;
import id.ac.ui.cs.advprog.berating.model.Review;
import id.ac.ui.cs.advprog.berating.repository.ReviewRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
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
    public Review updateReview(UUID reviewId, ReviewRequest reviewRequest) {
        Review oldReview = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if (!oldReview.getIsCurrentVersion()) {
            throw new IllegalStateException("Cannot update a review that is not the current version");
        }

        Review newReview = createNewVersion(oldReview, reviewRequest);

        oldReview.setIsCurrentVersion(false);
        reviewRepository.save(oldReview);

        return reviewRepository.save(newReview);
    }

    @Override
    public List<Review> getReviewHistory(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        return reviewRepository.findAllVersionsByParentId(review.getParentId());
    }

    @Override
    public Review getCurrentVersion(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if (review.getIsCurrentVersion()) {
            return review;
        }

        return reviewRepository.findCurrentVersionByParentId(review.getParentId());
    }

    @Override
    public Review deleteReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        List<Review> allVersions = reviewRepository.findAllVersionsByParentId(review.getParentId());
        
        for (Review version : allVersions) {
            reviewRepository.delete(version);
        }

        return review;
    }

    @Override
    public List<Review> getReviewUser(UUID patientId) {
        return reviewRepository.findByPatientId(patientId);
    }

    private Review createReviewEntity(UUID consultationId, ReviewRequest reviewRequest) {
        Review.ReviewBuilder builder = Review.builder()
                .consultationId(consultationId)
                .doctorId(reviewRequest.getDoctorId())
                .patientId(reviewRequest.getPatientId())
                .rating(reviewRequest.getRating())
                .version(1)
                .isCurrentVersion(true);

        if (reviewRequest.getComment() != null) {
            builder.comment(reviewRequest.getComment());
        }

        return builder.build();
    }

    private Review createNewVersion(Review oldReview, ReviewRequest reviewRequest) {
        return Review.builder()
            .doctorId(reviewRequest.getDoctorId())
            .patientId(reviewRequest.getPatientId())
            .consultationId(oldReview.getConsultationId())
            .rating(reviewRequest.getRating())
            .comment(reviewRequest.getComment())
            .version(oldReview.getVersion() + 1)
            .parentId(oldReview.getParentId())
            .isCurrentVersion(true)
            .build();
    }
}
