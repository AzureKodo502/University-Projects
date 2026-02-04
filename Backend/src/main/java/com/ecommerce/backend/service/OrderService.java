package com.ecommerce.backend.service;

import com.ecommerce.backend.model.*;
import com.ecommerce.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private UserRepository userRepository;

    // LOGICA DI CHECKOUT
    @Transactional // Se una riga fallisce, annulla TUTTO (Rollback)
    public Order creaOrdine(Long userId) {
        // Recupera Utente
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Recupera il Carrello
        List<CartItem> carrello = cartItemRepository.findByUser(user);
        if (carrello.isEmpty()) {
            throw new RuntimeException("Il carrello è vuoto!");
        }

        // Calcola il totale
        double totale = 0;
        for (CartItem item : carrello) {
            totale += item.getScarpa().getPrezzo() * item.getQuantita();
        }

        // Salva l'Ordine
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setDataCreazione(LocalDateTime.now());
        newOrder.setStato("CONFERMATO");
        newOrder.setTotale(totale);
        Order ordineSalvato = orderRepository.save(newOrder);

        // Sposta gli elementi da Carrello a OrderItem
        for (CartItem cartItem : carrello) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(ordineSalvato);
            orderItem.setScarpa(cartItem.getScarpa());
            orderItem.setQuantita(cartItem.getQuantita());
            orderItem.setPrezzoAlMomentoDellAcquisto(cartItem.getScarpa().getPrezzo());

            orderItemRepository.save(orderItem);
        }

        // Svuota il carrello una volta inseriti nell'ordine
        cartItemRepository.deleteAll(carrello);

        return ordineSalvato;
    }

    public List<Order> getOrdiniUtente(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return orderRepository.findByUser(user);
    }
}
