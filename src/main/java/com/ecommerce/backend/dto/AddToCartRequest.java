package com.ecommerce.backend.dto;

public class AddToCartRequest {
    private Long userId;
    private Long scarpaId;
    private Integer quantita;
    private Integer taglia;

    // Costruttore vuoto (FONDAMENTALE per JSON)
    public AddToCartRequest() {}

    // GETTER E SETTER MANUALI (Per sicurezza)
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getScarpaId() { return scarpaId; }
    public void setScarpaId(Long scarpaId) { this.scarpaId = scarpaId; }

    public Integer getQuantita() { return quantita; }
    public void setQuantita(Integer quantita) { this.quantita = quantita; }

    public Integer getTaglia() { return taglia; }
    public void setTaglia(Integer taglia) { this.taglia = taglia; }
}