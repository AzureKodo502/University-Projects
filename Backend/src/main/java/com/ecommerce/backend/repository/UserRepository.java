package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository per la gestione della persistenza dell'entità {@link User}.
 * Fornisce i metodi necessari per le operazioni di autenticazione e
 * per la verifica dell'unicità degli account in fase di registrazione.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Ricerca un utente nel database tramite il suo indirizzo email.
     * Utilizzato principalmente durante il processo di login.
     * * @param email L'indirizzo e-mail dell'utente.
     * @return Un {@link Optional} contenente l'utente se trovato, altrimenti vuoto.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica la presenza di un account associato a una specifica email.
     * Metodo di supporto per la logica di business che impedisce duplicazioni.
     * * @param email L'indirizzo e-mail da controllare.
     * @return true se l'email è già presente nel database, false altrimenti.
     */
    boolean existsByEmail(String email);
}