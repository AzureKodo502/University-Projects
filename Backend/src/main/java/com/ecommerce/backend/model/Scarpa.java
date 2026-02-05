package com.ecommerce.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * Entità JPA che modella l'oggetto "Scarpa" all'interno del catalogo.
 * Rappresenta la risorsa principale del sistema, contenente le specifiche tecniche,
 * i dettagli commerciali e i riferimenti per la visualizzazione nel frontend.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scarpa {

    /**
     * Identificativo univoco del prodotto nel database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome commerciale della Scarpa (es. "Air Max").
     * Vincolato a non essere nullo a livello di schema database.
     */
    @Column(nullable = false)
    private String nome;

    /**
     * Prezzo di listino attuale.
     */
    private Double prezzo;

    /**
     * Descrizione estesa del prodotto.
     * Definita con una lunghezza massima di 1000 caratteri per ospitare dettagli della Scarpa.
     */
    @Column(length = 1000)
    private String descrizione;

    /**
     * Il brand della Scarpa (es. Nike, Adidas).
     * Utilizzato spesso come criterio primario di filtraggio nelle query.
     */
    private String marchio;

    /**
     * Identificativo del modello specifico all'interno della linea del brand.
     */
    private String modello;

    /**
     * Rappresenta la taglia disponibile per questa specifica istanza del prodotto.
     */
    private Integer tagliaDisponibile;

    /**
     * URL della risorsa immagine (solitamente ospitata su un server statico o cloud storage)
     */
    private String imageUrl;
}