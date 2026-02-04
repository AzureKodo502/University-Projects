package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // POST http://localhost:8080/api/orders/checkout/1 (Utente paga)
    @PostMapping("/checkout/{userId}")
    public ResponseEntity<Order> checkout(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(orderService.creaOrdine(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request se il carrello vuoto
        }
    }

    // GET http://localhost:8080/api/orders/user/1 (Storico ordini)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> storico(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdiniUtente(userId));
    }
}
