package com.ecommerce.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELAZIONE: Molti elementi del carrello appartengono a UN Utente
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // RELAZIONE: Molti elementi del carrello possono riferirsi a UNA Scarpa
    @ManyToOne
    @JoinColumn(name = "scarpa_id", nullable = false)
    private Scarpa scarpa;

    private Integer quantita;
    private Integer taglia;
}
