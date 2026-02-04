package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.AddToCartRequest;
import com.ecommerce.backend.model.CartItem;
import com.ecommerce.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // GET http://localhost:8080/api/cart/1 (Vedi carrello utente)
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCarrello(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCarrelloByUserId(userId));
    }

    // POST http://localhost:8080/api/cart/add
    @PostMapping("/add")
    public ResponseEntity<CartItem> aggiungi(@RequestBody AddToCartRequest request) {
        System.out.println("Backend ha ricevuto: " + request.getUserId() + " - " + request.getTaglia());
        return ResponseEntity.ok(cartService.aggiungiAlCarrello(request));
    }

    @PostMapping("/remove")
    public ResponseEntity<String> rimuovi(@RequestBody AddToCartRequest request) {
        cartService.rimuoviDalCarrello(
                request.getUserId(),
                request.getScarpaId(),
                request.getTaglia()
        );
        return ResponseEntity.ok("Rimosso");
    }
}