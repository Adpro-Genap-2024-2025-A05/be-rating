package id.ac.ui.cs.advprog.berating.interfaces;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.berating.dto.ReviewRequest;
import id.ac.ui.cs.advprog.berating.enums.ReviewStatus;
import id.ac.ui.cs.advprog.berating.model.Review;

public class ReviewServiceTest {
    
    @Test
    void testInterfaceContract() {
        assertDoesNotThrow(() -> {
            ReviewService service = new ReviewService() {
                @Override
                public Review createReview(UUID consultationId, ReviewRequest reviewRequest) {
                    return null;
                }

                @Override
                public List<Review> getDoctorReviews(UUID doctorId) {
                    return null;
                }

                @Override
                public Review updateReviewStatus(UUID reviewId, ReviewStatus status) {
                    return null;
                }
            };
        });
    }

    @Test
    void testMethodSignatures() throws NoSuchMethodException {
        ReviewService.class.getMethod("createReview", UUID.class, ReviewRequest.class);
        ReviewService.class.getMethod("getDoctorReviews", UUID.class);
        ReviewService.class.getMethod("updateReviewStatus", UUID.class, ReviewStatus.class);
    }

    @Test
    void testReturnTypes() throws NoSuchMethodException {
        assertEquals(Review.class, ReviewService.class.getMethod("createReview", UUID.class, ReviewRequest.class).getReturnType());
        assertEquals(List.class, ReviewService.class.getMethod("getDoctorReviews", UUID.class).getReturnType());
        assertEquals(Review.class, ReviewService.class.getMethod("updateReviewStatus", UUID.class, ReviewStatus.class).getReturnType());
    }

    @Test
    void testParameterTypes() throws NoSuchMethodException {
        assertArrayEquals(
            new Class<?>[] { UUID.class, ReviewRequest.class },
            ReviewService.class.getMethod("createReview", UUID.class, ReviewRequest.class).getParameterTypes()
        );
        
        assertArrayEquals(
            new Class<?>[] { UUID.class },
            ReviewService.class.getMethod("getDoctorReviews", UUID.class).getParameterTypes()
        );
        
        assertArrayEquals(
            new Class<?>[] { UUID.class, ReviewStatus.class },
            ReviewService.class.getMethod("updateReviewStatus", UUID.class, ReviewStatus.class).getParameterTypes()
        );
    }
}
