package id.ac.ui.cs.advprog.berating.repository;

import id.ac.ui.cs.advprog.berating.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
