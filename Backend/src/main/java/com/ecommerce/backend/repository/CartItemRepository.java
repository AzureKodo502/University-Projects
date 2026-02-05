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

/**
 * Interfaccia di persistenza per l'entità {@link CartItem}.
 * Estende {@link JpaRepository} per fornire le operazioni standard.
 * Include query personalizzate per la gestione specifica degli articoli nel carrello
 * basate sull'utente e sulle caratteristiche del prodotto.
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Recupera tutti gli elementi del carrello associati a un determinato utente.
     * Sfrutta la "Query Creation from Method Names" di Spring Data JPA.
     */
    List<CartItem> findByUser(User user);

    /**
     * Ricerca un articolo specifico nel carrello filtrando per utente, prodotto e taglia.
     * @return Un {@link Optional} che contiene il CartItem se trovato, evitando potenziali NullPointerException.
     */
    Optional<CartItem> findByUserAndScarpaAndTaglia(User user, com.ecommerce.backend.model.Scarpa scarpa, int taglia);

    /**
     * Esegue l'eliminazione mirata di un articolo dal carrello tramite parametri diretti.
     * L'annotazione @Modifying è necessaria per query di tipo DELETE o UPDATE.
     * L'annotazione @Transactional garantisce l'integrità dell'operazione a livello di database.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId AND c.scarpa.id = :scarpaId AND c.taglia = :taglia")
    void deleteByIds(@Param("userId") Long userId, @Param("scarpaId") Long scarpaId, @Param("taglia") Integer taglia);
}