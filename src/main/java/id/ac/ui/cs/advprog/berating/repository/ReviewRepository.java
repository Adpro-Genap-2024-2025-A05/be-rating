package id.ac.ui.cs.advprog.berating.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import id.ac.ui.cs.advprog.berating.model.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByDoctorId(UUID doctorId);
    
    @Query("SELECT r FROM Review r WHERE r.parentId = ?1 ORDER BY r.version")
    List<Review> findAllVersionsByParentId(UUID parentId);
    
    @Query("SELECT r FROM Review r WHERE r.parentId = ?1 AND r.isCurrentVersion = true")
    Review findCurrentVersionByParentId(UUID parentId);
}
