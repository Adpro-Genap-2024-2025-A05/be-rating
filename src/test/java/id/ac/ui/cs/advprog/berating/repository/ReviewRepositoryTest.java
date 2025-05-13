package id.ac.ui.cs.advprog.berating.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
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

    private UUID doctorId;
    private UUID patientId;
    private UUID consultationId;
    private UUID parentId;
    private Review review1;
    private Review review2;
    private Review review3;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        consultationId = UUID.randomUUID();
        parentId = UUID.randomUUID();

        review1 = Review.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("First version")
                .version(1)
                .parentId(parentId)
                .isCurrentVersion(false)
                .build();

        review2 = Review.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(4)
                .comment("Second version")
                .version(2)
                .parentId(parentId)
                .isCurrentVersion(false)
                .build();

        review3 = Review.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(3)
                .comment("Third version")
                .version(3)
                .parentId(parentId)
                .isCurrentVersion(true)
                .build();

        entityManager.persist(review1);
        entityManager.persist(review2);
        entityManager.persist(review3);
        entityManager.flush();
    }

    @Test
    void testFindByDoctorId() {
        List<Review> reviews = reviewRepository.findByDoctorId(doctorId);
        
        assertNotNull(reviews);
        assertEquals(1, reviews.size()); // Only current version should be returned
        assertTrue(reviews.stream().allMatch(r -> r.getDoctorId().equals(doctorId)));
        assertTrue(reviews.stream().allMatch(Review::getIsCurrentVersion));
        assertEquals(review3, reviews.get(0)); // Should be the current version
    }

    @Test
    void testFindByDoctorIdWithNoResults() {
        List<Review> reviews = reviewRepository.findByDoctorId(UUID.randomUUID());
        
        assertNotNull(reviews);
        assertTrue(reviews.isEmpty());
    }

    @Test
    void testFindByDoctorIdWithMultipleCurrentVersions() {
        // Create another current version review for the same doctor
        Review review4 = Review.builder()
                .doctorId(doctorId)
                .patientId(UUID.randomUUID())
                .consultationId(UUID.randomUUID())
                .rating(5)
                .comment("Another current review")
                .version(1)
                .parentId(UUID.randomUUID())
                .isCurrentVersion(true)
                .build();

        entityManager.persist(review4);
        entityManager.flush();

        List<Review> reviews = reviewRepository.findByDoctorId(doctorId);
        
        assertNotNull(reviews);
        assertEquals(2, reviews.size()); // Should return both current versions
        assertTrue(reviews.stream().allMatch(r -> r.getDoctorId().equals(doctorId)));
        assertTrue(reviews.stream().allMatch(Review::getIsCurrentVersion));
        assertTrue(reviews.contains(review3));
        assertTrue(reviews.contains(review4));
    }

    @Test
    void testFindAllVersionsByParentId() {
        List<Review> reviews = reviewRepository.findAllVersionsByParentId(parentId);
        
        assertNotNull(reviews);
        assertEquals(3, reviews.size());
        assertTrue(reviews.stream().allMatch(r -> r.getParentId().equals(parentId)));
        
        assertEquals(1, reviews.get(0).getVersion());
        assertEquals(2, reviews.get(1).getVersion());
        assertEquals(3, reviews.get(2).getVersion());
    }

    @Test
    void testFindAllVersionsByParentIdWithNoResults() {
        List<Review> reviews = reviewRepository.findAllVersionsByParentId(UUID.randomUUID());
        
        assertNotNull(reviews);
        assertTrue(reviews.isEmpty());
    }

    @Test
    void testFindCurrentVersionByParentId() {
        Review currentReview = reviewRepository.findCurrentVersionByParentId(parentId);
        
        assertNotNull(currentReview);
        assertEquals(parentId, currentReview.getParentId());
        assertTrue(currentReview.getIsCurrentVersion());
        assertEquals(3, currentReview.getVersion());
    }

    @Test
    void testFindCurrentVersionByParentIdWithNoResults() {
        Review currentReview = reviewRepository.findCurrentVersionByParentId(UUID.randomUUID());
        
        assertNull(currentReview);
    }

    @Test
    void testSaveAndRetrieveReview() {
        UUID newParentId = UUID.randomUUID();
        Review newReview = Review.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .consultationId(consultationId)
                .rating(5)
                .comment("New review")
                .version(1)
                .parentId(newParentId)
                .isCurrentVersion(true)
                .build();

        Review savedReview = reviewRepository.save(newReview);
        Review retrievedReview = reviewRepository.findById(savedReview.getId()).orElse(null);

        assertNotNull(retrievedReview);
        assertEquals(savedReview.getId(), retrievedReview.getId());
        assertEquals(newParentId, retrievedReview.getParentId());
        assertTrue(retrievedReview.getIsCurrentVersion());
    }

    @Test
    void testFindByPatientId() {
        List<Review> reviews = reviewRepository.findByPatientId(patientId);
        
        assertNotNull(reviews);
        assertEquals(1, reviews.size()); // Only current version should be returned
        assertTrue(reviews.stream().allMatch(r -> r.getPatientId().equals(patientId)));
        assertTrue(reviews.stream().allMatch(Review::getIsCurrentVersion));
        assertEquals(review3, reviews.get(0)); // Should be the current version
    }

    @Test
    void testFindByPatientIdWithNoResults() {
        List<Review> reviews = reviewRepository.findByPatientId(UUID.randomUUID());
        
        assertNotNull(reviews);
        assertTrue(reviews.isEmpty());
    }

    @Test
    void testFindByPatientIdWithMultipleCurrentVersions() {
        // Create another current version review for the same patient
        Review review4 = Review.builder()
                .doctorId(UUID.randomUUID())
                .patientId(patientId)
                .consultationId(UUID.randomUUID())
                .rating(5)
                .comment("Another current review")
                .version(1)
                .parentId(UUID.randomUUID())
                .isCurrentVersion(true)
                .build();

        entityManager.persist(review4);
        entityManager.flush();

        List<Review> reviews = reviewRepository.findByPatientId(patientId);
        
        assertNotNull(reviews);
        assertEquals(2, reviews.size()); // Should return both current versions
        assertTrue(reviews.stream().allMatch(r -> r.getPatientId().equals(patientId)));
        assertTrue(reviews.stream().allMatch(Review::getIsCurrentVersion));
        assertTrue(reviews.contains(review3));
        assertTrue(reviews.contains(review4));
    }

    @Test
    void testFindByPatientIdWithDifferentPatients() {
        // Create a review for a different patient
        UUID differentPatientId = UUID.randomUUID();
        Review review4 = Review.builder()
                .doctorId(doctorId)
                .patientId(differentPatientId)
                .consultationId(UUID.randomUUID())
                .rating(5)
                .comment("Different patient review")
                .version(1)
                .parentId(UUID.randomUUID())
                .isCurrentVersion(true)
                .build();

        entityManager.persist(review4);
        entityManager.flush();

        // Test original patient's reviews
        List<Review> originalPatientReviews = reviewRepository.findByPatientId(patientId);
        assertNotNull(originalPatientReviews);
        assertEquals(1, originalPatientReviews.size()); // Only current version
        assertTrue(originalPatientReviews.stream().allMatch(r -> r.getPatientId().equals(patientId)));
        assertTrue(originalPatientReviews.stream().allMatch(Review::getIsCurrentVersion));

        // Test different patient's reviews
        List<Review> differentPatientReviews = reviewRepository.findByPatientId(differentPatientId);
        assertNotNull(differentPatientReviews);
        assertEquals(1, differentPatientReviews.size());
        assertTrue(differentPatientReviews.stream().allMatch(r -> r.getPatientId().equals(differentPatientId)));
        assertTrue(differentPatientReviews.stream().allMatch(Review::getIsCurrentVersion));
    }
}
