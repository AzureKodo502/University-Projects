package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Scarpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository dedicato alla gestione del catalogo prodotti (entità {@link Scarpa}).
 * Implementa logiche di filtraggio per supportare la ricerca
 * e navigazione per brand all'interno dell'interfaccia utente.
 */
@Repository
public interface ScarpaRepository extends JpaRepository<Scarpa, Long> {

    /**
     * Esegue una ricerca parziale sul nome del prodotto.
     * Utilizza la clausola SQL 'LIKE' con conversione in minuscolo (IgnoreCase)
     * per garantire che la ricerca sia efficace indipendentemente da come
     * l'utente digita il testo nel frontend.
     * * @param query La stringa di ricerca inserita dall'utente.
     * @return Una lista di Scarpe il cui nome contiene la sottostringa fornita.
     */
    List<Scarpa> findByNomeContainingIgnoreCase(String query);

    /**
     * Filtra il catalogo in base al marchio specifico.
     * Viene utilizzato per la navigazione categorizzata (es. filtra per "Nike").
     * * @param marchio Il nome esatto del brand da ricercare.
     * @return Una lista di oggetti {@link Scarpa} appartenenti al marchio indicato.
     */
    List<Scarpa> findByMarchio(String marchio);
}