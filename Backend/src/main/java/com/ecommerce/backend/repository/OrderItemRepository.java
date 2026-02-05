package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository per la gestione della persistenza degli oggetti {@link OrderItem}.
 * Estende {@link JpaRepository} per ereditare tutte le operazioni standard
 * (save, delete, findById, ecc.).
 * * Questa interfaccia gestisce il dettaglio analitico degli ordini, permettendo
 * la persistenza delle singole righe d'ordine collegate alla testata principale.
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Non sono necessari metodi personalizzati al momento, poiché le operazioni
    // di inserimento e recupero vengono gestite tramite le relazioni JPA
    // definite nell'entità Order.
}