package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository per l'accesso e la gestione della persistenza degli ordini.
 * Fornisce le funzionalità per il salvataggio dei nuovi ordini e il recupero
 * della cronologia transazionale degli utenti.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Recupera la lista completa degli ordini effettuati da un determinato utente.
     * Utilizzato per popolare la sezione "Storico Ordini" nel frontend JavaFX.
     * * @param user L'entità {@link User} di cui si vuole consultare lo storico.
     * @return Una lista di {@link Order} ordinata cronologicamente per data di inserimento
     */
    List<Order> findByUser(User user);
}