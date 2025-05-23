package id.ac.ui.cs.advprog.berating.repository;

import id.ac.ui.cs.advprog.berating.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    Optional<Rating> findByKonsultasiId(UUID konsultasiId);
    List<Rating> findByCaregiverId(UUID caregiverId);
    List<Rating> findByPacilianId(UUID pacilianId);
    boolean existsByKonsultasiId(UUID konsultasiId);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.caregiverId = :caregiverId")
    Double getAverageRatingForCaregiver(UUID caregiverId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.caregiverId = :caregiverId")
    Long getTotalRatingsForCaregiver(UUID caregiverId);
}