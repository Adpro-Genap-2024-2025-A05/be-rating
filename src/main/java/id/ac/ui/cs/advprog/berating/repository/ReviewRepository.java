package id.ac.ui.cs.advprog.berating.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import id.ac.ui.cs.advprog.berating.model.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {}
