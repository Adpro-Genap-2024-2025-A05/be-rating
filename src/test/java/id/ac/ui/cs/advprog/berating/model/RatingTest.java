package id.ac.ui.cs.advprog.berating.model;

import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.berating.model.Rating;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

class RatingTest {

    @Test
    void testRatingCreation() {
        UUID konsultasiId = UUID.randomUUID();
        UUID pacilianId = UUID.randomUUID();
        UUID caregiverId = UUID.randomUUID();
        String pacilianName = "John Doe";
        Integer rating = 5;
        String review = "Great service!";

        Rating ratingObj = Rating.builder()
                .konsultasiId(konsultasiId)
                .pacilianId(pacilianId)
                .caregiverId(caregiverId)
                .pacilianName(pacilianName)
                .rating(rating)
                .review(review)
                .build();

        assertNotNull(ratingObj);
        assertEquals(konsultasiId, ratingObj.getKonsultasiId());
        assertEquals(pacilianId, ratingObj.getPacilianId());
        assertEquals(caregiverId, ratingObj.getCaregiverId());
        assertEquals(pacilianName, ratingObj.getPacilianName());
        assertEquals(rating, ratingObj.getRating());
        assertEquals(review, ratingObj.getReview());
    }

    @Test
    void testRatingValidation() {
        Rating rating = new Rating();
        
        // Test minimum rating
        rating.setRating(1);
        assertEquals(1, rating.getRating());

        // Test maximum rating
        rating.setRating(5);
        assertEquals(5, rating.getRating());
    }

    @Test
    void testTimestamps() {
        Rating rating = new Rating();
        rating.onCreate();
        
        assertNotNull(rating.getCreatedAt());
        assertNotNull(rating.getUpdatedAt());
        
        LocalDateTime initialUpdatedAt = rating.getUpdatedAt();
        
        // Simulate update
        try {
            Thread.sleep(1000); // Wait for 1 second to ensure timestamp difference
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        rating.onUpdate();
        assertTrue(rating.getUpdatedAt().isAfter(initialUpdatedAt));
    }

    @Test
    void testNoArgsConstructor() {
        Rating rating = new Rating();
        assertNotNull(rating);
        assertNull(rating.getId());
        assertNull(rating.getKonsultasiId());
        assertNull(rating.getPacilianId());
        assertNull(rating.getCaregiverId());
        assertNull(rating.getRating());
        assertNull(rating.getReview());
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID konsultasiId = UUID.randomUUID();
        UUID pacilianId = UUID.randomUUID();
        UUID caregiverId = UUID.randomUUID();
        String pacilianName = "John Doe";
        Integer rating = 4;
        String review = "Good service";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Rating ratingObj = new Rating(id, konsultasiId, pacilianId, pacilianName, 
                                    caregiverId, rating, review, createdAt, updatedAt);

        assertEquals(id, ratingObj.getId());
        assertEquals(konsultasiId, ratingObj.getKonsultasiId());
        assertEquals(pacilianId, ratingObj.getPacilianId());
        assertEquals(caregiverId, ratingObj.getCaregiverId());
        assertEquals(pacilianName, ratingObj.getPacilianName());
        assertEquals(rating, ratingObj.getRating());
        assertEquals(review, ratingObj.getReview());
        assertEquals(createdAt, ratingObj.getCreatedAt());
        assertEquals(updatedAt, ratingObj.getUpdatedAt());
    }
} 