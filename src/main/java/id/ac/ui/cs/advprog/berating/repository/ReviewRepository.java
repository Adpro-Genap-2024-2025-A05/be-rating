package id.ac.ui.cs.advprog.berating.repository;

import id.ac.ui.cs.advprog.berating.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
}
