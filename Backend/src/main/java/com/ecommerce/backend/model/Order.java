package com.ecommerce.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entità JPA che rappresenta un ordine d'acquisto.
 * Memorizza i dettagli della transazione, il riferimento all'utente che acquista
 * e lo stato dell'avanzamento dell'ordine.
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * Identificativo univoco dell'ordine.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relazione Many-to-One verso l'utente che ha effettuato l'ordine.
     * Un utente può essere associato a molteplici ordini nel corso del tempo.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Timestamp che registra il momento esatto della creazione dell'ordine.
     * Fondamentale per la visualizzazione dello storico.
     */
    private LocalDateTime dataCreazione;

    /**
     * Valore complessivo dell'ordine al momento della transazione.
     */
    private Double totale;

    /**
     * Rappresenta lo stato corrente dell'ordine (CONFERMATO, SPEDITO, ANNULLATO).
     * Di default viene impostato su "CONFERMATO".
     */
    private String stato = "CONFERMATO";
}