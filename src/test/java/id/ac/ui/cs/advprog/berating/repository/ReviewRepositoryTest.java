package id.ac.ui.cs.advprog.berating.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import id.ac.ui.cs.advprog.berating.model.Review;

@DataJpaTest
public class ReviewRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    private UUID doctorId1;
    private UUID doctorId2;
    private UUID patientId;
    private UUID consultationId;
    private Review review1;
    private Review review2;
    private Review review3;

    @BeforeEach
    void setUp() {
        doctorId1 = UUID.randomUUID();
        doctorId2 = UUID.randomUUID();
        patientId = UUID.randomUUID();
        consultationId = UUID.randomUUID();

        review1 = Review.builder()
                .doctorId(doctorId1)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("Great service!")
                .build();

        review2 = Review.builder()
                .doctorId(doctorId1)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(4)
                .comment("Good service")
                .build();

        review3 = Review.builder()
                .doctorId(doctorId2)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(3)
                .comment("Average service")
                .build();

        entityManager.persist(review1);
        entityManager.persist(review2);
        entityManager.persist(review3);
        entityManager.flush();
    }

    @Test
    void testFindByDoctorId() {
        List<Review> reviews = reviewRepository.findByDoctorId(doctorId1);
        
        assertNotNull(reviews);
        assertEquals(2, reviews.size());
        assertTrue(reviews.stream().allMatch(review -> review.getDoctorId().equals(doctorId1)));
    }

    @Test
    void testFindByDoctorIdWithNoReviews() {
        UUID nonExistentDoctorId = UUID.randomUUID();
        List<Review> reviews = reviewRepository.findByDoctorId(nonExistentDoctorId);
        
        assertNotNull(reviews);
        assertTrue(reviews.isEmpty());
    }

    @Test
    void testSaveReview() {
        Review newReview = Review.builder()
                .doctorId(doctorId1)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("New review")
                .build();

        Review savedReview = reviewRepository.save(newReview);
        
        assertNotNull(savedReview);
        assertNotNull(savedReview.getId());
        assertEquals(newReview.getDoctorId(), savedReview.getDoctorId());
        assertEquals(newReview.getRating(), savedReview.getRating());
    }

    @Test
    void testFindById() {
        Optional<Review> foundReview = reviewRepository.findById(review1.getId());
        
        assertTrue(foundReview.isPresent());
        assertEquals(review1.getId(), foundReview.get().getId());
        assertEquals(review1.getDoctorId(), foundReview.get().getDoctorId());
    }

    @Test
    void testFindByIdNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<Review> foundReview = reviewRepository.findById(nonExistentId);
        
        assertTrue(foundReview.isEmpty());
    }

    @Test
    void testFindAll() {
        List<Review> allReviews = reviewRepository.findAll();
        
        assertNotNull(allReviews);
        assertEquals(3, allReviews.size());
    }

    @Test
    void testDeleteReview() {
        reviewRepository.delete(review1);
        Optional<Review> deletedReview = reviewRepository.findById(review1.getId());
        
        assertTrue(deletedReview.isEmpty());
    }

    @Test
    void testUpdateReview() {
        review1.setRating(4);
        review1.setComment("Updated comment");
        Review updatedReview = reviewRepository.save(review1);
        
        assertNotNull(updatedReview);
        assertEquals(4, updatedReview.getRating());
        assertEquals("Updated comment", updatedReview.getComment());
    }
}
