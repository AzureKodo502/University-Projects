package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller per la finalizzazione del processo d'acquisto.
 * Gestisce la conversione del carrello in ordini permanenti e il recupero
 * della cronologia transazionale dell'utente.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    /**
     * Business logic dedicata alla gestione del ciclo di vita degli ordini.
     */
    @Autowired
    private OrderService orderService;

    /**
     * Esegue il processo di checkout trasformando gli articoli nel carrello in un ordine confermato.
     * L'operazione è atomica a livello di service per garantire l'integrità dei dati.
     * * @param userId Identificativo dell'utente che intende completare l'acquisto.
     * @return ResponseEntity contenente l'oggetto {@link Order} generato (HTTP 200),
     * o un HTTP 400 Bad Request in caso di carrello vuoto o problemi di stock.
     */
    @PostMapping("/checkout/{userId}")
    public ResponseEntity<Order> checkout(@PathVariable Long userId) {
        try {
            // Delega al service la creazione dell'ordine e lo svuotamento del carrello
            return ResponseEntity.ok(orderService.creaOrdine(userId));
        } catch (RuntimeException e) {
            // Gestione di eccezioni di business (es. carrello vuoto)
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Recupera lo storico completo degli ordini effettuati da un determinato utente.
     * * @param userId Identificativo dell'utente per il filtraggio degli ordini.
     * @return ResponseEntity contenente la lista degli {@link Order} (HTTP 200).
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> storico(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdiniUtente(userId));
    }
}