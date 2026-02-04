package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.CartItem;
import com.ecommerce.backend.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {


    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndScarpaAndTaglia(User user, com.ecommerce.backend.model.Scarpa scarpa, int taglia);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId AND c.scarpa.id = :scarpaId AND c.taglia = :taglia")
    void deleteByIds(@Param("userId") Long userId, @Param("scarpaId") Long scarpaId, @Param("taglia") Integer taglia);
}