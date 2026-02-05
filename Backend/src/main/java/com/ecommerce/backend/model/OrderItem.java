package com.ecommerce.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entità JPA che rappresenta il dettaglio di un singolo prodotto all'interno di un ordine.
 * Funge da tabella di congiunzione tra l'ordine generale e il catalogo,
 * congelando i dati al momento della transazione.
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    /**
     * Identificativo univoco del dettaglio ordine.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relazione Many-to-One con l'ordine principale.
     * Molteplici righe di dettaglio appartengono a un singolo ordine d'ordine.
     */
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Relazione Many-to-One con l'entità Scarpa.
     * Identifica quale prodotto è stato acquistato.
     */
    @ManyToOne
    @JoinColumn(name = "scarpa_id", nullable = false)
    private Scarpa scarpa;

    /**
     * Quantità acquistata per lo specifico prodotto.
     */
    private Integer quantita;

    /**
     * Campo critico per l'integrità dei dati: memorizza il prezzo del prodotto
     * nel preciso istante dell'acquisto. Questo garantisce che modifiche future
     * al listino prezzi non alterino il valore storico degli ordini passati.
     */
    private Double prezzoAlMomentoDellAcquisto;
}