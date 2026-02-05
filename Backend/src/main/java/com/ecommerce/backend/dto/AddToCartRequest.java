package com.ecommerce.backend.dto;

/**
 * Data Transfer Object (DTO) utilizzato per incapsulare i dati necessari
 * all'aggiunta o rimozione di un articolo nel carrello.
 * * Questa classe funge da "ponte" tra il frontend (JavaFX) e il backend,
 * permettendo il trasferimento di informazioni senza esporre la struttura
 * interna delle entità JPA.
 */
public class AddToCartRequest {
    private Long userId;
    private Long scarpaId;
    private Integer quantita;
    private Integer taglia;

    /**
     * Costruttore di default esplicito.
     * Necessario per i framework di serializzazione/deserializzazione JSON
     */
    public AddToCartRequest() {}

    // GETTER E SETTER

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getScarpaId() { return scarpaId; }
    public void setScarpaId(Long scarpaId) { this.scarpaId = scarpaId; }

    public Integer getQuantita() { return quantita; }
    public void setQuantita(Integer quantita) { this.quantita = quantita; }

    public Integer getTaglia() { return taglia; }
    public void setTaglia(Integer taglia) { this.taglia = taglia; }
}