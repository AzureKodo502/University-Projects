package com.ecommerce.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entità JPA che rappresenta un singolo elemento all'interno di un carrello acquisti.
 * Questa classe mappa la tabella "cart_items" nel database H2 e gestisce le relazioni
 * tra gli utenti e i prodotti selezionati.
 */
@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    /**
     * Identificativo univoco autoincrementale (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relazione Many-to-One con l'entità {@link User}.
     * Indica che più record di CartItem possono appartenere allo stesso utente.
     * La colonna "user_id" funge da Foreign Key.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Relazione Many-to-One con l'entità {@link Scarpa}.
     * Collega l'elemento del carrello al prodotto specifico nel catalogo.
     */
    @ManyToOne
    @JoinColumn(name = "scarpa_id", nullable = false)
    private Scarpa scarpa;

    /**
     * Numero di unità del prodotto selezionate dall'utente.
     */
    private Integer quantita;

    /**
     * Specifica della taglia scelta per la Scarpa.
     */
    private Integer taglia;
}