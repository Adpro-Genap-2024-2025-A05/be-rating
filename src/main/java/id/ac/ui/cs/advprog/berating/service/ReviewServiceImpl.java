package id.ac.ui.cs.advprog.berating.service;

import id.ac.ui.cs.advprog.berating.dto.ReviewRequest;
import id.ac.ui.cs.advprog.berating.interfaces.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import id.ac.ui.cs.advprog.berating.model.Review;
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

    private Review createReviewEntity(UUID consultationId, ReviewRequest reviewRequest) {
        Review.ReviewBuilder builder = Review.builder()
                .consultationId(consultationId)
                .doctorId(reviewRequest.getDoctorId())
                .patientId(reviewRequest.getPatientId())
                .rating(reviewRequest.getRating());

        if (reviewRequest.getComment() != null) {
            builder.comment(reviewRequest.getComment());
        }

        return builder.build();
    }
}
