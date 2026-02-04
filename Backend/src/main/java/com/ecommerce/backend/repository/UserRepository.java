package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Select * from users where email = ?
    Optional<User> findByEmail(String email);

    // Controllo se l'email esiste già
    boolean existsByEmail(String email);
}