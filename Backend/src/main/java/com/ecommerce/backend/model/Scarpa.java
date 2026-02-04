package com.ecommerce.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor // Costruttore con tutti gli argomenti
@Builder
public class Scarpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private Double prezzo;

    @Column(length = 1000)
    private String descrizione;

    private String marchio;
    private String modello;

    private Integer tagliaDisponibile;

    private String imageUrl;
}