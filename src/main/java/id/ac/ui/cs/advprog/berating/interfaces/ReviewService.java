package id.ac.ui.cs.advprog.berating.interfaces;

import java.util.List;
import java.util.UUID;

import id.ac.ui.cs.advprog.berating.dto.ReviewRequest;
import id.ac.ui.cs.advprog.berating.model.Review;

public interface ReviewService {
    Review createReview(UUID consultationId, ReviewRequest reviewRequest);
    List<Review> getDoctorReviews(UUID doctorId);
}
