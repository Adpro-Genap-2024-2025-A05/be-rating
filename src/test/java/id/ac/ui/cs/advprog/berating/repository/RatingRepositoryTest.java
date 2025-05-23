package id.ac.ui.cs.advprog.test.repository;

import id.ac.ui.cs.advprog.berating.berating.repository.RatingRepository;
import id.ac.ui.cs.advprog.berating.model.Rating;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class RatingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RatingRepository ratingRepository;

    @Test
    void testFindByKonsultasiId() {
        // Create test data
        UUID konsultasiId = UUID.randomUUID();
        Rating rating = Rating.builder()
                .konsultasiId(konsultasiId)
                .pacilianId(UUID.randomUUID())
                .caregiverId(UUID.randomUUID())
                .rating(5)
                .build();
        entityManager.persist(rating);
        entityManager.flush();

        // Test
        Optional<Rating> found = ratingRepository.findByKonsultasiId(konsultasiId);
        assertTrue(found.isPresent());
        assertEquals(konsultasiId, found.get().getKonsultasiId());
    }

    @Test
    void testFindByCaregiverId() {
        // Create test data
        UUID caregiverId = UUID.randomUUID();
        Rating rating1 = Rating.builder()
                .konsultasiId(UUID.randomUUID())
                .pacilianId(UUID.randomUUID())
                .caregiverId(caregiverId)
                .rating(4)
                .build();
        Rating rating2 = Rating.builder()
                .konsultasiId(UUID.randomUUID())
                .pacilianId(UUID.randomUUID())
                .caregiverId(caregiverId)
                .rating(5)
                .build();
        entityManager.persist(rating1);
        entityManager.persist(rating2);
        entityManager.flush();

        // Test
        List<Rating> found = ratingRepository.findByCaregiverId(caregiverId);
        assertEquals(2, found.size());
        assertTrue(found.stream().allMatch(r -> r.getCaregiverId().equals(caregiverId)));
    }

    @Test
    void testFindByPacilianId() {
        // Create test data
        UUID pacilianId = UUID.randomUUID();
        Rating rating = Rating.builder()
                .konsultasiId(UUID.randomUUID())
                .pacilianId(pacilianId)
                .caregiverId(UUID.randomUUID())
                .rating(5)
                .build();
        entityManager.persist(rating);
        entityManager.flush();

        // Test
        List<Rating> found = ratingRepository.findByPacilianId(pacilianId);
        assertEquals(1, found.size());
        assertEquals(pacilianId, found.get(0).getPacilianId());
    }

    @Test
    void testExistsByKonsultasiId() {
        // Create test data
        UUID konsultasiId = UUID.randomUUID();
        Rating rating = Rating.builder()
                .konsultasiId(konsultasiId)
                .pacilianId(UUID.randomUUID())
                .caregiverId(UUID.randomUUID())
                .rating(5)
                .build();
        entityManager.persist(rating);
        entityManager.flush();

        // Test
        assertTrue(ratingRepository.existsByKonsultasiId(konsultasiId));
        assertFalse(ratingRepository.existsByKonsultasiId(UUID.randomUUID()));
    }

    @Test
    void testGetAverageRatingForCaregiver() {
        // Create test data
        UUID caregiverId = UUID.randomUUID();
        Rating rating1 = Rating.builder()
                .konsultasiId(UUID.randomUUID())
                .pacilianId(UUID.randomUUID())
                .caregiverId(caregiverId)
                .rating(4)
                .build();
        Rating rating2 = Rating.builder()
                .konsultasiId(UUID.randomUUID())
                .pacilianId(UUID.randomUUID())
                .caregiverId(caregiverId)
                .rating(5)
                .build();
        entityManager.persist(rating1);
        entityManager.persist(rating2);
        entityManager.flush();

        // Test
        Double average = ratingRepository.getAverageRatingForCaregiver(caregiverId);
        assertEquals(4.5, average);
    }

    @Test
    void testGetTotalRatingsForCaregiver() {
        // Create test data
        UUID caregiverId = UUID.randomUUID();
        Rating rating1 = Rating.builder()
                .konsultasiId(UUID.randomUUID())
                .pacilianId(UUID.randomUUID())
                .caregiverId(caregiverId)
                .rating(4)
                .build();
        Rating rating2 = Rating.builder()
                .konsultasiId(UUID.randomUUID())
                .pacilianId(UUID.randomUUID())
                .caregiverId(caregiverId)
                .rating(5)
                .build();
        entityManager.persist(rating1);
        entityManager.persist(rating2);
        entityManager.flush();

        // Test
        Long total = ratingRepository.getTotalRatingsForCaregiver(caregiverId);
        assertEquals(2L, total);
    }
} 